package org.csstudio.nams.configurator.editor.stackparts;

import org.csstudio.nams.configurator.editor.DirtyFlagProvider;
import org.csstudio.nams.configurator.treeviewer.model.AbstractConfigurationBean;
import org.csstudio.nams.configurator.treeviewer.model.ConfigurationBean;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

public class DefaultStackPart extends AbstractStackPart {

	private Composite _main;

	public DefaultStackPart(DirtyFlagProvider flagProvider, Composite parent) {
		super(flagProvider, ConfigurationBean.class, 0);
		_main = new Composite(parent, SWT.NONE);
		_main.setLayout(new GridLayout(1, false));
		Label label = new Label(_main, SWT.NONE);
		label.setText("Default");
	}

	@Override
	public Control getMainControl() {
		return _main;
	}

	@Override
	public boolean isDirty() {
		return false;
	}

	@Override
	public void save(ConfigurationBean original) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("not implemented yet.");
	}

}
