package com.github.gurpreetsachdeva.OHLCAnalyticsService.publishersubscriber.service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.BlockingQueue;

import com.github.gurpreetsachdeva.OHLCAnalyticsService.model.BarResponse;
import com.github.gurpreetsachdeva.OHLCAnalyticsService.publishersubscriber.Subscriber;

public class PubSubService {

	// Keeps set of subscriber topic wise, using set to prevent duplicates
	Map<String, Set<Subscriber>> subscribersTopicMap = new HashMap<String, Set<Subscriber>>();

	// Holds BarResponses published by publishers
	BlockingQueue<BarResponse> queue;

	

	// Adds BarResponse sent by publisher to queue
	public void addBarResponseToQueue(BarResponse br) {
		System.out.println("queue size"+queue.size());
		queue.add(br);
	}

	// Add a new Subscriber for a topic
	public void addSubscriber(String topic, Subscriber subscriber) {

		if (subscribersTopicMap.containsKey(topic)) {
			Set<Subscriber> subscribers = subscribersTopicMap.get(topic);
			subscribers.add(subscriber);
			subscribersTopicMap.put(topic, subscribers);
		} else {
			Set<Subscriber> subscribers = new HashSet<Subscriber>();
			subscribers.add(subscriber);
			subscribersTopicMap.put(topic, subscribers);
		}
	}

	// Remove an existing subscriber for a topic
	public void removeSubscriber(String topic, Subscriber subscriber) {

		if (subscribersTopicMap.containsKey(topic)) {
			Set<Subscriber> subscribers = subscribersTopicMap.get(topic);
			subscribers.remove(subscriber);
			subscribersTopicMap.put(topic, subscribers);
		}
	}

	// Broadcast new BarResponses added in queue to All subscribers of the topic.
	// BarResponsesQueue will be empty after broadcasting
	public void broadcast() {
		while (queue.size() != 0) {
			BarResponse br;
			try {
				br = queue.take();
				String topic = br.getSymbol();
				Set<Subscriber> subscribersOfTopic = subscribersTopicMap.get(topic);
				for (Subscriber subscriber : subscribersOfTopic) {
					// add broadcasted BarResponse to subscribers BarResponse queue
					List<BarResponse> subscriberBarResponses = subscriber.getSubscriberMessages();
					subscriberBarResponses.add(br);
					subscriber.setSubscriberMessages(subscriberBarResponses);

				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	

	// Sends BarResponses about a topic for subscriber at any point
	//, Subscriber subscriber
	public void getBarResponsesForSubscriberOfTopic() {
		while (queue.size() != 0) {
			BarResponse br;
			try {
				br = queue.take();
				System.out.println("Taking an entry out");
				String topic=br.getSymbol();
				Set<Subscriber> subscribersOfTopic = subscribersTopicMap.get(topic);
				for (Subscriber subscriber : subscribersOfTopic) {
					// add broadcasted BarResponse to subscribers BarResponse queue
					List<BarResponse> subscriberBarResponses = subscriber.getSubscriberMessages();
					subscriberBarResponses.add(br);
					subscriber.setSubscriberMessages(subscriberBarResponses);

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
	public BlockingQueue<BarResponse> getQueue() {
		return queue;
	}

}
