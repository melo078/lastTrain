package gov.bjjtw.lastTrain.Preprocess;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import gov.bjjtw.lastTrain.CommonTools.CommonTools;


import main.mainClass;

public class DivideTimeTable {

	public void ReadTimeTable(String filename)//将列车时刻表按照工作日/周末以及上行/下行拆分成四个表格
	{
		try {
			//sortTable(filename);
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filename),"GBK"));
			BufferedWriter bw_weekday=new BufferedWriter(new FileWriter(new File(filename.substring(0, 9)+"_timetable_allstation_0702fixed.csv"),true));
			//BufferedWriter bw_weekend=new BufferedWriter(new FileWriter(new File("D:\\weekend_timetable_allstation_0702fixed.csv"),true));
			String temp,start_acc,start_departuretime,end_acc,end_departuretime,str[],tripNumber,week,end_arrivingtime;
			str=br.readLine().split(",");
			start_acc=str[0];
			start_departuretime=str[1];
			week=str[2];
			tripNumber=str[5];
			while((temp=br.readLine())!=null){
				str=temp.split(",");
				if(tripNumber.equals(str[5]))
				{
					end_acc=str[0];
					end_departuretime=str[1];
					end_arrivingtime=str[6];
					week=str[2];					
					bw_weekday.write(start_acc+","+end_acc+","+start_departuretime+","+end_departuretime+","+tripNumber+","+end_arrivingtime+"\n");
					start_acc=end_acc;
					start_departuretime=end_departuretime;
				}
				else{
					start_acc=str[0];
					start_departuretime=str[1];
					tripNumber=str[5];
					week=str[2];
				}
			}
			br.close();
			bw_weekday.close();
			File file = new File(filename);
			file.delete();
		
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		
	}

	public void sortTable(String filename) throws IOException {
		// TODO Auto-generated method stub
		String new_file1 = "D:\\weekday_sorted.csv";
		String new_file2 = "D:\\weekend_sorted.csv";
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filename),"GBK"));
		BufferedWriter bw1=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new_file1,true),"GBK"));
		BufferedWriter bw2=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new_file2,true),"GBK"));
		String temp="";
		br.readLine();
		Map<String, List<String>> weekday_result = new HashMap<>();
		Map<String, List<String>> weekend_result = new HashMap<>();
		String weekday_tripnumber = "";
		String weekend_tripnumber = "";
		ArrayList<String> id = new ArrayList<String>();
		ArrayList<String> id2 = new ArrayList<String>();
		while((temp=br.readLine())!=null){
			String[] string = temp.split(",");	 		
			if(string[2].equals("平日")) {
				weekday_tripnumber = string[5];
				if (!id.contains(weekday_tripnumber)) {
					ArrayList<String> value = new ArrayList<>();
					id.add(weekday_tripnumber);
					value.add(temp);
					weekday_result.put(weekday_tripnumber, value);
					
				}else {
					List<String> value = weekday_result.get(weekday_tripnumber);
					value.add(temp);
					weekday_result.put(weekday_tripnumber,value);
				}
			}else {
				weekend_tripnumber = string[5];
				if (!id2.contains(weekend_tripnumber)) {
					ArrayList<String> value = new ArrayList<>();
					id2.add(weekend_tripnumber);
					value.add(temp);
					weekend_result.put(weekend_tripnumber, value);
					
				}else {
					List<String> value = weekend_result.get(weekend_tripnumber);
					value.add(temp);
					weekend_result.put(weekend_tripnumber,value);
				}
			}
		}
		br.close();	
		java.util.Iterator<Entry<String, List<String>>> entries = weekday_result.entrySet().iterator(); 
		while (entries.hasNext()) { 
			  Entry<String, List<String>> entry = entries.next();
			  List<String> sorted_result = entry.getValue();
			  int len =sorted_result.size();
			  int minIndex =0;
			  String tempm = "";
			  for (int  i = 0; i < len - 1; i++) {
			        minIndex = i;               
			        for (int j = i + 1; j < len; j++) {
			        	int time2 = CommonTools.TransferTime(sorted_result.get(j).split(",")[1]);
			        	int time1 = CommonTools.TransferTime(sorted_result.get(minIndex).split(",")[1]);
			            if (time2 < time1) {     
			                minIndex = j;        
			            }
			        }			        
			        tempm = sorted_result.get(i);  
			        sorted_result.set(i, sorted_result.get(minIndex));
			        sorted_result.set(minIndex,tempm);
			   }
			  
			   for(String sb : sorted_result) {
			    	bw1.write(sb+"\n");
			   }		   
			}
		bw1.close();
		
		//ArrayList<String> sorted_result2 = new ArrayList<String>();		
		java.util.Iterator<Entry<String, List<String>>> entries2 = weekend_result.entrySet().iterator(); 
		while (entries2.hasNext()) { 			
			  Entry<String, List<String>> entry = entries2.next(); 
			  List<String> sorted_result2 = entry.getValue();
			  int len =sorted_result2.size();
			  int minIndex =0;
			  String tempm = "";
			  for (int  i = 0; i < len - 1; i++) {
			        minIndex = i;               
			        for (int j = i + 1; j < len; j++) {
			        	int time1 = CommonTools.TransferTime(sorted_result2.get(j).split(",")[1]);
			        	int time2 = CommonTools.TransferTime(sorted_result2.get(minIndex).split(",")[1]);
			            if (time1 < time2) {     
			                minIndex = j;        
			            }
			        }			        
			        tempm = sorted_result2.get(i);  
			        sorted_result2.set(i, sorted_result2.get(minIndex));
			        sorted_result2.set(minIndex, tempm);
			   }
			  
			   for(String sb : sorted_result2) {
			    	bw2.write(sb+"\n");
			   }		   
			}
		bw2.close();
		}

}
