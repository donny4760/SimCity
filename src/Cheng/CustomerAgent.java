package Cheng;

import Cheng.gui.CustomerGui;
import Cheng.gui.RestaurantGui;
import Cheng.interfaces.Customer;
import Cheng.interfaces.Waiter;
import agent.Agent;
import agents.Person;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import tracePanelpackage.AlertLog;
import tracePanelpackage.AlertTag;

/**
 * Restaurant customer agent.
 */
public class CustomerAgent extends Agent implements Customer{
	public int count = 0;
    public int tablex2;
    public int tabley2;
    
    public int tablex3;
    public int tabley3;
	public int tablenum;
	private int ThinkingTime =10000;
	private int EatingTime = 5000;
	public String Choice;
	public double price;
	private Menu menu;
	private String name;
	private int hungerLevel = 5;        // determines length of meal
	Timer timer = new Timer();
	private CustomerGui customerGui;
	// agent correspondents
	private Waiter waiters;
	//    private boolean isHungry = false; //hack for gui
	private HostAgent host;
	private CashierAgent cashier;
	private int seatnum;
	private boolean atCashier = false;
	private boolean tableFull = false;
	private Person p;
	public enum AgentState
	{ReadyToSeat,DoingNothing, WaitingInRestaurant, BeingSeated,Ordered,Eating, DoneEating, Leaving, TakingOrder, Paied, Leave};
	private AgentState state = AgentState.DoingNothing;//The start state

	public enum AgentEvent 
	{none, gotHungry, followHost, seated,ordering,beingServed, doneEating, doneLeaving, TakingOrder, Reorder, Paied, Paying, ReadyToSeat};
	AgentEvent event = AgentEvent.none;
	
	private double Cash;
	
	/**
	 * Constructor for CustomerAgent class
	 *
	 * @param name name of the customer
	 * @param gui  reference to the customergui so the customer can send it messages
	 */
	public CustomerAgent(Person p){
		super();
		this.name = p.getName();
		menu = new Menu();
		Random r2 = new Random();
		this.p = p;
	}

	/**
	 * hack to establish connection to Host agent.
	 */
	public void setPosition(){
		tablex2 = customerGui.tablex2;
		tablex3 = customerGui.tablex3;
		tabley2 = customerGui.tabley2;
		tabley3 = customerGui.tabley3;
	}
	
	public void setWaiter(WaiterAgent w) {
		this.waiters = w;
	}
	public void setHost(HostAgent host) {
		this.host = host;
	}
	public void setCashier(CashierAgent cashier){
		this.cashier = cashier;
	}
	// Messages
	public void msgReadyToSeat(){
		AlertLog.getInstance().logMessage(AlertTag.RossCustomer, p.getName(),"Ready to be seated");
		event = AgentEvent.ReadyToSeat;
		stateChanged();
	}
	
	public void gotHungry() {//from animation
		AlertLog.getInstance().logMessage(AlertTag.RossCustomer, p.getName(),"I'm hungry");
		event = AgentEvent.gotHungry;
		stateChanged();
	}

	public void msgFollowMe(Menu m,int table,Waiter w) {
		AlertLog.getInstance().logMessage(AlertTag.RossCustomer, p.getName(),"Msg Recieved to seat");
		Random r = new Random();
		int setChoice = r.nextInt(4);
		for(int i=0; i<m.menu.size(); i++){
			if(p.money< m.getPrice(i))
				setChoice = 3;
		}
		this.waiters = w;
		seatnum = table;
		event = AgentEvent.followHost;
		this.menu = m;
		Choice = m.getName(setChoice);
		price = m.getPrice(setChoice);
		//if (Cash < m.getPrice(setChoice))
			
		stateChanged();
		
	}

