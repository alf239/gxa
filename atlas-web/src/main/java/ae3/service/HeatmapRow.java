package ae3.service;

import java.util.HashMap;

import org.apache.commons.lang.StringUtils;

import ae3.service.structuredquery.UpdownCounter;

public class HeatmapRow implements Comparable<HeatmapRow>{
	String fv;
	String ef;
	private int count_up;
	private int count_dn;
	private double avgPval_up;
	private double avgPval_dn;
	
	
	public HeatmapRow(String efv, String ef, int count_up, int count_dn, double avg_up, double avg_dn){
		this.ef = ef;
		this.fv = efv;
		this.count_dn = count_dn;
		this.count_up = count_up;
		this.avgPval_dn = avg_dn;
		this.avgPval_up = avg_up;
	}
	public String getFv() {
		return fv;
	}
	public void setFv(String fv) {
		this.fv = fv;
	}
	public String getShortFv(){
		String fv_short = StringUtils.capitalize(fv);
		return fv_short.length() > 30 ? fv_short.substring(0,30)+"..." : fv_short;
	}
	public String getEf() {
		return ef;
	}
	public void setEf(String ef) {
		this.ef = ef;
	}
	public int getCount_up() {
		return count_up;
	}
	public void setCount_up(int count_up) {
		this.count_up = count_up;
	}
	public int getCount_dn() {
		return count_dn;
	}
	public void setCount_dn(int count_dn) {
		this.count_dn = count_dn;
	}
	public double getPvalAvg_up() {
		return avgPval_up;
	}
	public void setPvalAvg_up(double avg_up) {
		this.avgPval_up = avg_up;
	}
	public double getPvalAvg_dn() {
		return avgPval_dn;
	}
	public void setPvalAvg_dn(double avg_dn) {
		this.avgPval_dn = avg_dn;
	}
	
	public HashMap<String, String> getCellColor() {
		
		String color="#ffffff";
		HashMap<String, String> colorMap = new HashMap<String, String>();
		if(count_up>0){
			int uc = coltrim((getPvalAvg_up() > 0.05 ? 0.05 : getPvalAvg_up()) * 255 / 0.05);            
			color =  String.format("#ff%02x%02x", uc, uc);
			colorMap.put("up",color);
		}
		
		if(count_dn>0){
			int dc = coltrim((getPvalAvg_dn() > 0.05 ? 0.05 : getPvalAvg_dn()) * 255 / 0.05);
            color =  String.format("#%02x%02xff", dc, dc);
            colorMap.put("dn",color);
		}
		
        return colorMap;
    }
	
	public String getText(){
		if(isMixedCell())
			return " found over-expressed in "+fv+ " in "+ count_up + " experiments and under-expressed in "+ count_dn+ " experiments";
		else if(count_up > 0)
			return " found over-expressed in "+fv+ " in " + count_up + " experiments";
		else
			if(count_dn > 0)
				return " found under-expressed in "+fv+ " in " + count_dn + " experiments";
			else
				return "";
	}
	
	public boolean isMixedCell(){
		return (count_dn>0 && count_up>0);
	}
	
	public String getExpr(){
		if (count_dn>0) return "dn";
		else if (count_up>0) return "up";
		else return "";
	}
	
		
	public int getNoStudies(){
		return count_dn+count_up;
	}
	
    public HashMap<String, String> getCellText()
    {
        double c;
        HashMap<String, String> colorMap = new HashMap<String, String>();
        if(count_up>0) {
            c = (getPvalAvg_up() > 0.05 ? 0.05 : getPvalAvg_up()) * 255 / 0.05;
            colorMap.put("up",c > 127 ? "#000000" : "#ffffff");
        } 
        if(count_dn>0){
            c = (getPvalAvg_dn() > 0.05 ? 0.05 : getPvalAvg_dn()) * 255 / 0.05;
            colorMap.put("dn",c > 127 ? "#000000" : "#ffffff");
        }
        return colorMap;
    }
    
    private int coltrim(double v)
    {
        return Math.min(255, Math.max(0, (int)v));
    }
	public int compareTo(HeatmapRow o) {
		if (this.getNoStudies() == o.getNoStudies()){
            if(this.avgPval_dn+this.avgPval_up > o.avgPval_dn+o.avgPval_up)
            	return -1;
            else if(this.avgPval_dn+this.avgPval_up < o.avgPval_dn+o.avgPval_up)
            	return 1;
            else
            	return 0;
		}
        else if (this.getNoStudies() > o.getNoStudies())
            return 1;
        else
            return -1;
	}
}
