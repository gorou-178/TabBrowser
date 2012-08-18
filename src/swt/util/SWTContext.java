package swt.util;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class SWTContext {
	
	public static ScheduledExecutorService executor = null;
	private static SWTContext context = null;
	
	private final Display display;
	private final Shell shell;
	
	private SWTContext(Display display, Shell shell, ScheduledExecutorService executor){
		this.display = display;
		this.shell = shell;
		this.executor = executor;
	}
	
	public final static void init(Display display, Shell shell){
		SWTContext.init(display, shell, Executors.newSingleThreadScheduledExecutor());
	}
	
	public final static void init(Display display, Shell shell, ScheduledExecutorService executor){
		if(context == null){
			context = new SWTContext(display,shell, executor);
		}
	}
	
	public final static void shutdown(){	
		instance().executor.shutdown();
		instance().dispose();
	}
	
	public final static SWTContext instance(){
		return context;
	}
	
	public final Display getDisplay(){
		return display;
	}
	
	public final Shell getShell(){
		return shell;
	}
	
	public final void dispose(){
		if(SWTUtil.isAlive(shell)){
			Shell[] children = shell.getShells();
			for(Shell child: children){
				if(SWTUtil.isAlive(child))
					child.dispose();
			}
			shell.dispose();
		}
		if(SWTUtil.isAlive(display)){
			display.dispose();
		}
	}
	
}
