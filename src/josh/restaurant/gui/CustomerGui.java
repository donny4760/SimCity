package josh.restaurant.gui;

//import restaurant.HostAgent.WaitingPosition;


import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import josh.restaurant.CustomerAgent;
import josh.restaurant.HostAgent;

public class CustomerGui implements Gui{

	private CustomerAgent agent = null;
	private boolean isPresent = false;
	private boolean isHungry = false;

	RestaurantGui gui;

	private int xPos, yPos;
	private int xDestination, yDestination;
	private enum Command {noCommand, GoToSeat, GoToCashier, LeaveRestaurant};
	private Command command=Command.noCommand;

	public static final int xTable = 100;
	public static final int yTable = 100;
	
	public static final int xCashier = 40;
	public static final int yCashier = 280; 
	
	public static int customerSize = 20; 
	public static int offScreen = -40; 
	public static int spaceBtwnTables = 80;
	public static int startCor = 0; 
	
	public static final int onScreenHomeX = 10;
	public static final int onScreenHomeY = 10;
	public static final int spacebtwn = 30;
	
	public static int walkSpeed = 2;
	private int myTableNum; 
	
	public static List<WaitPosition> waitingPos = new ArrayList<WaitPosition>();
	
	public class WaitPosition {
		CustomerAgent occupiedBy_;
		public int xPos = 10;
		public int yPos = 10; 
		WaitPosition(CustomerAgent c) {
			occupiedBy_ = c;
		}
		void setUnoccupied() {
			occupiedBy_ = null;
		}
		void setOccupant(CustomerAgent cust) {
			this.occupiedBy_ = cust;
		}
		CustomerAgent getOccupant() {
			return occupiedBy_;
		}
		boolean isOccupied() {
			return (occupiedBy_ != null);
		}
	}

	public CustomerGui(CustomerAgent c, RestaurantGui gui){
		agent = c;
		this.gui = gui;
		myTableNum = 0; 
		
		if (waitingPos.size() == 0) {
			waitingPos.add(new WaitPosition(c));
			xDestination = onScreenHomeX;
			yDestination = onScreenHomeY;
		}
		else {
			int freeCount = 1;
			boolean seated = false;
			for (WaitPosition w : waitingPos) {
				if (!w.isOccupied()) {
					w.setOccupant(c);
					xDestination = onScreenHomeX;
					yDestination = onScreenHomeY + (freeCount * spacebtwn);
					seated = true;
					break;
				}
				freeCount += 1;
			}
			if (seated == false) { //if this new position exceed the positions already available 
				waitingPos.add(new WaitPosition(c));
				xDestination = onScreenHomeX;
				yDestination = (onScreenHomeY) + (freeCount * spacebtwn);
			}
			
		}
		
		xPos = startCor;
		yPos = startCor;
	
	}
	
	public int getYPos () {
		return (yPos);
	}

	public void updatePosition() {
		if (xPos < xDestination)
			xPos+=walkSpeed;
		else if (xPos > xDestination)
			xPos-=walkSpeed;

		if (yPos < yDestination)
			yPos+=walkSpeed;
		else if (yPos > yDestination)
			yPos-=walkSpeed;

		//the state changes are important here as they inform customer gui what msg to send back to customer
		if (xPos == xDestination && yPos == yDestination) {
			
			if (command==Command.GoToSeat) // initial approach to seat
				agent.gui_msgAnimationFinishedGoToSeat();
			
			else if (command == Command.GoToCashier) 
				agent.gui_msgAnimationFinishedGoToCashier();
			
			else if (command==Command.LeaveRestaurant) { // leaving restaurant 
				agent.gui_msgAnimationFinishedLeaveRestaurant();
				//System.out.println("about to call gui.setCustomerEnabled(agent);");
				//reset customer so no longer hungry and so cust in list is no longer hungry
				isHungry = false;
				gui.setCustomerEnabled(agent);
			}
			command=Command.noCommand;
		}
	}

	public void draw(Graphics2D g) {
		g.setColor(Color.GREEN);
		g.fillRect(xPos, yPos, customerSize, customerSize);
	}

	public boolean isPresent() {
		return isPresent;
	}
	public void setHungry() {
		isHungry = true;
		agent.gui_msgGotHungry();
		setPresent(true);
	}
	public boolean isHungry() {
		return isHungry;
	}

	public void setPresent(boolean p) {
		isPresent = p;
	}
	
	public void DoGoToCashier() {
		
		xDestination = xCashier;
		yDestination = yCashier; 
		command = Command.GoToCashier; 
			
	}

	//seat customer based on what # table they are assigned, told be waiter
	public void DoGoToSeat(CustomerAgent c, int seatnumber) {
		
		for (WaitPosition w : waitingPos) {
			if (w.getOccupant() == c) {
				w.setUnoccupied();
				break;
			}
		}
		
		myTableNum = seatnumber; 
		xDestination = xTable + ((seatnumber - 1) * spaceBtwnTables);
		yDestination = yTable;
		command = Command.GoToSeat;
		
	}
	
	public void DoExitRestaurant(CustomerAgent c) {
		
		for (WaitPosition w : waitingPos) {
			if (w.getOccupant() == c) {
				w.setUnoccupied();
				break;
			}
		}
		
		xDestination = offScreen;
		yDestination = offScreen;
		command = Command.LeaveRestaurant;
	}
}
