package de.gocodinggroup.multiplicationtable.taskaccess;

import java.time.Duration;
import java.time.Instant;

public class Result<RepresentationOfTask, RepresentationOfResult> {
	Person person;
	Task<RepresentationOfTask> task;
	Instant startTimeOfTaskSolving;
	Duration  timeToSolveTask;
	RepresentationOfResult result;
}
