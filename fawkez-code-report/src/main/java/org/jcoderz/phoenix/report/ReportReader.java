/*
 * $Id: ReportReader.java 627 2008-03-16 11:11:43Z amandel $
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

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBException;

import org.jcoderz.phoenix.report.jaxb.Item;

/**
 * Common Interface for all Report Readers.
 *
 * @author Michael Griffel
 */
public interface ReportReader
{
   /**
    * Parses the report file/directory.
    *
    * @param f input XML report or directory.
    * @throws JAXBException if an JAXB exception occures.
    * @throws FileNotFoundException if the given file does not exists.
    */
   void parse (File f)
      throws JAXBException, FileNotFoundException;

   /**
    * Merges the items of the input report as a Map of filename string and of
    * the type Item (org.jcoderz.phoenix.report.jaxb.Item) into the
    * given item Map.
    *
    * @param items the items that should be merged with the report's
    *        input items.
    * @throws JAXBException if an JAXB exception occurs.
    */
   void merge (Map<ResourceInfo, List<Item>> items)
      throws JAXBException;
}
