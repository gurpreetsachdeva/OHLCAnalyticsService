package com.github.gurpreetsachdeva.OHLCAnalyticsService;



import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import com.github.gurpreetsachdeva.OHLCAnalyticsService.model.BarResponse;
import com.github.gurpreetsachdeva.OHLCAnalyticsService.publishersubscriber.Subscriber;
import com.github.gurpreetsachdeva.OHLCAnalyticsService.publishersubscriber.service.PubSubService;

/**
* A simple WebSocket Subscriber
*/
public class TradeBarUpdater extends WebSocketServer implements Subscriber{

	private PubSubService service;
	private String topic;
	private boolean newSubscriber;
	private Map<String,List<WebSocket>> topicConnMap=new HashMap<String, List<WebSocket>>();
	private Map<WebSocket,String>	reverseLookup=new HashMap<>();
	public boolean isNewSubscriber() {
		return newSubscriber;
	}

	public void setNewSubscriber(boolean newSubscriber) {
		this.newSubscriber = newSubscriber;
	}

	List <BarResponse> responses;
	public PubSubService getService() {
		return service;
	}

	public void setService(PubSubService service) {
		this.service = service;
	}

	public TradeBarUpdater( int port ) throws UnknownHostException {
		super( new InetSocketAddress( port ) );
	}

	public TradeBarUpdater( InetSocketAddress address ) {
		super( address );
	}

	@Override
	public void onOpen( WebSocket conn, ClientHandshake handshake ) {
		conn.send("Welcome to the server!"); //This method sends a message to the new client
	//	broadcast( "new connection: " + handshake.getResourceDescriptor() ); //This method sends a message to all clients connected
		System.out.println( conn.getRemoteSocketAddress().getAddress().getHostAddress() + " entered the room!" );
	}

	@Override
	public void onClose( WebSocket conn, int code, String reason, boolean remote ) {
	//	broadcast( conn + " has left the room!" );
		System.out.println( conn + " has left the room!" );
		String topic=this.reverseLookup.get(conn);
		this.topicConnMap.get(topic).remove(conn);
		this.reverseLookup.remove(conn);
		
	}

	@Override
	public void onMessage( WebSocket conn, String message ) {
		//broadcast( "Subscribed to "+message );
		if(message.startsWith("CallbackService")) {
			
			conn.send("BarResponse  "+message.substring("CallbackService".length()));
			
		}
		else
		{
		
		
		conn.send("Subscribed to "+message);
		this.reverseLookup.put(conn, message);
		List<WebSocket> connections=this.topicConnMap.get(message);
		if(connections==null) {
			connections=new ArrayList<>();
			this.service.addSubscriber(message, this);
			
		}
		connections.add(conn);
		new Thread(new WebSockerWorker(this.service,message),"Web Consumer-"+conn).start();

		this.topicConnMap.put(message,connections);
		
		
		
		System.out.println( conn + ": " + message );
		}
	}
	@Override
	public void onMessage( WebSocket conn, ByteBuffer message ) {
	//	broadcast( message.array() );
		System.out.println( conn + ": " + message );
    	

	}

	@Override
	public void onError( WebSocket conn, Exception ex ) {
		ex.printStackTrace();
		if( conn != null ) {
			// some errors like port binding failed may not be assignable to a specific websocket
		}
	}

	@Override
	public void onStart() {
		System.out.println("Server started!");
		System.out.println(this.getPort());
		setConnectionLostTimeout(0);
		setConnectionLostTimeout(1);
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
		
	}

	@Override
	public void callBack(String message) {
		
		// TODO Auto-generated method stub
		//Retrieve Symbol from Message
		System.out.println("In Callback of Subscriber");
		
		String sym=message.substring(message.indexOf("symbol")+7);
		sym=sym.substring(0,sym.indexOf(","));
	
		for(WebSocket S:this.topicConnMap.get(sym)) {
			
			this.onMessage(S, "CallbackService"+message);
			
		}
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
	
	public TradeBarUpdater(PubSubService pubSubService, String topic) {
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

}
