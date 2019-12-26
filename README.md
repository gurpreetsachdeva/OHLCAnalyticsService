OHLCAnalyticsService
=========================================

Please Read all the parts before starting the application
A Multithreaded Daemon for parsing Trades and generating JSON Data for Bar Charts

Brief Description of the problem:
===============================================

This is a Feed kind of a problem where a ProviderFeed is generating Trades for the market and we need to generate bar charts on the GUI
for that or some another process needs to consume those "Streamed Data".

Rough Design 
======================================================

Never Ever Do This :
==============================
1. Dont Load the full File ever or Dont keep the file Data in Memory.
2. 


Now What to Do ?
==============================
1. Use Three different subsystems i.e Decouple architecture to make a daemon Java Process . We will also use Pub Sub pattern.
2. Keeping  a Thread for each of the different files[in our cases its just a single file], This will read line by line[You could have done a batch read also], and put it a Thread Safe Queue.
3. Another Thread Pool named FSM , will take Trades Data and generate Bars for that.It is a publisher.
	i. Logic For bar Generation is simple , Every new trade will define the open bar or modify the previous bar.
	ii. Till the time moves on to barStartTime+15 seconds, keep on updating the Bar Data for every sym e.g Volume,High,Low updates.
	iii. If time moves to New bar, update close of Previous bar and also publish this bar to the Publishing Service
4. Another Thread Pool for Web Sockets which will consume the packets from Publishing service.
5. One Important thing, when keeping bars for a sym. Just keep the last 5000 bars as to keep the memory consumption to a certain threshold. Obviously assumption is that this service will keep a certain range of tickers only.
6. You can also start Java Threads as well as test this through a web client.
7. Block until something new Gets written to the trade file and stream it to the corresponding subscribers in real time.

Screenshots For Runnable Tests:
================================================================

Some sample runs are shown in screenshot Folder.
Some Performance stats are also taken in screenshot folder.

Steps To Run ?
=======================================
1. Code is Available in github.
2. You can either clone or download the tar.
3. git clone https://github.com/gurpreetsachdeva/OHLCAnalyticsService
4. Make sure you have Java 8 or later  and Maven in your path.
	if not install at a Ec2 or linux box:
	
	sudo apt-get install openjdk-8-jdk
	sudo apt install maven
5. On the repository do a mvn clean install
6 Open the index.html directly into the browser by dragging and dropping. You can also host this at a nginx server. This file connects to your websocket.
8. Run your Java Daemon using this command.
Run java command to start the daemon service or you can do an Right Click Run on Eclipse Java Project as well. Both should work but I have given the command below
java -Dfile.encoding=UTF-8 -classpath /home/gurpreet/eclipse-workspace/OHLCAnalyticsService/target/classes:/home/gurpreet/.m2/repository/com/googlecode/json-simple/json-simple/1.1.1/json-simple-1.1.1.jar:/home/gurpreet/.m2/repository/org/java-websocket/Java-WebSocket/1.4.0/Java-WebSocket-1.4.0.jar:/home/gurpreet/.m2/repository/org/slf4j/slf4j-api/1.7.25/slf4j-api-1.7.25.jar com.github.gurpreetsachdeva.OHLCAnalyticsService.App Worker3:XMLNXETH
Please Note Arguments to this java commands are worker Threads for Subscription.

Worker3:XMLNXETH worker4:TickerName etc


	
	
Deployment
=============================

ssh -i "AWS_KEY" box_public_DNS
Install Java :sudo apt-get install openjdk-8-jdk
Clone the code from github or download the tar.gz file in the root folder of github repository given by Gurpreet.
tar -xvzf fileName.tar.gz or git clone https://github.com/gurpreetsachdeva/OHLCAnalyticsService
Run java command to start the daemon service or you can do an Right Click Run on Eclipse Java Project as well. Both should work but I have given the command below

ToDos
========
1. Make everything configurable by reading through property file and not dependent on App Driver class constants.
