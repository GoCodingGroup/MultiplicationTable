package de.lezleoh.multiplicationtable.taskaccess;

public interface Taskscheduler<Representation> {
	Task<Representation> getRandomTask();

	Task<Representation> getNextTaskForPerson(Person person);

	void taskIsSolvedByPerson(Task<Representation> task, Person person);

	void taskIsNotSolvedByPerson(Task<Representation> task, Person person);
}
