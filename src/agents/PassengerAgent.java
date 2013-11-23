package agents;

import simcity201.gui.PassengerGui;
import agent.Agent;
import agents.BusAgent.TranEvent;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Restaurant customer agent.
 */
	public class PassengerAgent extends Agent {
	private String name;
	private PassengerGui passengerGui;
	private String waitDest = "Bank";
	private String dest = "Restaurants1";
	private String walkDest ="Rest6";
	private String carDest = "Rest1";
	private StopAgent stop = null;
	private CarAgent car = null;
	private BusAgent bus = null;
	private boolean atCar = false;
	Timer timer = new Timer();
	public enum AgentState
	{DoingNothing,Walking,WaitingAtStop, OnBus, Arrived, NeedCar, AtCar, OnCar, OffCar, noCar, InBuilding, Pressed};
	private AgentState state = AgentState.DoingNothing;//The start state

	public enum AgentEvent 
	{none,goToStop, GettingOn, GettingOff, GoingToCar, Driving, LeaveCar, Walk, LeaveBus, Enter, LeaveCarEnter, PressStop};
	AgentEvent event = AgentEvent.none;
	
	public PassengerAgent(String name){
		super();
		this.name = name;
		if(car == null && bus == null){
			state = AgentState.noCar;
			event = AgentEvent.Walk;
		}
	}

	/**
	 * hack to establish connection to Host agent.
	 */

	public String getPassengerName() {
		return name;
		
	}
	// Messages

	public void msgGetOn(BusAgent b){
		event = AgentEvent.GettingOn;
		this.bus = b;
		stateChanged();
	}
	public void msgGetOff(String dest){
		event = AgentEvent.GettingOff;
		stateChanged();
	}
	
	public void msgYouAreHere(){
		Do("Im here");
		event = AgentEvent.LeaveCar;
		stateChanged();
	}
	
	public void msgAtCar(){
		atCar = true;
		stateChanged();
	}
	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	protected boolean pickAndExecuteAnAction() {
		//	CustomerAgent is a finite state machine
		if (state == AgentState.DoingNothing && event == AgentEvent.goToStop ){
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
			state = AgentState.AtCar;
			goToCar();
			return true;
		}
		if (state == AgentState.AtCar && event == AgentEvent.Driving && atCar == true){
			atCar = false;
			state = AgentState.OnCar;
			GetOnCar();
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
		
		if(state == AgentState.noCar && event == AgentEvent.Walk){
			state = AgentState.Walking;
			Walk();
			return true;
		}
		if (state == AgentState.Walking && event == AgentEvent.Enter){
			state = AgentState.InBuilding;
			AtDest();
			return true;
		}
		return false;
	}

	private void GetOff() {
		// TODO Auto-generated method stub
		Do("GettingOff");
		passengerGui.DoGoToStop(dest);
		passengerGui.show(dest);
		timer.schedule(new TimerTask() {
			public void run() {
				print("DoneWaiting");
				event = AgentEvent.LeaveBus;
				stateChanged();
			}
		},2000
		);
	}

	private void GetOn() {
		// TODO Auto-generated method stub
		Do("GettingOnBus");
		bus.msgImOn(this);
		passengerGui.hide();
	}

	// Actions
	private void Walk(){
		Do("Walking");
		passengerGui.DoWalkTo(walkDest);
		timer.schedule(new TimerTask() {
			public void run() {
				print("DoneWaiting");
				event = AgentEvent.Enter;
				stateChanged();
			}
		},5000
		);
		
	}
	private void goToStop(){
		Do("GoingToStop");
		passengerGui.DoGoToStop(waitDest);
		timer.schedule(new TimerTask() {
			public void run() {
				print("DoneWaiting");
				event = AgentEvent.PressStop;
				stateChanged();
			}
		},5000
		);
	}
	private void PressStop(){
		stop.msgINeedBus(this, waitDest, dest);
	}
	
	private void goToCar(){
		passengerGui.DoGoToCar(car.getX(), car.getY());
		timer.schedule(new TimerTask() {
			public void run() {
				print("DoneWaiting");
				event = AgentEvent.Driving;
				stateChanged();
			}
		},2000
		);
	}
	
	private void GetOnCar(){
		passengerGui.hide();
		car.msgINeedARide(this, carDest);
	}
	
	private void GetOffCar(){
		passengerGui.show(carDest);
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
		this.state = AgentState.DoingNothing;
		this.event = AgentEvent.goToStop;
	}
	public void setCar(CarAgent car) {
		// TODO Auto-generated method stub
		this.car = car;
		this.state = AgentState.NeedCar;
		this.event = AgentEvent.GoingToCar;
	}
}
