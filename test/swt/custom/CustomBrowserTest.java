package swt.custom;

import java.util.concurrent.Executors;

import junit.framework.Assert;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import swt.util.SWTContext;

public class CustomBrowserTest {
	
	private static CustomBrowser browser;
	private static Display display;
	private static Shell shell;
	
	@BeforeClass
	public static void beforeClass() throws Exception {
		System.out.println("call beforeClass");
		shell.setLayout(new GridLayout(1, true));
		shell.setText( "GurimmerLibrary" );
		shell.setBackground(display.getSystemColor(SWT.COLOR_GRAY));
		
		SWTContext.init(display, shell, Executors.newScheduledThreadPool(10));
		browser = new CustomBrowser(shell);
	}

	@AfterClass
	public static void afterClass() throws Exception {
		System.out.println("call afterClass");
		SWTContext.shutdown();
		
	}

	@Before
	public void before() throws Exception {
		System.out.println("call before");
	}
	
	@After
	public void after() throws Exception {
		System.out.println("call after");
	}
	
	@Test
	public void getBrowserTest() throws Exception {
		Browser tmpBrowser = browser.getBrowser();
		Assert.assertNotNull(tmpBrowser);
	}
	
}
