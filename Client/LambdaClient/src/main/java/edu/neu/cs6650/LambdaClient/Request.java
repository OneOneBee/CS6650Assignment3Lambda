package edu.neu.cs6650.LambdaClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.amazonaws.services.lambda.AWSLambda;
import com.amazonaws.services.lambda.AWSLambdaClientBuilder;
import com.amazonaws.services.lambda.model.InvokeRequest;
import com.amazonaws.services.lambda.model.InvokeResult;

public class Request implements Runnable {
	private int startInterval;
	private int endInterval;
	
	private List<TaskResult> results;
	private List<Long> latencies;
	private Map<Long, Long> map;
	
	public Request(int start, int end) {
		this.startInterval = start;
		this.endInterval = end;
	}
	
	public List<TaskResult> getResults() {
		return results;
	}
	
	public List<Long> getLatencies() {
		return latencies;
	}
	
	public Map<Long, Long> getMap() {
		return map;
	}
	
	public void run() {
		results = new ArrayList<TaskResult>();
		latencies = new ArrayList<Long>();
		map = new TreeMap<Long, Long>();
		
		for (int time = startInterval; time <= endInterval; ++time) {
			for (int i = 0; i < Constants.NUM_TEST_PER_PHASE; ++i) {
				DummyData data1 = new DummyData(time);
				DummyData data2 = new DummyData(time);
				DummyData data3 = new DummyData(time);
				
				String postPayload1 = buildPostPayload(data1);
				invoke("post", postPayload1);
				
				String postPayload2 = buildPostPayload(data2);
				invoke("post", postPayload2);
				
				String getCurrentPayload = buildGetCurrentPayload(data1.getUserID());
				invoke("current", getCurrentPayload);
				
				String getSinglePayload = buildGetSinglePayload(data2.getUserID(), data2.getDay());
				invoke("single", getSinglePayload);
				
				String postPayload3 = buildPostPayload(data3);
				invoke("post", postPayload3);
				
			}
		}
	}
	
	/**

        
		AWSLambdaClientBuilder builder = AWSLambdaClientBuilder.standard().withRegion("us-west-2");
		AWSLambda client = builder.build();
		InvokeRequest req = new InvokeRequest()
                .withFunctionName("post")
                .withPayload("{\n" +
                        " \"userid\": \"4\",\n" +
                        " \"day\": \"1\",\n" +
                        " \"timeinterval\": \"3\",\n" +
                        " \"stepcount\": \"437\"\n" +
                        "}"); // 4/1/3/437
		InvokeResult result = client.invoke(req);
		

	 */
	private void invoke(String function, String payload) {
		long stime = System.currentTimeMillis();
		TaskResult trst = new TaskResult();
		
		AWSLambdaClientBuilder builder = AWSLambdaClientBuilder.standard().withRegion("us-west-2");
		AWSLambda client = builder.build();
		InvokeRequest req = new InvokeRequest()
                .withFunctionName(function)
                .withPayload(payload);
		InvokeResult result = client.invoke(req);
		
		long etime = System.currentTimeMillis();
		
		trst.startTime = stime / 1000;
		trst.endTime = etime / 1000;
		trst.latency = etime - stime;
		results.add(trst);
		latencies.add(etime - stime);
		if (!map.containsKey(trst.startTime)) {
			map.put(trst.startTime, (long)1);
		} else {
			map.put(trst.startTime, map.get(trst.startTime) + 1);
		}
	}
	
	private String buildPostPayload(DummyData data) {
		String userid = String.valueOf(data.getUserID());
		String day = String.valueOf(data.getDay());
		String timeinterval = String.valueOf(data.getTimeInterval());
		String stepcount = String.valueOf(data.getStepCount());
		
		StringBuilder sb = new StringBuilder();
		
		sb.append("{\n");
		sb.append(" \"userid\":");
		sb.append(" \"" + userid + "\",\n");
		sb.append(" \"day\":");
		sb.append(" \"" + day + "\",\n");
		sb.append(" \"timeinterval\":");
		sb.append(" \"" + timeinterval + "\",\n");
		sb.append(" \"stepcount\":");
		sb.append(" \"" + stepcount + "\"\n");
		sb.append("}");
		
		return sb.toString();
	}
	
	private String buildGetCurrentPayload(int userId) {
		String id = String.valueOf(userId);
		
		StringBuilder sb = new StringBuilder();
		sb.append("{\n");
		sb.append(" \"userid\":");
		sb.append(" \"" + id + "\"\n");
		sb.append("}");
		
		return sb.toString();
	}
	
	private String buildGetSinglePayload(int userId, int day) {
		String id = String.valueOf(userId);
		String daystr = String.valueOf(day);
		
		StringBuilder sb = new StringBuilder();
		sb.append("{\n");
		sb.append(" \"userid\":");
		sb.append(" \"" + id + "\",\n");
		sb.append(" \"day\":");
		sb.append(" \"" + daystr + "\"\n");
		sb.append("}");
		
		return sb.toString();
	}
}
