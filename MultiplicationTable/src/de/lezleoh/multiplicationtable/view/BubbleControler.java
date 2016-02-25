package de.lezleoh.multiplicationtable.view;

import de.lezleoh.multiplicationtable.model.Sprite;

public class BubbleControler implements BubbleControlerInterface {
	Sprite sprite;
	BubbleView bubbleView;

	public BubbleControler(Sprite sprite) {
		this.sprite = sprite;
		bubbleView = new BubbleView(sprite, this);
	}

	public void stop() {
		sprite.setXSpeed(0);
		sprite.setYSpeed(0);
	}

	@Override
	public BubbleView getView() {
		return bubbleView;
	}
}