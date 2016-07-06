package de.gocodinggroup.multiplicationtable.taskaccess;

import java.util.Random;

public class MultiplicationTableTaskscheduler implements Taskscheduler<String> {
	private static final int MINIMUM_FACTOR = 1;
	private static final int MAXIMUM_FACTOR = 10;

	@Override
	public Task<String> getRandomTask() {
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
	public Task<String> getNextTaskForPerson(Person person) {
		// TODO Auto-generated method stub
		return getRandomTask();
	}

	@Override
	public void taskIsSolvedByPerson(Task<String> task, Person person) {
		// TODO Auto-generated method stub

	}

	@Override
	public void taskIsNotSolvedByPerson(Task<String> task, Person person) {
		// TODO Auto-generated method stub

	}

}
