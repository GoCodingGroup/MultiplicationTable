package de.gocodinggroup.multiplicationtable.input.kinect;

import java.io.*;
import java.nio.*;
import java.util.*;

import de.gocodinggroup.multiplicationtable.game.controller.*;
import de.gocodinggroup.multiplicationtable.util.*;
import de.gocodinggroup.multiplicationtable.util.record.*;

/**
 * Class responsible for playing back kinect data
 * 
 * @author Dominik
 * @created 26.09.2016
 */
public class KinectPlaybackController implements KinectControllerInterface, Runnable {
	/** File input */
	private BufferedInputStream fileIn = null;

	/** Information packed in file header */
	private int kinectType = 0;
	private int kinectDepthWidth = 0;
	private int kinectDepthHeight = 0;
	private int maxSkeletonAmount = 0;
	private int dataCompressorType = 0;

	/** Data compressor used to extract data from this file */
	private KinectDataCompressor compressor;

	/** Kinect playback thread */
	private Thread kinectPlaybackThread = null;
	private boolean playingData = false;

	private String playbackFilePath;

	public KinectPlaybackController(String playbackFilePath) throws IOException {
		this.fileIn = new BufferedInputStream(new FileInputStream(playbackFilePath));
		this.playbackFilePath = playbackFilePath;

		// Parse file header
		this.parseFileHeader();

		// Allocate data compressor based on compressor type
		switch (this.dataCompressorType) {
			case 1337:
				this.compressor = new StandardKinectDataCompressor();
				if (this.compressor.getType() != 1337)
					GameController.LOGGER.error("Wrong kinect data compressor type selected!");
				break;
			default:
				GameController.LOGGER.error("Unrecognized kinect data compressor type " + this.dataCompressorType);
				break;
		}
	}

	@Override
	public void run() {
		synchronized (this.kinectPlaybackThread) {
			this.kinectPlaybackThread.notifyAll();
		}

		while (this.playingData) {
			// Retrieve next events
			List<KinectFrameEvent> events = null;
			try {
				events = this.compressor.getNextChunks(this.fileIn);
			} catch (IOException e) {
				e.printStackTrace();
				GameController.LOGGER.warn("Could not retrieve frames from capture file. Exiting");
				return;
			} catch (CaptureResetStreamException e) {
				// TODO: hacky solution :( Will cut of left over frames at the end
				// probably
				try {
					this.fileIn.close();
					this.fileIn = new BufferedInputStream(new FileInputStream(playbackFilePath));
					this.fileIn.read(new byte[6 * 4]);
					GameController.LOGGER.info("Capture file end reached. Restarting playback from begining.");
					continue;
				} catch (IOException e1) {
					// This should not occur
				}

			}
			if (events == null || events.size() <= 0) continue;

			// Sort for event processing
			Collections.sort(events, new Comparator<KinectFrameEvent>() {
				@Override
				public int compare(KinectFrameEvent event1, KinectFrameEvent event2) {
					int result = 0;
					if (event1.getTimestamp() < event2.getTimestamp()) result = -1;
					else if (event1.getTimestamp() > event2.getTimestamp()) result = 1;
					return result;
				}
			});

			long basetime = events.get(0).getTimestamp();
			for (KinectFrameEvent event : events) {
				try {
					Thread.sleep((event.getTimestamp() - basetime));
				} catch (Exception e) {
					// Ignore
				}
				basetime = event.getTimestamp();

				EventManager.dispatchEventAndWait(event);
				basetime = event.getTimestamp();
			}
		}
	}

	private void parseFileHeader() throws IOException {
		// Read header
		byte[] header = new byte[6 * 4];
		fileIn.read(header);
		ByteBuffer headerBuffer = ByteBuffer.wrap(header);

		// Check file header
		for (int i = 0; i < KinectDataRecorder.FILE_HEADER.length; i++)
			if (headerBuffer.get() != KinectDataRecorder.FILE_HEADER[i])
				throw new IOException("Wrong file or file corrupt");

		// Convert
		this.kinectType = headerBuffer.getInt();
		this.kinectDepthWidth = headerBuffer.getInt();
		this.kinectDepthHeight = headerBuffer.getInt();
		this.maxSkeletonAmount = headerBuffer.getInt();
		this.dataCompressorType = headerBuffer.getInt();
	}

	@Override
	public int getMaxSkeletonAmount() {
		return this.maxSkeletonAmount;
	}

	@Override
	public byte getKinectType() {
		return (byte) this.kinectType;
	}

	@Override
	public int getDepthWidth() {
		return this.kinectDepthWidth;
	}

	@Override
	public int getDepthHeight() {
		return this.kinectDepthHeight;
	}

	@Override
	public boolean startAndWait(int flags) throws InterruptedException {
		GameController.LOGGER.warn("Currently ignoring flags for startAndWait() in KinectPlaybackController");

		if (!this.playingData) {
			this.playingData = true;
			this.kinectPlaybackThread = new Thread(this);
			this.kinectPlaybackThread.start();

			synchronized (this.kinectPlaybackThread) {
				this.kinectPlaybackThread.wait();
			}
		}

		return true;
	}

	@Override
	public void stop() {
		if (this.playingData) {
			this.playingData = false;
			try {
				this.kinectPlaybackThread.join();
			} catch (InterruptedException e) {
				// Ignore
			}
			this.kinectPlaybackThread = null;
		}
	}

}
