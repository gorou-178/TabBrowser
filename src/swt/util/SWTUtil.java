package swt.util;

import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Resource;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Widget;

public class SWTUtil {

	public static void asyncRun( Runnable r ){
		asyncRun( Display.getDefault(), r );
	}
	
	public static void asyncRun( Display display, Runnable r ){
		if( isAlive( display ) ){
			display.asyncExec( r );
		}
	}
	
	public static void asyncRun( Widget c, Runnable r ){
		if( isAlive( c ) ){
			Display display = c.getDisplay();
			if( isAlive( display ) ){
				asyncRun( display, r );
			}
		}
	}
	
	public static void syncRun( Runnable r ){
		syncRun( Display.getDefault(), r );
	}
	
	public static void syncRun( Display display, Runnable r ){
		if( isAlive( display ) ){
			display.syncExec( r );
		}
	}
	
	public static void syncRun( Widget c, Runnable r ){
		if( isAlive( c ) ){
			Display display = c.getDisplay();
			if( isAlive( display ) ){
				syncRun( display, r );
			}
		}
	}
	
	public static boolean isAlive (Widget c) {
		return (c != null && ! c.isDisposed ());
	}
	public static boolean isAlive (Device d) {
		return (d != null && ! d.isDisposed ());
	}
	public static boolean isAlive (Resource r) {
		return (r != null && ! r.isDisposed ());
	}
	
	public static void safeDispose (Widget c) {
		if(isAlive(c)) c.dispose();
	}
	public static void safeDispose (Device d) {
		if(isAlive(d)) d.dispose();
	}
	public static void safeDispose (Resource r) {
		if(isAlive(r)) r.dispose();
	}
	
}
