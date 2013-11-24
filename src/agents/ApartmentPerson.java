package agents;

import java.util.ArrayList;
import java.util.List;

import simcity201.gui.ApartmentPersonGui;
import Buildings.ApartmentComplex;
import Buildings.ApartmentComplex.*;
import agent.Agent;

public class ApartmentPerson extends Agent{
	
	/**
	 * Data
	 */
	
	public Person p;
	List<String> groceries; //this is going to be a part of the person 
	
	boolean evicted = false;
	ApartmentComplex apartmentComplex;
	Apartment apartment;
	
	ApartmentPersonGui gui;
	
	Object renterLock = new Object();
	boolean timeToBill = false;

	//constructor
	public ApartmentPerson(Person agent, ApartmentComplex complex, Apartment a)
	{
		p = agent;
		apartmentComplex = complex;
		apartment = a;
	}
	
	public void setGui(ApartmentPersonGui g)
	{
		gui = g;
	}
	
	public void setApartment(Apartment a)
	{
		apartment = a;
	}
	
	public void doThings()
	{
		if(gui != null)
		{
			gui.personArrived();
		}
		stateChanged();
	}
	
	/**
	 * Messages
	 */
	
	public void msgPleasePayBill(ApartmentBill b)
	{
		//will be changed to p.bill.add(b);
		p.bills.add(b);
	}
	
	public void msgEvicted()
	{
		evicted = true;
	}
	
	/**
	 * Messages specific to the owner
	 */
	
	public void msgCantPay(ApartmentBill b, ApartmentPerson a)
	{
		synchronized(renterLock)
		{
			for(Apartment r: apartmentComplex.apartments)
			{
				if(r.person.equals(a))
				{
					r.strikes++;
				}
			}
		}
	}
	
	/*
	 * TODO: add an action that removes bills, dont put it in 
	 * a message. its okay for now, but its better in an
	 * actions since it wont look so crazy
	 */
	public void msgHereIsMoney(ApartmentBill b, float money, ApartmentPerson a)
	{
		synchronized(renterLock)
		{
			for(Apartment r: apartmentComplex.apartments)
			{
				if(r.person.equals(a))
				{
					synchronized(p.billLock)
					{
						for(ApartmentBill bill: r.bills)
						{
							if(b == bill && b.getBalance() == money)
							{
								r.bills.remove(bill);
								return;
							}
						}
					}
				}
			}
		}
	}
	
	/**
	 * Scheduler
	 */
	
	protected boolean pickAndExecuteAnAction() {
		if(evicted)
		{
			doClearApartment();
			return true;
		}
		if(groceries.size() > 0)
		{
			doStoreGroceries();
			return true;
		}
		if(p.hungerLevel < 20 /* && haveEnoughFood && haveTime*/)
		{
			doCookAndEatFood();
			return true;
		}
		if(p.bills.size() > 0)
		{
			doPayBills();
			return true;
		}
		if(timeToBill)
		{
			for(Role r: p.roles)
			{
				if(r.getRole() == Role.roles.ApartmentOwner)
				{
					doBillPeople();
					return true;
				}
			}
		}
		doLeave();
		return false;
	}

	private void doLeave() {
		gui.personLeft();
		p.msgDone();
	}

	private void doPayBills() {
		for(ApartmentBill b: p.bills)
		{
			if(p.money >= b.getBalance())
			{
				p.money -= b.getBalance();
				b.getOwner().msgHereIsMoney(b, b.getBalance(), this);
			}
			else
			{
				b.getOwner().msgCantPay(b, this);
			}
		}
	}

	private void doCookAndEatFood() {
		//make him move to stove and say what he's cooking.
		//then make him go to table to eat
		//then brings the food to sink
		//then set hunger level to zero
		p.hungerLevel = 0;
	}

	private void doStoreGroceries() {
		//make him move to fridge
		groceries.clear();
	}
	
	private void doBillPeople()
	{
		for(Apartment r: apartmentComplex.apartments)
		{
			r.person.msgPleasePayBill(new ApartmentBill(10.0f, r.person, this));
		}
	}

	private void doClearApartment() {
		for(Role r: p.roles)
		{
			if(r.getRole() == Role.roles.ApartmentRenter)
			{
				p.roles.remove(r);
				break;
			}
		}
	}
}