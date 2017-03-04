package de.gocodinggroup.multiplicationtable.coordinate.test;

import static org.junit.Assert.*;

import org.apache.commons.math3.geometry.euclidean.threed.*;
import org.junit.*;

import de.gocodinggroup.multiplicationtable.coordinate.*;

public class CoordinateConverterTest {

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

	@Test
	public void testConvertFromKinectToFloorCoords_FloorIsParallelToZXPlaneOfKinect() {
		/*
		//@formatter:off		   
		 upperLeft----------------upperRight
		     |                        |
		 lowerLeft----------------lowerRight
		//@formatter:on
		*/
		Vector3D upperLeft = new Vector3D(-1, -1, 1);
		Vector3D upperRight = new Vector3D(1, -1, 1);
		Vector3D lowerLeft = new Vector3D(-1, -1, 2);
		Vector3D lowerRight = new Vector3D(1, -1, 2);

		CoordinateConverter converter = new CoordinateConverter(upperLeft, upperRight, lowerLeft, lowerRight);

		// upperLeft
		Vector3D actualVector = converter.convertFromKinectToFloorCoords(upperLeft);
		Vector3D expectedVector = new Vector3D(0, 0, 0);
		assertEquals(expectedVector, actualVector);

		// upperRight
		actualVector = converter.convertFromKinectToFloorCoords(upperRight);
		expectedVector = new Vector3D(2, 0, 0);
		assertEquals(expectedVector, actualVector);

		// lowerRight
		actualVector = converter.convertFromKinectToFloorCoords(lowerRight);
		expectedVector = new Vector3D(2, 0, 1);
		assertEquals(expectedVector, actualVector);

		// lowerLeft
		actualVector = converter.convertFromKinectToFloorCoords(lowerLeft);
		expectedVector = new Vector3D(0, 0, 1);
		assertEquals(expectedVector, actualVector);
	}

	@Test
	public void testConvertFromKinectToFloorCoords_FloorIsNotParallelToZXPlaneOfKinect() {

		Vector3D upperLeft = new Vector3D(-1, 0, 0);
		Vector3D upperRight = new Vector3D(1, 0, 0);
		Vector3D lowerLeft = new Vector3D(-1, 1, 1);
		Vector3D lowerRight = new Vector3D(1, 1, 1);

		CoordinateConverter converter = new CoordinateConverter(upperLeft, upperRight, lowerLeft, lowerRight);

		// upperLeft
		Vector3D actualVector = converter.convertFromKinectToFloorCoords(upperLeft);
		Vector3D expectedVector = new Vector3D(0, 0, 0);
		assertEquals(expectedVector.getX(), actualVector.getX(), 0.000001);
		assertEquals(expectedVector.getY(), actualVector.getY(), 0.000001);
		assertEquals(expectedVector.getZ(), actualVector.getZ(), 0.000001);

		// upperRight
		actualVector = converter.convertFromKinectToFloorCoords(upperRight);
		expectedVector = new Vector3D(2, 0, 0);
		assertEquals(expectedVector.getX(), actualVector.getX(), 0.000001);
		assertEquals(expectedVector.getY(), actualVector.getY(), 0.000001);
		assertEquals(expectedVector.getZ(), actualVector.getZ(), 0.000001);

		// lowerRight
		actualVector = converter.convertFromKinectToFloorCoords(lowerRight);
		expectedVector = new Vector3D(2, 0, Math.sqrt(2));
		assertEquals(expectedVector.getX(), actualVector.getX(), 0.000001);
		assertEquals(expectedVector.getY(), actualVector.getY(), 0.000001);
		assertEquals(expectedVector.getZ(), actualVector.getZ(), 0.000001);

		// lowerLeft
		actualVector = converter.convertFromKinectToFloorCoords(lowerLeft);
		expectedVector = new Vector3D(0, 0, Math.sqrt(2));
		assertEquals(expectedVector.getX(), actualVector.getX(), 0.000001);
		assertEquals(expectedVector.getY(), actualVector.getY(), 0.000001);
		assertEquals(expectedVector.getZ(), actualVector.getZ(), 0.000001);
	}

