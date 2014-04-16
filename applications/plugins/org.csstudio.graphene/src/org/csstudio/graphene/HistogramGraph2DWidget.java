package org.csstudio.graphene;

import static org.epics.pvmanager.formula.ExpressionLanguage.formula;
import static org.epics.pvmanager.formula.ExpressionLanguage.formulaArg;
import static org.epics.pvmanager.graphene.ExpressionLanguage.histogramGraphOf;
import static org.epics.pvmanager.vtype.ExpressionLanguage.vDouble;
import static org.epics.util.time.TimeDuration.ofHertz;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.csstudio.csdata.ProcessVariable;
import org.csstudio.ui.util.widgets.ErrorBar;
import org.csstudio.utility.pvmanager.ui.SWTUtil;
import org.csstudio.utility.pvmanager.widgets.ConfigurableWidget;
import org.csstudio.utility.pvmanager.widgets.VImageDisplay;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IMemento;
import org.epics.graphene.AreaGraph2DRendererUpdate;
import org.epics.graphene.Graph2DRendererUpdate;
import org.epics.graphene.InterpolationScheme;
import org.epics.graphene.LineGraph2DRendererUpdate;
import org.epics.graphene.ScatterGraph2DRendererUpdate;
import org.epics.pvmanager.PVManager;
import org.epics.pvmanager.PVReader;
import org.epics.pvmanager.PVReaderEvent;
import org.epics.pvmanager.PVReaderListener;
import org.epics.pvmanager.PVWriter;
import org.epics.pvmanager.PVWriterEvent;
import org.epics.pvmanager.PVWriterListener;
import org.epics.pvmanager.graphene.BubbleGraph2DExpression;
import org.epics.pvmanager.graphene.ExpressionLanguage;
import org.epics.pvmanager.graphene.Graph2DExpression;
import org.epics.pvmanager.graphene.Graph2DResult;
import org.epics.pvmanager.graphene.HistogramGraph2DExpression;
import org.epics.pvmanager.graphene.LineGraph2DExpression;
import org.epics.util.array.ArrayDouble;
import org.epics.util.array.ArrayInt;
import org.epics.vtype.VNumberArray;
import org.epics.vtype.VTable;
import org.epics.vtype.ValueFactory;
import org.epics.vtype.ValueUtil;

