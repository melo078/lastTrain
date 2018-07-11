package gov.bjjtw.lastTrain.Preprocess;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class Operation{
	private String filepath;
	public String[][] table_data = null;
	int width = 0;
	int height = 0;

	public  Operation(String filepath,int width,int height){
		this.filepath=filepath;
		this.width = width;
		this.height = height;
	}

	public  void readCsv(){	
		try {
			BufferedReader br=new BufferedReader(new FileReader(new File(filepath)));
			String str=null;
			String temp[]=null;
			table_data = new String[this.width][this.height];
			//table_data =new String[380][21];
			int i=0;
			while((str=br.readLine())!=null) {
				// 字符串为单位的二维数组
				temp=str.split(",");
				int j=0;
				while(j<temp.length){
					table_data[i][j]=temp[j];
					j++;
				}
				i++;
			}
			br.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

}
