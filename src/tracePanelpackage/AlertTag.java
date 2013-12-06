package tracePanelpackage;


/**
 * These enums represent tags that group alerts together.  <br><br>
 * 
 * This is a separate idea from the {@link AlertLevel}.
 * A tag would group all messages from a similar source.  Examples could be: BANK_TELLER, RESTAURANT_ONE_WAITER,
 * or PERSON.  This way, the trace panel can sort through and identify all of the alerts generated in a specific group.
 * The trace panel then uses this information to decide what to display, which can be toggled.  You could have all of
 * the bank tellers be tagged as a "BANK_TELLER" group so you could turn messages from tellers on and off.
 * 
 * @author Keith DeRuiter
 *
 */
public enum AlertTag {
	PERSON,
	BANK_TELLER,
	BANK_CUSTOMER,
	BANK_Security,
	ApartmentPerson,
	HousePerson,
	StopAgent,
	TruckAgent,
	BusAgent,
	CarAgent,
	BankATM,
	PassengerAgent,
	MarketCashier,
	MarketCustomer,
	MarketDealer,
	MarketRestaurantHandler,
	LYNCustomer,
	LYNCook,
	LYNCashier,
	LYNWaiter,
	LYNhost,
	JoshCustomer,
	JoshCook,
	JoshCashier,
	JoshWaiter,
	Joshhost,
	RyanCustomer,
	RyanCook,
	RyanCashier,
	RyanWaiter,
	Ryanhost,
	DavidCustomer,
	DavidCook,
	DavidWaiter,
	Davidhost,
	DavidCashier,
	EricCook,
	EricCustomer,
	EricWaiter,
	EricCashier,
	Erichost,
	RossCustomer,
	RossCook,
	RossWaiter,
	RossCashier,
	Rosshost
	
}
