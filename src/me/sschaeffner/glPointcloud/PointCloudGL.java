package me.sschaeffner.glPointcloud;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

import java.io.*;
import java.nio.*;

import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import de.gocodinggroup.kinectdatarecorder.events.*;
import de.gocodinggroup.kinectdatarecorder.playback.*;
import de.gocodinggroup.multiplicationtable.input.kinect.*;
import de.gocodinggroup.util.*;

/**
 * @author sschaeffner
 */
public class PointCloudGL {

	// window handle
	private long window;

	// shader handler
	private ShaderHandler shaderHandler;

	// point cloud
	private PointCloud pointCloud;

	private KinectControllerInterface kinectController;
	private KinectDepthPlaybacker playback;

	private boolean initDone = false;

	private double[] newData = null;

	public static void main(String[] args) throws InterruptedException, IOException {
		new PointCloudGL().run();
	}

	public PointCloudGL() throws InterruptedException, IOException {
		EventManager.registerEventListenerForEvent(KinectDepthFrameEvent.class, e -> {
			KinectDepthFrameEvent event = (KinectDepthFrameEvent) e;
			this.onDepthFrameEvent(event.getDepthFrame(), null, event.getXyz(), null);
		});
		EventManager.registerEventListenerForEvent(KinectSkeletonFrameEvent.class, e -> {
			KinectSkeletonFrameEvent event = (KinectSkeletonFrameEvent) e;
			this.onSkeletonFrameEvent(event.getFlags(), event.getPositions(), event.getOrientations(), event.getState());
		});

		// this.kinectController = new
		// KinectPlaybackController("./MultiplicationTableRecordings/semi_working_playback.cap");
		// this.kinectController = new
		// KinectPlaybackController("D:\\MultiplicationTableRecordings\\playback.cap");
		// this.kinectController = new KinectRealController();
		// this.kinectController.startAndWait(J4KSDK.DEPTH | J4KSDK.SKELETON |
		// J4KSDK.XYZ);
		playback = new KinectDepthPlaybacker(new File("C:\\Users\\Dominik\\Desktop\\recording.depth"));
		playback.start();
	}

	private void onSkeletonFrameEvent(boolean[] flags, float[] positions, float[] orientations, byte[] state) {
		// Do nothing
		System.out.println("" + ((flags != null ? flags.length * 1 : 0) + (positions != null ? positions.length * 4 : 0)
				+ (orientations != null ? orientations.length * 4 : 0) + (state != null ? state.length * 1 : 0)));
	}

	private void onDepthFrameEvent(short[] depthFrame, byte[] playerIndex, float[] xyz, float[] uv) {
		newData = new double[217088 * 2];

		if (!initDone) return;
		if (xyz == null) return;

		for (int i = 0; i < xyz.length / 3; i++) {
			if (xyz[i * 3 + 2] < 2) {
				// System.out.println("(" + xyz[i * 3] + "/" + xyz[i * 3 + 1] + "/"
				// + xyz[i * 3 + 2] + ")");
				newData[2 * i] = xyz[i * 3 + 2];
				newData[2 * i + 1] = xyz[i * 3 + 1];
			}
		}

		/*
		 * if (xyz != null) { float minX = Float.MIN_VALUE; float maxX =
		 * Float.MAX_VALUE; float minY = Float.MIN_VALUE; float maxY =
		 * Float.MAX_VALUE; float minZ = Float.MIN_VALUE; float maxZ =
		 * Float.MAX_VALUE;
		 * 
		 * System.out.println("xyz length: " + xyz.length);
		 * 
		 * for (int i = 0; i < xyz.length; i += 3) { float x = xyz[i]; float y =
		 * xyz[i + 1]; float z = xyz[i + 2];
		 * 
		 * minX = (x < minX) ? x : minX; minY = (y < minY) ? y : minY; minZ = (z <
		 * minZ) ? z : minZ;
		 * 
		 * maxX = (x > maxX) ? x : maxX; maxY = (y > maxY) ? y : maxY; maxZ = (z >
		 * maxZ) ? z : maxZ; }
		 * 
		 * System.out.println("minX: " + minX); System.out.println("maxX: " +
		 * maxX); System.out.println("minY: " + minY); System.out.println("maxY: "
		 * + maxY); System.out.println("minZ: " + minZ);
		 * System.out.println("maxZ: " + maxZ); } else {
		 * System.out.println("XYZ null"); }
		 */
	}

	public void run() {
		init();
		loop();
		cleanup();
	}

