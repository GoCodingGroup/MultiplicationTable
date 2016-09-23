package de.gocodinggroup.multiplicationtable.input.kinect;

/**
 * Proxy interface for retrieving information about the connected kinect
 * 
 * @author Dominik
 * @created 23.09.2016
 */
public interface KinectControllerInterface {
	/**
	 * Retrieves the skeleton count limit for the current kinect device
	 * 
	 * @return
	 */
	public int getMaxSkeletonAmount();

	/**
	 * Retrieves the kinect device type
	 * 
	 * @return
	 */
	public byte getKinectType();

	/**
	 * Retrieve depth frame width
	 * 
	 * @return
	 */
	public int getDepthWidth();

	/**
	 * Retrieve depth frame height
	 */
	public int getDepthHeight();

	/**
	 * Starts the kinect and waits until first data has been output
	 * 
	 * @return
	 */
	public boolean startAndWait(int flags) throws InterruptedException;

	/**
	 * Stops outputting data
	 */
	public void stop();
}