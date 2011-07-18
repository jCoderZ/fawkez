/*
 * $Id: SerializableIterator.java 1011 2008-06-16 17:57:36Z amandel $
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
package org.jcoderz.commons.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Provides a serializable iterator over a collection (which may
 * be either a {@link java.util.Collection} or an array of Objects. The
 * {@link #remove()} operation is not supported in this implementation.
 *
 * NOTE: It is the caller's responsibility to ensure that all objects in
 * the arguments passed to the factory methods are serializable. The
 * implementation does not check this.
 *
 * @author Albrecht Messner
 */
public final class SerializableIterator
      implements Iterator, Serializable
{
   static final long serialVersionUID = 1L;

   private final List mItems;
   private int mNextIndex = 0;


   private SerializableIterator (Collection c)
   {
      mItems = new ArrayList();
      mItems.addAll(c);
   }

   /**
    * Construct a new iterator from the given collection. All elements
    * of the collection will be copied to an internal list.
    *
    * @param collection the collection for which an iterator should be
    *       created. All elements in the collection must be serializable.
    * @return a serializable iterator over the given collection
    */
   public static SerializableIterator fromCollection (Collection collection)
   {
      Assert.notNull(collection, "collection");
      return new SerializableIterator(collection);
   }

   /**
    * Construct a new iterator from the given array. All elements of
    * the array will be copied to an internal list.
    *
    * @param array the object array for which an iterator should be
    *       created. All elements in the array must be serializable.
    * @return a new iterator from the given array.
    */
   public static SerializableIterator fromArray (Object[] array)
   {
      Assert.notNull(array, "array");
      return new SerializableIterator(Arrays.asList(array));
   }

   /**
    * This operation is not supported in this implementation.
    * @throws UnsupportedOperationException whenever this method is called.
    * @see java.util.Iterator#remove()
    */
   public void remove ()
       throws UnsupportedOperationException
   {
      throw new UnsupportedOperationException(
            "Can't remove from a SerializableIterator");
   }

   /** {@inheritDoc} */
   public boolean hasNext ()
   {
      return mNextIndex < mItems.size();
   }

   /** {@inheritDoc} */
   public Object next ()
   {
      if (mNextIndex >= mItems.size())
      {
         throw new NoSuchElementException();
      }
      return mItems.get(mNextIndex++);
   }

   /** {@inheritDoc} */
   public String toString ()
   {
      return "[SerializableIterator: " + mItems + "]";
   }
}
