package gov.bjjtw.lastTrain.Graph;

import java.util.Map;

public interface Algorithm {
	/**
	 * ִ���㷨
	 */
	void perform(Graph g, String sourceVertex,String date,String time,String end_vertex);
	/**
	 * �õ�·��
	 */
	Map<String, String> getPath();
}

