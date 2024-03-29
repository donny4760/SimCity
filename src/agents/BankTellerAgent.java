package agents;

import agent.Agent;
import agents.Role.roles;

import java.sql.DatabaseMetaData;
import java.util.*;
import java.util.concurrent.Semaphore;

import simcity201.gui.Bank;
import simcity201.gui.BankTellerGui;
import simcity201.interfaces.BankCustomer;
import simcity201.interfaces.BankSecurity;
import simcity201.interfaces.BankTeller;
import simcity201.test.mock.EventLog;
import simcity201.test.mock.LoggedEvent;
import tracePanelpackage.AlertLog;
import tracePanelpackage.AlertTag;

public class BankTellerAgent extends Agent implements BankTeller, Worker {

	public EventLog log = new EventLog();
	
	/*		Data		*/
	int timeIn=0;
	boolean isWorking = true;
	public Person self;
	private String name;
	BankTellerGui gui;
	class Service {
		BankCustomer c;
		String customerName;
		String address;
		int ssn;
		Account.AccountType type;
		ServiceState s;
		//Account acc;
		float amount;
		Role role;
		int acc_number;
		
		Service (BankCustomer c, String name, String address, int ssn, Account.AccountType type, ServiceState s) {
			this.c = c;
			this.customerName = name;
			this.address = address;
			this.ssn = ssn;
			this.type = type;
			this.s = s;
		}
		Service (BankCustomer c, float amount, int acc_number, ServiceState s) {
			this.c = c;
			this.amount = amount;
			this.acc_number = acc_number;
			this.s = s;
		}
		Service (BankCustomer c, float amount, Role role, ServiceState s) {
			this.c = c;
			this.amount = amount;
			this.role = role;
			this.s = s;
		}
		Service (BankCustomer c, ServiceState s) {
			this.c = c;
			this.s = s;
		}
		Service (ServiceState s) {
			this.s = s;
		}
	}
	public enum ServiceState {
		greetCustomer, prepareToWork,
		accountCreateRequested, depositRequested, withdrawRequested,
		loanRequested, processing, asked, done
	}
	public List<Service> services
		= Collections.synchronizedList(new ArrayList<Service>());
	
	Bank bank;
	BankDatabase database;	//from bank
	
	class RobberyThreat {
		BankCustomer c;
		ThreatState s;
		
		RobberyThreat(BankCustomer c, ThreatState s) {
			this.c = c;
			this.s = s;
		}
	}
	public enum ThreatState {
		needHelp, calledHelp, secured, robbed
	}
	public List<RobberyThreat> threats = 
			Collections.synchronizedList(new ArrayList<RobberyThreat>());
	BankSecurity security;
	
	Semaphore atDest = new Semaphore(0, true);
	//BankCustomer nowServing = null;
	
	/*		Messages		*/
	
	public void youAreAtWork(Person p) {
		log.add(new LoggedEvent("Received youAreAtWork " + p.getName()));
		self = p;
		services.add(new Service(ServiceState.prepareToWork));
		isWorking=true;
		stateChanged();
	}
	
	public void securityOnDuty(BankSecurity sec) {
		security = sec;
	}
	/*
	 * 
	public void howdy(BankCustomer c) {
		log.add(new LoggedEvent("Received howdy " + c));
		if (isWorking) {
			services.add(new Service(c, ServiceState.greetCustomer));
			//nowServing = c;
		}else {
			c.leave();
		}
		stateChanged();
		
	}*/
	
	public void iNeedAccount(BankCustomer c, String name, String address, int ssn, Account.AccountType type) {
		log.add(new LoggedEvent("Received iNeedAccount " + c.getName()));
		//Service existingRecord = null;
		/*
		synchronized (services) {
		for (Service s : services) {
			if (s.c.equals(c)) {
				existingRecord = s;
				s.s = ServiceState.accountCreateRequested;
			}
		}
		}*/
		//if ( existingRecord == null) {
			services.add(new Service(c, name, address, ssn, type, ServiceState.accountCreateRequested));
		//}
		stateChanged();
	}
	
