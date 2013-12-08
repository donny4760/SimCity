package agents;

import simcity201.gui.CarGui;
import simcity201.gui.PassengerGui;
import agent.Agent;
import agents.BusAgent.TranEvent;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Semaphore;

/**
 * Restaurant customer agent.
 */
	public class PassengerAgent extends Agent {
	private int carx,cary;
	private String name;
	private PassengerGui passengerGui;
	private String waitDest;
	private String dest ;
	private String busDest;
	private String carDest;
	private StopAgent stop = null;
	private CarAgent car = null;
	private BusAgent bus = null;
	private CarGui carGui = null;
	Timer timer = new Timer();
	private Person person;
	private Semaphore atDest = new Semaphore(0,true);
	private Semaphore atStop = new Semaphore(0,true);
	private Semaphore atCar = new Semaphore(0,true);

	private Semaphore atSpecificDest = new Semaphore(0,true);
	private Semaphore atClosestTile = new Semaphore(0,true);
	public enum AgentState
	{DoingNothing,NeedBus,Walking,WaitingAtStop, OnBus, Arrived, NeedCar, AtCar, OnCar, OffCar, noCar, InBuilding, Pressed, Enter,WalkingClose};
	private AgentState state = AgentState.DoingNothing;//The start state

	public enum AgentEvent 
	{none,goToStop, GettingOn, GettingOff, GoingToCar, Driving, LeaveCar, Walk, LeaveBus, Enter, LeaveCarEnter, PressStop, Near,WalkClose};

	AgentEvent event = AgentEvent.none;
	
	public PassengerAgent(String name, Person p){
		super();
		this.name = name;
		this.person = p;
		
	}

	public String getPassengerName() {
		return name;
		
	}
	// Messages
	public void msgGoTo(Person p, String dest,CarAgent car, StopAgent stop){
		this.dest = dest;
		this.person = p;
		this.stop = stop;
		passengerGui.show();
		if(stop != null && dest != "Apart"){
			if(dest == "Rest1" || dest == "Rest2" || dest == "Rest3"||dest == "Rest4" || dest == "Rest6" )
					this.busDest = "Restaurants1";
				else if(dest == "Rest5" || dest == "House3")
					this.busDest = "Restaurants2";
				else if(dest == "House1"|| dest == "House2")
					this.busDest = "House";
				else this.busDest = dest;
		//computing waitDest
			if(p.location == "Rest1" || p.location == "Rest2" || p.location == "Rest3"||p.location == "Rest4" || p.location == "Rest6" )
					this.waitDest = "Restaurants1";
				else if(p.location == "Rest5" || p.location == "House3")
					this.waitDest = "Restaurants2";
				else if(p.location == "House1"|| p.location == "House2")
					this.waitDest = "House";
				else if(p.location == "birth")
					this.waitDest = "Bank";
				else if(p.location == "Apart")
					this.waitDest = "Restaurants1";
				else this.waitDest = p.location;
		
		if(this.waitDest == this.busDest || (this.waitDest == "Market" && this.dest == "House1")){
			state = AgentState.Walking;
			event = AgentEvent.Near;
		}
		
		else{
		this.state = AgentState.NeedBus;
		this.event = AgentEvent.goToStop;
		}
		
		}
		else if(car != null){
			this.car = car;
			this.carGui = car.carGui;
			this.carDest = dest;
			if(dest == p.location){
				state = AgentState.Walking;
				event = AgentEvent.Near;
			}
			else{
				this.state = AgentState.NeedCar;
			    this.event = AgentEvent.GoingToCar;
			}
		}
		else {
			state = AgentState.noCar;
			event = AgentEvent.WalkClose;
		}
		stateChanged();
	}
	
	public void msgGetOn(BusAgent b){
		event = AgentEvent.GettingOn;
		this.bus = b;
		stateChanged();
	}
	public void msgGetOff(String dest){
		event = AgentEvent.GettingOff;
		stateChanged();
	}
	
	public void msgYouAreHere(int carx, int cary){
		Do("Im here");
		this.carx = carx;
		this.cary = cary;
		event = AgentEvent.LeaveCar;
		stateChanged();
	}
	
	public void msgAtCar(){
		atCar.release();
		stateChanged();
	}
	public void	msgAtDest(){
		atDest.release();
		stateChanged();
	}
	
	public void msgAtStop(){
		atStop.release();
		stateChanged();
	}
	

	public void msgAtClosestTile(){
	   atClosestTile.release();
	   stateChanged();
	}
	
	public void msgAtSpecificDest(){
		atSpecificDest.release();
		stateChanged();
	}
	
	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	protected boolean pickAndExecuteAnAction() {
		//	CustomerAgent is a finite state machine
		if (state == AgentState.NeedBus && event == AgentEvent.goToStop ){
			state = AgentState.WaitingAtStop;
			goToStop();
			return true;
		}
		
		if (state == AgentState.WaitingAtStop && event == AgentEvent.PressStop ){
			state = AgentState.Pressed;
			PressStop();
			return true;
		}
		if (state == AgentState.Pressed && event == AgentEvent.GettingOn ){
			state = AgentState.OnBus;
			GetOn();
			return true;
		}
		if (state == AgentState.OnBus && event == AgentEvent.GettingOff ){
			state = AgentState.Arrived;
			GetOff();
			return true;
		}
		if (state == AgentState.Arrived && event == AgentEvent.LeaveBus){
			state = AgentState.InBuilding;
			AtDest();
			return true;
		}
		
		if (state == AgentState.NeedCar && event == AgentEvent.GoingToCar){
			state = AgentState.OnCar;
			goToCar();
			return true;
		}
		
		if (state == AgentState.OnCar && event == AgentEvent.LeaveCar ){
			state = AgentState.OffCar;
			GetOffCar();
			return true;
		}
		if (state == AgentState.OffCar && event == AgentEvent.LeaveCarEnter){
			state = AgentState.InBuilding;
			AtDest();
			return true;
		}
		if(state == AgentState.noCar && event == AgentEvent.WalkClose){
         state = AgentState.WalkingClose;
         WalkToTile();
         return true;
      }
		if(state == AgentState.WalkingClose && event == AgentEvent.Walk){
			state = AgentState.Walking;
			Walk();
			return true;
		}
		if (state == AgentState.Walking && event == AgentEvent.Near){
			state = AgentState.Enter;
			WalkAfter();
			return true;
		}
		if (state == AgentState.Enter && event == AgentEvent.Enter){
			state = AgentState.InBuilding;
			AtDest();
			return true;
		}
		return false;
	}

	private void GetOff() {
		// TODO Auto-generated method stub
		Do("GettingOff");
		passengerGui.showBus(this.busDest);
		try {
			atStop.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		WalkAfter();
		event = AgentEvent.LeaveBus;
	}

	private void GetOn() {
		// TODO Auto-generated method stub
		Do("GettingOnBus");
		bus.msgImOn(this);
		passengerGui.hide();
	}

	// Actions
	private void WalkToTile(){
	   Do("Walkingastarsadlkfjsadlkfjslfjsalfjdslfjslkfjslkfjsl");
	   passengerGui.goToClosestTile();
	   try
      {
         atClosestTile.acquire();
      } catch (InterruptedException e)
      {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
            event = AgentEvent.Walk;
        
	  
	}
	private void Walk(){

		Do("WalkingAstar");
		passengerGui.DoWalkTo(this.dest);
		try {
			atDest.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		event = AgentEvent.Near;
		
	}
	private void WalkAfter(){
		Do("Walking to specific dest");
		passengerGui.doWalkAfter(this.dest);
		try {
			atSpecificDest.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		event = AgentEvent.Enter;
		
	}
	private void goToStop(){
		Do("GoingToStop");
		passengerGui.DoGoToStop(this.waitDest);
		try {
			atStop.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		passengerGui.DoWait(this.waitDest);
		event = AgentEvent.PressStop;
	}
	private void PressStop(){
		stop.msgINeedBus(this, this.waitDest, this.busDest);
	}
	
	private void goToCar(){
		Do("Going to my car");
		passengerGui.DoGoToCar(carGui.getXPos(), carGui.getYPos());
		try {
			atCar.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		passengerGui.hide();
		passengerGui.DoEnterCar();
		car.msgINeedARide(this,person.location, this.carDest);
	}
	
	
	private void GetOffCar(){
		passengerGui.showCar(carGui.getXPos(), carGui.getYPos());
		WalkAfter();
		timer.schedule(new TimerTask() {
			public void run() {
				print("DoneWaiting");
				event = AgentEvent.LeaveCarEnter;
				stateChanged();
			}
		},2000
		);
		
		
	}
	private void AtDest(){
		Do("I'm at destination");
		person.msgAtDest();
		passengerGui.hide();
		person.location = dest;
	}

	public String getName() {
		return name;
	}
	

	public String toString() {
		return "customer " + getName();
	}

	public void setGui(PassengerGui g) {
		passengerGui = g;
	}

	public PassengerGui getGui() {
		return passengerGui;
	}
	
	
	public void setStop(StopAgent stop) {
		// TODO Auto-generated method stub
		this.stop = stop;
	}
	public void setCar(CarAgent car) {
		// TODO Auto-generated method stub
		this.car = car;
	}
}

