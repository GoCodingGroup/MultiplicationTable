package de.gocodinggroup.multiplicationtable.game.model.viewerentites;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.gocodinggroup.multiplicationtable.util.EventManager;
import de.gocodinggroup.multiplicationtable.util.events.XYZDataEvent;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class PointCloudAppearanceYzPlane implements ViewerEntity {
	private static final double RADIUS = 1;
	private ArrayList<Node> dataAsNodes;
	private Group fxRepresentation;
	private Group parent;
	private static double scaling = 300;
	private static double xShift = 0;
	private static double yShift = 300;
	private static double zShift = 0;

	public PointCloudAppearanceYzPlane(List<Point3D> dataAsPoints3D) {
		dataAsNodes = new ArrayList<Node>();
		constructAppearance(dataAsPoints3D);
		fxRepresentation = new Group();
		fxRepresentation.getChildren().addAll(dataAsNodes);
		EventManager.registerEventListenerForEvent(XYZDataEvent.class, (event) -> update((XYZDataEvent) event));
	}
	
	PointCloudAppearanceYzPlane(List<Point3D> dataAsPoints3D, Group parent){
		this(dataAsPoints3D);
		this.parent = parent;
	}
	

	private void constructAppearance(List<Point3D> dataAsPoints3D) {
		for (Point3D point : dataAsPoints3D) {
			Node fxRepresentation = getFxRepresentationOfSinglePoint(point);
			dataAsNodes.add(fxRepresentation);
		}
	}

	Node getFxRepresentationOfSinglePoint(Point3D point) {
		Circle circle = new Circle();
		circle.setRadius(RADIUS);
		circle.setFill(Color.BLUE);
		// zAxis of Kinect is shown on x-axis of screen
		circle.setCenterX(scaling * (point.getZ() + xShift));
		// xAxis of Kinect is shown on -y-axis of screen
		circle.setCenterY(-scaling * (point.getY() + yShift));
		return circle;
	}

	private void update(XYZDataEvent event) {
		List<Point3D> dataAsPoints3D = SensorDataConverter.convertSensorDataFromXYZToArrayList(event.getXYZData());

		Iterator<Point3D> iteratorPoints3D = dataAsPoints3D.iterator();
		Iterator<Node> iteratorNodes = dataAsNodes.iterator();

		while (iteratorNodes.hasNext() && iteratorPoints3D.hasNext()) {
			Point3D point3D = iteratorPoints3D.next();
			// zAxis of Kinect is shown on x-axis of screen
			double x = scaling * (point3D.getZ()) + zShift;
			// yAxis of Kinect is shown on y-axis of screen
			double y = -scaling * (point3D.getY()) + yShift;

			Node nextNode = iteratorNodes.next();
			nextNode.relocate(x, y);
		}
		/*
		 * //TODO remove:
		 * System.out.println("PointCloudAppearanceYzPlane.update( ): " +
		 * event.toString()+ " data: "); for(Node node : dataAsNodes){
		 * System.out.print("("+ node.getLayoutX() + ", " + node.getLayoutY() +
		 * "), "); } System.out.println();
		 */
	}

	public Node getFxRepresentation() {
		return fxRepresentation;
	}
}
