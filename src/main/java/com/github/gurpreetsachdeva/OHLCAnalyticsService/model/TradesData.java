package com.github.gurpreetsachdeva.OHLCAnalyticsService.model;

public class TradesData {
	
	//Sample Trade
	//{"sym":"XETHZUSD", "T":"Trade", "P":226.85, "Q":0.02,
	//"TS":1538409733.3449, "side": "b", "TS2":1538409738828589281}
	private String sym;
	private Double P;
	private Double Q;
	private Long TS;
	private Long TS2;
	private String side;
	private String T;
	public String getSym() {
		return sym;
	}
	public void setSym(String sym) {
		this.sym = sym;
	}
	public Double getP() {
		return P;
	}
	public void setP(Double p) {
		P = p;
	}
	
	public double getQ() {
		return Q;
	}
	public void setQ(Double q) {
		Q = q;
	}
	public Long getTS() {
		return TS;
	}
	public void setTS(Long tS) {
		TS = tS;
	}
	public Long getTS2() {
		return TS2;
	}
	public void setTS2(Long tS2) {
		TS2 = tS2;
	}
	public String getSide() {
		return side;
	}
	public void setSide(String side) {
		this.side = side;
	}
	public String getT() {
		return T;
	}
	public void setT(String t) {
		T = t;
	}
	
	public TradesData(String sym, Double p, Double q, Long tS2) {
		super();
		this.sym = sym;
		P = p;
		Q = q;
		TS2 = tS2;
	}
	@Override
	public String toString() {
		return "TradesData [sym=" + sym + ", P=" + P + ", Q=" + Q + ", TS2=" + TS2 + "]";
	}

}
