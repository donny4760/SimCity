package LYN.gui;


import LYN.CustomerAgent;
import LYN.HostAgent;

import java.awt.*;

public class HostGui implements Gui {

    private HostAgent agent = null;

    private int xPos = -20, yPos = -20;//default waiter position
    private int xDestination = -20, yDestination = -20;//default start position

    public int xTable = 200;
    public static final int yTable = 250;

    public HostGui(HostAgent agent) {
        this.agent = agent;
    }

    public void callpause(){
		agent.pause();
	}
    
    public void callresume(){
    	agent.resume();
    }

    public void updatePosition() {
        if (xPos < xDestination)
            xPos++;
        else if (xPos > xDestination)
            xPos--;

        if (yPos < yDestination)
            yPos++;
        else if (yPos > yDestination)
            yPos--;

        if (xPos == xDestination && yPos == yDestination
        		& (xDestination == xTable + 20) & (yDestination == yTable - 20) ) {
          // agent.msgAtTable();
           
           }
       
        if (xPos == -20 && yPos == -20){        	
        	//agent.msgAtOrigin();
        	//agent.check();
        }
        
    }

    public void draw(Graphics2D g) {
        g.setColor(Color.MAGENTA);
        g.fillRect(xPos, yPos, 20, 20);
    }

    public boolean isPresent() {
        return true;
    }
/*
    public void DoBringToTable(CustomerAgent customer, int a) {
        System.out.print(a);
    	if (a == 1) {
    		xTable = 200;
    	xDestination = xTable + 20;
        yDestination = yTable - 20;}
        else if (a == 2){
        	xTable = 100;
        	xDestination = xTable + 20;
            yDestination = yTable - 20;
        } else {
        	xTable = 300;
        	xDestination = xTable + 20;
            yDestination = yTable - 20;
        }
    }

    public void DoLeaveCustomer() {
        xDestination = -20;
        yDestination = -20;
    }

    public int getXPos() {
        return xPos;
    }

    public int getYPos() {
        return yPos;
    }
    */
}
