package de.gocodinggroup.multiplicationtable.util.record;

import java.io.*;
import java.nio.*;
import java.util.*;

import de.gocodinggroup.multiplicationtable.game.controller.*;

public class StandardKinectDataCompressor extends KinectDataCompressor {
	/**
	 * After every chunk this is put to notify that we reached the end of one
	 * chunk sequence
	 */
	private static final byte[] CHUNK_ENDSEQUENCE = new byte[] { (byte) 0xEC, (byte) 0x83, (byte) 0xB1, (byte) 0xB1 };

	/**
	 * In front of every depth chunk sequence
	 */
	private static final byte[] DEPTHCHUNK_BEGINSEQUENCE = new byte[] { (byte) 0xEC, (byte) 0x83, (byte) 0xB2,
			(byte) 0xB2 };

	/**
	 * In front of every skeleton chunk sequence
	 */
	private static final byte[] SKELETONCHUNK_BEGINSEQUENCE = new byte[] { (byte) 0xEC, (byte) 0x83, (byte) 0xB3,
			(byte) 0xB3 };

	/**
	 * We need to do this to make compression possible
	 */
	private static final int DEPTHDATA_COMPRESSION_TOLERANCE = 10 * 10;

	/**
	 * We need this to make compression possible. Throw out 50 % of the depth
	 * frames
	 */
	private static boolean DEPTH_THROW_OUT_TOGGLE = false;

	private static final int DEPTH_CHUNK = 1;
	private static final int SKELETON_CHUNK = 2;
	private static final int UNKNOWN_CHUNK = -1;

	@Override
	public boolean compressAndWriteDepthFrames(List<KinectDepthFrameEvent> frames, FileOutputStream file) {
		// No frames no write!
		if (frames.size() <= 0) return false;

		try {
			// Write depth frame begin
			file.write(DEPTHCHUNK_BEGINSEQUENCE);

			// Write keyframe
			KinectDepthFrameEvent keyframe = frames.get(0);
			ByteBuffer frameSizeBuffer = ByteBuffer.allocate(4);

			short[] keyDepthFrame = keyframe.getDepthFrame();
			byte[] keyPlayerIndex = keyframe.getPlayerIndex();
			float[] keyXyz = keyframe.getXyz();
			float[] keyUv = keyframe.getUv();

			byte[] keyframeData = convertToKeyDepthFrame(keyframe.getTimestamp(), keyDepthFrame, keyPlayerIndex, keyXyz,
					keyUv);
			frameSizeBuffer.putInt(keyframeData.length - (8 + 4 * 4));
			file.write(frameSizeBuffer.array());
			file.write(keyframeData);

			// Clear for next frame
			frameSizeBuffer.clear();

			// Write left over frames
			for (int i = 1; i < frames.size(); i++) {
				// throw out every second frame for compression reasons
				if (DEPTH_THROW_OUT_TOGGLE = !DEPTH_THROW_OUT_TOGGLE) continue;

				KinectDepthFrameEvent event = frames.get(i);
				byte[] compressedData = getCompressedDepthFrame(keyDepthFrame, keyPlayerIndex, keyXyz, keyUv,
						event.getDepthFrame(), event.getPlayerIndex(), event.getXyz(), event.getUv(), event.getTimestamp());

				// Figure out length (subtract header size. TODO: optimize/refactor)
				frameSizeBuffer.putInt(compressedData.length - (8 + 4 * 4));

				// Write to file
				file.write(frameSizeBuffer.array());
				file.write(compressedData);

				// Prepare for next frame
				frameSizeBuffer.clear();
			}

			// Write chunk end sequence
			file.write(CHUNK_ENDSEQUENCE);

			return true;
		} catch (IOException e) {
			e.printStackTrace();
			GameController.LOGGER.error("Could not write depth frames to capture file!");
		}
		return false;
	}

