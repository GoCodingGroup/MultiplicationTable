package de.gocodinggroup.multiplicationtable.taskaccess.test;

import static org.junit.Assert.*;

import org.junit.*;

import de.gocodinggroup.multiplicationtable.taskaccess.*;

public class MultiplicationTableTaskTest {

	@Test
	public void testFirstFactorIsGreaterThanOrEqualToOne() {
		int firstFactor = 0;
		int secondFactor = 1;
		MultiplicationTableTask task = new MultiplicationTableTask(firstFactor, secondFactor);
		assertEquals(1, task.getFirstFactor());
	}

	@Test
	public void testSecondFactorIsGreaterThanOrEqualToOne() {
		int firstFactor = 1;
		int secondFactor = 0;
		MultiplicationTableTask task = new MultiplicationTableTask(firstFactor, secondFactor);
		assertEquals(1, task.getSecondFactor());
	}

	@Test
	public void testFirstFactorIsSmallerThanOrEqualToHundred() {
		int firstFactor = 101;
		int secondFactor = 1;
		MultiplicationTableTask task = new MultiplicationTableTask(firstFactor, secondFactor);
		assertEquals(100, task.getFirstFactor());
	}

	@Test
	public void testSecondFactorIsSmallerThanOrEqualToHundred() {
		int firstFactor = 1;
		int secondFactor = 101;
		MultiplicationTableTask task = new MultiplicationTableTask(firstFactor, secondFactor);
		assertEquals(100, task.getSecondFactor());
	}

	@Test
	public void testGetProblem() {
		int firstFactor = 1;
		int secondFactor = 2;
		MultiplicationTableTask task = new MultiplicationTableTask(firstFactor, secondFactor);
		assertEquals("1 * 2", task.getProblem());
	}

	@Test
	public void testGetSolution() {
		int firstFactor = 5;
		int secondFactor = 7;
		MultiplicationTableTask task = new MultiplicationTableTask(firstFactor, secondFactor);
		assertEquals("35", task.getSolution());
	}

	@Test
	public void testGetIDFirstFactorIsOneSecondFactorIsOne() {
		int firstFactor = 1;
		int secondFactor = 1;
		MultiplicationTableTask task = new MultiplicationTableTask(firstFactor, secondFactor);
		assertEquals(1, task.getID());
	}

	@Test
	public void testGetIDFirstFactorIsOneSecondFactorIs100() {
		int firstFactor = 1;
		int secondFactor = 100;
		MultiplicationTableTask task = new MultiplicationTableTask(firstFactor, secondFactor);
		assertEquals(100, task.getID());
	}

	@Test
	public void testGetIDFirstFactorIsTwoSecondFactorIsOne() {
		int firstFactor = 2;
		int secondFactor = 1;
		MultiplicationTableTask task = new MultiplicationTableTask(firstFactor, secondFactor);
		assertEquals(101, task.getID());
	}

	@Test
	public void testGetIDFirstFactorIsTwoSecondFactorIsHundred() {
		int firstFactor = 2;
		int secondFactor = 100;
		MultiplicationTableTask task = new MultiplicationTableTask(firstFactor, secondFactor);
		assertEquals(200, task.getID());
	}
}
