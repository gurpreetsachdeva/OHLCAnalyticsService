package com.github.gurpreetsachdeva.OHLCAnalyticsService;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import com.github.gurpreetsachdeva.OHLCAnalyticsService.model.BarResponse;
import com.github.gurpreetsachdeva.OHLCAnalyticsService.model.TradesData;

/**
 * Hello world!
 *
 */
public class App 
{
	private String filePath = "/home/gurpreet/trades-data/trades.json";
	private Long TIME_CONVERTER = 1000000000L;
	private  BlockingQueue<TradesData> queue = new ArrayBlockingQueue<>(10000);
	private  BlockingQueue<BarResponse> barResponses = new ArrayBlockingQueue<>(10000);
	

//	private Map<String,Set<>>
	

	
    public static void main( String[] args ) throws InterruptedException
    {
    	
    	//MAKE SURE YOU RUN ONE CONSUME AT LEAST
        //new Thread(new WebSockerWorker(app.getBarResponses(),topicName[1]),"BarConsumer-"+threadCount+topicName[0]).start();
        // java App filePath:/home/gurpreet/trades-data/trades.json Worker3:XETHXXBT Worker4:XICNXXBT
    	// You can create N workers for consumption
    

    	App app=new App();
    	handleCommandLineArgs(args, app);
        Thread worker1=new Thread(new FileReaderWorker(app.getQueue(),app.getFilePath()),"FileReaderThreadPool");
        Thread worker2=new Thread(new FSMWorker(app.getQueue(),app.getTIME_CONVERTER(),app.getBarResponses()),"BarProducer");
        worker1.start();
        worker2.start();
      
             
    }




	private static void handleCommandLineArgs(String[] args, App app) {
		System.out.println("Args Passed are "+args);
		System.out.println("Args Length is "+args.length);
		int threadCount=1;
		for (String arg : args) {
        	
        	String[] topicName=arg.split(":");
        	if(topicName[0].equals("filePath")) {
        		app.setFilePath(topicName[1]);
        	}
        	else {
        	new Thread(new WebSockerWorker(app.getBarResponses(),topicName[1]),"BarConsumer-"+threadCount+topicName[0]).start();
        	threadCount+=1;
        	}
        }
	}




	public String getFilePath() {
		return filePath;
	}




	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}




	public Long getTIME_CONVERTER() {
		return TIME_CONVERTER;
	}




	public void setTIME_CONVERTER(Long tIME_CONVERTER) {
		TIME_CONVERTER = tIME_CONVERTER;
	}





	public BlockingQueue<TradesData> getQueue() {
		return queue;
	}




	public void setQueue(BlockingQueue<TradesData> queue) {
		this.queue = queue;
	}




	public BlockingQueue<BarResponse> getBarResponses() {
		return barResponses;
	}




	public void setBarResponses(BlockingQueue<BarResponse> barResponses) {
		this.barResponses = barResponses;
	}
}
