package swt.util;

import java.io.ByteArrayInputStream;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.swt.graphics.Image;

import anai.http.hc.HttpClientWrapper;

public class SiteIconCache {

	private SiteIconCache(){}
	
	public final static Image checkCache(String url){
		Image image = ImageContainer.getImage(url);
		if(image == null)
			return getSiteIcon(url);
		return image;
	}
	
	public final static Image getSiteIcon(String url){
		try {
			System.out.println("load siteIcon");
			String iconUrl = rootIconUrl(url);
			loadingSiteIcon(iconUrl);
			return ImageContainer.getImage(iconUrl);
		} catch (InterruptedException e) {
			e.printStackTrace();
			return null;
		} catch (ExecutionException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private final static void loadingSiteIcon(String url) throws InterruptedException, ExecutionException{
		final String iconUrl = rootIconUrl(url);
		Future<Image> future = SWTContext.executor.submit(new Callable<Image>() {
			@Override
			public Image call() throws Exception {
				System.out.println("siteicon loading...");
				ByteArrayInputStream bais = new ByteArrayInputStream(HttpClientWrapper.getResponseToByte(iconUrl, null));
				if(bais == null)
					return null;
				
				Image[] images = ImageLoaderWrapper.loadImageStream(bais);
				if(images == null || images.length <= 0)
					return null;
				
				System.out.println("siteicon load compleate.");
				return images[0];
			}
		});
		
		Image image = future.get();
		if(image != null)
			ImageContainer.addImage(iconUrl, image);
	}
	
	private static Pattern urlPttern = Pattern.compile("^(http(?:|s))://([^:/]+)(?::(¥¥d*)|)(/.*|)");
	private static String rootIconUrl (final String url) {
		Matcher matcher = urlPttern.matcher(url);
		if(matcher.find()){
			String protcol = matcher.group(1);
			String host = matcher.group(2);
			String port = matcher.group(3);
			try {
				int nPort = Integer.parseInt(port);
				if( "http".equals(protcol) && nPort == 80 )
					port = "";
				else if( "https".equals(protcol) && nPort == 443 )
					port = "";
				port = ":" + port;
			} catch (NumberFormatException e) {
				port = "";
			}
			String rootUrl = protcol + "://" + host + port;
			return rootUrl + "/favicon.ico";
		}
		return null;
	}
}