	public void iWantToDeposit(BankCustomer c, float amount, int acc_number) {
		log.add(new LoggedEvent("Received iWantToDeposit " + c.getName()));
		//Service existingRecord = null;
//		synchronized (services) {
//		for (Service s : services) {
//			if (s.c.equals(c)) {
//				existingRecord = s;
//				s.acc_number = acc_number;
//				s.s = ServiceState.depositRequested;
//			}
//		}
//		}
		//if ( existingRecord == null) {
			services.add(new Service(c, amount, acc_number, ServiceState.depositRequested));
		//}
		stateChanged();
	}
	
	public void iWantToWithdraw(BankCustomer c, float amount, int acc_number) {
		log.add(new LoggedEvent("Received iWantToWithdraw " + c.getName()));
		//Service existingRecord = null;
//		synchronized (services) {
//		for (Service s : services) {
//			if (s.c.equals(c)) {
//				existingRecord = s;
//				s.acc_number = acc_number;
//				s.s = ServiceState.withdrawRequested;
//			}
//		}
//		}
		//if ( existingRecord == null) {
			services.add(new Service(c, amount, acc_number, ServiceState.withdrawRequested));
		//}
		stateChanged();
	}
	
	public void iWantToLoan(BankCustomer c, float amount, Role role) {
		log.add(new LoggedEvent("Received iWantToLoan " + c.getName()));
		//Service existingRecord = null;
//		synchronized (services) {
//		for (Service s : services) {
//			if (s.c.equals(c)) {
//				existingRecord = s;
//				s.s = ServiceState.loanRequested;
//			}
//		}
//		}
		//if ( existingRecord == null) {
			services.add(new Service(c, amount, role, ServiceState.loanRequested));
		//}
		stateChanged();
	}
	
	public void giveMeTheMoney(BankCustomer c) {
		log.add(new LoggedEvent("Received giveMeTheMoney " + c));
//		synchronized (services) {
//		for (Service s : services) {
//			if (s.c.equals(c)) {
//				services.remove(s);
//				break;
//			}
//		}
//		}
		
		threats.add(new RobberyThreat(c, ThreatState.needHelp));
		stateChanged();
	}
	
	public void robberyIsDown(BankCustomer c) {
		log.add(new LoggedEvent("Received robberyIsDown " + c));
		synchronized (threats) {
		for (RobberyThreat t : threats) {
			if (t.s == ThreatState.calledHelp) {
				if (t.c.equals(c)) {
					t.s = ThreatState.secured;
					break;
				}
			}
		}
		}
		stateChanged();
	}
	
	public void giveRobberMoney(BankCustomer c) {
		log.add(new LoggedEvent("Received giveRobberMoney " + c));
		synchronized (threats) {
		for (RobberyThreat t : threats) {
			if (t.s == ThreatState.calledHelp) {
				if (t.c.equals(c)) {
					t.s = ThreatState.robbed;
					break;
				}
			}
		}
		}
		stateChanged();
	}
	
	public void dontCallCop(BankCustomer c) {
		log.add(new LoggedEvent("Received dontCallCop " + c));
		synchronized(services) {
		synchronized(threats) {
			services.clear();
			threats.clear();
			services.add(new Service(c, ServiceState.done));
		}
		}
		stateChanged();
	}
	
	
	public void doneNoThankYou(BankCustomer c) {
		log.add(new LoggedEvent("Received noThankYou " + c));
		//Service existingRecord = null;
		/*synchronized (services) {
		for (Service s : services) {
			if (s.c.equals(c)) {
				existingRecord = s;
				s.s = ServiceState.done;
				break;
			}
		}
		}*/
		//nowServing = c;
		//if ( existingRecord == null) {
			services.add(new Service(c, ServiceState.done));
		//}
		stateChanged();
	}
	