	/**
	 * Crafts the header from a depth frame. TODO: maybe move to
	 * KinectDepthFrameEvent class
	 * 
	 * @param timestamp
	 * @param depthFrame
	 * @param playerIndex
	 * @param xyz
	 * @param uv
	 * @return
	 */
	private static byte[] getDepthHeader(long timestamp, short[] depthFrame, byte[] playerIndex, float[] xyz,
			float[] uv) {
		ByteBuffer buffer = ByteBuffer.allocate(8 + 4 * 4);
		buffer.putLong(timestamp);
		buffer.putInt(depthFrame != null ? depthFrame.length : 0);
		buffer.putInt(playerIndex != null ? playerIndex.length : 0);
		buffer.putInt(xyz != null ? xyz.length : 0);
		buffer.putInt(uv != null ? uv.length : 0);
		return buffer.array();
	}

	/**
	 * Compresses data by diffing against keyframe
	 * 
	 * @param keyDepthFrame
	 * @param keyPlayerIndex
	 * @param keyXyz
	 * @param keyUv
	 * @param depthFrame
	 * @param playerIndex
	 * @param xyz
	 * @param uv
	 * @return
	 */
	private static byte[] getCompressedDepthFrame(short[] keyDepthFrame, byte[] keyPlayerIndex, float[] keyXyz,
			float[] keyUv, short[] depthFrame, byte[] playerIndex, float[] xyz, float[] uv, long timestamp) {
		// Calculate data diff
		Map<Integer, Short> depthFrameDiff = null;
		Map<Integer, Byte> playerIndexDiff = null;
		Map<Integer, Float> xyzDiff = null;
		Map<Integer, Float> uvDiff = null;

		if (keyDepthFrame != null && depthFrame != null) {
			depthFrameDiff = new HashMap<>();
			for (int i = 0; i < keyDepthFrame.length; i++)
				if ((depthFrame[i] - keyDepthFrame[i])
						* (depthFrame[i] - keyDepthFrame[i]) > DEPTHDATA_COMPRESSION_TOLERANCE)
					depthFrameDiff.put(i, depthFrame[i]);
		}

		if (keyPlayerIndex != null && playerIndex != null) {
			playerIndexDiff = new HashMap<>();
			for (int i = 0; i < keyPlayerIndex.length; i++)
				if (playerIndex[i] != keyPlayerIndex[i]) playerIndexDiff.put(i, playerIndex[i]);
		}

		if (keyXyz != null && xyz != null) {
			xyzDiff = new HashMap<>();
			for (int i = 0; i < keyXyz.length; i++)
				if ((xyz[i] - keyXyz[i]) * (xyz[i] - keyXyz[i]) > DEPTHDATA_COMPRESSION_TOLERANCE) xyzDiff.put(i, xyz[i]);
		}

		if (keyUv != null && uv != null) {
			uvDiff = new HashMap<>();
			for (int i = 0; i < keyUv.length; i++)
				if ((uv[i] - keyUv[i]) * (uv[i] - keyUv[i]) > DEPTHDATA_COMPRESSION_TOLERANCE) uvDiff.put(i, uv[i]);
		}

		// Calculate frame length
		int bufflen = 8 + 4 * 4;
		if (depthFrameDiff != null) bufflen += depthFrameDiff.size() * 4 + depthFrameDiff.size() * 2;
		if (playerIndexDiff != null) bufflen += playerIndexDiff.size() * 4 + playerIndexDiff.size() * 1;
		if (xyzDiff != null) bufflen += xyzDiff.size() * 4 + xyzDiff.size() * 4;
		if (uvDiff != null) bufflen += uvDiff.size() * 4 + uvDiff.size() * 4;

		// Pack header into byte frame
		ByteBuffer framebuffer = ByteBuffer.allocate(bufflen);
		framebuffer.putLong(timestamp);
		framebuffer.putInt(depthFrameDiff != null ? depthFrameDiff.keySet().size() : 0);
		framebuffer.putInt(playerIndexDiff != null ? playerIndexDiff.keySet().size() : 0);
		framebuffer.putInt(xyzDiff != null ? xyzDiff.keySet().size() : 0);
		framebuffer.putInt(uvDiff != null ? uvDiff.keySet().size() : 0);

		/* Pack compressed data into byte frame */

		// Pack depthFrameDiff data
		if (depthFrameDiff != null) {
			int[] depthFrameDiffIndices = new int[depthFrameDiff.keySet().size()];
			short[] depthFrameDiffData = new short[depthFrameDiff.keySet().size()];
			int cnt = 0;
			for (Integer index : depthFrameDiff.keySet()) {
				depthFrameDiffIndices[cnt] = index;
				depthFrameDiffData[cnt] = depthFrameDiff.get(index);
				cnt++;
			}

			// Store data in framebuffer
			for (int i = 0; i < depthFrameDiffIndices.length; i++)
				framebuffer.putInt(depthFrameDiffIndices[i]);
			for (int i = 0; i < depthFrameDiffData.length; i++)
				framebuffer.putShort(depthFrameDiffData[i]);
		}

		// Pack playerDiff data
		if (playerIndexDiff != null) {
			int[] playerIndexDiffIndices = new int[playerIndexDiff.keySet().size()];
			byte[] playerIndexDiffData = new byte[playerIndexDiff.keySet().size()];
			int cnt = 0;
			for (Integer index : playerIndexDiff.keySet()) {
				playerIndexDiffIndices[cnt] = index;
				playerIndexDiffData[cnt] = playerIndexDiff.get(index);
				cnt++;
			}

			// Store data in framebuffer
			for (int i = 0; i < playerIndexDiffIndices.length; i++)
				framebuffer.putInt(playerIndexDiffIndices[i]);
			for (int i = 0; i < playerIndexDiffData.length; i++)
				framebuffer.put(playerIndexDiffData[i]);
		}

		// Pack xyz data
		if (xyzDiff != null) {
			int[] xyzDiffIndices = new int[xyzDiff.keySet().size()];
			float[] xyzDiffData = new float[xyzDiff.keySet().size()];
			int cnt = 0;
			for (Integer index : xyzDiff.keySet()) {
				xyzDiffIndices[cnt] = index;
				xyzDiffData[cnt] = xyzDiff.get(index);
				cnt++;
			}

			// Store data in framebuffer
			for (int i = 0; i < xyzDiffIndices.length; i++)
				framebuffer.putInt(xyzDiffIndices[i]);
			for (int i = 0; i < xyzDiffData.length; i++)
				framebuffer.putFloat(xyzDiffData[i]);
		}

		// Pack uv data
		if (uvDiff != null) {
			int[] uvDiffIndices = new int[uvDiff.keySet().size()];
			float[] uvDiffData = new float[uvDiff.keySet().size()];
			int cnt = 0;
			for (Integer index : uvDiff.keySet()) {
				uvDiffIndices[cnt] = index;
				uvDiffData[cnt] = uvDiff.get(index);
				cnt++;
			}

			// Store data in framebuffer
			for (int i = 0; i < uvDiffIndices.length; i++)
				framebuffer.putInt(uvDiffIndices[i]);
			for (int i = 0; i < uvDiffData.length; i++)
				framebuffer.putFloat(uvDiffData[i]);
		}

		return framebuffer.array();
	}

