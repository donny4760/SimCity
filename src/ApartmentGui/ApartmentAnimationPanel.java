package ApartmentGui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.*;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.Timer;

import animation.BaseAnimationPanel;

public class ApartmentAnimationPanel extends BaseAnimationPanel implements ActionListener, MouseListener{

	private int WINDOWX = 500;
	private int WINDOWY = 500;
	
	Object lock = new Object();
	
	List<myApartmentGui> apartmentGuis = new ArrayList<myApartmentGui>();
	
	JButton nextApartment = new JButton();
	JButton previousApartment = new JButton();
	
	int selectedApartment = 0;
	
	private final int DELAY = 8;
	
	public ApartmentAnimationPanel()
	{
	   setLayout(new BorderLayout());
		Dimension d = new Dimension(WINDOWX, WINDOWY);
		this.setPreferredSize(d);
		this.setMinimumSize(d);
		this.setMaximumSize(d);
		this.setVisible(true);
		
		this.getSize();
		
		
		Timer timer = new Timer(DELAY, this );
    	timer.start();
    	
    	nextApartment.setName("Next");
    	nextApartment.setText("Next");
    	previousApartment.setName("Previous");
    	previousApartment.setText("Previous");
    	
    	this.setLayout(new FlowLayout(FlowLayout.CENTER));
    	this.add(nextApartment);
    	this.add(previousApartment);
    	
    	nextApartment.addActionListener(this);
    	previousApartment.addActionListener(this);
    	
    	nextApartment.setEnabled(true);
    	nextApartment.setVisible(true);
    	previousApartment.setEnabled(true);
    	previousApartment.setVisible(true);
	}
	
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D)g;
		ImageIcon myIcon = new ImageIcon(this.getClass().getResource("apartment.jpg"));
      Image img1 = myIcon.getImage();
      g.drawImage(img1, 0, 0, WINDOWX, WINDOWY,  this);
      
		if(apartmentGuis.size() == 0)
		{
			g2.setColor(Color.BLACK);
			g2.fillRect(0, 0, WINDOWX, WINDOWY);
			g2.setColor(Color.white);
			g2.drawString("Empty Apartment Complex", 100, 100);
			return;
		}
		g2.setColor(Color.green);
		g2.drawString("Apartment: " + Integer.toString(selectedApartment + 1), 10, 10);
		
		
		synchronized(lock)
		{
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
		}
	}
	
	public void addGui(ApartmentPersonGui g)
	{
		apartmentGuis.add(new myApartmentGui(g, apartmentGuis.size()));
	}
	
	public void removeGui(ApartmentPersonGui g)
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
	
	public Dimension getSize() {
		return new Dimension(WINDOWX, WINDOWY);
	}

	
	public void actionPerformed(ActionEvent arg0) {
		JButton button = null;
		if(arg0.getSource().getClass().equals(JButton.class))
		{
			button = (JButton)arg0.getSource();
		}
		if(button != null)
		{
			if(button.getName().equals("Next"))
			{
				selectedApartment++;
				if(selectedApartment >= apartmentGuis.size())
				{
					selectedApartment = 0;
				}
			}
			else if(button.getName().equals("Previous"))
			{
				selectedApartment--;
				if(selectedApartment < 0)
				{
					selectedApartment = apartmentGuis.size() - 1;
				}
			}
		}
		synchronized(lock)
		{
			try {
			for(myApartmentGui gui: apartmentGuis)
			{
				gui.gui.updatePosition();
			}
			}catch (ConcurrentModificationException cme) {
			}
		}
		this.repaint();
	}

	
	public void mouseClicked(MouseEvent arg0) {
		
	}

	
	public void mouseEntered(MouseEvent arg0) {
		
	}

	
	public void mouseExited(MouseEvent arg0) {
		
	}

	
	public void mousePressed(MouseEvent arg0) {
		
	}

	
	public void mouseReleased(MouseEvent arg0) {
		
	}
	
	private class myApartmentGui{
		public ApartmentPersonGui gui = null;
		public int apartmentNumber;
		public myApartmentGui(ApartmentPersonGui g, int number)
		{
			gui = g;
			apartmentNumber = number;
		}
	}
}
