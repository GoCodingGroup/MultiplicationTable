package de.gocodinggroup.multiplicationtable.util.events;

import de.gocodinggroup.multiplicationtable.game.model.gameentites.*;
import de.gocodinggroup.util.*;

public class BubbleHitEvent extends Event {
	private BubbleEntity bubble;

	public BubbleHitEvent(BubbleEntity bubble) {
		this.bubble = bubble;
	}

	public BubbleEntity getBubble() {
		return this.bubble;
	}

}
