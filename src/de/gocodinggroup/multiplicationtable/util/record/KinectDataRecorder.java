package de.gocodinggroup.multiplicationtable.util.record;

import java.io.*;
import java.nio.*;
import java.util.*;

import de.gocodinggroup.multiplicationtable.game.controller.*;
import de.gocodinggroup.multiplicationtable.input.kinect.*;
import de.gocodinggroup.multiplicationtable.util.*;

/**
 * Class responsible for recording data from a kinect data source to a file
 * 
 * @author Dominik
 * @created 23.09.2016
 */
public class KinectDataRecorder implements Runnable {
	/** Unique header binary encoding used to identify this file (hex mode) */
	public static final byte[] FILE_HEADER = new byte[] { (byte) 0xEC, (byte) 0x83, (byte) 0xB0, (byte) 0xB0 };

	/** Amount of frames that compose one chunk. */
	private static final int FRAME_CHUNK_SIZE = 60;

	/** Maximum file size in bytes (400MB) TODO: impelement */
	private static final long MAX_FILE_SIZE_IN_BYTES = 400 * 1024 * 1024;

	/** Frame events buffer */
	private List<KinectFrameEvent> frameEventsBuffer = null;

	/** Ouput file streams */
	private File file = null;
	private FileOutputStream fileOut = null;

	/** Controller interface necessary for retrieving header information */
	KinectControllerInterface kinectControllerInterface;

	/** File write thread */
	private Thread fileWriteThread = null;

	/** Which data compressor we should use */
	private KinectDataCompressor dataCompressor = null;

	/** Whether playback writing is done */
	private boolean isDone = false;

	/**
	 * Create new KinectDataRecorder that will write to the file specified
	 * 
	 * @param recordFilePath
	 *           file to write to
	 * @throws IOException
	 *            if an I/O error occurs while writing stream header
	 * @throws FileNotFoundException
	 *            if the file exists but is a directory rather than a regular
	 *            file, does not exist but cannot be created, or cannot be opened
	 *            for any other reason
	 */
	public KinectDataRecorder(KinectControllerInterface controller, KinectDataCompressor dataCompressor,
			String recordFilePath) throws IOException, FileNotFoundException {
		// Initialize and open file
		this.isDone = false;
		this.fileWriteThread = new Thread(this);
		this.frameEventsBuffer = new ArrayList<>();
		this.kinectControllerInterface = controller;
		this.dataCompressor = dataCompressor;

		this.file = new File(recordFilePath);

		// Does capture file already exist?
		boolean fileExists = false;
		while (this.file.exists()) {
			fileExists = true;

			String[] components = recordFilePath.split("\\.");
			if (components.length < 2) components = new String[] { components[0], "cap" };
			int number = 0;
			if (components[0].matches(".*\\d")) {
				int suffixIndex = this.getSuffixIntIndex(components[0]);
				number = new Integer(components[0].substring(suffixIndex));
				components[0] = components[0].substring(0, suffixIndex);
			}
			recordFilePath = components[0] + ++number + "." + components[1];
			this.file = new File(recordFilePath);
		}

		if (fileExists) GameController.LOGGER.info("Capture file already exists. New file name: " + recordFilePath);

		this.fileOut = new FileOutputStream(recordFilePath, false);

		// Register for these events so that we can record them
		EventManager.registerEventListenerForEvent(KinectDepthFrameEvent.class, e -> this.addEvent((KinectFrameEvent) e));
		EventManager.registerEventListenerForEvent(KinectSkeletonFrameEvent.class,
				e -> this.addEvent((KinectFrameEvent) e));

		// Start file write thread
		this.fileWriteThread.start();

	}

	/**
	 * Retrieves an int at the end of a string.
	 * 
	 * TODO: refactor this method into a utility class
	 * 
	 * @param string
	 * @return
	 */
	private int getSuffixIntIndex(String string) {
		int i = string.length();
		while (i > 0 && Character.isDigit(string.charAt(i - 1))) {
			i--;
		}
		return i;
	}

	/**
	 * Flushes the output buffer immediately to file. Blocks calling thread until
	 * writes have occured
	 */
	public void flush() {
		// Notify our write thread in case it is idle atm
		synchronized (this.frameEventsBuffer) {
			this.frameEventsBuffer.notify();
		}

		// Wait until write is finished
		synchronized (this.fileWriteThread) {
			try {
				this.fileWriteThread.wait(1000); // Wait one second max
			} catch (InterruptedException e) {
				// Ignore
			}
		}
	}

