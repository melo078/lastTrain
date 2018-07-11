package gov.bjjtw.lastTrain.CommonTools;


import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import static java.lang.Math.toRadians;

public class CommonTools {
    public static int TransferTime(String time) {
    	String str[]=time.split(":");
    	int h=Integer.parseInt(str[0]);
    	if(h==0) {
    	       h=24;
    	}
    	int m=Integer.parseInt(str[1]);
    	int s=Integer.parseInt(str[2]);    		
        return h*3600+m*60+s;
    }

    public static String SecondToTime(int second) {
        int h=second/3600;
        int m=(second-h*3600)/60;
        int s=(second-h*3600-m*60);
        String hours,minutes,seconds;
        if (h<10) {
            hours = "0"+h;
        } else {
            hours = String.valueOf(h);
        }
        if (m<10){
            minutes = "0"+m;
        } else{
            minutes = String.valueOf(m);
        }
        if (s<10){
            seconds = "0"+s;
        } else {
            seconds = String.valueOf(s);
        }
        return hours+":"+minutes+":"+seconds;
    }

    public static Object DeepCopy(Object orig) {
        Object obj = null;
        try {
            // Write the object out to a byte array
            FastByteArrayOutputStream fbos = new FastByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(fbos);
            out.writeObject(orig);
            out.flush();
            out.close();

            // Retrieve an input stream from the byte array and read
            // a copy of the object back in.
            ObjectInputStream in = new ObjectInputStream(fbos.getInputStream());
            obj = in.readObject();
        } catch(IOException e) {
            e.printStackTrace();
        } catch(ClassNotFoundException cnfe) {
            cnfe.printStackTrace();
        }
        return obj;
    }

    public static double SimpleDist(double lat1, double lng1, double lat2, double lng2) {
        // 经度差值
        double dx = lng1 - lng2;
        // 纬度差值
        double dy = lat1 - lat2;
        // 平均纬度
        double b = (lat1 + lat2) / 2.0;
        // 东西距离
        double Lx = toRadians(dx) * 6367000.0* Math.cos(toRadians(b));
        // 南北距离
        double Ly = 6367000.0 * toRadians(dy);
        // 用平面的矩形对角距离公式计算总距离
        return Math.sqrt(Lx * Lx + Ly * Ly);
    }

    public static boolean isWeekend(String dateString) {
        try{
        	ArrayList<String> holidays = new ArrayList<String>();
        	holidays.add("2018-09-24");
        	holidays.add("2018-10-01");
        	holidays.add("2018-10-02");
        	holidays.add("2018-10-03");
        	holidays.add("2018-10-04");
        	holidays.add("2018-10-05");
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date date = format.parse(dateString);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            if(holidays.contains(dateString)||cal.get(Calendar.DAY_OF_WEEK)==Calendar.SATURDAY||cal.get(Calendar.DAY_OF_WEEK)==Calendar.SUNDAY) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            return false;
        }
    }
}
