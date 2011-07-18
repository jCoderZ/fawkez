/*
 * $Id: Base64Util.java 1011 2008-06-16 17:57:36Z amandel $
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

import java.util.Arrays;

import org.jcoderz.commons.ArgumentMalformedException;


/**
 * This class provides encode/decode for RFC 2045 Base64 as
 * defined by RFC 2045, N. Freed and N. Borenstein.
 * RFC 2045: Multipurpose Internet Mail Extensions (MIME)
 * Part One: Format of Internet Message Bodies. Reference
 * 1996 Available at: http://www.ietf.org/rfc/rfc2045.txt
 * This class is used by XML Schema binary format validation
 *
 * This implementation does not encode/decode streaming
 * data. You need the data that you will encode/decode
 * already on a byte array.
 *
 * @author Michael Griffel
 *
 * TODO: remove deep copy of decoded Base64 data in case of padding chars.
 */
public final class Base64Util
{
   private static final String ENCODED_PARAMETER = "encoded";
   private static final int LOWER_SIX_BITS = 0x3f;
   private static final int BASELENGTH = 255;
   private static final int BITS_PER_BASE64_CHAR = 6;
   private static final int FOURBYTE = 4;
   private static final int BYTES_PER_BASE64_CHUNK = 3;
   private static final int TWENTYFOURBITGROUP = 3 * Constants.BITS_PER_BYTE;
   private static final char PAD = '=';
   private static final char[] LOOKUP_BASE64_ALPHABET
         = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/"
            .toCharArray();
   private static final byte[] BASE64_ALPHABET = new byte[BASELENGTH];

   static
   {
      Arrays.fill(BASE64_ALPHABET, (byte) -1);
      for (int i = 0; i < LOOKUP_BASE64_ALPHABET.length; i++)
      {
         BASE64_ALPHABET[LOOKUP_BASE64_ALPHABET[i]] = (byte) i;
      }
   }

   private Base64Util ()
   {
      // no instances allowed - only static methods
   }

   /**
    * Encodes hex octets into Base64.
    *
    * @param binaryData Array containing binary data.
    * @return Encoded Base64 array
    */
   public static char[] encodeToChars (byte[] binaryData)
   {
      final char[] result;
      if (binaryData == null)
      {
         result = null;
      }
      else if (binaryData.length == 0)
      {
         result = new char[0];
      }
      else
      {
         final int dataBits = binaryData.length * Constants.BITS_PER_BYTE;
         final int remainingBits = dataBits % TWENTYFOURBITGROUP;
         final int numberTriplets = dataBits / TWENTYFOURBITGROUP;
         final int numberQuartet = remainingBits != 0 ? numberTriplets + 1
               : numberTriplets;

         final char [] encodedData = new char[numberQuartet * FOURBYTE];
         int encodedIndex = 0;
         int dataIndex = 0;
         for (int i = 0; i < numberTriplets; i++)
         {
            //     b1       b2       b3
            // +---------+---------+---------+
            // |765432 10|7654 3210|76 543210| = x
            // +--------16---------8---------+
            // |      |       |       |      |
            //  ^^^^^^ ^^^^^^^ ^^^^^^^ ^^^^^^
            //    d1     d2      d3      d4
            final int x
                  = (binaryData[dataIndex++] & Constants.BYTE_MASK)
                           << (2 * Constants.BITS_PER_BYTE)             // b1
                  | (binaryData[dataIndex++] & Constants.BYTE_MASK)
                           << Constants.BITS_PER_BYTE                   // b2
                  | (binaryData[dataIndex++] & Constants.BYTE_MASK);    // b3

            encodedData[encodedIndex++] = LOOKUP_BASE64_ALPHABET        // d1
                  [(x >>> (3 * BITS_PER_BASE64_CHAR)) & LOWER_SIX_BITS];
            encodedData[encodedIndex++] = LOOKUP_BASE64_ALPHABET        // d2
                  [(x >>> (2 * BITS_PER_BASE64_CHAR)) & LOWER_SIX_BITS];
            encodedData[encodedIndex++] = LOOKUP_BASE64_ALPHABET        // d3
                  [(x >>> BITS_PER_BASE64_CHAR) & LOWER_SIX_BITS];
            encodedData[encodedIndex++] = LOOKUP_BASE64_ALPHABET        // d4
                  [x & LOWER_SIX_BITS];
         }
         // two bytes left
         if (remainingBits == 2 * Constants.BITS_PER_BYTE)
         {
            //      b2       b3
            // +---------+---------+
            // |765432 10|7654 3210| = x
            // +---------8---------+
            // |      |       |       |  pad |
            //  ^^^^^^ ^^^^^^^ ^^^^^^^ ^^^^^^
            //    d1     d2      d3      d4
            final int x
                  = (binaryData[dataIndex++] & Constants.BYTE_MASK)
                     << Constants.BITS_PER_BYTE                         // b2
                  | (binaryData[dataIndex++] & Constants.BYTE_MASK);    // b3

            encodedData[encodedIndex++] = LOOKUP_BASE64_ALPHABET        // d1
                  [x >>> 10 & LOWER_SIX_BITS];
            encodedData[encodedIndex++] = LOOKUP_BASE64_ALPHABET        // d2
                  [x >>> 4 & LOWER_SIX_BITS];
            encodedData[encodedIndex++] = LOOKUP_BASE64_ALPHABET        // d3
                  [x << 2 & LOWER_SIX_BITS];
            encodedData[encodedIndex++] = PAD;                          // d4
         }
         // one byte left
         else if (remainingBits == Constants.BITS_PER_BYTE)
         {
            //     b3
            // +---------+
            // |765432 10| = x
            // +---------+
            // |      |      | pad  | pad  |
            //  ^^^^^^ ^^^^^^ ^^^^^^ ^^^^^^
            //    d1     d2     d3     d4
            final int x
                  = (binaryData[dataIndex++] & Constants.BYTE_MASK);    // b3

            encodedData[encodedIndex++] = LOOKUP_BASE64_ALPHABET        // d1
                  [(x >>> 2) & LOWER_SIX_BITS];
            encodedData[encodedIndex++] = LOOKUP_BASE64_ALPHABET        // d2
                  [(x << 4) & LOWER_SIX_BITS];
            encodedData[encodedIndex++] = PAD;                          // d3
            encodedData[encodedIndex++] = PAD;                          // d4
         }
         result = encodedData;
      }
      return result;
   }

