/*
 * $Id: StatisticCollector.java 1454 2009-05-10 11:06:43Z amandel $
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

import java.awt.Color;
import java.awt.Dimension;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.Map.Entry;

import net.sourceforge.chart2d.Chart2DProperties;
import net.sourceforge.chart2d.Dataset;
import net.sourceforge.chart2d.GraphChart2DProperties;
import net.sourceforge.chart2d.GraphProperties;
import net.sourceforge.chart2d.LBChart2D;
import net.sourceforge.chart2d.LegendProperties;
import net.sourceforge.chart2d.MultiColorsProperties;
import net.sourceforge.chart2d.Object2DProperties;

import org.jcoderz.commons.util.IoUtil;
import org.jcoderz.commons.util.ObjectUtil;
import org.jcoderz.phoenix.report.jaxb.File;
import org.jcoderz.phoenix.report.jaxb.Report;

/**
 * TODO: Extend this class to run historic tool on existing reports.
 *
 * @author Andreas Mandel
 */
public final class StatisticCollector
{
   private static final int MAX_SERVICE_PACKAGES = 15;
   private static final int ROW_ERROR = 0;
   private static final int ROW_CPD = 1;
   private static final int ROW_WARNING = 2;
   private static final int ROW_DESIGN = 3;
   private static final int ROW_CODE_STYLE = 4;
   private static final int ROW_INFO = 5;
   private static final int ROW_COVERAGE = 6;
   private static final int PERCENT = 100;
   private static final int LARGE_IMAGE_WIDTH = 1000;
   private static final int LARGE_IMAGE_HEIGHT = 600;
   private static final int SMALL_IMAGE_WIDTH = 800;
   private static final int SMALL_IMAGE_HEIGHT = 300;
   private static final Dimension LARGE_SIZE
         = new Dimension(LARGE_IMAGE_WIDTH, LARGE_IMAGE_HEIGHT);
   private static final Dimension SMALL_SIZE
         = new Dimension(SMALL_IMAGE_WIDTH, SMALL_IMAGE_HEIGHT);

   /** The report to be used as input. */
   private final Report mReport;

   /** The package prefix to check for. */
   private final String mPrefix;

   /** The dir to be used as output. */
   private final java.io.File mOutDir;

   /** The timestamp when the report was initiated. */
   private final String mTimestamp;


   /**
    * Creates a new StatisticCollector object.
    *
    * @param report the input report.
    * @param outDir the output dir for the charts and summary xml.
    * @param timestamp the timestamp for the summary db entry.
    */
   public StatisticCollector (Report report, java.io.File outDir,
         String timestamp)
   {
      mReport = report;
      // TODO: Try to find this automagically, 2 - 3 packagelevels
      mPrefix = "org.jcoderz.";
      mOutDir = outDir;
      mTimestamp = timestamp;
   }

   /**
    * Creates a new StatisticCollector object.
    *
    * @param report the input report.
    * @param prefix the package prefix
    * @param outDir the output dir for the charts and summary xml.
    * @param timestamp the timestamp for the summary db entry.
    */
   public StatisticCollector (Report report, String prefix, java.io.File outDir,
         String timestamp)
   {
      mReport = report;
      mPrefix = prefix;
      mOutDir = outDir;
      mTimestamp = timestamp;
   }

   /**
    * Creates the charts.
    * @throws IOException if the image can not be written.
    */
   public void createCharts ()
         throws IOException
   {
      // chart should contain x axis with packages,
      // y axis with number of lines
      // (2 lines 1 for production code one for test code)

      final Map<String, FileSummary> productionPackages
          = new HashMap<String, FileSummary>();
      final Map<String, FileSummary> testPackages
          = new HashMap<String, FileSummary>();
      final Map<String, FileSummary> allPackages
          = new HashMap<String, FileSummary>();

      summarize(productionPackages, testPackages, allPackages);

      writeSummary(allPackages);

      allPackages.clear();  // make GC happy

      final Map<String, FileSummary> production;
      final Map<String, FileSummary> test;
      final String level;

      if (productionPackages.size() > MAX_SERVICE_PACKAGES)
      {
         production = generateServiceLevelMap(productionPackages);
         test = generateServiceLevelMap(testPackages);
         level = "Service";
      }
      else
      {
         production = productionPackages;
         test = testPackages;
         level = "Package";
      }

      createLocChart(production, test, level);

      createQualityChart(production, level);
   }

