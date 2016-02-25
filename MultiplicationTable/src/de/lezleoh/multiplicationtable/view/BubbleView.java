package de.lezleoh.multiplicationtable.view;

import de.lezleoh.multiplicationtable.model.Sprite;
import de.lezleoh.multiplicationtable.util.Observer;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class BubbleView implements Observer {
	
	Sprite sprite;
	Circle circle;
	BubbleControler controler;

	BubbleView(Sprite sprite, BubbleControler controler) {
		circle  = new Circle();
		circle.setFill(Color.BLUE);
		
		//Reaction on Events
		circle.setOnMousePressed(new EventHandler<MouseEvent>() {
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
		return circle;
	}

	@Override
	public void update() {
		resize();
		relocate();
	}

	private void resize() {
		int radius = Math.max(sprite.getHeight(), sprite.getWidth());
		circle.setRadius(radius);
	}

	public void relocate() {
		circle.setCenterX(sprite.getXLocation());
		circle.setCenterY(sprite.getYLocation());

	}

}