	/**
	 * Converts data set to byte array representing full key frame
	 * 
	 * @param timestamp
	 * @param depthFrame
	 * @param playerIndex
	 * @param xyz
	 * @param uv
	 * @return
	 */
	private static byte[] convertToKeyDepthFrame(long timestamp, short[] depthFrame, byte[] playerIndex, float[] xyz,
			float[] uv) {
		// Allocate buffer
		int totalLength = 8 + 4 * 4;

		// Calculate total length
		if (depthFrame != null) totalLength += 2 * depthFrame.length;
		if (playerIndex != null) totalLength += 1 * playerIndex.length;
		if (xyz != null) totalLength += 4 * xyz.length;
		if (uv != null) totalLength += 4 * uv.length;

		// Allocate buffer
		ByteBuffer keyframeBuffer = ByteBuffer.allocate(totalLength);

		// Write file as specified in format
		keyframeBuffer.put(getDepthHeader(timestamp, depthFrame, playerIndex, xyz, uv));

		if (depthFrame != null) for (int i = 0; i < depthFrame.length; i++)
			keyframeBuffer.putShort(depthFrame[i]);

		if (playerIndex != null) for (int i = 0; i < playerIndex.length; i++)
			keyframeBuffer.put(playerIndex[i]);

		if (xyz != null) for (int i = 0; i < xyz.length; i++)
			keyframeBuffer.putFloat(xyz[i]);

		if (uv != null) for (int i = 0; i < uv.length; i++)
			keyframeBuffer.putFloat(uv[i]);

		return keyframeBuffer.array();
	}

