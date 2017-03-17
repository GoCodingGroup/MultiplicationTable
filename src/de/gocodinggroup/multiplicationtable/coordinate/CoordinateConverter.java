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
	 * the upper-right vertex is called UPPER-MIDDLE.
	 * 
	 * The three components of a point in beamer coordinates are defined as
	 * follows.
	 * 
	 * firstComponent: fractionOfScreenWidth,
	 * 
	 * secondComponent: fractionOfScreenHeight,
	 * 
	 * thirdComponent: altitude in Millimeters above floor.
	 * 
	 * The projection of the beamer must be flipped accordingly to the
	 * perspective of the gamer.
	 * 
	 * 
	 * FLOOR COORDINATE SYSTEM
	 * 
	 * The origin of the floor coordinate system is identical to the point
	 * UPPER-LEFT described above. The x-Axis starts at UPPER-LEFT and runs
	 * through UPPER-RIGHT. The y-Axis is vertical to the floor. The z-Axis is
	 * normal to x-Axis and y-Axis. The distance is expressed in millimeters.
	 * 
	 **/
	/**
	//@formatter:off

	              Kinect----> xAxis-Kinect
	                |
	                |
	                V
	           zAxis-Kinect
	
	     upperLeft-------upperRight----> xAxis-Floor
	       /|                   \  
	      / |                    \
	     /  V                     \
	    / zAxis-Floor              \
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

	public CoordinateConverter(Vector3D upperLeft, Vector3D upperRight, Vector3D lowerLeft, Vector3D lowerRight) {
		this.upperLeft = upperLeft;
		this.upperRight = upperRight;
		this.lowerLeft = lowerLeft;
		this.lowerRight = lowerRight;
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

		// construct unitVectors of floor coordinate system
		Vector3D unitVectorXDirectionFloor = upperRight.subtract(upperLeft).normalize();
		Vector3D vectorFromUpperLeftToLowerLeft = lowerLeft.subtract(upperLeft);
		Vector3D unitVectorYDirectionFloor = vectorFromUpperLeftToLowerLeft.crossProduct(unitVectorXDirectionFloor)
				.normalize();
		Vector3D unitVectorZDirectionFloor = unitVectorXDirectionFloor.crossProduct(unitVectorYDirectionFloor)
				.normalize();

		// construct floor coordinate planes
		Plane xyPlaneFloor = new Plane(unitVectorZDirectionFloor, TOLERABLE_DISTANCE);
		double offset = xyPlaneFloor.getOffset(upperLeft);
		xyPlaneFloor = xyPlaneFloor.translate(unitVectorZDirectionFloor.scalarMultiply(offset));
		double zFloorCoordinate = xyPlaneFloor.getOffset(kinectPoint);

		Plane xzPlaneFloor = new Plane(unitVectorYDirectionFloor, TOLERABLE_DISTANCE);
		offset = xzPlaneFloor.getOffset(upperLeft);
		xzPlaneFloor = xzPlaneFloor.translate(unitVectorYDirectionFloor.scalarMultiply(offset));
		double yFloorCoordinate = xzPlaneFloor.getOffset(kinectPoint);

		Plane yzPlaneFloor = new Plane(unitVectorXDirectionFloor, TOLERABLE_DISTANCE);
		offset = yzPlaneFloor.getOffset(upperLeft);
		yzPlaneFloor = yzPlaneFloor.translate(unitVectorXDirectionFloor.scalarMultiply(offset));
		double xFloorCoordinate = yzPlaneFloor.getOffset(kinectPoint);

		Vector3D floorPoint = new Vector3D(xFloorCoordinate, yFloorCoordinate, zFloorCoordinate);
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
	 *         thirdComponent: altitude in millimeters above floor
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

		Vector3D floorPoint = convertFromKinectToFloorCoords(kinectPoint);
		double altitude = floorPoint.getY();

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
