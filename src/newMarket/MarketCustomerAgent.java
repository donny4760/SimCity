package newMarket;

import java.util.ArrayList;
import java.util.List;

import newMarket.test.mock.EventLog;
import newMarket.test.mock.LoggedEvent;
import agent.Agent;
import agents.Grocery;
import agents.Person;
import agents.Task;

public class MarketCustomerAgent extends Agent {

	/*		Data		*/
	
	public Person self;
	
	MarketCashierAgent cashier;
	NewMarket market;
	
	public EventLog log = new EventLog();
	
	public enum AgentState { none, waitingForPrice, needToPayGroceries, leaving, waitingForGroceries, gotGrocery, gotKickedOut };
	AgentState state;
	
	float orderPriceQuote = -1;
	List<Grocery> order;
	
	public MarketCustomerAgent(Person p, MarketCashierAgent cashier) {
		this.self = p;
		this.state = AgentState.none;
		this.cashier = cashier;
	}

	/*		Messages		*/
	
	public void msgHereIsPrice(List<Grocery> order, float price) {
		
		if (state == AgentState.waitingForPrice) {
			// maybe check order?
			orderPriceQuote = price;
			state = AgentState.needToPayGroceries;
		}
		stateChanged();
		log.add(new LoggedEvent("Received msgHereIsPrice."));
	}
	
	public void msgHereIsFood(List<Grocery> order) {
		if (state == AgentState.waitingForGroceries) {
			state = AgentState.gotGrocery;
		}
		stateChanged();
		log.add(new LoggedEvent("Received msgHereIsFood."));
	}
	
	public void msgGetOut() {
		if (state == AgentState.waitingForGroceries) {
			state = AgentState.gotKickedOut;
		}
		stateChanged();
		log.add(new LoggedEvent("Received msgGetOut."));
	}
	
	/*		Scheduler		*/
	
	protected boolean pickAndExecuteAnAction() {
		
		if (state==AgentState.none ) {
			if(self.currentTask.sTasks.size() == 0) {
				doLeave();
				return false;
			}
			for (Task.specificTask st : self.currentTask.sTasks) {
				if (st.equals(Task.specificTask.buyGroceries)) {
					doOrder();
					return true;
				}
			}
		}
		
		if (state==AgentState.needToPayGroceries) {
			doPayGroceries();
			return true;
		}
		
		if (state == AgentState.gotGrocery) {
			doUpdateGroceries();
			return true;
		}
		
		if (state == AgentState.gotKickedOut) {
			doLeave();
			return false;
		}
		
		return false;
	}


	

	/*		Action		*/
	
	private void doOrder() {
		state = AgentState.waitingForPrice;
		order = self.homefood;
		//order.add(new Grocery("pizza", 1) );
		print("gotfood");
		cashier.msgIWantFood(this, order);
	}
	private void doPayGroceries() {
		state = AgentState.waitingForGroceries;
		self.currentTask.sTasks.remove(Task.specificTask.buyGroceries);
		if (self.money < orderPriceQuote) {
			cashier.msgHereIsMoney(this, (float)self.money);
		}else {
			cashier.msgHereIsMoney(this, orderPriceQuote);
		}
	}
	
	
	private void doLeave() {
		state = AgentState.leaving;
		self.msgDone();
		for(Grocery g: self.groceries) {
			print(Integer.toString(g.getAmount()));
		}
		market.removeCustomer(this);
	}
	
	private void doUpdateGroceries() {
		state = AgentState.none;
		self.money -= orderPriceQuote;
		for(Grocery g: order) {
			self.groceries.add(g);
		}
	}

	/*		Utilities		*/
	
	public void setMarket(NewMarket newMarket) {
		this.market = newMarket;
	}
}

