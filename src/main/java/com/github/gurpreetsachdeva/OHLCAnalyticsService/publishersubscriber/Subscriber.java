package com.github.gurpreetsachdeva.OHLCAnalyticsService.publishersubscriber;

import java.util.List;

import com.github.gurpreetsachdeva.OHLCAnalyticsService.model.BarResponse;
import com.github.gurpreetsachdeva.OHLCAnalyticsService.publishersubscriber.service.PubSubService;

public interface Subscriber {	
	
	//store all messages received by the subscriber
	
	
	
	//Add subscriber with PubSubService for a topic
	public  void addSubscriber(String topic, PubSubService pubSubService);
	
	//Unsubscribe subscriber with PubSubService for a topic
	public  void unSubscribe(String topic, PubSubService pubSubService);
	
	//Request specifically for messages related to topic from PubSubService
	public  void getMessagesForSubscriberOfTopic(String topic, PubSubService pubSubService);
	

	//Every subscriber has to overwrite this.
	public void callBack(String message);
	
	public List<BarResponse> getBarResponses();
	public void setBarResponses(List<BarResponse> br);
	public boolean isNewSubscriber();
	public void setNewSubscriber(boolean newSubscriber);

	
}