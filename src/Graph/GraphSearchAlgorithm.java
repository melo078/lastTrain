package Graph;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

//http://www.jb51.net/article/64443.htm 
public class GraphSearchAlgorithm {
	  private Set<String> visitedVertex;
	  public String date;
	  public String transPath;
	  public Set<String> UnVisitedVertex=new HashSet<String>();

	  public boolean perform(Graph g, String sourceVertex,String dateString,String time,String end_Vertex,String stationnametoacccode) {
		//cloneG = g;
		if (null == visitedVertex) {
	      visitedVertex = new HashSet<>();
	    }
	    date=dateString;
	    transPath = stationnametoacccode;
	    return Dijkstra(g,sourceVertex,dateString,time,end_Vertex,transPath);
	  }

		public boolean isWeekend(String dateString)
		{
			try{
				DateFormat format = new SimpleDateFormat("yyyy-MM-dd"); 
				Date date = format.parse(dateString); 
			    Calendar cal = Calendar.getInstance();
			    cal.setTime(date);
			    if(cal.get(Calendar.DAY_OF_WEEK)==Calendar.SATURDAY||cal.get(Calendar.DAY_OF_WEEK)==Calendar.SUNDAY)
			 {
			  return true;
			 }
			 else return false;
					
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				return false;
			}

	}
	  
