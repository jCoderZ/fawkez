/*
 * $Id: Java2Html.java 1238 2008-11-03 12:37:53Z amandel $
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
package org.jcoderz.phoenix.report;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.logging.Logger;

import javax.swing.text.Segment;

import org.gjt.sp.jedit.Mode;
import org.gjt.sp.jedit.syntax.DefaultTokenHandler;
import org.gjt.sp.jedit.syntax.ModeProvider;
import org.gjt.sp.jedit.syntax.ParserRuleSet;
import org.gjt.sp.jedit.syntax.Token;
import org.gjt.sp.jedit.syntax.TokenMarker;
import org.gjt.sp.jedit.syntax.TokenMarker.LineContext;
import org.jcoderz.commons.util.Assert;
import org.jcoderz.commons.util.IoUtil;


/**
 * Splits an input file into several tokens suitable for syntax 
 * highlighting.
 * This class encapsulates the access to the jEdit syntax 
 * highlighter package. No jEdit related classes should be
 * passed by interfaces of this class. 
 * 
 * @author Andreas Mandel
 */
public class Syntax
{
    private static final int MAX_RATIO_ILLEGAL_CHARACTERS = 10;
    private static final int MAX_AVERAGE_LINE_LENGTH = 200;
    private static final int BINARY_TEST_PROBE_CHARACTERS = 1024;
    private static final String CLASSNAME = Syntax.class.getName();
    private static final Logger LOGGER = Logger.getLogger(CLASSNAME);
    
    private final Charset mSourceCharset;
    private final int mTabWidth;
    private final char[] mFileContent;
    private int mFileContentPos;
    // CHECKME: ate tabs in the token counted to the length?
    private Token mToken = emptyToken();
    private int mCurrentLineNumber;
    private int mCurrentLinePos;
    private Segment mCurrentLine;
    private int mNumberOfLines;

    private final TokenMarker mTokenMarker;
    private final DefaultTokenHandler mTokenHandler 
        = new DefaultTokenHandler();
    private LineContext mLineContext = null;
    /** First line of the file. */
    private String mFirstLine; 
    
    static 
    {
        SyntaxModeCatalogHandler.loadModes();
    }

    /**
     * Initializes an Ascii2Html instance.
     * 
     * @param in the source file to read.
     * @param charSet the char set to use when reading the source file.
     *   If null the platform default char set will be used.  
     * @param tabWidth the tab width to use when calculating the cursor 
     *   position.
     * @throws IOException if a error occurs while reading the source file. 
     */
    public Syntax (File in, Charset charSet, int tabWidth)
        throws IOException
    {
        Assert.notNull(in, "in");
        mSourceCharset = charSet == null ? Charset.defaultCharset() : charSet;
        mTabWidth = tabWidth;
        mFileContent = readFile(in).toCharArray();
        mFileContentPos = 0;
        mCurrentLineNumber = 0;
        mCurrentLine = null;
        final Mode mode 
            = ModeProvider.instance.getModeForFile(in.getName(), mFirstLine);
        if (mode == null)
        {
            if (isBinary(in.getAbsolutePath(), mFileContent))
            {
                throw new RuntimeException("No html view for binary file '"
                    + in.getAbsolutePath() + "'.");
            }
            
            LOGGER.fine("Could not find mode file for '" + in.getName() 
                + "'. Is the jedit-syntax.jar on the classpath?");
            mTokenMarker = new TokenMarker();
            mTokenMarker.addRuleSet(new ParserRuleSet("text", "MAIN"));
        }
        else
        {
            mTokenMarker = mode.getTokenMarker();
        }
    }

    /**
     * Returns the number of lines of the parsed file. 
     * The value is available after creation of the class.
     * @return the number of lines of the parsed file.
     */
    public int getNumberOfLines ()
    {
        return mNumberOfLines;
    }
    
    /**
     * The line number of the currently parsed token.
     * Counting starts with line 1. Nevertheless prior the first call
     * to {@link #nextToken()} 0 is returned.
     * @return the line number of the currently parsed token.
     */
    public int getCurrentLineNumber ()
    {
        return mCurrentLineNumber;
    }
    
    /**
     * Returns the cursor position of start of the current token.
     * @return the cursor position of start of the current token.
     */
    public int getCurrentLinePos ()
    {
        return mCurrentLinePos;
    }
    
    /**
     * Returns the current token type as string. 
     * To be used as symbolic identifier of the token. Possible
     * return values can be fount in {@link Token#tokenToString(byte)}.
     * For the {@link Token#END} null is returned. 
     * @return the current token type as string. 
     */
    public String getCurrentTokenType ()
    {
        final String result;
        if (mToken.id == Token.END)
        {
            result = null;
        }
        else
        {
            result = Token.tokenToString(mToken.id);
        }
        return result;
    }

