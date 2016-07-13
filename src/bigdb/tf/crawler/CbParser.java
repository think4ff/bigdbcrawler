package bigdb.tf.crawler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class CbParser
{
	private String html;
	private Map<String, String> fixedParameters = new HashMap<String, String>();

	private String title;
	private List<String> datas = new ArrayList<String>();
	
	public String getTitle()
	{
		return title;
	}
	
	public List<String> getDatas()
	{
		return datas;
	}
	
	public CbParser(String html)
	{
		this.html = html;
	}
	
	public void setFixedParameter(String name, String value){
		fixedParameters.put(name, value);
	}

	public int parse(String titleSelector, String dataSelector)
	{
		Document doc = Jsoup.parse(html);

		// html title
		this.title = parseTitlePart(doc, titleSelector);

		// html data
		this.datas = parseTablePart(doc, dataSelector);
		
		// html data
//		this.datas = parseDataPart(doc, dataSelector);
		
		// return data count
		return datas.size();
	}
	
	private String parseTitlePart(Document doc, String titleSelector)
	{
		 Elements elements = doc.select(titleSelector);
		 if( elements == null || elements.size() == 0)
			 return "";
		 return elements.first().text();
	}

//	private List<String> parseDataPart(Document doc, String dataSelector)
//	{
//		List<String> params = new ArrayList<String>();
//		for( Element e : doc.select(dataSelector) )
//		{
//			String value = e.text();
//			if( StringUtils.isNotBlank(value))
//			{
//				params.add(value);
//			}
//		}
//		return params;
//	}
	
	
	private List<String> parseTablePart(Document doc, String dataSelector)
	{
		List<String> params = new ArrayList<String>();
		
		Element table = doc.select(dataSelector).get(0);
		Elements rows = table.select("tr");
		
		for(int i = 0; i < rows.size(); i++)
		{
			if(i%2 == 0) continue; //날짜컬럼 제외.
			
			Element  row  = rows.get(i);
			for( Element e : row.select("td") )
			{
				if(e.text().length() == 1) continue; //(날짜)공백컬럼 제거
				params.add(e.text()); //모든 월의 시작일은 1일.
			}
		}
		return params;
	}
}
