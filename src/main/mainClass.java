package main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import gov.bjjtw.lastTrain.Graph.Graph;
import gov.bjjtw.lastTrain.Graph.GraphSearchAlgorithm;
import gov.bjjtw.lastTrain.CommonTools.CommonTools;

import java.util.*;
import java.util.Map.Entry;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.stream.Collectors;

import javax.print.DocFlavor.STRING;
import javax.xml.transform.Templates;


public class mainClass {
    private static Graph graph;
    private static String startVertex="";
    private static String endVertex="";
    private static String startTime="";
    private static String dateString="";
    private static ArrayList<String> postpone_opening_stations = new ArrayList<String>();//暂缓开通的车站list
    private static ArrayList<String> S1Line = new ArrayList<String>(); //S1线的车站
    private static String dir=null;
    private static String sameTransStationAdjDist=null;
    private static String stationdistance=null;
    private static String sameTransStationAdj=null;
    private static String timetable_weekday=null;
    private static String timetable_weekend=null;
    private static String acccodeInLine=null;
    private static String stationnametoacccode=null;
    private static String entertime=null;
    private static String acccodeLatLng=null;
    private static Set<String> unVisitedVertex=new HashSet<String>();
    private static Map<String,List<String>>  adj = null;
    private static Map<String,String>  map = new HashMap<>();//存放站点acc与中文名对应
    //可查询末班车路径从此时刻开始，时间越晚g加载的列车运行时刻表数据越少
    private static String loadTimetableTime=null;
    private static final double epsilon = 1e-15;

    public enum Cate {
        REACHABLE_PATH, REACHABLE_STATION, REACHABLE_REVERSE_PATH, REACHABLE_MINTRANSFER_PATH
    }

    public static void fit(){
        initConf("",true);
        graph = fit(true);
        unVisitedVertex = graph.getUnVisitedVertex();
        adj = graph.getAdj();
        postpone_opening_stations.add("150998575");
        postpone_opening_stations.add("150998609");
        postpone_opening_stations.add("150996547");
        postpone_opening_stations.add("150996551");       
        postpone_opening_stations.add("150996793");
        postpone_opening_stations.add("151019567");
        postpone_opening_stations.add("150996779");
        S1Line.add("150999061");
        S1Line.add("150999063");
        S1Line.add("150999065");
        S1Line.add("150999067");
        S1Line.add("150999069");
        S1Line.add("150999071");
        S1Line.add("150999073");
    }

    private static void resetGraph(){
        graph.setAdj(adj);
        graph.setAdj3(adj);
        graph.setUnVisitedVertex(unVisitedVertex);
        graph.cleanMinDisLink();
        graph.cleanMinTimeLink();
        graph.cleanMinScoreLink();
        graph.cleanReachableSt();
        graph.cleanWalkTimeString();
        graph.cleanStack3();
        graph.cleanStack();
        graph.cleanScore_Stack();
        graph.cleanStack2();
        graph.cleanStackPath();
        graph.cleanStackPath2();
        graph.resetParams();
    }
    