	public void msgWhatDoYouWant() {
		//from animation
		AlertLog.getInstance().logMessage(AlertTag.RossCustomer, p.getName(),"msg What do you want");
		event = AgentEvent.TakingOrder;
		stateChanged();
	}
	public void msgReorder(Menu m){
		AlertLog.getInstance().logMessage(AlertTag.RossCustomer, p.getName(),"Reorder");
		this.menu = m;
		Random r = new Random();
		int setChoice = r.nextInt(menu.menu.size());
		Choice = m.getName(setChoice);
		price = m.getPrice(setChoice);
		event = AgentEvent.TakingOrder;
		state = AgentState.TakingOrder;
		stateChanged();
	}
	public void msgHereIsTheFood() {
		//from animation
		AlertLog.getInstance().logMessage(AlertTag.RossCustomer, p.getName(),"Here is food");
		event = AgentEvent.beingServed;
		stateChanged();
	}
	public void msgGoToPay(){
		AlertLog.getInstance().logMessage(AlertTag.RossCustomer, p.getName(),"Msg go to pay");
		event = AgentEvent.Paying;
		stateChanged();
	}
	public void msgGiveChange(double change){
		AlertLog.getInstance().logMessage(AlertTag.RossCustomer, p.getName(),"msg Give change");
		p.money = (float)change;
		event = AgentEvent.Paied;
		stateChanged();
	}
	public void msgAnimationFinishedGoToSeat() {
		//from animation
		event = AgentEvent.seated;
		stateChanged();
	}
	public void msgAnimationFinishedLeaveRestaurant() {
		//from animation
		AlertLog.getInstance().logMessage(AlertTag.RossCustomer, p.getName(),"msg Animation finished leave Restaurant");
		event = AgentEvent.doneLeaving;
		stateChanged();
	}
	@Override
	public void YouOweUs(double remaining_cost) {
		// TODO Auto-generated method stub
		
	}
	public void msgAtCashier(){
		atCashier = true;
		stateChanged();
	}
	public void msgTableFull(){
		tableFull = true;
		stateChanged();
	}
	public void msgTableAvailable(){
		tableFull = false;
		stateChanged();
	}
	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	protected boolean pickAndExecuteAnAction() {
		//	CustomerAgent is a finite state machine
		if (state == AgentState.DoingNothing && event == AgentEvent.gotHungry ){
			state = AgentState.ReadyToSeat;
			goToRestaurant();
			return true;
		}
		
		if (state == AgentState.ReadyToSeat && event == AgentEvent.ReadyToSeat ){
			state = AgentState.WaitingInRestaurant;
			goToWait();
			return true;
				}

		if (state == AgentState.WaitingInRestaurant && event == AgentEvent.followHost ){
			state = AgentState.BeingSeated;
			SitDown();
			return true;
		}
		if (state == AgentState.BeingSeated && event == AgentEvent.ordering){
			state = AgentState.TakingOrder;
			TakeOrder();
			return true;
		}
		if (state == AgentState.TakingOrder && event == AgentEvent.TakingOrder){
			state = AgentState.Ordered;
			HereIsMyOrder();
			return true;
		}
		if (state == AgentState.Ordered && event == AgentEvent.beingServed){
			state = AgentState.Eating;
			EatFood();
			return true;
		}
		if (state == AgentState.Eating && event == AgentEvent.doneEating){
			state = AgentState.Leaving;
			IWantToPay();
			return true;
		}
		if (state == AgentState.Leaving && event == AgentEvent.Paying){
			state = AgentState.Paied;
			Pay();
			return true;
		}
		if (state == AgentState.Paied && event == AgentEvent.Paied && atCashier == true){
			state = AgentState.Leave;
			atCashier = false;
			Leave();
			return true;
		}
		if (state == AgentState.Leave && event == AgentEvent.doneLeaving){
			state = AgentState.DoingNothing;
			//no action
			return true;
		}
		return false;
	}

