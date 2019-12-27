from datetime import datetime
import sys
from random import randint

#Dont want to kill my hard disk so generated at max 10 Million Trades, around 45 lakh trades were 5.1G ..
max_trades_generated=10000000
fileName=sys.argv[1]
trade_count=1
#previous_time=int((datetime.now()-datetime(1970,1,1)).total_seconds())
previous_time=(datetime.now()-datetime(1970,1,1)).total_seconds()*1000000000
with open(fileName, "a") as myfile:
    while trade_count<max_trades_generated:
        price=str(randint(1,10000))
        #Generate trade every second
        TS2=int((datetime.now()-datetime(1970,1,1)).total_seconds()*1000000000)
        #myfile.write('{"sym":"GURU", "T":"Trade",  "P":'+price+', "Q":0.1376, "TS":1539440622.6547, "side": "b", "TS2":'+str(TS2)+'}\n')
        myfile.write('{"sym":"GURU", "T":"Trade",  "P":'+price+', "Q":0.1376, "TS":1539440622.6547, "side": "b", "TS2":'+str(TS2)+'}\n')
        trade_count=trade_count+1


