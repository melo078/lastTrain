package gov.bjjtw.lastTrain.Preprocess;

import java.io.*;


//import org.apache.poi.util.SystemOutLogger;

public class NonTransAdjTrans {
	static String nonTransAdjTrans = "";
	static String transAdjTrans = "";

	public static void main(String args[])throws IOException{
		//提取任意一个非换乘站对应的最近换乘站编码及换乘站对应的其他线路换乘站编码
		//将结果保存在NonTrans_Adj_Trans.csv
		NonTransAdjTrans adjoin=new NonTransAdjTrans();
		adjoin.NonTransAdjTrans1();
	}

	public void NonTransAdjTrans1() throws IOException {
		// TODO Auto-generated method stub
		String output_NonTransstationAdjStation="D:\\末班车\\available\\NonTrans_Adj_Trans.csv";
		String output_TransStationAdjStation="D:\\末班车\\available\\Trans_Adj_Trans.csv";
		Operation op=new Operation("D:\\末班车\\URTNet_Station.csv",380,6);
		op.readCsv();
		Operation op2=new Operation("D:\\末班车\\URTNet_TransferLinks_time.csv",129,8);
		op2.readCsv();
		int row_length=op.table_data.length;
		int column_length=6;
		String str1="",str2="";
		int row2_length=op2.table_data.length;
		
		for(int i=1;i<row2_length;i++) {
			//true标识可换乘线路路径
			if(op2.table_data[i][1]!=null&&op2.table_data[i][1].equals("TRUE")) {
				//找换乘站的换乘站点,1标识换乘站
				str1+=op2.table_data[i][3]+","+op2.table_data[i][5]+","+op2.table_data[i][7]+"\n";
			}	
		}
			
		for(int i=0;i<row_length;i++){
			if(op.table_data[i][3]!=null&&op.table_data[i][3].equals("中间站")){
				int j=i;
				//比较线路数是否相同，如果相同，就看是不是换乘站；
				while(op.table_data[j][2]!=null&&op.table_data[j][2].equals(op.table_data[i][2])){
					if(op.table_data[j][3].equals("换乘站")){
						op.table_data[i][4]=op.table_data[j][0];
						break;
					}
					if(j>-1){
						j--;
					}
					if(j==-1){
						break;
					}
				}
				j=i;
				while(op.table_data[j][2]!=null&&op.table_data[j][2].equals(op.table_data[i][2])){
					if(op.table_data[j][3]!=null&&op.table_data[j][3].equals("换乘站")){
						op.table_data[i][5]=op.table_data[j][0];
						break;
					}
					j++;		
				}
			} else if(op.table_data[i][3]!=null&&op.table_data[i][3].equals("始发终到站")) {
				int j=i;
				//比较线路数是否相同，如果相同，就看是不是换乘站；
				while(op.table_data[j][2]!=null&&op.table_data[j][2].equals(op.table_data[i][2])) {
					if(op.table_data[j][3].equals("换乘站")){
						op.table_data[i][4]=op.table_data[j][0];
						break;
					}
					if(j>-1){
						j--;
					}
					if(j==-1){
						break;
					}
				}
				j=i;
				while(op.table_data[j][2]!=null&&op.table_data[j][2].equals(op.table_data[i][2])){
					if(op.table_data[j][3]!=null&&op.table_data[j][3].equals("换乘站")){
						op.table_data[i][5]=op.table_data[j][0];
						break;
					}
					j++;		
				}
			} else{
				op.table_data[i][4]="换乘站";
			}		
		}
		
		try {
			BufferedWriter bw=new BufferedWriter(new FileWriter(new File(output_NonTransstationAdjStation)));
			BufferedWriter bw1=new BufferedWriter(new FileWriter(new File(output_TransStationAdjStation)));
			for(int i=0;i<row_length;i++){
				if(op.table_data[i][3]!=null&&!op.table_data[i][3].equals("换乘站")) {
					for(int j=0;j<column_length;j++){ 
						if(op.table_data[i][j]!=null&&op.table_data[i][j].contains("S")) { 
							str2+=op.table_data[i][j]+",";
							System.out.print(op.table_data[i][j]+","); 
						} 
					}
					str2+="\n";
					System.out.println(); 
				} 
			}
			bw.write(str2);
			bw1.write(str1);
			bw.close();
			bw1.close(); 
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
}