	  private boolean Dijkstra(Graph g,String sourceVertex,String dateString,String time,String end_Vertex,String transPath)
	  {
		try 
		{
			g.getReachable().clear();
			
			if(!InitialMinTimeLink(g,sourceVertex,time))
			{
				return false;
			}
		  	String ver=sourceVertex;
		    visitedVertex.add(sourceVertex);

		  	UnVisitedVertex=g.getUnVisitedVertex();//��ʼ��unVisitedVertex

		    UnVisitedVertex.remove(ver);
		    String ver_Time=time,ver_before=ver;
		    
		    while(!(FindLatestVertex(g,g.getMinTimeLink()).equals("null")))
		    {
		    	ver=FindLatestVertex(g,g.getMinTimeLink());
		    	g.addReachable(ver);
		    	visitedVertex.add(ver);
		    	List<String> toBeVisitedVertex = g.getAdj().get(ver);

		    	for(String v:visitedVertex)
		    	{
			    	if(toBeVisitedVertex.contains(v))
			    		toBeVisitedVertex.remove(v);		    		
		    	}

		    	String latest_time=g.UpperLimitTime;
		    	String latest_Vertex="";
		    	ver_Time=g.getMinTimeLink().get(ver);
          
		    	for(String ver_end : toBeVisitedVertex)
		    	{
		    		int de_t=0;
		    		int de_t_s=0; //departure time of started station 
		    		int arr_t=0;
		    		
		    		String alltime;
		    		String str2[];
		    		String s1=g.getAccInLine().get(ver);
		    		String s2=g.getAccInLine().get(ver_end);
		    		boolean notTransStation=s1.equals(s2);
		    		
		    		if(notTransStation)
		    		{
		    			alltime = FindLatestTime(g,ver,ver_end,ver_Time,0);
		    			str2= alltime.split(","); //str2[0] = departure time, str2[1]= arriving time
		    			de_t = TransferTime(str2[0]);
		    			de_t_s =  TransferTime(str2[2]);
		    			arr_t = TransferTime(str2[1]);
		    		}
		    		else{
		    			de_t=TransferTime(ver_Time)+Integer.parseInt(g.getTransTime().get(ver+ver_end));// bug: change first part to arriving time
		    			arr_t = de_t;
		    			de_t_s= TransferTime(ver_Time);
		    	
		    		}
		    		if(g.getMinTimeLink().get(ver_end)==null)
		    		{
		    			g.getMinTimeLink().put(ver_end,SecondToTime(de_t));
		    			g.AddStack(ver, ver_end, SecondToTime(de_t),SecondToTime(arr_t),SecondToTime(de_t_s));
		    		}
		    		else{
		    			int temp_t=TransferTime(g.getMinTimeLink().get(ver_end));
		    			if(de_t<temp_t)
		    			{
		    				g.getMinTimeLink().put(ver_end,SecondToTime(de_t));
		    				g.AddStack(ver,ver_end, SecondToTime(de_t),SecondToTime(arr_t),SecondToTime(de_t_s));
		    			}
		    		}
		    	}
		    	
		    	if(latest_time.equals(g.UpperLimitTime)==false)
		    		{
		    			ver_before=ver;
		    			ver=latest_Vertex;
		    			ver_Time=latest_time;
		    			UnVisitedVertex.remove(latest_Vertex);
		    		}
		    }
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		return true;
	  }
	  
	  private String FindLatestVertex(Graph g,Map<String,String> map)
	  {
		  String vertex="null";
		  String minTime=g.UpperLimitTime;
		  for (Map.Entry<String, String> entry : map.entrySet()) {
			  if(!visitedVertex.contains(entry.getKey()))
			  {
				  if(TransferTime(entry.getValue())-TransferTime(minTime)<0)
				  {
					vertex=entry.getKey();
					minTime=entry.getValue();
					  };				  
			  }
		  }
		  if(!minTime.equals(g.UpperLimitTime))
			  return vertex;
		  return "null";
	  }
	  
	  private boolean InitialMinTimeLink(Graph g,String vertex,String ver_time)
	  {
		  List<String> toBeUpdatedVertex = g.getAdj().get(vertex);
		  g.getMinTimeLink().clear();
		  for(String adjVertex:toBeUpdatedVertex)
		  {
			  if(g.getMinTimeLink().get(adjVertex)==null)
			  {
				  g.getMinTimeLink().put(adjVertex,new String());
			  }
			  String alltime;
			  int adj_time;
			  int arr_t;
			  int de_t_s;
			  String str[];
			  if(g.getAccInLine().get(vertex).equals(g.getAccInLine().get(adjVertex)))
			  {
				  alltime = FindLatestTime(g,vertex,adjVertex,ver_time,0);
				  if(alltime.equals("25:59:59")) return false;
				  str = alltime.split(",");
				  adj_time = TransferTime(str[0]);
				  
	    		  de_t_s =  TransferTime(str[2]);
	    		  arr_t = TransferTime(str[1]);
				  
			  }
			  else{
				  adj_time= (TransferTime(ver_time)+Integer.parseInt(g.getTransTime().get(vertex+adjVertex)));
				  arr_t = adj_time;
	    		  de_t_s= TransferTime(ver_time);
			  }
			  g.getMinTimeLink().put(adjVertex,SecondToTime(adj_time));
			  g.AddStack(vertex, adjVertex, SecondToTime(adj_time),SecondToTime(arr_t),SecondToTime(de_t_s));
		  }
		  return true;
		  
	  }

	 private String FindLatestTime(Graph g,String ver_start,String ver_end,String ver_start_Time,int isTransStation)
	  {

		 List<String> toBeVisitedTime=new ArrayList<String>();
		 if(isWeekend(date))
		 {
			  if(g.getTimetable_weekend().get(ver_start+ver_end)==null)
			  {
				  return g.UpperLimitTime;  
			  }
			  toBeVisitedTime = g.getTimetable_weekend().get(ver_start+ver_end);//ȡ��v��verʱ��ver���뿪ʱ�� 
		 }
		 else{
			  if(g.getTimetable_weekday().get(ver_start+ver_end)==null)
			  {
				  return g.UpperLimitTime;  
			  }
			  toBeVisitedTime = g.getTimetable_weekday().get(ver_start+ver_end);//ȡ��v��verʱ��ver���뿪ʱ��			 
		 }

		  int minSecond=1000000;
		  String latestTime=g.UpperLimitTime;//departure time of end_station 
		  String latestTime1=g.UpperLimitTime;//departure time of start_station
		  String arrtime = g.UpperLimitTime;
		  String latestArrTime = g.UpperLimitTime;
		  String str[],start_time,end_time,allTime;
		  
		  for(String v_time:toBeVisitedTime)
		  {
			  str=v_time.split(",");
			  start_time=str[0];
			  if(isTransStation==1)
			  {
				  end_time=str[2];// change route 
			  }
			  else
			  {
				  end_time=str[1];//no change, same route 
				  arrtime = str[2];
			  }
			  
			  int second=TransferTime(start_time)-TransferTime(ver_start_Time);
			  //System.out.println(SecondToTime(second));
			  
			  if(second>=0&&second<minSecond)//ע����Ա���ʱ������Ƿ����bug���Լ�v_time�п����пո��´���
			  {
				  minSecond=second;

				  
				  //System.out.println(SecondToTime(minSecond));
				  latestTime=end_time;
				  latestArrTime = arrtime; 
				  latestTime1 = start_time;
				  
				  //System.out.println(latestTime);;
			  }
			  
		  }
//		  System.out.println(ver_start+","+ver_end+","+minSecond+","+latestTime);
		  allTime = latestTime+ ","+ latestArrTime+","+ latestTime1;
		  //System.out.println(allTime); 

		  return allTime;
	  }
	 
	 private int TransferTime(String time)
	 {
		 String str[]=time.split(":");
		 //System.out.println(time);
		 int h=Integer.parseInt(str[0]);
		 if(h==0)
			 h=24;

		 int m=Integer.parseInt(str[1]);
		 int s=Integer.parseInt(str[2]);
		 return h*3600+m*60+s;
	 }
	 
	 private String SecondToTime(int second)
	 {
		 int h=second/3600;
		 int m=(second-h*3600)/60;
		 int s=(second-h*3600-m*60);
		 return h+":"+m+":"+s;
	 }
	 

	  
}


