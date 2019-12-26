package com.github.gurpreetsachdeva.OHLCAnalyticsService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.UnknownHostException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import com.github.gurpreetsachdeva.OHLCAnalyticsService.model.BarResponse;
import com.github.gurpreetsachdeva.OHLCAnalyticsService.model.TradesData;
import com.github.gurpreetsachdeva.OHLCAnalyticsService.publishersubscriber.service.PubSubService;

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
	private PubSubService pubSubService=new PubSubService();
	private int port=8887;
	

//	private Map<String,Set<>>
	

	
    public static void main( String[] args ) throws InterruptedException
    {
    	
    	/*MAKE SURE YOU RUN ONE CONSUME AT LEAST
        new Thread(new WebSockerWorker(app.getBarResponses(),topicName[1]),"BarConsumer-"+threadCount+topicName[0]).start();
        java App filePath:/home/gurpreet/trades-data/trades.json Worker3:XETHXXBT Worker4:XICNXXBT
    	You can create N workers for consumption*/
    
    	App app=new App();
    	app.getPubSubService().setQueue(new ArrayBlockingQueue<>(10000));
    	handleCommandLineArgs(args, app);//Also handles the creation of Producers
        Thread worker1=new Thread(new FileReaderWorker(app.getQueue(),app.getFilePath()),"FileReaderThreadPool");//In Future instead of a single producer ,make 10 threads with same design
        Thread worker2=new Thread(new FSMWorker(app.getQueue(),app.getTIME_CONVERTER(),app.getPubSubService()),"BarProducer");
        worker1.start();
        worker2.start();
        
        try {
			app.startWebSocketServer();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
             
    }

    public void startWebSocketServer() throws IOException, InterruptedException {
    	System.out.println(this.port);
    	TradeBarUpdater s = new TradeBarUpdater(this.port);
    	s.setService(this.getPubSubService());
		s.start();
		System.out.println( "WebSocket started on port: " + s.getPort() );

		BufferedReader sysin = new BufferedReader( new InputStreamReader( System.in ) );
		while ( true ) {
			String in = sysin.readLine();
			s.broadcast( in );
			if( in.equals( "exit" ) ) {
				s.stop(1000);
				break;
			}
		}
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
        	new Thread(new WebSockerWorker(app.getPubSubService(),topicName[1]),"BarConsumer-"+threadCount+topicName[0]).start();
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




	public PubSubService getPubSubService() {
		return pubSubService;
	}




	public void setPubSubService(PubSubService pubSubService) {
		this.pubSubService = pubSubService;
	}
}
