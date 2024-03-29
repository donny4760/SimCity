package newMarket;

import java.util.ArrayList;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.concurrent.Semaphore;

import newMarket.gui.Line;
import newMarket.gui.MarketDealerGui;
import simcity201.gui.CarGui;
import simcity201.gui.GlobalMap;
import tracePanelpackage.AlertLog;
import tracePanelpackage.AlertTag;
import agent.Agent;
import agents.CarAgent;
import agents.Grocery;
import agents.Person;
import animation.SimcityPanel;

public class MarketDealerAgent extends Agent {
	
	public MarketDealerAgent() {
		
	}
	
	public MarketDealerAgent(Person p) {
		this.self = p;
	}
	
	/*		Data		*/
	
	public Person self;
	
	public MarketDealerGui gui;
	
	private Semaphore atDestination = new Semaphore(0, true);
	
	//list of orders to be used for car orders
	private List<MyOrder> orders
		= Collections.synchronizedList(new ArrayList<MyOrder>());
	
	private class MyOrder{
		String type;
		MarketCustomerAgent c;
		OrderState s;
		float price;
		
		MyOrder(String type, MarketCustomerAgent c, OrderState s) {
			this.type = type;
			this.c = c;
			this.s = s;
		}
	}
	public enum OrderState { pending, processing, paid, notEnoughPaid,  };
	
	/*		Messages		*/
	
	/**
	 * from the gui
	 */
	public void gui_msgBackAtHomeBase() {
		atDestination.release();
	}
	
	/**
	 * from customer
	 * add car order to the list of orders
	 * @param c
	 * @param type
	 */
	public void msgIWantCar(MarketCustomerAgent c, String type) {
		//print("msgIWantCar called"); 
		AlertLog.getInstance().logMessage(AlertTag.MarketDealer, this.getName(), "msgIWantCar called");
		orders.add(new MyOrder(type, c, OrderState.pending));
		stateChanged();
	}
	
	/**
	 * from customer
	 * give money to dealer, check to see if enough money paid
	 * @param c
	 * @param money
	 */
	public void msgHereIsMoney(MarketCustomerAgent c, float money) {
		//print("msgHereIsMoney called");
		AlertLog.getInstance().logMessage(AlertTag.MarketDealer, this.getName(), "msgHereIsMoney called");
		
		synchronized(orders) {
			for (MyOrder o : orders) {
				if (o.c.equals(c) && o.s==OrderState.processing) {
					if (o.price > money) {
						o.s = OrderState.notEnoughPaid;
					}else {
						o.s = OrderState.paid;
					}
					break;
				}
			}
		}
		stateChanged();
	}
	
	/*		Scheduler		*/
	
	protected boolean pickAndExecuteAnAction() {
		
		try {
		
		MyOrder temp = null;
		
		//if there exists a myorder o in orders such o.s == pending, then givePrice(o)
		synchronized(orders) {
			for (MyOrder o : orders) {
				if(o.s == OrderState.pending ) {
					//givePrice(o);
					//return true;
					temp = o;
					break;
				}
			}
		}	if (temp!=null) { givePrice(temp); return true; }
		
		//if there exists a myorder in orders such that o.s == paid, then giveCar()
		synchronized(orders) {
			for (MyOrder o : orders) {
				if(o.s == OrderState.paid ) {
					//giveCar(o);
					//return true;
					temp = o;
					break;
				}
			}
		}	if (temp!=null) { giveCar(temp); return true; }
		

		//if there exists a myorder in orders such that o.s == notEnoughPaid, then kickout(o)
		synchronized(orders) {
			for (MyOrder o : orders) {
				if(o.s == OrderState.notEnoughPaid ) {
					//givePrice(o);
					//return true;
					temp = o;
					break;
				}
			}
		}	if (temp!=null) { kickout(temp); return true; }
		
		} catch (ConcurrentModificationException e) {
			return false;
		}
		
		return false;
	}

	/*		Action		*/
	
	//order state = processing
	//formulate price based on appropriate car price in newMarket panel
	//send the car price to customer
	private void givePrice(MyOrder o) {
		o.s = OrderState.processing;
		float price = 0;
			price += NewMarket.prices.get((o.type));
		o.price = price;
		if (price > 0) {
			o.c.msgHereIsCarPrice(o.type, price);
		}else {
			o.c.msgHereIsCarPrice(o.type, -1);
		}
	}
	
	//remove order o from orders
	//make a new car and carGui and add it to Sim City
	private void giveCar(MyOrder o) {
		orders.remove(o);
		
		//print("FETCHING CAR");
		AlertLog.getInstance().logMessage(AlertTag.MarketDealer, this.getName(), "retreiving the car");
		
		gui.DoFetchCar(o.type);
		
		try {
			atDestination.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	
		//giving car block
		CarAgent car = new CarAgent(o.type);
		CarGui carGui = new CarGui(car, GlobalMap.getGlobalMap().getAstar());
		SimcityPanel.guis.add(carGui);
		car.setGui(carGui);
		o.c.msgHereIsCar(car);
		gui.line.exitLine(o.c.getGui());
	}
	
	//remove order o from orders
	//message o owner to get out
	private void kickout(MyOrder o) {
		orders.remove(o);
		o.c.msgGetOut();
		gui.line.exitLine(o.c.getGui());
	}
	
	public void setGui(MarketDealerGui gui) {
		this.gui = gui;
	}
	
	 public Line getLine() {
		return (gui.line);
	}
	
	 public MarketDealerGui getGui() {
		 return (this.gui);
	 }
}
