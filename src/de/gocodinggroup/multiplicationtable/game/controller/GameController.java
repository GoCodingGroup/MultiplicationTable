package de.gocodinggroup.multiplicationtable.game.controller;

import java.io.*;
import java.util.*;

import de.gocodinggroup.multiplicationtable.game.model.gameentites.*;
import de.gocodinggroup.multiplicationtable.input.*;
import de.gocodinggroup.multiplicationtable.input.kinect.*;
import de.gocodinggroup.multiplicationtable.util.*;
import de.gocodinggroup.multiplicationtable.util.events.*;
import de.gocodinggroup.multiplicationtable.util.record.*;
import edu.ufl.digitalworlds.j4k.*;
import javafx.animation.*;
import javafx.application.*;
import javafx.scene.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.stage.*;

/**
 * Class that acts as supreme leader of the game (Application Entry Point as
 * well). All Hail GameController!
 *
 */
public class GameController extends Application {
	/** Width of the gamedisplay TODO: refactor */
	public static final int WORLD_WIDTH = 1000;

	/** Height of the gamedisplay TODO: refactor */
	public static final int WORLD_HEIGHT = WORLD_WIDTH;

	/**
	 * Logger instance, which should be used for logging instead of system.out
	 */
	public static final Logger LOGGER = new Logger(Logger.LOG_DEBUG);

	/**
	 * Random number generator for this game TODO: does this have to be central?
	 */
	private static Random random;

	/** Input provider for this game */
	private static InputParser input;

	/** Player entity */
	private PlayerEntity player;

	/** Save this so that we can add BubbleEntites TODO: review */
	private Group rootNode;

	/** Game Background TODO: review */
	private Rectangle gameBoardBackground;

	/** The kinect controller */
	private KinectControllerInterface kinectController;

	/** capture/playback stuff */
	private KinectDataRecorder dataRecorder;
	private String saveFile;
	private boolean shouldPlayback = false;
	private boolean shouldCapture = false;

	/**
	 * Application entry point
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		// Launch javaFX game
		launch(args);
	}

	/**
	 * Retrieve the game's random generator using this method
	 * 
	 * @return
	 */
	public static Random getRandom() {
		if (random == null)
			random = new Random();

		return random;
	}

	/**
	 * Retrieve the game's input provider using this method
	 * 
	 * @return
	 */
	public static InputParser getInputProvider() {
		return input;
	}

	/**
	 * Game setup method
	 * 
	 * @throws InterruptedException
	 * @throws IOException
	 */
	private void setupGame() throws InterruptedException, IOException {
		// Register for the event that a bubble is hit, so that we can verify
		// player input and generate a new task if wanted
		EventManager.registerEventListenerForEvent(BubbleHitEvent.class, e -> bubbleHit());

		// Check whether or not we should playback
		if (!this.shouldPlayback) {
			this.kinectController = new KinectRealController();
			this.kinectController.startAndWait(J4KSDK.DEPTH | J4KSDK.SKELETON);
		} else {
			this.kinectController = new KinectPlaybackController(this.saveFile);
			this.kinectController.startAndWait(J4KSDK.DEPTH | J4KSDK.SKELETON);
		}

		// Create input method (find way to not have to manually set this)
		input = new KinectInputParser(this.kinectController);
		// input = new MouseInput(this.rootNode);

		// Check whether we should capture
		if (this.shouldCapture) {
			LOGGER.info("Activating kinect frame recording to file \"" + this.saveFile + "\"");
			try {
				this.dataRecorder = new KinectDataRecorder(this.kinectController, new StandardKinectDataCompressor(),
						this.saveFile);
				Runtime.getRuntime().addShutdownHook(new Thread(() -> {
					try {
						this.kinectController.stop();
						dataRecorder.finish();
					} catch (IOException e) {
						e.printStackTrace();
						LOGGER.error("Finishing writing to file failed!");
					}
				}));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				LOGGER.error("could not find the file to  to");
			} catch (IOException e) {
				e.printStackTrace();
				LOGGER.error("could not deal with record file");
			}
		}

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
		/* Parse args */
		Map<String, String> params = getParameters().getNamed();
		for (String key : params.keySet()) {
			if (key.equals("capture")) {
				this.shouldCapture = true;
				this.saveFile = params.get(key);
			} else if (key.equals("playback")) {
				this.shouldPlayback = true;
				this.saveFile = params.get(key);
			}
		}

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

	@Override
	public void stop() throws Exception {
		LOGGER.info("Exiting JavaFX Application");
		this.kinectController.stop();
		if (this.dataRecorder != null)
			this.dataRecorder.finish();
	}

	/**
	 * Convenience method to add a bubble
	 */
	private void addBubble() {
		BubbleEntity entity = new BubbleEntity(250, 250, 30, 30, "Halleluja!");
		entity.setSpeed(getRandom().nextInt(8) - 4, getRandom().nextInt(8) - 4);
		this.rootNode.getChildren().add(entity.getFXRepresentation());
	}

	/**
	 * Clears all bubbles and is eventually going to start a new round. TODO:
	 * implement
	 * 
	 * TODO: trigger this mechanism when win condition for one round is met
	 */
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

	/**
	 * Functionality unimplemented
	 */
	private void bubbleHit() {
		// TODO: Bubble was hit, validate input and generate new task
		LOGGER.warn("Unimplemented method bubbleHit() in GameController!");
	}
}
