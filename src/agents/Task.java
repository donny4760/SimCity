package agents;

import java.util.ArrayList;
import java.util.List;

public class Task {
	public enum Objective{goTo, patron, worker, house}; //master refers to home
	private Objective objective;
	private String location;
	public enum specificTask{none, eatAtHome, eatAtApartment, buyGroceries, 
		payBills, sleepAtHome, buyCar, takeBus, takeCar, walk, sleepAtApartment,
		takeOutLoan, depositMoney, openBankAccount, depositGroceries, robBank, takeOutMoney};
	public List<specificTask> sTasks = new ArrayList<specificTask>();
	
	public Task(Objective o, String l)
	{
		objective = o;
		location = l;
	}
	
	public Objective getObjective()
	{
		return objective;
	}
	
	public String getLocation()
	{
		return location;
	}
}