	@Override
	public boolean compressAndWriteSkeletonFrames(List<KinectSkeletonFrameEvent> frames, FileOutputStream file) {
		// No frames no write!
		if (frames.size() <= 0) return false;

		try {
			// Write skeleton frame chunk begin sequence
			file.write(SKELETONCHUNK_BEGINSEQUENCE);

			// Allocate frameSizeBuffer
			ByteBuffer frameSizeBuffer = ByteBuffer.allocate(4);

			// Write skeleton frames
			for (KinectSkeletonFrameEvent event : frames) {
				// Calculate skeleton frame
				byte[] skeletonFrame = getSkeletonFrame(event.getTimestamp(), event.getFlags(), event.getPositions(),
						event.getOrientations(), event.getState());

				// Figure out length
				frameSizeBuffer.putInt(skeletonFrame.length - (8 + 4 * 4));

				// Write to file
				file.write(frameSizeBuffer.array());
				file.write(skeletonFrame);

				// Clear for next frame
				frameSizeBuffer.clear();
			}

			// Write chunk end sequence
			file.write(CHUNK_ENDSEQUENCE);

			return true;
		} catch (IOException e) {
			e.printStackTrace();
			GameController.LOGGER.error("Could not write depth frames to capture file!");
		}
		return false;
	}

	/**
	 * Write uncompressed skeleton frame
	 * 
	 * @param timestamp
	 * @param flags
	 * @param positions
	 * @param orientations
	 * @param state
	 * @return
	 */
	private static byte[] getSkeletonFrame(long timestamp, boolean[] flags, float[] positions, float[] orientations,
			byte[] state) {
		int totalLength = 8 + 4 * 4;
		if (flags != null) totalLength += 1 * flags.length;
		if (positions != null) totalLength += 4 * positions.length;
		if (orientations != null) totalLength += 4 * orientations.length;
		if (state != null) totalLength += 1 * state.length;

		ByteBuffer frame = ByteBuffer.allocate(totalLength);
		frame.putLong(timestamp);
		frame.putInt(flags.length);
		frame.putInt(positions.length);
		frame.putInt(orientations.length);
		frame.putInt(state.length);

		if (flags != null) for (int i = 0; i < flags.length; i++)
			frame.put(flags[i] == true ? (byte) 0x1 : (byte) 0x0);

		if (positions != null) for (int i = 0; i < positions.length; i++)
			frame.putFloat(positions[i]);

		if (orientations != null) for (int i = 0; i < orientations.length; i++)
			frame.putFloat(orientations[i]);

		if (state != null) for (int i = 0; i < state.length; i++)
			frame.put(state[i]);

		return frame.array();
	}

	@Override
	public int getType() {
		return 1337;
	}

	@Override
	public List<KinectFrameEvent> getNextChunks(BufferedInputStream fileIn)
			throws IOException, CaptureResetStreamException {
		List<KinectFrameEvent> frames = new ArrayList<>();

		if (getNextChunkType(fileIn) == DEPTH_CHUNK) {
			// Retrieve one chunk
			this.readChunk(frames, fileIn);

			// Only read next chunk if it is not a depth chunk again
			if (getNextChunkType(fileIn) != DEPTH_CHUNK) {
				this.readChunk(frames, fileIn);
			}
		}
		return frames;
	}

