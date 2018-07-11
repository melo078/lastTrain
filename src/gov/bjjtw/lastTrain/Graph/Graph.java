package gov.bjjtw.lastTrain.Graph;
import gov.bjjtw.lastTrain.CommonTools.CommonTools;

import java.io.*;
import java.util.*;

public final class Graph implements Serializable{
	private String firstVertax;
	private String firstTime,date;
	private String endVertex;
	private String distance;
    private Map<String,Double> minScoreLink=new HashMap<>();
    private Map<String,String> minTimeLink=new HashMap<>();
	private Map<String,Integer> minDisLink=new HashMap<>();
	private Map<String,String> transTime=new HashMap<>();
	private Map<String, String> accInLine=new HashMap<>();
	//private Map<String, String> acctoName=new HashMap<>();
	private Map<String, String> WalkTimeString=new HashMap<>();
	private Stack<String> stack=new Stack<>();
	private Stack<String> Score_stack=new Stack<>();
	private Stack<String> stackPath=new Stack<>();
	private Stack<String> stack2=new Stack<>();
	private Stack<String> stackPath2=new Stack<>();
	private Stack<String> stack3=new Stack<>();
	private Map<String, List<String>> adj = new HashMap<>();
	private Stack<String> stack4=new Stack<>();
	private Map<String, List<String>> adj3 = new HashMap<>();
	private Map<String, Integer> stationdistance =new HashMap<>();
	private Map<String, List<String>> timetable_weekday = new HashMap<>();
	private Map<String, List<String>> timetable_noair_weekday = new HashMap<>();
	private Map<String, List<String>> timetable_noair_weekend = new HashMap<>();
	private Map<String, List<String>> timetable_weekend = new HashMap<>();
	private Set<String> UnVisitedVertex=new HashSet<String>();
	private HashMap<String, String> station_geo = new HashMap<String, String>();
	private LinkedList<String> reachableSt = new LinkedList<String>();
	private Double score;
	private LinkedList<String> transferStations ;
	private int inc_sec;
	  
	private boolean isWeekend;
	public static final String UpperLimitTime="25:59:59";
	public static final int UpperLimitDis =10000000;

	public void addGeoPosition(String acccode, String geoposition){
		station_geo.put(acccode,geoposition);
	}




	public Float [] getGeoPosition(String accode){
		Float[] positionfloat = new Float[2];
		positionfloat[0] = null;
		positionfloat[1] = null;
		if (station_geo.containsKey(accode) == true) {
			String[] positionstr = station_geo.get(accode).split(",");
			if (positionstr.length == 2) {
				positionfloat[0] = Float.valueOf(positionstr[0]);
				positionfloat[1] = Float.valueOf(positionstr[1]);
			}
		}
		return positionfloat;
	}

	public void setAdj(Map<String,List<String>> inputAdj){
		adj =new HashMap<>();
		for(String k: inputAdj.keySet()){
			List<String> tmpList = new ArrayList<String>();
			for(String v: inputAdj.get(k)){
				tmpList.add(v);
			}
			adj.put(k,tmpList);
		}
	}

	public void setAdj3(Map<String,List<String>> inputAdj){
		adj3 =new HashMap<>();
		for(String k: inputAdj.keySet()){
			List<String> tmpList = new ArrayList<String>();
			for(String v: inputAdj.get(k)){
				tmpList.add(v);
			}
			adj3.put(k,tmpList);
		}
	}

	public void InitialSearchStartVertex(String startVertex,String dateString,String time,String end_Vertex) {
		firstVertax=startVertex;
		firstTime=time;
		endVertex=end_Vertex;
		date=dateString;
	    //algorithm.perform(this, firstVertax,date,firstTime,endVertex);
	}

	public void InitialSearchStartVertex2(String startVertex,String end_Vertex) {
		firstVertax=startVertex;
		endVertex=end_Vertex;
	    //algorithm.perform(this, firstVertax,date,firstTime,endVertex);
	  }

