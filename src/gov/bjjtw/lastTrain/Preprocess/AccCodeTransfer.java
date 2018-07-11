package gov.bjjtw.lastTrain.Preprocess;

import java.util.HashMap;

public class AccCodeTransfer {

	public void AccCodeTransfer(String inputFile_transferLinks,String input_Station_name,String inputFile_Adjecent_TransStation) {  
		Operation[] operation= new Operation[3];
		operation[0]=new Operation(inputFile_transferLinks,380,21);
		operation[1]=new Operation(input_Station_name,380,21);
		operation[2]=new Operation(inputFile_Adjecent_TransStation,380,21);
		
		operation[0].readCsv();
		operation[1].readCsv();
		operation[2].readCsv();

		for(int i=0;i<operation[2].table_data.length;i++){
			for(int j=1;j<operation[2].table_data[i].length;j++){
				for(int k=0;k<operation[0].table_data.length;k++){
					if(operation[2].table_data[i][j]==null) {
						break;
					}
					if(operation[2].table_data[i][j].equals(operation[0].table_data[k][3])){
						operation[2].table_data[i][j]=operation[0].table_data[k][2]+","+operation[0].table_data[k][4];
								
					}			
				}
				for(int k=0;k<operation[0].table_data.length;k++){
					if(operation[2].table_data[i][j]==null) {
						break;
					}
					if(operation[2].table_data[i][j].equals(operation[0].table_data[k][5])){
						operation[2].table_data[i][j]=operation[0].table_data[k][2]+","+operation[0].table_data[k][6];
					}			
				}
			}
		}

		//把8号线转换成80，把13号线转换成130
		for(int i=0;i<operation[2].table_data.length;i++){
			for(int j=1;j<operation[2].table_data[i].length;j++){
				if(operation[2].table_data[i][j]==null){
					break;
				}
				String[] temp=operation[2].table_data[i][j].split(",");
				if((temp[0]==null)||(temp[1]==null)){
					break;
				}
				for(int k=0;k<operation[1].table_data.length;k++){
					HashMap<String,String> map=new HashMap<String,String>();
					map.put("房山线", "L091");
					map.put("八通线", "L011");
					map.put("机场线", "L021");
					map.put("亦庄线", "L051");
					map.put("昌平线", "L131");
					map.put("燕房线", "L092");
					map.put("西郊线", "L101");
					map.put("1号线", "L010");
					map.put("2号线", "L020");
					map.put("4号线", "L040");
					map.put("5号线", "L050");
					map.put("6号线", "L060");
					map.put("7号线", "L070");
					map.put("8号线", "L080");
					map.put("9号线", "L090");
					map.put("10号线", "L100");
					map.put("13号线", "L130");
					map.put("14号线", "L140");
					map.put("15号线", "L150");
					map.put("16号线", "L160");

					String ab=(String) map.get(temp[1]);
					if(ab==null){
						break;
					}
					if(temp[0].equals(operation[1].table_data[k][1])&&ab.equals(operation[1].table_data[k][2])){
						operation[2].table_data[i][j]=operation[1].table_data[k][4];
					}
				}
			}
		}

		for(int i=0;i<operation[2].table_data.length;i++){
			for(int j=0;j<operation[2].table_data[i].length;j++){
				if(operation[2].table_data[i][j]==null){
					break;
				}
				System.out.print(operation[2].table_data[i][j]+"\t");
			}
			System.out.println();
		}
	}

}




