package com.github.gurpreetsachdeva.OHLCAnalyticsService.publishersubscriber;

import com.github.gurpreetsachdeva.OHLCAnalyticsService.model.BarResponse;

public interface Publisher {
	
		
		//Publishes new message to PubSubService
		void publish(BarResponse br);
	

}
