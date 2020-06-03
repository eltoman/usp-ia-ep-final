package Interface;

import Models.Weight;

public interface DataSet {

	public double[] getDataSet();
	
	public int getDeltaError();
	
	public Weight getWeight();

	public int getCurrentCategory();
	
	public int getExpectedCategory();

	public void setDataSet(double[] dataSet);

	public void setDeltaError(int deltaError);

	public void setWeight(Weight relatedWeight);
	
	public void setExpectedCategory(int expectedCategory);

	public void setCurrentCategory(int category);
	
}
