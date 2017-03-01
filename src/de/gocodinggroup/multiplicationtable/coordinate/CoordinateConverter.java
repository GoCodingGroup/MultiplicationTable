package de.gocodinggroup.multiplicationtable.coordinate;

import org.apache.commons.math3.geometry.euclidean.threed.*;

public class CoordinateConverter {
	/**
	 * Converts coordinates based on Kinects coordinate system into the
	 * playground or beamer coordinate system
	 * 
	 * 
	 * KINECT COORDINATE SYSTEM
	 * 
	 * The origin of Kinects coordinate system is located in the IR depth sensor
	 * of the Kinect Controller. It is a right-handed coordinate system. The
	 * positive z-axis is extending in the direction in which the Kinect is
	 * pointed. The positive y-axis extends upward. The distance is expressed in
	 * millimeters. For details see:
	 * https://msdn.microsoft.com/en-us/library/hh973078.aspx
	 * 
	 * 
	 * BEAMER COORDINATE SYSTEM
	 * 
	 * The image of the beamer is distorted. If the beamer is well adjusted, the
	 * outline of the projection is trapezoidal. The vertex of the trapezoid
	 * which is placed on the left side (looking towards to the beamer) and is
	 * closer to the beamer is called UPPER-LEFT vertex. The remoter vertex on
	 * the left side (looking towards the beamer) is called LOWER-LEFT vertex.
	 * The two other vertexes are named correspondingly UPPER-RIGHT and
	 * LOWER-RIGHT vertex. The point in the middle of the upper-left vertex and
	 * the upper-right vertex is called UPPER-MIDDLE. The three components of a
	 * point in beamer coordinates are defined as follows. firstComponent:
	 * fractionOfScreenWidth, secondComponent: fractionOfScreenHeight,
	 * thirdComponent: altitude in Millimeters above floor.
	 * 
	 * The projection of the beamer must be flipped accordingly to the
	 * perspective of the gamer described in the section SCREEN COORDINATE
	 * SYSTEM.
	 * 
	 * 
	 * FLOOR COORDINATE SYSTEM
	 * 
	 * The origin of the floor coordinate system is identical to the point
	 * upper-middle described above. The z-Axis starts at upper-middle and runs
	 * through the point lower-middle. The x-Axis starts at upper-middle and runs
	 * through upper-right. The y-Axis is vertical to the ground. The distance is
	 * expressed in millimeters.
	 **/
	/**
	//@formatter:off

	              Kinect----> xAxis-Kinect
	                |
	                |
	                V
	           zAxis-Kinect
	
	     upperLeft-------upperRight----> xAxis-Playground
	       /         |          \  
	      /          |           \
	     /           V            \
	    /      zAxis-PlayGround    \
	   /                            \
	  /                              \
	 /                                \
	 lowerLeft----------------lowerRight
	
	
	//@formatter:on
	*/
	private static final double TOLERABLE_ANGLE_IN_DEGREES = 1;
	private static final double TOLERABLE_DISTANCE = 0.01;
	private Vector3D lowerRight;
	private Vector3D lowerLeft;
	private Vector3D upperRight;
	private Vector3D upperLeft;
	private Vector3D upperMiddle;
	private Vector3D lowerMiddle;

	public CoordinateConverter(Vector3D upperLeft, Vector3D upperRight, Vector3D lowerLeft, Vector3D lowerRight) {
		this.upperLeft = upperLeft;
		this.upperRight = upperRight;
		this.lowerLeft = lowerLeft;
		this.lowerRight = lowerRight;
		this.upperMiddle = getMiddle(upperLeft, upperRight);
		this.lowerMiddle = getMiddle(lowerLeft, lowerRight);
	}

	private Vector3D getMiddle(Vector3D vector1, Vector3D vector2) {
		Vector3D middle = vector1.add(vector2);
		middle = middle.scalarMultiply(.5);
		return middle;
	}

	/**
	 * Converts coordinates based on Kinects coordinate system into the floor
	 * coordinate system
	 * 
	 * @param kinectPoint:
	 *           the point in Kinect coordinates
	 * @return corresponding point in floor coordinates
	 */
	public Vector3D convertFromKinectToFloorCoords(Vector3D kinectPoint) {

		// move to origin
		Vector3D floorPoint = kinectPoint.subtract(upperMiddle);

		// define rotation
		Vector3D zAxisKinect = new Vector3D(0, 0, 1);
		Vector3D zAxisPlayGround = lowerMiddle.subtract(upperMiddle);
		Rotation rotation = new Rotation(zAxisPlayGround, zAxisKinect);
		// rotate
		floorPoint = rotation.applyTo(floorPoint);

		return floorPoint;
	}

