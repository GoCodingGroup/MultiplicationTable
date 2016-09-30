package de.gocodinggroup.multiplicationtable.game.model.viewerentites;

import java.util.ArrayList;

import javafx.geometry.Point3D;

public class SensorDataConverter {
	private static final String ILLEGAL_ARGUMENT_EXCEPTION_MESSAGE = "The depthDataArrayLength is not a whole multiple of width";

	public static ArrayList<Point3D> convertSensorDataFromXYZToArrayList(float[] xyzData) {
		double x = Double.NaN;
		double y = Double.NaN;
		double z = Double.NaN;
		ArrayList<Point3D> arrayListPoint3D = new ArrayList<Point3D>();
		Point3D actPoint3D;
		for (int i = 0; i < xyzData.length; i++) {
			switch (i % 3) {
			case 0:
				x = (double) (xyzData[i]);
				break;
			case 1:
				y = (double) (xyzData[i]);
				break;
			case 2:
				z = (double) (xyzData[i]);
				actPoint3D = new Point3D(x, y, z);
				arrayListPoint3D.add(actPoint3D);
				break;
			}
		}
		return arrayListPoint3D;
	}

	public static ArrayList<Point3D> convertSensorDataFromDepthMapToArrayList(short[] depthFrame, int maxWidth) {
		if (depthFrame.length % maxWidth != 0) {
			throw new IllegalArgumentException(ILLEGAL_ARGUMENT_EXCEPTION_MESSAGE);
		}
		ArrayList<Point3D> arrayListPoint3D = new ArrayList<Point3D>();
		Point3D actPoint3D;
		int maxHeight = depthFrame.length / maxWidth;
		for (int i = 0; i < maxHeight; i++) {
			for (int j = 0; j < maxWidth; j++) {
				double z = (double) (depthFrame[i*maxWidth+j]);
				actPoint3D = new Point3D(i, j, z);
				arrayListPoint3D.add(actPoint3D);
			}
		}
		return arrayListPoint3D;
	}


}
