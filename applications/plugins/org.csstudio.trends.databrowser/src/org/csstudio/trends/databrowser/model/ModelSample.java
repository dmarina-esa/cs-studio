package org.csstudio.trends.databrowser.model;

import org.csstudio.swt.chart.ChartSample;
import org.csstudio.value.Value;
import org.csstudio.value.ValueUtil;

/** One sample of a ModelItem.
 *  <p>
 *  The archive presents samples in the fullest,
 *  with timestamp, status, severity.
 *  <p>
 *  The plot library wants the samples as x/y coordinate,
 *  with info for tooltip and representation.
 *  <p>
 *  This interface allows both:
 *  It provides the archive sample, but also presents it
 *  via an interface suitable for the plot library. 
 *  
 *  @author Kay Kasemir
 */
public class ModelSample implements ChartSample
{
    /** This is the 'real' archive sample. */
    private final Value sample;
    
    /** Create ModelSample from Archive Sample. */
    ModelSample(Value sample)
    {
        this.sample = sample;
    }
    
    /** @return The archive sample. */
    public Value getSample()
    {   return sample;  }
    
    /** @see org.csstudio.swt.chart.ChartSample */
    public ChartSample.Type getType()
    {
        double y = getY();
        if (Double.isInfinite(y)  ||  Double.isNaN(y))
            return ChartSample.Type.Point;
        return ChartSample.Type.Normal;
    }

    /** @see org.csstudio.swt.chart.ChartSample */
    public double getX()
    {
        return sample.getTime().toDouble();
    }

    /** @see org.csstudio.swt.chart.ChartSample */
    public double getY()
    {
        return ValueUtil.getDouble(sample);
    }

    /** @see org.csstudio.swt.chart.ChartSample */
    public String getInfo()
    {
        return ValueUtil.getInfo(sample);
    }

    @Override
    public boolean equals(Object obj)
    {
        if (! (obj instanceof ModelSample))
            return false;
        ModelSample o = (ModelSample) obj;
        return sample.equals(o.getSample());
    }
    
    @Override
    public String toString()
    {   return "ModelSample: " + sample.toString();  } //$NON-NLS-1$
}
