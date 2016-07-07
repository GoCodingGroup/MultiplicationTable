package de.gocodinggroup.multiplicationtable.kinect;

import de.gocodinggroup.multiplicationtable.kinect.tmp.*;
import edu.ufl.digitalworlds.j4k.*;

public class Kinect extends J4KSDK {
	/* TODO: tmp */
	private boolean isFootOnGround = false;
	private int highestFootPos, lowestFootPos;

	private Gui2DDemo g2ddemo;

	private VideoFrame videoTexture;

	public Kinect() {
		super();
		videoTexture = new VideoFrame();

		g2ddemo = new Gui2DDemo();
	}

	@Override
	public void onColorFrameEvent(byte[] data) {
		videoTexture.update(getColorWidth(), getColorHeight(), data);
	}

	@Override
	public void onDepthFrameEvent(short[] depth_frame, byte[] player_index, float[] XYZ, float[] UV) {
		// Do nothing
	}

	@Override
	public void onSkeletonFrameEvent(boolean[] flags, float[] positions, float[] orientations, byte[] state) {
		for (int i = 0; i < getSkeletonCountLimit(); i++) {
			Skeleton skeleton = Skeleton.getSkeleton(i, flags, positions, orientations, state, this);
			if (skeleton.isTracked()) {
				double[] pos = skeleton.get3DJoint(Skeleton.FOOT_RIGHT);
				int x = (int) (pos[0] * 100);
				int y = (int) (pos[1] * 100);
				int z = (int) (pos[2] * 100);

				double distance = Math.sqrt((highestFootPos - lowestFootPos) * (highestFootPos - lowestFootPos));
				if (y < highestFootPos - distance * 0.8) {
					if (isFootOnGround == false)
						System.out.println(System.currentTimeMillis() + ": FOOT STOMPED!!!");

					isFootOnGround = true;
				} else {
					isFootOnGround = false;
				}

				g2ddemo.setX(x);
				g2ddemo.setY(y);
				g2ddemo.setZ(z);
				g2ddemo.setHeightMax(highestFootPos);
				g2ddemo.setHeightMin(lowestFootPos);
			}
		}
	}
}
