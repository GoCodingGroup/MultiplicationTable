package de.gocodinggroup.multiplicationtable.taskaccess;

public interface Taskscheduler<RepresentationOfTask, RepresentationOfResult> {
	Task<RepresentationOfTask> getNextTaskForPerson(Person person);
	void storeResult(Result<RepresentationOfTask, RepresentationOfResult> result);
}
