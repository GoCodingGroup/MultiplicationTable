package de.lezleoh.multiplicationtable.view;

import de.lezleoh.multiplicationtable.model.Sprite;
import de.lezleoh.multiplicationtable.util.Observer;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class BubbleView1 extends Circle implements Observer {
	Sprite sprite;
	BubbleControler controler;

	BubbleView1(Sprite sprite, BubbleControler controler) {
		super();
		super.setFill(Color.BLUE);
		super.setOnMousePressed(new EventHandler<MouseEvent>() {
		    public void handle(MouseEvent me) {
		    	controler.stop();
		    }
		});
		
		this.sprite = sprite;
		this.controler = controler;
		
		resize();
		relocate();
		sprite.addObserver(this);
	}

	Circle getShape() {
		return this ;
	}

	@Override
	public void update() {
		resize();
		relocate();
	}

	private void resize() {
		int radius = Math.max(sprite.getHeight(), sprite.getWidth());
		super.setRadius(radius);
	}

	public void relocate() {
		super.setCenterX(sprite.getXLocation());
		super.setCenterY(sprite.getYLocation());

	}
}
