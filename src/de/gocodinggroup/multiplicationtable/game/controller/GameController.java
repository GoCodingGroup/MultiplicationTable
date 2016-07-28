package de.gocodinggroup.multiplicationtable.game.controller;

import java.util.*;

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

	// Player entity
	private PlayerEntity player;

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
		if (random == null) random = new Random();

		return random;
	}

	public static InputProvider getInputProvider() {
		return input;
	}

	private void setupGame() {
		// Register for the event that a bubble is hit, so that we can verify
		// player input and generate a new task if wanted
		EventManager.registerEventListenerForEvent(BubbleHitEvent.class, e -> bubbleHit());

		/*
		 * Create input method
		 */
		input = new MouseInput(this.rootNode);
		// input = new KinectInput();

		// Setup background
		this.gameBoardBackground = new Rectangle(0, 0, WORLD_WIDTH, WORLD_HEIGHT);
		this.gameBoardBackground.setFill(Color.BLACK);
		this.gameBoardBackground.setStroke(Color.GREEN);
		this.gameBoardBackground.setStrokeWidth(5);
		this.rootNode.getChildren().add(this.gameBoardBackground);

		// Create player:
		this.player = new PlayerEntity(0, 0, 25, 25);

		// Generate initial task
		generateTask();
	}

	@Override
	public void start(Stage primaryStage) {
		/* Initialize Game */
		primaryStage.setTitle("Multiplication Table");

		try {
			this.rootNode = new Group();
			Scene mainScene = new Scene(rootNode, WORLD_WIDTH, WORLD_HEIGHT);

			// Setup all of our game
			setupGame();

			primaryStage.setScene(mainScene);
			primaryStage.show();

			/* Start Game */
			new AnimationTimer() {
				@Override
				public void handle(long now) {
					// Move all GameObjects
					EventManager.dispatchEventAndWait(new MoveEvent(now));

					// Update Game (Physics, logic etc)
					EventManager.dispatchEventAndWait(new UpdateEvent(now));
				}
			}.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void addBubble() {
		BubbleEntity entity = new BubbleEntity(250, 250, 30, 30, "Halleluja!");
		entity.setSpeed(getRandom().nextInt(8) - 4, getRandom().nextInt(8) - 4);
		this.rootNode.getChildren().add(entity.getFXRepresentation());
	}

	// TODO: trigger this mechanism when win condition for one round is met
	private void generateTask() {
		// Delete old entities
		this.rootNode.getChildren().clear();

		// Readd player and background
		this.rootNode.getChildren().add(this.player.getFXRepresentation());
		this.rootNode.getChildren().add(this.gameBoardBackground);

		/* TODO: tmp code */
		addBubble();
		addBubble();
		addBubble();
		addBubble();
		addBubble();

		// Move player to the front and background to the back
		this.player.getFXRepresentation().toFront();
		this.gameBoardBackground.toBack();
	}

	private void bubbleHit() {
		// TODO: Bubble was hit, validate input and generate new task
	}
}
