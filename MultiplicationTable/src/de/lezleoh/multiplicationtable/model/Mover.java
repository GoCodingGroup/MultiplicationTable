package de.lezleoh.multiplicationtable.model;

import de.lezleoh.multiplicationtable.util.Observable;

public class Mover extends Observable implements Sprite {

	private World world;

	private int xLocation;
	private int yLocation;

	private int width;
	private int height;

	private int xSpeed;
	private int ySpeed;

	public Mover(World world, int width, int height) {
		this.world = world;
		world.addActor(this);

		this.xLocation = world.getWidth() / 2;
		this.yLocation = world.getHeight() / 2;

		this.width = width;
		this.height = height;

		this.xSpeed = 0;
		this.ySpeed = 0;
	}

	@Override
	public void act() {
		move();
		notifyObservers();
	}

	public void move() {
		xLocation = xLocation + xSpeed;
		yLocation = yLocation + ySpeed;
	}

	@Override
	public World getWorld() {
		return world;
	}

	@Override
	public void setXLocation(int xLocation) {
		this.xLocation = xLocation;
	}

	@Override
	public void setYLocation(int yLocation) {
		this.yLocation = yLocation;
	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public void setWidth(int width) {
		this.width = width;
	}

	@Override
	public int getHeight() {
		return height;
	}

	@Override
	public void setHeight(int height) {
		this.height = height;
	}

	@Override
	public int getXSpeed() {
		return xSpeed;
	}

	@Override
	public void setXSpeed(int xSpeed) {
		this.xSpeed = xSpeed;
	}

	@Override
	public int getYSpeed() {
		return ySpeed;
	}

	@Override
	public void setYSpeed(int ySpeed) {
		this.ySpeed = ySpeed;
	}

	@Override
	public int getXLocation() {
		return xLocation;
	}

	@Override
	public int getYLocation() {
		return yLocation;
	}

}