	public void addsec(int sec) {
		  inc_sec = sec;
	  }
	public int getsec() {
		  return inc_sec;
	  }
	  
	public void addEdge(String fromVertex, String toVertex) {
		if (firstVertax == null) {
			firstVertax = fromVertex;
		}
		if (adj.get(fromVertex) == null) {
			adj.put(fromVertex, new ArrayList<String>());
			adj.get(fromVertex).add(toVertex);
		} else {
			adj.get(fromVertex).add(toVertex);
		}
		if (adj3.get(toVertex) == null){
			adj3.put(toVertex, new ArrayList<String>());
			adj3.get(toVertex).add(fromVertex);
		} else {
			adj3.get(toVertex).add(fromVertex);
		}
	}

	public void RemoveEdge(String fromVertex, String toVertex) {
		if(adj.get(fromVertex).contains(toVertex)) {
			adj.get(fromVertex).remove((toVertex));
		}
		if(adj3.get(toVertex).contains(fromVertex)){
			adj3.get(toVertex).remove((fromVertex));
		}
	}

	public void AddStack(String fromvertex,String toVertex,String time,String arrtime,String time_start) {
		stack.push(fromvertex+","+toVertex+","+time+","+arrtime+","+time_start);
	}
	public void AddScore_Stack(String fromvertex,String toVertex,String time,String arrtime,String time_start) {
		Score_stack.push(fromvertex+","+toVertex+","+time+","+arrtime+","+time_start);
	}

	public void AddStackPath(String str) {
		  stackPath.push(str);
	  }
	public void AddStackPath2(String str)
	  {
		  stackPath2.push(str);
	  }
	public void addReachable(String station) {
		  reachableSt.add(station);
	  }
	  
	public LinkedList<String> getReachable(){
		  return reachableSt;
	  }
	  
	public Stack<String> getStack()
	  {
		  return stack;
	  }
public Stack<String> getScore_Stack()
	  {
		  return Score_stack;
	  }
	public Stack<String> getPathStack()
	  {
		  return stackPath;
	  }
	public Stack<String> getStack2()
	{
		  return stack2;
	}

	public Stack<String> getStack3() {
		return stack3;
	}
	public void setStack3(){
		stack3.clear();
		for(String s:stack){
			stack3.add(s);
		}
	}
	public Stack<String> getStack4() {
		return stack4;
	}
	public void setStack4(){
		stack4.clear();
		for(String s:Score_stack){
			stack4.add(s);
		}
	}

	public Stack<String> getPathStack2()
	  {
		  return stackPath2;
	  }
	public void addTransTime(String ver_start,String ver_end,String time)
	  {
		  transTime.put(ver_start+ver_end,time);
	  }
	  
	public void Add_weeekday_timetable(String acccode,String departureTime1,String departureTime2,String arrivingTime) {
		if(timetable_weekday.get(acccode)==null) {
			timetable_weekday.put(acccode, new ArrayList<String>());
		}
		timetable_weekday.get(acccode).add(departureTime1+","+departureTime2+","+arrivingTime);
	}
	public void Add_weeekday_noair_timetable(String acccode,String departureTime1,String departureTime2,String arrivingTime) {
		if(timetable_noair_weekday.get(acccode)==null) {
			timetable_noair_weekday.put(acccode, new ArrayList<String>());
		}
		timetable_noair_weekday.get(acccode).add(departureTime1+","+departureTime2+","+arrivingTime);
	}
	public void Add_weeekend_noair_timetable(String acccode,String departureTime1,String departureTime2,String arrivingTime) {
		if(timetable_noair_weekend.get(acccode)==null) {
			timetable_noair_weekend.put(acccode, new ArrayList<String>());
		}
		timetable_noair_weekend.get(acccode).add(departureTime1+","+departureTime2+","+arrivingTime);
	}

	public void Add_stationdistance(String acccode,int distance) {
		if(stationdistance.get(acccode)==null) {
			stationdistance.put(acccode, 0);
		}
		stationdistance.put(acccode,distance);
	}

