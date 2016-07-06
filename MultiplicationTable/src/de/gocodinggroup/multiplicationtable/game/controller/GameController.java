package de.gocodinggroup.multiplicationtable.game.controller;

import java.util.*;

import de.gocodinggroup.multiplicationtable.game.model.*;
import de.gocodinggroup.multiplicationtable.game.model.gameentites.*;
import de.gocodinggroup.multiplicationtable.kinect.*;
import de.gocodinggroup.multiplicationtable.util.*;
import de.gocodinggroup.multiplicationtable.util.events.*;
import javafx.animation.*;
import javafx.application.*;
import javafx.scene.*;
import javafx.stage.*;

/**
 * Class that acts as supreme leader of the game (Application Entry Point as
 * well)
 */
public class GameController extends Application {
	public static final int WORLD_WIDTH = 500;
	public static final int WORLD_HEIGHT = WORLD_WIDTH;
	private static Random random;

	private List<GameEntity> entities;

	/* TODO: do we need this? */
	private Group rootNode;
	private Scene mainScene;
	// private Stage primaryStage;

	/* TODO: needs refactoring! */
	private static Kinect kinect;

	public static void main(String[] args) {
		kinect = new Kinect();
		kinect.start(Kinect.DEPTH | Kinect.COLOR | Kinect.SKELETON | Kinect.XYZ | Kinect.PLAYER_INDEX);
		// kinect.showViewerDialog();
		launch(args);
	}

	public static Random getRandom() {
		if (random == null)
			random = new Random();

		return random;
	}

	public GameController() {
		this.entities = new ArrayList<>();
		EventManager.registerEventListenerForEvent(BubbleHitEvent.class, (event) -> bubbleHit((BubbleHitEvent) event));
	}

	@Override
	public void start(Stage primaryStage) {
		/* Initialize Game */
		primaryStage.setTitle("Multiplication Table");

		try {
			/* TODO: do we need this (?) */
			this.rootNode = new Group();
			this.mainScene = new Scene(rootNode, WORLD_WIDTH, WORLD_HEIGHT);

			addBubble();

			primaryStage.setScene(mainScene);
			primaryStage.show();

			/* Start Game */
			UpdateTimer timer = new UpdateTimer();
			timer.start();

			/* TODO: TMP (?) */
			// this.primaryStage = primaryStage;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void addBubble() {
		// TODO: refactor system so that this can become less calls

		BubbleEntity entity = new BubbleEntity(250, 250, 30, 30);
		entity.setSpeed(getRandom().nextInt(8) - 4, getRandom().nextInt(8) - 4);
		this.entities.add(entity);
		this.rootNode.getChildren().add(entity.getShape());
	}

	private void bubbleHit(BubbleHitEvent event) {
		System.out.println("Bubble" + event.getBubble() + "has been hit!");
	}

	private class UpdateTimer extends AnimationTimer {
		@Override
		public void handle(long now) {
			// Move all GameObjects
			EventManager.dispatchEventAndWait(new MoveEvent(now));

			// TODO: TMP (?); Update Game (Physics, logic etc)
			EventManager.dispatchEventAndWait(new UpdateEvent(now));
		}
	}
}
