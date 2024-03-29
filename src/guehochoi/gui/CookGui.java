package guehochoi.gui;

import guehochoi.gui.WaiterGui.Food;
import guehochoi.gui.WaiterGui.FoodState;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

import javax.swing.ImageIcon;

import guehochoi.restaurant.CookAgent;
import guehochoi.interfaces.Cook;

public class CookGui implements Gui {

	/* CookGui mostly communicates with Cook agent and the kitchen
	 * waiter might have access to kitchen since waiter should take the plate 
	 * from the kitchen table. */
	
	private final static int COOK_SIZE_X = 20;
	private final static int COOK_SIZE_Y = 20;
	
	private CookAgent agent;
	private KitchenGui kitchenGui; 
	
	private List<Destination> destinations =
			new ArrayList<Destination>();
	
	/* goToDest, goToCookingStation, goToPlatingArea will release the semaphore on agent */
    private enum Command {noCommand, goToRef, goToCookingStation, goToDest, goGetCookedFood, goToPlatingArea, goHome};
    private Command command = Command.noCommand;
    
    private final static int SIZE_COOK_X = 20;
    private final static int SIZE_COOK_Y = 20;
    

    private int xPos = AnimationPanel.WINDOWX, yPos = AnimationPanel.WINDOWY;
    private int xDestination = AnimationPanel.WINDOWX, yDestination = AnimationPanel.WINDOWY;
    
    class Destination {
    	Point p;
    	Command c;
    	public Destination(Point p, Command c) {
    		this.p = p;
    		this.c = c;
    	}
    }
    
    private List<Food> foods =
    		Collections.synchronizedList(new ArrayList<Food>());
    
    public class Food {
    	String type;
    	FoodState s;
    	
    	Food(String type, FoodState s) {
    		this.type = type;
    		this.s = s;
    	}
    }
    public enum FoodState {
    	ordered, takingToCookStation, beingCooked, doneCooking, served
    }
    
    private Food currentFood;
    
    private String imagedir = "/guehochoi/gui/";
    private String imageFileName = "Ryan_Cook.png";
    BufferedImage icon;
    
	public CookGui(CookAgent agent, KitchenGui kitchenGui) {
		this.kitchenGui = kitchenGui;
		this.agent = agent;
		
		String imageCaption = "Waiter:" +agent.getName();
    	ImageIcon temp = createImageIcon(imagedir + imageFileName, imageCaption);
    	icon = getScaledImage(temp.getImage(), SIZE_COOK_X, SIZE_COOK_Y);
		
		moveToHome();
	}
	
	protected ImageIcon createImageIcon(String path, String description) {
    	java.net.URL imgURL = getClass().getResource(path);
    	//System.out.println(getClass().getResource(path));
    	if(imgURL != null) {
    		return new ImageIcon(imgURL, description);
    	}else {
    		// could not find file
    		//System.out.println("\n\n\nCANNOT FIND THE IMAGE\n\n\n");
    		return null;
    	}
    }
    private BufferedImage getScaledImage(Image srcImg, int w, int h){
        BufferedImage resizedImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = resizedImg.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(srcImg, 0, 0, w, h, null);
        g2.dispose();
        return resizedImg;
    }
    
	
	
	@Override
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
        	if (command == Command.goHome) {
        		//agent.msgAtDestination();
        		command = Command.noCommand;
        	}
        	if (command == Command.goToDest) {
        		agent.msgAtDestination();
        		command = Command.noCommand;
        	}
        	if (command == Command.goToPlatingArea) {
        		synchronized ( foods ) {
        		for (Food f: foods) {
        			if (f.s == FoodState.doneCooking) {
        				f.s = FoodState.served;
        				kitchenGui.putFoodOnPlatingArea(f.type);
        			}
        		}//foods
        		}//sync
        		agent.msgAtDestination();
        		command = Command.noCommand;
        	}
        	if (command == Command.goGetCookedFood) {
        		currentFood.s = FoodState.doneCooking;
        		kitchenGui.takeFoodFromCookingArea(currentFood);
        	}
        	if (command == Command.goToCookingStation) {
        		synchronized ( foods ) {
        		for (Food f: foods) {
        			if (f.s == FoodState.takingToCookStation) {
        				f.s = FoodState.beingCooked;
        				kitchenGui.putFoodInCookingArea(f);
        			}
        		}//foods
        		}//sync
        		agent.msgAtDestination();
        		command = Command.noCommand;
        	}
        	if (command == Command.goToRef) {
        		synchronized ( foods ) {
        		for (Food f: foods) {
        			if (f.s == FoodState.ordered) {
        				f.s = FoodState.takingToCookStation;
        			}
        		}//foods
        		}//sync
        		command = Command.noCommand;
        	}
        	
        	
        	if (!destinations.isEmpty()) {
         	   Destination dest = destinations.remove(0);
         	   xDestination = (int)dest.p.getX();
         	   yDestination = (int)dest.p.getY();
         	   command = dest.c;
            }
        }
	}

	@Override
	public void draw(Graphics2D g) {
		g.setColor(Color.BLACK);
		//g.fillRect(xPos, yPos, SIZE_COOK_X, SIZE_COOK_Y);
		g.drawImage(icon, xPos, yPos, null);
		
		synchronized ( foods ) {
		for (Food f : foods) {
			if (f.s == FoodState.takingToCookStation) {
				g.setColor(Color.cyan);
				g.drawString(f.type, xPos, yPos+30);
			}
			/*
			if (f.s == FoodState.beingCooked) {
				g.setColor(Color.cyan);
				g.drawString(f.type, (int)kitchenGui.getCookingX(), (int)kitchenGui.getCookingY()+30);
			}*/
			if (f.s == FoodState.doneCooking) {
				g.setColor(Color.cyan);
				g.drawString(f.type, xPos, yPos+30);
			}
		}//foods
        }//sync
	}

	@Override
	public boolean isPresent() {
		// TODO Auto-generated method stub
		return true;
	}
	
	public void DoCooking(String choice) {
		foods.add(new Food(choice, FoodState.ordered));
		destinations.add(new Destination(new Point( kitchenGui.getRefX(), kitchenGui.getRefY()), Command.goToRef));
		destinations.add(new Destination(new Point( kitchenGui.getCookingX(), kitchenGui.getCookingY()), Command.goToCookingStation));
	}
	public void moveToHome() {
		destinations.add(new Destination(new Point( kitchenGui.getHomeX(), kitchenGui.getHomeY()), Command.goHome));
	}
	public void DoPlating(String choice) {
		destinations.add(new Destination(new Point( kitchenGui.getCookingX(), kitchenGui.getCookingY()), Command.goGetCookedFood));
		synchronized ( foods ) {
		for (Food f: foods) {
			if (f.type.equalsIgnoreCase(choice)) {
				if (f.s == FoodState.beingCooked) {
					currentFood = f;
					//f.s = FoodState.doneCooking;
					break;
				}
			}
		}//foods
		}//sync
		destinations.add(new Destination(new Point( kitchenGui.getPlatingX(), kitchenGui.getPlatingY()), Command.goToPlatingArea));
	}

	//TODO: Animate the Cook getting food from the "refrigerator", putting it on the cooking area 
	// 			and moving it to the plating area when it is finished
}
