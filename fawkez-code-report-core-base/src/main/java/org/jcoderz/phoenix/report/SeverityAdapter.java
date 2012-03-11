/*
 * $Id: Severity.java 1336 2009-03-28 22:04:07Z amandel $
 *
 * Copyright 2012, The jCoderZ.org Project. All rights reserved.
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

import javax.xml.bind.annotation.adapters.XmlAdapter;


/**
 * Jaxb Adapter for the  Severity.
 *
 * @author generated via stylesheet
 */
public final class SeverityAdapter
      extends XmlAdapter<String, Severity>
{
  /**
   * Creates a Severity from its String
   * representation.
   * @param value a string holding the database representation of the
   *    Severity.
   * @return a Severity representing the
   *    given string.
   */
  public Severity unmarshal(String value)
  {
    return Severity.fromString(value);
  }

  /**
   * Creates a XML String representation of the given Severity.
   * @param value a string holding the xml representation of the
   *    Severity.
   * @return a String representing the
   *    given Severity.
   */
  public String marshal(Severity value)
  {
    return value == null ? null : value.toString();
  }

}
