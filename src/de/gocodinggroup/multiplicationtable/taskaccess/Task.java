package de.gocodinggroup.multiplicationtable.taskaccess;

public interface Task<Representation> {
	Representation getProblem();
	Representation getSolution();
	long getID();
}
