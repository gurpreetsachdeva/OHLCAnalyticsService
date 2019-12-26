package com.github.gurpreetsachdeva.OHLCAnalyticsService;

import java.util.ArrayList;
import java.util.List;

import com.github.gurpreetsachdeva.OHLCAnalyticsService.model.BarResponse;
import com.github.gurpreetsachdeva.OHLCAnalyticsService.publishersubscriber.Subscriber;
import com.github.gurpreetsachdeva.OHLCAnalyticsService.publishersubscriber.service.PubSubService;

public class WebSockerWorker implements Runnable,Subscriber {

	private String topic;
	// You can even hear multiple tickers as well :)

	private PubSubService service;
	
	List<BarResponse> responses;

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
		//Register the subscriber
		this.getMessagesForSubscriberOfTopic(topic, service);
	

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
		
		System.out.println("Calling Service");
		// TODO Auto-generated method stub
		 pubSubService.getMessagesForSubscriberOfTopic(topic, this);

	}

	@Override
	public void callBack(String message) {
		// TODO Auto-generated method stub
		System.out.println(Thread.currentThread().getName()+"    "+message);
		
	}

	@Override
	public List<BarResponse> getBarResponses() {
		// TODO Auto-generated method stub
		return this.responses;
	}

	@Override
	public void setBarResponses(List<BarResponse> br) {
		// TODO Auto-generated method stub
		this.responses=br;
		
	}

	@Override
	public String toString() {
		return "WebSockerWorker [topic=" + topic + "]";
	}

}
