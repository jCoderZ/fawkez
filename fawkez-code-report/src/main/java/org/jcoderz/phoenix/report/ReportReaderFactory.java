/*
 * $Id: ReportReaderFactory.java 1454 2009-05-10 11:06:43Z amandel $
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

import javax.xml.bind.JAXBException;

import org.jcoderz.phoenix.report.ReportNormalizer.SourceReport;

/**
 * Factory class to create a report reader for the requested report format.
 *
 * @author Michael Griffel
 */
public final class ReportReaderFactory
{
   /** Constructor. */
    private ReportReaderFactory ()
    {
        // No instances allowed -- only static methods
    }

    /**
     * Creates a report reader for the given report format.
     *
     * @param sr the source report format.
     * @return a report reader for the given report format.
     */
    public static ReportReader createReader (SourceReport sr)
    {
        final ReportFormat format = sr.getReportFormat();
        final ReportReader result;
        try
        {
            if (ReportFormat.CHECKSTYLE == format)
            {
                result = new CheckstyleReportReader();
            }
            else if (ReportFormat.FINDBUGS == format)
            {
                result = new FindBugsReportReader();
            }
            else if (ReportFormat.JCOVERAGE == format)
            {
                result = new JCoverageReportReader();
            }
            else if (ReportFormat.COBERTURA == format)
            {
                result = new CoberturaReportReader();
            }
            else if (ReportFormat.PMD == format)
            {
                result = new PmdReportReader();
            }
            else if (ReportFormat.CPD == format)
            {
                result = new CpdReportReader();
            }
            else if (ReportFormat.SOURCE_DIRECTORY == format)
            {
                result = new SourceDirectoryReader();
            }
            else if (ReportFormat.EMMA == format)
            {
                result = new EmmaReportReader();
            }
            else if (ReportFormat.GENERIC == format)
            {
                result = GenericReportReader.initialize(
                    Origin.fromString(sr.getFlavor()));
            }
            else if (ReportFormat.JCODERZ == format)
            {
                throw new UnsupportedOperationException(
                        "jcoderz report not supported.");
            }
            else
            {
                throw new UnsupportedOperationException(
                        "Unsupported report format: " + format);
            }
        }
        catch (JAXBException e)
        {
            throw new RuntimeException(e);
        }
        return result;
    }
}
