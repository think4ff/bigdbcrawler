package bigdb.tf.crawler;

import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

public class CbResult
{
	private String			title;
	private List<String>	datas;
	private String          location;

	public CbResult(String title, List<String> datas)
	{
		this.title = title;
		this.datas = datas;
		if (datas == null)
			datas = Collections.emptyList();
	}

	public String getTitle()
	{
		return title;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

	public List<String> getDatas()
	{
		return datas;
	}

	public void setDatas(List<String> datas)
	{
		this.datas = datas;
	}

	
	public String resultParser(String currDate, String inText)
	{
		String rsValue = "";
		
		String	averageTemper	= "";
		String  highTemper		= "";
		String	lowTemper		= "";
		String	averageCloud	= "";
		String	rainfall		= "";
		
		StringTokenizer tokens = new StringTokenizer( inText, ":" );
		for( int dLoop = 1; tokens.hasMoreElements(); dLoop++ )
		{
			rsValue = tokens.nextToken().trim();
			if(dLoop == 2) averageTemper = rsValue.replaceAll("[^0-9/./-]", "");//"숫자.-"만 남김
			if(dLoop == 3) highTemper    = rsValue.replaceAll("[^0-9/./-]", "");
			if(dLoop == 4) lowTemper     = rsValue.replaceAll("[^0-9/./-]", "");
			if(dLoop == 5) averageCloud  = rsValue.replaceAll("[^0-9/./-]", "");
			if(dLoop == 6) rainfall      = rsValue.replaceAll("[^0-9/./-]", "");
		}
		
		if(averageCloud.length() == 0 || "-".equals(averageCloud))
			averageCloud = "0";
		if(rainfall.length() == 0 || "-".equals(rainfall))
			rainfall = "0.0";
		
		float avgCloud = Float.parseFloat(averageCloud);
		float dayRainy = Float.parseFloat(rainfall);
		String weather = "";
		
		if(avgCloud >= 0 && 2 >= avgCloud)
			weather = "맑음";
		if(avgCloud > 2 && 6 > avgCloud)
			weather = "구름조금";
		if(avgCloud >= 6 && 9 > avgCloud)
			weather = "구름많음";
		if(avgCloud >= 9 && 10 >= avgCloud)
			weather = "흐림";
		if(dayRainy > 0.5)
			weather = "비";
		if(dayRainy >= 80)
			weather = "폭우";
		
		String rowData = String.format("%s,%s,%s,%s,%s,%s,%s,%s", this.location, currDate, averageTemper, highTemper
														  , lowTemper, averageCloud, rainfall, weather );
		return rowData;
	}

	public String getLocation() {
		return this.location;
	}
	public void setLocation(String location) {
		if("184".equals(location))
			this.location = "184,제주";
		if("185".equals(location))
			this.location = "185,고산";
		if("188".equals(location))
			this.location = "188,성산";
		if("189".equals(location))
			this.location = "189,서귀포";
	}
}
