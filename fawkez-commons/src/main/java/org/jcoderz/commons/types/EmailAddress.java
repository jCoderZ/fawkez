/*
 * $Id: EmailAddress.java 1404 2009-04-14 12:34:34Z amandel $
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
package org.jcoderz.commons.types;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jcoderz.commons.ArgumentMalformedException;
import org.jcoderz.commons.util.Assert;
import org.jcoderz.commons.util.HashCodeUtil;
import org.jcoderz.commons.util.StringUtil;

/**
 * This class represents an email addresses compliant to RFC 2822,
 * chapter 3.4.1. Addr-spec specification.
 * This class does not support obsolete addressing (see ch. 4.4.
 * Obsolete Addressing).
 *
 * <pre>
 * atext           =       ALPHA / DIGIT / ; Any character except controls,
 *                         "!" / "#" /     ;  SP, and specials.
 *                         "$" / "%" /     ;  Used for atoms
 *                         "&" / "'" /
 *                         "*" / "+" /
 *                         "-" / "/" /
 *                         "=" / "?" /
 *                         "^" / "_" /
 *                         "`" / "{" /
 *                         "|" / "}" /
 *                         "~"
 * atom            =       [CFWS] 1*atext [CFWS]
 * dot-atom        =       [CFWS] dot-atom-text [CFWS]
 * dot-atom-text   =       1*atext *("." 1*atext)
 * addr-spec       = local-part "@" domain
 * local-part      = dot-atom / quoted-string
 * qtext           =       NO-WS-CTL /     ; Non white space controls
 *                         %d33 /          ; The rest of the US-ASCII
 *                         %d35-91 /       ;  characters not including "\"
 *                         %d93-126        ;  or the quote character
 * qcontent        =       qtext / quoted-pair
 * quoted-string   = [CFWS]
 *                   DQUOTE *([FWS] qcontent) [FWS] DQUOTE
 *                   [CFWS]
 * domain          = dot-atom / domain-literal
 * domain-literal  = [CFWS] "[" *([FWS] dcontent) [FWS] "]" [CFWS]
 * dcontent        = dtext / quoted-pair
 * dtext           = NO-WS-CTL /     ; Non white space controls
 *                   %d33-90 /       ; The rest of the US-ASCII
 *                   %d94-126        ;  characters not including "[",
 *                                   ;  "]", or "\"
 * FWS             =       ([*WSP CRLF] 1*WSP) /   ; Folding white space
 *                         obs-FWS
 * ctext           =       NO-WS-CTL /     ; Non white space controls
 *                         %d33-39 /       ; The rest of the US-ASCII
 *                         %d42-91 /       ;  characters not including "(",
 *                         %d93-126        ;  ")", or "\"
 * ccontent        =       ctext / quoted-pair / comment
 * comment         =       "(" *([FWS] ccontent) [FWS] ")"
 * CFWS            =       *([FWS] comment) (([FWS] comment) / FWS)
 * </pre>
 *
 * <p>The maximum length of an email address is: 
 * 64+1+255 characters (local-part + @ + domain).
 * The minimum length of an email address is: 
 * 1+1+4 characters (local-part + @ + domain).</p>
 *
 * <p>A valid list of top-level domains is defined by the IANA. A top-level
 * domain which is not part of the
 * <a href="http://data.iana.org/TLD/tlds-alpha-by-domain.txt">official list</a>
 * will be rejected.</p>
 *
 * @author Michael Rumpf
 */
