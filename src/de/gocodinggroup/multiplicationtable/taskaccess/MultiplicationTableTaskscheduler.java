package de.gocodinggroup.multiplicationtable.taskaccess;

import java.util.*;

public class MultiplicationTableTaskscheduler implements Taskscheduler<String, String> {
	private static final int MINIMUM_FACTOR = 1;
	private static final int MAXIMUM_FACTOR = 10;

	@Override
	public Task<String> getRandomTask(Category<String> category) {
		int firstFactor = getRandomNumberBetween(MINIMUM_FACTOR, MAXIMUM_FACTOR);
		int secondFactor = getRandomNumberBetween(MINIMUM_FACTOR, MAXIMUM_FACTOR);
		Task<String> task = new MultiplicationTableTask(firstFactor, secondFactor);
		return task;
	}

	private int getRandomNumberBetween(int min, int max) {
		Random random = new Random();
		return min + random.nextInt((max - min) + 1);
	}

	@Override
	public Task<String> getNextTaskForPerson(Person person, Category<String> category) {
		return getRandomTask(category);
	}

	@Override
	public void taskIsSolvedByPerson(Task<String> task, Person person) {
		System.err.println("taskIsSolvedByPerson() not implemented");
	}

	@Override
	public void taskIsNotSolvedByPerson(Task<String> task, Person person) {
		System.err.println("taskIsNotSolvedByPerson() not implemented");
	}

}
