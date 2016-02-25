package de.lezleoh.multiplicationtable.model;

import java.util.ArrayList;

public class World {

	private int width;
	private int height;
	
	/**
	 * The duration of a frame in Milliseconds  
	 */
	public static final int FRAME_DURATION = 1000; 

	private ArrayList<Sprite> sprites;

	public World(int width, int height) {
		super();
		this.width = width;
		this.height = height;
		sprites = new ArrayList<Sprite>();
	}

	public void act() {
		for (Sprite sprite : sprites) {
			sprite.act();
		}
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public void addActor(Sprite sprite) {
		sprites.add(sprite);
	}

	public void removeActor(Sprite sprite) {
		sprites.remove(sprite);
	}

}
