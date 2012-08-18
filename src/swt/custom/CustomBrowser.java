package swt.custom;

import java.io.FileNotFoundException;
import java.util.concurrent.Executors;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.browser.LocationListener;
import org.eclipse.swt.browser.OpenWindowListener;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.ProgressListener;
import org.eclipse.swt.browser.TitleEvent;
import org.eclipse.swt.browser.TitleListener;
import org.eclipse.swt.browser.WindowEvent;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.GestureEvent;
import org.eclipse.swt.events.GestureListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.internal.cocoa.OS;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import swt.util.ImageContainer;
import swt.util.ImageLoaderWrapper;
import swt.util.SWTContext;
import swt.util.SiteIconCache;

public class CustomBrowser {

	private static String [] image_files = {
		"load/load1_16.gif",
		"load/load2_16.gif",
		"load/load3_16.gif",
		"load/load4_16.gif",
		"load/load5_16.gif",
		"load/load6_16.gif",
		"load/load7_16.gif",
		"load/load8_16.gif",
	};
	
	private Shell parent;
	private CText text;
	private Browser browser;
	private Image[] images;
	
	public CustomBrowser(Shell shell){
		parent = shell;
		create(shell.getDisplay(), shell);
	}
	
	private void create(final Display display, final Shell shell){
		
		images = loadImage(display, image_files);
		
		shell.addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent disposeevent) {
				Image oldImage = shell.getImage();
				parent.setImage(null);
				if(oldImage != null && ! oldImage.isDisposed()){
					oldImage.dispose();
					oldImage = null;
				}
			}
		});
		
		text = new CText(shell,SWT.BORDER_SOLID);
		text.setLayout(new GridLayout(2,false));
		text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		text.setImage(images[0]);
		text.addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent arg0) {
				text.setImage(null);
				disposeImage(images);
			}
		});
		
		final LoadingAnimation animation = new LoadingAnimation(display, images);
		LoadingAnimationListener animationListener = new LoadingAnimationListener() {
			@Override
			public void stopAnimation(Image image) {
				text.setImage(image);
			}
			@Override
			public void startAnimation(Image image) {
				text.setImage(image);
			}
			@Override
			public void loadingAnimation(Image image) {
				text.setImage(image);
			}
		};
		animation.addLoadingAnimationListener(animationListener);
		
		browser = new Browser(shell,SWT.BORDER);
		browser.addProgressListener(new ProgressListener() {
			@Override
			public void completed(ProgressEvent progressevent) {
				System.out.println("completed");
				display.asyncExec(new Runnable(){
					public void run(){
						Image siteIcon = SiteIconCache.checkCache(browser.getUrl());
						parent.setImage(siteIcon);
					}
				});
			}
			
			@Override
			public void changed(ProgressEvent progressevent) {
				System.out.println(progressevent.current + "/" + progressevent.total);
			}
		});
		
		browser.setLayoutData(new GridData(GridData.FILL_BOTH));
		browser.setUrl("http://www.google.co.jp/");
		browser.addOpenWindowListener(new OpenWindowListener() {
			@Override
			public void open(WindowEvent windowevent) {
				final Shell shell = new Shell(display);
                shell.setText("New Window");
                shell.setLayout(new GridLayout(1,true));
                shell.open();
                CustomBrowser browser = new CustomBrowser(shell);
                windowevent.browser = browser.getBrowser();
                display.timerExec(10, new Runnable(){
                	public void run(){
                		shell.layout();
                	}
                });
			}
		});
		
		browser.addTitleListener(new TitleListener() {
			@Override
			public void changed(TitleEvent titleevent) {
				shell.setText(titleevent.title);
			}
		});
		
		browser.addLocationListener(new LocationListener() {
			@Override
			public void changing(LocationEvent locationevent) {
				System.out.println("location chanding...");
				animation.start();
			}
			
			@Override
			public void changed(LocationEvent locationevent) {
				text.setText(locationevent.location);
				System.out.println("location chanded");
				animation.stop();
			}
		});
		
		browser.addGestureListener(new GestureListener() {
			@Override
			public void gesture(GestureEvent arg0) {
				if(arg0.detail == SWT.GESTURE_SWIPE){
					if(arg0.xDirection > 0){
						browser.forward();
					}
					else if(arg0.xDirection < 0){
						browser.back();
					}
				}
			}
		});
		
		text.addKeyListener(new KeyListener() {
			@Override
			public void keyReleased(KeyEvent arg0) {
				System.out.println("text keyReleased: " + arg0.keyCode);
				if(arg0.keyCode == OS.NSCarriageReturnCharacter){ // Return KeyCode = 13
					browser.setUrl(text.getText().getText());
				}
			}
			
			@Override
			public void keyPressed(KeyEvent arg0) {
				System.out.println("text keyPressed: " + arg0.keyCode);
			}
		});
		
	}
	
	public void setUrl(String url){
		if(!browser.isDisposed())
			browser.setUrl(url);
	}
	
	public void setText(String text){
		if(!this.text.isDisposed())
			this.text.getText().setText(text);
	}
	
	public Browser getBrowser(){
		return browser;
	}
	
	private Image[] loadImage(Display display, String[] files){
		
		Image[] images = new Image [files.length];
		int i = 0;
		for (final String path : files){
			try {
				Image[] loadImages = ImageLoaderWrapper.loadImagefile(path);
				if(images != null){
					images [i++] = loadImages[0];
					ImageContainer.addImage(path, loadImages[0]);
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		return images;
	}
	
	private void disposeImage(Image[] images){
		for(Image image : images){
			if(!image.isDisposed())
				image.dispose();
		}
	}
	
	public static void main(String[] args) {
		
		Display display = new Display();
		Shell shell = new Shell( display );
		shell.setLayout(new GridLayout(1, true));
		shell.setText( "GurimmerLibrary" );
		shell.setBackground(display.getSystemColor(SWT.COLOR_GRAY));
		
		SWTContext.init(display, shell, Executors.newScheduledThreadPool(10));
		
		CustomBrowser browser = new CustomBrowser(shell);
		browser.setUrl("http://www.google.co.jp/");
		
		shell.open();
		while( ! shell.isDisposed() ){
			if( ! display.readAndDispatch() ){
				display.sleep();
			}
		}
		
		SWTContext.shutdown();
	}

	public Shell getParent() {
		return parent;
	}

	public void setParent(Shell parent) {
		this.parent = parent;
	}

	public CText getText() {
		return text;
	}

	public void setText(CText text) {
		this.text = text;
	}

	public Image[] getImages() {
		return images;
	}

	public void setImages(Image[] images) {
		this.images = images;
	}

	public void setBrowser(Browser browser) {
		this.browser = browser;
	}
	
}
