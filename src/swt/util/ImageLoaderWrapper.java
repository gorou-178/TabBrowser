package swt.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.widgets.Display;

public class ImageLoaderWrapper {

	private ImageLoaderWrapper(){}
	
	public final static Image[] loadResouceImage(Device device, String path){
		if(!SWTUtil.isAlive(device))
			return null;
		return loadImageStream(device, ImageLoaderWrapper.class.getResourceAsStream(path));
	}
	
	public final static Image[] loadResouceImage(String path){
		return loadImageStream(ImageLoaderWrapper.class.getResourceAsStream(path));
	}
	
	public final static Image[] loadImagefile(Device device, String filepath) throws FileNotFoundException{
		if(!SWTUtil.isAlive(device))
			return null;
		ImageData[] imageDatas = new ImageLoader().load(filepath);
		Image[] images = new Image[imageDatas.length];
		int index = 0;
		for(ImageData imageData: imageDatas ){
			images[index++] = new Image(device,imageData);
		}
		return images;
	}
	
	public final static Image[] loadImagefile(String filepath) throws FileNotFoundException{
		return loadImagefile(Display.getDefault(), new File(filepath));
	}
	
	public final static Image[] loadImagefile(Device device, File file) throws FileNotFoundException{
		if(!SWTUtil.isAlive(device))
			return null;
		ImageData[] imageDatas = new ImageLoader().load(new BufferedInputStream(new FileInputStream(file)));
		Image[] images = new Image[imageDatas.length];
		int index = 0;
		for(ImageData imageData: imageDatas ){
			images[index++] = new Image(Display.getDefault(),imageData);
		}
		return images;
	}
	
	public final static Image[] loadImagefile(File file) throws FileNotFoundException{
		return loadImagefile(Display.getDefault(), file);
	}
	
	public final static Image[] loadImageStream(Device device, InputStream is){
		if(!SWTUtil.isAlive(device))
			return null;
		ImageData[] imageDatas = new ImageLoader().load(is);
		Image[] images = new Image[imageDatas.length];
		int index = 0;
		for(ImageData imageData: imageDatas ){
			images[index++] = new Image(device,imageData);
		}
		return images;
	}
	
	public final static Image[] loadImageStream(InputStream is){
		return loadImageStream(Display.getDefault(), is);
	}
	
	public final static Image loadImageData(ImageData imageData){
		if(imageData == null)
			return null;
		return new Image(Display.getDefault(),imageData);
	}
	
	public final static Image loadImageData(Device device, ImageData imageData){
		return loadImageData(Display.getDefault(), imageData);
	}
	
}