    /**
     * Returns the length of the current reported token.
     * @return the length of the current reported token.
     */
    public int getCurrentTokenLength ()
    {
        return mToken.length;
    }

    /**
     * Parses the next token and returns its textual content as string.
     * @return the textual content of the new token. 
     */
    public String nextToken ()
    {
        if (mCurrentLine == null 
            || mToken.id == Token.END)
        {
            nextLine();
        }
        else
        {
            mCurrentLinePos += mToken.length;
            mToken = mToken.next;
        }
        final String result;
        if (mCurrentLine.count == 0)
        {
            mToken = emptyToken();
            result = "";
        }
        else
        {
            if (mToken != null)
            {
                result 
                    = new String(mFileContent, 
                        mCurrentLine.offset + mToken.offset, mToken.length);
            }
            else
            {
                result = ""; 
                mToken = emptyToken();
            }
        }
        return result;
    }
    
    /**
     * Forward to next line. Takes care for different line ending styles.
     * Parsing for next line is started.
     */
    private void nextLine ()
    {
        if (mFileContentPos > mFileContent.length)
        {
            mCurrentLine = null; // END OF FILE
            mCurrentLineNumber = mNumberOfLines + 1;
        }
        else
        {
            int pos = mFileContentPos;
            while (pos < mFileContent.length
                && mFileContent[pos] != '\n'
                && mFileContent[pos] != '\r')
            {
                pos++;
            }
            final int currentLineEnd = pos;
            if (pos < mFileContent.length
                && (mFileContent[pos] == '\n'
                    || mFileContent[pos] == '\r'))
            {
                pos++;
            }
            if (pos < mFileContent.length
                && mFileContent[pos - 1] != mFileContent[pos]
                && (mFileContent[pos] == '\n'
                    || mFileContent[pos] == '\r'))
            {
                pos++;
            }
            mCurrentLine 
                = new Segment(mFileContent, 
                    mFileContentPos, currentLineEnd - mFileContentPos);
            mCurrentLineNumber++;
            mFileContentPos = pos;
            mCurrentLinePos = 1;
            
            if (mCurrentLine.count > 0)
            {
                mTokenHandler.init();
                mLineContext 
                    = mTokenMarker.markTokens(
                        mLineContext, mTokenHandler, mCurrentLine);
                mToken = mTokenHandler.getTokens();
            }
            else
            {
                mToken = emptyToken();
            }
        }
    }
    
    private String readFile (File in)
        throws IOException
    {
        String result = "";
        final FileInputStream fis = new FileInputStream(in);
        Reader reader = null;
        LineNumberReader lnr = null;
        try
        {   
            reader = new InputStreamReader(fis, mSourceCharset);
            lnr = new LineNumberReader(reader);
            result = IoUtil.readFully(lnr);
            mNumberOfLines = lnr.getLineNumber();
            mFirstLine 
                = new BufferedReader(new StringReader(result)).readLine();
        }
        finally
        {
            IoUtil.close(lnr);
            IoUtil.close(reader);
            IoUtil.close(fis);
        }
        return result;
    }
    
    private static Token emptyToken ()
    {
        return new Token(Token.END, 0, 0, null);
    }

    static boolean isBinary (String name, char[] fileContent)
    {
        int newLines = 0;
        int chars = 0;
        int illegal = 0;
        int i;
        for (i = 0; i < fileContent.length 
            && i < BINARY_TEST_PROBE_CHARACTERS; i++)
        {
            final char c = fileContent[i];
            if (c == '\n' || c == '\r')
            {
                newLines++;
            }
            else if (Character.isWhitespace(c))
            {
                chars++;
            }
            else if (Character.isISOControl(c))
            {
                illegal++;
            }
            else if (Character.isDefined(c))
            {
                chars++;
            }
            else
            {
                illegal++;
            }
        }
        boolean result = false; // assume a text file
        // less than a new line per 200 characters
        if (((newLines + 1) * MAX_AVERAGE_LINE_LENGTH) < i)
        {
            result = true;
        }
        // to many 'illegal' chars
        else if (illegal * MAX_RATIO_ILLEGAL_CHARACTERS > chars)
        {
            result = true;
        }
        LOGGER.finest("For file " + name + " tested " + i + " chars with "
            + newLines + " newlines, " + chars + " legal chars, "
            + illegal + " illegal chars. -> " 
            + (result ? "isBinary" : "isNotBinary"));
        return result;
    }

}
