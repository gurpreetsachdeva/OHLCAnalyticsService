package com.github.gurpreetsachdeva.OHLCAnalyticsService.publishersubscriber;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.github.gurpreetsachdeva.OHLCAnalyticsService.model.BarResponse;
import com.github.gurpreetsachdeva.OHLCAnalyticsService.publishersubscriber.service.PubSubService;

public abstract class Subscriber {	
	
	//store all messages received by the subscriber
	private List<BarResponse> subscriberMessages = new ArrayList<BarResponse>();
	//Keep two types of subscriber, one for the clients running in JVM1 and others for Remote services
	private String type;
	
	public List<BarResponse> getSubscriberMessages() {
		return subscriberMessages;
	}
	public void setSubscriberMessages(List<BarResponse> subscriberMessages) {
		this.subscriberMessages = subscriberMessages;
	}
	
	//Add subscriber with PubSubService for a topic
	public abstract void addSubscriber(String topic, PubSubService pubSubService);
	
	//Unsubscribe subscriber with PubSubService for a topic
	public abstract void unSubscribe(String topic, PubSubService pubSubService);
	
	//Request specifically for messages related to topic from PubSubService
	public abstract void getMessagesForSubscriberOfTopic(String topic, PubSubService pubSubService);
	
	//Print all messages received by the subscriber 
	public void printMessages(){
		for(BarResponse message : subscriberMessages){
			System.out.println("Message Topic -> "+ message.getSymbol() + " : " + message);
		}
	}
}