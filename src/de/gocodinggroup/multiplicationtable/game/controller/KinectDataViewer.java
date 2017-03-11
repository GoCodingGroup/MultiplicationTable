package de.gocodinggroup.multiplicationtable.game.controller;

import java.util.*;

import de.gocodinggroup.multiplicationtable.game.model.viewerentites.*;
import de.gocodinggroup.multiplicationtable.input.kinect.*;
import de.gocodinggroup.multiplicationtable.util.events.*;
import javafx.application.*;
import javafx.event.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.input.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.stage.*;

public class KinectDataViewer extends Application {
	public static final int VIEWER_WIDTH = 1200;
	public static final int VIEWER_HEIGHT = 800;

	private static KinectControllerInterface input;

	@Override
	public void start(Stage primaryStage) throws Exception {

		try {

			input = new KinectRealController();

			Group root = new Group();

			ArrayList<Point3D> dummyXYZData = initializeData();
			PointCloudAppearanceYzPlane pointCloudAppearance = new PointCloudAppearanceYzPlane(dummyXYZData);
			root.getChildren().add(pointCloudAppearance.getFxRepresentation());

			ArrayList<Point3D> depthData = initializeData();
			DepthFrameAppearance depthFrameAppearance = new DepthFrameAppearance(depthData);
			root.getChildren().add(depthFrameAppearance.getFxRepresentation());

			PlaneAppearanceYzPlane planeAppearanceYzPlane = new PlaneAppearanceYzPlane();
			root.getChildren().add(planeAppearanceYzPlane.getFxRepresentation());

			primaryStage.setTitle("Kinect Data Viewer");
			primaryStage.setX(0);
			primaryStage.setY(0);
			primaryStage.setWidth(VIEWER_WIDTH);
			primaryStage.setHeight(VIEWER_HEIGHT);
			Scene scene = new Scene(root);
			primaryStage.setScene(scene);
			primaryStage.show();
			scene.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent event) {
					Circle circle = new Circle(event.getSceneX(), event.getSceneY(), 30);
					circle.setFill(Color.YELLOW);
					root.getChildren().add(circle);
				}
			});
			scene.addEventHandler(UpdateSceneEvent.UPDATE_SCENE, new EventHandler<UpdateSceneEvent>() {
				@Override
				public void handle(UpdateSceneEvent event) {
					root.getChildren().add(event.getGroup());
				}
			});

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private ArrayList<Point3D> initializeData() {
		ArrayList<Point3D> dummyXYZData = new ArrayList<Point3D>();
		for (int i = 0; i < input.getDepthHeight() * input.getDepthWidth(); i++) {
			dummyXYZData.add(new Point3D(0, 0, 0));
		}
		return dummyXYZData;
	}

	public static void main(String[] args) {
		// Launch Viewer
		launch(args);
	}
}
