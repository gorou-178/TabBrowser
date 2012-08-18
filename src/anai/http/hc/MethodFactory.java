package anai.http.hc;

import static org.apache.commons.lang.CharEncoding.*;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.ByteArrayRequestEntity;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;

public abstract class MethodFactory {
	
	public static final String GET = "GET";
	public static final String HEAD = "HEAD";
	public static final String POST = "POST";
	public static final String PUT = "PUT";
	public static final String DELETE = "DELETE";
	public static final String CONNECT = "CONNECT";

	public static final String CLOSE = "Close";
	public static final String KEEP_ALIVE = "Keep-Alive";

	public static final String HTTP_1_0 = "HTTP/1.0";
	public static final String HTTP_1_1 = "HTTP/1.1";
	
	public static final String CONTENT_TYPE = "Content-Type";
	public static final String MIME_TYPE_FORM = "application/x-www-form-urlencoded;charset=";

	public static final String DEFAULT_USER_AGENT = "Mozilla/4.0  (compatible; MSIE 6.0; Windows NT 5.1;)";

	public static final String LOCAL_LOOPBACK_ADDRESS = "127.0.0.1";
	
	public final static class Get extends MethodFactory {
		final String url;
		Get (final String url) {
			this.url = url;
		}
		public HttpMethod create () {
			GetMethod gm = new GetMethod (url);
			//gm.setRequestHeader ("Accept-Encoding:", "gzip");
			return gm;
		}
		public String methodName () { return GET; }
	}
	
	public final static class Post extends MethodFactory {
		final String url;
		final String [] params;
		final String encode;
		Post (final String url, final String [] params, final String encode) {
			this.url = url;
			this.params = params;
			this.encode = encode;
		}

		public HttpMethod create () {
			PostMethod pm = new PostMethod (url);
			pm.setRequestHeader( CONTENT_TYPE, MIME_TYPE_FORM + encode );
			//pm.setRequestHeader ("Accept-Encoding:", "gzip");
			for (int i = 0, n = params.length; i + 1 < n; i+= 2){
				pm.addParameter (params [i], params [i + 1]);
			}
			return pm;
		}
		public String methodName () { return POST; }
	}
	public final static class Put extends MethodFactory {
		final String url;
		final RequestEntity entity;
		Put (final String url, final String data) {
			this.url = url;
			RequestEntity re = null;
			try{
				this.entity = new StringRequestEntity (data, "text/plain", UTF_8);
			} catch (Throwable t) {
				throw new IllegalStateException ("UTF-8 must be supported", t);
			}
		}
		Put (final String url, byte [] data, String mimeType) {
			this.url = url;
			this.entity = new ByteArrayRequestEntity (data, mimeType);
		}
		public HttpMethod create () {
			PutMethod pm = new PutMethod (url);
			pm.setContentChunked (true);
			pm.setRequestEntity (entity);
			return pm;
		}
		public String methodName () { return PUT; }
	}
	public final static class Delete extends MethodFactory {
		final String url;
		Delete (final String url) {
			this.url = url;
		}
		public HttpMethod create () {
			DeleteMethod pm = new DeleteMethod (url);
			return pm;
		}
		public String methodName () { return DELETE; }
	}

	abstract protected HttpMethod create ();
	abstract protected String methodName ();
}