	public void msgAtDestination() {
		atDest.release();
		stateChanged();
	}
	
	/*		Scheduler		*/
	
	public boolean pickAndExecuteAnAction() {
		Service tempService = null;
		RobberyThreat tempThreat = null;
		synchronized (services) {
		for (Service s : services) {
			if (s.s == ServiceState.prepareToWork) {
				//services.remove(s);
				//callNextOnLine();
				tempService = s;break;
				//return true;
			}
		}
		}	if(tempService != null)	{prepareToWork(tempService); return true;}
		
		if (!isWorking && bank.customers.isEmpty() && self != null) {
			leaveBank();
		}
		
		/*
		if (!isWorking && self != null) {
			leaveBank();
		}*/
		
		
		
		synchronized (threats) {
		for (RobberyThreat t : threats) {
			if (t.s == ThreatState.needHelp) {
				//askForHelp(t);
				//return true;
				tempThreat = t; break;
			}
		}
		}	if(tempThreat != null) {askForHelp(tempThreat); return true;}
		
		synchronized (threats) {
		for (RobberyThreat t : threats) {
			if (t.s == ThreatState.secured) {
				//clearThreat(t);
				//return true;
				tempThreat = t; break;
			}
		}
		}	if(tempThreat != null) {clearThreat(tempThreat); return true;}

		synchronized (threats) {
		for (RobberyThreat t : threats) {
			if (t.s == ThreatState.robbed) {
				//clearThreat(t);
				//return true;
				tempThreat = t; break;
			}
		}
		}	if(tempThreat != null) {openTheVault(tempThreat); return true;}

		/*
		 * 
		synchronized (services) {
		for (Service s : services) {
			if (s.s == ServiceState.greetCustomer) {
				//greetings(s);
				//return true;
				tempService = s; break;
			}
		}
		}	if(tempService != null) {greetings(tempService); return true;}*/
		
		
		synchronized (services) {
		for (Service s : services) {
			if (s.s == ServiceState.accountCreateRequested) {
				//createAccount(s);
				//return true;
				tempService = s; break;
			}
		}
		}	if(tempService != null) {createAccount(tempService); return true;}
		
		synchronized (services) {
		for (Service s : services) {
			if (s.s == ServiceState.depositRequested) {
				//processDeposit(s);
				//return true;
				tempService = s; break;
			}
		}
		}	if(tempService != null) {processDeposit(tempService); return true;}
		
		synchronized (services) {
		for (Service s : services) {
			if (s.s == ServiceState.withdrawRequested) {
				//processWithdraw(s);
				//return true;
				tempService = s; break;
			}
		}
		}	if(tempService != null) {processWithdraw(tempService); return true;}
		
		synchronized (services) {
		for (Service s : services) {
			if (s.s == ServiceState.loanRequested) {
				//processLoan(s);
				//return true;
				tempService = s; break;
			}
		}
		}	if(tempService != null) {processLoan(tempService); return true;}
		
		synchronized (services) {
		for (Service s : services) {
			if (s.s == ServiceState.done) {
				//serviceDone(s);
				//return true;
				tempService = s; break;
			}
		}
		}	if(tempService != null) {serviceDone(tempService); return true;}
		
		/*
		synchronized (services) {
		for (Service s : services) {
			if (s.s == ServiceState.doneProcessing) {
				//askForAnythingElse(s);
				//return true;
				tempService = s; break;
			}
		}
		}	if(tempService != null) {askForAnythingElse(tempService); return true;}
		*/
		
		/*
		if (services.isEmpty())
			callNextOnLine();
		*/
		return false;
	}
	
	/*		Action		*/
	
	private void prepareToWork(Service s) {
		services.remove(s);
		gui.DoGoToTellerWindow();
		try{
			atDest.acquire();
		}catch(InterruptedException ie){
			ie.printStackTrace();
		}
		callNextOnLine();
	}
	
