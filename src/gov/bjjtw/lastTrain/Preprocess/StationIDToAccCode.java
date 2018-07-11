package gov.bjjtw.lastTrain.Preprocess;

import java.util.HashMap;
import java.util.Map;

public class StationIDToAccCode {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub

		//同一站不同线路的换乘站换乘邻接编码
		String sameTransStationAdj="D:\\末班车\\Trans_Adj_Trans.csv";
		//同一线上换乘站的邻接站编码
		String transStationAdjTransStation="D:\\末班车\\Adjecent_TransStation_ID.csv";
		//所有非换乘站邻接换乘站编码
		String nonTransStation="D:\\末班车\\available\\NonTrans_Adj_Trans.csv";
		String acccodeFile="D:\\末班车\\available\\StationTransAccCode.csv";
		Map<String,String> map=new HashMap<>();
		try {
//			String str[],temp;
//			BufferedReader br_acccode=new BufferedReader(new FileReader(new File(acccodeFile)));
//			while((temp=br_acccode.readLine())!=null)
//			{
//				str=temp.split(",");
//				map.put(str[0],str[2]);
//			}
//			br_acccode.close();
//			
//			BufferedReader br_1=new BufferedReader(new FileReader(new File(sameTransStationAdj)));
//			BufferedWriter bw_1=new BufferedWriter(new FileWriter(new File("D:\\末班车\\Trans_Adj_Trans_acc.csv"),true));
//			while((temp=br_1.readLine())!=null)
//			{
//				str=temp.split(",");
//				bw_1.write(map.get(str[0])+","+map.get(str[1])+","+str[2]+"\n");
//			}
//			bw_1.close();
//			br_1.close();
//			
//			BufferedReader br_2=new BufferedReader(new FileReader(new File(transStationAdjTransStation)));
//			BufferedWriter bw_2=new BufferedWriter(new FileWriter(new File("D:\\末班车\\Adjecent_TransStation_ID_acc.csv"),true));
//			while((temp=br_2.readLine())!=null)
//			{
//				str=temp.split(",");
//				if((map.get(str[0])!=null)&&(map.get(str[1])!=null))
//					bw_2.write(map.get(str[0])+","+map.get(str[1])+"\n");
//			}
//			bw_2.close();
//			br_2.close();
//			
//			BufferedReader br_3=new BufferedReader(new FileReader(new File(nonTransStation)));
//			BufferedWriter bw_3=new BufferedWriter(new FileWriter(new File("D:\\末班车\\NonTrans_Adj_Trans_acc.csv"),true));
//			while((temp=br_3.readLine())!=null)
//			{
//				str=temp.split(",");
//				if(str.length==2)
//				{
//					bw_3.write(map.get(str[0])+","+map.get(str[1])+"\n");
//				}
//				else
//				{
//					bw_3.write(map.get(str[0])+","+map.get(str[1])+","+map.get(str[2])+"\n");
//				}
//				
//			}
//			bw_3.close();
//			br_3.close();
			
			
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

}
