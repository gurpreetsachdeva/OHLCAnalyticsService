package com.github.gurpreetsachdeva.OHLCAnalyticsService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import com.github.gurpreetsachdeva.OHLCAnalyticsService.model.BarResponse;
import com.github.gurpreetsachdeva.OHLCAnalyticsService.publishersubscriber.Subscriber;
import com.github.gurpreetsachdeva.OHLCAnalyticsService.publishersubscriber.service.PubSubService;

public class WebSockerWorker extends Subscriber implements Runnable {

	private String topic;
	// You can even hear multiple tickers as well :)

	private BlockingQueue<BarResponse> barResponses;

	public WebSockerWorker(BlockingQueue<BarResponse> barResponses, String topic) {
		// TODO Auto-generated constructor stub

		this.topic = topic;
		this.barResponses = barResponses;
		List<Subscriber> subscriber = this.getSubscribersTopicMap().get(topic);
		if (subscriber == null) {
			subscriber = new ArrayList<>();
			System.out.println("Adding this");
			
			

		}
		subscriber.add(this);
		this.getSubscribersTopicMap().put(topic, subscriber);
	}

	@Override
	public void run() {
		try {
			// TODO Auto-generated method stub
			while (true) {
				BarResponse br = barResponses.take();
				//System.out.println(br);
				List<Subscriber> sb = this.getSubscribersTopicMap().get(br.getSymbol());
				if (sb != null) {
					for (Subscriber s : sb) {
						List<BarResponse> responses = s.getSubscriberMessages();
						responses.add(br);
						System.out.println(Thread.currentThread().getName() + ":    " + br);
					}
				}

			}

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Inside WebSocket");

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
