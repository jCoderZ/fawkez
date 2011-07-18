/*
 * $Id: Constants.java 1011 2008-06-16 17:57:36Z amandel $
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

import java.util.Locale;

/**
 * Utility class, keeps all jCoderZ wide constants.
 *
 * @author Andreas Mandel
 * @author Michael Griffel
 */
public final class Constants
{
   /** Name of the oracle driver class. */
   public static final String ORACLE_DRIVER_CLASS_NAME
         = "oracle.jdbc.OracleDriver";

   /** Number of bits per bytes. */
   public static final int BITS_PER_BYTE = 8;

   /** Number of bytes per int (integer). */
   public static final int BYTES_PER_INT = 4;

   /** Mask bytes. */
   public static final int BYTE_MASK = 0xFF;

   /** number of bits for an integer. */
   public static final int BITS_PER_INTEGER = BITS_PER_BYTE * BYTES_PER_INT;

   /** Number of bytes per kilo byte. */
   public static final int BYTES_PER_KILO_BYTE = 1024;

   /** space character. */
   public static final char SPACE_CHAR = ' ';

   /** tab character. */
   public static final char TAB_CHAR = '\t';

   /** line feed character. */
   public static final char LINE_FEED_CHAR = '\n';

   /** carriage return character. */
   public static final char CARRIAGE_RETURN_CHAR = '\r';

   /** The line separator. */
   public static final String LINE_SEPARATOR
         = System.getProperty("line.separator");

   /** The UTF-8 encoding String. */
   public static final String ENCODING_UTF8 = "UTF-8";

   /** The ASCII encoding String. */
   public static final String ENCODING_ASCII = "ASCII";

   /**
    * A central Locale to be used for string case conversions,
    * date/time conversions etc.
    * Used as a country independent locale for locale independent 
    * conversions.
    */
   public static final Locale SYSTEM_LOCALE = Locale.US;


   /** constants class, no public instances allowed. */
   private Constants ()
   {
      // constants class -- holds only constants
   }
}
