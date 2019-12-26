package com.github.gurpreetsachdeva.OHLCAnalyticsService.publishersubscriber.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

import com.github.gurpreetsachdeva.OHLCAnalyticsService.model.BarResponse;
import com.github.gurpreetsachdeva.OHLCAnalyticsService.publishersubscriber.Subscriber;

public class PubSubService {

	private int MAX_HISTORY_SIZE=100000;
	// Keeps List of subscriber topic wise, using set to prevent duplicates
	private Map<String, List<Subscriber>> subscribersTopicMap = new ConcurrentHashMap<String, List<Subscriber>>();

	// Holds BarResponses published by publishers
	private BlockingQueue<BarResponse> queue;

	private Map<String, List<BarResponse>> historyBars = new ConcurrentHashMap<>();

	// Adds BarResponse sent by publisher to queue
	public void addBarResponseToQueue(BarResponse br) {
		try {
			queue.put(br);
			List<BarResponse> responses = historyBars.get(br.getSymbol());
			if (responses == null) {
				responses = new ArrayList<>();
			}
			responses.add(br);
			historyBars.put(br.getSymbol(), responses);

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// Add a new Subscriber for a topic
	public void addSubscriber(String topic, Subscriber subscriber) {

		if (subscribersTopicMap.containsKey(topic)) {
			List<Subscriber> subscribers = subscribersTopicMap.get(topic);
			subscribers.add(subscriber);
			System.out.println("Adding Subscriber");
			subscribersTopicMap.put(topic, subscribers);
		} else {
			List<Subscriber> subscribers = new ArrayList<>();
			subscribers.add(subscriber);
			subscribersTopicMap.put(topic, subscribers);
			System.out.println("Adding Subscriber");

		}
	}

	// Remove an existing subscriber for a topic
	public void removeSubscriber(String topic, Subscriber subscriber) {

		if (subscribersTopicMap.containsKey(topic)) {
			List<Subscriber> subscribers = subscribersTopicMap.get(topic);
			subscribers.remove(subscriber);
		}
	}

	// Broadcast new BarResponses added in queue to All subscribers of the topic.
	// BarResponsesQueue will be empty after broadcasting
	/*
	 * public void broadcast() { try { BarResponse br = queue.take(); String topic =
	 * br.getSymbol(); List<Subscriber> subscribersOfTopic =
	 * subscribersTopicMap.get(topic); if (subscribersOfTopic != null) { for
	 * (Subscriber subscriber : subscribersOfTopic) { // add broadcasted BarResponse
	 * to subscribers BarResponse queue List<BarResponse> subscriberBarResponses =
	 * subscriber.getBarResponses(); subscriberBarResponses.add(br);
	 * System.out.println(Thread.currentThread().getName()+"   Subscriber     "
	 * +subscriber+"    "+br);
	 * 
	 * }
	 * 
	 * } } catch (InterruptedException e) { // TODO Auto-generated catch block
	 * e.printStackTrace(); } //System.out.println("Passing Bar Name as topic"); }
	 * 
	 */
	// Sends BarResponses about a topic for subscriber at any point
	// , Subscriber subscriber
	public void getCurrentStreamingBars() {
		System.out.println("Streaming new Entries in the file");

		BarResponse br;
		try {
			while (true) {
				br = queue.take();
			//	System.out.println("Taking an entry out");
				String topic = br.getSymbol();
			//	System.out.println(br);
			//	System.out.println(subscribersTopicMap);
				List<Subscriber> subscribersOfTopic = subscribersTopicMap.get(topic);
			//	System.out.println(subscribersOfTopic);
				if (subscribersOfTopic != null) {
					for (Subscriber subscriber : subscribersOfTopic) {
						// add broadcasted BarResponse to subscribers BarResponse queue
						
						List<BarResponse> subscriberBarResponses = subscriber.getBarResponses();
						if(subscriberBarResponses==null) {
							subscriberBarResponses=new ArrayList<BarResponse>();
						}
						subscriberBarResponses.add(br);
						subscriber.setBarResponses(subscriberBarResponses);
						//System.out.println("Calling callback for subscriber"+subscriber);
						subscriber.callBack(br.toString());

					}
				}
			}

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Exiting Streaming");
	}

	public PubSubService(BlockingQueue<BarResponse> queue) {
		super();
		this.queue = queue;
	}

	public PubSubService() {
		// TODO Auto-generated constructor stub
	}

	public BlockingQueue<BarResponse> getQueue() {
		return queue;
	}

	public void getMessagesForSubscriberOfTopic(String topic, Subscriber s) {
		// TODO Auto-generated method stub
		System.out.println("Running for Topic"+topic);
		System.out.println(s);
		List<BarResponse> responses = this.historyBars.get(topic);

		if (responses != null) {
			
			List<Subscriber> subscribersOfTopic = subscribersTopicMap.get(topic);
			System.out.println(subscribersOfTopic);
			
				for (Subscriber ls:subscribersOfTopic) {
					for (BarResponse br : responses) {
					System.out.println(ls);
					if(!ls.isNewSubscriber()) {
					System.out.println("Calling History Bars for every subscriber");

					ls.callBack(br.toString());
					}
					ls.setNewSubscriber(false);;
				}
				
				
			}
		}

		// Queue Current Bars from queue
		System.out.println("Calling Live bars");
		this.getCurrentStreamingBars();

	}

	public Map<String, List<Subscriber>> getSubscribersTopicMap() {
		return subscribersTopicMap;
	}

	public void setSubscribersTopicMap(Map<String, List<Subscriber>> subscribersTopicMap) {
		this.subscribersTopicMap = subscribersTopicMap;
	}

	public void setQueue(BlockingQueue<BarResponse> queue) {
		this.queue = queue;
	}

}
