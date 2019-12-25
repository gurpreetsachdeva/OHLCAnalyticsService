package com.github.gurpreetsachdeva.OHLCAnalyticsService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.github.gurpreetsachdeva.OHLCAnalyticsService.publishersubscriber.Subscriber;
import com.github.gurpreetsachdeva.OHLCAnalyticsService.publishersubscriber.service.PubSubService;

public class WebSockerWorker extends Subscriber implements Runnable {

	private String topic;
	// You can even hear multiple tickers as well :)

	private PubSubService service;

	public WebSockerWorker(PubSubService pubSubService, String topic) {
		// TODO Auto-generated constructor stub
		this.service = pubSubService;
		
		List<Subscriber> subscribers = this.service.getSubscribersTopicMap().get(topic);
		if (subscribers == null) {
			subscribers = new ArrayList<>();
		}
		subscribers.add(this);
		
		this.service.getSubscribersTopicMap().put(topic,subscribers);
		System.out.println("Added");
		
		this.topic = topic;
	}

	@Override
	public void run() {

		System.out.println("Inside WebSocket");
		while (true) {
			service.broadcast();
		}

	}

	@Override
	public void addSubscriber(String topic, PubSubService pubSubService) {
		// TODO Auto-generated method stub
		pubSubService.addSubscriber(topic, this);

	}

	@Override
	public void unSubscribe(String topic, PubSubService pubSubService) {
		// TODO Auto-generated method stub
		pubSubService.removeSubscriber(topic, this);

	}

	@Override
	public void getMessagesForSubscriberOfTopic(String topic, PubSubService pubSubService) {
		// TODO Auto-generated method stub
		// pubSubService.getMessagesForSubscriberOfTopic(topic, this);

	}

}
