package com.github.gurpreetsachdeva.OHLCAnalyticsService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

import com.github.gurpreetsachdeva.OHLCAnalyticsService.model.BarResponse;
import com.github.gurpreetsachdeva.OHLCAnalyticsService.model.TradesData;
import com.github.gurpreetsachdeva.OHLCAnalyticsService.publishersubscriber.Publisher;

public class FSMWorker implements Runnable, Publisher {

	private BlockingQueue<TradesData> queue;
	private Long TIME_CONVERTER;
	private BlockingQueue<BarResponse> barResponses;
	private Map<String, List<BarResponse>> symBars = new HashMap<>();

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
				//System.out.println(lastBar);
				

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

	public FSMWorker(BlockingQueue<TradesData> queue, Long tIME_CONVERTER, BlockingQueue<BarResponse> br) {
		super();
		this.queue = queue;
		TIME_CONVERTER = tIME_CONVERTER;
		this.barResponses = br;
	}

	@Override
	public void publish(BarResponse br) {
		// TODO Auto-generated method stub
		try {
			barResponses.put(br);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
