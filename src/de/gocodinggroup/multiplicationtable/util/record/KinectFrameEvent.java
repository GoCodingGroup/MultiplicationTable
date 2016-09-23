package de.gocodinggroup.multiplicationtable.util.record;

import java.util.*;

import de.gocodinggroup.multiplicationtable.util.*;

/**
 * Container class for all kinect data in a frame
 * 
 * @author Dominik
 * @created 23.09.2016
 */
public abstract class KinectFrameEvent extends Event {
	/** timestamp of this frame */
	private long timestamp;

	public KinectFrameEvent() {
		this(new Date().getTime());
	}

	public KinectFrameEvent(long timestamp) {
		this.timestamp = timestamp;
	}

	/**
	 * Retrieve timestamp at which this frame occured
	 * 
	 * @return
	 */
	public long getTimestamp() {
		return timestamp;
	}
}
