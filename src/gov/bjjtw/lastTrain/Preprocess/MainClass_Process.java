package gov.bjjtw.lastTrain.Preprocess;

public class MainClass_Process {

	public static void main(String[] args) throws Exception { 
		String inputFile_transferLinks="D:\\末班车\\URTNet_TransferLinks_time.csv";
		String adjecent_TransStation="D:\\末班车\\Adjecent_TransStation.csv";
		String input_Station_name="D:\\末班车\\station.csv";
		String outputFile_Adjecent_TransStation_ID="D:\\末班车\\Adjecent_TransStation_ID.csv";
		
		String input_URTNetStation="D:\\末班车\\URTNet_Station.csv";
		String output_URTNetStation="D:\\末班车\\NonTrans_Adj_Trans.csv";
		String filename="D:\\TimeTables0627.csv";
		String weekday = "D:\\weekday_sorted.csv";
		String weekend = "D:\\weekend_sorted.csv";
//		TransStation transStation=new TransStation();
//		transStation.Adjecent_Station(inputFile_transferLinks, adjecent_TransStation,outputFile_Adjecent_TransStation_ID);//计算每个地铁站的邻接换乘地铁站并且存储到Adjecent_TransStation.csv中
//		
// 已经生成最终版保存在Adjecent_TransStation.csv,无需更改
//		AccCodeTransfer accCodeTransfer=new AccCodeTransfer();
//		accCodeTransfer.AccCodeTransfer(inputFile_transferLinks,input_Station_name,adjecent_TransStation);
		
		DivideTimeTable timeTables=new DivideTimeTable();
		//已经生成两个平日和双休保存在timetables中，无需更改
		 long time=System.currentTimeMillis();
		 timeTables.sortTable(filename);
		 long consume = System.currentTimeMillis()-time;
		 System.out.println(consume);
		 timeTables.ReadTimeTable(weekday);
		 timeTables.ReadTimeTable(weekend);


	    }
	


}
