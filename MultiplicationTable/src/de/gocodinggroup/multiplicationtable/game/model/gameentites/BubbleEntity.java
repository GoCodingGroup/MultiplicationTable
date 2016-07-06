package de.gocodinggroup.multiplicationtable.game.model.gameentites;

import de.gocodinggroup.multiplicationtable.game.controller.*;
import de.gocodinggroup.multiplicationtable.game.model.*;
import de.gocodinggroup.multiplicationtable.util.*;
import de.gocodinggroup.multiplicationtable.util.events.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;

/**
 * Special GameEntity that will be displayed as a circle
 *
 */
public class BubbleEntity extends GameEntity {
	private Circle shape;

	public BubbleEntity(int locationX, int locationY, int width, int height) {
		super(locationX, locationY, width, height);

		this.shape = new Circle();
		this.shape.setFill(Color.BLUE);
		this.shape.setOnMousePressed((mouseEvent) -> {
			if (getSpeedX() == 0 && getSpeedY() == 0)
				setSpeed(GameController.getRandom().nextInt(8) - 4, GameController.getRandom().nextInt(8) - 4);
			else
				setSpeed(0, 0);

			EventManager.dispatchEventAndWait(new BubbleHitEvent(this));
		});

		// Register for future update events
		EventManager.registerEventListenerForEvent(UpdateEvent.class, (event) -> update());
	}

	/**
	 * Update Game Logic
	 */
	private void update() {
		if (getLocationX() - getWidth() / 2 <= 0 || getLocationX() + getWidth() / 2 >= GameController.WORLD_WIDTH)
			setSpeedX(-getSpeedX());
		if (getLocationY() - getHeight() / 2 <= 0 || getLocationY() + getHeight() / 2 >= GameController.WORLD_HEIGHT)
			setSpeedY(-getSpeedY());

		resize();
		relocate();
	}

	private void resize() {
		int radius = Math.max(this.getHeight(), this.getWidth());
		this.shape.setRadius(radius);
	}

	private void relocate() {
		this.shape.setCenterX(this.getLocationX());
		this.shape.setCenterY(this.getLocationY());

	}

	/* getter and setter */

	public Circle getShape() {
		return this.shape;
	}
}
