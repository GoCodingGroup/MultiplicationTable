package de.gocodinggroup.multiplicationtable.input.kinect;

import de.gocodinggroup.multiplicationtable.game.controller.GameController;
import de.gocodinggroup.multiplicationtable.input.InputParser;
import de.gocodinggroup.multiplicationtable.util.EventManager;
import de.gocodinggroup.multiplicationtable.util.events.DepthDataEvent;
import de.gocodinggroup.multiplicationtable.util.events.PlayerJumpedEvent;
import de.gocodinggroup.multiplicationtable.util.events.XYZDataEvent;
import de.gocodinggroup.multiplicationtable.util.record.KinectDepthFrameEvent;
import de.gocodinggroup.multiplicationtable.util.record.KinectSkeletonFrameEvent;
import edu.ufl.digitalworlds.j4k.Skeleton;

public class KinectInputParser implements InputParser {
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

	private KinectControllerInterface kinectInterface;

	// counter Limiting XYZDATAEvents
	private int counter = 1;
	private static int COUNTER_TRESHOLD = 10;

	public KinectInputParser(KinectControllerInterface kinectInterface) {
		this.kinectInterface = kinectInterface;

		EventManager.registerEventListenerForEvent(KinectDepthFrameEvent.class, e -> {
			KinectDepthFrameEvent event = (KinectDepthFrameEvent) e;
			this.onDepthFrameEvent(event.getDepthFrame(), event.getPlayerIndex(), event.getXyz(), event.getUv());
		});
		EventManager.registerEventListenerForEvent(KinectSkeletonFrameEvent.class, e -> {
			KinectSkeletonFrameEvent event = (KinectSkeletonFrameEvent) e;
			this.onSkeletonFrameEvent(event.getFlags(), event.getPositions(), event.getOrientations(),
					event.getState());
		});
	}

	public void onDepthFrameEvent(short[] depth_frame, byte[] player_index, float[] XYZ, float[] UV) {

		if (counter % COUNTER_TRESHOLD == 0) {
			EventManager.dispatchEventAndWait(new XYZDataEvent(XYZ));
			EventManager.dispatchEvent(new DepthDataEvent(depth_frame, this.kinectInterface.getDepthWidth()));
			System.out.println("new Frame: ");
			System.out.print(" Heigth: " + this.kinectInterface.getDepthHeight());
			System.out.print(" Width: " + this.kinectInterface.getDepthWidth());
		}
		counter++;
	}

	public void onSkeletonFrameEvent(boolean[] flags, float[] positions, float[] orientations, byte[] state) {
		// Do we already have a tracked player that is playing ?!
		boolean playerExists = false;
		Skeleton skeleton;

		for (int i = 0; i < this.kinectInterface.getMaxSkeletonAmount(); i++) {
			skeleton = Skeleton.getSkeleton(i, flags, positions, orientations, state,
					this.kinectInterface.getKinectType());
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
			for (int i = 0; i < this.kinectInterface.getMaxSkeletonAmount(); i++) {
				skeleton = Skeleton.getSkeleton(i, flags, positions, orientations, state,
						this.kinectInterface.getKinectType());
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
				// TODO: this does not seem to work with playback data :O
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