	private int getNextChunkType(BufferedInputStream fileIn) throws IOException, CaptureResetStreamException {
		byte[] chunkHeader = new byte[4];
		fileIn.mark(4);
		fileIn.read(chunkHeader);
		fileIn.reset();
		if (compareSignature(DEPTHCHUNK_BEGINSEQUENCE, chunkHeader)) {
			return DEPTH_CHUNK;
		} else if (compareSignature(SKELETONCHUNK_BEGINSEQUENCE, chunkHeader)) {
			return SKELETON_CHUNK;
		} else {
			// TODO: tmp hack to make file loop
			throw new CaptureResetStreamException();

			// GameController.LOGGER.warn("unrecognized chunk signature");
			// return UNKNOWN_CHUNK;
		}
	}

	/**
	 * Retrieves one chunk
	 * 
	 * @param frames
	 * @param fileIn
	 * @throws IOException
	 */
	private int readChunk(List<KinectFrameEvent> frames, BufferedInputStream fileIn) throws IOException {
		// Read chunk signature
		byte[] chunkHeader = new byte[4];
		fileIn.read(chunkHeader);
		if (compareSignature(DEPTHCHUNK_BEGINSEQUENCE, chunkHeader)) {
			// Read depth chunk
			this.readDepthChunk(frames, fileIn);
			return DEPTH_CHUNK;
		} else if (compareSignature(SKELETONCHUNK_BEGINSEQUENCE, chunkHeader)) {
			// Read skeleton chunk
			this.readSkeletonChunk(frames, fileIn);
			return SKELETON_CHUNK;
		} else {
			GameController.LOGGER.warn("unrecognized chunk signature");
			return UNKNOWN_CHUNK;
		}
	}

	/**
	 * Reads one depth chunk from the file
	 * 
	 * @param frames
	 * @throws IOException
	 */
	private void readDepthChunk(List<KinectFrameEvent> frames, BufferedInputStream fileIn) throws IOException {
		byte[] byteBuffer = new byte[4 + 8 + 4 * 4];
		fileIn.read(byteBuffer);

		// This is the keyframe header
		ByteBuffer frameHeader = ByteBuffer.wrap(byteBuffer);
		int keyFrameSize = frameHeader.getInt();
		long keyTimestamp = frameHeader.getLong();
		int keyDepthFrameLength = frameHeader.getInt();
		int keyPlayerIndexLength = frameHeader.getInt();
		int keyXyzLength = frameHeader.getInt();
		int keyUvLength = frameHeader.getInt();

		// Keyframe data
		short[] keyDepthFrame = keyDepthFrameLength > 0 ? new short[keyDepthFrameLength] : null;
		byte[] keyPlayerIndex = keyPlayerIndexLength > 0 ? new byte[keyPlayerIndexLength] : null;
		float[] keyXyz = keyXyzLength > 0 ? new float[keyXyzLength] : null;
		float[] keyUv = keyUvLength > 0 ? new float[keyUvLength] : null;

		// Read data
		ByteBuffer frameData = ByteBuffer.allocate(keyFrameSize);
		byteBuffer = frameData.array();
		fileIn.read(byteBuffer);
		frameData = ByteBuffer.wrap(byteBuffer);

		// Parse keyframe data
		for (int i = 0; i < keyDepthFrameLength; i++)
			keyDepthFrame[i] = frameData.getShort();
		for (int i = 0; i < keyPlayerIndexLength; i++)
			keyPlayerIndex[i] = frameData.get();
		for (int i = 0; i < keyXyzLength; i++)
			keyXyz[i] = frameData.getFloat();
		for (int i = 0; i < keyUvLength; i++)
			keyUv[i] = frameData.getFloat();

		// Craft key frame from data
		frames.add(new KinectDepthFrameEvent(keyTimestamp, keyDepthFrame, keyPlayerIndex, keyXyz, keyUv));

		// if we have not reached this signature
		while (!compareSignature(CHUNK_ENDSEQUENCE, peekInt(fileIn))) {
			// Parse and add frame
			frames.add(this.uncompressDepthFrameEvent(keyDepthFrame, keyPlayerIndex, keyXyz, keyUv, fileIn));
		}

		// Throw out signature as peekInt won't advance buffer pos
		fileIn.read(new byte[4]);
	}

