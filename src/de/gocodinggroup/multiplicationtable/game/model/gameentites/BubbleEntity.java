package de.gocodinggroup.multiplicationtable.game.model.gameentites;

import de.gocodinggroup.multiplicationtable.game.controller.*;
import de.gocodinggroup.multiplicationtable.game.model.*;
import de.gocodinggroup.multiplicationtable.util.*;
import de.gocodinggroup.multiplicationtable.util.events.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.scene.text.*;

/**
 * Special GameEntity that will be displayed as a circle
 *
 */
public class BubbleEntity extends GameEntity {
	private Circle circle;
	private Text text;

	public BubbleEntity(int locationX, int locationY, int width, int height, String displayText) {
		super(locationX, locationY, width, height);

		this.text = new Text(displayText);
		this.text.setBoundsType(TextBoundsType.VISUAL);
		// @formatter:off
		this.text.setStyle(
                "-fx-font-family: \"Times New Roman\";" +
                "-fx-font-size: 15px;"
        );
		// @formatter:on
		this.text.setFill(Color.WHITE);

		this.circle = new Circle();
		this.circle.setFill(Color.BLUE);
		this.circle.setOnMousePressed((mouseEvent) -> bubbleHit());
		circle.setRadius(getWidth(this.text) / 2 + 10); // Padding of 10

		StackPane pane = new StackPane();
		pane.getChildren().addAll(circle, text);
		pane.setPadding(new Insets(0));
		this.fxRepresentation = pane;

		double calculatedWidth = this.circle.getRadius() * 2;
		this.setWidth((int) calculatedWidth);
		this.setHeight((int) calculatedWidth);

		// Register for future update events
		EventManager.registerEventListenerForEvent(UpdateEvent.class, (event) -> update());
		EventManager.registerEventListenerForEvent(PlayerJumpedEvent.class,
				(event) -> playerJumped((PlayerJumpedEvent) event));
	}

	/**
	 * Game Logic
	 */
	private void update() {
		if (getLocationX() - getWidth() / 2 <= 0 || getLocationX() + getWidth() / 2 >= GameController.WORLD_WIDTH)
			setSpeedX(-getSpeedX());
		if (getLocationY() - getHeight() / 2 <= 0 || getLocationY() + getHeight() / 2 >= GameController.WORLD_HEIGHT)
			setSpeedY(-getSpeedY());

		relocate();
	}

	private void playerJumped(PlayerJumpedEvent e) {
		if (isWithinBubble(e.getImpactLocationX(), e.getImpactLocationY()))
			bubbleHit();
	}

	/* Convenience */

	private boolean isWithinBubble(int x, int y) {
		double radius = this.circle.getRadius();
		int locationX = this.getLocationX();
		int locationY = this.getLocationY();

		if (Math.sqrt((locationX - x) * (locationX - x) + (locationY - y) * (locationY - y)) <= radius)
			return true;

		return false;
	}

	private void bubbleHit() {
		if (getSpeedX() == 0 && getSpeedY() == 0)
			setSpeed(GameController.getRandom().nextInt(8) - 4, GameController.getRandom().nextInt(8) - 4);
		else
			setSpeed(0, 0);

		EventManager.dispatchEventAndWait(new BubbleHitEvent(this));
	}

	private void relocate() {
		this.fxRepresentation.setLayoutX(this.getLocationX() - getWidth() / 2);
		this.fxRepresentation.setLayoutY(this.getLocationY() - getHeight() / 2);
	}

	private double getWidth(Text text) {
		new Scene(new Group(text));
		text.applyCss();
		return text.getLayoutBounds().getWidth();
	}
}