public class HistogramGraph2DWidget
		extends
		AbstractGraph2DWidget<AreaGraph2DRendererUpdate, HistogramGraph2DExpression>
		implements ISelectionProvider {

	private PVWriter<Object> selectionValueWriter;
	
	private boolean highlightSelectionValue = false;
	
	private static final String MEMENTO_HIGHLIGHT_SELECTION_VALUE = "highlightSelectionValue"; //$NON-NLS-1$

    /**
     * Creates a new widget.
     * 
     * @param parent
     *            the parent
     * @param style
     *            the style
     */
    public HistogramGraph2DWidget(Composite parent, int style) {
    	super(parent, style);
		addPropertyChangeListener(new PropertyChangeListener() {
			
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (evt.getPropertyName().equals("highlightSelectionValue") && getGraph() != null) {
					getGraph().update(getGraph().newUpdate().highlightFocusValue((Boolean) evt.getNewValue()));
				}
				
			}
		});
		getImageDisplay().addMouseMoveListener(new MouseMoveListener() {
			
			@Override
			public void mouseMove(MouseEvent e) {
				if (isHighlightSelectionValue() && getGraph() != null) {
					getGraph().update(getGraph().newUpdate().focusPixel(e.x));
				}
			}
		});
		
		addPropertyChangeListener(new PropertyChangeListener() {
			
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (evt.getPropertyName().equals("selectionValuePv") && getGraph() != null) {
					if (selectionValueWriter != null) {
						selectionValueWriter.close();
						selectionValueWriter = null;
					}
					
					if (getSelectionValuePv() == null || getSelectionValuePv().trim().isEmpty()) {
						return;
					}
					
					selectionValueWriter = PVManager.write(formula(getSelectionValuePv()))
							.writeListener(new PVWriterListener<Object>() {
								@Override
								public void pvChanged(
										PVWriterEvent<Object> event) {
									if (event.isWriteFailed()) {
										Logger.getLogger(BubbleGraph2DWidget.class.getName())
										.log(Level.WARNING, "Line graph selection notification failed", event.getPvWriter().lastWriteException());
									}
								}
							})
							.async();
					if (getSelectionValue() != null) {
						selectionValueWriter.write(getSelectionValue());
					}
							
				}
				
			}
		});
		addPropertyChangeListener(new PropertyChangeListener() {
			
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (evt.getPropertyName().equals("selectionValue") && selectionValueWriter != null) {
					if (getSelectionValue() != null) {
						selectionValueWriter.write(getSelectionValue());
					}
				}
				
			}
		});
    }
    
	protected HistogramGraph2DExpression createGraph() {
		HistogramGraph2DExpression graph = histogramGraphOf(formula(getDataFormula()));
		graph.update(graph.newUpdate().highlightFocusValue(isHighlightSelectionValue()));
		return graph;
	}
	
	public boolean isHighlightSelectionValue() {
		return highlightSelectionValue;
	}
	
	public void setHighlightSelectionValue(boolean highlightSelectionValue) {
		boolean oldValue = this.highlightSelectionValue;
		this.highlightSelectionValue = highlightSelectionValue;
		changeSupport.firePropertyChange("highlightSelectionValue", oldValue, this.highlightSelectionValue);
	}

	public void saveState(IMemento memento) {
		super.saveState(memento);
		memento.putBoolean(MEMENTO_HIGHLIGHT_SELECTION_VALUE, isHighlightSelectionValue());
	}

	public void loadState(IMemento memento) {
		super.loadState(memento);
		if (memento != null) {
			if (memento.getBoolean(MEMENTO_HIGHLIGHT_SELECTION_VALUE) != null) {
				setHighlightSelectionValue(memento.getBoolean(MEMENTO_HIGHLIGHT_SELECTION_VALUE));
			}
		}
	}
	
	private VNumberArray selectionValue;
	private String selectionValuePv;
	
	public String getSelectionValuePv() {
		return selectionValuePv;
	}
	
	public void setSelectionValuePv(String selectionValuePv) {
		String oldValue = this.selectionValuePv;
		this.selectionValuePv = selectionValuePv;
		changeSupport.firePropertyChange("selectionValuePv", oldValue, this.selectionValuePv);
	}
	
	public VNumberArray getSelectionValue() {
		return selectionValue;
	}
	
	private void setSelectionValue(VNumberArray selectionValue) {
		VNumberArray oldValue = this.selectionValue;
		this.selectionValue = selectionValue;
		if (oldValue != this.selectionValue) {
			changeSupport.firePropertyChange("selectionValue", oldValue, this.selectionValue);
		}
	}
	
	@Override
	protected void processInit() {
		super.processInit();
		processValue();
	}
	
	@Override
	protected void processValue() {
		Graph2DResult result = getCurrentResult();
		if (result == null || result.getData() == null) {
			setSelectionValue(null);
		} else {
			int index = result.focusDataIndex();
			if (index == -1) {
				setSelectionValue(null);
			} else {
				if (result.getData() instanceof VNumberArray) {
					VNumberArray data = (VNumberArray) result.getData();
					VNumberArray selection = ValueUtil.subArray(data, index);
					setSelectionValue(selection);
					return;
				}
				setSelectionValue(null);
			}
		}
	}

	@Override
	public ISelection getSelection() {
		if (getDataFormula() != null) {
			return new StructuredSelection(new HistogramGraph2DSelection(this));
		}
		return null;
	}

	@Override
	public void addSelectionChangedListener(
			final ISelectionChangedListener listener) {
	}

	@Override
	public void removeSelectionChangedListener(
			ISelectionChangedListener listener) {
	}

	@Override
	public void setSelection(ISelection selection) {
		throw new UnsupportedOperationException("Not implemented yet");
	}

	private boolean configurable = true;

	private HistogramGraph2DConfigurationDialog dialog;

	@Override
	public boolean isConfigurable() {
		return this.configurable;
	}

	@Override
	public void setConfigurable(boolean configurable) {
		boolean oldValue = this.configurable;
		this.configurable = configurable;
		changeSupport.firePropertyChange("configurable", oldValue,
				this.configurable);
	}

	@Override
	public void openConfigurationDialog() {
		if (dialog != null)
			return;
		dialog = new HistogramGraph2DConfigurationDialog(this, "Configure Histogram");
		dialog.open();
	}

	@Override
	public boolean isConfigurationDialogOpen() {
		return dialog != null;
	}

	@Override
	public void configurationDialogClosed() {
		dialog = null;
	}

}
