package com.github.gurpreetsachdeva.OHLCAnalyticsService.publishersubscriber.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

import com.github.gurpreetsachdeva.OHLCAnalyticsService.model.BarResponse;
import com.github.gurpreetsachdeva.OHLCAnalyticsService.publishersubscriber.Subscriber;

public class PubSubService {

	// Keeps List of subscriber topic wise, using set to prevent duplicates
	private Map<String, List<Subscriber>> subscribersTopicMap=new ConcurrentHashMap<String, List<Subscriber>>();

	// Holds BarResponses published by publishers
	private BlockingQueue<BarResponse> queue;

	// Adds BarResponse sent by publisher to queue
	public void addBarResponseToQueue(BarResponse br) {
		try {
			queue.put(br);
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
		} else {
			List<Subscriber> subscribers = new ArrayList<>();
			subscribers.add(subscriber);
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
	public void broadcast() {
		try {
			BarResponse br = queue.take();
			//System.out.println(br);
			String topic = br.getSymbol();
				//System.out.println(subscribersTopicMap);
				//System.out.println(topic);
				

				List<Subscriber> subscribersOfTopic = subscribersTopicMap.get(topic);
				//System.out.println(subscribersOfTopic);
				//System.out.println(Thread.currentThread().getName()+"Thread.currentThread().getName()*****************");
				//System.out.println(queue.size());
				if (subscribersOfTopic != null) {
					for (Subscriber subscriber : subscribersOfTopic) {
						// add broadcasted BarResponse to subscribers BarResponse queue
						List<BarResponse> subscriberBarResponses = subscriber.getSubscriberMessages();
						subscriberBarResponses.add(br);
						System.out.println(Thread.currentThread().getName()+"   Subscriber     "+subscriber+"    "+br);

					}
				
			}
		}
		catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//System.out.println("Passing Bar Name as topic");
	}

	// Sends BarResponses about a topic for subscriber at any point
	// , Subscriber subscriber
	public void getBarResponsesForSubscriberOfTopic() {
		while (queue.size() != 0) {
			BarResponse br;
			try {
				br = queue.take();
				System.out.println("Taking an entry out");
				String topic = br.getSymbol();
				List<Subscriber> subscribersOfTopic = subscribersTopicMap.get(topic);
				for (Subscriber subscriber : subscribersOfTopic) {
					// add broadcasted BarResponse to subscribers BarResponse queue
					List<BarResponse> subscriberBarResponses = subscriber.getSubscriberMessages();
					subscriberBarResponses.add(br);

				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

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

	public void getMessagesForSubscriberOfTopic(String topic) {
		// TODO Auto-generated method stub

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