	/**
	 * Converts coordinates based on Kinects coordinate system into the beamer
	 * coordinate system
	 * 
	 * @param kinectPoint:
	 *           the point in Kinect coordinates
	 * @return corresponding point in beamer coordinates firstComponent:
	 *         fractionOfScreenWidth, secondComponent: fractionOfScreenHeight,
	 *         thirdComponent: altitude in Millimeters above floor
	 */
	public Vector3D convertFromKinectToBeamerCoords(Vector3D kinectPoint) {

		Line leftBoundary = new Line(upperLeft, lowerLeft, TOLERABLE_DISTANCE);
		Line rightBoundary = new Line(upperRight, lowerRight, TOLERABLE_DISTANCE);
		Vector3D vanishingPointLeftAndRightBoundary = leftBoundary.intersection(rightBoundary);

		Line upperBoundary = new Line(upperLeft, upperRight, TOLERABLE_DISTANCE);
		Line lowerBoundary = new Line(lowerLeft, lowerRight, TOLERABLE_DISTANCE);
		Vector3D vanishingPointUpperAndLowerBoundary = upperBoundary.intersection(lowerBoundary);

		double fractionOfScreenWidth = 0;
		double fractionOfScreenHeight = 0;
		double altitude = kinectPoint.getY();

		// rectangle
		if (upperBoundaryAndLowerBoundaryAreApproximatlyParallel()
				&& leftBoundaryAndRightBoundaryAreApproximatlyParallel()) {
			fractionOfScreenHeight = calculateFractionOfBeamerHeight(kinectPoint);
			fractionOfScreenWidth = calculateFractionOfBeamerWidth(kinectPoint);
			// trapezium
		} else if (upperBoundaryAndLowerBoundaryAreApproximatlyParallel()) {
			fractionOfScreenHeight = calculateFractionOfBeamerHeight(kinectPoint);
			Line vanishingLineKinectPoint = new Line(vanishingPointLeftAndRightBoundary, kinectPoint, TOLERABLE_DISTANCE);
			Vector3D intersection = vanishingLineKinectPoint.intersection(upperBoundary);
			fractionOfScreenWidth = intersection.distance(upperLeft) / upperLeft.distance(upperRight);
			// trapezium
		} else if (leftBoundaryAndRightBoundaryAreApproximatlyParallel()) {
			fractionOfScreenWidth = calculateFractionOfBeamerWidth(kinectPoint);
			Line vanishingLineKinectPoint = new Line(vanishingPointUpperAndLowerBoundary, kinectPoint, TOLERABLE_DISTANCE);
			Vector3D intersection = vanishingLineKinectPoint.intersection(leftBoundary);
			fractionOfScreenHeight = intersection.distance(upperLeft) / upperLeft.distance(lowerLeft);
			// quadrilateral
		} else {
			Line vanishingLineKinectPoint = new Line(vanishingPointLeftAndRightBoundary, kinectPoint, TOLERABLE_DISTANCE);
			Vector3D intersection = vanishingLineKinectPoint.intersection(upperBoundary);
			fractionOfScreenWidth = intersection.distance(upperLeft) / upperLeft.distance(upperRight);

			vanishingLineKinectPoint = new Line(vanishingPointUpperAndLowerBoundary, kinectPoint, TOLERABLE_DISTANCE);
			intersection = vanishingLineKinectPoint.intersection(leftBoundary);
			fractionOfScreenHeight = intersection.distance(upperLeft) / upperLeft.distance(lowerLeft);
		}

		Vector3D beamerPoint = new Vector3D(fractionOfScreenWidth, fractionOfScreenHeight, altitude);

		return beamerPoint;
	}

	private double calculateFractionOfBeamerWidth(Vector3D kinectPoint) {
		double totalLength = Math.abs(upperLeft.getX() - upperRight.getX());
		double partLength = Math.abs(upperLeft.getX() - kinectPoint.getX());
		return partLength / totalLength;
	}

	private double calculateFractionOfBeamerHeight(Vector3D kinectPoint) {
		double totalLength = Math.abs(upperLeft.getZ() - lowerLeft.getZ());
		double partLength = Math.abs(upperLeft.getZ() - kinectPoint.getZ());
		return partLength / totalLength;
	}

	private boolean upperBoundaryAndLowerBoundaryAreApproximatlyParallel() {
		Vector3D upperVector = upperLeft.subtract(upperRight);
		Vector3D lowerVector = lowerLeft.subtract(lowerRight);
		return vectorsAreApproximatlyParrallel(upperVector, lowerVector);
	}

	private boolean leftBoundaryAndRightBoundaryAreApproximatlyParallel() {
		Vector3D leftVector = upperLeft.subtract(lowerLeft);
		Vector3D rightVector = upperRight.subtract(lowerRight);
		return vectorsAreApproximatlyParrallel(leftVector, rightVector);
	}

	private double getAngleInDegrees(Vector3D firstArm, Vector3D secondArm) {
		firstArm = getNormalizedVector(firstArm);
		secondArm = getNormalizedVector(secondArm);
		double dotProduct = firstArm.dotProduct(secondArm);
		double angleInRadians = Math.acos(dotProduct);
		double angleInDegrees = (angleInRadians / (2 * Math.PI)) * 360;
		return angleInDegrees;
	}

	private Vector3D getNormalizedVector(Vector3D vector) {
		double length = vector.getNorm();
		double scaleFactor = 1 / length;
		vector = vector.scalarMultiply(scaleFactor);
		return vector;
	}

	private boolean vectorsAreApproximatlyParrallel(Vector3D vector1, Vector3D vector2) {
		double angleInDegrees = getAngleInDegrees(vector1, vector2);
		boolean areParallel = angleInDegrees < TOLERABLE_ANGLE_IN_DEGREES;
		return areParallel;
	}
}