	/**
	 * Uncompresses one depth frame event TODO: optimize
	 * 
	 * @param keyDepthFrame
	 * @param keyPlayerIndex
	 * @param keyXyz
	 * @param keyUv
	 * @param fileIn
	 * @return
	 * @throws IOException
	 */
	private KinectDepthFrameEvent uncompressDepthFrameEvent(short[] keyDepthFrame, byte[] keyPlayerIndex, float[] keyXyz,
			float[] keyUv, BufferedInputStream fileIn) throws IOException {
		byte[] bytebuffer = new byte[4 + 8 + 4 * 4];
		fileIn.read(bytebuffer);
		ByteBuffer compressedFrameHeader = ByteBuffer.wrap(bytebuffer);

		int frameBodySize = compressedFrameHeader.getInt();
		long timestamp = compressedFrameHeader.getLong();
		int depthFrameDiffLength = compressedFrameHeader.getInt();
		int playerIndexDiffLength = compressedFrameHeader.getInt();
		int xyzDiffLength = compressedFrameHeader.getInt();
		int uvDiffLength = compressedFrameHeader.getInt();

		bytebuffer = new byte[frameBodySize];
		fileIn.read(bytebuffer);
		ByteBuffer compressedFrameBody = ByteBuffer.wrap(bytebuffer);

		// Extract diffs and recalculate original data
		int[] depthFrameDiffIndices = new int[depthFrameDiffLength];
		short[] depthFrameDiffData = new short[depthFrameDiffLength];
		int[] playerIndexDiffIndices = new int[playerIndexDiffLength];
		byte[] playerIndexDiffData = new byte[playerIndexDiffLength];
		int[] xyzDiffIndices = new int[xyzDiffLength];
		float[] xyzDiffData = new float[xyzDiffLength];
		int[] uvDiffIndices = new int[uvDiffLength];
		float[] uvDiffData = new float[uvDiffLength];

		for (int i = 0; i < depthFrameDiffLength; i++)
			depthFrameDiffIndices[i] = compressedFrameBody.getInt();
		for (int i = 0; i < depthFrameDiffLength; i++)
			depthFrameDiffData[i] = compressedFrameBody.getShort();

		for (int i = 0; i < playerIndexDiffLength; i++)
			playerIndexDiffIndices[i] = compressedFrameBody.getInt();
		for (int i = 0; i < playerIndexDiffLength; i++)
			playerIndexDiffData[i] = compressedFrameBody.get();

		for (int i = 0; i < xyzDiffLength; i++)
			xyzDiffIndices[i] = compressedFrameBody.getInt();
		for (int i = 0; i < xyzDiffLength; i++)
			xyzDiffData[i] = compressedFrameBody.getFloat();

		for (int i = 0; i < uvDiffLength; i++)
			uvDiffIndices[i] = compressedFrameBody.getInt();
		for (int i = 0; i < uvDiffLength; i++)
			uvDiffData[i] = compressedFrameBody.getFloat();

		// Recalculate original data
		short[] depthFrameData = null;
		byte[] playerIndex = null;
		float[] xyz = null;
		float[] uv = null;

		if (keyDepthFrame != null) {
			depthFrameData = new short[keyDepthFrame.length];
			for (int i = 0; i < depthFrameDiffLength; i++)
				depthFrameData[i] = keyDepthFrame[i];
			for (int i = 0; i < depthFrameDiffLength; i++)
				depthFrameData[depthFrameDiffIndices[i]] = depthFrameDiffData[i];
		}

		if (keyPlayerIndex != null) {
			playerIndex = new byte[keyPlayerIndex.length];
			for (int i = 0; i < playerIndexDiffLength; i++)
				playerIndex[i] = keyPlayerIndex[i];
			for (int i = 0; i < playerIndexDiffLength; i++)
				playerIndex[playerIndexDiffIndices[i]] = playerIndexDiffData[i];
		}

		if (keyXyz != null) {
			xyz = new float[keyXyz.length];
			for (int i = 0; i < xyzDiffLength; i++)
				xyz[i] = keyXyz[i];
			for (int i = 0; i < xyzDiffLength; i++)
				xyz[xyzDiffIndices[i]] = xyzDiffData[i];
		}

		if (keyUv != null) {
			uv = new float[keyUv.length];
			for (int i = 0; i < uvDiffLength; i++)
				uv[i] = keyUv[i];
			for (int i = 0; i < uvDiffLength; i++)
				uv[uvDiffIndices[i]] = uvDiffData[i];
		}

		return new KinectDepthFrameEvent(timestamp, depthFrameData, playerIndex, xyz, uv);
	}