   /**
    * Writes a summary xml for the given summary map.
    * @param packages a map with the package names / summaries to be
    *       used.
    * @throws IOException if the XML file can not be written
    */
   private void writeSummary (Map<String, FileSummary> packages)
         throws IOException
   {
      final StringBuffer sb = new StringBuffer();


      final FileSummary all = new FileSummary();
      final Iterator<FileSummary> i = packages.values().iterator();

      while (i.hasNext())
      {
         all.add(i.next());
      }

      sb.append("<findingsummary ");
      fillSummaryLine(sb, all);
      sb.append(">\n");

      sb.append("   <packagelevelxml>\n");
      for (Entry<String, FileSummary> entry : packages.entrySet())
      {
         final String pkg = entry.getKey();
         final FileSummary summary = entry.getValue();
         sb.append("      <package name='");
         sb.append(pkg);
         sb.append("' ");
         fillSummaryLine(sb, summary);
         sb.append("/>\n");
      }
      sb.append("   </packagelevelxml>\n");


      final Map<String, FileSummary> serviceLevelMap
          = generateServiceLevelMap(packages);


      sb.append("   <servicelevelxml>\n");
      for (Entry<String, FileSummary> service : serviceLevelMap.entrySet())
      {
         final String pkg = service.getKey();
         final FileSummary summary = service.getValue();
         sb.append("      <service name='");
         sb.append(pkg);
         sb.append("' ");
         fillSummaryLine(sb, summary);
         sb.append("/>\n");
      }
      sb.append("   </servicelevelxml>\n");

      sb.append("</findingsummary>\n");

      FileWriter w = null;
      try
      {
         final java.io.File out = new java.io.File(mOutDir, "summary.xml");
         w = new FileWriter(out);
         w.write(sb.toString());
      }
      finally
      {
          IoUtil.close(w);
      }
   }

   private Map<String, FileSummary> generateServiceLevelMap (
       Map<String, FileSummary> packages)
   {
      final Map<String, FileSummary> serviceLevelMap
          = new HashMap<String, FileSummary>();

      for (Entry<String, FileSummary> packagz : packages.entrySet())
      {
         final String pkg = packagz.getKey();
         final FileSummary summary = packagz.getValue();
         final String service = getService(pkg);

         FileSummary serviceSummary = serviceLevelMap.get(service);
         if (serviceSummary == null)
         {
            serviceSummary = new FileSummary(service);
            serviceLevelMap.put(service, serviceSummary);
         }
         serviceSummary.add(summary);
      }
      return serviceLevelMap;
   }

   private void fillSummaryLine (final StringBuffer sb, FileSummary summary)
   {
      sb.append("timestamp='");
      sb.append(mTimestamp);
      sb.append("' ");
      for (int i = 0; i < Severity.VALUES.size(); i++)
      {
          final Severity currentSeverity = Severity.fromInt(i);
          sb.append(currentSeverity.toString());
          sb.append("='");
          sb.append(summary.getViolations(currentSeverity));
          sb.append("' ");
      }
      sb.append(" loc='");
      sb.append(summary.getLinesOfCode());
      sb.append("' codeLoc='");
      sb.append(summary.getCoverage()
          + summary.getViolations(Severity.COVERAGE));
      sb.append("' quality='");
      sb.append(summary.getQualityAsFloat());
      sb.append('\'');
   }

   private String getService (String pkg)
   {
      String result;
      if (pkg != null && pkg.startsWith(mPrefix))
      {
         result = pkg.substring(mPrefix.length());
         if (result.indexOf('.') != -1)
         {
            result = result.substring(result.indexOf('.') + 1);
         }
         if (result.indexOf('.') != -1)
         {
            result = result.substring(0, result.indexOf('.'));
         }
      }
      else
      {
         result = ObjectUtil.toStringOrEmpty(pkg);
      }
      return result;
   }

