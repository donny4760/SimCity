package simcity201.gui;


import agents.BusAgent;
import agents.PassengerAgent;
import agents.CarAgent;
import java.awt.*;

import javax.swing.ImageIcon;

public class CarGui implements Gui {

    private CarAgent agent = null;

    public int xPos = 650, yPos = 650;//default bus position
    private int xDestination = 650, yDestination = 650;//default bus position
    
    private String buspic = "bus.png";
	private Image img;
    
    
    public static final int xBank = 300;
    public static final int yBank = 40;
    
    public static final int xMarket = 570;
    public static final int yMarket = 200;
    
    public static final int xHouse = 850;
    public static final int yHouse = 0;
    
    public static final int xRest1 = 705;
    public static final int yRest1 = 325;
    
    public static final int xRest2 = 705;
    public static final int yRest2 = 475;
    
    public static final int xRest3 = 855;
    public static final int yRest3 = 325;
    
    public static final int xRest4 = 855;
    public static final int yRest4 = 475;
    
    public static final int xRest5 = 1005;
    public static final int yRest5 = 325;
    
    public static final int xRest6 = 1005;
    public static final int yRest6 = 475;
    
    public static final int xHouse1 = 695;
    public static final int yHouse1 = 130;
    
    public static final int xHouse2 = 845;
    public static final int yHouse2 = 130;
    
    public static final int xHouse3 = 995;
    public static final int yHouse3 = 130;
    
    public static final int xApart = 0;
    public static final int yApart = 0;
    
    public CarGui(CarAgent agent) {
        this.agent = agent;
        ImageIcon customer = new ImageIcon(this.getClass().getResource(buspic));
		img = customer.getImage();
        
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
        		& (xDestination == xBank) & (yDestination == yBank)) {
           agent.msgAtDest();
           agent.msgAtBank();
        }
        if (xPos == xDestination && yPos == yDestination
        		& (xDestination == xMarket) & (yDestination == yMarket)) {
           agent.msgAtDest();
           agent.msgAtMarket();
        }
        if (xPos == xDestination && yPos == yDestination
        		& (xDestination == xHouse) & (yDestination == yHouse)) {
           agent.msgAtDest();
           agent.msgAtHouse();
        }
        if (xPos == xDestination && yPos == yDestination
        		& (xDestination == xRest1) & (yDestination == yRest1)) {
           agent.msgAtDest();
           agent.msgAtRest1();
        }
        if (xPos == xDestination && yPos == yDestination
        		& (xDestination == xRest2) & (yDestination == yRest2)) {
           agent.msgAtDest();
           agent.msgAtRest2();
        }
        if (xPos == xDestination && yPos == yDestination
        		& (xDestination == xRest3) & (yDestination == yRest3)) {
           agent.msgAtDest();
           agent.msgAtRest3();
        }
        if (xPos == xDestination && yPos == yDestination
        		& (xDestination == xRest4) & (yDestination == yRest4)) {
           agent.msgAtDest();
           agent.msgAtRest4();
        }
        if (xPos == xDestination && yPos == yDestination
        		& (xDestination == xRest5) & (yDestination == yRest5)) {
           agent.msgAtDest();
           agent.msgAtRest5();
        }
        if (xPos == xDestination && yPos == yDestination
        		& (xDestination == xRest6) & (yDestination == yRest6)) {
           agent.msgAtDest();
           agent.msgAtRest6();
        }
       

    }

    public void draw(Graphics2D g) {
    	g.drawImage(img,xPos,yPos,null);
    }

    public boolean isPresent() {
        return true;
    }
    
    public void DoGoToPark(String dest){
    	if(dest == "Bank"){
            xDestination = xBank +30;
            yDestination = yBank +30;}
        	if(dest == "Market"){
                xDestination = xMarket +30;
                yDestination = yMarket +30;}
        	if(dest == "House"){
                xDestination = xHouse +30;
                yDestination = yHouse +30;}
        	
    	if(dest == "Rest1"){
            xDestination = xRest1 +30;
            yDestination = yRest1 +30;}
    	if(dest == "Rest2"){
            xDestination = xRest2 +30;
            yDestination = yRest2 +30;}
    	if(dest == "Rest3"){
            xDestination = xRest3 +30;
            yDestination = yRest3 +30;}
    	if(dest == "Rest4"){
            xDestination = xRest4 +30;
            yDestination = yRest4 +30;}
    	if(dest == "Rest5"){
            xDestination = xRest5 +30;
            yDestination = yRest5 +30;}
    	if(dest == "Rest6"){
            xDestination = xRest6 +30;
            yDestination = yRest6 +30;}
    	if(dest == "House1"){
            xDestination = xHouse1 +30;
            yDestination = yHouse1 +30;
    	}
    	if(dest == "House2"){
            xDestination = xHouse2 +30;
            yDestination = yHouse2 +30;
    	}
    	if(dest == "House3"){
            xDestination = xHouse3 +30;
            yDestination = yHouse3 +30;}
    	if(dest == "Apart"){
            xDestination = xApart +30;
            yDestination = yApart +30;}
    			
    }
    public void DoGoTo(String dest) {
    	if(dest == "Bank"){
            xDestination = xBank;
            yDestination = yBank;}
        	if(dest == "Market"){
                xDestination = xMarket;
                yDestination = yMarket;}
        	if(dest == "House"){
                xDestination = xHouse;
                yDestination = yHouse;}
        	
    	if(dest == "Rest1"){
            xDestination = xRest1;
            yDestination = yRest1;}
    	if(dest == "Rest2"){
            xDestination = xRest2;
            yDestination = yRest2;}
    	if(dest == "Rest3"){
            xDestination = xRest3;
            yDestination = yRest3;}
    	if(dest == "Rest4"){
            xDestination = xRest4;
            yDestination = yRest4;}
    	if(dest == "Rest5"){
            xDestination = xRest5;
            yDestination = yRest5;}
    	if(dest == "Rest6"){
            xDestination = xRest6;
            yDestination = yRest6;}
    	if(dest == "House1"){
            xDestination = xHouse1;
            yDestination = yHouse1;
    	}
    	if(dest == "House2"){
            xDestination = xHouse2;
            yDestination = yHouse2;
    	}
    	if(dest == "House3"){
            xDestination = xHouse3;
            yDestination = yHouse3;}
    	if(dest == "Apart"){
            xDestination = xApart;
            yDestination = yApart;}
    	
    	
    }
    
   
    
    public int getXPos() {
        return xPos;
    }

    public int getYPos() {
        return yPos;
    }
}