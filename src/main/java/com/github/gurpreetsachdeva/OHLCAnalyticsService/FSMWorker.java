package com.github.gurpreetsachdeva.OHLCAnalyticsService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

import com.github.gurpreetsachdeva.OHLCAnalyticsService.model.BarResponse;
import com.github.gurpreetsachdeva.OHLCAnalyticsService.model.TradesData;
import com.github.gurpreetsachdeva.OHLCAnalyticsService.publishersubscriber.Publisher;
import com.github.gurpreetsachdeva.OHLCAnalyticsService.publishersubscriber.service.PubSubService;

public class FSMWorker implements Runnable, Publisher {

	private BlockingQueue<TradesData> queue;
	private Long TIME_CONVERTER;
	private PubSubService service;
	private Map<String, List<BarResponse>> symBars = new HashMap<>();

	public FSMWorker(BlockingQueue<TradesData> queue, Long tIME_CONVERTER, PubSubService service) {
		super();
		this.queue = queue;
		TIME_CONVERTER = tIME_CONVERTER;
		this.service = service;

	}

	@Override
	public void run() {
		// TODO Auto-generated method stub

		System.out.println("Inside FSMWorker");
		TradesData data;
		try {
			data = queue.take();
			while (data != null) {
				publishBars(data);
				data = queue.take();
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Exiting FSMWorker");

	}

	/*Business Logic For Bar Generation*/
	private void publishBars(TradesData td) {

		String sym = td.getSym();

		List<BarResponse> barResponses = symBars.get(sym);
		if (barResponses == null) {
			barResponses = new ArrayList<>();
			BarResponse br = new BarResponse(td.getP(), td.getP(), td.getP(), td.getP(), td.getQ(), td.getP(), 1, sym,
					td.getTS2(), 1L);
			barResponses.add(br);
			symBars.put(sym, barResponses);

		} else {
			BarResponse lastBar = barResponses.get(barResponses.size() - 1);
			Long barStartTime = lastBar.getTs2();
			if (td.getTS2() >= (barStartTime + 15 * TIME_CONVERTER)) {
				lastBar.setC(lastBar.getPrevious());

				BarResponse br = new BarResponse(td.getP(), td.getP(), td.getP(), td.getP(), lastBar.getV() + td.getQ(),
						td.getP(), lastBar.getBar_num() + 1, sym, td.getTS2(), 1L);
				barResponses.add(br);
				publish(lastBar);
				
			} else {
				if (td.getP() < lastBar.getL()) {
					lastBar.setL(td.getP());
				}
				if (td.getP() > lastBar.getH()) {
					lastBar.setH(td.getP());
				}
				lastBar.setV(lastBar.getV() + td.getQ());
				lastBar.setPrevious(td.getP());
				lastBar.setNoOfTrades(lastBar.getNoOfTrades() + 1);

			}
		}

	}

	@Override
	public void publish(BarResponse br) {
		// TODO Auto-generated method stub
		service.addBarResponseToQueue(br);

	}

}