   /**
    * Collects summary data and puts the results in the given maps.
    * @param productionPackages map to collect production code data.
    * @param testPackages map to collect test code data.
    * @param all All packages.
    */
   private void summarize (final Map<String, FileSummary> productionPackages,
         final Map<String, FileSummary> testPackages,
         final Map<String, FileSummary> all)
   {
      final List<File> allFiles = mReport.getFile();
      for (final File currentFile : allFiles)
      {
         // Level 3 is currently given to test classes, below 3 is production
         if (currentFile.getLevel().equals(ReportLevel.TEST))
         {
            addToMap(testPackages, currentFile);
            addToMap(all, currentFile);
         }
         else
         {
            addToMap(productionPackages, currentFile);
            addToMap(all, currentFile);
         }
      }
   }

   private FileSummary addToMap (final Map<String, FileSummary> map,
       final File file)
   {
      final String pkg = file.getPackage();
      FileSummary counter = map.get(pkg);
      if (counter == null)
      {
         counter = new FileSummary(pkg);
         map.put(pkg, counter);
      }
      calculateSummary(file, counter);
      return counter;
   }

   /**
    * Collects finding summary for a concrete file.
    * @param currentFile the file structure.
    * @param counter the counter structure.
    */
   private void calculateSummary (File currentFile, FileSummary counter)
   {
       counter.add(currentFile);
   }

   /**
    * Creates the lines of code chart.
    * Locs are painted per package. Separated by test and production code.
    * The output is a "loc.png".
    *
    * @throws IOException if the image can not be written.
    */
   private void createLocChart (Map<String, FileSummary> src,
       Map<String, FileSummary> test, String level)
         throws IOException
   {
     //<-- Begin Chart2D configuration -->

     //Configure object properties
     final Object2DProperties object2DProps = new Object2DProperties();
     object2DProps.setObjectTitleText ("LOC by " + level);

     //Configure chart properties
     final Chart2DProperties chart2DProps = new Chart2DProperties();
     chart2DProps.setChartDataLabelsPrecision (1);

     //Configure legend properties
     final LegendProperties legendProps = new LegendProperties();
     final String[] legendLabels = {"Production", "Test"};
     legendProps.setLegendLabelsTexts (legendLabels);

     //Configure graph chart properties
     final GraphChart2DProperties graphChart2DProps
             = new GraphChart2DProperties();

     final Set<String> labels = new TreeSet<String>(src.keySet());
     labels.addAll(test.keySet());

     if (labels.size() == 0)
     {
        throw new RuntimeException("No packages found for chart!");
     }

     final String [] labelsLongAxisLabels
           = labels.toArray(new String[]{});
     final String [] labelsAxisLabels
           = cutPackages(labelsLongAxisLabels);

     graphChart2DProps.setLabelsAxisLabelsTexts(labelsAxisLabels);
     graphChart2DProps.setLabelsAxisTitleText(level + " Name");
     graphChart2DProps.setNumbersAxisTitleText("LOC");
     graphChart2DProps.setLabelsAxisTicksAlignment(
           GraphChart2DProperties.CENTERED);

     //Configure graph properties
     final GraphProperties graphProps = new GraphProperties();
     graphProps.setGraphBarsExistence(false);
     graphProps.setGraphDotsExistence(true);
     graphProps.setGraphAllowComponentAlignment(true);
     graphProps.setGraphDotsWithinCategoryOverlapRatio(1);

     //Configure dataset
     final Dataset dataset = new Dataset (legendLabels.length,
           labelsAxisLabels.length, 1);

     // fill data....
     for (int j = 0; j < dataset.getNumCats(); ++j)
     {
        dataset.set(0, j, 0, getCounter(src, labelsLongAxisLabels[j]));
        dataset.set(1, j, 0, getCounter(test, labelsLongAxisLabels[j]));
     }

     //Configure graph component colors
     final MultiColorsProperties multiColorsProps
         = new MultiColorsProperties();

     //Configure chart
     final LBChart2D chart2D = new LBChart2D();
     chart2D.setObject2DProperties (object2DProps);
     chart2D.setChart2DProperties (chart2DProps);
     chart2D.setLegendProperties (legendProps);
     chart2D.setGraphChart2DProperties (graphChart2DProps);
     chart2D.addGraphProperties (graphProps);
     chart2D.addDataset (dataset);
     chart2D.addMultiColorsProperties (multiColorsProps);

     //<-- End Chart2D configuration -->


     chart2D.setMaximumSize(LARGE_SIZE);
     chart2D.setPreferredSize(LARGE_SIZE);

     if (chart2D.validate(false))
     {
        java.io.File file = new java.io.File(mOutDir, "loc_large.png");
        javax.imageio.ImageIO.write(chart2D.getImage(), "PNG", file);
        chart2D.setMaximumSize(SMALL_SIZE);
        chart2D.setPreferredSize(SMALL_SIZE);
        chart2D.pack();
        file = new java.io.File(mOutDir, "loc_small.png");
        javax.imageio.ImageIO.write(chart2D.getImage(), "PNG", file);
     }
     else
     {
        chart2D.validate(true);
     }
   }

