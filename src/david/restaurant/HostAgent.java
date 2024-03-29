package david.restaurant;

import agent.Agent;
import agents.Person;
import agents.Role;
import agents.Worker;
import david.restaurant.Interfaces.Waiter;
import david.restaurant.gui.HostGui;
import david.restaurant.gui.RestaurantPanel;
import david.restaurant.gui.Table;

import java.util.*;
import java.util.concurrent.Semaphore;
//import com.sun.corba.se.impl.orbutil.concurrent.Mutex;



import tracePanelpackage.AlertLog;
import tracePanelpackage.AlertTag;

/**
 * Restaurant Host Agent
 */
public class HostAgent extends Agent implements Worker {
	private List<myCustomer> customers = Collections.synchronizedList(new ArrayList<myCustomer>());
	private List<myWaiter> waiters = new ArrayList<myWaiter>();
	public Collection<Table> tables = new ArrayList<Table>();
	private enum cState{waiting, notified, canBeSeated, needsToMove, moving, goingToBeSeated, leaving, 
		needsToShift, beingServed};

		public RestaurantPanel restPanel = null;

		private Object customerLock = new Object();
		private Object tableLock = new Object();
		private Object waiterLock = new Object();
		private Object lineLock = new Object();

		public String name;

		int peopleInLine = 0;

		public HostGui hostGui = null;
		public CashierAgent cashier;
		public CookAgent cook;
		public void print_()
		{
			print(Integer.toString(customers.size() + waiters.size() + tables.size()));
		}

		public HostAgent(String name, RestaurantPanel r) {
			super();
			this.restPanel = r;
			this.name = name;
			// make some tables
			tables = new ArrayList<Table>();
		}

		public void msgAddTable(Table t)
		{
			synchronized(tableLock)
			{
				tables.add(t);
				stateChanged();
			}
		}

		public void AddWaiter(Waiter w)
		{
			synchronized(waiterLock)
			{
				waiters.add(new myWaiter(w));
			}
			stateChanged();
		}

		public String getMaitreDName() {
			return name;
		}

		public String getName() {
			return name;
		}

		public List getWaitingCustomers() {
			return customers;
		}

		public Collection getTables() {
			return tables;
		}

		public void gMsgCustomerReady(CustomerAgent cust)
		{
			synchronized(customerLock)
			{
				for(myCustomer c: customers)
				{
					if(c.customer == cust)
					{
						c.state = cState.canBeSeated;
						break;
					}
				}
			}
			stateChanged();
		}

		public void gMsgShiftCustomers()
		{
			print("gMsgShiftCustomers");
			synchronized(customerLock)
			{
				for(myCustomer cust: customers)
				{
					if(cust.state == cState.canBeSeated || cust.state == cState.needsToMove || cust.state == cState.moving)
					{
						cust.placeInLine--;
						cust.state = cState.needsToMove;
					}
				}
				peopleInLine--;
			}
			stateChanged();
		}

		// Messages
		public void msgIWantToEat(CustomerAgent cust) {
			synchronized(customerLock)
			{
				print(Integer.toString(tables.size()));
				print("msgIWanttoEat");
				int numCustomers = 0;
				for(myCustomer c: customers)
				{
					if(c.state == cState.canBeSeated || c.state == cState.moving || c.state == cState.needsToMove 
							|| c.state == cState.needsToShift || c.state == cState.beingServed)
					{
						numCustomers++;
					}
				}
				if(numCustomers >= tables.size())
				{
					print("waiting customer");
					customers.add(new myCustomer(cust, cState.waiting, -1));
				}
				else
				{
					customers.add(new myCustomer(cust, cState.needsToMove, peopleInLine++));
				}
				stateChanged();
				return;
			}
		}

		public void msgStaying(CustomerAgent c)
		{
			synchronized(customerLock)
			{
				boolean temp = false;
				for(myCustomer mc: customers)
				{
					if(mc.customer == c)
					{
						mc.state = cState.needsToMove;
						mc.placeInLine = peopleInLine++;
						temp = true;
						break;
					}
				}
				if(!temp)
				{

					customers.add(new myCustomer(c, cState.needsToMove, peopleInLine++));
					print("problem");
				}
			}
			stateChanged();
		}

