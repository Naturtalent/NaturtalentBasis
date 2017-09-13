package it.naturtalent.application.menu;

import org.eclipse.e4.ui.internal.workbench.swt.AbstractPartRenderer;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.ui.model.application.ui.menu.MMenu;
import org.eclipse.e4.ui.workbench.renderers.swt.WorkbenchRendererFactory;

@SuppressWarnings("restriction")
public class CustomWorkbenchRendererFactory extends WorkbenchRendererFactory {
	
	private CustomMenuManagerRenderer myMenuManagerRenderer;
	
	@Override
	public AbstractPartRenderer getRenderer(MUIElement uiElement, Object parent) {
		if (uiElement instanceof MMenu) {
			if (myMenuManagerRenderer == null) {
				myMenuManagerRenderer = new CustomMenuManagerRenderer();
				super.initRenderer(myMenuManagerRenderer);
				myMenuManagerRenderer.init();
			}
			return myMenuManagerRenderer;
		}
		
		return super.getRenderer(uiElement, parent);
	}
}
