package gov.bjjtw.lastTrain.Preprocess;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class StationAdjStation {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		StationAdjStation s=new StationAdjStation();
		s.AdjStation();
	}

	//找出所有站的邻接站点acc编码对，不包括同站的换乘站
	public void AdjStation() {
	  	String temp="",str[],line="",acccode="";
	  	try {
			BufferedReader br=new BufferedReader(new FileReader(new File("D:\\末班车\\available\\StationTransAccCode_new.csv")));
			BufferedWriter bw=new BufferedWriter(new FileWriter(new File("D:\\末班车\\available\\StationAdjStation.csv"),true));
			temp=br.readLine();
			str=temp.split(",");
			line=str[1];
			acccode=str[2];
			while((temp=br.readLine())!=null) {
				str=temp.split(",");
				if(str[1].equals("150995474")) {
					bw.write("150995474,150995473\n");
					bw.write("150995473,150995474\n");
				}
				if(str[1].equals("151018007")) {
					bw.write("151018007,151018009\n");
					bw.write("151018009,151018007\n");
				}
				if(str[1].equals(line)) {
					bw.write(acccode+","+str[2]+"\n");
					bw.write(str[2]+","+acccode+"\n");
				}
				line=str[1];
				acccode=str[2];
			}
			br.close();
			bw.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

}
