package de.gocodinggroup.multiplicationtable.grounddetection;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression;

public class PlaneApproximationImplXYZ extends OLSMultipleLinearRegression implements PlaneApproximation {
	private static int DIMENSION = 3;
	private double[] sample;
	private double[][] model;

	public PlaneApproximationImplXYZ(float[] xyzData)  {
		sample = new double[xyzData.length/DIMENSION];
		model = new double[xyzData.length/DIMENSION][2];
		createSampleAndModel(xyzData);
	}

	private void createSampleAndModel(float[] xyzData) {
		// use z-values as sample
		// use x,y values as model
		//@formatter:off
		/*
		 * x 0 -> [0, 0] = [0/3, 0]
		 * y 1 -> [0, 1] = [1/3, 1]
		 * z 2 -> [0]    = [2/3]
		 * x 3 -> [1, 0] = [3/3, 0]
		 * y 4 -> [1, 1] = [4/3, 1]
		 * z 5 -> [1]    = [5/3]
		 * ...
		 * x k  -> [k/3, 0]
		 * y k+1 ->[k/3, 1]
		 * z k+2 ->[k/3]
		 */
		//@formatter:on
		
		for (int k = 0; k < xyzData.length; k++) {
			switch (k % DIMENSION) {
			case 0:
				model[k / DIMENSION][0] = xyzData[k];
				break;
			case 1:
				model[k / DIMENSION][1] = xyzData[k];
				break;
			case 2:
				sample[k / DIMENSION] = xyzData[k];
				break;
			}
		}
		newSampleData(sample, model);
		setNoIntercept(false);
		
	}

	@Override
	public Vector3D getNormal() {
		RealVector regressionCoefficients = this.calculateBeta();
		/*
		 * z(x,y)= b0 * 1 + b1 * x + b2 * y
		 * 
		 * Hesse form: b1 * x + b2 * y - z = -b0
		 * 
		 * Normal Vektor: (b1, b2, -1) pointOnPlane: (0, 0, b0)
		 * 
		 */
		double xComponent = regressionCoefficients.getEntry(1);
		double yComponent = regressionCoefficients.getEntry(2);
		double zComponent = -1;
		
		RealVector normal = new ArrayRealVector();
		normal = normal.append(xComponent);
		normal = normal.append(yComponent);
		normal = normal.append(zComponent);
		normal = normal.unitVector();
		

		Vector3D normalVector = new Vector3D(xComponent, yComponent, zComponent);
		return normalVector;
	}

	@Override
	public Vector3D getPointOnPlane() {
		RealVector regressionCoefficients = this.calculateBeta();
		/*
		 * z(x,y)= b0 * 1 + b1 * x + b2 * y
		 * 
		 * Hesse form: b1 * x + b2 * y - z = -b0
		 * 
		 * Normal Vektor: (b1, b2, -1) pointOnPlane: (0, 0, b0)
		 * 
		 */
		double xComponent = 0;
		double yComponent = 0;
		double zComponent = regressionCoefficients.getEntry(0);
		
		RealVector normal = new ArrayRealVector();
		normal = normal.append(xComponent);
		normal = normal.append(yComponent);
		normal = normal.append(zComponent);
		
		Vector3D pointOnPlane = new Vector3D(xComponent, yComponent, zComponent);
		return pointOnPlane;
	}
	

}
