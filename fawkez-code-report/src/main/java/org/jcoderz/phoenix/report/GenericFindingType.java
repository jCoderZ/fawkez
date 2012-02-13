/*
 * $Id: CheckstyleFindingType.java 1173 2008-09-22 10:04:44Z amandel $
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

import java.io.Serializable;
import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.JAXBException;

import org.jcoderz.phoenix.report.GenericReportReader.SourceFile;
import org.jcoderz.phoenix.report.ftf.jaxb.FindingDescription;
import org.jcoderz.phoenix.report.jaxb.Item;
import org.jcoderz.phoenix.report.jaxb.ObjectFactory;

/**
 * Enumeration type for generic findings.
 * The description and patterns are read from a xml file.
 *
 * @author Andreas Mandel
 */
public final class GenericFindingType
      extends FindingType
{
   private final Pattern mPattern; 
   private final int mPriority;
   private final FindingDescription mFindingDescription;
   private final int mTextPos;
   private final int mLineStart;
   private final int mLineEnd;
   private final int mColumnStart;
   private final int mColumnEnd;
   private final int mSourceText;
   private final boolean mSourceColumnByCaret;
   private final Severity mSeverity;
   private final boolean mIsGlobal;
   
   private final ObjectFactory mOf = new ObjectFactory(); 
   
   // private final Severity mSeverity;

   /**
    * Create new finding type based on xml description.
    * @param root the definition of the root finding description. 
    * @param fd the definition of the detailed finding description. 
    */
   public GenericFindingType (
       FindingDescription root, FindingDescription fd)
    {
       super(fd.getSymbol(), fd.getShortDescription(),
           fd.getDescription());
   
       mPriority = fd.getPriority();
       mPattern = Pattern.compile(
           fd.getPattern(), Pattern.MULTILINE + Pattern.UNIX_LINES);
       mFindingDescription = fd;
       mTextPos = fd.isSetTextPos() ? Integer.parseInt(fd.getTextPos()) : -1;
       mLineStart = fd.isSetLineStartPos()
           ? Integer.parseInt(fd.getLineStartPos()) : -1;
       mLineEnd = fd.isSetLineEndPos()
           ? Integer.parseInt(fd.getLineEndPos()) : -1;
       if (fd.isSetColumnStartPos() && "caret".equals(fd.getColumnStartPos()))
       {
           mColumnStart = -1;
           mSourceColumnByCaret = true;
       }
       else
       {
           mColumnStart = fd.isSetColumnStartPos() 
               ? Integer.parseInt(fd.getColumnStartPos()) : -1;
           mSourceColumnByCaret = false;
       }
       mColumnEnd = fd.isSetColumnEndPos() 
           ? Integer.parseInt(fd.getColumnEndPos()) : -1;
       mSourceText = fd.isSetSourceTextPos() 
           ? Integer.parseInt(fd.getSourceTextPos()) : -1;
       mSeverity = fd.isSetSeverity() ? fd.getSeverity() : null;
       mIsGlobal = fd.isGlobal();
    }
   

   /**
    * Try to match the given method and fill the item 
    * accordingly if a match is found. 
    * @param sf the input source read. Allows to set the new file position.
    * @param message the message to parse.
    * @return a new Item with available data filled or null.
    * @throws JAXBException if Item creation fails on jaxb level.
    */
   public Item createItem (SourceFile sf, String message) throws JAXBException
   {
       Item result = null;
       final Matcher match = mPattern.matcher(message);
       if (match.lookingAt())
       {
           sf.setPos(sf.getPos() + match.end() + 1);
           result = mOf.createItem();
           result.setFindingType(getSymbol());
           if (mTextPos != -1)
           {
               result.setMessage(match.group(mTextPos));
           }
           else
           {
               result.setMessage(match.group());
           }
           if (mLineStart != -1)
           {
               result.setLine(Integer.parseInt(match.group(mLineStart)));
           }
           if (mLineEnd != -1)
           {
               result.setEndLine(Integer.parseInt(match.group(mLineEnd)));
           }
           if (mColumnStart != -1)
           {
               result.setColumn(Integer.parseInt(match.group(mColumnStart)));
           }
           if (mColumnEnd != -1)
           {
               result.setEndColumn(Integer.parseInt(match.group(mColumnEnd)));
           }
           if (mSourceText != -1)
           {
               result.setSourceText(match.group(mSourceText));
           }
           if (mSeverity != null)
           {
               result.setSeverity(mSeverity);
           }
           if (mFindingDescription.isGlobal())
           {
               result.setGlobal(true);
           }
       }
       return result;
   }
   
   /** @return the severity assigned to findings of this type by default. */
   public Severity getSeverity ()
   {
       return mSeverity;
   }

   /**
    * The priority used to match for this finding in relation to
    * other findings of this type. 
    * The higher the value the higher is the priority of this pattern.
    * A catch all pattern like "(.*)" should therefore get a low 
    * number (eg. {@link Integer#MIN_VALUE}) as priority.
    * Default priority is 0.  
    */
   private int getPriority ()
   {
       return mPriority;
   }
   
   /**
    * @return the sourceColumnByCaret
    */
    public boolean isSourceColumnByCaret ()
    {
        return mSourceColumnByCaret;
    }


    /**
     * @return the isGlobal
     */
    public boolean isGlobal ()
    {
        return mIsGlobal;
    }
    
    
   /**
    * Init of the enum.
    */
   public static void initialize ()
   {
       // already done
   }
   
   /**
    * Class to sort {@link GenericFindingType}s by their priority.
    */
   public static class OrderByPriority 
       implements Comparator<GenericFindingType>, Serializable
   {
       private static final long serialVersionUID = 1L;

       /** {@inheritDoc} */
       public int compare (GenericFindingType o1, GenericFindingType o2)
       {
            final int result;
            if (o1.getPriority() > o2.getPriority())
            {
                result = -1;
            }
            else if (o1.getPriority() < o2.getPriority())
            {
                result = 1;
            }
            else
            {
                result = o1.getSymbol().compareTo(o2.getSymbol());
            }
            return result;
        }
   }

}
