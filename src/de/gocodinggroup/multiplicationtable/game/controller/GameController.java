package de.gocodinggroup.multiplicationtable.game.controller;

import java.util.*;

import de.gocodinggroup.multiplicationtable.game.model.*;
import de.gocodinggroup.multiplicationtable.game.model.gameentites.*;
import de.gocodinggroup.multiplicationtable.input.*;
import de.gocodinggroup.multiplicationtable.util.*;
import de.gocodinggroup.multiplicationtable.util.events.*;
import javafx.animation.*;
import javafx.application.*;
import javafx.scene.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.stage.*;

/**
 * Class that acts as supreme leader of the game (Application Entry Point as
 * well)
 */
public class GameController extends Application {
	public static final int WORLD_WIDTH = 1000;
	public static final int WORLD_HEIGHT = WORLD_WIDTH;
	private static Random random;

	// All Game entities (so that we can change our root node at will, f.e. for
	// reordering subnodes)
	private List<GameEntity> entities;

	// Save this so that we can add BubbleEntites
	private Group rootNode;

	// Game Background
	private Rectangle gameBoardBackground;

	// Input provider for this game
	private static InputProvider input;

	public static void main(String[] args) {
		// Launch javaFX game
		launch(args);
	}

	public static Random getRandom() {
		if (random == null)
			random = new Random();

		return random;
	}

	public static InputProvider getInputProvider() {
		return input;
	}

	public GameController() {
		this.entities = new ArrayList<>();
		EventManager.registerEventListenerForEvent(BubbleHitEvent.class, (event) -> bubbleHit((BubbleHitEvent) event));

		this.gameBoardBackground = new Rectangle(0, 0, WORLD_WIDTH, WORLD_HEIGHT);
		this.gameBoardBackground.setFill(Color.BLACK);
		this.gameBoardBackground.setStroke(Color.GREEN);
		this.gameBoardBackground.setStrokeWidth(5);
	}

	@Override
	public void start(Stage primaryStage) {
		/* Initialize Game */
		primaryStage.setTitle("Multiplication Table");

		try {
			this.rootNode = new Group();
			Scene mainScene = new Scene(rootNode, WORLD_WIDTH, WORLD_HEIGHT);

			/*
			 * Change this line of code to select desired input
			 */
			//input = new MouseInput(this.rootNode);
			input = new KinectInput();

			// Add background stuff
			this.rootNode.getChildren().add(this.gameBoardBackground);

			/* TODO: tmp code */
			addBubble();
			addBubble();
			addBubble();
			addBubble();
			addBubble();
			addBubble();
			addBubble();
			addBubble();
			addBubble();
			addBubble();
			addBubble();
			// Always add player last so that he will be drawn over everything
			// TODO: find better way
			addPlayer();

			for (GameEntity entity : this.entities)
				this.rootNode.getChildren().add(entity.getFXRepresentation());

			primaryStage.setScene(mainScene);
			primaryStage.show();

			/* Start Game */
			UpdateTimer timer = new UpdateTimer();
			timer.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void addPlayer() {
		PlayerEntity entity = new PlayerEntity(0, 0, 20, 20);
		this.entities.add(entity);
	}

	private void addBubble() {
		BubbleEntity entity = new BubbleEntity(250, 250, 30, 30, "Halleluja!");
		entity.setSpeed(getRandom().nextInt(8) - 4, getRandom().nextInt(8) - 4);
		this.entities.add(entity);
	}

	private void bubbleHit(BubbleHitEvent event) {
		System.out.println("Bubble" + event.getBubble() + "has been hit!");
	}

	private class UpdateTimer extends AnimationTimer {
		@Override
		public void handle(long now) {
			// Move all GameObjects
			EventManager.dispatchEventAndWait(new MoveEvent(now));

			// Update Game (Physics, logic etc)
			EventManager.dispatchEventAndWait(new UpdateEvent(now));
		}
	}
}
