package Cheng;

import Cheng.CustomerAgent.AgentEvent;
import Cheng.WaiterAgent.CustomerState;
import Cheng.gui.RestaurantGui;
import Cheng.interfaces.Cashier;
import Cheng.interfaces.Customer;
import Cheng.interfaces.Host;
import Cheng.interfaces.Market;
import Cheng.interfaces.Waiter;
import Cheng.gui.RestaurantPanel;
import agent.Agent;
import agents.Grocery;
import agents.Person;
import agents.Role;
import agents.Worker;

import java.util.*;

import simcity201.interfaces.NewMarketInteraction;
import tracePanelpackage.AlertLog;
import tracePanelpackage.AlertTag;

public class CashierAgent extends Agent implements Cashier,NewMarketInteraction,Worker{
	public String name;
	private Menu m = new Menu();
	public double money = 1000;
	public double loan;
	private Host host;
	public List<MyCustomer> customer =Collections.synchronizedList(new ArrayList<MyCustomer>());
	public enum CustomerState{Unpay,Paying, Paied, GoToPay, Owe};
	public enum BillState{Unpay,Paying,Paid, Owe};
	public List<Bill> bill = Collections.synchronizedList(new ArrayList<Bill>());
	public boolean rich = false;
	public boolean loanhere = false;
	private MyCustomer C = null;
	private CookAgent cook;
	private Bill B = null;
	public Person p = null;
	public int timeIn;
	public boolean isWorking;
	public RestaurantPanel rp;
	public CashierAgent(String name,RestaurantPanel rp){
		super();
		this.rp = rp;

		this.name = name;
		this.loan = 0;
	}
	public void setHost(Host h){
		this.host = h;
	}
	public String getName(){
		return this.name;
	}

	public void msgHereIsPrice(List<Grocery> orders, float price) {
		bill.add(new Bill(price,BillState.Unpay));
		stateChanged();

	}
	@Override
	public void msgHereIsFood(List<Grocery> orders) {

	}
	@Override
	public void msgNoFoodForYou() {


	}
	public void msgComputeCheck(Waiter w,Customer c, String Choice){
		double price = 0;
		for(int i=0; i< m.menu.size(); i++){
			if(m.getName(i) == Choice){
				price  = m.getPrice(i);
			}
		}
		customer.add(new MyCustomer(w,c,Choice,CustomerState.Unpay, price));
		stateChanged();
	}

	public void msgPay(Customer c, double cash){
		for(MyCustomer mycust : customer){
			if(mycust.getCust() == c)
			{
				MyCustomer mc = mycust;
				mc.s = CustomerState.Paying;
				mc.setCash(cash);
			}
		}
		stateChanged();
	}
	public void msgMarketBill(Market m,double money){

	}
	public void msgHereIsMoney(double money){
		this.money += money;
		loanhere = true;
		stateChanged();
	}
	@Override
	public boolean pickAndExecuteAnAction() {
		// TODO Auto-generated method stub

		if(this.p == null){
			return false;
		}

		if(isWorking == false) {

			if(p.quitWork)
			{
				rp.quitCashier();
				p.canGetJob = false;
				p.quitWork = false;
				AlertLog.getInstance().logMessage(AlertTag.Ross, p.getName(),"I QUIT");
			}
			for(Role r : p.roles)
			{
				if(r.getRole().equals(Role.roles.WorkerRossCashier))
				{
					p.roles.remove(r);
					break;
				}
			}

			p.msgDone();
			p.payCheck += 30;
			this.p = null;
			return false;
		}



		MyCustomer C = null;
		Bill B = null;

		synchronized(customer){
			for(MyCustomer mycust: customer){
				if(mycust.s == CustomerState.Unpay){
					C = mycust;
					break;
				}
			}
		}

		if(C != null){
			ComputeCheck(C);
			return true;
		}

		synchronized(customer){
			for(MyCustomer mycust: customer){
				if(mycust.s == CustomerState.Paying){
					C = mycust;
					break;
				}
			}
		}

		if(C != null){
			GiveChange(C);
			return true;
		}

		synchronized(bill){
			for(Bill mybill:bill){
				if(mybill.s == BillState.Unpay){
					B = mybill;
					break;

				}
			}
		}

		if(B != null){
			PayMarket(B);
			return true;
		}

		synchronized(bill){
			for(Bill mybill:bill){
				if(mybill.s == BillState.Owe && loanhere == true){
					loanhere = false;
					B = mybill;
					break;
				}
			}
		}

		if(B != null){
			PayMarketAgain(B);
			return true;
		}



		if(rich == true){
			rich = false;
			PayDebt();
			return true;
		}

		return false;
	}


	private void ComputeCheck(MyCustomer c) {
		// TODO Auto-generated method stub
		AlertLog.getInstance().logMessage(AlertTag.RossCashier, p.getName(),"Compute check");
		c.s = CustomerState.GoToPay;

	}
	private void GiveChange(MyCustomer c){
		double change = 0;
		
			money += c.check;
			change = c.Cash - c.check;
			c.setChange(change);
			c.c.msgGiveChange(c.Change);
			c.s = CustomerState.Paied;
		
	}
	private void PayMarket(Bill b){
		if(money > b.money && (money - b.money) >loan){
			money -= b.money;
			//b.m.msgPayMarket(b.money);
			cook.msgPayMarket(b.money);
			b.s = BillState.Paid;
			rich = true;
		}
		else{
			//b.s = BillState.Owe;
			//loan = b.money - money;
			//host.msgINeedMoney(loan);
			cook.msgMarketNoFood();
			b.s = BillState.Paid;
		}
	}

	private void PayMarketAgain(Bill b){
		money -= b.money;
		cook.msgPayMarket(money);
		//b.m.msgPayMarket(b.money);
		b.s = BillState.Paid;
	}
	private void PayDebt(){
		money -= loan;
		host.msgPayDebt(loan);
	}

	public class MyCustomer{
		Waiter waiter;
		Customer c;
		public double check;
		String Choice;
		CustomerState s;
		public double Cash;
		public double Change;
		MyCustomer(Waiter w ,Customer c, String Choice, CustomerState s,double check){
			this.c = c;
			this.Choice = Choice;
			this.s = s;
			this.check = check;
			this.waiter = w;
		}
		public void setCash(double c){
			this.Cash = c;
		}
		public void setChange(double c){
			this.Change = c;
		}
		public Customer getCust(){
			return c;
		}
	}
	public class Bill{
		public double money;
		BillState s;
		Bill(double money,BillState s){
			this.money = money;
			this.s = s;
		}
	}
	public void setCook(CookAgent cook){
		this.cook = cook;
	}
	@Override
	public void setTimeIn(int timeIn) {
		// TODO Auto-generated method stub

	}
	@Override
	public int getTimeIn() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public void goHome() {
		// TODO Auto-generated method stub

	}
	@Override
	public Person getPerson() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void msgLeave() {
		isWorking = false;
		stateChanged();
		// TODO Auto-generated method stub

	}
	@Override
	public void msgOutOfStock() {
		// TODO Auto-generated method stub
		
	}

}