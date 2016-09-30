package de.gocodinggroup.multiplicationtable.util.events;

import java.util.ArrayList;

import de.gocodinggroup.multiplicationtable.game.model.viewerentites.SensorDataConverter;
import de.gocodinggroup.multiplicationtable.util.Event;
import javafx.geometry.Point3D;

/**
 * Event sent when KinectInput.onDepthFrameEvent() is executed
 * 
 * @author Martin
 *
 */
public class XYZDataEvent extends Event {
	private float[] xyzData;

	public XYZDataEvent(float[] xyzData) {
		super();
		this.xyzData = xyzData;
	}

	/**
	 * @return the xyzData provided by kinect sensor
	 */
	public float[] getXYZData() {
		return this.xyzData;
	}
	
	/** 
	 * @return the xyzData provided by kinect sensor as ArrayList of 3DPoints
	 */
	public ArrayList<Point3D> getXYZDataAsArrayListOf3DPoints() {
		return SensorDataConverter.convertSensorDataFromXYZToArrayList(xyzData);
	}
}
