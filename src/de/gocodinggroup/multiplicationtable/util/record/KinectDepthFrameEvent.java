package de.gocodinggroup.multiplicationtable.util.record;

/**
 * Depth frame representation
 * 
 * @author Dominik
 * @created 23.09.2016
 */
public class KinectDepthFrameEvent extends KinectFrameEvent {
	/** depth frame event parameters */
	private short[] depthFrame;
	private byte[] playerIndex;
	private float[] xyz;
	private float[] uv;

	/**
	 * Initialize a new DepthFrameEvent from given data. If no data is available
	 * for a particular input, it is save to specify "null"
	 * 
	 * @param depthFrame
	 * @param playerIndex
	 * @param xyz
	 * @param uv
	 */
	public KinectDepthFrameEvent(short[] depthFrame, byte[] playerIndex, float[] xyz, float[] uv) {
		super();
		this.depthFrame = depthFrame;
		this.playerIndex = playerIndex;
		this.xyz = xyz;
		this.uv = uv;
	}

	/**
	 * Initialize a new DepthFrameEvent from given data. If no data is available
	 * for a particular input, it is save to specify "null"
	 * 
	 * @param timestamp
	 * @param depthFrame
	 * @param playerIndex
	 * @param xyz
	 * @param uv
	 */
	public KinectDepthFrameEvent(long timestamp, short[] depthFrame, byte[] playerIndex, float[] xyz, float[] uv) {
		super(timestamp);
		this.depthFrame = depthFrame;
		this.playerIndex = playerIndex;
		this.xyz = xyz;
		this.uv = uv;
	}

	/**
	 * Retrieve the depthFrame information stored in this frame
	 * 
	 * @return null if no data is present
	 */
	public short[] getDepthFrame() {
		return depthFrame;
	}

	/**
	 * Retrieve the player index information stored in this frame
	 * 
	 * @return null if no data is present
	 */
	public byte[] getPlayerIndex() {
		return playerIndex;
	}

	/**
	 * Retrieve the xyz information stored in this frame
	 * 
	 * @return null if no data is present
	 */
	public float[] getXyz() {
		return xyz;
	}

	/**
	 * Retrieve the uv information stored in this frame
	 * 
	 * @return null if no data is present
	 */
	public float[] getUv() {
		return uv;
	}
}
