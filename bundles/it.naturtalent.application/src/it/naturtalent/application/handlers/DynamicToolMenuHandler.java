
package it.naturtalent.application.handlers;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuItem;

public class DynamicToolMenuHandler {

	@Execute
	public void execute(MMenuItem menuItem) {
		System.err.println(menuItem.getLabel() + " invoked");
	}

}