		void msgLeaving(CustomerAgent c)
		{
			synchronized(customerLock)
			{
				myCustomer temp = null;
				for(myCustomer mc: customers)
				{
					if(mc.customer == c)
					{
						temp = mc;
						break;
					}
				}
				if(temp != null)
				{
					customers.remove(temp);
				}
			}
			
			if (customers.size() == 0 && isWorking == false) {
				AlertLog.getInstance().logMessage(AlertTag.David, this.name,"Closing restaurant");
				AlertLog.getInstance().logMessage(AlertTag.Davidhost, this.name,"Closing restaurant");

				for (myWaiter w : waiters) {
					((WaiterAgent)w.w).goHome();
				}
				waiters.clear();

				restPanel.cook.goHome();
				restPanel.cashier.goHome();
				restPanel.closeRestaurant();
				if(p.quitWork)
				{
					restPanel.quitHost();
					p.canGetJob = false;
					p.quitWork = false;
					AlertLog.getInstance().logMessage(AlertTag.David, p.getName(),"I QUIT");
				
				for(Role r : p.roles)
				{
					if(r.getRole().equals(Role.roles.WorkerDavidhost))
					{
						p.roles.remove(r);
						break;
					}
				}
				}
				p.payCheck += 30;

				this.p.msgDone();
				this.p = null;
			}


			
			stateChanged();
		}

		public void msgTableIsFree(int t, Waiter w, CustomerAgent c)
		{
			Table table = null;
			synchronized(tableLock)
			{
				for(Table temp:tables)
				{
					if(temp.tableNumber == t)
					{
						table = temp;
						break;
					}
				}
				if(table != null)
				{
					table.setUnoccupied();
				}
			}
			synchronized(waiterLock)
			{
				for(myWaiter waiter: waiters)
				{
					if(waiter.w == w)
					{
						waiter.numCustomers--;
						break;
					}
				}
			}
			synchronized(customerLock)
			{
				myCustomer customer = null;
				for(myCustomer cust: customers)
				{
					if(cust.customer == c)
					{
						customer = cust;
					}
				}
				if(customer != null)
				{
					customers.remove(customer);
				}
			}
			
			if (customers.size() == 0 && isWorking == false) {
				AlertLog.getInstance().logMessage(AlertTag.David, this.name,"Closing restaurant");
				AlertLog.getInstance().logMessage(AlertTag.Davidhost, this.name,"Closing restaurant");

				for (myWaiter w_ : waiters) {
					((WaiterAgent)w_.w).goHome();
				}
				waiters.clear();

				restPanel.cook.goHome();
				restPanel.cashier.goHome();
				restPanel.closeRestaurant();
				if(p.quitWork)
				{
					restPanel.quitHost();
					p.canGetJob = false;
					p.quitWork = false;
					AlertLog.getInstance().logMessage(AlertTag.David, p.getName(),"I QUIT");
				
				for(Role r : p.roles)
				{
					if(r.getRole().equals(Role.roles.WorkerDavidhost))
					{
						p.roles.remove(r);
						break;
					}
				}
				}
				p.payCheck += 30;

				this.p.msgDone();
				this.p = null;
			}


			stateChanged();
		}

		public void msgWantToGoOnBreak(Waiter w)
		{
			myWaiter temp = null;
			synchronized(waiterLock)
			{
				for(myWaiter waiter: waiters)
				{
					if(waiter.w == w)
					{
						waiter.wantBreak = true;
						break;
					}
				}
			}
			stateChanged();
		}

		public void msgOffBreak(Waiter w, int numCustomers)
		{
			synchronized(waiterLock)
			{
				for(myWaiter waiter: waiters)
				{
					if(waiter.w == w)
						return;
				}
				waiters.add(new myWaiter(w, numCustomers));
			}
			stateChanged();
		}

		/**
		 * Scheduler.  Determine what action is called for, and do it.
		 */
		public boolean pickAndExecuteAnAction() {
			
			if(this.p == null) {
				return false;
			}
			synchronized(waiterLock)
			{
				for(myWaiter waiter: waiters)
				{
					if(waiter.wantBreak == true)
					{
						DoGiveBreak(waiter);
						return true;
					}
				}
			}

			synchronized(customerLock)
			{
				boolean temp = false;
				for(myCustomer mc: customers)
				{
					if(mc.state == cState.waiting || mc.state == cState.leaving)
					{
						temp = true;
					}
				}
				if(temp)
				{
					DoNotifyCustomers();
					return true;
				}
			}

			synchronized(customerLock)
			{
				for(myCustomer cust: customers)
				{
					if(cust.state == cState.needsToMove)
					{
						cust.state = cState.moving;
						cust.customer.getGui().msgGoToWaitingArea(100 - cust.placeInLine * 30, 50);
						return true;
					}
				}
			}

			if(customers.isEmpty() == false)
			{
				synchronized(tableLock)
				{
					synchronized(customerLock)
					{
						boolean canSeat = false;
						for(myCustomer cust: customers)
						{
							if(cust.state == cState.canBeSeated)
							{
								canSeat = true;
								break;
							}
						}
						if(canSeat)
						{
							for(Table temp: tables)
							{
								if(temp.isOccupied() == false)
								{
									if(waiters.size() > 0)
									{
										DoMessageWaiterNewCust(temp);
										return true;
									}
								}
							}
						}
					}
				}
			}
			return false;
		}

