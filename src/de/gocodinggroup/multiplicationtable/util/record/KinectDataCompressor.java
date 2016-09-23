package de.gocodinggroup.multiplicationtable.util.record;

import java.io.*;
import java.util.*;

/**
 * Abstract base class. Use subclasses to compress and decompress
 * KinectFrameData
 * 
 * @author Dominik
 *
 */
public abstract class KinectDataCompressor {
	/**
	 * Compresses and writes depth frames to file
	 * 
	 * @param frames
	 * @param file
	 * @return amount of written bytes
	 */
	public abstract boolean compressAndWriteDepthFrames(List<KinectDepthFrameEvent> frames, FileOutputStream file);

	/**
	 * Compresses and writes skeleton frames to file
	 * 
	 * @param frames
	 * @param file
	 * @return amount of written bytes
	 */
	public abstract boolean compressAndWriteSkeletonFrames(List<KinectSkeletonFrameEvent> frames, FileOutputStream file);

	/**
	 * Retrieve the next events (TODO: refactor maybe)
	 * 
	 * @param fileIn
	 * @return
	 * @throws IOException
	 * @throws CaptureResetStreamException
	 */
	public abstract List<KinectFrameEvent> getNextChunks(BufferedInputStream fileIn)
			throws IOException, CaptureResetStreamException;

	/**
	 * Returns a unique id for this compressor
	 * 
	 * @return
	 */
	public abstract int getType();
}
