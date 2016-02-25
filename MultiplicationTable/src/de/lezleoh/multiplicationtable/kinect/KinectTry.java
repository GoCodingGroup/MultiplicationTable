package de.lezleoh.multiplicationtable.kinect;

import java.util.Date;

import edu.ufl.digitalworlds.j4k.J4KSDK;
import edu.ufl.digitalworlds.j4k.Skeleton;

public class KinectTry extends J4KSDK {
	int counter;
	long time;
	Skeleton skeleton;

	KinectTry() {
		super();
		counter = 0;
		time = 0;
		skeleton = new Skeleton();
	}

	@Override
	public void onColorFrameEvent(byte[] data) {
		/*
		 * if (counter == 0) time = new Date().getTime(); counter += 1; if
		 * (counter % 50 == 0) { System.out.println("c"); } else {
		 * System.out.print("c"); }
		 */
		// System.out.println("ColorFrameEvent: " + counter);

	}

	@Override
	public void onDepthFrameEvent(short[] depth_frame, byte[] player_index, float[] XYZ, float[] UV) {
		/*
		 * if (counter == 0) time = new Date().getTime(); counter += 1; if
		 * (counter % 50 == 0) { System.out.println("d"); } else {
		 * System.out.print("d"); }
		 */
		// System.out.println("DepthFrameEvent: " + counter);

	}

	@Override
	public void onSkeletonFrameEvent(boolean[] skeleton_tracked, float[] joint_position, float[] joint_orientation,
			byte[] joint_status) {
		if (counter == 0)
			time = new Date().getTime();
		counter += 1;

		for (int i = 0; i < this.getMaxNumberOfSkeletons(); i++) {
			skeleton = Skeleton.getSkeleton(i, skeleton_tracked, joint_position, joint_orientation, joint_status, this);
			if (skeleton.isTracked()) {
				
				Double xPosition = new Double(skeleton.get3DJointX(Skeleton.FOOT_RIGHT));
				Double yPosition = new Double(skeleton.get3DJointY(Skeleton.FOOT_RIGHT));
				Double zPosition = new Double(skeleton.get3DJointZ(Skeleton.FOOT_RIGHT));
				
				System.out.println("xPosition: " + xPosition);
				System.out.println("yPosition: " + yPosition);
				System.out.println("zPosition: " + zPosition);
				System.out.println();
				
				for (int j = 0; j < yPosition.intValue(); j++) {
					System.out.print("-");
				}
				System.out.println();
			}
		}

	}

	public static void main(String[] args) {
		if (System.getProperty("os.arch").toLowerCase().indexOf("64") < 0) {
			System.out.println("WARNING: You are running a 32bit version of Java.");
			System.out.println("This may reduce significantly the performance of this application.");
			System.out.println("It is strongly adviced to exit this program and install a 64bit version of Java.\n");
		}

		System.out.println("This program will run for about 20 seconds.");
		KinectTry kinectTry = new KinectTry();

		kinectTry.start(J4KSDK.COLOR | J4KSDK.DEPTH | J4KSDK.SKELETON);

		// Sleep for 20 seconds.
		try {
			Thread.sleep(40000);
		} catch (InterruptedException e) {
		}

		kinectTry.stop();
		System.out.println();
		System.out.println("FPS: " + kinectTry.counter * 1000.0 / (new Date().getTime() - kinectTry.time));
	}

}
