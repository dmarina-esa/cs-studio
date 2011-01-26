/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archivereader.channelarchiver;

import org.csstudio.platform.data.ISeverity;

/** Implementation of the Severity interface for EPICS samples.
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class SeverityImpl implements ISeverity
{
	final private String text;
	final private boolean has_value;
	final private boolean txt_stat;
	
	public SeverityImpl(final String text, final boolean has_value,
	        final boolean txt_stat)
	{
		this.text = text;
		this.has_value = has_value;
		this.txt_stat = txt_stat;
	}

	@Override
    public String toString()
	{
		return text;
	}
    
    public boolean isOK()
    {
        return text.length() == 0
                || text.equals(ServerInfoRequest.NO_ALARM)
                || text.equals("NO_ALARM");
    }

    public boolean isMinor()
    {
        return text.equals("MINOR");
    }

    public boolean isMajor()
    {
        return text.equals("MAJOR");
    }

    public boolean isInvalid()
    {
        return !hasValue() || text.equals("INVALID");
    }

	public boolean hasValue()
	{
		return has_value;
	}

	public boolean statusIsText()
	{
		return txt_stat;
	}
}
