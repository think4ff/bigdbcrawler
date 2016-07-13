package bigdb.tf.crawler;

import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

public class CbResult
{
	private String			title;
	private List<String>	datas;

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
			if(dLoop == 2) averageTemper = rsValue.replaceAll("[^0-9/./-]", "");//"¼ýÀÚ.-"¸¸ ³²±è
			if(dLoop == 3) highTemper    = rsValue.replaceAll("[^0-9/./-]", "");
			if(dLoop == 4) lowTemper     = rsValue.replaceAll("[^0-9/./-]", "");
			if(dLoop == 5) averageCloud  = rsValue.replaceAll("[^0-9/./-]", "");
			if(dLoop == 6) rainfall      = rsValue.replaceAll("[^0-9/./-]", "");
		}
		if(averageCloud.length() == 0 || "-".equals(averageCloud))
			averageCloud = "0";
		if(rainfall.length() == 0 || "-".equals(rainfall))
			rainfall = "0.0";
		
		String rowData = String.format("%s,%s,%s,%s,%s,%s", currDate, averageTemper, highTemper
														  , lowTemper, averageCloud, rainfall   );
		return rowData;
	}
}
