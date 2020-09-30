package com.csvcomparator.gui.utils;

import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.swt.widgets.Control;
import org.eclipse.wb.swt.SWTResourceManager;

public class SWTGUIUtils {

	public static void addInfoDeco(Control control, String text, int position){
		ControlDecoration deco = new ControlDecoration(control, position);
		deco.setImage(SWTResourceManager.getImage(control.getClass(), "/org/eclipse/jface/fieldassist/images/info_ovr.gif"));
		deco.setDescriptionText(text);
	}
}
