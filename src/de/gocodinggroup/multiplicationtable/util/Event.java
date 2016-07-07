package de.gocodinggroup.multiplicationtable.util;

/**
 * Every Event sent through the EventManager must be derived from this class
 * 
 * @author Dominik
 *
 */
public abstract class Event {
	private static long globalEventID = 0;
	private long eventID;

	public Event() {
		this.eventID = globalEventID++;
	}

	public long getEventID() {
		return eventID;
	}
}
