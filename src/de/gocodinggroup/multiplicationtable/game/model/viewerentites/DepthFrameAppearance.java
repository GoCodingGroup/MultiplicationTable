package de.gocodinggroup.multiplicationtable.game.model.viewerentites;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.gocodinggroup.multiplicationtable.util.EventManager;
import de.gocodinggroup.multiplicationtable.util.events.DepthDataEvent;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;

public class DepthFrameAppearance implements ViewerEntity {
	private static final double RADIUS = 1;
	private static double scaling = 1;
	private static double xShift = 0;
	private static double yShift = 0;

	private ArrayList<Shape> dataAsNodes;
	private Group fxRepresentation;

	public DepthFrameAppearance(List<Point3D> dataAsPoints3D) {
		this.dataAsNodes = new ArrayList<Shape>();
		constructAppearance(dataAsPoints3D);
		fxRepresentation = new Group();
		fxRepresentation.getChildren().addAll(dataAsNodes);
		EventManager.registerEventListenerForEvent(DepthDataEvent.class, (event) -> update((DepthDataEvent) event));
	}

	private void constructAppearance(List<Point3D> dataAsPoints3D) {
		for (Point3D point : dataAsPoints3D) {
			Shape fxRepresentation = getFxRepresentationOfSinglePoint(point);
			dataAsNodes.add(fxRepresentation);
		}

	}

	private Shape getFxRepresentationOfSinglePoint(Point3D point) {
		Circle circle = new Circle();
		circle.setRadius(RADIUS);
		circle.setCenterX(point.getX());
		circle.setCenterY(point.getY());
		Color depthColor = calculateColorCorrepondingToDepth(point);
		circle.setFill(depthColor);
		return circle;
	}

	private Color calculateColorCorrepondingToDepth(Point3D point) {
		double depth = point.getZ();
		// values from 8000 to 40000 are mapped to values from 0 to 255
		Double greyscale = -(0.00796875 * depth) + 318.75;
		int greyscaleInt = greyscale.intValue();
		greyscaleInt = Math.min(greyscaleInt, 255);
		greyscaleInt = Math.max(greyscaleInt, 0);
		return Color.rgb(greyscaleInt, 0, 0);

	}

	private void update(DepthDataEvent event) {
		List<Point3D> dataAsPoints3D = SensorDataConverter
				.convertSensorDataFromDepthMapToArrayList(event.getDepthFrame(), event.getMaxWidth());

		Iterator<Point3D> iteratorPoints3D = dataAsPoints3D.iterator();
		Iterator<Shape> iteratorNodes = dataAsNodes.iterator();
		while (iteratorNodes.hasNext() && iteratorPoints3D.hasNext()) {
			Point3D point3D = iteratorPoints3D.next();
			// xAxis of Kinect is shown on y-axis of screen
			double x = scaling * (point3D.getY()) + xShift;
			// yAxis of Kinect is shown on x-axis of screen
			double y = scaling * (point3D.getX()) + yShift;

			Shape nextNode = iteratorNodes.next();
			nextNode.relocate(x, y);
			nextNode.setFill(calculateColorCorrepondingToDepth(point3D));
		}

	}

	@Override
	public Node getFxRepresentation() {
		return fxRepresentation;
	}

}
