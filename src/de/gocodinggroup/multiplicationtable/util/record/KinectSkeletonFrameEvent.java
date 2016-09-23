package de.gocodinggroup.multiplicationtable.util.record;

/**
 * Skeleton Frame event representation
 * 
 * @author Dominik
 * @created 23.09.2016
 */
public class KinectSkeletonFrameEvent extends KinectFrameEvent {
	/** skeleton frame event */
	private boolean[] flags;
	private float[] positions;
	private float[] orientations;
	private byte[] state;

	public KinectSkeletonFrameEvent(boolean[] flags, float[] positions, float[] orientations, byte[] state) {
		super();
		this.flags = flags;
		this.positions = positions;
		this.orientations = orientations;
		this.state = state;
	}

	public KinectSkeletonFrameEvent(long timestamp, boolean[] flags, float[] positions, float[] orientations,
			byte[] state) {
		super(timestamp);
		this.flags = flags;
		this.positions = positions;
		this.orientations = orientations;
		this.state = state;
	}

	public boolean[] getFlags() {
		return flags;
	}

	public float[] getPositions() {
		return positions;
	}

	public float[] getOrientations() {
		return orientations;
	}

	public byte[] getState() {
		return state;
	}
}
