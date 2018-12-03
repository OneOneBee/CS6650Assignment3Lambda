package edu.neu.cs6650.LambdaClient;

import java.util.Random;

public class DummyData {
	private int userID;
	private int stepCount;
	private int day;
	private int timeInterval;
	
	public DummyData(int timeInterval) {
		Random random = new Random();
		this.userID = random.nextInt(Constants.USER_POPULATION) + 1;
		this.day = Constants.DAY_NUM;
		this.timeInterval = timeInterval;
		this.stepCount = random.nextInt(Constants.STEP_UPPER_BOUND + 1);
	}
	
	public int getUserID() {
		return this.userID;
	}
	
	public int getStepCount() {
		return this.stepCount;
	}
	
	public int getDay() {
		return this.day;
	}
	
	public int getTimeInterval() {
		return this.timeInterval;
	}
}
