package de.gocodinggroup.multiplicationtable.util;

/**
 * Interface for receiving events. This is meant to be implemented using java 8
 * lambdas
 * 
 * @author Dominik
 *
 */
public interface EventListener {
	public void eventReceived(Event event);
}
