package agents;

public interface Worker {
	public void setTimeIn(int timeIn);
	public int getTimeIn();
	public void goHome();
	public Person getPerson();
	public void msgLeave();

}
