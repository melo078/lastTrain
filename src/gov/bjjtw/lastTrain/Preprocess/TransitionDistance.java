package gov.bjjtw.lastTrain.Preprocess;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;

public class TransitionDistance {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		String filepath_dis= "D:\\resources\\transfer_stop.csv";
		String filepath_acc="D:\\resources\\acc_new.csv";
		FileOutputStream out= null;
        OutputStreamWriter osw = null;
        BufferedWriter bfw= null;
        String file = "D:\\resources\\transdist.csv";
		out = new FileOutputStream(file);
        osw = new OutputStreamWriter(out, "gbk");
        bfw = new BufferedWriter(osw);
        
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filepath_dis), "GBK"));
        String temp="",stationnames1="",str[],startline="",endline="",costtime="",distance="";
		String temp2="",stationnames2="",acc="",line="",str2[];	
		Map<String, String> nametoacc = new HashMap<>();
		temp=br.readLine();
		BufferedReader br_new = new BufferedReader(new InputStreamReader(new FileInputStream(filepath_acc), "GBK"));
	    while ((temp2=br_new.readLine())!=null) {
	    	str2=temp2.split(",");
    		acc=str2[0];
    		stationnames2=str2[1];
	    	line=str2[2];
	    	nametoacc.put(stationnames2+line, acc);			
		}

	    br_new.close();bfw.append("初始acc,换乘acc,花费时间,距离").append("\n");
		while((temp=br.readLine())!=null) {
	    	str=temp.split(",");
			stationnames1 = str[0];
			startline=str[1];
			endline=str[2];
			costtime=str[3];
			distance=str[4];
			String from=nametoacc.get(stationnames1+startline);
			String to = nametoacc.get(stationnames1+endline);
			bfw.append(from+","+to+","+costtime+","+distance).append("\n");
	    }
	    br.close();
	    bfw.flush();
	    bfw.close();
	}
}
