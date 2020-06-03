package Models;

import Interface.DataSet;

public class TestSet implements DataSet{

	private int deltaError;
	private int currentCategory;
	private int expectedCategory;
	private double[] content;
	private Weight weight;

	@Override
	public int getDeltaError() {
		return deltaError;
	}

	@Override
	public void setDeltaError(int deltaError) {
		this.deltaError = deltaError;
	}

	@Override
	public Weight getWeight() {
		return weight;
	}

	@Override
	public void setWeight(Weight relatedWeight) {
		this.weight = relatedWeight;
	}

	@Override
	public int getCurrentCategory() {
		return currentCategory;
	}

	@Override
	public int getExpectedCategory() {
		return expectedCategory;
	}

	@Override
	public void setExpectedCategory(int expectedCategory) {
		this.expectedCategory = expectedCategory;
	}

	@Override
	public void setCurrentCategory(int category) {
		this.currentCategory = category;
	}

	@Override
	public double[] getDataSet() {
		return content;
	}

	@Override
	public void setDataSet(double[] dataSet) {
		this.content = dataSet;
	}

}

