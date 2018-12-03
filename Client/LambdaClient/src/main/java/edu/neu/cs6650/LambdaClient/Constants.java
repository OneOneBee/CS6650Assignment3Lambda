package edu.neu.cs6650.LambdaClient;

public class Constants {
	public final static int DAY_NUM = 1;
	public final static int USER_POPULATION = 100000;
	public final static  int NUM_TEST_PER_PHASE = 100;
	public final static int STEP_UPPER_BOUND = 5000;
	public final static String REGION = "us-west-2";
	public final static String[] URLS = {
			"https://5jdsu16kxk.execute-api.us-west-2.amazonaws.com/stepcounter",
			"https://5grdv6iu6a.execute-api.us-west-2.amazonaws.com/current/current",
			"https://0ps3q0x6x1.execute-api.us-west-2.amazonaws.com/single/single"
	};
}
