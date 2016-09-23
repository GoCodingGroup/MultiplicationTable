package de.gocodinggroup.multiplicationtable.input;

import de.gocodinggroup.multiplicationtable.util.*;
import de.gocodinggroup.multiplicationtable.util.events.*;
import javafx.scene.*;

public class MouseInput implements InputParser {
	private int playerX, playerY;

	public MouseInput(Node trackedNode) {
		this.playerX = 0;
		this.playerY = 0;

		trackedNode.setOnMouseMoved((event) -> {
			this.playerX = (int) event.getSceneX();
			this.playerY = (int) event.getSceneY();
		});

		trackedNode.setOnMouseClicked((event) -> {
			EventManager.dispatchEvent(new PlayerJumpedEvent(this.playerX, this.playerY));
		});
	}

	@Override
	public int getPlayerX() {
		return this.playerX;
	}

	@Override
	public int getPlayerY() {
		return this.playerY;
	}

}
