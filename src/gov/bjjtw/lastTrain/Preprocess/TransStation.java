package gov.bjjtw.lastTrain.Preprocess;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class TransStation {

	public void Adjecent_Station(String inputFile_transferLinks,String outputFile_Adjecent_TransStation,String outputFile_Adjecent_TransStation_ID) {
		try{
			BufferedReader br=new BufferedReader(new FileReader(new File(inputFile_transferLinks))); 
			BufferedWriter wr=new BufferedWriter(new FileWriter(new File(outputFile_Adjecent_TransStation)));
			BufferedWriter bw=new BufferedWriter(new FileWriter(new File(outputFile_Adjecent_TransStation_ID))); 
			String str_id="";
			String original_info[][]=new String[200][3];
			String trans_stations[][]=new String[200][100];
			for (int i=0;i<200;i++) {
				for (int j = 0; j < 3; j++) {
					original_info[i][j] = "0";
				}
			}
			for (int i=0;i<200;i++) {
				for (int j = 0; j < 100; j++) {
					trans_stations[i][j] = "0";
				}
			}
			String station_id[]=new String[200];
			String string=null;
			String str[]=null;
			br.readLine();
			String str3="";
			int i=0;
			for(int j=0;j<200;j++) {
				station_id[j]="0";
			}
			
			int line=0;
			int temp=0;
			while((string=br.readLine())!=null) {
				str=string.split(",");
				original_info[i][0]=str[2];
				original_info[i][1]=str[3];
				original_info[i][2]=str[5];
				int h=0;
				for(h=0;h<=temp;h++) {
					if(station_id[h].equals(str[3])) {
						break;
					}
				}

				if(h==(temp+1)) {
					station_id[temp]=str[3];
					temp++;
				}
				i++;
			}

			line=temp+1;
			for(int h=0;h<line;h++) {
				System.out.print(station_id[h]+",");
			}

			System.out.print("\n");

			for(i=0;i<line;i++) {
				int j;
				for(j=0;j<200;j++) {
					if(trans_stations[j][0].equals("0")) {
						trans_stations[j][0]=original_info[i][0];
						break;
					}
					String str1=trans_stations[j][0];
					String str2=original_info[i][0];
					
					if(str1.equals(str2)) {
						break;
					}
				}

				int position=0;

				for(int t=0;t<line;t++) {
					if(station_id[t].equals(original_info[i][1])) {
						position=t;
						break;
					}
				}

				//2号线是环线，加上末尾邻接换乘站
				if(station_id[position].equals("S02002")) {
					for(int k=1;k<20;k++) {
						if(trans_stations[j][k].equals("S02018")) {
							break;
						}
						if(trans_stations[j][k].equals("0")) {
							trans_stations[j][k]="S02018";
							str_id+="S02002,S02018\n";
							break;
						}
					}
				}

				//2号线是环线，加上首个邻接换乘站
				if(station_id[position].equals("S02018")) {
					for(int k=1;k<20;k++) {
						if(trans_stations[j][k].equals("S02002")) {
							break;
						}
						if(trans_stations[j][k].equals("0")) {
							trans_stations[j][k]="S02002";
							str_id+="S02018,S02002\n";
							break;
						}
					}
				}

				//10号线是环线，加上末尾邻接换乘站
				if(station_id[position].equals("S10003")) {
					for(int k=1;k<20;k++) {
						if(trans_stations[j][k].equals("S10045")) {
							break;
						}
						if(trans_stations[j][k].equals("0")) {
							trans_stations[j][k]="S10045";
							str_id+="S10003,S10045\n";
							break;
						}
					}
				}

				//10号线是环线，加上首个邻接换乘站
				if(station_id[position].equals("S10045")) {
					for(int k=1;k<20;k++) {
						if(trans_stations[j][k].equals("S10003")) {
							break;
						}
						if(trans_stations[j][k].equals("0")) {
							trans_stations[j][k]="S10003";
							str_id+="S10045,S10003"+"\n";
							break;
						}
					}
				}

				if(position>0&&position<line-1) {
					if(station_id[position-1].substring(0,4).equals(station_id[position].substring(0,4))) {
						for(int k=1;k<20;k++) {
							if(trans_stations[j][k].equals(station_id[position-1])) {
								break;
							}
							if(trans_stations[j][k].equals("0")) {
								trans_stations[j][k]=station_id[position-1];
								str_id+=station_id[position]+","+station_id[position-1]+"\n";
								break;
							}
						}
					}

					if(station_id[position+1].substring(0,4).equals(station_id[position].substring(0,4))) {
						for(int k=1;k<20;k++) {
							if(trans_stations[j][k].equals(station_id[position+1])) {
								break;
							}
							if(trans_stations[j][k].equals("0")) {
								trans_stations[j][k]=station_id[position+1];
								str_id+=station_id[position]+","+station_id[position+1]+"\n";
								break;
							}
						}
					}
				} else if(position==0) {
					if(station_id[position+1].substring(0,4).equals(station_id[position].substring(0,4))) {
						for(int k=1;k<20;k++) {
							if(trans_stations[j][k].equals(station_id[position+1])) {
								break;
							}
							if(trans_stations[j][k].equals("0")) {
								trans_stations[j][k]=station_id[position+1];
								str_id+=station_id[position]+","+station_id[position+1]+"\n";
								break;
							}
						}
					}
				} else {
					if (station_id[position-1].substring(0,4).equals(station_id[position].substring(0,4))) {
						for(int k=1;k<20;k++) {
							if(trans_stations[j][k].equals(station_id[position-1])) {
								break;
							}
							if(trans_stations[j][k].equals("0")) {
								trans_stations[j][k]=station_id[position-1];
								str_id+=station_id[position]+","+station_id[position-1]+"\n";
								break;
							}
						}
					}
				}
			}

			System.out.println(str_id);

			for(i=0;i<line;i++) {
				for(int j=0;j<20;j++) {
					if(trans_stations[i][j].equals("0")) {
						if(j!=0) {
							str3=str3+"\n";
						}
						break;
					} else {
						str3=str3+trans_stations[i][j]+",";
					}
				}
			}

			wr.write(str3);
			bw.write(str_id);
			wr.close();
			br.close();
			bw.close();
		} catch(Exception e){
			e.printStackTrace();
		}
	}

}
