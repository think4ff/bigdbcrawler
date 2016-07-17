package bigdb.tf.crawler;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.message.BasicNameValuePair;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.TypeRef;
import com.jayway.jsonpath.spi.json.JacksonJsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;

public class Main {
	
	static Configuration conf() {
		Configuration conf = Configuration.builder().mappingProvider(new JacksonMappingProvider())
				.jsonProvider(new JacksonJsonProvider()).build();
		return conf;
	}
	
	static String propertyFile = "./weatherCrawler.property";
	static String outFileName; //= "./data/weatherOutput.csv";
	static String header = "location_code,location,date,averageTemper,highTemper,lowTemper,averageCloud,rainfall,weather";
//	static String header = "date_yyyyMMdd,평균기온_℃,최고기온_℃,최저기온_℃,평균운량,일강수량_mm,날씨";
	static String delim = " \r\n";
	static int startYear;
	static int startMonth;
	static int endYear;
	static int endMonth;

	public static void main(String[] args) throws Exception {
		String encoding    = "UTF-8";
		String location    = "";
		String startYYYYmm = "";
		String endYYYYmm   = "";
		
		FileInputStream ppFnm = new FileInputStream(propertyFile);
		Properties emvPp = new Properties();
		try {
			emvPp.load(ppFnm);
			outFileName = emvPp.getProperty("OUTPUT_FILE");
			encoding    = emvPp.getProperty("ENCODING_TYPE");
			location    = emvPp.getProperty("LOCATION");
			startYYYYmm = emvPp.getProperty("START_YYYYMM");
			endYYYYmm   = emvPp.getProperty("END_YYYYMM");
		} finally {
			closeQuietly(ppFnm); //close property file
		}
		
		StringBuilder sb = new StringBuilder();
		sb.append(outFileName).append("_").append(startYYYYmm).append("_").append(endYYYYmm).append(".csv");
		FileOutputStream outFile = new FileOutputStream(sb.toString());
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outFile, encoding));
		
		List<String> arrLoc = new ArrayList<String>();
		StringTokenizer tokens = new StringTokenizer( location, "," );
		
//		int i = 0;
		while(tokens.hasMoreElements())
		{
			arrLoc.add(tokens.nextToken().trim());
//			System.out.println("arrloc:" + arrLoc.get(i));
//			i++;
		}
		
		//cvs header write.
		try {
			writer.write(header + delim); 
		} catch (IOException e) {
			e.printStackTrace();
		} 
		//지역별 조회
		for(String pLocation : arrLoc) 
		{
			preProcess(pLocation, startYYYYmm, endYYYYmm, writer);
		}
		closeQuietly(writer); //close for writer
	}
	
	static void preProcess(String location, String startYYYYmm, String endYYYYmm, BufferedWriter writer) {
		//기상청 URL
		String url = "http://www.kma.go.kr/weather/climate/past_cal.jsp";
		
		int loopCnt = countMonth(startYYYYmm, endYYYYmm);		
		System.out.println("loopCnt::" + loopCnt);
		
		Map<String, Object> params = new HashMap<String, Object>();
//		params.put("stn", "235");// 지점(지역)
		params.put("stn", location);// 지점(지역)
		params.put("x", "23");   //?
		params.put("y", "9");    //?
		params.put("obs", "1");  // 요소(1:기온/강수량, 2:날씨)
//		params.put("yy", "2016");// 년도
//		params.put("mm", "07");  // 월
		
		List<String> resultCVS = new ArrayList<String>();
		
		int inYY = startYear, inMM = startMonth;
		for(int i=0; i <= loopCnt; i++) 
		{
//			System.out.println("inYY="+ inYY);
//			System.out.println("inMM=" + inMM);
			params.put("yy", Integer.toString(inYY));// 년도
			params.put("mm", Integer.toString(inMM));// 월			
			
			resultCVS = loadCrawlingHtml(url, location, params);
			writeFile(resultCVS, writer);
			
			if(inMM == 12)
				inMM = 1;
			else
				inMM += 1;
			if(endYear > inYY && inMM == 1)
				inYY +=  1; //1월이면 다음년도.
		}
	}

	static List<String> loadCrawlingHtml(String url, String location, Map<String, Object> params) {
		String responseHtml = "";
		List<String> rsCVS = new ArrayList<String>();
		
		try {
			responseHtml = HttpUtils.get(url, params);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		CbParser parser = new CbParser(responseHtml);
		parser.parse("p.table_topinfo", "table.table_develop tbody");

		List<String> results = new ArrayList<String>();
		for (String text : parser.getDatas()) {
			results.add(text);
		}

		CbResult rs = new CbResult(parser.getTitle(), results);
		//result에 지역 세팅
		rs.setLocation(location);
//		out.println("타이틀:" + rs.getTitle());
		String currYrMon = getYrMonth(rs.getTitle());
		String currDate = "";
		String currNumD  = "";
		int dayNum = 1;
		
		for (String text : rs.getDatas()) 
		{
			if(dayNum < 10)
				currNumD = "0" + Integer.toString(dayNum);
			else
				currNumD = Integer.toString(dayNum);

			dayNum++;
			
//			out.println(String.format("%s,%s", currYrMon + currNumD, text));
			currDate = currYrMon + currNumD;
			String rowData = rs.resultParser(currDate, text);

			rsCVS.add(rowData);
		}
		return rsCVS;
	}

	static int countMonth(String strtDate, String endDate) {
		startYear = Integer.parseInt(strtDate.substring(0,4));
		startMonth = Integer.parseInt(strtDate.substring(4,6));

		endYear = Integer.parseInt(endDate.substring(0,4));
		endMonth = Integer.parseInt(endDate.substring(4,6));

		int calMonth = (endYear - startYear)* 12 + (endMonth - startMonth);
		
		return calMonth;
	}
	
	static String getYrMonth(String inDate)	{
		String date = "";
		if(inDate.length() == 9)
			date = inDate.replaceAll(" ", "");
		else 
			date = inDate;
		
		String year  = date.substring(0,4).trim();
		String month = date.substring(5,7).trim();
		
		if(month.length() == 1)
			month = "0" + month;
		
		return year + month;
	}  
	
	private static void closeQuietly(FileInputStream fis) {
		try {
			if(fis != null)
				fis.close();
		} catch(Exception e) {
		}
	}
	
	private static void closeQuietly(BufferedWriter bfw) {
		try {
			if(bfw != null)
				bfw.close();
		} catch(Exception e) {
		}
		
	}
	
	static void writeFile(List<String> inDatas, BufferedWriter writer) {
		//current encoding type
//		String currEncoding = new java.io.OutputStreamWriter(System.out).getEncoding();
//		System.out.println("currEncoding:" + currEncoding);
		
		for(String rowData : inDatas) {
			try {
				writer.write(rowData + delim);
//				System.out.println(rowData);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
}
