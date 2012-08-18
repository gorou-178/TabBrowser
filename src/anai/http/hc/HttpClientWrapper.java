package anai.http.hc;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;
import org.apache.commons.lang.StringUtils;

import anai.http.ssl.EasySSLProtocolSocketFactory;

public class HttpClientWrapper {

	private final static String HTTP = "http";
	private final static String HTTPS = "https";
	private final static String DEFAULT_HTTP_PORT = "80";
	private final static String DEFAULT_HTTPS_PORT = "443";
	private final static int DEFAULT_HTTP_PORT_INT = 80;
	private final static int DEFAULT_HTTPS_PORT_INT = 443;

	private final static AuthScope ANY_SCOPE = new AuthScope( AuthScope.ANY_HOST, AuthScope.ANY_PORT, AuthScope.ANY_REALM, AuthScope.ANY_SCHEME );

	static{
		Protocol easyHttpsProtocol = new Protocol( HTTPS, (ProtocolSocketFactory)new EasySSLProtocolSocketFactory(), DEFAULT_HTTPS_PORT_INT );
		Protocol.registerProtocol( HTTPS, easyHttpsProtocol );
	}

	private HttpClientWrapper(){}

	public static String get( String url, String[] headers ){
		HttpMethod method = doMethod( url, headers, new MethodFactory.Get( url ) );
		if(method == null)
			return null;
		return responseString( getBytes( method ), headerMap( method ).get( "Content-Type" ) );
	}

	public static String post( String url, String[] headers, String[] postData, String encode ){
		HttpMethod method = doMethod( url, headers, new MethodFactory.Post( url, postData, encode ) );
		if(method == null)
			return null;
		return responseString( getBytes( method ), headerMap( method ).get( "Content-Type" ) );
	}
	
	public static byte[] getResponseToByte(String url, String[] headers){
		HttpMethod method = doMethod( url, headers, new MethodFactory.Get( url ) );
		if(method == null)
			return null;
		return getBytes(method);
	}
	
	public static InputStream getInputStream(String url, String[] headers){
		HttpMethod method = doMethod( url, headers, new MethodFactory.Get( url ) );
		if(method == null)
			return null;
		try {
			return method.getResponseBodyAsStream();
		} catch (IOException e) {
			return null;
		}
	}

	private static HttpMethod doMethod( String url, String [] headers, MethodFactory mc ){

		try {
			URI uri = new URI( url, false );
			String host = uri.getHost();
			int port = uri.getPort();
			//System.out.println( host + ": " + port );

			HttpClient client = new HttpClient();
			HttpMethod method = mc.create ();
			if( headers != null ){
				for( int j = 0, jn = headers.length; j + 1 < jn; j += 2 ){
					method.setRequestHeader( headers[j], headers[j + 1] );
				}
			}

			String basicUserId = "";
			String basicUserPass = "";
			if( ! StringUtils.isEmpty( basicUserId ) && ! StringUtils.isEmpty( basicUserPass ) ){
				client.getState().setCredentials( ANY_SCOPE, new UsernamePasswordCredentials( basicUserId, basicUserPass ) );
			}

			HostConfiguration proxy = null;//new HostConfiguration ();
			if( proxy != null ){

				String proxyHost = "";
				int proxyPort = DEFAULT_HTTP_PORT_INT;

				String proxyUserName = "";
				String proxyUserPass = "";
				String baseEncode = "";

				proxy.setProxy( proxyHost, proxyPort );
				proxy.setHost(uri);
				if( ! StringUtils.EMPTY.equals( proxyUserName ) || ! StringUtils.EMPTY.equals( proxyUserPass ) ){
					method.setRequestHeader ("Proxy-Authorization", "Basic " + baseEncode );
				}
				// コネクションタイムアウト設定(接続完了までのタイムアウト設定)
				client.getHttpConnectionManager().getParams().setConnectionTimeout(5000);
				// ソケットタイムアウト設定(接続してからレスポンスが返るまでのタイムアウト設定)
				client.getParams().setSoTimeout(5000);
				client.executeMethod( proxy, method );
			}
			else{
				client.executeMethod( method );
			}
			
			return method;

		} catch (URIException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		} catch (HttpException e) {
			e.printStackTrace();
		} catch (ConnectException e){
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	private static byte [] getBytes( HttpMethod method ){

		InputStream is = null;
		try {
			//System.out.println( "statusCode: " + method.getStatusCode() );
			if( method.getStatusCode() == 200 ){
				is = method.getResponseBodyAsStream();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		if( is == null )
			return new byte [0];
		
		// byte配列に書きこむ
		ByteArrayOutputStream baos = null;
		try {
			baos = new ByteArrayOutputStream();
			byte[] buffer = new byte[4096];
			int bytesRead = -1;
			while((bytesRead = is.read(buffer)) != -1) {
				baos.write(buffer, 0, bytesRead);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if(baos == null){
			return null;
		}
		
		final byte [] rawbytes = baos.toByteArray();
		if( rawbytes == null )
			return new byte [0];

		return rawbytes;
	}

	private static Map<String,String> headerMap( HttpMethod method ){
		final Map<String,String> m = new LinkedHashMap<String,String> ();
		final Header [] headers = method.getResponseHeaders ();
		if (headers == null)
			return m;
		for (final Header h : headers)
		{
			if (h == null)
				continue;
			m.put (h.getName (), h.getValue ());
		}
		return m;
	}

	private static String responseString( byte[] response, String contentType ){
		if( response != null && response.length > 0 ){
			String result = null;
			Pattern contentTypePt = Pattern.compile(".*charset=(.*)");
			Matcher matcher = contentTypePt.matcher(contentType);
			if(matcher.find()){
				String charset = matcher.group(1);
				try {
					result = new String(response, charset);
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
					try{ result = new String(response, "JISAutoDetect"); } catch (UnsupportedEncodingException e1) {}
				}
			}
			else{
				try{ result = new String(response, "JISAutoDetect"); } catch (UnsupportedEncodingException e1) {}
			}
			return result;
		}
		return null;
	}
}
