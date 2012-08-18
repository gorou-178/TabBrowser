package swt.util;

import gnu.trove.THashMap;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Display;

public class ImageContainer {

	private final static Map<String, Image> imageMap = new THashMap<String, Image>();
	private ImageContainer(){}
	
	public final static Image getImage(String key){
		return imageMap.get(key);
	}
	
	public final static String getKey(Image image){
		if(!SWTUtil.isAlive(image))
			return null;
		if(imageMap.containsValue(image)){
			for(Map.Entry<String, Image> entry: imageMap.entrySet()){
				if(image.equals(entry.getValue())){
					return entry.getKey();
				}
			}
		}
		return null;
	}
	
	public final static boolean containImage(Image image){
		if(!SWTUtil.isAlive(image))
			return false;
		return imageMap.containsValue(image);
	}
	
	public final static boolean containsKey(String key){
		if(StringUtils.isEmpty(key))
			return false;
		return imageMap.containsKey(key);
	}
	
	public final static void addImage(String key, Image image){
		Image oldImage = imageMap.put(key, image);
		if(SWTUtil.isAlive(oldImage))
			oldImage.dispose();
	}
	
	public final static void addImage(String key, ImageData imageData){
		Image oldImage = imageMap.put(key, new Image(Display.getDefault(),imageData));
		if(SWTUtil.isAlive(oldImage))
			oldImage.dispose();
	}
	
	public final static void removeImage(String key){
		Image delImage = imageMap.remove(key);
		if(SWTUtil.isAlive(delImage))
			delImage.dispose();
	}
	
	public final static void removeImage(Image image){
		if(!SWTUtil.isAlive(image))
			return;
		if(imageMap.containsValue(image)){
			for(Map.Entry<String, Image> entry: imageMap.entrySet()){
				if(image.equals(entry.getValue())){
					imageMap.remove(entry.getKey());
					break;
				}
			}
		}
	}
	
	public final static void removeImageAll(){
		for( Image image: imageMap.values()){
			if(SWTUtil.isAlive(image))
				image.dispose();
		}
	}
	
}
