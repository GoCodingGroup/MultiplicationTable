package de.gocodinggroup.multiplicationtable.grounddetection.test;

import static org.junit.Assert.*;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.junit.Test;

import de.gocodinggroup.multiplicationtable.grounddetection.PlaneApproximationImplXYZ;

public class PlaneApproximationImplXYZTest {

	@Test
	public void testGetNormal() {
		//@formatter:off
		float[] rawData = 
			{ 0, 0, 1, 
			  0, 1, 1, 
			  1, 0, 1 };
		//@formatter:on
		PlaneApproximationImplXYZ planeApprox = new PlaneApproximationImplXYZ(rawData);

		Vector3D expectedVector = new Vector3D(0.0, 0.0, -1.0);
		Vector3D actualVector3D = planeApprox.getNormal();

		assertEquals(expectedVector, actualVector3D);
	}

	@Test
	public void testGetPointOnPlane() {
		//@formatter:off
		float[] rawData = 
			{ 0, 0, 1, 
			  0, 1, 1, 
			  1, 0, 1 };
		//Hesse Form: 0*x + 0*y + 1*z = 1
		//@formatter:on

		PlaneApproximationImplXYZ planeApprox = new PlaneApproximationImplXYZ(rawData);

		Vector3D actualPointOnPlane = planeApprox.getPointOnPlane();
		// to test, if a point lies on the plane, insert the point in hesse form
		// of plane
		assertEquals(1, actualPointOnPlane.getX() * 0 + actualPointOnPlane.getY() * 0 + actualPointOnPlane.getZ() * 1,
				0.001);

	}

}
