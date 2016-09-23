package de.gocodinggroup.multiplicationtable.game.model.gameentites;

import de.gocodinggroup.multiplicationtable.game.controller.*;
import de.gocodinggroup.multiplicationtable.game.model.*;
import de.gocodinggroup.multiplicationtable.input.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;

public class PlayerEntity extends GameEntity {
	public PlayerEntity(int locationX, int locationY, int width, int height) {
		super(locationX, locationY, width, height);

		Circle circle = new Circle();
		circle = new Circle();
		circle.setFill(Color.PINK);
		circle.setRadius(15);
		circle.setCenterX(GameController.WORLD_WIDTH / 2);
		circle.setCenterY(GameController.WORLD_HEIGHT / 2);
		this.fxRepresentation = circle;
	}

	@Override
	protected void move(long now) {
		InputParser input = GameController.getInputProvider();
		this.locationX = input.getPlayerX();
		this.locationY = input.getPlayerY();
		((Circle) this.fxRepresentation).setCenterX(this.locationX);
		((Circle) this.fxRepresentation).setCenterY(this.locationY);
	}
}
