package de.lezleoh.multiplicationtable.view;

import de.lezleoh.multiplicationtable.model.Reflector;
import de.lezleoh.multiplicationtable.model.World;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
	private World world;

	@Override
	public void start(Stage primaryStage) {
		world = new World(500, 500);
		primaryStage.setTitle("Multiplication Table");
		try {
			Group root = new Group();
			Scene scene = new Scene(root, world.getWidth(), world.getHeight());
			
			Reflector reflector = new Reflector(world, 30, 30);
			reflector.setXLocation(100);
			reflector.setYLocation(25);
			reflector.setXSpeed(1);
			reflector.setYSpeed(5);
			
			BubbleControler bubbleViewControler = new BubbleControler(reflector);
			BubbleView bubbleView = bubbleViewControler.getView();
			root.getChildren().add(bubbleView.getShape());
			
			GameAnimation gameAnimation = new GameAnimation();
			gameAnimation.start();
			
			primaryStage.setScene(scene);
			primaryStage.show();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void doHandle(){
		world.act();
	}

	public static void main(String[] args) {
		launch(args);
	}
	
	private class GameAnimation extends AnimationTimer {

		@Override
		public void handle(long now) {
			doHandle();

		}

	}
}
