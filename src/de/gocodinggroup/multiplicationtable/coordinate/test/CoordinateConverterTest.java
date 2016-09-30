package de.gocodinggroup.multiplicationtable.coordinate.test;

import static org.junit.Assert.*;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.junit.Test;

import de.gocodinggroup.multiplicationtable.coordinate.CoordinateConverter;

public class CoordinateConverterTest {

	//@formatter:off
			/*

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
			
			*/
			//@formatter:on

	@Test
	public void testGetAbsoluteCoordinatesWithOriginUpperMiddle_AxesParallel() {
		Vector3D upperLeft = new Vector3D(-1, -1, 1);
		Vector3D upperRight = new Vector3D(1, -1, 1);
		Vector3D lowerLeft = new Vector3D(-1, -1, 2);
		Vector3D lowerRight = new Vector3D(1, -1, 2);

		CoordinateConverter converter = new CoordinateConverter(upperLeft, upperRight, lowerLeft, lowerRight);

		Vector3D upperMiddle = getMiddle(upperLeft, upperRight);

		Vector3D actualVector = converter.convertFromKinectToFloorCoords(upperMiddle);
		Vector3D expectedVector = new Vector3D(0, 0, 0);
		assertEquals(expectedVector, actualVector);

	}

	private Vector3D getMiddle(Vector3D vector1, Vector3D vector2) {
		Vector3D middle = vector1.add(vector2);
		middle = middle.scalarMultiply(.5);
		return middle;
	}

	@Test
	public void testGetAbsoluteCoordinatesWithOriginUpperMiddle_EqualOrigins_RotationAroundXaxis() {
		// Coordinates in Kinect coordinate system
		Vector3D upperLeft = new Vector3D(-1, 0, 0);
		Vector3D upperRight = new Vector3D(1, 0, 0);
		Vector3D lowerLeft = new Vector3D(-1, -1, 1);
		Vector3D lowerRight = new Vector3D(1, -1, 1);

		Vector3D lowerMiddle = getMiddle(lowerLeft, lowerRight);

		CoordinateConverter converter = new CoordinateConverter(upperLeft, upperRight, lowerLeft, lowerRight);

		Vector3D actualVector = converter.convertFromKinectToFloorCoords(lowerMiddle);

		// Coordinates in playground coordinate system
		Vector3D expectedVector = new Vector3D(0, 0, Math.sqrt(2));
		assertEquals(expectedVector.getX(), actualVector.getX(), 0.000001);
		assertEquals(expectedVector.getY(), actualVector.getY(), 0.000001);
		assertEquals(expectedVector.getZ(), actualVector.getZ(), 0.000001);

	}

	@Test
	public void testGetRelativeCoordinatesWithOriginUpperLeft_UpperLeftHasCoordinates_0_0_0() {
		// Coordinates in Kinect coordinate system
		Vector3D upperLeft = new Vector3D(-1, 0, 0);
		Vector3D upperRight = new Vector3D(1, 0, 0);
		Vector3D lowerLeft = new Vector3D(-1, 0, 1);
		Vector3D lowerRight = new Vector3D(1, 0, 1);

		CoordinateConverter converter = new CoordinateConverter(upperLeft, upperRight, lowerLeft, lowerRight);

		Vector3D actualVector = converter.convertFromKinectToBeamerCoords(upperLeft);

		// Coordinates in screen coordinates
		Vector3D expectedVector = new Vector3D(0, 0, 0);

		assertEquals(expectedVector.getX(), actualVector.getX(), 0.000001);
		assertEquals(expectedVector.getY(), actualVector.getY(), 0.000001);
		assertEquals(expectedVector.getZ(), actualVector.getZ(), 0.000001);
	}

	@Test
	public void testGetRelativeCoordinatesWithOriginUpperLeft_UpperRightHasCoordinates_1_0_0() {
		// Coordinates in Kinect coordinate system
		Vector3D upperLeft = new Vector3D(-1, 0, 0);
		Vector3D upperRight = new Vector3D(1, 0, 0);
		Vector3D lowerLeft = new Vector3D(-1, 0, 1);
		Vector3D lowerRight = new Vector3D(1, 0, 1);

		CoordinateConverter converter = new CoordinateConverter(upperLeft, upperRight, lowerLeft, lowerRight);

		Vector3D actualVector = converter.convertFromKinectToBeamerCoords(upperRight);

		// Coordinates in screen coordinates
		Vector3D expectedVector = new Vector3D(1, 0, 0);

		assertEquals(expectedVector.getX(), actualVector.getX(), 0.000001);
		assertEquals(expectedVector.getY(), actualVector.getY(), 0.000001);
		assertEquals(expectedVector.getZ(), actualVector.getZ(), 0.000001);
	}
	
	@Test
	public void testGetRelativeCoordinatesWithOriginUpperLeft_LowerLeftHasCoordinates_0_1_0() {
		// Coordinates in Kinect coordinate system
		Vector3D upperLeft = new Vector3D(-1, 0, 0);
		Vector3D upperRight = new Vector3D(1, 0, 0);
		Vector3D lowerLeft = new Vector3D(-1, 0, 1);
		Vector3D lowerRight = new Vector3D(1, 0, 1);

		CoordinateConverter converter = new CoordinateConverter(upperLeft, upperRight, lowerLeft, lowerRight);

		Vector3D actualVector = converter.convertFromKinectToBeamerCoords(upperRight);

		// Coordinates in screen coordinates
		Vector3D expectedVector = new Vector3D(1, 0, 0);

		assertEquals(expectedVector.getX(), actualVector.getX(), 0.000001);
		assertEquals(expectedVector.getY(), actualVector.getY(), 0.000001);
		assertEquals(expectedVector.getZ(), actualVector.getZ(), 0.000001);
	}
}
