package com.csvcomparator.gui;

import org.eclipse.swt.widgets.Display;

/*************************************************************************
 * Starts the application, first it displays the splash screen and then 
 * initializes the GUI.
 * 
 * @author Reto Scheiwiller, 2015
 * 
 *************************************************************************/ 
public class Main {

	private static Display display;

	/*************************************************************************
	 * Launch the application.
	 * 
	 * @param args
	 *************************************************************************/ 
	public static void main(String args[]) {
		try {
			
			display = new Display();
			
			//------------------------------------------------
			// Show the splash screen
			//------------------------------------------------
			final SplashScreen splash = new SplashScreen(display);
			
			//------------------------------------------------
			// Load the GUI while the splash is displayed.
			//------------------------------------------------
			display.asyncExec(new Runnable() {
				
				@Override
				public void run() {
					long startMillis = System.currentTimeMillis();
					
					//------------------------------------------------
					// Initialize the GUI
					//------------------------------------------------
					final CSVComparatorGUI shell = new CSVComparatorGUI(display);
					
					//------------------------------------------------
					// Show the splash screen for at least 3500ms 
					//------------------------------------------------
					if (System.currentTimeMillis() - startMillis < 3500) {
						try {
							Thread.sleep(3500 - (System.currentTimeMillis() - startMillis));
						} catch (InterruptedException e) {
							System.out.print("Exception occured during initialization.");
							e.printStackTrace();
						}
					}
					
					splash.dispose();
					
					shell.open();
					shell.setMinimized(false);
					shell.setActive();
					
				}
			});
			
			//------------------------------------------------
			// Event Loop
			//------------------------------------------------
			while((Display.getCurrent().getShells().length != 0)
	                 && !Display.getCurrent().getShells()[0].isDisposed()){
				if (!display.readAndDispatch()) {
					display.sleep();
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
