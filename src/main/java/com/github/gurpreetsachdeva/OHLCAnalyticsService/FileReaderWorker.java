package com.github.gurpreetsachdeva.OHLCAnalyticsService;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.github.gurpreetsachdeva.OHLCAnalyticsService.model.TradesData;

public class FileReaderWorker implements Runnable {

	private  BlockingQueue<TradesData> queue;


	private String filePath;
	

	@Override
	public void run() {
		// TODO Auto-generated method stub
		// Read File and keep on appending to the Concurrent Queue,if file gets modified , keep blocking and pushing
		System.out.println("Inside FileReaderWorker");

		JSONParser jsonParser = new JSONParser();
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(filePath));
			String line = reader.readLine();
			JSONObject obj = (JSONObject) jsonParser.parse(line);
			while (line != null) {
				parseTradeLine(obj);
				obj = (JSONObject) jsonParser.parse(line);
				line = reader.readLine();
			}
			reader.close();
		} catch (IOException | ParseException e) {
			e.printStackTrace();
		}
		
		System.out.println("Exiting FileReaderWorker");


	}

	private void parseTradeLine(JSONObject obj) {
		Double price = Double.valueOf(String.valueOf(obj.get("P")));
		Long time = Long.valueOf(String.valueOf((obj.get("TS2"))));
		String sym = (String) obj.get("sym");
		Double quantity = Double.valueOf(String.valueOf(obj.get("Q")));
		TradesData td = new TradesData(sym, price, quantity, time);
		

		try {
			queue.put(td);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public FileReaderWorker(BlockingQueue<TradesData> queue, String filePath) {
		super();
		this.queue = queue;
		this.filePath = filePath;
	}



	

}
