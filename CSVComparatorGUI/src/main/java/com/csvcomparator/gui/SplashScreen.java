package com.csvcomparator.gui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wb.swt.SWTResourceManager;

/*************************************************************************
 * This class represents the splash screen of the application.
 * 
 * @author Reto Scheiwiller, 2015
 * 
 *************************************************************************/ 
public class SplashScreen extends Shell {

	public Shell shellToOpen = null;
	
	//####################################################################################
	// CONSTRUCTORS
	//####################################################################################
	public SplashScreen(Display display) {
		super(display, SWT.ON_TOP | SWT.NO_BACKGROUND | SWT.NO_TRIM);
		
		
		//------------------------------------------------
		// Load image
		//------------------------------------------------
		Image image = SWTResourceManager.getImage(CompareDefinitionGUI.class,
						"/com/csvcomparator/gui/images/CSVComparator_SplashScreen.png");
		
		ImageData imdata = image.getImageData();
		
		//set Size, height+30 for ProgressBar
		this.setSize(imdata.width, imdata.height);
		
//		//------------------------------------------------
//		// Create ProgressBar
//		//------------------------------------------------
//		ProgressBar progressBar = new ProgressBar(this, SWT.HORIZONTAL | SWT.INDETERMINATE);
//		progressBar.setForeground(SWTResourceManager.getColor(SWT.COLOR_LIST_SELECTION));
//		progressBar.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true, 1, 1));
//		progressBar.setBounds(imdata.width/2-150, imdata.height-30, 300, 20);
//		progressBar.update();
//		
		Rectangle splashRect = this.getBounds();
		Rectangle displayRect = display.getBounds();
		int x = (displayRect.width - splashRect.width) / 2;
		int y = (displayRect.height - splashRect.height) / 2;
		this.setLocation(x, y);
		this.open();
		
		//------------------------------------------------
		// Draw the image
		//------------------------------------------------
		GC gc = new GC(this);
		gc.drawImage(image, 0, 0);


	}

	//####################################################################################
	// OVERRRIDEN METHODS
	//####################################################################################
	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
	
}