package edu.neu.cs6650.LambdaClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Phase {
	private String name;
	private int numThreads;
	private int startInterval;
	private int endInterval;
	private int totalRequestCount;
	
	private List<TaskResult> results;
	private List<Long> latencies;
	private Map<Long, Long> map;
	
	public Phase(String name, int numThreads, int start, int end) {
		this.name = name;
		this.numThreads = numThreads;
		this.startInterval = start;
		this.endInterval = end;
		this.totalRequestCount = numThreads * (end - start + 1) * Constants.NUM_TEST_PER_PHASE;
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
	
	public int getTotalCount() {
		return totalRequestCount;
	}
	
	public void run() {
		System.out.println("Running" + name + "phase : " + numThreads + " threads, each " + Constants.NUM_TEST_PER_PHASE 
				+ " times for time intervals from " + startInterval + " to " + endInterval);
		
		results = new ArrayList<TaskResult>();
		latencies = new ArrayList<Long>();
		map = new TreeMap<Long, Long>();
		List<Request> requests = new ArrayList<Request>();
		
		long startTime = System.currentTimeMillis();
		
		ExecutorService executorService = Executors.newFixedThreadPool(numThreads);
		for (int i = 0; i < numThreads; ++i) {
			Request request = new Request(startInterval, endInterval);
			requests.add(request);
			executorService.submit(request);
		}
		try {
			executorService.shutdown();
			executorService.awaitTermination(1, TimeUnit.DAYS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		long endTime = System.currentTimeMillis();
		
		for (Request request : requests) {
			results.addAll(request.getResults());
			latencies.addAll(request.getLatencies());
			for (long key : request.getMap().keySet()) {
                if (!map.containsKey(key)) {
                    map.put(key, request.getMap().get(key));
                } else {
                    map.put(key, map.get(key) + request.getMap().get(key));
                }
            }
		}
		
		System.out.println(String.format("%s phase complete : Time %f seconds",
                        name, (endTime - startTime) / 1000.0));
	}
	
}
