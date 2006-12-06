package org.csstudio.utility.pv;

import org.csstudio.value.Value;

/** A control system PV.
 *  <p>
 *  When 'start'ed, the PV will attempt to connect or
 *  do whatever is needed to obtain the meta information like
 *  units, precision, ... Then it will subscribe to updates
 *  of the current value.
 *  
 *  @author Kay Kasemir
 */
public interface PV
{
    /** @return Returns the name. */
    public String getName();
    
    /** Add a new listener. */
    public void addListener(PVListener listener);

    /** Remove a listener. */
    public void removeListener(PVListener listener);

    /** Start the PV: connect, ... */
    public void start() throws Exception;
    
    /** @return Returns <code>true</code> between <code>start()</code> and <code>stop()</code>. */
    public boolean isRunning();

    /** @return Returns <code>true</code> when connected.
     *  While <code>isRunning</code>, we are subscribed for value updates,
     *  but we might still be disconnected, at least temporarily.
     */
    public boolean isConnected();
    
    /** Stop the PV: disconnect, ... */
    public void stop();

    /** Get the value.
     *  <p>
     *  This is the most recent value.
     *  Check isConnected() to see if this is valid,
     *  or use inside a PV listener's value update.
     *  
     *  @see PVListener
     *  @see #isConnected()
     *  @return Returns the most recent value. Might be <code>null</code>!
     */
    public Value getValue();

    /** Set PV to given value.
     *  Should accept Double, String, maybe more.
     */
    public void setValue(Object new_value);
}