	private void askForHelp(RobberyThreat t) {
		t.s = ThreatState.calledHelp;
		security.helpMe(t.c, this);
//		print("EMERGENCY CODE X: KILL THAT ROBBERY!");
		AlertLog.getInstance().logMessage(AlertTag.BANK, this.name, "EMERGENCY, Code Red:RobberyThreat");
		AlertLog.getInstance().logMessage(AlertTag.BANK_TELLER, this.name, "EMERGENCY, Code Red:RobberyThreat");
	}
	private void clearThreat(RobberyThreat t) {
		threats.remove(t);
//		print("My bank never gets robbed :)");
		AlertLog.getInstance().logMessage(AlertTag.BANK, this.name, "My bank never gets robbed :)");
		AlertLog.getInstance().logMessage(AlertTag.BANK_TELLER, this.name, "My bank never gets robbed :)");
		services.clear();
		//nowServing=null;
		callNextOnLine();
	}
	private void openTheVault(RobberyThreat t) {
		threats.remove(t);
//		print("Take the money and leave please");
		AlertLog.getInstance().logMessage(AlertTag.BANK, this.name, "Take the money and leave please");
		AlertLog.getInstance().logMessage(AlertTag.BANK_TELLER, this.name, "Take the money and leave please");
		if (database.budget >= 1000000) {
			database.updateBudget(0-1000000);
			t.c.hereIsTheMoney(1000000);
		}else {
			t.c.hereIsTheMoney(0-database.budget);
			database.updateBudget(0-database.budget);
		}
		//nowServing = null;
	}
	
	/*
	 * 
	private void greetings(Service s) {
		services.remove(s);
		s.c.howMayIHelpYou();
//		print("How may I help you?");
		AlertLog.getInstance().logMessage(AlertTag.BANK, this.name, "How may I help you?");
		AlertLog.getInstance().logMessage(AlertTag.BANK_TELLER, this.name, "How may I help you?");
	}
	*/
	
