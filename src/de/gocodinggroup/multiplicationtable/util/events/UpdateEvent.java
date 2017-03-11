package de.gocodinggroup.multiplicationtable.util.events;

import de.gocodinggroup.util.*;

/**
 * Event sent to notify receivers that the game logic should be updated now
 * 
 * @author Dominik
 *
 */
public class UpdateEvent extends Event {
	private long now;

	public UpdateEvent(long now) {
		this.now = now;
	}

	/**
	 * @return current time according to animationTimer
	 */
	public long getNow() {
		return now;
	}
}
