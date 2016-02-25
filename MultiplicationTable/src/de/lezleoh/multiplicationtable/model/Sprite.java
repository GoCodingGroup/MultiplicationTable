package de.lezleoh.multiplicationtable.model;

import de.lezleoh.multiplicationtable.util.Observer;

public interface Sprite {

	void act();

	World getWorld();

	int getXLocation();

	void setXLocation(int xLocation);

	int getYLocation();

	void setYLocation(int yLocation);

	int getWidth();

	void setWidth(int width);

	int getHeight();

	void setHeight(int height);

	int getXSpeed();

	void setXSpeed(int xSpeed);

	int getYSpeed();

	void setYSpeed(int ySpeed);

	void addObserver(Observer observer);

	void removeObserver(Observer observer);

	void notifyObservers();
}
