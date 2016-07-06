package de.gocodinggroup.multiplicationtable.taskaccess;

public interface Taskscheduler<R1, R2> {
	Task<R1> getRandomTask(Category<R2> category);

	Task<R1> getNextTaskForPerson(Person person, Category<R2> category);

	void taskIsSolvedByPerson(Task<R1> task, Person person);

	void taskIsNotSolvedByPerson(Task<R1> task, Person person);
}
