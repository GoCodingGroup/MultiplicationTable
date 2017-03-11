package de.gocodinggroup.multiplicationtable.util.events;

import java.util.*;

import de.gocodinggroup.multiplicationtable.game.model.viewerentites.*;
import de.gocodinggroup.util.*;
import javafx.geometry.*;

public class DepthDataEvent extends Event {
	private short[] depthFrame;
	private int maxWidth;

	public DepthDataEvent(short[] depthFrame, int maxWidth) {
		super();
		this.maxWidth = maxWidth;
		this.depthFrame = depthFrame;
	}

	public short[] getDepthFrame() {
		return this.depthFrame;
	}

	public ArrayList<Point3D> getDepthFrameAsArrayListOf3DPoints() {
		return SensorDataConverter.convertSensorDataFromDepthMapToArrayList(depthFrame, maxWidth);
	}

	public int getMaxWidth() {
		return maxWidth;
	}

}