	@Test
	public void testConvertFromKinectToBeamerCoords_UpperLeftHasBeamerCoordinates_0_0_0() {
		// Coordinates in Kinect coordinate system
		Vector3D upperLeft = new Vector3D(-1, 0, 0);
		Vector3D upperRight = new Vector3D(1, 0, 0);
		Vector3D lowerLeft = new Vector3D(-1, 0, 1);
		Vector3D lowerRight = new Vector3D(1, 0, 1);

		CoordinateConverter converter = new CoordinateConverter(upperLeft, upperRight, lowerLeft, lowerRight);

		Vector3D actualVector = converter.convertFromKinectToBeamerCoords(upperLeft);

		// Coordinates in beamer coordinates
		Vector3D expectedVector = new Vector3D(0, 0, 0);

		assertEquals(expectedVector.getX(), actualVector.getX(), 0.000001);
		assertEquals(expectedVector.getY(), actualVector.getY(), 0.000001);
		assertEquals(expectedVector.getZ(), actualVector.getZ(), 0.000001);
	}

	@Test
	public void testConvertFromKinectToBeamerCoords_UpperRightHasBeamerCoordinates_1_0_0() {
		// Coordinates in Kinect coordinate system
		Vector3D upperLeft = new Vector3D(-1, 0, 0);
		Vector3D upperRight = new Vector3D(1, 0, 0);
		Vector3D lowerLeft = new Vector3D(-1, 0, 1);
		Vector3D lowerRight = new Vector3D(1, 0, 1);

		CoordinateConverter converter = new CoordinateConverter(upperLeft, upperRight, lowerLeft, lowerRight);

		Vector3D actualVector = converter.convertFromKinectToBeamerCoords(upperRight);

		// Coordinates in beamer coordinates
		Vector3D expectedVector = new Vector3D(1, 0, 0);

		assertEquals(expectedVector.getX(), actualVector.getX(), 0.000001);
		assertEquals(expectedVector.getY(), actualVector.getY(), 0.000001);
		assertEquals(expectedVector.getZ(), actualVector.getZ(), 0.000001);
	}

	@Test
	public void testConvertFromKinectToBeamerCoords_LowerLeftHasBeamerCoordinates_0_1_0() {
		// Coordinates in Kinect coordinate system
		Vector3D upperLeft = new Vector3D(-1, 0, 0);
		Vector3D upperRight = new Vector3D(1, 0, 0);
		Vector3D lowerLeft = new Vector3D(-1, 0, 1);
		Vector3D lowerRight = new Vector3D(1, 0, 1);

		CoordinateConverter converter = new CoordinateConverter(upperLeft, upperRight, lowerLeft, lowerRight);

		Vector3D actualVector = converter.convertFromKinectToBeamerCoords(lowerLeft);

		// Coordinates in beamer coordinates
		Vector3D expectedVector = new Vector3D(0, 1, 0);

		assertEquals(expectedVector.getX(), actualVector.getX(), 0.000001);
		assertEquals(expectedVector.getY(), actualVector.getY(), 0.000001);
		assertEquals(expectedVector.getZ(), actualVector.getZ(), 0.000001);
	}

	@Test
	public void testConvertFromKinectToBeamerCoords_leftBoundaryAndRightBoundaryAreNotParallel_UpperLeftHasBeamerCoordinates_0_0_0() {
		// Coordinates in Kinect coordinate system
		Vector3D upperLeft = new Vector3D(-1, 0, 0);
		Vector3D upperRight = new Vector3D(1, 0, 0);
		Vector3D lowerLeft = new Vector3D(-2, 0, 1);
		Vector3D lowerRight = new Vector3D(2, 0, 1);

		CoordinateConverter converter = new CoordinateConverter(upperLeft, upperRight, lowerLeft, lowerRight);

		Vector3D actualVector = converter.convertFromKinectToBeamerCoords(upperLeft);

		// Coordinates in beamer coordinates
		Vector3D expectedVector = new Vector3D(0, 0, 0);

		assertEquals(expectedVector.getX(), actualVector.getX(), 0.000001);
		assertEquals(expectedVector.getY(), actualVector.getY(), 0.000001);
		assertEquals(expectedVector.getZ(), actualVector.getZ(), 0.000001);

	}

