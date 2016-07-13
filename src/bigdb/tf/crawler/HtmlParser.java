package bigdb.tf.crawler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class HtmlParser
{
	private String html;
	Map<String, String> fixedParameters = new HashMap<String, String>();

	public HtmlParser(String html)
	{
		this.html = html;
	}
	
	public void setFixedParameter(String name, String value){
		fixedParameters.put(name, value);
	}

	public List<NameValuePair> parse()
	{
		Document doc = Jsoup.parse(html);

		List<NameValuePair> params = new ArrayList<NameValuePair>();

		//Element form = doc.getElementById("prodViewForm");
		Element form = doc.select("#prodViewForm").first();
		params.addAll(parseField(form));
		params.addAll(parseCheckbox(form));
		params.addAll(parseSelectTag(form));

		for(Map.Entry<String, String> entry: fixedParameters.entrySet())
		{
			params.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
		}
		
		for (NameValuePair nv : params)
		{
			System.out.println(String.format("%s=%s", nv.getName(), nv.getValue()));
		}
		return params;
	}
	
	private static boolean VERBOSE = false;
	
	List<NameValuePair> parseField(Element parent)
	{
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		for( Element e : parent.select("input[type=hidden]"))
		{
			String name = e.attr("name");
			String value = e.attr("value");
			if( VERBOSE && StringUtils.isEmpty(name))
			{
				System.out.println("E = " + e);
			}
			
			if( StringUtils.isNotBlank(name) && StringUtils.isNotBlank(value))
			{
				if( ! isFixedParam(name))
					params.add(new BasicNameValuePair(name,value));				
			}
		}
		
		for( Element e : parent.select("input[type=text]"))
		{
			String name = e.attr("name");
			String value = e.attr("value");
			if( VERBOSE && StringUtils.isEmpty(name))
			{
				System.out.println("E = " + e);
			}
			
			if( StringUtils.isNotBlank(name) && StringUtils.isNotBlank(value))
			{
				if( ! isFixedParam(name))
					params.add(new BasicNameValuePair(name,value));				
			}
		}
		return params;
	}
	
	
	private boolean isFixedParam(String name)
	{
		return fixedParameters.containsKey(name);
	}

	List<NameValuePair> parseCheckbox(Element parent)
	{
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		for( Element e : parent.select("input[checked=checked]"))
		{
			String name = e.attr("name");
			String value = e.attr("value");
			if( VERBOSE &&  StringUtils.isEmpty(name))
			{
				System.out.println("E = " + e);
			}
			if( StringUtils.isNotBlank(name) && StringUtils.isNotBlank(value))
			{
				if( ! isFixedParam(name))
					params.add(new BasicNameValuePair(name,value));				
			}
		}
		return params;
	}

	List<NameValuePair> parseSelectTag(Element parent)
	{
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		for (Element e : parent.select("select"))
		{
			String name = e.attr("name");
			String value = "";
			if( VERBOSE && StringUtils.isEmpty(name))
			{
				System.out.println("E = " + e);
			}

			Elements options = e.select("option[selected=selected]");
			if (options != null && options.size() > 0)
			{
				value = options.first().attr("value");
			}

			if (StringUtils.isNotBlank(name) && StringUtils.isNotBlank(value))
			{
				params.add(new BasicNameValuePair(name, value));
			}
		}
		return params;
	}
}
