package de.gocodinggroup.multiplicationtable.taskaccess;

public class MultiplicationTableTask implements Task<String> {
	private int firstFactor;
	private int secondFactor;

	public MultiplicationTableTask(int firstFactor, int secondFactor) {
		this.firstFactor = returnValueBetweenOneAndHundredBordersIncluded(firstFactor);
		this.secondFactor = returnValueBetweenOneAndHundredBordersIncluded(secondFactor);
	}

	private int returnValueBetweenOneAndHundredBordersIncluded(int number) {
		number = returnValueSmallerThanOrEqualToHundred(number);
		number = returnValueGreaterThanOrEqualToOne(number);
		return number;
	}

	private int returnValueSmallerThanOrEqualToHundred(int number) {
		if (number > 100) {
			number = 100;
		}
		return number;
	}

	private int returnValueGreaterThanOrEqualToOne(int number) {
		if (number < 1) {
			number = 1;
		}
		return number;
	}

	@Override
	public String getProblem() {
		String firstFactorAsString = String.valueOf(firstFactor);
		String secondFactorAsString = String.valueOf(secondFactor);
		return firstFactorAsString + " * " + secondFactorAsString;
	}

	@Override
	public String getSolution() {
		int result = firstFactor * secondFactor;
		String resultAsString = String.valueOf(result);
		return resultAsString;
	}

	@Override
	public long getID() {
		return (firstFactor - 1) * 100 + secondFactor;
	}

	public int getFirstFactor() {
		return firstFactor;
	}

	public int getSecondFactor() {
		return secondFactor;
	}

}
