package de.gocodinggroup.multiplicationtable.util.events;

import de.gocodinggroup.multiplicationtable.util.*;

/**
 * Event sent when kinect logic senses the player jumping
 * 
 * @author Dominik
 *
 */
public class PlayerJumpedEvent extends Event {
	private int impactLocationX, impactLocationY;

	/**
	 * @return locationX of the jumps impact point
	 */
	public int getImpactLocationX() {
		return impactLocationX;
	}

	/**
	 * @return locationY of the jumps impact point
	 */
	public int getImpactLocationY() {
		return impactLocationY;
	}

	public PlayerJumpedEvent(int locationX, int locationY) {
		this.impactLocationX = locationX;
		this.impactLocationY = locationY;
	}
}