    private static void initConf(String filename,Boolean isResource) {
        FileInputStream inStream = null;
        try {
            BufferedReader br = null;
            if (isResource == false) {
                br=new BufferedReader(new FileReader(new File(filename)));
            } else {
                InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("conf.properties");
                br = new BufferedReader(new InputStreamReader(is));
            }

            Properties prop = new Properties();
            prop.load(br);
            sameTransStationAdjDist = prop.getProperty("path.sameTransStationAdjDist").toString();
            sameTransStationAdj = prop.getProperty("path.sameTransStationAdj").toString();
            timetable_weekday = prop.getProperty("path.timetable_weekday").toString();
            timetable_weekend = prop.getProperty("path.timetable_weekend").toString();
            acccodeInLine = prop.getProperty("path.acccodeInLine").toString();
            stationnametoacccode = prop.getProperty("path.stationnametoacccode").toString();
            entertime = prop.getProperty("path.entertime").toString();
            loadTimetableTime = prop.getProperty("conf.loadTimetableTime").toString();
            dir = prop.getProperty("path.dir").toString();
            startTime = prop.getProperty("conf.startTime").toString();
            startVertex = prop.getProperty("conf.startVertex").toString();
            endVertex= prop.getProperty("conf.endVertex").toString();
            dateString= prop.getProperty("conf.dateString").toString();
            stationdistance= prop.getProperty("path.stationdistance");
            acccodeLatLng = prop.getProperty("path.acccodeLatlng").toString();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (NullPointerException ex){
            System.out.println("conf keys wrong");
            ex.printStackTrace();
        }finally {
            if (inStream != null) {
                try {
                    inStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static Graph fit(boolean isResource) {
        Graph empty_graph = new Graph();
        int startTimeToSec= CommonTools.TransferTime(loadTimetableTime);
        //将所有站点以及与其邻接的边添加到图中（不包括同站换乘点）
        AddAllVertexAndEdge(empty_graph,acccodeInLine,isResource);
        //同一站点的不同编码acc添加到图中
        AddSameTransferVertexAndEdge(empty_graph,sameTransStationAdj,sameTransStationAdjDist,isResource);
        AddStationWalkingTime(empty_graph,entertime,isResource);
        ReadTimeTable(empty_graph,stationdistance,timetable_weekday,timetable_weekend,acccodeInLine,stationnametoacccode,startTimeToSec,isResource);
        InitialTransVertexList(empty_graph,stationnametoacccode,isResource);
        LoadStationGeoPosition(empty_graph,acccodeLatLng,isResource);
        LoadAcc(acccodeLatLng,map,isResource);
        return empty_graph;
    }

    private static void LoadAcc(String station, Map<String, String> map, boolean isResource) {
		// TODO Auto-generated method stub
    	try {
            BufferedReader br = null;
            if (isResource == false) {
                br=new BufferedReader(new FileReader(new File(dir+station)));
            } else {
                InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(station);
                br = new BufferedReader(new InputStreamReader(is,"GBK"));
            }            
            String [] str = null;
            String temp = "";
            while((temp=br.readLine())!=null) {
                str=temp.split(",");
                map.put(str[2], str[1]);
                }
            br.close();
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
	}

	private static void AddStationWalkingTime(Graph g,String entertime,boolean isResource) {
        try {
            BufferedReader br = null;
            if (isResource == false) {
                br=new BufferedReader(new FileReader(new File(dir+entertime)));
            } else {
                InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(entertime);
                br = new BufferedReader(new InputStreamReader(is));
            }
            br.readLine();

            String [] str = null;
            String temp = "";
            while((temp=br.readLine())!=null) {
                str=temp.split(",");
                if(str[5].equals("进站")&&str[6].equals("全天")&&str[7].equals("工作日")) {
                    g.Add_WalkTime(str[0],str[4]);
                }
            }
            br.close();
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

    private static void InitialTransVertexList(Graph g,String transPath,boolean isResource) {
        try {
            String temp="",str[];
            BufferedReader br = null;
            if (isResource == false) {
                br=new BufferedReader(new FileReader(new File(dir+transPath)));
            } else {
                InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(transPath);
                br = new BufferedReader(new InputStreamReader(is));
            }
            while((temp=br.readLine())!=null) {
                str=temp.split(",");
                g.Add_UnVisitedVertex(str[2]);
            }
            br.close();
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

    private static void AddAllVertexAndEdge(Graph g, String filePath,boolean isResource) {
        String temp1 = "", str1[], line = "", acccode = "";
        BufferedReader br = null;
        try {
            if (isResource == false) {
                br = new BufferedReader(new FileReader(new File(dir + filePath)));
            } else {
                InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(filePath);
                br = new BufferedReader(new InputStreamReader(is));
            }

            temp1 = br.readLine();
            str1 = temp1.split(",");
            line = str1[1];
            acccode = str1[2];
            g.addVertex(str1[2]);

            while ((temp1 = br.readLine()) != null) {
                str1 = temp1.split(",");
                g.addVertex(str1[2]);
                if (str1[1].equals(line)) {
                    g.addEdge(acccode, str1[2]);
                    g.addEdge(str1[2], acccode);
                }
                line = str1[1];
                acccode = str1[2];
            }
            g.addEdge("151020057", "151020055");
            //去除机场线的单行问题
            g.RemoveEdge("151020059", "151020055");
            g.RemoveEdge("151020057", "151020059");
            // 2号线和10号线首末站需要考虑环路
            g.addEdge("150995474","150995473");
            g.addEdge("150995473", "150995474");
            g.addEdge("151018007","151018009");
            g.addEdge("151018009", "151018007");
            br.close();
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

    private static void LoadStationGeoPosition(Graph g, String acccodeLatLng, Boolean isResource) {
        String temp1 = null;
        String [] str1 = null;
        try {
            BufferedReader br = null;
            if (isResource == false) {
                br=new BufferedReader(new FileReader(new File(dir+acccodeLatLng)));
            } else {
                InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(acccodeLatLng);
                br = new BufferedReader(new InputStreamReader(is));
            }
            while((temp1=br.readLine())!=null) {
                str1=temp1.split(",");
                g.addGeoPosition(str1[2],str1[3]+","+str1[4]);
            }
            br.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    private static void ReadTimeTable(Graph g,String stationdistance,String timetable_weekday,String timetable_weekend,String acccodeInLine,String stationnametoacccode,int startTimeToSec,boolean isResource) {
        try {
            String str[] = null, temp = "";
            BufferedReader br_weekday = null;
            BufferedReader br_weekend = null;
            BufferedReader br_acc_line = null;
            BufferedReader br_dist = null;
            if (isResource == false) {
                br_weekday = new BufferedReader(new FileReader(new File(dir + timetable_weekday)));
                br_dist = new BufferedReader(new FileReader(new File(dir + stationdistance)));
                br_weekend = new BufferedReader(new FileReader(new File(dir + timetable_weekend)));
                br_acc_line = new BufferedReader(new FileReader(new File(dir + acccodeInLine)));
            } else {
                InputStream is1 = Thread.currentThread().getContextClassLoader().getResourceAsStream(timetable_weekday);
                br_weekday = new BufferedReader(new InputStreamReader(is1));
                InputStream is2 = Thread.currentThread().getContextClassLoader().getResourceAsStream(timetable_weekend);
                br_weekend = new BufferedReader(new InputStreamReader(is2));
                InputStream is3 = Thread.currentThread().getContextClassLoader().getResourceAsStream(acccodeInLine);
                br_acc_line = new BufferedReader(new InputStreamReader(is3));
                InputStream is4 = Thread.currentThread().getContextClassLoader().getResourceAsStream(stationnametoacccode);
                InputStream is5 = Thread.currentThread().getContextClassLoader().getResourceAsStream(stationdistance);
                br_dist = new BufferedReader(new InputStreamReader(is5));
            }


            while ((temp = br_weekday.readLine()) != null) {
                str = temp.split(",");
              //按终点站或起始站有无机场线，间数据加载入两个map中
                String acccode1 = str[0] + str[1];
                boolean judge1 =  str[0].equals("151020057") || str[0].equals("151020059");
                boolean judge2 =  str[1].equals("151020055") || str[1].equals("151020057") || str[1].equals("151020059");
                if((!(judge1 || judge2)) && startTimeToSec <= CommonTools.TransferTime(str[2]) ) {
                	g.Add_weeekday_noair_timetable(acccode1, str[2], str[3], str[5]);
                }
                //每次加载列车运行时刻表时，只加载计算时间之后的数据
                if (startTimeToSec <= CommonTools.TransferTime(str[2])) {
                    g.Add_weeekday_timetable(acccode1, str[2], str[3], str[5]);
                }
            }

            br_weekday.close();


            while ((temp = br_dist.readLine()) != null) {
                str = temp.split(",");
                String acccode1 = str[0] + str[1];
                int dis=Integer.parseInt(str[2]);
                g.Add_stationdistance(acccode1, dis);
            }

            br_dist.close();
            //按终点站或起始站有无机场线，间数据加载入两个map中
            while ((temp = br_weekend.readLine()) != null) {
                str = temp.split(",");
                String acccode1 = str[0] + str[1];
                boolean judge1 =  str[0].equals("151020057") || str[0].equals("151020059");
                boolean judge2 =  str[1].equals("151020057") || str[1].equals("151020059");
                if((!(judge1 || judge2)) && startTimeToSec <= CommonTools.TransferTime(str[2]) ) {
                	g.Add_weeekend_noair_timetable(acccode1, str[2], str[3], str[5]);
                }
                //每次加载列车运行时刻表时，只加载计算时间之后的数据
                if (startTimeToSec <= CommonTools.TransferTime(str[2])) {
                    g.Add_weeekend_timetable(acccode1, str[2], str[3], str[5]);
                }
            }
            br_weekend.close();

            while ((temp = br_acc_line.readLine()) != null) {
                str = temp.split(",");
                g.Add_AccInLine(str[2], str[1]);
            }

            br_acc_line.close();
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

    private static void AddSameTransferVertexAndEdge(Graph g,String sameTransStationAdj,String sameTransStationAdjDist,boolean isResource) {
        String temp1 = "";
        String [] str1 = null;
        try {
            BufferedReader br = null;
            BufferedReader br_dist = null;
            if (isResource == false) {
                br = new BufferedReader(new FileReader(new File(dir+sameTransStationAdj)));
                br_dist = new BufferedReader(new FileReader(new File(dir+sameTransStationAdjDist)));
            } else {
                InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(sameTransStationAdj);
                br = new BufferedReader(new InputStreamReader(is));
                InputStream is2 = Thread.currentThread().getContextClassLoader().getResourceAsStream(sameTransStationAdjDist);
                br_dist = new BufferedReader(new InputStreamReader(is2));
            }

            while((temp1=br.readLine())!=null) {
                str1=temp1.split(",");
                g.addVertex(str1[0]);
                g.addEdge(str1[0], str1[1]);
                g.addTransTime(str1[0],str1[1],str1[2]);
            }
            br.close();
            br_dist.readLine();
            while((temp1=br_dist.readLine())!=null) {
                str1=temp1.split(",");
                int dist=Integer.parseInt(str1[3]);
                g.Add_stationdistance(str1[0]+str1[1],dist);
            }
            br_dist.close();
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }
    //基于最小时间
    private static LinkedList<String> ComputeReachablePath(String startvertex,String dateString,String time,String endvertex,Boolean isReverse) {
        String ver=endvertex;
        String content="";
        Boolean hasValidStation = false;
        LinkedList<String> path = new LinkedList<String>();
        graph.setStack3();
        if (isReverse) {
            ver=startvertex;
        }

        while(!graph.getStack().empty()) {
            content=graph.getStack().pop();
            String str[]=content.split(",");
            String station1 = str[1];
            String station0 = str[0];
        	//System.out.println(content);
            if (isReverse){
                station1 = str[0];
                station0 = str[1];
            }
            if(station1.equals(ver)) {
                graph.AddStackPath(content);
                ver=station0;
            }
        }
        try {
            String context = "";
            while(!graph.getPathStack().empty()) {
                content=graph.getPathStack().pop();
                String str[]=content.split(",");
                context = str[0]+","+str[1]+","+str[4]+","+str[3]+","+str[2];
                if (str[2].equals(Graph.UpperLimitTime) == false) {
                    hasValidStation = true;
                }
                path.add(context);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (hasValidStation == false) {
            path.clear();
        }
        return path;
    }

    public static LinkedList<String> GetReachablePath(String datestring, String starttime, String startvertex, String endvertex,boolean isMinTransfer) {
        String [] tmp;
        LinkedList<String> result ;
        if (isMinTransfer){
            result = GetReachable(datestring,starttime,startvertex,endvertex,Cate.REACHABLE_MINTRANSFER_PATH);
        }else {
            result = GetReachable(datestring,starttime,startvertex,endvertex,Cate.REACHABLE_PATH);
        }
        LinkedList<String> output = new LinkedList<String>();
        //String arrivetime = ""; //记录第一段到达最后一个可达车站的时间
        LinkedList<String> simple = new LinkedList<String>();//记录输出路径包含的站点acc
        LinkedList<String> simplenames = new LinkedList<String>();//记录输出路径包含的站点中文名
        if (result.size()>0) {
            for (String item : result) {
                tmp = item.split(",");
                //output.add(tmp[0] + "," + tmp[1]+"," +tmp[2] +","+tmp[3] +","+tmp[4] +",1");
                if (postpone_opening_stations.contains(tmp[0])) {
                    output.add(tmp[0] + "," + tmp[2] + ",2");
				}else {
				    output.add(tmp[0] + "," + tmp[2] + ",1");
				}          
                simple.add(tmp[0]);
            }
            tmp = result.getLast().split(",");
            if (postpone_opening_stations.contains(tmp[1])) {
                output.add(tmp[1] + "," + tmp[3] + ",2");
			}else {
			    output.add(tmp[1] + "," + tmp[3] + ",1");
			}    
            startvertex = tmp[1];
           // arrivetime = tmp[3];
            simple.add(tmp[1]);
        } else {
            output.add(startvertex+","+starttime+",1");
           // arrivetime = starttime;
            simple.add(startvertex);
        }
        
        for(String s: simple) {
        	if (!simplenames.contains(map.get(s))) {
				simplenames.add(map.get(s));
			}
        }
        //考虑S1线未与1号线连接的情况
        
        //出发地在S1线，目的地在别的线上
//        if (startvertex.equals(endvertex) == false && S1line.contains(startvertex) && !S1line.contains(endvertex)){
//            if (!startvertex.equals("151018273")) {
//            	   resetGraph();  
//            	   LinkedList<String> result2 = GraphTraversal2(startvertex, "151018273", stationnametoacccode);
//                   for (String item: result2){
//                   		String accname = item.split(",")[1];
//                   		if (!simplenames.contains(map.get(accname))) {
//                   			output.add(item.split(",")[1]+",,0");
//                   		}                   		
//                   }
//             }
//            resetGraph();
//            startvertex = "150995203";
//            LinkedList<String> reachable_result = GetReachable(datestring,arrivetime,startvertex,endvertex,Cate.REACHABLE_PATH);
//            if (reachable_result.size()>0) {
//                for (String item : reachable_result) {
//                    tmp = item.split(",");
//                    //output.add(tmp[0] + "," + tmp[1]+"," +tmp[2] +","+tmp[3] +","+tmp[4] +",1");
//                    if (Postpone_opening.contains(tmp[0])) {
//                        output.add(tmp[0] + "," + tmp[2] + ",2");
//    				}else {
//    				    output.add(tmp[0] + "," + tmp[2] + ",1");
//    				}          
//                    simple.add(tmp[0]);
//                }
//                tmp = reachable_result.getLast().split(",");
//                if (Postpone_opening.contains(tmp[1])) {
//                    output.add(tmp[1] + "," + tmp[3] + ",2");
//    			}else {
//    			    output.add(tmp[1] + "," + tmp[3] + ",1");
//    			}    
//                startvertex = tmp[1];
//                simple.add(tmp[1]);
//            } else {
//                output.add(startvertex+",,0");
//                simple.add(startvertex);
//            }
//            for(String s: simple) {
//            	if (!simplenames.contains(map.get(s))) {
//    				simplenames.add(map.get(s));
//    			}
//            }                        
//        }
        
        //始发地在别的线上，目的地在S1线上
//        if (startvertex.equals(endvertex) == false && S1line.contains(endvertex) && !S1line.contains(startvertex)){
//        	if (!startvertex.equals("150995203")) {
//        		   resetGraph();
//                   LinkedList<String> result2 = GraphTraversal2(startvertex, "150995203", stationnametoacccode);
//                   for (String item: result2){
//                   		String accname = item.split(",")[1];
//                   		if (!simplenames.contains(map.get(accname))) {
//                   			output.add(item.split(",")[1]+",,0");
//                   		}            		
//                   }
//			}
//            resetGraph();
//            LinkedList<String> reachable_result = GetReachable(datestring,arrivetime,"151018273",endvertex,Cate.REACHABLE_PATH);
//            if (reachable_result.size()>0) {
//                for (String item : reachable_result) {
//                    tmp = item.split(",");
//                    //output.add(tmp[0] + "," + tmp[1]+"," +tmp[2] +","+tmp[3] +","+tmp[4] +",1");
//    				    output.add(tmp[0] + "," + tmp[2] + ",1");       
//                    simple.add(tmp[0]);
//                }
//                tmp = reachable_result.getLast().split(",");
//    			output.add(tmp[1] + "," + tmp[3] + ",1"); 
//                startvertex = tmp[1];
//                simple.add(tmp[1]);
//            } else {
//            	startvertex = "151018273";
//                simple.add("151018273");
//            	output.add("151018273,,0");
//            }
//            for(String s: simple) {
//            	if (!simplenames.contains(map.get(s))) {
//    				simplenames.add(map.get(s));
//    			}
//            }                                    
//        }
        
        //不可达的情况
        if (startvertex.equals(endvertex) == false){
            resetGraph();
            LinkedList<String> unreachable_result = GraphTraversal2(startvertex, endvertex, stationnametoacccode);
            for (String item: unreachable_result){
            	String accname = item.split(",")[1];
            	if (!simplenames.contains(map.get(accname))) {
            			output.add(item.split(",")[1]+",,0");
            	}
            		
            }
        }
        
        return output;
    }

    public static LinkedList<String> GetReachableStation(String datestring, String starttime, String startvertex){
        return GetReachable(datestring,starttime,startvertex,"151020057",Cate.REACHABLE_STATION);
    }

    public static LinkedList<String> GetReachableStationLatestPath(String datestring, String endtime, String startvertex, String endvertex){
        LinkedList<String> result = GetReachable(datestring,endtime,startvertex,endvertex,Cate.REACHABLE_REVERSE_PATH);
        if (result != null){
            Collections.reverse(result);
        } else {
            result = new LinkedList<String>();
        }
        return result;
    }
    //基于时间最短的遍历
    private static LinkedList<String> GraphTraversal(String startVertex, String endVertex, String startTime, String dateString,String stationnametoacccode,Boolean isReverse) {
        graph.InitialSearchStartVertex(startVertex,dateString,startTime,endVertex);
        GraphSearchAlgorithm graphSearchAlgorithm =new GraphSearchAlgorithm();
        if(graphSearchAlgorithm.perform(graph, startVertex, dateString, startTime, endVertex,stationnametoacccode, isReverse)) {
            return ComputeReachablePath(startVertex,dateString,startTime,endVertex,isReverse);
        } else {
            return null;
        }
    }
    //基于分数最小的遍历
    private static LinkedList<String> GraphTraversal3(String startVertex, String endVertex, String startTime, String dateString,String stationnametoacccode,Boolean isReverse) {
        graph.InitialSearchStartVertex(startVertex,dateString,startTime,endVertex);
        GraphSearchAlgorithm graphSearchAlgorithm =new GraphSearchAlgorithm();
        if(graphSearchAlgorithm.perform3(graph, startVertex, dateString, startTime, endVertex,stationnametoacccode, isReverse)) {
            return ComputeReachablePath3(startVertex,dateString,startTime,endVertex,isReverse);
        } else {
            return null;
        }
    }

    private static double GetGeoDistanceBetweenStations(String station1, String station2){
        Float [] lastStationPosition = graph.getGeoPosition(station1);
        Float [] destStationPosition = graph.getGeoPosition(station2);
        if (lastStationPosition[0] - 0.0 > epsilon && lastStationPosition[1] - 0.0 > epsilon && destStationPosition[0] - 0.0 > epsilon && destStationPosition[1] - 0.0 > epsilon){
            return CommonTools.SimpleDist(lastStationPosition[1],lastStationPosition[0],destStationPosition[1],destStationPosition[0]);
        }
        return -1.0;
    }

    private static LinkedList<String> GetReachable(String datestring, String starttime, String startvertex, String endvertex, Cate type) {
        resetGraph();
        graph.setIsWeekend(CommonTools.isWeekend(datestring));
        //0是00:00:00的秒数，18000是05:00:00的秒数
        LinkedList<String> reachableStation ;
            switch (type) {
                case REACHABLE_STATION:
                    reachableStation = GraphTraversal(startvertex, endvertex, starttime, datestring, stationnametoacccode,false);
                    LinkedList<String> stations = graph.getReachable();
                    if (reachableStation != null ) {
                    	if (!S1Line.contains(startvertex) && graph.getMinTimeLink().get("151018273")!=null) {
                    		int jinanqiao_arrivetime = CommonTools.TransferTime(graph.getMinTimeLink().get("151018273"));
                        	int time_difference = jinanqiao_arrivetime - CommonTools.TransferTime("21:07:00");//比较到达金安桥的时间是否小于金安桥末班车的时间
                        	if (time_difference>0) {
                        		stations.remove("151018273");
    							return stations;
    						}
                            return stations;
						}else if(S1Line.contains(startvertex) && graph.getMinTimeLink().get("150995203")!=null){
							int jinanqiao_arrivetime = CommonTools.TransferTime(graph.getMinTimeLink().get("150995203"));
                        	int time_difference = jinanqiao_arrivetime - CommonTools.TransferTime("23:30:00");//比较到达苹果园的时间是否小于苹果园末班车的时间
                        	if (time_difference>0) {
                        		stations.remove("150995203");
    							return stations;
    						}
                            return stations;
						}else {
							return stations;
						}
                    } else {
                        return null;
                    }
                case REACHABLE_REVERSE_PATH:
                    reachableStation = GraphTraversal(startvertex, endvertex, starttime, datestring, stationnametoacccode,true);
                    if (reachableStation != null && reachableStation.size()>0) {
                        return reachableStation;
                    } else {
                        return null;
                    }
                case REACHABLE_MINTRANSFER_PATH:
                    reachableStation = GraphTraversal3(startvertex, endvertex, starttime, datestring, stationnametoacccode,false);
                   if(reachableStation !=null && reachableStation.size()>0){
                        return reachableStation;
                    } else {
                        String minStation = startvertex;
                        double minDist = GetGeoDistanceBetweenStations(startvertex,endvertex);
                        for(String line: graph.getReachable()){
                            String [] items = line.split(",");
                            double currentDistance = GetGeoDistanceBetweenStations(items[0],endvertex);
                            if (currentDistance - 0.0000001 > epsilon && minDist - currentDistance > epsilon) {
                                minStation = items[0];
                                minDist = currentDistance;
                            }
                        }
                        if (minStation.equals(startvertex) == false){
                            return ComputeReachablePath3(startvertex,datestring,starttime,minStation,false);
                        } else {
                            return reachableStation;
                        }
                    }

                case REACHABLE_PATH:
                    reachableStation = GraphTraversal(startvertex, endvertex, starttime, datestring, stationnametoacccode,false);
                    if (reachableStation != null && reachableStation.size()>0) {
                        return reachableStation;
                    } else {
                        String minStation = startvertex;
                        double minDist = GetGeoDistanceBetweenStations(startvertex,endvertex);
                        for(String line: graph.getReachable()){
                            String [] items = line.split(",");
                            double currentDistance = GetGeoDistanceBetweenStations(items[0],endvertex);
                            if (currentDistance - 0.0000001 > epsilon && minDist - currentDistance > epsilon) {
                                minStation = items[0];
                                minDist = currentDistance;
                            }
                        }
                        if (minStation.equals(startvertex) == false){
                            return ComputeReachablePath2(startvertex,datestring,starttime,minStation,false);
                        } else {
                            return reachableStation;
                        }
                    }
                default:
                    return null;
            }
    }
    //基于距离
    private static LinkedList<String> ComputeReachablePath2(String startvertex2, String datestring2, String starttime2,
			String minStation, boolean isReverse) {
		// TODO Auto-generated method stub
    	  String ver=minStation;
          String content="";
          Boolean hasValidStation = false;
          LinkedList<String> path = new LinkedList<String>();

          if (isReverse) {
              ver=startvertex2;
          }

          while(!graph.getStack3().empty()) {
              content=graph.getStack3().pop();
              String str[]=content.split(",");
              String station1 = str[1];
              String station0 = str[0];
              if (isReverse){
                  station1 = str[0];
                  station0 = str[1];
              }
              if(station1.equals(ver)) {
                  graph.AddStackPath(content);
                  ver=station0;
              }
          }
          try {
              String context = "";
              while(!graph.getPathStack().empty()) {
                  content=graph.getPathStack().pop();
                  String str[]=content.split(",");
                  context = str[0]+","+str[1]+","+str[4]+","+str[3]+","+str[2];
                  if (str[2].equals(Graph.UpperLimitTime) == false) {
                      hasValidStation = true;
                  }
                  path.add(context);
              }
          } catch (Exception e) {
              e.printStackTrace();
          }
          if (hasValidStation == false) {
              path.clear();
          }
          return path;
	}
    //基于分数
    private static LinkedList<String> ComputeReachablePath3(String startvertex2, String datestring2, String starttime2,
                                                            String minStation, boolean isReverse) {
        // TODO Auto-generated method stub
        String ver=minStation;
        String content="";
        Boolean hasValidStation = false;
        LinkedList<String> path = new LinkedList<String>();
        graph.setStack4();
        if (isReverse) {
            ver=startvertex2;
        }

        while(!graph.getStack4().empty()) {
            content=graph.getStack4().pop();
            String str[]=content.split(",");
            String station1 = str[1];
            String station0 = str[0];
            if (isReverse){
                station1 = str[0];
                station0 = str[1];
            }
            if(station1.equals(ver)) {
                graph.AddStackPath(content);
                ver=station0;
            }
        }
        try {
            String context = "";
            while(!graph.getPathStack().empty()) {
                content=graph.getPathStack().pop();
                String str[]=content.split(",");
                context = str[0]+","+str[1]+","+str[4]+","+str[3]+","+str[2];
                if (str[2].equals(Graph.UpperLimitTime) == false) {
                    hasValidStation = true;
                }
                path.add(context);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (hasValidStation == false) {
            path.clear();
        }
        return path;
    }
	// 三元桥机场线bug
    public static void main(String[] args) throws IOException {
    	fit();
//    	LinkedList<String> stations = mainClass.GetReachableStation("2018-06-13","21:10:30","150995204");
//    	  for(String string : stations) {
//    			  System.out.println(string);
//
//    	  }
        	  
          
//		LinkedList<String> path = mainClass.GetReachablePath("2018-06-13","23:00:00","151018037","150996029",false);
//         for(String string : path) {
//        	  System.out.println(string);
//          }
//          System.out.println("------------------------------------------------");
		LinkedList<String> path1 = mainClass.GetReachablePath("2018-06-13","22:58:00","151018037","150996029",true);
         for(String string : path1) {
        	  System.out.println(string);
          }
          //System.out.println(graph.getMinScoreLink().get("150997279"));
//        BufferedReader bReader = null;
//   	 	BufferedReader path = null;
//   	 	String temps = "";
//    	Map<String, String> accnames = new HashMap<>();
//   	 	Map<String, String> lines = new HashMap<>();
//   	 	Map<String, String> map = new HashMap<>();
//		map.put("L091","房山线");
//		map.put("L011","八通线");
//		map.put("L021","机场线");
//		map.put("L051","亦庄线");
//		map.put("L131","昌平线");
//		map.put("L092","燕房线");
//		map.put("L101","西郊线");
//		map.put("L010","1号线");
//		map.put("L020","2号线");
//		map.put("L040","4号线");
//		map.put("L050","5号线");
//		map.put("L060","6号线");
//		map.put("L070","7号线");
//		map.put("L080","8号线");
//		map.put("L090","9号线");
//		map.put("L100","10号线");
//		map.put("L130","13号线");
//		map.put("L140","14号线");
//		map.put("L141","14号线");
//		map.put("L150","15号线");
//		map.put("L160","16号线");
//		map.put("L161","S1线");
//   	 	bReader = new BufferedReader(new InputStreamReader(new FileInputStream("C:\\Users\\ssx\\Desktop\\acc_new.csv"),"GBK"));
//   	 	bReader.readLine();
//   	 	Map<String, String> accname = new HashMap<>();
//   	 	while((temps=bReader.readLine())!=null) {
//   	 		String[] string = temps.split(",");
//   	 		accnames.put(string[0], string[1]);
//   	 		lines.put(string[0], string[2]);
//   	 		String chineselinename = map.get(string[2]);
//   	 		accname.put(string[0], string[1]+","+chineselinename);
//   	 	}
//   	 	bReader.close();
        //LinkedList<String> wantedpath1 = mainClass.GetReachablePath("2018-6-22","20:33:00","151018775","151018035");
   	 	//        String filename = "C:\\Users\\ssx\\Desktop\\temp_result.csv";
//        BufferedWriter weekr=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename),"GBK"));
//        for(String string :wantedpath1) {
//        	String [] strings = string.split(",");
//        	String result = strings[3];
//        	String os = accnames.get(strings[0]);
//        	String ds = accnames.get(strings[1]);
//   	 		weekr.write(os+","+ds+","+result+"\n");
//   	 	}
//        weekr.close();

//   	 	int s =1;
//   	 	String starttime ="16:03:35";
//   	 	BufferedReader b = new BufferedReader(new InputStreamReader(new FileInputStream("C:\\Users\\ssx\\Desktop\\lichen5.csv"),"GBK"));
//	 	while((temps=b.readLine())!=null) {
//   	 		String[] string = temps.split(",");
//   		 	String filename3 = "C:\\Users\\ssx\\Desktop\\table\\李臣5"+s+".csv";
//   		 	BufferedWriter weekr=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename3),"GBK"));
//   		 	LinkedList<String> wantedpath1 = mainClass.GetReachablePath("2018-06-22",starttime, string[0],string[1]);
//   		 	saveresult(weekr,wantedpath1,accnames,lines,map,"2018-06-22"+starttime);
//   	 	 	weekr.close();
//   	 	 	s++;
//	 		String lasttime = wantedpath1.getLast().split(",")[3];
//	 		int plus = CommonTools.TransferTime(lasttime)+300;
//	 		starttime = CommonTools.SecondToTime(plus);
//   	 	}


   	 
    }

//    private static void saveascsv(BufferedWriter weekdayr, LinkedList<String> wantedpath1, Map<String, String> accnames, Map<String, String> lines, Map<String, String> map, String starttime) throws IOException {
//		// TODO Auto-generated method stub
//    	weekdayr.write("末班车路径查询测试表:\n");
//    	weekdayr.write("测试日期:\n");
//    	weekdayr.write("计划测试时间:,"+starttime+",\n");
//    	String[] first = wantedpath1.getFirst().split(",");
//    	String[] last = wantedpath1.getLast().split(",");
//    	weekdayr.write("路径起始点:,"+accnames.get(first[0])+"("+first[0]+")-->"+accnames.get(last[1])+"("+last[1]+")\n");
//    	weekdayr.write("进站刷卡时刻:,,测量员到达站台时刻:,,\n");
//    	weekdayr.write("车站序号,所属线路,车站,列车到达站台停稳即将开门时刻,是否换乘,测量员到达接续站台时刻,列车到达站台时刻,列车关门即将发车时刻\n");
//		int i =1;
//		ArrayList<String> list = new ArrayList<String>();
//    	for(String item :wantedpath1) {
//	        	String[] string = item.split(",");
//	        	String name1 = accnames.get(string[0]);
//	        	String linename1 = lines.get(string[0]);
//	        	String linename2 = lines.get(string[1]);
//	        	if(linename1.equals(linename2) && !list.contains(name1)) {
//	        		list.add(name1);
//	        		String weekdayresult1 = i +","+ map.get(linename1)+","+name1+",,否,,,,,\n";
//	        		weekdayr.write(weekdayresult1);
//	        		i++;
//	        	}
//	        	if(!linename1.equals(linename2) && !list.contains(name1))
//	        	{
//	        		list.add(name1);
//	        		String weekdayresult1 = i +","+ map.get(linename1)+"-->"+map.get(linename2)+","+name1+",,是,,,,,\n";
//	        		weekdayr.write(weekdayresult1);
//	        		i++;
//	        	}	        	
//	        	
//	        }
//	        String[] string = wantedpath1.getLast().split(",");
//	    	String name2 = accnames.get(string[1]);
//	    	String linename2 = lines.get(string[1]);
//	    	String weekdayresult2 = i +","+ map.get(linename2)+","+name2+",,否,,,,,\n";
//	        weekdayr.write(weekdayresult2);
//	        weekdayr.write("测试人员签字： ");
//	        weekdayr.close();
//	        //System.out.println("到达目的地时间： "+string[3]);
//	}
//    
//    
//    private static void saveresult(BufferedWriter weekdayr, LinkedList<String> wantedpath1, Map<String, String> accnames, Map<String, String> lines, Map<String, String> map, String starttime) throws IOException {
//		// TODO Auto-generated method stub
//    	String[] first = wantedpath1.getFirst().split(",");
//    	String[] last = wantedpath1.getLast().split(",");
//    	weekdayr.write("路径起始点:,"+accnames.get(first[0])+"("+first[0]+")-->"+accnames.get(last[1])+"("+last[1]+")\n");
//    	weekdayr.write("计划测试时刻,+"+starttime+"\n");
//		int i =1;
//    	for(String item :wantedpath1) {
//	        	String[] string = item.split(",");
//	        	String name1 = accnames.get(string[0]);
//	        	String name2 = accnames.get(string[1]);
//	        	String linename1 = lines.get(string[0]);
//	        	String weekdayresult1 = i +","+ map.get(linename1)+","+name1+","+name2+","+string[2]+","+string[3]+","+string[4]+",\n";
//	        	weekdayr.write(weekdayresult1);
//	        	i++;
//	        }
//	        weekdayr.close();
//	}
    
    

	private static void outputTestResult(String testName,ArrayList<Long> timeConsume){
        System.out.println("=========="+testName+"==========");
        System.out.println("0ms - 100ms:"+timeConsume.stream().filter(x -> x <= 100).count());
        System.out.println("101ms - 150ms:"+timeConsume.stream().filter(x-> x>100 && x<= 150).count());
        System.out.println("151ms - 200ms:"+timeConsume.stream().filter(x-> x>150 && x<=200).count());
        System.out.println("201ms - 250ms:"+timeConsume.stream().filter(x-> x>200 && x<=250).count());
        System.out.println("251ms - 350ms:"+timeConsume.stream().filter(x-> x>250 && x<=350).count());
        System.out.println("351ms - :"+timeConsume.stream().filter(x-> x>350).count());
        System.out.println("avg:"+ timeConsume.stream().collect(Collectors.averagingInt(x -> x.intValue() ))  );
    }

    //基于距离
    private static LinkedList<String> GetReachableStation2(String startvertex,String endvertex) {
        resetGraph();
        String startVertex1 = startvertex;
        String  endVertex1=endvertex;
        LinkedList<String> reachableStation = GraphTraversal2(startVertex1, endVertex1, stationnametoacccode);
        if (reachableStation != null) {
            return reachableStation;
        } else {
            return null;
        }
    }
    //基于距离
    private static LinkedList<String> GraphTraversal2(String startVertex, String endVertex,String stationnametoacccode) {
        graph.InitialSearchStartVertex2(startVertex,endVertex);
        GraphSearchAlgorithm graphSearchAlgorithm =new GraphSearchAlgorithm();
        if(graphSearchAlgorithm.perform2(graph, startVertex, endVertex,stationnametoacccode)) {
            //将可达路径返回至sh输出
            return GetshortPath(startVertex,endVertex);
        } else {
            return null;
        }
    }
    //基于距离
    private static LinkedList<String> GetshortPath(String startvertex,String endvertex) {
        String ver=endvertex;
        String content="";
        LinkedList<String> path = new LinkedList<String>();
        while(!graph.getStack2().empty()) {
            content=graph.getStack2().pop();
            String str[]=content.split(",");
            if(str[1].equals(ver)) {
                graph.AddStackPath2(content);
                ver=str[0];
            }
        }
        try {
            String context = "";
            while(!graph.getPathStack2().empty()) {
                content=graph.getPathStack2().pop();
                String str[]=content.split(",");
                context = str[0]+","+str[1]+","+str[2];
                path.add(context);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return path;
    }


}