	/***
	 * Flushes the output buffer and completes this file. After invoking finish()
	 * this object won't ever write to the file again
	 * 
	 * @throws InterruptedException
	 * @throws IOException
	 */
	public void finish() throws IOException {
		if (this.isDone) return;

		// Write last data
		this.flush();

		// Kill write thread
		try {
			this.isDone = true;
			this.fileWriteThread.interrupt();
			this.fileWriteThread.join();
			this.fileWriteThread = null;
		} catch (InterruptedException e) {
			// Ignore
		}

		// Close file output
		this.fileOut.close();
		this.fileOut = null;

		// This should never happen but check anyways
		if (this.frameEventsBuffer.size() > 0) GameController.LOGGER.warn("There are " + this.frameEventsBuffer.size()
				+ " KinectFrameEvents left over that were not written to file!");

		// Remove frame events association
		this.frameEventsBuffer = null;

		// Print finished message
		GameController.LOGGER.info("Finished capturing to file");
	}

	@Override
	public void run() {
		// Write kinect information to file
		try {
			this.startWrite();
		} catch (IOException e1) {
			e1.printStackTrace();
			GameController.LOGGER.error("Could not write header to file. Exiting");
		}

		while (!this.isDone) {
			synchronized (this.frameEventsBuffer) {
				// Wait until stuff is in our event queue
				try {
					this.frameEventsBuffer.wait();
				} catch (InterruptedException e) {
					// Ignore
				}
			}

			// Two list for both chunks (makes compression easier)
			List<KinectDepthFrameEvent> depthChunk = new ArrayList<>();
			List<KinectSkeletonFrameEvent> skeletonChunk = new ArrayList<>();

			// We must have events in the queue. Copy all of them to a separate
			// Arraylist that we will later compress and write to file. Don't write
			// data in this loop as we want to free up the frameEventsBuffer array
			// as soon as possible. Otherwise we will block every call to
			// addEvent() unnecessarily long
			synchronized (this.frameEventsBuffer) {

				for (KinectFrameEvent event : this.frameEventsBuffer) {
					// Sort elements into lists. TODO: rework system so that we don't
					// have to sort at all
					if (event instanceof KinectDepthFrameEvent) depthChunk.add((KinectDepthFrameEvent) event);
					else if (event instanceof KinectSkeletonFrameEvent) skeletonChunk.add((KinectSkeletonFrameEvent) event);
				}

				// Clear Framebuffer because we don't want to write events to file
				// twice
				this.frameEventsBuffer.clear();
			}

			// Write chunks using the data compressor:
			this.dataCompressor.compressAndWriteDepthFrames(depthChunk, this.fileOut);
			this.dataCompressor.compressAndWriteSkeletonFrames(skeletonChunk, this.fileOut);

			try {
				// Flush changes
				this.fileOut.flush();
				if (this.file.length() > MAX_FILE_SIZE_IN_BYTES) break;
				GameController.LOGGER.info("Written " + this.file.length() + "(=" + (this.file.length() / (1024 * 1024))
						+ "MB) bytes to the capture file so far");
			} catch (IOException e) {
				e.printStackTrace();
				GameController.LOGGER.error("Exception " + e + " while flushing to capture file");
			}

			// Notify that write has finished
			synchronized (this.fileWriteThread) {
				this.fileWriteThread.notify();
			}
		}

		try {
			this.finish();
		} catch (IOException e) {
			e.printStackTrace();
			GameController.LOGGER.warn("Error while finishing data recording");
		}
	}

	/**
	 * Writes the first information object to the file (This includes info like
	 * kinect version etc)
	 * 
	 * @throws IOException
	 */
	private void startWrite() throws IOException {
		// Write the file Header
		this.fileOut.write(FILE_HEADER);

		// Write general information
		ByteBuffer intBuffer = ByteBuffer.allocate(5 * 4);
		intBuffer.putInt(this.kinectControllerInterface.getKinectType());
		intBuffer.putInt(this.kinectControllerInterface.getDepthWidth());
		intBuffer.putInt(this.kinectControllerInterface.getDepthHeight());
		intBuffer.putInt(this.kinectControllerInterface.getMaxSkeletonAmount());
		intBuffer.putInt(this.dataCompressor.getType());

		// Write file header
		this.fileOut.write(intBuffer.array());
	}

	/**
	 * Synchronously adds event into frameEvents for writing to file
	 * 
	 * @param frameEvent
	 */
	private void addEvent(KinectFrameEvent frameEvent) {
		if (this.frameEventsBuffer == null) return;

		synchronized (this.frameEventsBuffer) {
			this.frameEventsBuffer.add(frameEvent);

			// Wake up our file write thread when one chunk can be written
			if (this.frameEventsBuffer.size() >= FRAME_CHUNK_SIZE * 2) this.frameEventsBuffer.notify();
		}
	}
}
