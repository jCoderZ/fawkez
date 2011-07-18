/*
 * $Id: CategoryFilter.java 1011 2008-06-16 17:57:36Z amandel $
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


import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.jcoderz.commons.Category;

/**
 * This filter is used for filtering log messages according to the category.
 *
 */
public class CategoryFilter
      implements Filter
{
   private final Set mAllowedCategories;

   /**
    * Creates a new instance of this and sets the categories, which will
    * pass this filter. All categories within the supplied list must be
    * given in their textual representation.
    *
    * @param categories The list storing passable categories in their
    * textual representation.
    */
   public CategoryFilter (final List categories)
   {
      mAllowedCategories = new HashSet();
      if (categories != null)
      {
         for (final Iterator iter = categories.iterator(); iter.hasNext(); )
         {
            final Category cat
                  = Category.fromString((String) iter.next());
            mAllowedCategories.add(cat);
         }
      }
   }

   /** {@inheritDoc} */
   public boolean isPassable (LogItem entry)
   {
      return mAllowedCategories.contains(entry.getCategory());
   }
}
