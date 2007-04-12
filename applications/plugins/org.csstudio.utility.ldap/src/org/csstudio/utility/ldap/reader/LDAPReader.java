/*
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchrotron,
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
package org.csstudio.utility.ldap.reader;

import java.util.ArrayList;
import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.csstudio.utility.ldap.Activator;
import org.csstudio.utility.ldap.preference.PreferenceConstants;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IPreferencesService;

public class LDAPReader extends Job {
	private boolean debug = false;
	private String name;
	private String filter;
	private int defaultScope=SearchControls.SUBTREE_SCOPE;
	private ArrayList<String> list;
	private ErgebnisListe ergebnisListe;
	private Hashtable<Object,String> env = new Hashtable<Object,String>(11);

	/**
	 * Used the connection settings from org.csstudio.utility.ldap.ui
	 * (used with UI)
	 *
	 * @param nameUFilter<br> 0: name<br>1: = filter<br>
	 * @param ergebnisListe
	 */
	public LDAPReader(String[] nameUFilter, ErgebnisListe ergebnisListe){
		super("LDAPReader");
		setBasics(nameUFilter[0], nameUFilter[1], ergebnisListe, getUIenv());
	}


	/**
	 * Used the connection settings from org.csstudio.utility.ldap.ui
	 * (used with UI)
	 *
	 * @param nameUFilter<br> 0: name<br>1: = filter<br>
	 * @param searchScope
	 * @param ergebnisListe
	 */

	public LDAPReader(String[] nameUFilter, int searchScope, ErgebnisListe ergebnisListe){

		super("LDAPReader");

		setBasics(nameUFilter[0], nameUFilter[1], ergebnisListe, getUIenv());
		setDefaultScope(searchScope);
	}

	/**
	 * Used the connection settings from org.csstudio.utility.ldap.ui
	 * (used with UI)
	 *
	 * @param name
	 * @param filter
	 * @param ergebnisListe
	 */
	public LDAPReader(String name, String filter, ErgebnisListe ergebnisListe){
		super("LDAPReader");
		setBasics(name, filter, ergebnisListe, getUIenv());
	}

	/**
	 * Used the connection settings from org.csstudio.utility.ldap.ui
	 * (used with UI)
	 *
	 * @param name
	 * @param filter
	 * @param searchScope
	 * @param ergebnisListe
	 */
	public LDAPReader(String name, String filter, int searchScope, ErgebnisListe ergebnisListe){
		super("LDAPReader");
		setBasics(name, filter, ergebnisListe, getUIenv());
		setDefaultScope(searchScope);
	}

	/**
	 * Need connection settings. (For Headless use)
	 *
	 * @param name
	 * @param filter
	 * @param searchScope
	 * @param ergebnisListe
	 * @param env connection settings.
	 * 	@see javax.naming.directory.DirContext;
	 * 	@see	javax.naming.Context;
	 */

	public LDAPReader(String name, String filter, int searchScope, ErgebnisListe ergebnisListe, Hashtable<Object,String> env){
		super("LDAPReader");
		setBasics(name, filter, ergebnisListe, env);
		setDefaultScope(searchScope);
	}
	/**
	 * Need connection settings. (For Headless use)
	 *
	 * @param name
	 * @param filter
	 * @param ergebnisListe
	 * @param env value for<br>
	 * 	0: Context.PROVIDER_URL<br>
	 *  1: Context.SECURITY_PROTOCOL<br>
	 *  2: Context.SECURITY_AUTHENTICATION<br>
	 *  3: Context.SECURITY_PRINCIPAL<br>
	 *  4: Context.SECURITY_CREDENTIALS<br>
	 *
	 * 	@see javax.naming.directory.DirContext;
	 * 	@see	javax.naming.Context;
	 */
	public LDAPReader(String name, String filter,  ErgebnisListe ergebnisListe, String[] env){
		super("LDAPReader");
		setBasics(name, filter, ergebnisListe, makeENV(env));
	}
	/**
	 *
	 * @param name
	 * @param filter
	 * @param searchScope
	 * @param ergebnisListe
	 * @param env value for<br>
	 * 	0: Context.PROVIDER_URL<br>
	 *  1: Context.SECURITY_PROTOCOL<br>
	 *  2: Context.SECURITY_AUTHENTICATION<br>
	 *  3: Context.SECURITY_PRINCIPAL<br>
	 *  4: Context.SECURITY_CREDENTIALS<br>
	 *
	 * 	@see javax.naming.directory.DirContext;
	 * 	@see	javax.naming.Context;
	 */
	public LDAPReader(String name, String filter, int searchScope, ErgebnisListe ergebnisListe, String[] env){
		super("LDAPReader");
		setBasics(name, filter, ergebnisListe, makeENV(env));
		setDefaultScope(searchScope);
	}


	/**
	 * @param name
	 * @param filter
	 * @param ergebnisListe
	 * @param env connection settings.
	 */
	private void setBasics(String name, String filter, ErgebnisListe ergebnisListe, Hashtable<Object, String> env) {
		this.ergebnisListe = ergebnisListe;
		this.name = name;
		this.filter = filter;
		this.env = env;
	}

	private DirContext initial() {
		// Create initial context
		try {
			InitialDirContext ctx = new InitialDirContext(env);
			return ctx;
		}catch (NamingException e) {
//			Activator.logException("Ungültiger LDAP Pfad", e);
			System.out.println("Ungültiger LDAP Pfad\r\n"+env);
			e.printStackTrace();
		}
		return null;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor ) {
		DirContext ctx;
		if((ctx = initial())!=null){
	        SearchControls ctrl = new SearchControls();
	        ctrl.setSearchScope(defaultScope);
	        try{
	        	list = new ArrayList<String>();
	            NamingEnumeration answer = ctx.search(name, filter, ctrl);
	            ctx.search(name, filter, ctrl);
				try {
					while(answer.hasMore()){
						String name = ((SearchResult)answer.next()).getName();
						list.add(name);
					}
				} catch (NamingException e) {
//					Activator.logException("LDAP Fehler", e);
					System.out.println("LDAP Fehler");
					e.printStackTrace();
				}
				answer.close();
				ctx.close();
				ergebnisListe.setAnswer(list);
				return ASYNC_FINISH;
			} catch (NamingException e) {
//				Activator.logException("Falscher LDAP Suchpfad.", e);
				System.out.println("Falscher LDAP Suchpfad.");
				e.printStackTrace();
			}
		}
		return Status.CANCEL_STATUS;
	}

	/**
	 * @param env
	 * @return
	 */
	private Hashtable<Object, String> makeENV(String[] env) {
		// Set up the environment for creating the initial context
		Hashtable<Object, String> tmpENV = new Hashtable<Object, String>(11);
		tmpENV.put(Context.INITIAL_CONTEXT_FACTORY,"com.sun.jndi.ldap.LdapCtxFactory"); //$NON-NLS-1$
		switch(env.length){
			default:
			case 5:
				tmpENV.put(Context.SECURITY_CREDENTIALS, env[4]);
			case 4:
				tmpENV.put(Context.SECURITY_PRINCIPAL, env[3]);
			case 3:
				tmpENV.put(Context.SECURITY_AUTHENTICATION, env[2]);
			case 2:
				tmpENV.put(Context.SECURITY_PROTOCOL, env[1]);
			case 1:
				tmpENV.put(Context.PROVIDER_URL, env[0]);
			case 0:
				break;
		}
		return tmpENV;
	}

	/**
	 * @return env with the setings from PreferencPage
	 */
	private Hashtable<Object, String> getUIenv() {

		IEclipsePreferences prefs = new DefaultScope().getNode(Activator.PLUGIN_ID);
		// Set up the environment for creating the initial context
		if(debug){
			System.out.println("Path: "+prefs.absolutePath());
			System.out.println("PLUGIN_ID: "+Activator.PLUGIN_ID);
			System.out.println("P_STRING_URL: "+prefs.get(PreferenceConstants.P_STRING_URL,"1"));
			System.out.println("SECURITY_PROTOCOL: "+prefs.get(PreferenceConstants.SECURITY_PROTOCOL,"2"));
			System.out.println("SECURITY_AUTHENTICATION: "+prefs.get(PreferenceConstants.SECURITY_AUTHENTICATION,"3"));
			System.out.println("P_STRING_USER_DN: "+prefs.get(PreferenceConstants.P_STRING_USER_DN,"4"));
			System.out.println("P_STRING_USER_PASSWORD: "+prefs.get(PreferenceConstants.P_STRING_USER_PASSWORD,"5"));
		}
		String[] env = null;
		// password
		if(prefs.get(PreferenceConstants.P_STRING_USER_PASSWORD,"").trim().length()>0){
			env = new String[5];
			env[4]=prefs.get(PreferenceConstants.P_STRING_USER_PASSWORD,"");
		}
		// user
		if(prefs.get(PreferenceConstants.P_STRING_USER_DN,"").trim().length()>0){
			if(env==null){
				env = new String[4];
			}
			env[3]=prefs.get(PreferenceConstants.P_STRING_USER_DN,"");
		}
		if(prefs.get(PreferenceConstants.SECURITY_AUTHENTICATION,"").trim().length()>0){
			if(env==null){
				env = new String[3];
			}
			env[2]=prefs.get(PreferenceConstants.SECURITY_AUTHENTICATION,"");
		}
		if(prefs.get(PreferenceConstants.SECURITY_PROTOCOL,"").trim().length()>0){
			if(env==null){
				env = new String[2];
			}
			env[1]=prefs.get(PreferenceConstants.SECURITY_PROTOCOL,"");
		}
		if(env==null){
			env = new String[1];
		}
		env[0]=prefs.get(PreferenceConstants.P_STRING_URL,"");

		return makeENV(env);
	}


	private void setDefaultScope(int defaultScope) {
		this.defaultScope = defaultScope;
	}
}
