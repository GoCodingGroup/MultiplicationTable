package de.gocodinggroup.multiplicationtable.util.events;

import javafx.event.*;
import javafx.scene.*;

public class UpdateSceneEvent extends Event {

	private static final long serialVersionUID = 201608171059L;
	public static final EventType<UpdateSceneEvent> UPDATE_SCENE = new EventType<>(Event.ANY, "UPDATE_SCENE");

	private Group group;

	public UpdateSceneEvent(Group source, EventTarget target) {
		super(source, target, UPDATE_SCENE);
		this.group = source;
	}

	public Group getGroup() {
		return group;
	}

}
