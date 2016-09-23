package de.gocodinggroup.multiplicationtable.util;

import java.util.*;

import javafx.application.*;

/**
 * Event Manager class that recipients must register to and that is used for
 * dispatching events
 * 
 * @author Dominik
 *
 */
public class EventManager {
	private static EventManager manager;
	private Map<Class<? extends Event>, List<EventListener>> eventMap;

	private EventManager() {
		this.eventMap = new HashMap<>();
	}

	/**
	 * Retrieve global event manager instance
	 * 
	 * @return
	 */
	private static EventManager getEventManager() {
		if (manager == null) {
			manager = new EventManager();
		}

		return manager;
	}

	/**
	 * Register event listener for certain event
	 * 
	 * @param eventType
	 * @param listener
	 */
	public static void registerEventListenerForEvent(Class<? extends Event> eventType, EventListener listener) {
		EventManager eManager = getEventManager();
		eManager.registerEventListener(eventType, listener);
	}

	/**
	 * Remove event listener for certain event
	 * 
	 * @param eventType
	 * @param listener
	 */
	public static void removeEventListenerForEvent(Class<? extends Event> eventType, EventListener listener) {
		EventManager eManager = getEventManager();
		eManager.removeEventListener(eventType, listener);
	}

	/**
	 * Dispatches an Event immediately on the current Thread and waits until all
	 * registered listeners have acted upon said event
	 * 
	 * @param event
	 */
	public static void dispatchEventAndWait(Event event) {
		EventManager eManager = getEventManager();
		eManager.dispatch(event);
	}

	/**
	 * Dispatches an event asynchronously on the JavaFX-Main thread and
	 * immediately returns execution flow to caller
	 * 
	 * @param event
	 */
	public static void dispatchEvent(Event event) {
		EventManager eManager = getEventManager();
		Platform.runLater(() -> eManager.dispatch(event));
	}

	private void registerEventListener(Class<? extends Event> eventType, EventListener listener) {
		List<EventListener> listeners;
		if (eventMap.containsKey(eventType)) listeners = eventMap.get(eventType);
		else listeners = new ArrayList<>();

		listeners.add(listener);
		eventMap.put(eventType, listeners);
	}

	private void removeEventListener(Class<? extends Event> eventType, EventListener listener) {
		if (eventMap.containsKey(eventType)) {
			List<EventListener> listeners = eventMap.get(eventType);
			listeners.remove(listener);
			eventMap.put(eventType, listeners);
		}
	}

	private void dispatch(Event event) {
		Class<?> eventType = event.getClass();
		if (eventMap.containsKey(eventType)) eventMap.get(eventType).forEach(listener -> listener.eventReceived(event));
	}
}
