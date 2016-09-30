package de.gocodinggroup.multiplicationtable.grounddetection;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

public interface PlaneApproximation {
	Vector3D getNormal();
	Vector3D getPointOnPlane();
}
