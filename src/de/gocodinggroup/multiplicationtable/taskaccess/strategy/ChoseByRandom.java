package de.gocodinggroup.multiplicationtable.taskaccess.strategy;

import java.util.*;

import de.gocodinggroup.multiplicationtable.taskaccess.MultiplicationTableTask;
import de.gocodinggroup.multiplicationtable.taskaccess.Person;
import de.gocodinggroup.multiplicationtable.taskaccess.Result;
import de.gocodinggroup.multiplicationtable.taskaccess.Task;
import de.gocodinggroup.multiplicationtable.taskaccess.Taskscheduler;

public class ChoseByRandom implements Taskscheduler<String, String> {
	private static final int MINIMUM_FACTOR = 1;
	private static final int MAXIMUM_FACTOR = 10;

	@Override
	public Task<String> getNextTaskForPerson(Person person) {
		return getRandomTask();
	}

	private Task<String> getRandomTask() {
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
	public void storeResult(Result<String, String> result) {
		// TODO Auto-generated method stub

	}
}