   /**
    * Encodes hex octets into Base64.
    *
    * @param binaryData Array containing binary data.
    * @return Encoded Base64 string.
    */
   public static String encode (byte[] binaryData)
   {
      return new String(encodeToChars(binaryData));
   }

   /**
    * Encodes hex octets into Base64.
    * The encoded characters are written to the given string
    * buffer <tt>sb</tt>.
    *
    * @param sb the string buffer that is used to write the
    *       Base64 characters to.
    * @param binaryData Array containing binary data.
    */
   public static void appendEncoded (StringBuffer sb, byte[] binaryData)
   {
      sb.append(encodeToChars(binaryData));
   }

   /**
    * Decodes Base64 data into octets.
    *
    * @param encoded Base64 encoded string.
    * @return an array containing decoded data.
    * @throws ArgumentMalformedException if the given string is not
    *       Base64 encoded.
    */
   public static byte[] decode (String encoded)
         throws ArgumentMalformedException
   {
      Assert.notNull(encoded, ENCODED_PARAMETER);
      final byte[] result;

      if (encoded.length() % FOURBYTE != 0)
      {
         throw new ArgumentMalformedException(ENCODED_PARAMETER, encoded,
               "Base64 length must be a multiple of " + FOURBYTE);
      }
      final char[] base64Data = encoded.toCharArray();
      final int numberQuadruple = base64Data.length / FOURBYTE;

      if (numberQuadruple == 0)
      {
         throw new ArgumentMalformedException(ENCODED_PARAMETER, encoded,
               "Base64 length " + base64Data.length + " must be at least "
                  + FOURBYTE + " bytes");
      }

      byte b1 = 0, b2 = 0, b3 = 0, b4 = 0;
      int encodedIndex = 0;
      int dataIndex = 0;
      final byte[] decodedData
            = new byte[(numberQuadruple) * BYTES_PER_BASE64_CHUNK];
      final int pureBase64Chunks = numberQuadruple - 1;

      for (int i = 0; i < pureBase64Chunks; i++)
      {
         b1 = base64AlphabetLookup(base64Data[dataIndex++]);
         b2 = base64AlphabetLookup(base64Data[dataIndex++]);
         b3 = base64AlphabetLookup(base64Data[dataIndex++]);
         b4 = base64AlphabetLookup(base64Data[dataIndex++]);
         //      b1       b2       b3       b4
         // +---------+---------+---------+--------+
         // |00 543210|0054 3210|005432 10|00543210|
         // +---------+---------+---------+--------+
         //    |^^^^^^   ^^|^^^^   ^^^^|^^   ^^^^^^|
         //         d1          d2          d3
         decodedData[encodedIndex++] = (byte) (b1 << 2 | b2 >> 4);      // d1
         decodedData[encodedIndex++] = (byte) (b2 << 4 | b3 >> 2);      // d2
         decodedData[encodedIndex++] = (byte) (b3 << 6 | b4);           // d3
      }

      // read last chunk
      b1 = base64AlphabetLookup(base64Data[dataIndex++]);
      b2 = base64AlphabetLookup(base64Data[dataIndex++]);
      final char beforeLastChar = base64Data[dataIndex++];
      final char lastChar = base64Data[dataIndex++];

      if (isData((beforeLastChar)) && isData((lastChar))) //No PAD e.g 3cQl
      {
         //      b1       b2       b3       b4
         // +---------+---------+---------+--------+
         // |00 543210|0054 3210|005432 10|00543210|
         // +---------+---------+---------+--------+
         //    |^^^^^^   ^^|^^^^   ^^^^|^^   ^^^^^^|
         //         d1          d2          d3
         b3 = BASE64_ALPHABET[beforeLastChar];
         b4 = BASE64_ALPHABET[lastChar];
         decodedData[encodedIndex++] = (byte) (b1 << 2 | b2 >> 4);      // d1
         decodedData[encodedIndex++] = (byte) (b2 << 4 | b3 >> 2);      // d2
         decodedData[encodedIndex++] = (byte) (b3 << 6 | b4);           // d3
         result = decodedData;
      }
      else
      {
         final int decodedDataLength = encodedIndex;
         // Check if they are PAD character(s)
         if (isPad(beforeLastChar) && isPad(lastChar))
         {
            // Two PAD e.g. 3c[Pad][Pad]
            assertLastFourBitsZero(encoded, b2);
            final byte[] tmp = new byte[decodedDataLength + 1];
            System.arraycopy(decodedData, 0, tmp, 0, decodedDataLength);
            tmp[encodedIndex] = (byte) (b1 << 2 | b2 >> 4);
            result = tmp;
         }
         else if (isData(beforeLastChar) && isPad(lastChar))
         {
            // One PAD  e.g. 3cQ[Pad]
            b3 = BASE64_ALPHABET[beforeLastChar];
            assertLastTwoBitsZero(encoded, b3);
            final byte[] tmp = new byte[decodedDataLength + 2];
            System.arraycopy(decodedData, 0, tmp, 0, decodedDataLength);
            tmp[encodedIndex++] = (byte) (b1 << 2 | b2 >> 4);
            tmp[encodedIndex]   = (byte) (b2 << 4 | b3 >> 2);
            result = tmp;
         }
         else
         {
            // an error  like "3c[Pad]r", "3cdX", "3cXd", "3cXX"
            // where X is non data
            throw new ArgumentMalformedException(ENCODED_PARAMETER, encoded,
                  "At least one of the last 2 characters '"
                  + new StringBuffer().append(beforeLastChar).append(lastChar)
                  + "' are not a valid Base64 [padding] character");
         }
      }
      return result;
   }


   private static void assertLastFourBitsZero (String encoded, byte b)
   {
      if ((b & 0xf) != 0) // last 4 bits should be zero
      {
         throw new ArgumentMalformedException(ENCODED_PARAMETER, encoded,
               "Last 4 bits should be zero of the last "
               + "non-padding character '"
               + Integer.toHexString(b) + "'");
      }
   }

   private static void assertLastTwoBitsZero (String encoded, byte b)
   {
      if ((b & 0x3) != 0) // last 2 bits should be zero
      {
         throw new ArgumentMalformedException(ENCODED_PARAMETER, encoded,
               "Last 2 bits should be zero of the last "
               + "non-padding character '"
               + Integer.toHexString(b) + "'");
      }
   }

   private static byte base64AlphabetLookup (char octect)
   {
      if (!isData(octect))
      {
         throw new ArgumentMalformedException("octect",
               Character.toString(octect),
               "Illegal Base64 character '" + octect + "'");
      }
      return BASE64_ALPHABET[octect];
   }

   private static boolean isPad (char octect)
   {
      return (octect == PAD);
   }

   private static boolean isData (char octect)
   {
      return (BASE64_ALPHABET[octect] != -1);
   }
}
