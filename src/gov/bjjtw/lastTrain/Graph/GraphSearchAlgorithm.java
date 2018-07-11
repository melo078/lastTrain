package gov.bjjtw.lastTrain.Graph;

import gov.bjjtw.lastTrain.CommonTools.CommonTools;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;


public class GraphSearchAlgorithm {
	private Set<String> visitedVertex;
	public String date;
	public String transPath;
	public Set<String> UnVisitedVertex=new HashSet<String>();
	public int transferweight = 10000000;
	String pingguoyuan_arrivetime ;
	//基于最小时间
	public boolean perform(Graph g, String sourceVertex,String dateString,String time,String end_Vertex,String stationnametoacccode, Boolean isReverse) {
		if (null == visitedVertex) {
			visitedVertex = new HashSet<>();
	    }
	    date=dateString;
	    transPath = stationnametoacccode;
	    if (isReverse){
			return Dijkstra3(g,sourceVertex,dateString,time,end_Vertex,transPath);
		}
	    return Dijkstra(g,sourceVertex,dateString,time,end_Vertex,transPath);
	}
	//基于分数
	public boolean perform3(Graph g, String sourceVertex,String dateString,String time,String end_Vertex,String stationnametoacccode, Boolean isReverse) {
		if (null == visitedVertex) {
			visitedVertex = new HashSet<>();
		}
		date=dateString;
		transPath = stationnametoacccode;
		if (isReverse){
			return Dijkstra3(g,sourceVertex,dateString,time,end_Vertex,transPath);
		}
		return Score_Dijkstra(g,sourceVertex,dateString,time,end_Vertex,transPath);
	}
	//基于距离
	public boolean perform2(Graph g, String sourceVertex,String end_Vertex,String stationnametoacccode) {
		if (null == visitedVertex) {
	      visitedVertex = new HashSet<>();
	    }
	    transPath = stationnametoacccode;
		return Dijkstra2(g,sourceVertex,end_Vertex,transPath);
	}
	  
