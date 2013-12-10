package Cheng;import Cheng.CustomerAgent.AgentEvent;import Cheng.gui.CookGui;import Cheng.gui.CustomerGui;import Cheng.gui.RestaurantGui;import Cheng.gui.WaiterGui;import agent.Agent;import agents.Grocery;import java.util.*;import simcity201.gui.GlobalMap;import simcity201.interfaces.NewMarketInteraction;public class CookAgent extends Agent implements NewMarketInteraction{		public enum CookState{Pending,PickingUp, Cooking, Plating, Done,Out};	private CookState state = CookState.Pending;	Timer timer;	private boolean MarketNoFood = false;	private boolean Market1NoFood = false;	private boolean Market2NoFood = false;	private CookGui cookGui;	private Menu menu;	private MarketAgent market;	private MarketAgent market1;	private MarketAgent market2;	private CashierAgent cashier;	private String name;	List<Order> order = Collections.synchronizedList(new ArrayList<Order>());	public List<Food> food = Collections.synchronizedList(new ArrayList<Food>());	List<Grocery> g = new ArrayList<Grocery>();	private enum FoodState{Out, Plenty, Asked,  Replenished};	private WaiterAgent waiter;	private boolean done = false;	private boolean noFood = false;	private boolean atBingxiang = false;	private boolean atGrill = false;	private boolean atPlating = false;	private boolean atOrigin = false;	public enum PayState{none,pending};	PayState ps = PayState.none;	private float money = 0;	public Map<String , Double> map = new HashMap<String, Double>();	public CookAgent(String name){		super();		timer = new Timer();		this.cookGui =null;		this.name = name;		map.put("Steak", (double)5000);		map.put("Pizza", (double)1000);		map.put("Chicken", (double)2000);		map.put("Salad", (double)4000);		food.add(new Food("Steak", 5000,0,FoodState.Plenty));		food.add(new Food("Pizza",1000,0,FoodState.Plenty));		food.add(new Food("Chicken",2000,5,FoodState.Plenty));		food.add(new Food("Salad", 4000,5,FoodState.Plenty));	}	public void msgHereIsPrice(List<Grocery> orders, float price) {		cashier.msgHereIsPrice(orders, price);		stateChanged();			}	public void msgPayMarket(double money){		this.money = (float) money;		ps = PayState.pending;		stateChanged();	}	@Override	public void msgHereIsFood(List<Grocery> orders) {		food.get(0).number +=10;		food.get(1).number +=10;		food.get(2).number +=10;		food.get(3).number +=10;		Do("OrderRecieved!!!!!!!!!!!!!!!!!!!!!!!!!!");		stateChanged();			}	@Override	public void msgNoFoodForYou() {		food.get(0).number +=10;		food.get(1).number +=10;		food.get(2).number +=10;		food.get(3).number +=10;		Do("NoFoodForMe!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");		stateChanged();	}	public void setRunOutOfFood(){		for(Food f : food){			f.number = 0;		}	}	public void msgCookOrder(WaiterAgent w, String Choice, int table){		Do("msgCookOrder");		for(Food f : food){			if(f.Choice.equals(Choice)){				if(f.number == 0){					Do("Run out of Food");					f.s = FoodState.Out;					order.add(new Order(w,Choice,table,CookState.Out));				}				else{					Do("add order");					f.number--;					order.add(new Order(w,Choice,table, CookState.Pending));				}				}		}		stateChanged();	}	public void setGui(CookGui gui) {		this.cookGui = gui;	}	public CookGui getGui() {		return cookGui;	}	public void setMarket(MarketAgent m){		this.market = m;	}	public void setMarket1(MarketAgent m){		this.market1 = m;	}	public void setMarket2(MarketAgent m){		this.market2 = m;	}	public String getName() {		return name;	}	public void msgAtBingxiang(){		atBingxiang = true;		stateChanged();	}	public void msgAtGrill(){		atGrill = true;		stateChanged();	}	public void msgAtPlating(){		atPlating = true;		stateChanged();	}	public void msgAtOrigin(){		atOrigin = true;		stateChanged();	}	public void msgHereIsDelivery(String choice, int number){		Do("Food Replenished");		for(Food f : food){			if(f.Choice.equals(choice)){				f.number += number;				f.s = FoodState.Replenished;				Do(choice+f.number);			}		}		stateChanged();	}	public void msgMarketNoFood(){//		food.get(0).number +=10;//		food.get(1).number +=10;//		food.get(2).number += 10;//		food.get(3).number +=10;//		if(m == market){//			MarketNoFood = true;//			Market1NoFood = false;//			Market2NoFood = false;//		}//		if(m == market1){//			Market1NoFood = true;//			MarketNoFood = false;//			Market2NoFood = false;//		}//		if(m == market2){//			Market2NoFood = true;//			Market1NoFood = false;//			MarketNoFood = false;//		}		stateChanged();	}	public void msgFoodDone(Order o ){		Do("msgFoodDone");		o.s = CookState.Done;		stateChanged();	}		@Override	protected boolean pickAndExecuteAnAction() {		// TODO Auto-generated method stub		Order O = null;		Food F = null;		synchronized(food){			for(Food f : food){			if(f.number == 0){				F = f;				break;			}		}		}				if(F != null){			AskMarket(F);			return true;		}				synchronized(order){			for(Order o : order)		{			if(o.s == CookState.Pending && atOrigin == true)			{				atOrigin = false;				O = o;				break;			}		}		}		if(O != null){			PickUp(O);			return true;		}				synchronized(order){for(Order o : order)		{			if(o.s == CookState.Cooking && atBingxiang == true)			{				atBingxiang = false;				O = o;				break;			}		}		}		if(O != null){			CookIt(O);			return true;		}				synchronized(order){for(Order o : order){			if(o.s == CookState.Plating && atGrill == true && done == true){				atGrill = false;				done = false;				O = o;				break;			}		}		}		if(O != null){			PlateIt(O);			return true;		}				synchronized(order){for(Order o : order){			if(o.s == CookState.Done && atPlating == true){				atPlating = false;				O = o;				break;			}		}		}		if(O != null){			NotifyWaiter(O);			return true;		}				for(Order o : order){			if(o.s == CookState.Out){				O = o;				break;			}		}				if(O != null){			TellWaiter(O);			return true;		}				synchronized(food){for(Food f : food){			if(f.s == FoodState.Out){				F = f;				break;			}		}		}				if(F != null){			AskMarket(F);			return true;		}				if(ps == PayState.pending){					payMarket();			return true;		}				return false;	}		public void	payMarket(){		GlobalMap.getGlobalMap().marketHandler.msgHereIsMoney(this, this.money);		ps = PayState.none;	}		public void PickUp(Order o){				cookGui.DoGoToBingxiang();		o.s = CookState.Cooking;	}	public void CookIt(Order o){		//DoCooking() animation		cookGui.msgshowOrder(o.Choice);		Do("Cooking");		cookGui.DoCooking();		o.s = CookState.Plating;		long time = (map.get(o.Choice)).longValue();		timer.schedule(new TimerTask(){			public void run(){				done = true;				stateChanged();			}		}, time);	}	public void PlateIt(Order o) {		//DoPlating animation		cookGui.DoPlating();		o.s = CookState.Done;		Do("Plating");			}	public void NotifyWaiter(Order o){		o.w.msgFoodReady(o.Choice,o.table);		order.remove(o);		cookGui.DoGoBack();		cookGui.msghideOrder();	}		public void TellWaiter(Order o){		o.w.msgOutOfFood(o.Choice, o.table);		order.remove(o);	}		public void AskMarket(Food f){//		if(MarketNoFood == false && Market1NoFood == false && Market2NoFood == false){//			market.msgOutOfFood(f.Choice);//		}//		if(MarketNoFood == true){//		market1.msgOutOfFood(f.Choice);//		}//		if(Market1NoFood == true){//			market2.msgOutOfFood(f.Choice);//		}//		if(Market2NoFood == true){//			market.msgOutOfFood(f.Choice);//		}		f.number +=1;		g.add(new Grocery(f.Choice,10));		GlobalMap.getGlobalMap().marketHandler.msgIWantFood(this, g);		f.s = FoodState.Asked;	}	private class Order{				WaiterAgent w;		String Choice;		int table;		CookState s;				Order(WaiterAgent w, String Choice, int table, CookState s){			this.w = w;			this.Choice = Choice;			this.table = table;			this.s = s;		}				}			public class Food{		public String Choice;		int CookingTime;		public int number;		FoodState s;		Food(String Choice, int CookingTime, int number, FoodState s){			this.Choice = Choice;			this.CookingTime = CookingTime;			this.number = number;			this.s = s;		}	}	public void setCashier(CashierAgent cashier){		this.cashier = cashier;	}	@Override	public void msgOutOfStock() {		// TODO Auto-generated method stub			}			}