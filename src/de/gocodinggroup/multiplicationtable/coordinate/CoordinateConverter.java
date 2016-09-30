package de.gocodinggroup.multiplicationtable.coordinate;

import org.apache.commons.math3.geometry.euclidean.threed.Rotation;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

public class CoordinateConverter {
	/**
	 * Converts coordinates based on Kinects coordinate system into the
	 * playground or screen coordinate system
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
	 * SCREEN COORDINATE SYSTEM
	 * 
	 * The image of the beamer is distorted. If the beamer is well adjusted, the
	 * outline of the projection is trapezoidal. The vertex of the trapezoid
	 * which is placed on the left side (looking towards to the beamer) and is
	 * closer to the beamer is called UPPER-LEFT vertex. The remoter vertex on
	 * the left side (looking towards the beamer) is called LOWER-LEFT vertex.
	 * The two other vertexes are named correspondingly UPPER-RIGHT and
	 * LOWER-RIGHT vertex. The point in the middle of the upper-left vertex and
	 * the upper-right vertex is called UPPER-MIDDLE. The three Components of
	 * the point in screen coordinates are defined as follows. firstComponent:
	 * fractionOfScreenWidth, secondComponent: fractionOfScreenHeight,
	 * thirdComponent: altitude in Millimeters above floor
	 * 
	 * 
	 * 
	 * PLAYGROUND COORDINATE SYSTEM
	 * 
	 * The origin of the playground coordinate system is identical to the point
	 * upper-middle described above. The z-Axis starts at upper-middle and runs
	 * through the point lower-middle. The x-Axis starts at upper-middle and
	 * runs through upper-right. The y-Axis is vertical to the ground. The
	 * distance is expressed in millimeters.
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
	 * Converts coordinates based on Kinects coordinate system into the
	 * playground coordinate system
	 * 
	 * @param kinectPoint:
	 *            the point in Kinect coordinates
	 * @return corresponding point in playground coordinates
	 */
	public Vector3D convertFromKinectToFloorCoords(Vector3D kinectPoint) {
		// move to origin
		Vector3D playGroundPoint = kinectPoint.subtract(upperMiddle);

		// define rotation
		Vector3D zAxisKinect = new Vector3D(0, 0, 1);
		Vector3D zAxisPlayGround = lowerMiddle.subtract(upperMiddle);
		Rotation rotation = new Rotation(zAxisPlayGround, zAxisKinect);
		// rotate
		playGroundPoint = rotation.applyTo(playGroundPoint);

		return playGroundPoint;
	}

	/**
	 * Converts coordinates based on Kinects coordinate system into the screen
	 * coordinate system
	 * 
	 * @param kinectPoint:
	 *            the point in Kinect coordinates
	 * @return corresponding point in screen coordinates firstComponent:
	 *         fractionOfScreenWidth, secondComponent: fractionOfScreenHeight,
	 *         thirdComponent: altitude in Millimeters above floor
	 */
	public Vector3D convertFromKinectToBeamerCoords(Vector3D kinectPoint) {
		// @TODO works only if the beamer's projection isn't distorted at all.
		// The projection must be an rectangle. Distortion must be taken in
		// account. Implementation should work for non trapezoidal projection as
		// well.
		Vector3D playGroundPoint = convertFromKinectToFloorCoords(kinectPoint);
		Vector3D shiftVector = upperMiddle.subtract(upperLeft);
		Vector3D intermediateVector = playGroundPoint.add(shiftVector);

		double distanceUpperLeftToUpperRight = Vector3D.distance(upperLeft, upperRight);
		double fractionOfScreenWidth = intermediateVector.getX() / distanceUpperLeftToUpperRight;

		double distanceUpperLeftToLowerLeft = Vector3D.distance(upperLeft, lowerLeft);
		double fractionOfScreenHeight = intermediateVector.getZ() / distanceUpperLeftToLowerLeft;

		Vector3D screenPoint = new Vector3D(fractionOfScreenWidth, fractionOfScreenHeight, intermediateVector.getY());

		return screenPoint;
	}

}
