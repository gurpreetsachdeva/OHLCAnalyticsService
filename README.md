OHLCAnalyticsService
=========================================

Please Read all the parts before starting the application.

A Multithreaded Daemon Application for parsing Trades and generating JSON Data for Bar Charts. 

Brief Description of the problem:
===============================================

This is a Feed kind of a problem where a ProviderFeed is generating Trades for the market and we need to generate bar charts on the GUI
for that or some another process needs to consume those "Streamed Data".

Rough Design 
======================================================

Never Ever Do This :
==============================
1. Dont Load the full File ever or Dont keep the file Data as a whole in Memory.
2. Always remember that a finite state machine can generate the next bar by having the last Bar.
3. Pub Sub Service History will have only N days history and not from the start of the world. This is defined by a constant and went with this approach to keep your service safe from dying a painful Memory Full death.


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
6. You can also start Java WorkerThreads passed in command line OR  as well as test this through a web client OR BOTH.
7. Block until something new Gets written to the trade file and stream it to the corresponding subscribers in real time.
8. No of Trades represent how many trades were used for each bar, just extra information.
9. If tom someone wants get a bar in semicomplete state[bar_num wont change,for every trade just blindly push], thats also possible so had both implemented below.{Currently configured for complete state but semicomlete state requires only a single line change in publish bars).

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
	
	Try this first from root directory and then connect to a ticker via opening index.html file browser.
	
	java -jar target/OHLCAnalyticsService-0.0.1-SNAPSHOT-jar-with-dependencies.jar Worker3:XMLNXETH worker4:EOSXBT filePath:./trades.json
	

	
	sudo apt-get install openjdk-8-jdk
	
	sudo apt install maven
5. On the repository do a mvn clean install
6 Open the index.html directly into the browser by dragging and dropping. You can also host this at a nginx server. This file connects to your websocket.
8. Run your Java Daemon using this command.
Run java command to start the daemon service or you can do an Right Click Run on Eclipse Java Project as well. Both should work but I have given the command below
Commands :
======================================
1. Start without any Subscriber for Bar Trades.
	java -jar target/OHLCAnalyticsService-0.0.1-SNAPSHOT-jar-with-dependencies.jar 
	
2. Two Subscribers 
	java -jar target/OHLCAnalyticsService-0.0.1-SNAPSHOT-jar-with-dependencies.jar Worker3:XMLNXETH worker4:EOSXBT
	
	See Screenshot for below command :TwoConsumersOutputCommandLine.png
	java -jar target/OHLCAnalyticsService-0.0.1-SNAPSHOT-jar-with-dependencies.jar Worker3:XMLNXETH worker4:EOSXBT filePath:/home/gurpreet/trades-data/trades.json
	
	Prefix of workername can be anything you want a see in logs.
3  N Subscribers 

	In a similar fashion.
	
	I tested with around 30 subscribers , see the screenshots of python Notebook where I retrieve them and generate a command for them.
	
4. Obviously you can add as many webclients as you want through index.html
Please Note Arguments to this java commands are worker Threads for Subscription.

Worker3:XMLNXETH worker4:TickerName etc


	
	
Deployment
=============================

ssh -i "AWS_KEY" box_public_DNS
Install Java :sudo apt-get install openjdk-8-jdk
Clone the code from github or download the tar.gz file in the root folder of github repository given by Gurpreet.
tar -xvzf fileName.tar.gz or git clone https://github.com/gurpreetsachdeva/OHLCAnalyticsService
Run java command to start the daemon service or you can do an Right Click Run on Eclipse Java Project as well. Both should work but I have given the command below


Test Run for Checking Streaming Data:
======================================
1. Keep the java daemon Running.
2. cat trades.json|grep XMLNXETH > new.json
3. Open the UI and subscribe to let us say ABC.
4. You will see not see any output for ABC tickers as it is not in the trade file yet.
5. Replace the XMLNXETH to ABC for generating new set of Trades. What works better than vim for this. %s/XMLNXETH/ABC/g
3. Append new.json to trades.json , so this acts as a feeder for me.
4. cat new.json >> trades.json
5 You will observe that your UI client will get refreshed by the New Trades. I just utilized the 

Tar Without .git to keep the blob from expanding : tar cvz --exclude=".git" -f OHLCAnalyticsService.tar.gz OHLCAnalyticsService/

Memory Analysis using MAT
===================================
1. Create Heap Dumps and Analysed them in eclipse. Attach the screenshots in screenshot folder

ToDos
========
1. Make everything configurable by reading through property file and not dependent on App Driver class constants.