public class EmailAddress
  implements Serializable
{
  private static final long serialVersionUID = 1L;

  private static final int MAX_LENGTH_LOCAL_PART = 64;
  private static final int MAX_LENGTH_DOMAIN = 255;

  // RFC 2822 token definitions for a valid email
  private static final String SP = "!#$%&'*+-/=?^_`{|}~";
  private static final String ATEXT = "[a-zA-Z0-9" + SP + "]";
  private static final String ATOM = ATEXT + "+";

  // one or more atext chars
  private static final String DOT_ATOM = "\\." + ATOM;
  // one atom followed by 0 or more dotAtoms.
  private static final String LOCAL_PART = ATOM + "(" + DOT_ATOM + ")*";

  // RFC 1035 tokens for domain names:
  private static final String LETTER = "[a-zA-Z]";
  private static final String LET_DIG = "[a-zA-Z0-9]";
  private static final String LET_DIG_HYP = "[a-zA-Z0-9-]";
  private static final String RFC_LABEL 
      = LET_DIG + LET_DIG_HYP + "{0,61}" + LET_DIG;
  private static final String DOMAIN 
      = RFC_LABEL + "(\\." + RFC_LABEL + ")*\\." + LETTER + "{2,6}";

  //Combined together, these form the allowed email regexp allowed by RFC 2822:
  private static final String ADDRESS = "^" + LOCAL_PART + "@" + DOMAIN + "$";

  private static final Pattern ADDRESS_PATTERN = Pattern.compile(ADDRESS);


  private final String mLocalPart;
  private final String mDomain;
  private final String mTopLevelDomain;

  /**
   * This is the constructor of the EmailAddress type.
   *
   * @param email The email address.
   */
  public EmailAddress (String email)
  {
    Assert.notNull(email, "email");
    final String mail = email.trim();
    final Matcher matcher = ADDRESS_PATTERN.matcher(email);
    if (!matcher.matches())
    {
      throw new ArgumentMalformedException("email", email,
          "EMail pattern does not match the RFC2822 grammar!");
    }

    final int at = mail.indexOf('@');
    if (at > MAX_LENGTH_LOCAL_PART)
    {
      throw new ArgumentMalformedException("email", email,
          "The local part is longer than " + MAX_LENGTH_LOCAL_PART 
          + " characters!");
    }
    if (mail.length() - at - 1 > MAX_LENGTH_DOMAIN)
    {
      throw new ArgumentMalformedException("email", email,
          "The domain is longer than " + MAX_LENGTH_DOMAIN 
          + " characters!");
    }
    mLocalPart = mail.substring(0, at);
    mDomain = mail.substring(at + 1);

    final int dot = mail.lastIndexOf('.');
    mTopLevelDomain = mail.substring(dot + 1);
  }

  /**
   * Factory method for converting a String into an instance of type 
   * EmailAddress.
   *
   * @param email the email address to parse
   * @return An instance of type EmailAddress
   */
  public static EmailAddress fromString(String email)
  {
    return new EmailAddress(email);
  }

  /**
   * Returns the local part of the address.
   * @return the local part of the address.
   */
  public String getName ()
  {
    return mLocalPart;
  }


  /**
   * Returns the domain name of the address.
   * @return the domain name of the address.
   */
  public String getDomain ()
  {
    return mDomain;
  }


  /**
   * Returns the top-level domain name of the address.
   * @return the top-level domain name of the address.
   */
  public String getTopLevelDomain ()
  {
    return mTopLevelDomain;
  }


  /**
   * Returns the full email address.
   * @return the full email address.
   */
  public String getAddress ()
  {
    return mLocalPart + "@" + mDomain;
  }

  /**
   * Returns the full email address.
   * @return the full email address.
   */
  public String toString ()
  {
    return mLocalPart + "@" + mDomain;
  }

  /**
   * Returns the true if this email address equals the other.
   * @param obj the object to compare to.
   * @return the true if this email address equals the other.
   * @see java.lang.Object#equals(java.lang.Object)
   */
  public boolean equals (Object obj)
  {
    boolean result = false;
    if (this == obj)
    {
      result = true;
    }
    else if (obj instanceof EmailAddress)
    {
      final EmailAddress other = (EmailAddress) obj;
      if (StringUtil.equals(getDomain(), other.getDomain())
          && StringUtil.equals(getName(), other.getName()))
      {
        result = true;
      }
    }
    return result;
  }

  /**
   * Returns the hash code for this email.
   * @return the hash code for this email.
   */
  public int hashCode ()
  {
    int hashCode = HashCodeUtil.hash(HashCodeUtil.SEED, mLocalPart);
    return HashCodeUtil.hash(hashCode, mDomain);
  }
}
