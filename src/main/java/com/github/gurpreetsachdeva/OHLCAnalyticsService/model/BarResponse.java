package com.github.gurpreetsachdeva.OHLCAnalyticsService.model;

public class BarResponse {
	
	//Sample Object
	//{"event": "ohlc_notify", "symbol": "XXBTZUSD", "bar_num": 1,
	//	"o":6538.8, "h":6538.8, "l":6538.8, "c":0.0, "volume": 1}
	private Double o;
	private Double l;
	private Double h;
	private Double c;
	private Double v;
	private Double previous;
	private Integer bar_num;
	private String event="ohlc_notify";
	private String symbol;
	private Long ts2;
	private Long noOfTrades;
	public BarResponse(Double o, Double l, Double h, Double c, Double v, Double previous,Integer bar_num, String symbol,
			Long ts2,Long noOfTrades) {
		super();
		this.o = o;
		this.l = l;
		this.h = h;
		this.c = c;
		this.v = v;
		this.previous=previous;
		this.bar_num = bar_num;
		this.symbol = symbol;
		this.ts2 = ts2;
		this.noOfTrades=noOfTrades;
	}
	public Double getO() {
		return o;
	}
	public void setO(Double o) {
		this.o = o;
	}
	public Double getL() {
		return l;
	}
	public void setL(Double l) {
		this.l = l;
	}
	public Double getH() {
		return h;
	}
	public void setH(Double h) {
		this.h = h;
	}
	public Double getC() {
		return c;
	}
	public void setC(Double c) {
		this.c = c;
	}
	public Double getV() {
		return v;
	}
	public void setV(Double v) {
		this.v = v;
	}
	public Integer getBar_num() {
		return bar_num;
	}
	public void setBar_num(Integer bar_num) {
		this.bar_num = bar_num;
	}
	public String getEvent() {
		return event;
	}
	public void setEvent(String event) {
		this.event = event;
	}
	public String getSymbol() {
		return symbol;
	}
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	public Long getTs2() {
		return ts2;
	}
	public void setTs2(Long ts2) {
		this.ts2 = ts2;
	}
	public Double getPrevious() {
		return previous;
	}
	public void setPrevious(Double previous) {
		this.previous = previous;
	}
	public Long getNoOfTrades() {
		return noOfTrades;
	}
	public void setNoOfTrades(Long noOfTrades) {
		this.noOfTrades = noOfTrades;
	}
	@Override
	public String toString() {
		return "BarResponse [o=" + o + ", l=" + l + ", h=" + h + ", c=" + c + ", v=" + v + ", previous=" + previous
				+ ", bar_num=" + bar_num + ", event=" + event + ", symbol=" + symbol + ", ts2=" + ts2 + ", noOfTrades="
				+ noOfTrades + "]";
	}
	
	
	
	
	
	
	
}
