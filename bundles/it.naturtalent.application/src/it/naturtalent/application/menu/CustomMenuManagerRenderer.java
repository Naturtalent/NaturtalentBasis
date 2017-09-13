package it.naturtalent.application.menu;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.workbench.renderers.swt.MenuManagerRenderer;
import org.eclipse.jface.internal.MenuManagerEventHelper;

@SuppressWarnings("restriction")
public class CustomMenuManagerRenderer extends MenuManagerRenderer {

	@Override
	public void init(IEclipseContext context) {
		super.init(context);
	}

	@Override
	public void init() {
		super.init();
		MenuManagerEventHelper.getInstance()
				.setHideHelper(ContextInjectionFactory.make(MenuManagerHideProcessor.class, context));
	}

}
