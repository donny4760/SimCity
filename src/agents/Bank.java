package agents;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Vector;

import javax.swing.*;

import java.awt.*;
import java.util.List;

public class Bank {

	private BankAnimationPanel bap = new BankAnimationPanel();
	
	private Vector<Person> people = new Vector<Person>();
	
	private BankDatabase db = BankDatabase.getDB();
	
	private final static int MAX_LINE = 10;
	private List<BankCustomerAgent> pplOnLine =
			Collections.synchronizedList(new ArrayList<BankCustomerAgent>(MAX_LINE));
	private int line_count = 0;
	
	
	// Temp*****
	
	private JFrame frame = new JFrame("Bank");
	
	// *********
	
	public Bank() {
		
	}
	
	/**
	 * shared data: line
	 * the iAmOnLine will make you to go to SLEEP, doing something else while waiting
	 * @param BankCustomer bc
	 */
	synchronized public void iAmOnLine(BankCustomerAgent bca) {
		while (line_count == MAX_LINE) {
			try {
				System.out.println("\tFull, Waiting");
				wait(5000); 	// Full, wait to add
			}catch(InterruptedException ex) {
			}
		}
		
		pplOnLine.add(bca);
		line_count++;
		if (line_count == 1) {
			System.out.println("\tNot Empty, notify");
			notify();		//notify a waiting bank teller
		}
	}
	synchronized public BankCustomerAgent whoIsNextOnLine() {
		while (line_count == 0) {
			try {
				System.out.println("\tEmpty, waiting");
				wait(5000);
			}catch(InterruptedException ex) {};
		}
		
		BankCustomerAgent bca = pplOnLine.get(0);
		line_count--;
		if (line_count == MAX_LINE-1) {
			System.out.println("\tNot full, notify");
			notify();		//notify customer waiting outside
		}
		return bca;
	}
	
	public static void main(String[] args) {
		Bank bank = new Bank();
		bank.frame.setVisible(true);
		bank.frame.setSize(new Dimension(400,400));
		bank.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		BankCustomerAgent customer = new BankCustomerAgent("Customer");
		BankTellerAgent teller = new BankTellerAgent("Teller");
		customer.setBank(bank);
		teller.setBank(bank);
		teller.setDB(bank.db);
		
		customer.startThread();
		teller.startThread();
		
		customer.youAreInside();
	}
	
}