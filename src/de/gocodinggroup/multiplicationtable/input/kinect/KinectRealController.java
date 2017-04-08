package de.gocodinggroup.multiplicationtable.input.kinect;

import de.gocodinggroup.kinectdatarecorder.events.*;
import de.gocodinggroup.util.*;
import edu.ufl.digitalworlds.j4k.*;

/**
 * Class for receiving kinect input and putting it
 * 
 * @author Dominik
 *
 */
public class KinectRealController extends J4KSDK implements KinectControllerInterface {
	/** Lock object used to implement the "StartAndWait()" functionality */
	private Object lock;

	public KinectRealController() {
		super();

		// Initialize lock
		this.lock = new Object();

		// For some reason, this is the only resolution that kinect 2 can deal
		// with, at least through this API
		this.setDepthResolution(512, 424);

		// Make sure kinect will be disconnected properly on exit
		Runtime.getRuntime().addShutdownHook(new Thread(() -> this.stop()));
	}

	@Override
	public void onColorFrameEvent(byte[] data) {
		// Data started coming, therefore stop waiting.
		if (this.lock != null) {
			synchronized (this.lock) {
				this.lock.notifyAll();
				this.lock = null;
			}
		}

		// Dispatch color frame event
		EventManager.dispatchAndWait(new KinectColorFrameEvent(data));
	}

	@Override
	public void onDepthFrameEvent(short[] depthFrame, byte[] playerIndex, float[] xyz, float[] uv) {
		// Data started coming, therefore stop waiting.
		if (this.lock != null) {
			synchronized (this.lock) {
				this.lock.notifyAll();
				this.lock = null;
			}
		}

		// Dispatch depth frame event
		EventManager.dispatchAndWait(new KinectDepthFrameEvent(depthFrame, xyz));
	}

	@Override
	public void onSkeletonFrameEvent(boolean[] flags, float[] positions, float[] orientations, byte[] state) {
		// Data started coming, therefore stop waiting.
		if (this.lock != null) {
			synchronized (this.lock) {
				this.lock.notifyAll();
				this.lock = null;
			}
		}

		// Dispatch skeleton frame event
		EventManager.dispatchAndWait(new KinectSkeletonFrameEvent(flags, positions, orientations, state));
	}

	public int getSkeletonCountLimit() {
		return super.getSkeletonCountLimit();
	}

	@Override
	public byte getKinectType() {
		return this.getDeviceType();
	}

	@Override
	public boolean startAndWait(int flags) throws InterruptedException {
		boolean returnValue = this.start(flags);
		synchronized (this.lock) {
			this.lock.wait();
		}
		return returnValue;
	}
}