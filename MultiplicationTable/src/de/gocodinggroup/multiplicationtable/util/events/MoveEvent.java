package de.gocodinggroup.multiplicationtable.util.events;

/**
 * Event sent to notify all entities that they should move now
 * 
 * @author Dominik
 *
 */
public class MoveEvent extends UpdateEvent {
	public MoveEvent(long now) {
		super(now);
	}
}
