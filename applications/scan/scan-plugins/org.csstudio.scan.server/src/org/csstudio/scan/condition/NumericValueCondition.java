/*******************************************************************************
 * Copyright (c) 2011-2015 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * The scan engine idea is based on the "ScanEngine" developed
 * by the Software Services Group (SSG),  Advanced Photon Source,
 * Argonne National Laboratory,
 * Copyright (c) 2011 , UChicago Argonne, LLC.
 *
 * This implementation, however, contains no SSG "ScanEngine" source code
 * and is not endorsed by the SSG authors.
 ******************************************************************************/
package org.csstudio.scan.condition;

import java.time.Duration;
import java.util.concurrent.TimeoutException;

import org.csstudio.scan.ScanSystemPreferences;
import org.csstudio.scan.command.Comparison;
import org.csstudio.scan.device.Device;
import org.csstudio.scan.device.DeviceListener;
import org.csstudio.scan.device.VTypeHelper;
import org.diirt.util.time.TimeDuration;

/** Condition that waits for a Device to reach a certain numeric value.
 *
 *  <p>For absolute checks (Comparison.EQUALS, ABOVE, ...) the current
 *  value of the device is monitored.
 *
 *  <p>For relative checks (INCREASED_BY, DECREASED_BY), the reference
 *  point is the initial value of the device when await() was called,
 *  resetting in case the desired value is changed while await() is
 *  pending.
 *
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class NumericValueCondition implements DeviceCondition, DeviceListener
{
    protected final static Duration value_check_timeout = TimeDuration.ofSeconds(ScanSystemPreferences.getValueCheckTimeout());

    /** Device to monitor */
    final private Device device;

    /** Comparison */
    final private Comparison comparison;

    /** Desired value of device */
    private double desired_value;

    /** Tolerance to use for Comparison.EQUALS */
    final private double tolerance;

    /** Timeout in seconds, <code>null</code> to "wait forever" */
    final private Duration timeout;

    /** Initial value to await Comparison.INCREASE_BY/DECREASE_BY */
    private volatile double initial_value = Double.NaN;

    /** Updated by device listener or forced for early completion */
    private boolean is_condition_met;

    /** Updated by device listener */
    private Exception error = null;

    /** Initialize
     *  @param device {@link Device} where values should be read
     *  @param comparison Comparison to use
     *  @param desired_value Desired numeric value of device
     *  @param tolerance Tolerance, e.g. 0.1
     *  @param timeout Time out in seconds, or <code>null</code> for "wait forever"
     */
    public NumericValueCondition(final Device device, final Comparison comparison,
            final double desired_value, final double tolerance,
            final Duration timeout)
    {
        this.device = device;
        this.comparison = comparison;
        this.tolerance = Math.abs(tolerance);
        this.timeout = timeout;
        setDesiredValue(desired_value);
    }

    /** @param desired_value (New) desired value, replacing the one set at initialization time */
    public void setDesiredValue(final double desired_value)
    {   // Invalidate initial value
        initial_value = Double.NaN;
        this.desired_value = desired_value;
    }

    /** Wait for value of device to reach the desired value (within tolerance)
     *  @throws TimeoutException on timeout
     *  @throws Exception on interruption or device read error
     */
    @Override
    public void await() throws TimeoutException, Exception
    {
        final WaitWithTimeout timeout = new WaitWithTimeout(this.timeout);

        // Fetch initial value with get-callback
        initial_value = VTypeHelper.toDouble(device.read(value_check_timeout));

        device.addListener(this);
        try
        {
            // Synchronize to avoid the following situation:
            // 1. not at desired value
            // 2. device changes and we would be notified
            // 3. ... but that's before we call wait, so we wait forever
            synchronized (this)
            {
                is_condition_met = isConditionMet();
                while (! is_condition_met)
                {   // Wait for update from device
                    if (timeout.waitUntilTimeout(this))
                        throw new TimeoutException("Timeout while waiting for " + device +
                                                   " " + comparison + " " + desired_value);
                    if (error != null)
                        throw error;
                }
            }
        }
        finally
        {
            device.removeListener(this);
        }
    }

    /** Determine if the condition is currently met
     *  @return <code>true</code> if condition is met
     *  @throws Exception on error reading from the device
     */
    public boolean isConditionMet() throws Exception
    {
        final double value = VTypeHelper.toDouble(device.read());
        // Note that these need to fail "safe" if any of the values are NaN
        switch (comparison)
        {
        case EQUALS:
            return Math.abs(desired_value - value) <= tolerance;
        case AT_LEAST:
            return value >= desired_value;
        case ABOVE:
            return value > desired_value;
        case AT_MOST:
            return value <= desired_value;
        case BELOW:
            return value < desired_value;
        case INCREASE_BY:
            return value >= initial_value + desired_value;
        case DECREASE_BY:
            return value <= initial_value - desired_value;
        default:
            throw new Error("Condition not implemented: " + comparison);
        }
    }

    /** Trigger another check of device's value
     *  {@inheritDoc}
     */
    @Override
    public void deviceChanged(final Device device)
    {
        synchronized (this)
        {
            try
            {
                if (Double.isNaN(initial_value))
                    initial_value = VTypeHelper.toDouble(device.read());
                if (isConditionMet())
                    is_condition_met = true;
            }
            catch (Exception ex)
            {
                error = ex;
            }
            // Notify await() so it can check again.
            notifyAll();
        }
    }

    /** {@inheritDoc} */
    @Override
    public void complete()
    {
        synchronized (this)
        {
            is_condition_met = true;
            // Notify await() so it can check again.
            notifyAll();
        }
    }

    /** @return Debug representation */
    @Override
    public String toString()
    {
        return "Wait for '" + device + "' "
                + comparison + " " + desired_value
                + ", tolerance=" + tolerance + ", timeout=" + timeout;
    }
}