	public void Add_UnVisitedVertex(String str)
	  {
		  UnVisitedVertex.add(str);
	  }
	public void cleanMinTimeLink(){
        minTimeLink.clear();
    }
    public void cleanMinScoreLink(){
		minScoreLink.clear();
	}
	public void cleanMinDisLink(){
		minDisLink.clear();
	}
	public void cleanWalkTimeString(){
		WalkTimeString.clear();
	}
	public void cleanStack(){
		stack.clear();
	}
    public void cleanScore_Stack(){
        Score_stack.clear();
    }
	public void cleanStackPath(){
		stackPath.clear();
	}
	public void cleanStack2(){
		stack2.clear();
	}
	public void cleanStack3(){
		stack3.clear();
	}
	public void cleanStackPath2(){
		stackPath2.clear();
	}
	public void cleanReachableSt(){
		reachableSt.clear();
	}
	public void resetParams(){
		firstTime = null;
		date = null;
		endVertex = null;
		distance = null;
	}

	public Set<String> getUnVisitedVertex() { return UnVisitedVertex; }
    public void setUnVisitedVertex(Set<String> v) {
		UnVisitedVertex = (Set<String>) CommonTools.DeepCopy(v);
	}

	public void Add_weeekend_timetable(String acccode,String departureTime1,String departureTime2,String arrivingTime) {
	  	if(timetable_weekend.get(acccode)==null) {
			timetable_weekend.put(acccode, new ArrayList<String>());
		}
		timetable_weekend.get(acccode).add(departureTime1+","+departureTime2+","+arrivingTime);
	}
	  
	public void Add_AccInLine(String accCode,String line) {
		accInLine.put(accCode,line);
	}

	/*
	public void Add_acctoName(String accCode,String name) {
		  acctoName.put(accCode,name);
	}
	public Map<String, String> getAcctoName() {
		return acctoName;
	}
	public String checkAcctoName(String key) { return acctoName.get(key);}
	*/

	public void addVertex(String vertex) {
	  	if (adj.get(vertex)==null) {
	  		adj.put(vertex, new ArrayList<>());
		}
		if (adj3.get(vertex)==null){
	  		adj3.put(vertex, new ArrayList<>());
		}
	}

	public Map<String, List<String>> getAdj() {
	    return adj;
	}

	public Map<String, List<String>> getAdj3() {
		return adj3;
	}

	public void Add_WalkTime(String acccode,String time) {
		if (WalkTimeString.get(acccode)==null) {
			WalkTimeString.put(acccode, new String());
		}
		WalkTimeString.put(acccode,time);
	}
	  
	public Map<String, String> getMinTimeLink() {
		    return minTimeLink;
	}

	public Map<String, Double> getMinScoreLink() {
		    return minScoreLink;
	}
	public Map<String, Integer> getMinDisLink() {
		return minDisLink;
	}

	public Map<String, List<String>> getTimetable_weekday() {
		    return timetable_weekday;
	}
	  
	public Map<String, List<String>> getTimetable_weekend() {
		    return timetable_weekend;
		  }
	public Map<String, List<String>> getnoair_Timetable_weekend() {
	    return timetable_noair_weekend;
	  }
	public Map<String, List<String>> getnoair_Timetable_weekday() {
	    return timetable_noair_weekday;
	  }
	public Map<String, Integer> getStationdistance() {
		return stationdistance;
	}
	  
	public Map<String, String> getTransTime() {
		    return transTime;
		  }
	  
	public Map<String, String> getAccInLine() { return accInLine; }

	public boolean getIsWeekend(){
	  return isWeekend;
	}

	public void setIsWeekend(boolean weekend){
		  isWeekend=weekend;
	  }
	  
	public int getWalkTime(String acccode){
		if(WalkTimeString.get(acccode)!=null) {
			return Integer.parseInt(WalkTimeString.get(acccode));
		} else {
			return 0;
		}
	}

	public void AddStack2(String vertex, String adjVertex, int distance2) {
		// TODO Auto-generated method stub
		stack2.push(vertex+","+adjVertex+","+distance2);
	}

}
