package de.lezleoh.multiplicationtable.taskaccess;

public interface Task<Representation> {
	Representation getProblem();
	Representation getSolution();
	long getID();
}
