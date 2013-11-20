package animation;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.*;

import javax.swing.Timer;

import simcity201.gui.ApartmentOwnerGui;
import simcity201.gui.ApartmentRenterGui;

public class ApartmentAnimationPanel extends BaseAnimationPanel implements ActionListener, MouseListener{

	private int windowWidth = 500;
	private int windowHeight = 500;
	List<myApartmentGui> apartmentGuis = new ArrayList<myApartmentGui>();
	List<ApartmentOwnerGui> ownerGuis = new ArrayList<ApartmentOwnerGui>();
	
	int selectedApartment = 0;
	
	private final int DELAY = 8;
	
	public ApartmentAnimationPanel()
	{
		Dimension d = new Dimension(windowWidth, windowHeight);
		this.setPreferredSize(d);
		this.setMinimumSize(d);
		this.setMaximumSize(d);
		this.setVisible(true);
		
		this.getSize();
		
		Timer timer = new Timer(DELAY, this );
    	timer.start();
	}
	
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D)g;
		g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, windowWidth, windowHeight);
		for(myApartmentGui gui: apartmentGuis)
		{
			gui.gui.updatePosition();
		}
		for(ApartmentOwnerGui gui: ownerGuis)
		{
			if(gui.isPresent())
			{
				gui.updatePosition();
			}
		}
		
		for(myApartmentGui gui: apartmentGuis)
		{
			if(gui.apartmentNumber == selectedApartment)
			{
				if(gui.gui.isPresent())
				{
					gui.gui.draw(g2);
				}
			}
		}
		for(ApartmentOwnerGui gui: ownerGuis)
		{
			if(gui.isPresent())
			{
				gui.draw(g2);
			}
		}
	}
	
	public void addGui(ApartmentRenterGui g)
	{
		apartmentGuis.add(new myApartmentGui(g, apartmentGuis.size() + 1));
	}
	
	public void addGui(ApartmentOwnerGui g)
	{
		ownerGuis.add(g);
	}
	
	public void removeGui(ApartmentRenterGui g)
	{
		for(myApartmentGui gui: apartmentGuis)
		{
			if(gui.gui.equals(g))
			{
				apartmentGuis.remove(gui);
				return ;
			}
		}
	}
	
	public void removeGui(ApartmentOwnerGui g)
	{
		ownerGuis.remove(g);
		
	}
	
	public Dimension getSize() {
		return new Dimension(windowWidth, windowHeight);
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		this.repaint();
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	private class myApartmentGui{
		public ApartmentRenterGui gui = null;
		public int apartmentNumber;
		public myApartmentGui(ApartmentRenterGui g, int number)
		{
			gui = g;
			apartmentNumber = number;
		}
	}
}
