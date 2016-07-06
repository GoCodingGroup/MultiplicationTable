package de.gocodinggroup.multiplicationtable.kinect.tmp;

import java.awt.*;

import javax.swing.*;

/**
 * Temporary (?) class to visualize Kinect sensory input
 * 
 * @author Simon
 *
 */
public class Gui2DDemo extends JFrame {

	private int x, y, z;
	private int heightMin, heightMax;

	public static void main(String[] args) {
		new Gui2DDemo();
	}

	public Gui2DDemo() {
		this.setContentPane(new MyPanel());

		this.setSize(new Dimension(800, 600));
		this.setLocationRelativeTo(null);
		this.setVisible(true);
	}

	public void setX(int x) {
		this.x = x;
		this.repaint();
	}

	public void setY(int y) {
		this.y = y;
		this.repaint();
	}

	public void setZ(int z) {
		this.z = z;
		this.repaint();
	}

	public void setHeightMin(int heigthMin) {
		this.heightMin = heigthMin;
		this.repaint();
	}

	public void setHeightMax(int heigthMax) {
		this.heightMax = heigthMax;
		this.repaint();
	}

	private class MyPanel extends JPanel {
		@Override
		public void paint(Graphics g) {
			// TODO Auto-generated method stub
			super.paint(g);

			if (g instanceof Graphics2D) {
				Graphics2D g2 = (Graphics2D) g;

				g2.fillOval(x + (getWidth() / 2) - 25, z - 25 - 100, 50, 50);

				g2.setColor(Color.BLUE);
				g2.fillRect(getWidth() / 2, getHeight() - 30, heightMin, 30);

				g2.setColor(Color.BLUE);
				g2.fillRect(getWidth() / 2, getHeight() - 30, heightMax, 30);

				g2.setColor(Color.PINK);
				g2.fillRect(getWidth() / 2, getHeight() - 30, y, 30);
			}
		}
	}
}