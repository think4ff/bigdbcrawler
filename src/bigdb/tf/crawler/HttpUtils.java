package bigdb.tf.crawler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

public class HttpUtils
{
	@SuppressWarnings("rawtypes")
	private static String _paramObjectToString(Object object)
	{
		if (object == null)
			return "";
		if (object instanceof String)
			return (String) object;
		if (object instanceof Collection)
		{
			Collection list = (Collection) object;
			return StringUtils.collectionToCommaDelimitedString(list);
		}
		else if (object instanceof Object[])
		{
			return StringUtils.arrayToCommaDelimitedString((Object[]) object);
		}
		return object.toString();
	}

	public static String makeGetUrl(String urlString,
			Map<String, Object> params) throws MalformedURLException,
					UnsupportedEncodingException
	{
		if (params != null && params.size() > 0)
		{

			StringBuilder paramBuffer = new StringBuilder();
			Set<String> paramKeys = params.keySet();
			for (String key : paramKeys)
			{
				String val = _paramObjectToString(params.get(key));
				key = URLEncoder.encode(key, "UTF-8");
				val = URLEncoder.encode(val, "UTF-8");
				paramBuffer.append("&" + key + "=" + val);
			}
			String prefix = "";
			if (urlString.indexOf("?") < 0)
			{
				prefix = "?";
			}
			urlString = String.format("%s%s%s", urlString, prefix, paramBuffer);
		}
		System.out.println("urlString::" + urlString);
		
		return urlString;
	}

	public static String makeGetUrl2(String urlString,
			Map<String, String> params) throws MalformedURLException,
					UnsupportedEncodingException
	{
		if (params != null && params.size() > 0)
		{

			StringBuilder paramBuffer = new StringBuilder();
			Set<String> paramKeys = params.keySet();
			for (String key : paramKeys)
			{
				String val = _paramObjectToString(params.get(key));
				key = URLEncoder.encode(key, "UTF-8");
				val = URLEncoder.encode(val, "UTF-8");
				paramBuffer.append("&" + key + "=" + val);
			}
			String prefix = "";
			if (urlString.indexOf("?") < 0)
			{
				prefix = "?";
			}
			urlString = String.format("%s%s%s", urlString, prefix, paramBuffer);
		}

		return urlString;
	}

	public static String get(String url) throws ClientProtocolException,
			IOException
	{
		HttpGet method = new HttpGet(url);
		HttpResponse response = executeMethod(method);
		return getResponseAsString(response);
	}

	public static String get(String url, Map<String, Object> params)
			throws ClientProtocolException, IOException
	{
		return get(makeGetUrl(url, params));
	}

	public static String post(String url, Map<String, String> params)
			throws ClientProtocolException, IOException
	{
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(params.size());
		Set<String> paramKeys = params.keySet();
		for (String key : paramKeys)
		{
			nameValuePairs.add(new BasicNameValuePair(key, params.get(key)));
		}

		return post(url, nameValuePairs);
	}
	
	public static String post(String url, List<NameValuePair> nameValuePairs)
			throws ClientProtocolException, IOException
	{
		HttpPost method = new HttpPost(url);
		method.setEntity(new UrlEncodedFormEntity(nameValuePairs, Charset.forName("UTF-8")));
		HttpResponse response = executeMethod(method);
		return getResponseAsString(response);
	}

	private static HttpResponse executeMethod(HttpRequestBase method)
			throws ClientProtocolException, IOException
	{
		HttpResponse response = null;
		HttpClient client = new DefaultHttpClient();
		response = client.execute(method);
		return response;
	}

	private static String getResponseAsString(HttpResponse response)
			throws IllegalStateException, IOException
	{
		String content = null;
		InputStream stream = null;
		try
		{
			if (response != null)
			{
				stream = response.getEntity().getContent();
				InputStreamReader reader = new InputStreamReader(stream);
				BufferedReader buffer = new BufferedReader(reader);
				StringBuilder sb = new StringBuilder();
				String cur;
				while ((cur = buffer.readLine()) != null)
				{
					sb.append(cur + "\n");
				}
				content = sb.toString();
			}
		}
		finally
		{
			if (stream != null)
			{
				stream.close();
			}
		}
		return content;
	}
}
