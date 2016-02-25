package de.lezleoh.multiplicationtable.model;

public class Reflector extends Mover {

	public Reflector(World world, int width, int height) {
		super(world, width, height);
	}

	@Override
	public void act() {
		move();
		reflectIfNecessary();
		notifyObservers();
	}

	private void reflectIfNecessary() {
		if (touchesNorthBorder()) {
			setYSpeed(-getYSpeed());
		}
		
		if (touchesEastBorder()) {
			setXSpeed(-getXSpeed());
		}

		if (touchesSouthBorder()) {
			setYSpeed(-getYSpeed());
		}

		if (touchesWestBorder()) {
			setXSpeed(-getXSpeed());
		}
	}

	private boolean touchesNorthBorder() {
		return getYLocation() - getHeight() / 2 <= 0;
	}

	private boolean touchesEastBorder() {
		return getXLocation() + getWidth() / 2 >= getWorld().getWidth();
	}

	private boolean touchesSouthBorder() {
		return getYLocation() + getHeight() / 2 >= getWorld().getHeight();
	}

	private boolean touchesWestBorder() {
		return getXLocation() - getWidth() / 2 <= 0;
	}
}