	// Actions
	private void goToWait(){
		AlertLog.getInstance().logMessage(AlertTag.RossCustomer, p.getName(),"Moving to the first");
		customerGui.DoGoToWait();
	}
	private void goToRestaurant() {
		AlertLog.getInstance().logMessage(AlertTag.RossCustomer, p.getName(),"Going to restaurant");
		if(tableFull == true)
		{
			timer.schedule(new TimerTask(){
				public void run(){
					AlertLog.getInstance().logMessage(AlertTag.RossCustomer, p.getName(),"let me think");
					Random r3 = new Random();
					int leave = r3.nextInt(2);
					if(leave == 0){
						AlertLog.getInstance().logMessage(AlertTag.RossCustomer, p.getName(),"table is full");
						state = AgentState.Paied;
						event = AgentEvent.Paied;
						atCashier = true;
					}
						else {
							AlertLog.getInstance().logMessage(AlertTag.RossCustomer, p.getName(),"see if there is a talbe");
							state = AgentState.DoingNothing;
							event = AgentEvent.gotHungry;
						}
						stateChanged();
					
				}
			}, 10000);
			
		}
		else {
			host.msgIWantFood(this);
		}
	}

	private void SitDown() {
		AlertLog.getInstance().logMessage(AlertTag.RossCustomer, p.getName(),"being seated, going to talbe");
		customerGui.DoGoToSeat(seatnum);//hack; only one table	
		timer.schedule(new TimerTask(){
			public void run(){
				Do("I'm thinking about the order");
				event = AgentEvent.ordering;
				stateChanged();
			}
		}, ThinkingTime);
	}

	private void TakeOrder(){
		AlertLog.getInstance().logMessage(AlertTag.RossCustomer, p.getName(),"take the order");
		customerGui.msgSetOrder();
		waiters.msgIWantFood(this);
	}
	private void HereIsMyOrder(){
		AlertLog.getInstance().logMessage(AlertTag.RossCustomer, p.getName(),"here is my order");
		waiters.msgHereIsMyOrder(this, Choice);
		customerGui.msgCancelSignal();
	}
	private void EatFood() {
		AlertLog.getInstance().logMessage(AlertTag.RossCustomer, p.getName(),"eat food");
		//This next complicated line creates and starts a timer thread.
		//We schedule a deadline of getHungerLevel()*1000 milliseconds.
		//When that time elapses, it will call back to the run routine
		//located in the anonymous class created right there inline:
		//TimerTask is an interface that we implement right there inline.
		//Since Java does not all us to pass functions, only objects.
		//So, we use Java syntactic mechanism to create an
		//anonymous inner class that has the public method run() in it.
		timer.schedule(new TimerTask() {
			Object cookie = 1;
			public void run() {
				AlertLog.getInstance().logMessage(AlertTag.RossCustomer, p.getName(),"done eating");
				event = AgentEvent.doneEating;
				//isHungry = false;
				stateChanged();
			}
		},
		EatingTime);//getHungerLevel() * 1000);//how long to wait before running task
		customerGui.msgShowOrder();
	}
	
	private void IWantToPay() {
		AlertLog.getInstance().logMessage(AlertTag.RossCustomer, p.getName(),"paying");
		waiters.msgIWantToPay(this);
		
	}
	private void Pay(){
		AlertLog.getInstance().logMessage(AlertTag.RossCustomer, p.getName(),"leave table to pay");
		waiters.msgLeavingTable(this);
		cashier.msgPay(this, p.money);
		customerGui.DoGoToCashier();
		customerGui.msgHideOrder();
	}
	// Accessors, etc.
	private void Leave(){
		customerGui.DoExitRestaurant();
		//put person message here
		p.hungerLevel = 0;
		p.msgDone();
	}
	public String getName() {
		return name;
	}
	
	public double getMoney(){
		return p.money;
	}
	public int getHungerLevel() {
		return hungerLevel;
	}

	public void setHungerLevel(int hungerLevel) {
		this.hungerLevel = hungerLevel;
		//could be a state change. Maybe you don't
		//need to eat until hunger lever is > 5?
	}

	public String toString() {
		return "customer " + getName();
	}

	public void setGui(CustomerGui g, int number) {
		customerGui = g;
		customerGui.setNumber(number);
	}

	public CustomerGui getGui() {
		return customerGui;
	}

	


	

	
}