	@Test
	public void testConvertFromKinectToBeamerCoords_leftBoundaryAndRightBoundaryAreNotParallel_LowerLeftHasBeamerCoordinates_0_1_0() {
		// Coordinates in Kinect coordinate system
		Vector3D upperLeft = new Vector3D(-1, 0, 0);
		Vector3D upperRight = new Vector3D(1, 0, 0);
		Vector3D lowerLeft = new Vector3D(-2, 0, 1);
		Vector3D lowerRight = new Vector3D(2, 0, 1);

		CoordinateConverter converter = new CoordinateConverter(upperLeft, upperRight, lowerLeft, lowerRight);

		Vector3D actualVector = converter.convertFromKinectToBeamerCoords(lowerLeft);

		// Coordinates in beamer coordinates
		Vector3D expectedVector = new Vector3D(0, 1, 0);

		assertEquals(expectedVector.getX(), actualVector.getX(), 0.000001);
		assertEquals(expectedVector.getY(), actualVector.getY(), 0.000001);
		assertEquals(expectedVector.getZ(), actualVector.getZ(), 0.000001);

	}

	@Test
	public void testConvertFromKinectToBeamerCoords_leftBoundaryAndRightBoundaryAreNotParallel_LowerRightHasBeamerCoordinates_1_1_0() {
		// Coordinates in Kinect coordinate system
		Vector3D upperLeft = new Vector3D(-1, 0, 0);
		Vector3D upperRight = new Vector3D(1, 0, 0);
		Vector3D lowerLeft = new Vector3D(-2, 0, 1);
		Vector3D lowerRight = new Vector3D(2, 0, 1);

		CoordinateConverter converter = new CoordinateConverter(upperLeft, upperRight, lowerLeft, lowerRight);

		Vector3D actualVector = converter.convertFromKinectToBeamerCoords(lowerRight);

		// Coordinates in beamer coordinates
		Vector3D expectedVector = new Vector3D(1, 1, 0);

		assertEquals(expectedVector.getX(), actualVector.getX(), 0.000001);
		assertEquals(expectedVector.getY(), actualVector.getY(), 0.000001);
		assertEquals(expectedVector.getZ(), actualVector.getZ(), 0.000001);

	}

	@Test
	public void testConvertFromKinectToBeamerCoords_NoParalles_UpperRightHasBeamerCoordinates_0_1_0() {
		// Coordinates in Kinect coordinate system
		Vector3D upperLeft = new Vector3D(-2, 0, 4.2);
		Vector3D upperRight = new Vector3D(0, 0, 3);
		Vector3D lowerLeft = new Vector3D(0, 0, 7);
		Vector3D lowerRight = new Vector3D(2, 0, 4.2);

		CoordinateConverter converter = new CoordinateConverter(upperLeft, upperRight, lowerLeft, lowerRight);

		Vector3D actualVector = converter.convertFromKinectToBeamerCoords(upperRight);

		// Coordinates in beamer coordinates
		Vector3D expectedVector = new Vector3D(1, 0, 0);

		assertEquals(expectedVector.getX(), actualVector.getX(), 0.001);
		assertEquals(expectedVector.getY(), actualVector.getY(), 0.001);
		assertEquals(expectedVector.getZ(), actualVector.getZ(), 0.001);

	}

	@Test
	public void testConvertFromKinectToBeamerCoords_ZCoord() {

		// Coordinates in Kinect coordinate system
		Vector3D upperLeft = new Vector3D(-1, 0, 0);
		Vector3D upperRight = new Vector3D(1, 0, 0);
		Vector3D lowerLeft = new Vector3D(-1, 1, 1);
		Vector3D lowerRight = new Vector3D(1, 1, 1);

		CoordinateConverter converter = new CoordinateConverter(upperLeft, upperRight, lowerLeft, lowerRight);

		Vector3D actualVector = converter.convertFromKinectToBeamerCoords(lowerRight);

		// Coordinates in beamer coordinates
		Vector3D expectedVector = new Vector3D(1, 1, 0);

		assertEquals(expectedVector.getX(), actualVector.getX(), 0.001);
		assertEquals(expectedVector.getY(), actualVector.getY(), 0.001);
		assertEquals(expectedVector.getZ(), actualVector.getZ(), 0.001);
	}
}
