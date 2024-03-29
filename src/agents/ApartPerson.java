package agents;

import ApartmentGui.ApartmentPersonGui;
import Buildings.ApartmentComplex.Apartment;

public interface ApartPerson {
	
	public String getName();

	public void setGui(ApartmentPersonGui g);

	public void setApartment(Apartment a);

	public void doThings();

	/**
	 * Messages
	 */

	public void msgPleasePayBill(ApartmentBill b);

	public void msgEvicted();

	/**
	 * Messages specific to the owner
	 */

	public void msgCantPay(ApartmentBill b, ApartPerson a);

	public void msgDoneSleeping();

	/**
	 * Messages from GUI for semaphore releases
	 */

	/*
	 * TODO: add an action that removes bills, dont put it in 
	 * a message. its okay for now, but its better in an
	 * actions since it wont look so crazy
	 */
	public void msgHereIsMoney(ApartmentBill b, float money, ApartPerson a);

}