package de.gocodinggroup.multiplicationtable.game.model;

import de.gocodinggroup.multiplicationtable.util.*;
import de.gocodinggroup.multiplicationtable.util.events.*;
import javafx.scene.*;

/**
 * Each entity in the game's world has to be derived from this class
 * 
 * @author Dominik
 *
 */
public abstract class GameEntity {
	protected int locationX, locationY;
	protected int width, height;
	protected int speedX, speedY;

	protected Node fxRepresentation;

	public GameEntity(int locationX, int locationY, int width, int height) {
		this.locationX = locationX;
		this.locationY = locationY;
		this.width = width;
		this.height = height;
		this.speedX = 0;
		this.speedY = 0;

		// Register this entity for update event
		EventManager.registerEventListenerForEvent(MoveEvent.class, (event) -> move(((MoveEvent) event).getNow()));
	}

	protected void move(long now) {
		this.locationX += this.speedX;
		this.locationY += this.speedY;
	}

	public Node getFXRepresentation() {
		return this.fxRepresentation;
	}

	/* Getters and Setters */

	public int getLocationX() {
		return locationX;
	}

	public void setLocationX(int locationX) {
		this.locationX = locationX;
	}

	public int getLocationY() {
		return locationY;
	}

	public void setLocationY(int locationY) {
		this.locationY = locationY;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getSpeedX() {
		return speedX;
	}

	public void setSpeedX(int speedX) {
		this.speedX = speedX;
	}

	public int getSpeedY() {
		return speedY;
	}

	public void setSpeedY(int speedY) {
		this.speedY = speedY;
	}

	/* Convenience methods */

	public void setSpeed(int speedX, int speedY) {
		this.speedX = speedX;
		this.speedY = speedY;
	}

	public void setLocation(int locationX, int locationY) {
		this.locationX = locationX;
		this.locationY = locationY;
	}

	public void setDimensions(int width, int height) {
		this.width = width;
		this.height = height;
	}
}
