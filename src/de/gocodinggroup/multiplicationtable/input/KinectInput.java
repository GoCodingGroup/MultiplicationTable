package de.gocodinggroup.multiplicationtable.input;

import de.gocodinggroup.multiplicationtable.game.controller.*;
import de.gocodinggroup.multiplicationtable.util.*;
import de.gocodinggroup.multiplicationtable.util.events.*;
import edu.ufl.digitalworlds.j4k.*;

public class KinectInput extends J4KSDK implements InputProvider {
	// Threshold in centimeters TODO: change this somehow to a more intelligent
	// system
	private static final int JUMP_THRESHOLD = 15;

	// ID for the tracked player skeleton. If multiple people are being tracked
	// we can still find our actual player with this ID
	private static final int PLAYER_ID = 1337;

	/*
	 * This constant takes care of scaling kinect sensor data so that it fits
	 * within the playing field TODO: calculate this during calibration
	 */
	private static final int ROOM_SCALE_WIDTH = 3;
	private static final int ROOM_SCALE_HEIGHT = 2;

	private int playerAvatarX = 0;
	private int playerAvatarY = 0;

	// Keep track of which foot is on the ground
	private boolean isLeftFootOnGround = false;
	private boolean isRightFootOnGround = false;

	// When both feet are not on ground this is set to true
	private boolean isPlayerInJumpMotion = false;

	// Measured groundY
	private int groundY = 0;

	public KinectInput() {
		super();

		// Start sensory data stuff
		this.start(KinectInput.DEPTH | KinectInput.COLOR | KinectInput.SKELETON | KinectInput.XYZ
				| KinectInput.PLAYER_INDEX);

		// Make sure kinect will be disconnected properly on exit
		Runtime.getRuntime().addShutdownHook(new Thread(() -> this.stop()));
	}

	@Override
	public void onColorFrameEvent(byte[] data) {
		// Do nothing
	}

	@Override
	public void onDepthFrameEvent(short[] depth_frame, byte[] player_index, float[] XYZ, float[] UV) {
		// Do nothing
	}

	@Override
	public void onSkeletonFrameEvent(boolean[] flags, float[] positions, float[] orientations, byte[] state) {
		// Do we already have a tracked player that is playing ?!
		boolean playerExists = false;
		Skeleton skeleton;

		for (int i = 0; i < getSkeletonCountLimit(); i++) {
			skeleton = Skeleton.getSkeleton(i, flags, positions, orientations, state, this);
			if (skeleton.isTracked()) {
				if (skeleton.getPlayerID() == PLAYER_ID) {
					playerExists = true;
					doSkeletonCalculations(skeleton);
					break;
				}
				skeleton.setPlayerID(0);
			}
		}

		if (!playerExists) {
			for (int i = 0; i < getSkeletonCountLimit(); i++) {
				skeleton = Skeleton.getSkeleton(i, flags, positions, orientations, state, this);
				if (skeleton.isTracked()) {
					skeleton.setPlayerID(PLAYER_ID);
					doSkeletonCalculations(skeleton);
					break;
				}
			}
		}
	}

	private void doSkeletonCalculations(Skeleton playerSkeleton) {
		// Update player avatar position
		double[] playerPos = playerSkeleton.get3DJoint(Skeleton.SPINE_BASE);

		// Update Player avatar position
		this.playerAvatarX = (int) (playerPos[0] * 100 * ROOM_SCALE_WIDTH) + GameController.WORLD_WIDTH / 2;
		this.playerAvatarY = (int) (playerPos[2] * 100 * ROOM_SCALE_HEIGHT);

		// jump detection
		recognizeJumps(playerSkeleton);
	}

	private void recognizeJumps(Skeleton playerSkeleton) {
		double[] leftFootPos = playerSkeleton.get3DJoint(Skeleton.FOOT_LEFT);
		int leftY = (int) (leftFootPos[1] * 100);

		double[] rightFootPos = playerSkeleton.get3DJoint(Skeleton.FOOT_RIGHT);
		int rightY = (int) (rightFootPos[1] * 100);

		if (leftY < groundY)
			groundY = leftY;
		if (rightY < groundY)
			groundY = rightY;

		isLeftFootOnGround = true;
		isRightFootOnGround = true;

		if (leftY > getGroundPosForXZ(this.playerAvatarX, this.playerAvatarY) + JUMP_THRESHOLD)
			isLeftFootOnGround = false;

		if (rightY > getGroundPosForXZ(this.playerAvatarX, this.playerAvatarY) + JUMP_THRESHOLD)
			isRightFootOnGround = false;

		if (!isLeftFootOnGround && !isRightFootOnGround)
			isPlayerInJumpMotion = true;
		else {
			if (isPlayerInJumpMotion == true) {
				EventManager.dispatchEvent(new PlayerJumpedEvent(this.playerAvatarX, this.playerAvatarY));
			}

			isPlayerInJumpMotion = false;
		}
	}

	/**
	 * 
	 * Does Mathemagics and returns where the ground is expected to be @. This
	 * takes the calibrations done at the beginning of the game into account
	 * 
	 * TODO: Properly implement this method
	 * 
	 * @return
	 **/
	private int getGroundPosForXZ(int x, int z) {
		return this.groundY;
	}

	@Override
	public int getPlayerX() {
		return this.playerAvatarX;
	}

	@Override
	public int getPlayerY() {
		return this.playerAvatarY;
	}
}