	private void createAccount(Service s) {
		//print("Creating account");
		s.s = ServiceState.processing;
		List<Account> accounts = database.ssnSearch(s.ssn);
		boolean found = false;
		if (accounts != null) {
		for (Account acc : accounts) {
			if (s.type == acc.getType()) {
//				print("the same type account already exist, unable to make account");
				AlertLog.getInstance().logMessage(AlertTag.BANK, this.name, "the same type account already exist, unable to make account");
				AlertLog.getInstance().logMessage(AlertTag.BANK_TELLER, this.name, "the same type account already exist, unable to make account");
				s.c.unableToMakeAccount("The same type account exists");
				found =true;
				break;
			}
		}
		}
		if (!found) {
			Account acc = new Account(s.customerName, s.address, s.ssn, s.type);
			database.insertAccount(acc);
//			print("here is your account: "+acc.getType().toString() + ", " + acc.getAccountNumber());
			AlertLog.getInstance().logMessage(AlertTag.BANK, this.name, "here is your account: "+acc.getType().toString() + ", " + acc.getAccountNumber());
			AlertLog.getInstance().logMessage(AlertTag.BANK_TELLER, this.name, "here is your account: "+acc.getType().toString() + ", " + acc.getAccountNumber());
			s.c.hereIsYourAccount(acc);
		}
		//s.s = ServiceState.doneProcessing;
		services.remove(s);
	}
	private void processDeposit(Service s) {
		s.s = ServiceState.processing;
		print("\t\t " + s.acc_number);
		Account customerAccount = database.searchAccount(s.acc_number);
		if (customerAccount.getBalance() + s.amount > customerAccount.getDepositLimit()) {
			s.c.depositTransaction(false, "Your account reached a limit");
		}else {
			if(database.hasLoan(s.c.getSelf())) {
				database.loanPayment(s.c.getSelf(), s.amount);
//				print("You have loan with us, so you have made payment for the loan");
				AlertLog.getInstance().logMessage(AlertTag.BANK, this.name, "You have loan with us, so you have made payment for the loan");
				AlertLog.getInstance().logMessage(AlertTag.BANK_TELLER, this.name, "You have loan with us, so you have made payment for the loan");
				log.add(new LoggedEvent("deposit became loan payment"));
			}else {
				customerAccount.deposit(s.amount);
//				print("deposit success");
				AlertLog.getInstance().logMessage(AlertTag.BANK, this.name, "deposit success");
				AlertLog.getInstance().logMessage(AlertTag.BANK_TELLER, this.name, "deposit success");
				log.add(new LoggedEvent("deposit successful"));
			}
			s.c.depositTransaction(true, null);
		}
		database.updateBudget(s.amount);
		//s.s = ServiceState.doneProcessing;
		//print("deposit");
		services.remove(s);
	}
	private void processWithdraw(Service s) {
		s.s = ServiceState.processing;
		Account customerAccount = database.searchAccount(s.acc_number);
		if (customerAccount.getBalance() < s.amount) {
			s.c.withdrawTransaction(false, "You do not have enough money in your account");
			AlertLog.getInstance().logMessage(AlertTag.BANK, this.name, "withdrawal failed, you do not have enough money in your account");
			AlertLog.getInstance().logMessage(AlertTag.BANK_TELLER, this.name, "withdrawal failed, you do not have enough money in your account");
		}else {
			AlertLog.getInstance().logMessage(AlertTag.BANK, this.name, "here is your withdrawed money of $" + s.amount);
			AlertLog.getInstance().logMessage(AlertTag.BANK_TELLER, this.name, "here is your withdrawed money of $" + s.amount);
			customerAccount.withdraw(s.amount);
			s.c.withdrawTransaction(true, null);
		}
		database.updateBudget(0-s.amount);
		//s.s = ServiceState.doneProcessing;
		//print("withdraw");
		services.remove(s);
	}
	private void processLoan(Service s) {
		s.s = ServiceState.processing;
		
		if (s.role != null) { // s.role is guaranteed to be a job role
			// if role is not a owner and s.amount > 10000, then you can't loan
			// else you will get a loan
			if(s.role.getRole() == roles.ApartmentOwner || 
					s.role.getRole() == roles.AptOwner || 
					s.role.getRole() == roles.houseOwner) {
				if (database.updateBudget(0-s.amount)) {
//					print("loan approved");
					AlertLog.getInstance().logMessage(AlertTag.BANK, this.name, "loan approved");
					AlertLog.getInstance().logMessage(AlertTag.BANK_TELLER, this.name, "loan approved");
					database.updateLoan(s.amount, s.c.getSelf());
					s.c.loanDecision(true);
				}else {
					//bank has not enough money to loan
//					print("approved but we got no money for ya");
					AlertLog.getInstance().logMessage(AlertTag.BANK, this.name, "approved but we got no money for ya");
					AlertLog.getInstance().logMessage(AlertTag.BANK_TELLER, this.name, "approved but we got no money for ya");
					s.c.loanDecision(false);
				}
			}else {
				if (s.amount > 10000) {
//					print("not aprroved");
					AlertLog.getInstance().logMessage(AlertTag.BANK, this.name, "not aprroved");
					AlertLog.getInstance().logMessage(AlertTag.BANK_TELLER, this.name, "not aprroved");
					s.c.loanDecision(false);
				}else {
					if (database.updateBudget(0-s.amount)) {
//						print("loan approved");
						AlertLog.getInstance().logMessage(AlertTag.BANK, this.name, "loan approved");
						AlertLog.getInstance().logMessage(AlertTag.BANK_TELLER, this.name, "loan approved");
						database.updateLoan(s.amount, s.c.getSelf());
						s.c.loanDecision(true);
					}else {
						//bank has not enough money to loan
//						print("approved but we got no money for ya");
						AlertLog.getInstance().logMessage(AlertTag.BANK, this.name, "approved but we got no money for ya");
						AlertLog.getInstance().logMessage(AlertTag.BANK_TELLER, this.name, "approved but we got no money for ya");
						s.c.loanDecision(false);
					}
				}
			}
			
		}else {
//			print("you got no job, no loan for ya");
			AlertLog.getInstance().logMessage(AlertTag.BANK, this.name, "you got no job, no loan for ya");
			AlertLog.getInstance().logMessage(AlertTag.BANK_TELLER, this.name, "you got no job, no loan for ya");
			s.c.loanDecision(false);
		}
		//s.s = ServiceState.doneProcessing;
		//print("loan");
		services.remove(s);
	}
	/*
	private void askForAnythingElse(Service s) {
		s.s = ServiceState.asked;
		//s.c.anythingElse();
		//services.clear();
		print("anything else?");
	}
	*/
	private void callNextOnLine() {
		if(!services.isEmpty()) {
			services.clear();
		}
//		services.clear();
//		print("next on line?");
		AlertLog.getInstance().logMessage(AlertTag.BANK, this.name, "next on line?");
		AlertLog.getInstance().logMessage(AlertTag.BANK_TELLER, this.name, "next on line?");
		BankCustomer c = bank.whoIsNextOnLine(this);
		if (c == null) {
			// if not working, it will return null, so teller can leave
			leaveBank();
			return;
		}
//		print("next is " + c.getName());
		AlertLog.getInstance().logMessage(AlertTag.BANK, this.name, "next is " + c.getName());
		AlertLog.getInstance().logMessage(AlertTag.BANK_TELLER, this.name, "next is " + c.getName());
		c.nextOnLine(this);
	}
	private void serviceDone(Service s) {
		services.remove(s);
		services.clear();
//		print("service done");
		AlertLog.getInstance().logMessage(AlertTag.BANK, this.name, "service done");
		AlertLog.getInstance().logMessage(AlertTag.BANK_TELLER, this.name, "service done");
		//nowServing = null;
		if(isWorking) {
			callNextOnLine();
		}else {
			leaveBank();
		}
	}
	private void leaveBank() {
		//if (nowServing!=null) 
		//	return;
		
		this.isWorking = false;
		log.add(new LoggedEvent("leaving bank"));
		AlertLog.getInstance().logMessage(AlertTag.BANK, this.name, "leaving bank");
		AlertLog.getInstance().logMessage(AlertTag.BANK_TELLER, this.name, "leaving bank");
		gui.DoLeaveBank();
		try {
			atDest.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		/*only leave when they are done with customer for now 
		 * (not leaving when waiting for someone to come on line)*/
		bank.leavingWork(this);
		self.msgDone();
		self =null;
	}
	
	
	
	/*		Utilities		*/
	public BankTellerAgent(String name) {
		this.name = name;
	}
	public void setBank(Bank bank) {
		this.bank = bank;
	}
	public String getName(){
		return this.name;
	}
	public void setDB(BankDatabase db) {
		this.database = db;
	}
	public void setGui(BankTellerGui g) {
		this.gui = g;
	}
	public BankTellerGui getGui() {
		return gui;
	}
	@Override
	public Person getSelf() {
		return self;
	}

	@Override
	public void setTimeIn(int timeIn) {
		this.timeIn = timeIn;
	}

	@Override
	public int getTimeIn() {
		return timeIn;
	}

	@Override
	public void goHome() {
		if(isWorking) {
			isWorking = false;
			stateChanged();
		}
	}
	public boolean isWorking() {
		return isWorking;
	}
	public Person getPerson() {
		return self;
	}
	public void setPerson(Person p) {
		this.self = p;
	}
	@Override
	public void msgLeave() {
		// TODO Auto-generated method stub
		
	}

	
	
	
	/* V1 Dump
	 * 
	 * 
	 * 
	 * 
	 * 
	 * ***/
	
	
}
 