		// Actions
		//utilities

		public void setGui(HostGui gui) {
			hostGui = gui;
		}

		public HostGui getGui() {
			return hostGui;
		}

		private class myWaiter
		{
			public Waiter w;
			public int numCustomers;
			public boolean wantBreak;
			public myWaiter(Waiter waiter)
			{
				w = waiter;
				numCustomers = 0;
				wantBreak = false;
			}
			public myWaiter(Waiter waiter, int n) {
				// TODO Auto-generated constructor stub
				w = waiter;
				numCustomers = n;
				wantBreak = false;
			}
		}
		//actions

		private void DoGiveBreak(myWaiter w)
		{
			if(waiters.size() > 1)
			{
				w.wantBreak = false;
				waiters.remove(w);
				w.w.msgOkBreak();
			}
			else
			{
				w.wantBreak = false;
				w.w.msgNoBreak();
			}
		}

		void DoNotifyCustomers()
		{
			synchronized(customerLock)
			{
				print("notifying customers");
				List<myCustomer> list = new ArrayList<myCustomer>();
				for(myCustomer c: customers)
				{
					if(c.state == cState.waiting)
					{
						c.state = cState.notified;
						c.customer.msgIsFull();
					}
					else if(c.state == cState.leaving)
					{
						list.add(c);
					}
				}
				for(myCustomer mc:list)
				{
					customers.remove(mc);
				}
			}
		}

		void DoMessageWaiterNewCust(Table t)
		{
			myCustomer c = null;
			synchronized(customerLock)
			{
				for(myCustomer mc: customers)
				{
					if(mc.state == cState.canBeSeated && mc.placeInLine == 0)
					{
						c = mc;
						mc.state = cState.goingToBeSeated;
					}
				}
				if(c == null)
				{
					return ;
				}
			}
			synchronized(waiterLock)
			{
				if(waiters.size() > 0)
				{
					myWaiter w = waiters.get(0);
					int min = w.numCustomers;
					for(myWaiter temp: waiters)
					{
						if(temp.numCustomers < min)
						{
							min = temp.numCustomers;
							w = temp;
						}
					}
					c.state = cState.beingServed;
					t.setOccupant(c.customer);
					w.numCustomers++;
					w.w.msgPleaseSitCustomer(c.customer, t.tableNumber);
				}
			}
		}

		private class myCustomer
		{
			public myCustomer(CustomerAgent cust, cState c, int p) 
			{
				customer = cust;
				state = c;
				placeInLine = p;
			}
			public int placeInLine;
			CustomerAgent customer;
			cState state;
		}

		public int timeIn = 0;
		Person self =null;
		public boolean isWorking;
		public Person p;

		@Override
		public void setTimeIn(int timeIn) {
			this.timeIn = timeIn;
		}

		@Override
		public int getTimeIn() {
			return timeIn;
		}



		@Override
		public void goHome() {
			isWorking = false;
			if (customers.size() == 0) {
				AlertLog.getInstance().logMessage(AlertTag.David, this.name,"Closing restaurant");
				AlertLog.getInstance().logMessage(AlertTag.Davidhost, this.name,"Closing restaurant");

				for (myWaiter w : waiters) {
					w.w.goHome();
				}
				waiters.clear();

				restPanel.cook.goHome();
				restPanel.cashier.goHome();
				restPanel.closeRestaurant();
				if(p.quitWork)
				{
					restPanel.quitHost();
					p.canGetJob = false;
					p.quitWork = false;
					AlertLog.getInstance().logMessage(AlertTag.David, p.getName(),"I QUIT");
				
				for(Role r : p.roles)
				{
					if(r.getRole().equals(Role.roles.WorkerDavidhost))
					{
						p.roles.remove(r);
						break;
					}
				}
				}
				p.payCheck += 30;

				this.p.msgDone();
				this.p = null;
			}



		}

		@Override
		public Person getPerson() {
			return self;
		}

		@Override
		public void msgLeave() {
			// TODO Auto-generated method stub

		}
}