	/**
	 * Reads one skeleton chunk from the file
	 * 
	 * @param frames
	 * @throws IOException
	 */
	private void readSkeletonChunk(List<KinectFrameEvent> frames, BufferedInputStream fileIn) throws IOException {
		// If we have not reached this signature the chunk continues
		while (!compareSignature(CHUNK_ENDSEQUENCE, peekInt(fileIn))) {
			frames.add(this.readSkeletonFrame(fileIn));
		}

		// Throw out signature as peekInt won't advance buffer pos
		fileIn.read(new byte[4]);
	}

	/**
	 * Read single skeleton frame from fileInStream
	 * 
	 * @param fileIn
	 * @return
	 * @throws IOException
	 */
	private KinectSkeletonFrameEvent readSkeletonFrame(BufferedInputStream fileIn) throws IOException {
		byte[] byteBuffer = ByteBuffer.allocate(4 + 8 + 4 * 4).array();
		fileIn.read(byteBuffer);
		ByteBuffer frameHeader = ByteBuffer.wrap(byteBuffer);

		// Parse header
		int frameBodyLength = frameHeader.getInt();
		long timestamp = frameHeader.getLong();
		int flagsLength = frameHeader.getInt();
		int positionsLength = frameHeader.getInt();
		int orientationsLength = frameHeader.getInt();
		int stateLength = frameHeader.getInt();

		// Declare arrays
		boolean[] flags = null;
		float[] positions = null;
		float[] orientations = null;
		byte[] state = null;

		// Read body
		byteBuffer = ByteBuffer.allocate(frameBodyLength).array();
		fileIn.read(byteBuffer);
		ByteBuffer frameBody = ByteBuffer.wrap(byteBuffer);

		if (flagsLength > 0) {
			flags = new boolean[flagsLength];
			for (int i = 0; i < flagsLength; i++)
				flags[i] = frameBody.get() == (byte) 0x1 ? true : false;
		}

		if (positionsLength > 0) {
			positions = new float[positionsLength];
			for (int i = 0; i < positionsLength; i++)
				positions[i] = frameBody.getFloat();
		}

		if (orientationsLength > 0) {
			orientations = new float[orientationsLength];
			for (int i = 0; i < orientationsLength; i++)
				orientations[i] = frameBody.getFloat();
		}

		if (stateLength > 0) {
			state = new byte[stateLength];
			for (int i = 0; i < stateLength; i++)
				state[i] = frameBody.get();
		}

		return new KinectSkeletonFrameEvent(timestamp, flags, positions, orientations, state);
	}

	/**
	 * Peeks an integer from the fileInputStream but leaving the buffers current
	 * read position unaltered
	 * 
	 * @param fileIn
	 * @return
	 * @throws IOException
	 */
	private byte[] peekInt(BufferedInputStream fileIn) throws IOException {
		byte[] buffer = new byte[4];
		fileIn.mark(4);
		fileIn.read(buffer);
		fileIn.reset();
		return buffer;
	}

	/**
	 * Compares signature to bytes
	 * 
	 * @param signature
	 * @param bytes
	 * @return
	 */
	private boolean compareSignature(byte[] signature, byte[] bytes) {
		for (int i = 0; i < signature.length; i++)
			if (bytes[i] != signature[i]) return false;

		return true;
	}
}