	private void init() {
		// Setup error callback
		GLFWErrorCallback.createPrint(System.err).set();

		// Initialize GLFW
		boolean glfwInitSuccess = glfwInit();
		if (!glfwInitSuccess) throw new IllegalStateException("Unable to initialize GLFW");

		// Configure GLFW
		glfwDefaultWindowHints(); // default window hints
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden
																// after creation
		glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable

		// Create the window
		window = glfwCreateWindow(1920, 1080, "PointCloud", NULL, NULL);
		if (window == NULL) throw new RuntimeException("Failed to create the GLFW window");

		// Setup a key callback (pressed, repeated, released)
		glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
			// will detect this in the rendering loop
			if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) glfwSetWindowShouldClose(window, true);
		});

		// Get the thread stack and push a new frame
		try (MemoryStack stack = stackPush()) {
			IntBuffer pWidth = stack.mallocInt(1); // int*
			IntBuffer pHeight = stack.mallocInt(1); // int*

			// Get the window size passed to glfwCreateWindow
			glfwGetWindowSize(window, pWidth, pHeight);

			// Get the resolution of the primary monitor
			GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

			// Center the window
			glfwSetWindowPos(window, (vidmode.width() - pWidth.get(0)) / 2, (vidmode.height() - pHeight.get(0)) / 2);
		} // the stack frame is popped automatically

		// Make the OpenGL context current
		glfwMakeContextCurrent(window);

		// Enable v-sync (swap buffers every frame)
		glfwSwapInterval(1);

		// Make the window visible
		glfwShowWindow(window);
	}

	private void setupPointCloud() {
		pointCloud = new PointCloud(new double[217088 * 2]);
	}

	private void loop() {
		// create openGL bindings
		GL.createCapabilities();

		// set background color to dark grey
		glClearColor(0.2f, 0.2f, 0.2f, 1.0f);

		// load shaders
		try {
			shaderHandler = new ShaderHandler();
		} catch (IOException e) {
			e.printStackTrace();
		}
		shaderHandler.bind();

		// setup point cloud vertex array
		setupPointCloud();

		initDone = true;

		// frame counter
		int d = 0;

		while (!glfwWindowShouldClose(window)) {
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the
																					// framebuffer

			if (newData != null) {
				pointCloud.updateData(newData);
				newData = null;
			}

			// update frame counter
			/*
			 * d++; if (d == 50) { pointCloud.updateData(new double[] { 0, 0, -0.5,
			 * 0.5, 0.5, -0.5 }); } else if (d == 100) { pointCloud.updateData(new
			 * double[] { 0, 0, -0.5, 0, 0.5, 0 }); d = 0; }
			 */

			/* begin drawing code */

			pointCloud.draw();

			/* end drawing code */

			// swap color buffers
			glfwSwapBuffers(window);

			// poll for events on the window
			glfwPollEvents();
		}

		System.exit(-9999);
	}

	private void cleanup() {
		pointCloud.destroy();
		shaderHandler.destroy();

		// Free the window callbacks and destroy the window
		glfwFreeCallbacks(window);
		glfwDestroyWindow(window);

		// Terminate GLFW and free the error callback
		glfwTerminate();
		glfwSetErrorCallback(null).free();
	}

	private class PointCloud {
		private int vertexArrayId;
		private int bufferId;
		private int drawCount;

		PointCloud(double[] pointData) {
			if (pointData.length % 2 != 0)
				throw new IllegalArgumentException("pointData has to have a length multiple of 2");
			drawCount = pointData.length / 2;

			// generate vertex array
			vertexArrayId = glGenVertexArrays();
			// bind vertex array for usage
			glBindVertexArray(vertexArrayId);

			// generate buffer (in vertexArray)
			bufferId = glGenBuffers();
			// bind buffer to GL_ARRAY_BUFFER
			glBindBuffer(GL_ARRAY_BUFFER, bufferId);

			// upload data to GL_ARRAY_BUFFER for drawing (allocates new memory)
			glBufferData(GL_ARRAY_BUFFER, pointData, GL_DYNAMIC_DRAW);

			// enable using this vertex array as source for drawing
			glEnableVertexAttribArray(0);
			// setup format of vertex array for automatic drawing
			glVertexAttribPointer(0, 2, GL_DOUBLE, false, 0, 0);

			// unbind vertex array
			glBindVertexArray(0);
		}

		void draw() {
			// bind vertex array
			glBindVertexArray(vertexArrayId);

			// set size of a point
			glPointSize(5.0f);

			// set color of a point
			glColor4d(0.7, 0.7, 0.7, 1);

			// draw vertex array as points
			glDrawArrays(GL_POINTS, 0, drawCount);

			// unbind vertex array
			glBindVertexArray(0);
		}

		void updateData(double[] pointData) {
			if (pointData.length / 2 == drawCount) {// amount of points unchanged
				// bind vertex array
				glBindVertexArray(vertexArrayId);
				glBindBuffer(GL_ARRAY_BUFFER, bufferId);

				// map the GL_ARRAY_BUFFER to a ByteBuffer
				DoubleBuffer buff = glMapBuffer(GL_ARRAY_BUFFER, GL_WRITE_ONLY).order(ByteOrder.nativeOrder())
						.asDoubleBuffer();
				buff.put(pointData, 0, pointData.length);

				// unmap the GL_ARRAY_BUFFER
				glUnmapBuffer(GL_ARRAY_BUFFER);

				// unbind vertex array
				glBindVertexArray(0);
			} else {
				throw new UnsupportedOperationException("Not yet implemented.");
				// allocate a new memory space using glBufferData(...)
			}
		}

		void destroy() {
			glDeleteVertexArrays(vertexArrayId);
		}
	}
}
