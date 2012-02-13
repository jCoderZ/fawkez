/*
 * $Id: FileSummary.java 1336 2009-03-28 22:04:07Z amandel $
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
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Comparator;
import java.util.Iterator;

import org.jcoderz.commons.util.Assert;
import org.jcoderz.commons.util.StringUtil;
import org.jcoderz.phoenix.report.jaxb.File;
import org.jcoderz.phoenix.report.jaxb.Item;

/**
 * This class encapsulates all finding information collected
 * for a file or a group of files.
 *
 * <p>This class also allows to perform the magic quality
 * calculation for the data collected in the summary.</p>
 *
 * @author Andreas Mandel
 */
public final class FileSummary
        implements Comparable<FileSummary>
{
    /** Constant used for initial string buffer size. */
    private static final int STRING_BUFFER_SIZE = 256;

    /** Constant for percentage calculation 1% = 1 / MAX_PERCENTAGE. */
    private static final int MAX_PERCENTAGE = 100;

    private static final float MAX_PERCENTAGE_FLOAT = 100;

    private final NumberFormat mCoveragePercantageFormatter =
        new DecimalFormat("##0.00");

    /** Counts the number of files added up in this summary. */
    private int mFiles = 0;

    /** Lines of code in the file. */
    private int mLinesOfCode;

    /**
     * Lines of code in the file that contain coverage information
     * that is not 0.
     */
    private int mCoveredLinesOfCode;

    /**
     * Holds the number of violations for each severity level.
     */
    private int[] mViolations = new int[Severity.VALUES.size()];

    /**
     * Percentage values for the violations.
     * Data stored in here is only valid if <code>mPercentUpToDate</code>
     * is true.
     */
    private int[] mPercent = new int[Severity.VALUES.size()];
    private boolean mPercentUpToDate = false;

    private final String mClassName;
    private final String mPackage;
    private final String mDetailedFile;
    private boolean mCoverageData;

    /**
     * Creates a new empty file summary object used to summarize
     * findings for classes in all packages.
     */
    public FileSummary ()
    {
        this ("Global Summary", "all", null, 0, false);
    }

    /**
     * Creates a new empty file summary object used to summarize
     * findings for classes in in the given package.
     *
     * @param packagename name of the package where this summary
     *      is used for.
     */
    public FileSummary (String packagename)
    {
        this ("Package Summary", packagename, null, 0, false);
    }

    /**
     * Creates a new empty file summary object used to summarize
     * findings for the given class in the given package with
     * link to the file and code information.
     * @param className the name of the class (without package
     *      information).
     * @param packagename the name of the package where the class
     *      resides in.
     * @param reportfile the name of the file where the html report
     *      stored.
     * @param linesOfCode the number of lines in the file.
     * @param withCoverage true if coverage information is
     *      available.
     */
    public FileSummary (String className, String packagename,
        String reportfile, int linesOfCode, boolean withCoverage)
    {
        mClassName = className;
        mPackage = packagename;
        mDetailedFile = reportfile;
        mLinesOfCode = linesOfCode;
        mCoverageData = withCoverage;
    }

    /**
     * Calculates the quality as percentage represented as float.
     * @param loc total number of lines of code. This is also the maximum
     *         that might be returned by this method.
     * @param violations the array holding the violations of the severity
     *      related to the position in the array. The elements of the
     *      array are NOT modified.
     * @return the quality as percentage represented as float.
     */
    public static float calculateQuality (int loc, int[] violations)
    {
        float quality = 0;
        if (loc > 0)
        {
            quality = calcUnweightedQuality(loc, violations);
            quality = (quality * MAX_PERCENTAGE) / loc;
        }
        return quality;
    }

    /**
     * Calculates the unweighed quality points scored for the code.
     * Maximum returned is <code>loc</code> the minimum is <code>0</code>.
     * @param loc total number of lines of code. This is also the maximum
     *         that might be returned by this method.
     * @param violations the array holding the violations of the severity
     *      related to the position in the array. The elements of the
     *      array are NOT modified.
     * @return the unweighed quality score.
     */
    private static int calcUnweightedQuality (int loc, int[] violations)
    {
        Assert.assertEquals(
            "Violations array length must match number of severities.",
            Severity.VALUES.size(), violations.length);
        int quality = loc * Severity.PENALTY_SCALE; // lines of code
        for (int i = 0; i < Severity.VALUES.size() && quality > 0; i++)
        {
            // not covered lines are bad this
            // not files with no coverage test at all get no penalty here!
            quality -= violations[i] * Severity.fromInt(i).getPenalty();
        }
        if (quality < 0)
        {
            quality = 0;
        }
        else
        {
            quality /= Severity.PENALTY_SCALE;
        }
        return quality;
    }

    /**
     * Calculates the quality percentage scored for the code.
     * Maximum returned is <code>100</code> the minimum is <code>0</code>.
     * @param loc total number of lines of code. This is also the maximum
     *         that might be returned by this method.
     * @param info number of info level findings.
     * @param warning number of warning level findings.
     * @param error number of error level findings.
     * @param coverage number of coverage level findings.
     * @param filtered number of filtered level findings.
     * @param codestyle number of codestyle level findings.
     * @param design number of design level findings.
     * @param cpd number of cpd level findings.
     * @return the unweighed quality score.
     */
    public static float calculateQuality (int loc, int info, int warning,
        int error, int coverage, int filtered, int codestyle, int design,
        int cpd)
    {
        final int[] violations = new int[Severity.VALUES.size()];
        violations[Severity.INFO.toInt()] = info;
        violations[Severity.COVERAGE.toInt()] = coverage;
        violations[Severity.WARNING.toInt()] = warning;
        violations[Severity.ERROR.toInt()] = error;
        violations[Severity.FILTERED.toInt()] = filtered;
        violations[Severity.CODE_STYLE.toInt()] = codestyle;
        violations[Severity.DESIGN.toInt()] = design;
        violations[Severity.CPD.toInt()] = cpd;
        return FileSummary.calculateQuality(loc, violations);
    }


    /** @return the name of the class (without package information). */
    public String getClassName ()
    {
        return mClassName;
    }

    /** @return the name of the package. */
    public String getPackage ()
    {
        return mPackage;
    }

    /** @return the number of files summarized in this file summary. */
    public int getNumberOfFiles ()
    {
        return mFiles;
    }

    /** {@inheritDoc} */
    public String toString ()
    {
        final StringBuilder result = new StringBuilder();
        calcPercent();

        result.append(getFullClassName());
        result.append("{ LOC:");
        result.append(mLinesOfCode);
        result.append('(');
        result.append(mViolations[Severity.OK.toInt()]);
        result.append("%)");
        final Iterator<Severity> i = Severity.VALUES.iterator();
        while (i.hasNext())
        {
            final Severity s = i.next();
            if (mViolations[s.toInt()] != 0)
            {
                result.append(", ");
                result.append(s.toString());
                result.append(':');
                result.append(mViolations[s.toInt()]);
                result.append('(');
                result.append(mPercent[s.toInt()]);
                result.append("%)");
            }
        }
        result.append('}');
        return result.toString();
    }

    /**
     * Add the counters of an other FileSummary to this one.
     * @param other the FileSummary be added.
     */
    public void add (FileSummary other)
    {
        for (int i = 0; i < mViolations.length; i++)
        {
            mViolations[i] += other.mViolations[i];
        }
        mLinesOfCode += other.mLinesOfCode;
        mCoveredLinesOfCode += other.mCoveredLinesOfCode;
        mPercentUpToDate = false;
        mFiles++;
        if (mCoverageData || other.isWithCoverage()
            || mCoveredLinesOfCode > 0
            || getNotCoveredLinesOfCode() > 0)
        {
            mCoverageData = true;
        }
    }

    /**
     * Adds the counters from the given file to this summary.
     * @param file the data to be added.
     */
    public void add (File file)
    {
        mFiles++;
        mLinesOfCode += file.getLoc();
        final Iterator<Item> i = file.getItem().iterator();
        while (i.hasNext())
        {
            final Item item = i.next();
            final Severity severity = item.getSeverity();
            addViolation(severity);
        }
    }

    /**
     * Returns true if this summary contains coverage data.
     * @return true if this summary contains coverage data.
     */
    public boolean isWithCoverage ()
    {
        return mCoverageData;
    }

    /** Increments the counter of covered lines. */
    public void addCoveredLine ()
    {
        mPercentUpToDate = false;
        mCoveredLinesOfCode++;
    }

    /**
     * Increments the counter for the given severity in this summary.
     * @param severity the severity of the counter to be incremented.
     */
    public void addViolation (Severity severity)
    {
        Assert.notNull(severity, "severity");
        mPercentUpToDate = false;
        mViolations[severity.toInt()]++;
    }

    /**
     * @return the full class name including package declaration.
     */
    public String getFullClassName ()
    {
        final String fullClassName;
        if (StringUtil.isEmptyOrNull(mPackage))
        {
            fullClassName = mClassName;
        }
        else
        {
            fullClassName = mPackage + "." + mClassName;
        }
        return fullClassName;
    }

    /** @return the report file associated to this FileSummary. */
    public String getHtmlLink ()
    {
        return mDetailedFile;
    }

    /** @return the number of lines of code. */
    public int getLinesOfCode ()
    {
        return mLinesOfCode;
    }

    /**
     * Returns the magic quality as percentage int.
     * The maximum quality code gets a score of 100. The lowest score
     * possible is 0.
     * @return the magic quality as percentage int (0-100).
     */
    public int getQuality ()
    {
        int quality = 0;
        if (mLinesOfCode > 0)
        {
            quality = calcUnweightedQuality(mLinesOfCode, mViolations);
            quality = (quality * MAX_PERCENTAGE) / mLinesOfCode;
        }
        return quality;
    }

    /**
     * Returns the magic quality as percentage float.
     * The maximum quality code gets a score of 100. The lowest score
     * possible is 0.
     * @return the magic quality as percentage float (0.0-100.0).
     */
    public float getQualityAsFloat ()
    {
        // might be we should cache the result?
        return FileSummary.calculateQuality (mLinesOfCode, mViolations);
    }

    /**
     * Generates a string containing xhtml code that renders to a
     * percentage bar that can be used as component of a web page.
     * @return a string containing xhtml.
     */
    public String getPercentBar ()
    {
        calcPercent();
        final StringBuilder sb = new StringBuilder(STRING_BUFFER_SIZE);
        sb.append("<table width='100%' cellspacing='0' cellpadding='0' "
            + "summary='quality-bar'><tr valign='middle'>");
        for (int i = Severity.OK.toInt(); i < Severity.MAX_SEVERITY_INT; i++)
        {
            if (mPercent[i] > 0)
            {
                sb.append("<td class='");
                sb.append(Severity.fromInt(i).toString());
                sb.append("' width='");
                sb.append(mPercent[i]);
                sb.append("%' height='10'></td>");
            }
        }
        sb.append("</tr></table>");
        return sb.toString();
    }

    /**
     * Generates a string containing xhtml code that renders to a
     * bar that can be used as component of a web page to represent
     * the amount of covered code.
     * @return a string containing xhtml.
     */
    public String getCoverageBar ()
    {
        final int notCovered = MAX_PERCENTAGE - getCoverage();

        final StringBuilder sb = new StringBuilder(STRING_BUFFER_SIZE);
        sb.append("<table width='100%' cellspacing='0' cellpadding='0' "
            + "summary='coverage-bar'><tr valign='middle'>");
        if (notCovered < MAX_PERCENTAGE)
        {
            sb.append("<td class='ok' width='");
            sb.append(MAX_PERCENTAGE - notCovered);
            sb.append("%' height='10'></td>");
        }
        if (notCovered != 0)
        {
            sb.append("<td class='error' width='");
            sb.append(notCovered);
            sb.append("%' height='10'></td></tr>");
        }
        sb.append("</tr></table>");
        return sb.toString();
    }

    /**
     * Returns the number of violations for the given severity.
     * @param severity the severity to check.
     * @return the number of violations for the given severity
     */
    public int getViolations (Severity severity)
    {
        return mViolations[severity.toInt()];
    }

    /**
     * Get the coverage percentage in double precision.
     * @return the coverage percentage in double precision.
     */
    public float getCoverageAsFloat ()
    {
        final float allLinesOfCode
            = getNotCoveredLinesOfCode() + mCoveredLinesOfCode;
        float result;
        if (allLinesOfCode != 0)
        {
            result = mCoveredLinesOfCode / allLinesOfCode;
        }
        else if (getNotCoveredLinesOfCode() > 0)
        {
            result = 0;
        }
        else // no coverage at all (might be interface...
        {
            result = 1;
        }
        return result * MAX_PERCENTAGE_FLOAT;
    }

    /**
     * Returns the coverage as user string.
     * @return the coverage as user string.
     */
    public String getCoverageAsString ()
    {
        return mCoveragePercantageFormatter.format(getCoverageAsFloat()) + "%";
    }

    /** @return the coverage percentage as int. */
    public int getCoverage ()
    {
        final int notCoveredLinesOfCode = getNotCoveredLinesOfCode();

        int notCovered;
        if (mCoveredLinesOfCode != 0)
        {
            notCovered = (notCoveredLinesOfCode * MAX_PERCENTAGE)
                    / (mCoveredLinesOfCode + notCoveredLinesOfCode);
            if ((notCovered == 0) && (notCoveredLinesOfCode > 0))
            {  // below 1% -> round up to 1%
                notCovered = 1;
            }
        }
        else if (notCoveredLinesOfCode > 0)
        {
            notCovered = MAX_PERCENTAGE;
        }
        else // no coverage at all (might be interface...
        {
            notCovered = 0;
        }
        return MAX_PERCENTAGE - notCovered;
    }

    /**
     * @return the number of not covered lines of code.
     */
    public int getNotCoveredLinesOfCode ()
    {
        return mViolations[Severity.COVERAGE.toInt()];
    }

    /**
     * All findings that are between {@link Severity#INFO} and
     * {@link Severity#ERROR} but not {@link Severity#COVERAGE}
     * are counted.
     * @return the number of violations summed up in this summary.
     */
    public int getNumberOfFindings ()
    {
        int sum = 0;
        for (int i = Severity.INFO.toInt(); i <= Severity.ERROR.toInt(); i++)
        {
            if (i != Severity.COVERAGE.toInt())
            {
                sum += mViolations[i];
            }
        }
        return sum;
    }

    /** {@inheritDoc} */
    public int compareTo (FileSummary o)
    {
        int result = 0;
        if (mPackage != null)
        {
            result = mPackage.compareTo((o).mPackage);
        }
        if (result == 0)
        {
            if (getClassName() != null)
            {
                result = getClassName().compareTo(o.getClassName());
            }
        }
        return result;
    }


    private void calcPercent ()
    {
        if (!mPercentUpToDate)
        {
            doCalcPercent();
        }
    }

    private void doCalcPercent ()
    {
        int remainingPercentage = MAX_PERCENTAGE;
        // errors
        if (mLinesOfCode != 0)
        {
            for (int i = Severity.ERROR.toInt(); i > Severity.INFO.toInt();
                    i--)
            {
                int percent;

                if (i == Severity.COVERAGE.toInt())
                {
                    percent = calcPercentCoverage();
                }
                else
                {
                    percent = calcPercentage(
                        mViolations[i] * Severity.fromInt(i).getPenalty(),
                        mLinesOfCode * Severity.PENALTY_SCALE);
                }
                // do not round to 0.
                if (mViolations[i] > 0 && percent == 0)
                {
                    percent = 1;
                }
                if (percent > remainingPercentage)
                {
                    percent = remainingPercentage;
                }
                mPercent[i] = percent;
                remainingPercentage -= percent;
            }
        }
        else
        {
            for (int i = Severity.ERROR.toInt(); i > Severity.INFO.toInt(); i--)
            {
                mPercent[i] = 0;
            }
        }
        mPercent[Severity.OK.toInt()] = remainingPercentage;
        mPercentUpToDate = true;
    }

    /**
     * Calculates the penalty percentage of the coverage tests.
     */
    private int calcPercentCoverage ()
    {
        final int coverageViolationPercentage;
        if (!mCoverageData)
        {
            coverageViolationPercentage = 0;
        }
        else
        {
            final int notCoveredLines
                = getNotCoveredLinesOfCode();
            coverageViolationPercentage = calcPercentage(
                notCoveredLines * Severity.COVERAGE.getPenalty(),
                Severity.PENALTY_SCALE
                * (mCoveredLinesOfCode + notCoveredLines));
        }
        return coverageViolationPercentage;
    }

    private static int calcPercentage (int part, int all)
    {
        final int result;
        if (all == 0)
        {
            result = 0;
        }
        else
        {
            result = part * MAX_PERCENTAGE / all;
        }
        return result;
    }


    /**
     * Comparator that allows to sort the FileSummary by name of the package.
     * @author Andreas Mandel
     */
    static final class SortByPackage
        implements Comparator<FileSummary>, Serializable
    {
        private static final long serialVersionUID = 2244367340241672131L;

        public int compare (FileSummary o1, FileSummary o2)
        {
            return o1.compareTo(o2);
        }
    }

    /**
     * Comparator that allows to sort the FileSummary by quality.
     * @author Andreas Mandel
     */
    static final class SortByQuality
        implements Comparator<FileSummary>, Serializable
    {
        private static final long serialVersionUID = 1718175789352629538L;

        public int compare (FileSummary o1, FileSummary o2)
        {
            int result;
            final float qualityA = o1.getQualityAsFloat();
            final float qualityB = o2.getQualityAsFloat();
            if (qualityA < qualityB)
            {
                result = -1;
            }
            else if (qualityA > qualityB)
            {
                result = 1;
            }
            else
            {
                result = o1.compareTo(o2);
            }
            return result;
        }
    }

    /**
     * Comparator that allows to sort the FileSummary by coverage.
     * @author Andreas Mandel
     */
    static final class SortByCoverage
        implements Comparator<FileSummary>, Serializable
    {
        private static final long serialVersionUID = -4275903074787742250L;

        public int compare (FileSummary a, FileSummary b)
        {
            final float coverA = a.getCoverageAsFloat();
            final float coverB = b.getCoverageAsFloat();

            final int result;
            if (coverA < coverB)
            {
                result = -1;
            }
            else if (coverA > coverB)
            {
                result = 1;
            }
            else if (a.getNotCoveredLinesOfCode() > b.getNotCoveredLinesOfCode())
            {
                result = -1;
            }
            else if (a.getNotCoveredLinesOfCode() < b.getNotCoveredLinesOfCode())
            {
                result = 1;
            }
            else
            {
                result = a.compareTo(b);
            }
            return result;
        }
    }
}
