package simcity201.gui;

import agents.PassengerAgent;

import java.awt.*;

import javax.swing.ImageIcon;

public class PassengerGui implements Gui{

	private PassengerAgent agent = null;
	private boolean isPresent = true;
	private boolean isHungry = false;
	private boolean isOnbus = false;
	//private HostAgent host

	private int xPos, yPos;
	private int xDestination, yDestination;
	private enum Command {noCommand, GoToSeat, LeaveRestaurant};
	private Command command=Command.noCommand;
	
	private String passengerpic = "passenger.png";
	private Image img;
	
	public int xCar;
	public int yCar;
	
	public static final int xBank = 300;
	public static final int yBank = 40;
	    
	public static final int xMarket = 570;
	public static final int yMarket = 200;
	    
	public static final int xHouse = 850;
	public static final int yHouse = 0;
	    
	public static final int xRestaurants1 = 855;
	public static final int yRestaurants1 = 445;
	    
	public static final int xRestaurants2 = 1165;
	public static final int yRestaurants2 = 250;
	    
    
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

	public PassengerGui(PassengerAgent c){ //HostAgent m) {
		agent = c;
		xPos = 0;
		yPos = 0;
		xDestination = 0;
		yDestination = 0;
		ImageIcon customer = new ImageIcon(this.getClass().getResource(passengerpic));
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

		if (xPos == xDestination && yPos == yDestination) {
			
			command=Command.noCommand;
		}
		 if (xPos == xDestination && yPos == yDestination
	        		& (xDestination == xCar) & (yDestination == yCar)) {
	          agent.msgAtCar();
	        }
	}

	public void draw(Graphics2D g) {
		if(isOnbus == false){
			g.drawImage(img,xPos,yPos,null);}
		//g.setColor(Color.GREEN);
		//g.fillRect(xPos, yPos, 20, 20);}
	}

	public boolean isPresent() {
		return isPresent;
	}
	
	public boolean isHungry() {
		return isHungry;
	}

	public void setPresent(boolean p) {
		isPresent = p;
	}

	public void DoGoToCar(int x, int y){
		xDestination = x;
		yDestination = y;
		this.xCar = x;
		this.yCar = y;
	}
	public void DoGoToStop(String dest){
		if(dest == "Bank"){
	        xDestination = xBank;
	        yDestination = yBank;}
	    	if(dest == "Market"){
	            xDestination = xMarket;
	            yDestination = yMarket;}
	    	if(dest == "House"){
	            xDestination = xHouse;
	            yDestination = yHouse;}
	    	if(dest == "Restaurants1"){
	            xDestination = xRestaurants1;
	            yDestination = yRestaurants1;}
	    	if(dest == "Restaurants2"){
	            xDestination = xRestaurants2;
	            yDestination = yRestaurants2;}
	    	
	}
	public void DoWalkTo(String dest){
		
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
	            yDestination = yHouse3;
	    	}
	    	if(dest == "Apart"){
	            xDestination = xApart;
	            yDestination = yApart;}
	}
	public void hide() {
		// TODO Auto-generated method stub
		isOnbus = true;
	}

	public void show(String dest) {
		// TODO Auto-generated method stub
		isOnbus = false;
		if(dest == "Bank"){
	        xDestination = xBank;
	        yDestination = yBank;
	        xPos = xBank;
            yPos = yBank;
			}
	    	if(dest == "Market"){
	            xDestination = xMarket;
	            yDestination = yMarket;
	            xPos = xMarket;
	            yPos = yMarket;
	            }
	    	if(dest == "House"){
	            xDestination = xHouse;
	            yDestination = yHouse;
	            xPos = xHouse;
	            yPos = yHouse;
	            }
	    	if(dest == "Restaurants1"){
	            xDestination = xRestaurants1;
	            yDestination = yRestaurants1;
	            xPos = xRestaurants1;
	            yPos = yRestaurants1;
	            }
	    	if(dest == "Restaurants2"){
	            xDestination = xRestaurants2;
	            yDestination = yRestaurants2;
	            xPos = xRestaurants2;
	            yPos = yRestaurants2;
	            }
	    	if(dest == "Rest1"){
	            xDestination = xRest1;
	            yDestination = yRest1;
	            xPos = xRest1;
	            yPos = yRest1;
	            }
	    	if(dest == "Rest2"){
	            xDestination = xRest2;
	            yDestination = yRest2;
	            xPos = xRest2;
	            yPos = yRest2;
	            }
	    	if(dest == "Rest3"){
	            xDestination = xRest3;
	            yDestination = yRest3;
	            xPos = xRest3;
	            yPos = yRest3;
	            }
	    	if(dest == "Rest4"){
	            xDestination = xRest4;
	            yDestination = yRest4;
	            xPos = xRest4;
	            yPos = yRest4;
	            }
	    	if(dest == "Rest5"){
	            xDestination = xRest5;
	            yDestination = yRest5;
	            xPos = xRest5;
	            yPos = yRest5;
	            }
	    	if(dest == "Rest6"){
	            xDestination = xRest6;
	            yDestination = yRest6;
	            xPos = xRest6;
	            yPos = yRest6;
	    	}
	    	if(dest == "House1"){
	            xDestination = xHouse1;
	            yDestination = yHouse1;
	            xPos = xHouse1;
	            yPos = yHouse1;
	    	}
	    	if(dest == "House2"){
	            xDestination = xHouse2;
	            yDestination = yHouse2;
	            xPos = xHouse2;
	            yPos = yHouse2;
	    	}
	    	if(dest == "House3"){
	            xDestination = xHouse3;
	            yDestination = yHouse3;
	            xPos = xHouse3;
	            yPos = yHouse3;
	    	}
	    	if(dest == "Apart"){
	            xDestination = xApart;
	            yDestination = yApart;
	            xPos = xApart;
	            yPos = yApart;
	            
	    	}

	}
}