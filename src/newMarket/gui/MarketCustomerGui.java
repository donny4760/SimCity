package newMarket.gui;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import simcity201.gui.Gui;
import newMarket.*;

public class MarketCustomerGui implements Gui {

	private MarketCustomerAgent agent = null;
	MarketAnimationPanel animationPanel = null;
	Line myLine = null;
	
	public void setAnimationPanel(MarketAnimationPanel m) {
		animationPanel = m;
	}
	
	private boolean isPresent = true;
	private int xPos, yPos;
	private int xDest, yDest, xFinalDest, yFinalDest;
	
	private enum Command {noCommand, GoToEmployee, LeaveMarket, waitInLine, waitForDealer};
	private Command command=Command.noCommand;

	public static int customerSize = 20; 
	public static int offScreen = -40; 
	public static int startCor = -20; 
	public static int walkSpeed = 2;
	
	public final int onScreenHomeX = 10;
	public final int onScreenHomeY = 10;
	public final int spacebtwn = 30;
	
	private boolean wantCar = false;
	
	//wait position list is static and shared with all market customer guis
	//public static List<WaitPosition> waitingPos = new ArrayList<WaitPosition>();
	/*
	public class WaitPosition {
		MarketCustomerAgent occupiedBy_;
		public int xPos;
		public int yPos; 
		WaitPosition(MarketCustomerAgent c, int xPos, int yPos) {
			occupiedBy_ = c;
			this.xPos = xPos;
			this.yPos = yPos; 
		}
		public void setUnoccupied() {
			occupiedBy_ = null;
		}
		public void setOccupant(MarketCustomerAgent cust) {
			this.occupiedBy_ = cust;
		}
		public MarketCustomerAgent getOccupant() {
			return occupiedBy_;
		}
		public boolean isOccupied() {
			return (occupiedBy_ != null);
		}
	}
	*/

	public MarketCustomerGui(MarketCustomerAgent c){
		agent = c;
		
		/*
		//below block to for determining where people wait.
		if (waitingPos.size() == 0) {
			waitingPos.add(new WaitPosition(c, 10, 10));
			//xDest = onScreenHomeX;
			//yDest = onScreenHomeY;
		}
		else {
			int freeCount = 1;
			boolean seated = false;
			for (WaitPosition w : waitingPos) {
				if (!w.isOccupied()) {
					w.setOccupant(c);
					//xDest = onScreenHomeX;
					//yDest = onScreenHomeY + (freeCount * spacebtwn);
					seated = true;
					break;
				}
				freeCount += 1;
			}
			if (seated == false) { //if this new position exceed the positions already available 
				waitingPos.add(new WaitPosition(c, 10, 10));
				//xDest = onScreenHomeX;
				//yDest = (onScreenHomeY) + (freeCount * spacebtwn);
			}
		}
		*/
		//**************************************
		
		xDest = startCor;
		yDest = startCor;
		
		xPos = startCor;
		yPos = startCor;
	}
	
	public int getYPos () {
		return (yPos);
	}
	
	public int getXPos () {
		return (xPos);
	}

	public void updatePosition() {
		
		//move X direction
		if (xPos < xDest)
			xPos+=walkSpeed;
		else if (xPos > xDest)
			xPos-=walkSpeed;

		//move Y direction
		if (yPos < yDest)
			yPos+=walkSpeed;
		else if (yPos > yDest)
			yPos-=walkSpeed;

		//the state changes are important here as they inform customer gui what msg to send back to customer
		if (xPos == xDest && yPos == yDest) {
			
			if(command == Command.GoToEmployee) {
				agent.gui_msgAtEmployee();
			}
			else if (command == Command.LeaveMarket) {
				agent.gui_msgOffScreen();
				setPresent(false);
			}
			else if (command == Command.waitInLine) {
				if (xPos == xFinalDest && yPos == yFinalDest) {
					//myLine.exitLine(this);
					agent.gui_msgAtEmployee();
				}
				else {
					//do nothing 
					return;
				}
			}
			else if (command == Command.waitForDealer) {
				if (xPos == xFinalDest && yPos == yFinalDest) {
					agent.gui_msgAtEmployee();
				}
				else {
					//do nothing
					return;
				}
			}
			command = Command.noCommand;
		}
	}

	public void draw(Graphics2D g) {
		if (wantCar) 
			g.setColor(Color.GRAY);
		else
			g.setColor(Color.GREEN);
		g.fillRect(xPos, yPos, customerSize, customerSize);
	}
	
	public void DoExitMarket(MarketCustomerAgent c) {
		
		System.out.println("do exit market called");
		
		if (c==null) {
			c =  agent;
		}
		
		/*
		for (WaitPosition w : waitingPos) {
			if (w.getOccupant() == c) {
				w.setUnoccupied();
				break;
			}
		}
		*/
		
		xDest = offScreen;
		yDest = offScreen;
		command = Command.LeaveMarket;
	}

	@Override
	public boolean isPresent() {
		return isPresent;
	}
	
	public void setPresent(boolean p) {
		isPresent = p;
	}
	
	public void DoWaitForDealer(MarketDealerAgent targetDealer) {
		System.out.println("customer gui doWaitForDealer");
		command = Command.waitForDealer;
		wantCar = true;
		
		xFinalDest = targetDealer.gui.getXHome() - 20;
		yFinalDest = targetDealer.gui.getYHome();
		
		myLine = targetDealer.getLine();
		
		myLine.waitForDealer(this);
		
	}

	public void DoWaitInLine(MarketCashierAgent targetCashier) {
		System.out.println("customer gui doWaitInLine");
		command = Command.waitInLine;
		
		//this is the final location that we will wait in line to get to
		xFinalDest = targetCashier.gui.getXHome() - 20;
		yFinalDest = targetCashier.gui.getYHome();
		
		System.out.println("FinalDest: " + xFinalDest +" "+ yFinalDest);
		
		//myLine is the line of the cashier I am waiting in right now...
		myLine = targetCashier.getLine();
		
		myLine.waitInLine(this);	
	}

	public void DoWalkDownLine() {
		this.yDest += 30;	
	}
	public void setXDest(int xDest) {
		this.xDest = xDest;
	}
	public void setYDest(int yDest) {
		this.yDest = yDest;
	}
}
