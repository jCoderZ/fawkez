/*
 * $Id: PeriodFilter.java 1011 2008-06-16 17:57:36Z amandel $
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

import org.jcoderz.commons.types.Period;

/**
 *
 * This class implements the timestamp period filter.
 * The method {@linkplain #isPassable(LogItem)} returns <code>true</code> if
 * the timestamp of the given {@link org.jcoderz.commons.logging.LogItem entry}
 * falls within of a period including in this filter.
 *
 */
public class PeriodFilter
      implements Filter
{
   /**
    * All periods to be filtered.
    */
   private final Period [] mPeriods;

   /**
    * Constructor.
    * @param periods The array of periods to be used as the filter criteria.
    */
   public PeriodFilter (final Period [] periods)
   {
      mPeriods = periods;
   }

   /** {@inheritDoc} */
   public boolean isPassable (LogItem entry)
   {
      boolean result = true;
      if (mPeriods.length > 0)
      {
         result = isIncluded(entry);
      }
      return result;
   }

   /**
    *
    * @param entry The entry to test.
    * @return true if the timestamp of supplied entry falls within of a range
    * period used by this filter.
    */
   private boolean isIncluded (LogItem entry)
   {
      boolean result = false;
      for (int i = 0; i < mPeriods.length; i++)
      {
         if (mPeriods[i].isIncluded(entry.getTimestamp()))
         {
            result = true;
            break;
         }
      }
      return result;
   }

}