   /**
    * Creates the quality chart.
    * The output is a "quality.png".
    *
    * @throws IOException if the image can not be written.
    */
   private void createQualityChart (Map<String, FileSummary> src, String level)
         throws IOException
   {
     //<-- Begin Chart2D configuration -->

     //Configure object properties
     final Object2DProperties object2DProps = new Object2DProperties();
     object2DProps.setObjectTitleText ("Quality by " + level);

     //Configure chart properties
     final Chart2DProperties chart2DProps = new Chart2DProperties();
     chart2DProps.setChartDataLabelsPrecision(0);

     //Configure legend properties
     final LegendProperties legendProps = new LegendProperties();
     final String[] legendLabels = {
         "Error", "C&P", "Warning", "Design", "Code Style",
         "Info", "Coverage"};
     legendProps.setLegendLabelsTexts (legendLabels);

     //Configure graph chart properties
     final GraphChart2DProperties graphChart2DProps
             = new GraphChart2DProperties();

     final Set<String> labels = new TreeSet<String>(src.keySet());

     if (labels.size() == 0)
     {
        throw new RuntimeException("No packages found for chart!");
     }

     final String [] labelsLongAxisLabels
           = labels.toArray(new String[labels.size()]);
     final String [] labelsAxisLabels = cutPackages(labelsLongAxisLabels);

     graphChart2DProps.setLabelsAxisLabelsTexts(labelsAxisLabels);
     graphChart2DProps.setLabelsAxisTitleText(level + " Name");
     graphChart2DProps.setNumbersAxisTitleText("Finding/Loc %");
     graphChart2DProps.setLabelsAxisTicksAlignment(
           GraphChart2DProperties.CENTERED);

     //Configure graph properties
     final GraphProperties graphProps = new GraphProperties();
     graphProps.setGraphBarsExistence(false);
     graphProps.setGraphDotsExistence(true);
     graphProps.setGraphAllowComponentAlignment(true);
     graphProps.setGraphDotsWithinCategoryOverlapRatio(1);

     //Configure dataset
     final Dataset dataset = new Dataset (legendLabels.length,
           labelsAxisLabels.length, 1);

     // fill data....
     for (int j = 0; j < dataset.getNumCats(); ++j)
     {
        final FileSummary sum = src.get(labelsLongAxisLabels[j]);
        if (sum != null && sum.getLinesOfCode() != 0)
        {
           final float loc = sum.getLinesOfCode();
           final float codeLoc = sum.getCoverage()
               + sum.getViolations(Severity.COVERAGE);

           dataset.set(ROW_ERROR, j, 0,
               (PERCENT * sum.getViolations(Severity.ERROR)) / loc);
           dataset.set(ROW_CPD, j, 0,
               (PERCENT * sum.getViolations(Severity.CPD)) / loc);
           dataset.set(ROW_WARNING, j, 0,
               (PERCENT * sum.getViolations(Severity.WARNING)) / loc);
           dataset.set(ROW_DESIGN, j, 0,
               (PERCENT * sum.getViolations(Severity.DESIGN)) / loc);
           dataset.set(ROW_CODE_STYLE, j, 0,
               (PERCENT * sum.getViolations(Severity.CODE_STYLE)) / loc);
           dataset.set(ROW_INFO, j, 0,
               (PERCENT * sum.getViolations(Severity.INFO)) / loc);

           if (codeLoc != 0)
           {
              dataset.set(ROW_COVERAGE, j, 0,
                    (PERCENT * sum.getCoverage()) / codeLoc);
           }
           else
           {
              dataset.set(ROW_COVERAGE, j, 0, PERCENT);
           }
        }
        else
        {
           dataset.set(ROW_ERROR, j, 0, 0);
           dataset.set(ROW_CPD, j, 0, 0);
           dataset.set(ROW_WARNING, j, 0, 0);
           dataset.set(ROW_DESIGN, j, 0, 0);
           dataset.set(ROW_CODE_STYLE, j, 0, 0);
           dataset.set(ROW_INFO, j, 0, 0);
           dataset.set(ROW_COVERAGE, j, 0, 0);
        }
     }

     //Configure graph component colors
     final MultiColorsProperties multiColorsProps
             = new MultiColorsProperties();

     multiColorsProps.setColorsCustomize(true);

     multiColorsProps.setColorsCustom(new Color[]
           {
              Color.RED,
              Color.BLUE,
              Color.ORANGE,
              Color.YELLOW,
              Color.YELLOW,
              Color.CYAN,
              Color.MAGENTA
           });

     //Configure chart
     final LBChart2D chart2D = new LBChart2D();
     chart2D.setObject2DProperties (object2DProps);
     chart2D.setChart2DProperties (chart2DProps);
     chart2D.setLegendProperties (legendProps);
     chart2D.setGraphChart2DProperties (graphChart2DProps);
     chart2D.addGraphProperties (graphProps);
     chart2D.addDataset (dataset);
     chart2D.addMultiColorsProperties (multiColorsProps);

     //<-- End Chart2D configuration -->

     chart2D.setMaximumSize(LARGE_SIZE);
     chart2D.setPreferredSize(LARGE_SIZE);

     if (chart2D.validate(false))
     {
        java.io.File file = new java.io.File(mOutDir, "quality_large.png");
        javax.imageio.ImageIO.write(chart2D.getImage(), "PNG", file);
        chart2D.setMaximumSize(SMALL_SIZE);
        chart2D.setPreferredSize(SMALL_SIZE);
        chart2D.pack();
        file = new java.io.File(mOutDir, "quality_small.png");
        javax.imageio.ImageIO.write(chart2D.getImage(), "PNG", file);
     }
     else
     {
        chart2D.validate(true);
     }
   }

   /**
    * Gets the loc counter for a given package.
    */
   private float getCounter (Map<String, FileSummary> src,
       String string)
   {
      final FileSummary counter = src.get(string);
      final int result;

      if (counter != null)
      {
         result = counter.getLinesOfCode();
      }
      else
      {
         result = 0;
      }
      return result;
   }

   /**
    * Returns an array with the short for of the given package name.
    * @param labelsAxisLabels an array with package names to be cut.
    * @return a string array containing short form of the input package names.
    */
   private String[] cutPackages (String[] labelsAxisLabels)
   {
      final String [] result = new String[labelsAxisLabels.length];

      for (int i = 0; i < labelsAxisLabels.length; i++)
      {
         if (labelsAxisLabels[i].startsWith(mPrefix))
         {
            result[i]
                  = labelsAxisLabels[i].substring(mPrefix.length());
            if (result[i].indexOf('.') != -1)
            {
               result[i] = result[i].substring(result[i].indexOf('.') + 1);
            }
         }
         else
         {
            result[i] = labelsAxisLabels[i];
         }
      }

      return result;
   }

}
