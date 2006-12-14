/* 
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchroton, 
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS. 
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED 
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND 
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE 
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, 
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR 
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE 
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR 
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE. 
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, 
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION, 
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS 
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY 
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
package org.csstudio.platform.libs.epics;

import org.csstudio.platform.libs.epics.preferences.PreferenceConstants;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Preferences;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;


/** The main plugin class to be used in the desktop.
 *  @author Original author unknown
 *  @author Kay Kasemir
  */
public class EpicsPlugin extends AbstractUIPlugin
{
	public static final String ID = "org.csstudio.platform.libs.epics"; //$NON-NLS-1$
    //The shared instance.
	private static EpicsPlugin plugin;
    private boolean use_pure_java;
	
	/** The constructor. */
	public EpicsPlugin()
    {
		plugin = this;
	}
    
    /** @return <code>true</code> if preferences suggest the use
     *  of pure java CA.
     */
    public boolean usePureJava()
    {   return use_pure_java; }

    /** Update the CAJ settings with the data from the
     *  preference page.
     *  <p>
     *  Unfortunately this only takes effect after a restart,
     *  the current setup seems to remain unaffected.
     */
	@SuppressWarnings("nls")
    public void installPreferences()
    {
        try
        {
            final Preferences prefs = getDefault().getPluginPreferences();
            use_pure_java = prefs.getBoolean(PreferenceConstants.constants[0]);
            // Set the 'CAJ' copy of the settings
            System.setProperty("com.cosylab.epics.caj.CAJContext.addr_list", 
                            prefs.getString(PreferenceConstants.constants[1]));
    		boolean yes_no = prefs.getBoolean(PreferenceConstants.constants[2]);
            System.setProperty("com.cosylab.epics.caj.CAJContext.auto_addr_list",
                            (yes_no ? "YES" : "NO")); 
    		System.setProperty("com.cosylab.epics.caj.CAJContext.connection_timeout",
                            prefs.getString(PreferenceConstants.constants[3]));
    		System.setProperty("com.cosylab.epics.caj.CAJContext.beacon_period", 
                            prefs.getString(PreferenceConstants.constants[4])); 
    		System.setProperty("com.cosylab.epics.caj.CAJContext.repeater_port",
                            prefs.getString(PreferenceConstants.constants[5]));
    		System.setProperty("com.cosylab.epics.caj.CAJContext.server_port", 
                            prefs.getString(PreferenceConstants.constants[6]));
    		System.setProperty("com.cosylab.epics.caj.CAJContext.max_array_bytes", 
                            prefs.getString(PreferenceConstants.constants[7]));

            // Set the 'JNI' copy of the settings
            System.setProperty("gov.aps.jca.jni.JNIContext.addr_list", 
                            prefs.getString(PreferenceConstants.constants[1]));
            System.setProperty("gov.aps.jca.jni.JNIContext.auto_addr_list",
                            (yes_no ? "YES" : "NO")); 
            System.setProperty("gov.aps.jca.jni.JNIContext.connection_timeout",
                            prefs.getString(PreferenceConstants.constants[3]));
            System.setProperty("gov.aps.jca.jni.JNIContext.beacon_period", 
                            prefs.getString(PreferenceConstants.constants[4])); 
            System.setProperty("gov.aps.jca.jni.JNIContext.repeater_port",
                            prefs.getString(PreferenceConstants.constants[5]));
            System.setProperty("gov.aps.jca.jni.JNIContext.server_port", 
                            prefs.getString(PreferenceConstants.constants[6]));
            System.setProperty("gov.aps.jca.jni.JNIContext.max_array_bytes", 
                            prefs.getString(PreferenceConstants.constants[7]));
        }
        catch (Exception e)
        {
            logException("Cannot set preferences", e);
        }
	}
	
    /** Add an exception to the plugin log. */
    public static void logException(String message, Exception e)
    {
        getDefault().log(IStatus.ERROR, message, e);
    }

    /** Add a message to the log.
     *  @param type
     *  @param message
     */
    private void log(int type, String message, Exception e)
    {
        getLog().log(new Status(type,
                                getClass().getName(),
                                IStatus.OK,
                                message,
                                e));
    }
    	
	/** This method is called upon plug-in activation */
	public void start(BundleContext context) throws Exception 
    {
		super.start(context);
		installPreferences();
	}

	/** This method is called when the plug-in is stopped */
	public void stop(BundleContext context) throws Exception
    {
		super.stop(context);
		plugin = null;
	}

	/** @return Returns the shared instance. */
	public static EpicsPlugin getDefault()
    {
		return plugin;
	}
}
