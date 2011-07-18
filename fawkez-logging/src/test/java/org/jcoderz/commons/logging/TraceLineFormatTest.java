/*
 * $Id: LoggerUtil.java 1302 2009-03-23 21:08:18Z amandel $
 *
 * Copyright 2006, The jCoderZ.org Project. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 *    * Redistributions of source code must retain the above copyright
 *      notice, this list of conditions and the following disclaimer.
 *    * Redistributions in binary form must reproduce the above
 *      copyright notice, this list of conditions and the following
 *      disclaimer in the documentation and/or other materials
 *      provided with the distribution.
 *    * Neither the name of the jCoderZ.org Project nor the names of
 *      its contributors may be used to endorse or promote products
 *      derived from this software without specific prior written
 *      permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE REGENTS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE REGENTS AND CONTRIBUTORS
 * BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR
 * BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.jcoderz.commons.logging;

import junit.framework.TestCase;

/**
 * Test for {@link TraceLineFormat}.
 * @author Andreas Mandel
 */
public class TraceLineFormatTest
    extends TestCase
{
    public void testGetLogSourceNullNull ()
    {
        final TraceLineFormat format = new TraceLineFormat();
        format.setLogSource(null, null);
        assertEquals("Unexpected source class.", 
            "", format.getLogSource()[TraceLineFormat.SOURCECLASS_INDEX]);
        assertEquals("Unexpected source method.", 
            "", format.getLogSource()[TraceLineFormat.SOURCEMETHOD_INDEX]);
    }

    public void testGetLogSourceEmpty ()
    {
        final TraceLineFormat format = new TraceLineFormat();
        format.setLogSource("", "");
        assertEquals("Unexpected source class.", 
            "", format.getLogSource()[TraceLineFormat.SOURCECLASS_INDEX]);
        assertEquals("Unexpected source method.", 
            "", format.getLogSource()[TraceLineFormat.SOURCEMETHOD_INDEX]);
    }

    public void testGetLogSourceFull ()
    {
        final TraceLineFormat format = new TraceLineFormat();
        format.setLogSource("org.jcoderz.foo.Bar", "clean(Bar.java:512)");
        assertEquals("Unexpected source class for full source.", 
            "org.jcoderz.foo.Bar", 
            format.getLogSource()[TraceLineFormat.SOURCECLASS_INDEX]);
        assertEquals("Unexpected source method for full source.", 
            "clean(Bar.java:512)", 
            format.getLogSource()[TraceLineFormat.SOURCEMETHOD_INDEX]);
    }
}
