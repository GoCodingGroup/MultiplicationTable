package de.gocodinggroup.multiplicationtable.game.model.viewerentites;

import org.apache.commons.math3.geometry.euclidean.threed.*;

import de.gocodinggroup.multiplicationtable.grounddetection.*;
import de.gocodinggroup.multiplicationtable.util.events.*;
import de.gocodinggroup.util.*;
import javafx.scene.*;
import javafx.scene.shape.Line;

public class PlaneAppearanceYzPlane implements ViewerEntity {
	private static double STROKE_WIDTH = 2.0;
	private static double LENGTH = 1000;
	private Line line;

	/*
	 * private double startX; private double startY; private double endX; private
	 * double endY;
	 */
	public PlaneAppearanceYzPlane() {
		line = new Line(0, 0, LENGTH, LENGTH);
		EventManager.registerEventListenerForEvent(XYZDataEvent.class, (event) -> update((XYZDataEvent) event));
	}

	private void update(XYZDataEvent event) {
		PlaneApproximationImplXYZ planeApproximation = new PlaneApproximationImplXYZ(event.getXYZData());
		Vector3D normal = planeApproximation.getNormal();
		line.setStrokeWidth(STROKE_WIDTH);
		line.setEndX(-normal.getZ() * LENGTH);
		line.setEndY(normal.getY() * LENGTH);
	}

	@Override
	public Node getFxRepresentation() {
		return line;
	}

}