	public boolean isWeekend(String dateString) {
		try{
			DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			Date date = format.parse(dateString);
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			if(cal.get(Calendar.DAY_OF_WEEK)==Calendar.SATURDAY||cal.get(Calendar.DAY_OF_WEEK)==Calendar.SUNDAY) {
				return true;
			} else {
				return false;
			}
					
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return false;
		}
	}
	//基于最短时间
	private boolean Dijkstra(Graph g,String sourceVertex,String dateString,String time,String end_Vertex,String transPath) {
		try {
			boolean air ;
			ArrayList<String> airlines = new ArrayList<String>();
			airlines.add("151020057");
			airlines.add("151020059");
			if (airlines.contains(sourceVertex)||airlines.contains(end_Vertex)) {
				air = true;
			}else {
				air = false;
			}
			g.getReachable().clear();
			if (!InitialMinTimeLink(g, sourceVertex, time,air)) {
				return false;
			}
			String ver = sourceVertex;
			visitedVertex.add(sourceVertex);

			UnVisitedVertex = g.getUnVisitedVertex();

			UnVisitedVertex.remove(ver);
			String ver_Time = time, ver_before = ver;
            
			while (!(FindLatestVertex(g, g.getMinTimeLink()).equals("null"))) {
				ver = FindLatestVertex(g, g.getMinTimeLink());
				g.addReachable(ver);
				visitedVertex.add(ver);
				List<String> toBeVisitedVertex = g.getAdj().get(ver);

				for (String v : visitedVertex) {
					if (toBeVisitedVertex.contains(v)) {
						toBeVisitedVertex.remove(v);
					}
				}

//				String latest_time = Graph.UpperLimitTime;
//				String latest_Vertex = "";
				ver_Time = g.getMinTimeLink().get(ver);

				for (String ver_end : toBeVisitedVertex) {
					int de_t = 0;
					//departure time of started station
					int de_t_s = 0;
					int arr_t = 0;

					String alltime;
					String str2[];
					String s1 = g.getAccInLine().get(ver);
					String s2 = g.getAccInLine().get(ver_end);
					boolean notTransStation = s1.equals(s2);

					if (notTransStation) {
						if(air) {
							alltime = FindLatestTime(g, ver, ver_end, ver_Time, 0);
							str2 = alltime.split(",");
							if (str2[0].equals("25:59:59")||str2[1].equals("25:59:59")||str2[2].equals("25:59:59")) {
								continue;
							}
							
						//str2[0] = departure time, str2[1]= arriving time
							de_t = CommonTools.TransferTime(str2[0]);
							de_t_s = CommonTools.TransferTime(str2[2]);
							arr_t = CommonTools.TransferTime(str2[1]);
						}else {
							alltime = FindLatestTime_noair(g, ver, ver_end, ver_Time, 0);
							str2 = alltime.split(",");
							if (str2[0].equals("25:59:59")||str2[1].equals("25:59:59")||str2[2].equals("25:59:59")) {
								continue;
							}
							
						//str2[0] = departure time, str2[1]= arriving time
							de_t = CommonTools.TransferTime(str2[0]);
							de_t_s = CommonTools.TransferTime(str2[2]);
							arr_t = CommonTools.TransferTime(str2[1]);
						}
					} else {
						// bug: change first part to arriving time
						de_t = CommonTools.TransferTime(ver_Time) + Integer.parseInt(g.getTransTime().get(ver + ver_end));
						arr_t = de_t;
						de_t_s = CommonTools.TransferTime(ver_Time);
					}

					if (g.getMinTimeLink().get(ver_end) == null) {
						g.getMinTimeLink().put(ver_end, CommonTools.SecondToTime(de_t));
						g.AddStack(ver, ver_end, CommonTools.SecondToTime(de_t), CommonTools.SecondToTime(arr_t), CommonTools.SecondToTime(de_t_s));
					} else {
						int temp_t = CommonTools.TransferTime(g.getMinTimeLink().get(ver_end));
						if (de_t < temp_t) {
							g.getMinTimeLink().put(ver_end, CommonTools.SecondToTime(de_t));
							g.AddStack(ver, ver_end, CommonTools.SecondToTime(de_t), CommonTools.SecondToTime(arr_t), CommonTools.SecondToTime(de_t_s));
						}
					}
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return true;
	}
	//最晚出发时间
	private boolean Dijkstra3(Graph g,String sourceVertex,String dateString,String time,String end_Vertex,String transPath) {
		try {
			g.getReachable().clear();
			if(!InitialMinTimeLink3(g,end_Vertex,time)) {
				return false;
			}
		  	String ver=end_Vertex;
		    visitedVertex.add(end_Vertex);

		    UnVisitedVertex=g.getUnVisitedVertex();

		    UnVisitedVertex.remove(ver);
		    String ver_Time=null,ver_before=ver;
		    
		    while(!(FindLatestVertex3(g,g.getMinTimeLink()).equals("null"))) {
		    	ver=FindLatestVertex3(g,g.getMinTimeLink());
		    	g.addReachable(ver);
		    	visitedVertex.add(ver);
		    	List<String> toBeVisitedVertex = g.getAdj3().get(ver);

		    	for(String v:visitedVertex) {
			    	if(toBeVisitedVertex.contains(v)) {
			    		toBeVisitedVertex.remove(v);
			    	}
		    	}

		    	ver_Time=g.getMinTimeLink().get(ver);
          
		    	for(String ver_start : toBeVisitedVertex) {
		    		int de_t=0;
					//departure time of started station
		    		int de_t_s=0;
		    		int arr_t=0;

		    		String alltime;
		    		String str2[];
		    		String s1=g.getAccInLine().get(ver);
		    		String s2=g.getAccInLine().get(ver_start);
		    		boolean notTransStation=s1.equals(s2);
		    		
		    		if(notTransStation) {
		    			alltime = FindLatestTime3(g,ver,ver_start,ver_Time,0);
						if (alltime.equals("25:59:59")) {
							continue;
						}
		    			str2= alltime.split(",");
						//str2[0] = departure time, str2[1]= arriving time
		    			de_t = CommonTools.TransferTime(str2[0]);
		    			arr_t = CommonTools.TransferTime(str2[1]);
						de_t_s =  CommonTools.TransferTime(str2[2]);
		    		} else{
						// bug: change first part to arriving time
//		    			de_t_s = CommonTools.TransferTime(ver_Time);
//						arr_t = de_t_s;
//						de_t =  arr_t - Integer.parseInt(g.getTransTime().get(ver+ver_start));
						de_t = CommonTools.TransferTime(ver_Time);
						arr_t = de_t;
						de_t_s =  de_t - Integer.parseInt(g.getTransTime().get(ver+ver_start));
		    		}

		    		if(g.getMinTimeLink().get(ver_start)==null) {
		    			
						g.getMinTimeLink().put(ver_start, CommonTools.SecondToTime(de_t_s));
		    			g.AddStack(ver_start, ver, CommonTools.SecondToTime(de_t), CommonTools.SecondToTime(arr_t), CommonTools.SecondToTime(de_t_s));
		    		} else {
		    			
		    			int temp_t= CommonTools.TransferTime(g.getMinTimeLink().get(ver_start));
		    			if(de_t<temp_t ) {
							g.getMinTimeLink().put(ver_start, CommonTools.SecondToTime(de_t_s));
		    				g.AddStack(ver_start,ver, CommonTools.SecondToTime(de_t), CommonTools.SecondToTime(arr_t), CommonTools.SecondToTime(de_t_s));
		    			}
		    		}
		    	}
		    }
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return true;
	  }
	//基于最短距离
	private boolean Dijkstra2(Graph g,String sourceVertex,String end_Vertex,String transPath) {
		try {
			if(!InitialMinDisLink(g,sourceVertex)) {
				return false;
			}
		    visitedVertex.add(sourceVertex);
		    while(!(FindLatestVertex2(g,g.getMinDisLink()).equals("null"))) {
		    	String ver=FindLatestVertex2(g,g.getMinDisLink());
		    	g.addReachable(ver);
		    	visitedVertex.add(ver);
		    	List<String> toBeVisitedVertex = g.getAdj().get(ver);
		    	for(String v:visitedVertex) {
			    	if (toBeVisitedVertex.contains(v)) {
			    		toBeVisitedVertex.remove(v);
			    	}
		    	}
		    	int ver_dist=g.getMinDisLink().get(ver);
		    	for(String ver_end : toBeVisitedVertex) {
		    	   int trans_dist=g.getStationdistance().get(ver+ver_end);
		    	   if(g.getMinDisLink().get(ver_end)==null) {
		    			g.getMinDisLink().put(ver_end,ver_dist+trans_dist);
		    			g.AddStack2(ver, ver_end,ver_dist+trans_dist);
		    		} else {
		    			int temp_t=g.getMinDisLink().get(ver_end);
		    			if(ver_dist+trans_dist<temp_t) {
		    				g.getMinDisLink().put(ver_end,ver_dist+trans_dist);
		    				g.AddStack2(ver,ver_end, ver_dist+trans_dist);
		    			}
		    		}
		    	}
		    }
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return true;
	}
	//基于分数（换乘次数越少，分数越大）
	private boolean Score_Dijkstra(Graph g,String sourceVertex,String dateString,String time,String end_Vertex,String transPath) {
		try {
			boolean air ;
			ArrayList<String> airlines = new ArrayList<String>();
			airlines.add("151020057");
			airlines.add("151020059");
			if (airlines.contains(sourceVertex)||airlines.contains(end_Vertex)) {
				air = true;
			}else {
				air = false;
			}
			g.getReachable().clear();
			if (!InitialMinScoreLink(g, sourceVertex, time,air)) {
				return false;
			}
			String ver = sourceVertex;
			visitedVertex.add(sourceVertex);
			UnVisitedVertex = g.getUnVisitedVertex();
			UnVisitedVertex.remove(ver);
			String ver_Time = time;
			double ver_Score= 0.0;
			//String ver_before = ver;
			while (!(FindLatestVertex4(g, g.getMinScoreLink()).equals("null"))) {
				ver = FindLatestVertex4(g, g.getMinScoreLink());
				g.addReachable(ver);
				visitedVertex.add(ver);
				List<String> toBeVisitedVertex = g.getAdj().get(ver);
				for (String v : visitedVertex) {
					if (toBeVisitedVertex.contains(v)) {
						toBeVisitedVertex.remove(v);
					}
				}
				ver_Time = g.getMinTimeLink().get(ver);
				ver_Score = g.getMinScoreLink().get(ver);
				for (String ver_end : toBeVisitedVertex) {
					int de_t = 0;
					//departure time of started station
					int de_t_s = 0;
					int arr_t = 0;
					String alltime;
					String str2[];
					String s1 = g.getAccInLine().get(ver);
					String s2 = g.getAccInLine().get(ver_end);
					boolean notTransStation = s1.equals(s2);
					if (notTransStation) {
						if(air) {
							alltime = FindLatestTime(g, ver, ver_end, ver_Time, 0);
							str2 = alltime.split(",");
							if (str2[0].equals("25:59:59")||str2[1].equals("25:59:59")||str2[2].equals("25:59:59")) {
								continue;
							}
						//str2[0] = next_departure time, str2[1]= arriving time
							de_t = CommonTools.TransferTime(str2[0]);
							de_t_s = CommonTools.TransferTime(str2[2]);
							arr_t = CommonTools.TransferTime(str2[1]);
						}else {
							alltime = FindLatestTime_noair(g, ver, ver_end, ver_Time, 0);
							str2 = alltime.split(",");
							if (str2[0].equals("25:59:59")||str2[1].equals("25:59:59")||str2[2].equals("25:59:59")) {
								continue;
							}

						//str2[0] = next_departure time, str2[1]= arriving time
							de_t = CommonTools.TransferTime(str2[0]);
							de_t_s = CommonTools.TransferTime(str2[2]);
							arr_t = CommonTools.TransferTime(str2[1]);
						}
					} else {
						// bug: change first part to arriving time
						de_t = CommonTools.TransferTime(ver_Time) + Integer.parseInt(g.getTransTime().get(ver + ver_end));
						arr_t = de_t;
						de_t_s = CommonTools.TransferTime(ver_Time);
						de_t+=transferweight;
					}
					if (g.getMinScoreLink().get(ver_end) == null) {
						g.getMinScoreLink().put(ver_end, ver_Score+de_t+0.0);
						if (de_t>transferweight){
							g.getMinTimeLink().put(ver_end, CommonTools.SecondToTime(de_t-transferweight));
							g.AddScore_Stack(ver, ver_end, CommonTools.SecondToTime(de_t-transferweight), CommonTools.SecondToTime(arr_t), CommonTools.SecondToTime(de_t_s));
						}else{
							g.getMinTimeLink().put(ver_end, CommonTools.SecondToTime(de_t));
							g.AddScore_Stack(ver, ver_end, CommonTools.SecondToTime(de_t), CommonTools.SecondToTime(arr_t), CommonTools.SecondToTime(de_t_s));
						}

					} else {
						double temp_t =g.getMinScoreLink().get(ver_end);
						if (de_t < temp_t) {
							g.getMinScoreLink().put(ver_end, ver_Score+de_t+0.0);
							if (de_t>transferweight){
								g.getMinTimeLink().put(ver_end, CommonTools.SecondToTime(de_t-transferweight));
								g.AddScore_Stack(ver, ver_end, CommonTools.SecondToTime(de_t-transferweight), CommonTools.SecondToTime(arr_t), CommonTools.SecondToTime(de_t_s));
							}else{
								g.getMinTimeLink().put(ver_end, CommonTools.SecondToTime(de_t));
								g.AddScore_Stack(ver, ver_end, CommonTools.SecondToTime(de_t), CommonTools.SecondToTime(arr_t), CommonTools.SecondToTime(de_t_s));
							}
						}
					}
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return true;
	}

	//时间最小
	private String FindLatestVertex(Graph g,Map<String,String> map) {
		String vertex="null";
		String minTime=Graph.UpperLimitTime;
		for (Map.Entry<String, String> entry : map.entrySet()) {
			if(!visitedVertex.contains(entry.getKey())) {
				if(CommonTools.TransferTime(entry.getValue())- CommonTools.TransferTime(minTime)<0) {
					vertex = entry.getKey();
					minTime = entry.getValue();
				}
			}
		}
		if (!minTime.equals(Graph.UpperLimitTime)) {
			return vertex;
		}
		return "null";
	}
	//分数
	private String FindLatestVertex4(Graph g,Map<String,Double> map) {
		String vertex="null";
		Double minTime = transferweight*100+0.0;
		for (Map.Entry<String, Double> entry : map.entrySet()) {
			if(!visitedVertex.contains(entry.getKey())) {
				if(entry.getValue() < minTime) {
					vertex = entry.getKey();
					minTime = entry.getValue();
				}
			}
		}
		if (minTime != transferweight*100+0.0) {
			return vertex;
		}
		return "null";
	}
	//最晚出发时间
	private String FindLatestVertex3(Graph g,Map<String,String> map) {
		String vertex="null";
		String maxTime="01:00:00";
		for (Map.Entry<String, String> entry : map.entrySet()) {
			if(!visitedVertex.contains(entry.getKey())) {
				int a1 = CommonTools.TransferTime(maxTime);
				int a2 = CommonTools.TransferTime(entry.getValue());
				if(CommonTools.TransferTime(entry.getValue())- CommonTools.TransferTime(maxTime)>0) {
					vertex = entry.getKey();
					maxTime = entry.getValue();
				}
			}
		}
		if (!maxTime.equals(Graph.UpperLimitTime)) {
			return vertex;
		}
		return "null";
	}
	//距离
	private String FindLatestVertex2(Graph g,Map<String,Integer> map) {
		String vertex="null";
		int mindis=Graph.UpperLimitDis;
		for (Map.Entry<String, Integer> entry : map.entrySet()) {
			if(!visitedVertex.contains(entry.getKey())) {
				if(entry.getValue()-mindis<0) {
					vertex=entry.getKey();
					mindis=entry.getValue();
				};
			}
		}

		//System.out.println(vertex+":"+mindis);
		if(mindis!=Graph.UpperLimitDis) {
			return vertex;
		}
		return "null";
	}

	private boolean InitialMinDisLink(Graph g,String vertex) {
		List<String> toBeUpdatedVertex = g.getAdj().get(vertex);
		g.getMinDisLink().clear();
		for(String adjVertex:toBeUpdatedVertex) {
			int distance=Graph.UpperLimitDis;
			if(g.getStationdistance().get(vertex+adjVertex)==null) {
				g.getMinDisLink().put(adjVertex,Graph.UpperLimitDis);
			} else {
				distance=g.getStationdistance().get(vertex+adjVertex);
			  	g.getMinDisLink().put(adjVertex,distance);
			}
			g.AddStack2(vertex, adjVertex, distance);
		}
		return true;
	}

	private boolean InitialMinTimeLink(Graph g,String vertex,String ver_time,boolean air) {
		List<String> toBeUpdatedVertex = g.getAdj().get(vertex);
		g.getMinTimeLink().clear();
		for(String adjVertex:toBeUpdatedVertex) {
			if(g.getMinTimeLink().get(adjVertex)==null) {
				g.getMinTimeLink().put(adjVertex,new String());
			}
			String alltime;
			int adj_time;
			int arr_t;
			int de_t_s;
			String str[];
			if(air) {
				if(g.getAccInLine().get(vertex).equals(g.getAccInLine().get(adjVertex))) {
					alltime = FindLatestTime(g,vertex,adjVertex,ver_time,0);
					if(alltime.equals("25:59:59")) {
						return false;
					}
					str = alltime.split(",");
					adj_time = CommonTools.TransferTime(str[0]);
				  
					de_t_s =  CommonTools.TransferTime(str[2]);
					arr_t = CommonTools.TransferTime(str[1]);
				} else {
					adj_time= (CommonTools.TransferTime(ver_time)+Integer.parseInt(g.getTransTime().get(vertex+adjVertex)));
					arr_t = adj_time;
					de_t_s= CommonTools.TransferTime(ver_time);
				}
				g.getMinTimeLink().put(adjVertex, CommonTools.SecondToTime(adj_time));
				g.AddStack(vertex, adjVertex, CommonTools.SecondToTime(adj_time), CommonTools.SecondToTime(arr_t), CommonTools.SecondToTime(de_t_s));
			}else {
				if(g.getAccInLine().get(vertex).equals(g.getAccInLine().get(adjVertex))) {
					alltime = FindLatestTime_noair(g,vertex,adjVertex,ver_time,0);
					if(alltime.equals("25:59:59")) {
						return false;
					}
					str = alltime.split(",");
					adj_time = CommonTools.TransferTime(str[0]);
					de_t_s =  CommonTools.TransferTime(str[2]);
					arr_t = CommonTools.TransferTime(str[1]);
				} else {
					adj_time= (CommonTools.TransferTime(ver_time)+Integer.parseInt(g.getTransTime().get(vertex+adjVertex)));
					arr_t = adj_time;
					de_t_s= CommonTools.TransferTime(ver_time);
				}
				g.getMinTimeLink().put(adjVertex, CommonTools.SecondToTime(adj_time));
				g.AddStack(vertex, adjVertex, CommonTools.SecondToTime(adj_time), CommonTools.SecondToTime(arr_t), CommonTools.SecondToTime(de_t_s));
			}
		}
		return true;
	}

	private boolean InitialMinScoreLink(Graph g,String vertex,String ver_time,boolean air) {
		List<String> toBeUpdatedVertex = g.getAdj().get(vertex);
		g.getMinTimeLink().clear();
		for(String adjVertex:toBeUpdatedVertex) {
			if(g.getMinTimeLink().get(adjVertex)==null) {
				g.getMinTimeLink().put(adjVertex,new String());
				g.getMinScoreLink().put(adjVertex,null);
			}
			String alltime;
			int adj_time;
			int arr_t;
			int de_t_s;
			String str[];
			if(air) {
				if(g.getAccInLine().get(vertex).equals(g.getAccInLine().get(adjVertex))) {
					alltime = FindLatestTime(g,vertex,adjVertex,ver_time,0);
					if(alltime.equals("25:59:59")) {
						return false;
					}
					str = alltime.split(",");
					adj_time = CommonTools.TransferTime(str[0]);

					de_t_s =  CommonTools.TransferTime(str[2]);
					arr_t = CommonTools.TransferTime(str[1]);
					//记录评分
					g.getMinScoreLink().put(adjVertex, adj_time+0.0);
					g.AddScore_Stack(vertex, adjVertex, CommonTools.SecondToTime(adj_time), CommonTools.SecondToTime(arr_t), CommonTools.SecondToTime(de_t_s));
				} else {
					adj_time= (CommonTools.TransferTime(ver_time)+Integer.parseInt(g.getTransTime().get(vertex+adjVertex)));
					arr_t = adj_time;
					de_t_s= CommonTools.TransferTime(ver_time);
					//记录评分
					g.getMinScoreLink().put(adjVertex, adj_time+transferweight+0.0);
					g.AddScore_Stack(vertex, adjVertex, CommonTools.SecondToTime(adj_time), CommonTools.SecondToTime(arr_t), CommonTools.SecondToTime(de_t_s));
				}
					g.getMinTimeLink().put(adjVertex, CommonTools.SecondToTime(adj_time));
			}else {
				if(g.getAccInLine().get(vertex).equals(g.getAccInLine().get(adjVertex))) {
					alltime = FindLatestTime_noair(g,vertex,adjVertex,ver_time,0);
					if(alltime.equals("25:59:59")) {
						return false;
					}
					str = alltime.split(",");
					adj_time = CommonTools.TransferTime(str[0]);
					de_t_s =  CommonTools.TransferTime(str[2]);
					arr_t = CommonTools.TransferTime(str[1]);
					g.getMinScoreLink().put(adjVertex, adj_time+0.0);
					g.AddScore_Stack(vertex, adjVertex, CommonTools.SecondToTime(adj_time), CommonTools.SecondToTime(arr_t), CommonTools.SecondToTime(de_t_s));
				} else {
					adj_time= (CommonTools.TransferTime(ver_time)+Integer.parseInt(g.getTransTime().get(vertex+adjVertex)));
					arr_t = adj_time;
					de_t_s= CommonTools.TransferTime(ver_time);
					g.getMinScoreLink().put(adjVertex, adj_time+transferweight+0.0);
					g.AddScore_Stack(vertex, adjVertex, CommonTools.SecondToTime(adj_time), CommonTools.SecondToTime(arr_t), CommonTools.SecondToTime(de_t_s));
				}
					g.getMinTimeLink().put(adjVertex, CommonTools.SecondToTime(adj_time));
			}
		}
		return true;
	}
	//最晚出发时间
	private boolean InitialMinTimeLink3(Graph g,String vertex,String ver_time) {
		List<String> toBeUpdatedVertex = g.getAdj3().get(vertex);
		g.getMinTimeLink().clear();
		for (String adjVertex : toBeUpdatedVertex) {
			if (g.getMinTimeLink().get(adjVertex) == null) {
				g.getMinTimeLink().put(adjVertex, new String());
			}
			String alltime;
			int adj_time = 0;
			int arr_t = 0;
			int de_t_s = 0;
			String str[];
			if (g.getAccInLine().get(vertex).equals(g.getAccInLine().get(adjVertex))) {
				alltime = FindLatestTime3(g, vertex, adjVertex, ver_time, 0);
				if (alltime.equals("25:59:59")) {
					return false;
				}
				str = alltime.split(",");
				adj_time = CommonTools.TransferTime(str[0]);
				arr_t = CommonTools.TransferTime(str[1]);
				de_t_s = CommonTools.TransferTime(str[2]);
			} else {
				adj_time = CommonTools.TransferTime(ver_time);
				arr_t = adj_time;
				de_t_s = adj_time - Integer.parseInt(g.getTransTime().get(adjVertex + vertex));
			}
			g.getMinTimeLink().put(adjVertex, CommonTools.SecondToTime(de_t_s));
			g.AddStack(adjVertex, vertex, CommonTools.SecondToTime(adj_time), CommonTools.SecondToTime(arr_t), CommonTools.SecondToTime(de_t_s));
		}

		return true;
	}

	private String FindLatestTime(Graph g,String ver_start,String ver_end,String ver_start_Time,int isTransStation) {
		List<String> toBeVisitedTime=new ArrayList<String>();
			if(isWeekend(date)) {
				if(g.getTimetable_weekend().get(ver_start+ver_end)==null) {
					return Graph.UpperLimitTime;
				}
				toBeVisitedTime = g.getTimetable_weekend().get(ver_start+ver_end);
			} else{
				if(g.getTimetable_weekday().get(ver_start+ver_end)==null) {
					return Graph.UpperLimitTime;
				}
				toBeVisitedTime = g.getTimetable_weekday().get(ver_start+ver_end);
			}
		int minSecond=1000000;
		//departure time of end_station
		String latestTime=Graph.UpperLimitTime;
		//departure time of start_station
		String latestTime1=Graph.UpperLimitTime;
		String arrtime = Graph.UpperLimitTime;
		String latestArrTime = Graph.UpperLimitTime;
		String str[],start_time,end_time,allTime;
		  
		for(String v_time:toBeVisitedTime) {
			str=v_time.split(",");
			start_time=str[0];
			if(isTransStation==1) {
				// change route
				end_time=str[2];
			} else {
				//no change, same route
				end_time=str[1];
				arrtime = str[2];
			}
			int second= CommonTools.TransferTime(start_time) - CommonTools.TransferTime(ver_start_Time);
			if(second>=0&&second<minSecond) {
				minSecond=second;
				latestTime=end_time;
				latestArrTime = arrtime;
				latestTime1 = start_time;
				//System.out.println(latestTime);;
			}
		  }

          //System.out.println(ver_start+","+ver_end+","+minSecond+","+latestTime);
		  allTime = latestTime+ ","+ latestArrTime+","+ latestTime1;
		  return allTime;
	}
	//查找不包含机场线的最近时间
	private String FindLatestTime_noair(Graph g,String ver_start,String ver_end,String ver_start_Time,int isTransStation) {
		List<String> toBeVisitedTime=new ArrayList<String>();
			if(isWeekend(date)) {
				if(g.getnoair_Timetable_weekend().get(ver_start+ver_end)==null) {
					return Graph.UpperLimitTime+ ","+Graph.UpperLimitTime+ ","+Graph.UpperLimitTime;
				}
				toBeVisitedTime = g.getnoair_Timetable_weekend().get(ver_start+ver_end);
			} else{
				if(g.getnoair_Timetable_weekday().get(ver_start+ver_end)==null) {
					return Graph.UpperLimitTime+ ","+Graph.UpperLimitTime+ ","+Graph.UpperLimitTime;
				}
				toBeVisitedTime = g.getnoair_Timetable_weekday().get(ver_start+ver_end);
			}
		int minSecond=1000000;
		//departure time of end_station
		String latestTime=Graph.UpperLimitTime;
		//departure time of start_station
		String latestTime1=Graph.UpperLimitTime;
		String arrtime = Graph.UpperLimitTime;
		String latestArrTime = Graph.UpperLimitTime;
		String str[],start_time,end_time,allTime;
		  
		for(String v_time:toBeVisitedTime) {
			str=v_time.split(",");
			start_time=str[0];
			if(isTransStation==1) {
				// change route
				end_time=str[2];
			} else {
				//no change, same route
				end_time=str[1];
				arrtime = str[2];
			}

			int second= CommonTools.TransferTime(start_time) - CommonTools.TransferTime(ver_start_Time);

			if(second>=0&&second<minSecond) {
				minSecond=second;
				latestTime=end_time;
				latestArrTime = arrtime;
				latestTime1 = start_time;
			}
		  }

		  allTime = latestTime+ ","+ latestArrTime+","+ latestTime1;
		  return allTime;
	}
	//最晚出发时间的最近时间查找
	private String FindLatestTime3(Graph g,String ver_end,String ver_start,String ver_end_Time,int isTransStation) {
		List<String> toBeVisitedTime=new ArrayList<String>();
		if(isWeekend(date)) {
			if(g.getTimetable_weekend().get(ver_start+ver_end)==null) {
				return Graph.UpperLimitTime ;
			}
			toBeVisitedTime = g.getTimetable_weekend().get(ver_start+ver_end);
		} else{
			if(g.getTimetable_weekday().get(ver_start+ver_end)==null) {
				return Graph.UpperLimitTime;
			}
			toBeVisitedTime = g.getTimetable_weekday().get(ver_start+ver_end);
		}

		int minSecond=1000000;
		//departure time of end_station
		String latestTime=Graph.UpperLimitTime;
		//departure time of start_station
		String latestTime1=Graph.UpperLimitTime;
		String arrtime = Graph.UpperLimitTime;
		String latestArrTime = Graph.UpperLimitTime;
		String str[],start_time,end_time,allTime;


		for(String v_time:toBeVisitedTime) {
			str=v_time.split(",");
			start_time=str[0];
			if(isTransStation==1) {
				// change route
				end_time=str[2];
			} else {
				//no change, same route
				end_time=str[1];
				arrtime = str[2];
			}

			int second= CommonTools.TransferTime(ver_end_Time) - CommonTools.TransferTime(arrtime);

			if(second>=0&&second<minSecond) {
				minSecond=second;
				latestTime=end_time;
				latestArrTime = arrtime;
				latestTime1 = start_time;
			}
		}

		allTime = latestTime+ ","+ latestArrTime+","+ latestTime1;
		return allTime;
	}
}


