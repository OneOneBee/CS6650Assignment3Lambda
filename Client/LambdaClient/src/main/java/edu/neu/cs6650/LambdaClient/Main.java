package edu.neu.cs6650.LambdaClient;

import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Main {
	public static void main(String[] args) {
		int maxThread = 128;
		
        System.out.println("Start testing against : lambda");
        System.out.println("=============================================================================================");

        Phase[] phases = new Phase[4];
        
        phases[0] = new Phase("Warmup", (int)(maxThread * 0.1), 0, 2);
        phases[1] = new Phase("Loading", (int)(maxThread * 0.5), 3, 7);
        phases[2] = new Phase("Peak", maxThread, 8, 18);
        phases[3] = new Phase("Cooldown", (int)(maxThread * 0.25), 19, 23);
		
        long startTime = System.currentTimeMillis();
        
        for (int i = 0; i < 4; ++i) {
        	phases[i].run();
        }
        
        long endTime = System.currentTimeMillis();
        
        System.out.println("Total wall time : " + (endTime - startTime) / 1000);
        
        statistic(phases, startTime, endTime);

	}
	
	private static void statistic(Phase[] phases, long startTime, long endTime) {
		List<TaskResult> results = new ArrayList<TaskResult>();
		List<Long> latencies = new ArrayList<Long>();
		Map<Long, Long> map = new TreeMap<Long, Long>();
		int totalRequestCount = 0;
		
		for (Phase phase : phases) {
			results.addAll(phase.getResults());
			latencies.addAll(phase.getLatencies());
			totalRequestCount += phase.getTotalCount();
			for (long key : phase.getMap().keySet()) {
                if (!map.containsKey(key)) {
                    map.put(key, phase.getMap().get(key));
                } else {
                    map.put(key, map.get(key) + phase.getMap().get(key));
                }
	        }
		}
		
		writeToCSV(results);
		
		writeMap(map);
		
		Collections.sort(latencies);
		
	    System.out.println("===========================================================");
	    System.out.println(String.format("Total number of requests sent : %d", totalRequestCount));
//	    System.out.println(String.format("Total number of successful response : %d", successRequestCount));
	    System.out.println(String.format("Total Wall Time : %f seconds", (endTime - startTime) / 1000.0));
	    System.out.println(String.format("Overall throughput is : %f requests / second", (totalRequestCount * 1000.0 / (endTime - startTime))));
	    System.out.println(String.format("Mean latency is : %f seconds", CalcMeanLatency(latencies) / 1000.0));
	    System.out.println(String.format("Median latency is : %f seconds", GetMedian(latencies) / 1000.0));
	    System.out.println(String.format("95-th percentile latency is : %f seconds", GetPercentile(latencies, 0.95f) / 1000.0));
	    System.out.println(String.format("99-th percentile latency is : %f seconds", GetPercentile(latencies, 0.99f) / 1000.0));
	}
	
	 // Calculate mean latencies
    private static float CalcMeanLatency(List<Long> latencies)
    {
        Long total = 0L;
        Long count = 0L;
        for (Long latency : latencies) {
            if (latency >= 0) {
                total += latency;
                count++;
            }
        }

        if (count == 0) return 0.0f;
        else return 1.0f * total / count;
    }

    // Assuming latencies are sorted
    private static float GetMedian(List<Long> latencies)
    {
        int mid = latencies.size() / 2;

        if (latencies.size() % 2 == 0) {
            return (latencies.get(mid) + latencies.get(mid-1)) / 2.0f;
        } else {
            return latencies.get(mid);
        }
    }

    // Assume latencies are sorted
    private static long GetPercentile(List<Long> latencies, float percentile)
    {
        int index = (int) (latencies.size() * percentile);
        return latencies.get(index);
    }
	
	public static void writeToCSV(List<TaskResult> results) {
		FileWriter fileWriter = null;
		
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("_yyyy_MM_dd_HH_mm_ss");
			fileWriter = new FileWriter("requestTimes" + sdf.format(new Date()) + ".csv");
			fileWriter.append("StartTime,EndTime,Latency,Success,RequestType,Content\n");
			
			for (TaskResult result : results) {
				fileWriter.append(String.valueOf(result.startTime));
				fileWriter.append(",");
				fileWriter.append(String.valueOf(result.endTime));
				fileWriter.append(",");
				fileWriter.append(String.valueOf(result.latency));
				fileWriter.append(",");
				fileWriter.append(String.valueOf(result.success));
				fileWriter.append("\n");
			}
			
			System.out.println("CSV file was created successfully!");
			
		} catch (Exception e) {
			System.out.println("Error when writing CSV file");
			e.printStackTrace();
		} finally {
			try {
				fileWriter.flush();
				fileWriter.close();
			} catch (IOException e) {
				System.out.println("Error when flushing/closing fileWriter!");
				e.printStackTrace();
			}
		}
	}
	
	public static void writeMap(Map<Long, Long> map) {
		FileWriter fileWriter = null;
		
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("_yyyy_MM_dd_HH_mm_ss");
			fileWriter = new FileWriter("map" + sdf.format(new Date()) + ".csv");
			fileWriter.append("timestamp,reqNum\n");
			
			for (Long time : map.keySet()) {
				fileWriter.append(String.valueOf(time));
				fileWriter.append(",");
				fileWriter.append(String.valueOf(map.get(time)));
				fileWriter.append("\n");
			}
			
			System.out.println("CSV file was created successfully!");
			
		} catch (Exception e) {
			System.out.println("Error when writing CSV file");
			e.printStackTrace();
		} finally {
			try {
				fileWriter.flush();
				fileWriter.close();
			} catch (IOException e) {
				System.out.println("Error when flushing/closing fileWriter!");
				e.printStackTrace();
			}
		}
	}
}
