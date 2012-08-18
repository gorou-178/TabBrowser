package swt.custom;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

public class LoadingAnimation {

	private Display display;
	private Image [] images;
	private AtomicBoolean stopFlag;
	private AtomicBoolean startFlag;
	private int index;
	private List<LoadingAnimationListener> listeners;
	private Runnable loadingTask;
	
	public LoadingAnimation(Display d, Image[] images){
		this.display = d;
		this.images = images;
		this.startFlag = new AtomicBoolean(false);
		this.stopFlag = new AtomicBoolean(false);
		this.index = 0;
		listeners = new ArrayList<LoadingAnimationListener>();
		loadingTask = new Runnable() {
			@Override
			public void run() {
				if(stopFlag.get())
					return;
				loading();
				if(!display.isDisposed())
					display.timerExec(120, this);
			}
		};
	}
	public void start(){
		if(startFlag.get())
			return;
		startFlag.set(true);
		stopFlag.set(false);
		notifyStartAnimationListener(images[index]);
		if(display.isDisposed())
			return;
		display.timerExec(120, loadingTask);
	}
	public void stop(){
//		startFlag.set(false);
//		stopFlag.set(true);
		notifyStopAnimationListener();
	}
	private void loading(){
		notifyLoadingAnimationListener(images[index]);
		index = (index + 1) % images.length;
	}
	public void notifyStopAnimationListener(){
		//index = 0;
		for(final LoadingAnimationListener listener : listeners){
			listener.stopAnimation(images[index]);
		}
	}
	public void notifyStartAnimationListener(final Image image){
		for(final LoadingAnimationListener listener : listeners){
			listener.startAnimation(image);
		}
	}
	public void notifyLoadingAnimationListener(final Image image){
		for(final LoadingAnimationListener listener : listeners){
			listener.loadingAnimation(image);
		}
	}
	public void addLoadingAnimationListener(LoadingAnimationListener listener){
		listeners.add(listener);
	}
	public void removeLoadingAnimationListener(LoadingAnimationListener listener){
		listeners.remove(listener);
	}
	
}
