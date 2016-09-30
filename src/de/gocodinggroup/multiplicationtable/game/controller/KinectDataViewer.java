package de.gocodinggroup.multiplicationtable.game.controller;

import java.util.ArrayList;

import de.gocodinggroup.multiplicationtable.game.model.viewerentites.DepthFrameAppearance;

import de.gocodinggroup.multiplicationtable.game.model.viewerentites.PlaneAppearanceYzPlane;
import de.gocodinggroup.multiplicationtable.game.model.viewerentites.PointCloudAppearanceYzPlane;
import de.gocodinggroup.multiplicationtable.input.kinect.KinectControllerInterface;
import de.gocodinggroup.multiplicationtable.input.kinect.KinectRealController;
import de.gocodinggroup.multiplicationtable.util.events.UpdateSceneEvent;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

public class KinectDataViewer extends Application {
	public static final int VIEWER_WIDTH = 1200;
	public static final int VIEWER_HEIGHT = 800;
	private static KinectControllerInterface input;
	

	@Override
	public void start(Stage primaryStage) throws Exception {

		try {
			input = new KinectRealController();
			
			Group root = new Group();
			/*
			 * if (input.getDeviceType() == input.MICROSOFT_KINECT_1) {
			 * 
			 * double angle = (double) -input.getElevationAngle(); kinectEntity
			 * = new KinectEntity(VIEWER_WIDTH / 25, VIEWER_HEIGHT / 2,
			 * VIEWER_WIDTH, VIEWER_HEIGHT, angle);
			 * root.getChildren().add(kinectEntity.getFxRepresentation()); }
			 */
			ArrayList<Point3D> dummyXYZData = initializeData();
			PointCloudAppearanceYzPlane pointCloudAppearance = new PointCloudAppearanceYzPlane(dummyXYZData);
			//PointCloudAppearanceYzPlane2 pointCloudAppearance = new PointCloudAppearanceYzPlane2(root);
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
					System.out.println("handleEvent:");
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
