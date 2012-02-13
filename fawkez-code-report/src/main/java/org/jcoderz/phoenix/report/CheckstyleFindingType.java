/*
 * $Id: CheckstyleFindingType.java 1173 2008-09-22 10:04:44Z amandel $
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

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.jcoderz.phoenix.checkstyle.message.jaxb.CheckstyleMessages;
import org.jcoderz.phoenix.checkstyle.message.jaxb.FindingData;

/**
 * Enumeration type for checkstyle findings.
 * It also holds a method to get the finding type from the message
 * received. This is needed due to the fact that there is no reliable
 * enumeration of checkstyle findings delivered with checkstyle.
 * <p>New patterns might be needed with each checkstyle update.</p>
 * <p>Once assigned the symbols should not be changed without a urgent
 * need. The symbols are used to generate wiki page link.</p>
 *
 * @author Andreas Mandel
 */
public final class CheckstyleFindingType
      extends FindingType
{
   private static final List<CheckstyleFindingType> CHECKSTYLE_FINDING_TYPES;

   private static final String CHECKSTYLE_MESSAGE_JAXB_CONTEXT
      = "org.jcoderz.phoenix.checkstyle.message.jaxb";

   private static final String CHECKSTYLE_MESSAGE_FILE
      = "org/jcoderz/phoenix/checkstyle/checkstyle-messages.xml";

   private final String mMessagePattern;
   private final Severity mSeverity;

   static
   {
      CHECKSTYLE_FINDING_TYPES = new ArrayList<CheckstyleFindingType>();
   }

   /**
    * Checkstyle finding type that relates to:
    * <i>Interfaces should describe a type and hence have methods</i>.
    */
   public static final CheckstyleFindingType CS_INTERFACE_TYPE =
      new CheckstyleFindingType("CS_INTERFACE_TYPE", "Interface type.",
         "Interfaces should describe a type and hence have methods.",
         "interfaces should describe a type and hence have methods.",
         Severity.DESIGN);

   /**
    * Checkstyle finding type that relates to:
    * <i>Line is longer than the allowed number of characters</i>.
    */
   public static final CheckstyleFindingType CS_LINE_TO_LONG =
      new CheckstyleFindingType("CS_LINE_TO_LONG", "Line too long.",
         "Line is longer than the allowed number of characters.",
         "Line is longer than [0-9]+ characters.");

   /**
    * Checkstyle finding type that relates to:
    * <i>Line does not match expected header line</i>.
    */
   public static final CheckstyleFindingType CS_HEADER_MISMATCH =
      new CheckstyleFindingType("CS_HEADER_MISMATCH", "Header does not match.",
         "Line does not match expected header line. "
         + "Please use the global header.",
         "Line does not match expected header line of .*\\.");

   /**
    * Checkstyle finding type that relates to:
    * <i>Missing a Javadoc comment</i>.
    */
   public static final CheckstyleFindingType CS_JAVADOC_MISSING =
      new CheckstyleFindingType("CS_JAVADOC_MISSING",
         "Missing a Javadoc comment.",
         "Missing a Javadoc comment.",
         "Missing a Javadoc comment\\.");

   /**
    * Checkstyle finding type that relates to:
    * <i>Missing a Javadoc comment</i>.
    */
   public static final CheckstyleFindingType CS_JAVADOC_EMPTY_DESC =
      new CheckstyleFindingType("CS_JAVADOC_EMPTY_DESC",
         "Javadoc has empty description section.",
         "Javadoc has empty description section.",
         "Javadoc has empty description section\\.");

   /**
    * Checkstyle finding type that relates to:
    * <i>Unused Javadoc tag</i>.
    */
   public static final CheckstyleFindingType CS_JAVADOC_UNUSED_TAG =
      new CheckstyleFindingType("CS_JAVADOC_UNUSED_TAG",
         "Unused Javadoc tag.",
         "Unused Javadoc tag.",
         "Unused .* tag for '.*'\\.");

   /**
    * Checkstyle finding type that relates to:
    * <i>Expected an @return tag</i>.
    */
   public static final CheckstyleFindingType CS_JAVADOC_RETURN_EXPECTED =
      new CheckstyleFindingType("CS_JAVADOC_RETURN_EXPECTED",
         "Expected an @return tag.",
         "Expected an @return tag.",
         "Expected an @return tag.");

   /**
    * Checkstyle finding type that relates to:
    * <i>Missing Javadoc tag</i>.
    */
   public static final CheckstyleFindingType CS_JAVADOC_EXPECTED_TAG =
      new CheckstyleFindingType("CS_JAVADOC_EXPECTED_TAG",
         "Missing Javadoc tag.",
         "Missing Javadoc tag.",
         "Expected .* tag for '.*'\\.");

   /**
    * Checkstyle finding type that relates to:
    * <i>Unable to get class information for something</i>.
    */
   public static final CheckstyleFindingType CS_JAVADOC_CLASS_INFO =
      new CheckstyleFindingType("CS_JAVADOC_CLASS_INFO",
         "Unable to get class information for something.",
         "Unable to get class information for something.",
         "Unable to get class information for .* tag '.*'\\.");

   /**
    * Checkstyle finding type that relates to:
    * <i>Incomplete/Unclosed HTML tag</i>.
    */
   public static final CheckstyleFindingType CS_JAVADOC_HTML_UNCLOSED =
      new CheckstyleFindingType("CS_JAVADOC_HTML_UNCLOSED",
         "Incomplete HTML tag.",
         "Incomplete/Unclosed HTML tag.",
         "Incomplete HTML tag found: .*");

   /**
    * Checkstyle finding type that relates to:
    * <i>Name does not match given pattern</i>.
    */
   public static final CheckstyleFindingType CS_INVALID_PATTERN =
      new CheckstyleFindingType("CS_INVALID_PATTERN",
         "Name does not match given pattern.",
         "Name does not match given pattern.",
         "Name '.*' must match pattern '.*'\\.");

   /**
    * Checkstyle finding type that relates to:
    * <i>After the method declaration there should be a ' '</i>.
    */
   public static final CheckstyleFindingType CS_NO_WHITESPACE_AFTER_MSG_DECL =
      new CheckstyleFindingType("CS_NO_WHITESPACE_AFTER_MSG_DECL",
         "Missing whitespace.",
         "After the method declaration there should be a ' '.",
         "No whitespace \\( \\(\\) after method declaration\\.");

   /**
    * Checkstyle finding type that relates to:
    * <i>Comment matches to-do format</i>.
    */
   public static final CheckstyleFindingType CS_TODO =
      new CheckstyleFindingType("CS_TODO",
         "Comment matches to-do format.",
         "Comment matches to-do format.",
         "Comment matches to-do format '.*'\\.",
         Severity.INFO);

   /**
    * Checkstyle finding type that relates to:
    * <i>Dont use magics in the code</i>.
    */
   public static final CheckstyleFindingType CS_MAGIC =
      new CheckstyleFindingType("CS_MAGIC",
         "Dont use magics in the code.",
         "Magics make the code hard to maintain and understand. "
         + " Define appropriate constant instead.",
         "Dont use magic .* in the code\\.");

   /**
    * Checkstyle finding type that relates to:
    * <i>Whitespace not allowed</i>.
    */
   public static final CheckstyleFindingType CS_WHITESPACE_AFTER =
      new CheckstyleFindingType("CS_WHITESPACE_AFTER",
         "Whitespace not allowed.",
         "Whitespace not allowed.",
         "'.*' is followed by whitespace\\.");

   /**
    * Checkstyle finding type that relates to:
    * <i>Whitespace expected</i>.
    */
   public static final CheckstyleFindingType CS_NO_WHITESPACE_AFTER =
      new CheckstyleFindingType("CS_NO_WHITESPACE_AFTER",
         "Whitespace expected.",
         "Whitespace expected.",
         "'.*' is not followed by whitespace\\.");

   /**
    * Checkstyle finding type that relates to:
    * <i>Whitespace not allowed</i>.
    */
   public static final CheckstyleFindingType CS_WHITESPACE_BEFORE =
      new CheckstyleFindingType("CS_WHITESPACE_BEFORE",
         "Whitespace not allowed.",
         "Whitespace not allowed.",
         "'.*' is preceeded with whitespace\\.");

   /**
    * Checkstyle finding type that relates to:
    * <i>Whitespace expected</i>.
    */
   public static final CheckstyleFindingType CS_NO_WHITESPACE_BEFORE =
      new CheckstyleFindingType("CS_NO_WHITESPACE_AFTER",
         "Whitespace expected.",
         "Whitespace expected.",
         "'.*' is not preceeded with whitespace\\.");

   /**
    * Checkstyle finding type that relates to:
    * <i>A required javadoc tag is missing</i>.
    */
   public static final CheckstyleFindingType CS_MISSING_TAG =
      new CheckstyleFindingType("CS_MISSING_TAG",
         "A required javadoc tag is missing.",
         "A required javadoc tag is missing.",
         "Type Javadoc comment is missing an .* tag\\.");

   /**
    * Checkstyle finding type that relates to:
    * <i>A field is hidden</i>.
    */
   public static final CheckstyleFindingType CS_HIDDEN_FIELD =
      new CheckstyleFindingType("CS_HIDDEN_FIELD",
         "A field is hidden.",
         "A field is hidden.",
         "'.*' hides a field\\.", Severity.DESIGN);

   /**
    * Checkstyle finding type that relates to:
    * <i>Line contains a tab character</i>.
    */
   public static final CheckstyleFindingType CS_CONTAINS_TAB =
      new CheckstyleFindingType("CS_CONTAINS_TAB",
         "Line contains a tab character.",
         "Line contains a tab character. You should use spaces for "
         + "indentation.",
         "Line contains a tab character\\.");

   /**
    * Checkstyle finding type that relates to:
    * <i>File does not end with a newline</i>.
    */
   public static final CheckstyleFindingType CS_NO_NEWLINE =
      new CheckstyleFindingType("CS_NO_NEWLINE",
         "File does not end with a newline.",
         "File does not end with a newline.",
         "File does not end with a newline\\.");

   /**
    * Checkstyle finding type that relates to:
    * <i>Method length exceeds the maximum allowed length</i>.
    */
   public static final CheckstyleFindingType CS_MAX_LEN_METHOD =
      new CheckstyleFindingType("CS_MAX_LEN_METHOD",
         "Method length exceeds the maximum allowed length.",
         "A Method should have a moderate length...",
         "Method length is [\\.,0-9]+ lines \\(max allowed is [\\.,0-9]+\\)\\.",
         Severity.DESIGN);

   /**
    * Checkstyle finding type that relates to:
    * <i>Length of anonymous inner class exceeds the maximum allowed length</i>.
    */
   public static final CheckstyleFindingType CS_MAX_LEN_ANON_CLASS =
      new CheckstyleFindingType("CS_MAX_LEN_ANON_CLASS",
         "Length of anonymous inner class exceeds the maximum allowed length.",
         "A anonymous inner class should have a moderate length...",
         "Anonymous inner class length is [0-9]+ lines "
         + "\\(max allowed is [0-9]+\\)\\.",
         Severity.DESIGN);

   /**
    * Checkstyle finding type that relates to:
    * <i>Empty block detected</i>.
    */
   public static final CheckstyleFindingType CS_EMPTY_BLOCK =
      new CheckstyleFindingType("CS_EMPTY_BLOCK",
         "Empty block detected.",
         "If you think this is ok you must at least put a comment inside "
         + "this block, describing why it is ok.",
         "Empty .* block\\.");

   /**
    * Checkstyle finding type that relates to:
    * <i>Unused import</i>.
    */
   public static final CheckstyleFindingType CS_IMPORT_UNUSED =
      new CheckstyleFindingType("CS_IMPORT_UNUSED",
         "Unused import.",
         "Unused import.",
         "Unused import - .*\\.");

   /**
    * Checkstyle finding type that relates to:
    * <i>Indentation violation</i>.
    */
   public static final CheckstyleFindingType CS_SPECIAL_INDENT =
      new CheckstyleFindingType("CS_SPECIAL_INDENT",
         "Indentation violation.",
         "Several keywords require a special indentation.",
         "Expected indentation for '.*' is '.*' but was at '.*'\\.");

   /**
    * Checkstyle finding type that relates to:
    * <i>Deeply nested tries</i>.
    */
   public static final CheckstyleFindingType CS_NESTED_TRY_DEPTH =
      new CheckstyleFindingType("CS_NESTED_TRY_DEPTH",
         "Deeply nested tries.",
         "The nesting level for the try/catches is to deep.",
         "Nested try depth is [0-9]+ \\(max allowed is [0-9]+\\)\\.",
         Severity.DESIGN);

   /**
    * Checkstyle finding type that relates to:
    * <i>Too many parameters</i>.
    */
   public static final CheckstyleFindingType CS_NUMBER_OF_PARAMETERS =
      new CheckstyleFindingType("CS_NUMBER_OF_PARAMETERS",
         "Too many parameters.",
         "Too many parameters.",
         "More than [0-9]+ parameters\\.",
         Severity.DESIGN);

   /**
    * Checkstyle finding type that relates to:
    * <i>Method unused</i>.
    */
   public static final CheckstyleFindingType CS_METHOD_UNUSED =
      new CheckstyleFindingType("CS_METHOD_UNUSED",
         "Method unused.",
         "Method is never used.",
         "Unused private method '.*'\\.");

   /**
    * Checkstyle finding type that relates to:
    * <i>Local variable unused</i>.
    */
   public static final CheckstyleFindingType CS_LOCAL_VARIABLE_UNUSED =
      new CheckstyleFindingType("CS_LOCAL_VARIABLE_UNUSED",
         "Local variable unused.",
         "Local variable is never used.",
         "Unused local variable '.*'\\.");

   /**
    * Checkstyle finding type that relates to:
    * <i>Indentation must be a multiple of 4</i>.
    */
   public static final CheckstyleFindingType CS_ILLEGAL_INDENTATION =
      new CheckstyleFindingType("CS_ILLEGAL_INDENTATION",
         "Indentation must be a multiple of 4.",
         "Indentation must be a multiple of 4.",
         "Indentation must be a multiple of 4\\.");

   /**
    * Checkstyle finding type that relates to:
    * <i>Field is never used</i>.
    */
  public static final CheckstyleFindingType CS_FIELD_UNUSED =
      new CheckstyleFindingType("CS_FIELD_UNUSED",
         "Field unused.",
         "Field is never used.",
         "Unused private field '.*'\\.");

  /**
   * Checkstyle finding type that relates to:
   * <i>The equals operator should be on a new line</i>.
   */
   public static final CheckstyleFindingType CS_EQUALS_NEWLINE =
      new CheckstyleFindingType("CS_EQUALS_NEWLINE",
         "The equals operator should be on a new line.",
         "The equals operator should be on a new line.",
         "The equals operator should be on a new line\\.");

   /**
    * Checkstyle finding type that relates to:
    * <i>Line matches a illegal pattern</i>.
    */
   public static final CheckstyleFindingType CS_ILLEGAL_LINE =
      new CheckstyleFindingType("CS_ILLEGAL_PATTERN",
         "Line matches a illegal pattern.",
         "Line matches a illegal pattern.",
         "Line matches the illegal pattern '.*'\\.");

   /**
    * Checkstyle finding type that relates to:
    * <i>Long constants should use a uppercase L</i>.
    */
   public static final CheckstyleFindingType CS_UPPER_CASE_L =
      new CheckstyleFindingType("CS_UPPER_CASE_L", "Use uppercase L.",
         "Long constants should use a uppercase L the lower case L looks "
            + "a lot like 1. 123L vs. 123l.",
         "Should use uppercase 'L'\\.");

   /**
    * Checkstyle finding type that relates to:
    * <i>Invalid log level for trace log</i>.
    */
   public static final CheckstyleFindingType CS_NO_LOG_LEVEL_INFO =
      new CheckstyleFindingType("CS_NO_LOG_LEVEL_INFO",
         "Invalid log level for trace log.",
         "Trace log messages should have log level smaller than info, for "
            + "higher severity use predefined log messages.",
         "Maximum allowed log level for trace log is '.*' but was '.*'\\.",
         Severity.DESIGN);

   /**
    * Checkstyle finding type that relates to:
    * <i>The brace should not be on a new line</i>.
    */
   public static final CheckstyleFindingType CS_BRACE_ON_NEW_LINE =
       new CheckstyleFindingType("CS_BRACE_ON_NEW_LINE",
          "The brace should not be on a new line.",
          "The brace should not be on a new line.",
          "'[\\{\\}\\(\\)]' should be on the (previous|same) line\\.");

   /**
    * Checkstyle finding type that relates to:
    * <i>Avoid inline conditionals</i>.
    */
   public static final CheckstyleFindingType CS_INLINE_CONDITIONAL =
       new CheckstyleFindingType("CS_INLINE_CONDITIONAL",
          "Avoid inline conditionals.",
          "Avoid inline conditionals.",
          "Avoid inline conditionals\\.");

   /**
    * Checkstyle finding type that relates to:
    * <i>Avoid redundant code</i>.
    */
  public static final CheckstyleFindingType CS_REDUNDANT_MODIFIER =
       new CheckstyleFindingType("CS_REDUNDANT_MODIFIER",
          "Avoid redundant code.",
          "Avoid redundant code.",
          "Redundant '.*' modifier\\.");

  /**
   * Checkstyle finding type that relates to:
   * <i>Javadoc Pattern</i>.
   */
  public static final CheckstyleFindingType CS_JAVADOC_PATTERN =
      new CheckstyleFindingType("CS_JAVADOC_PATTERN",
         "Javadoc pattern violation.",
         "The javadoc tag does not comply to the required pattern.",
         "Type Javadoc tag .* must match pattern '.*'\\.");

  /**
   * Checkstyle finding type that relates to:
   * <i>Redundant throws with subclass.</i>.
   */
  public static final CheckstyleFindingType CS_REDUNDANT_THROWS_SUBCLASS =
      new CheckstyleFindingType("CS_REDUNDANT_THROWS_SUBCLASS",
         "Redundant throws declaration of a subclass.",
         "The throws statement already contains the superclass and so"
         + "declaring a subclass is redundant.",
         "Redundant throws: '.*' is subclass of '.*'\\.");

  /**
   * Checkstyle finding type that relates to:
   * <i>Redundant throws with unchecked exception.</i>.
   */
  public static final CheckstyleFindingType CS_REDUNDANT_THROWS_UNCHECKED =
      new CheckstyleFindingType("CS_REDUNDANT_THROWS_UNCHECKED",
         "Throws declaration of a unchecked exception is not needed.",
         "Throws declaration of a unchecked exception is not needed.",
         "Redundant throws: '.*' is unchecked exception\\.");

  /**
   * Checkstyle finding type that relates to:
   * <i>Boolean expression complexity is ... (max allowed is ...).</i>.
   */
  public static final CheckstyleFindingType CS_BOOLEAN_EXPRESSION_COMPLEXITY =
      new CheckstyleFindingType("CS_BOOLEAN_EXPRESSION_COMPLEXITY",
          "Boolean expression is too complex.",
          "Boolean expression is too complex. "
          + "Too many conditions leads to code that is difficult to "
          + "read and hence debug and maintain.",
          "Boolean expression complexity is .* \\(max allowed is .*\\)\\.");

  /**
   * Checkstyle finding type that relates to:
   * <i>String comparison with ==.</i>.
   */
  public static final CheckstyleFindingType CS_STRING_EQUALS_COMPARISON =
      new CheckstyleFindingType("CS_STRING_EQUALS_COMPARISON",
          "String comparison with ==.",
          "String comparison with ==.",
          "Literal Strings should be compared using equals\\(\\), not '=='\\.");

  /**
   * Checkstyle finding type that relates to:
   * <i>Missing package documentation file.</i>.
   */
  public static final CheckstyleFindingType CS_MISSING_PACKAGE_DOCUMENTATION =
      new CheckstyleFindingType("CS_MISSING_PACKAGE_DOCUMENTATION",
          "Missing package documentation file.",
          "Package content should be documentet using a package.html or"
          + " package.xml file.",
          "Missing package documentation file\\.");

  /**
   * Checkstyle finding type that relates to:
   * <i>Unable to get class information for .....</i>.
   */
  public static final CheckstyleFindingType CS_EXCEPTION_CLASS_NOT_FOUND =
      new CheckstyleFindingType("CS_EXCEPTION_CLASS_NOT_FOUND",
          "Unable to get class information for certain class.",
          "Mostly this is caused by a checkstyle internal issue or a finder"
          + "class path setting.",
          "Unable to get class information for .*\\.");

  /**
   * Checkstyle finding type that relates to:
   * <i>Using '.*' is not allowed.</i>.
   */
  public static final CheckstyleFindingType CS_TYPE_NOT_ALLOWED =
      new CheckstyleFindingType("CS_TYPE_NOT_ALLOWED",
          "Use of a type that is not permited.",
          "The type noted in the message should not be used.",
          "Using '.*' is not allowed\\.");

  /**
   * A internal checkstyle exception was triggered we shoulds also
   * report this!
   */
  public static final CheckstyleFindingType CS_EXCEPTION =
      new CheckstyleFindingType("CS_EXCEPTION",
         "Checkstyle analysis exception.",
         "Exception during checkstyle analysis. There seems to be "
         + "something strange here. One often problem is a reference to a "
         + "Unknown or not visible class in Javadoc.",
         "Got an exception - .*\\.");

   private CheckstyleFindingType (String symbol, String shortText,
         String description, String messagePattern, Severity severity)
   {
      super(symbol, shortText, description);
      mMessagePattern = messagePattern;
      mSeverity = severity;
      CHECKSTYLE_FINDING_TYPES.add(this);
   }



    private CheckstyleFindingType (String symbol, String shortText,
        String description, String messagePattern)
    {
        this(symbol, shortText, description, messagePattern,
            Severity.CODE_STYLE);
    }

   /**
    * Reads the given message and tries to find a matching finding type.
    * @param message the message to read.
    * @return the finding type matching to the message, or null if no such
    *   type was found.
    */
   public static FindingType detectFindingTypeForMessage (String message)
   {
      new FindingType.LazyInit();
      FindingType result = null;

      for (final CheckstyleFindingType type : CHECKSTYLE_FINDING_TYPES)
      {
         if (message.matches(type.getMessagePattern()))
         {
            result = type;
            break;
         }
      }
      return result;
   }

   /** @return the severity assigned to findings of this type by default. */
   public Severity getSeverity ()
   {
       return mSeverity;
   }

   /**
    * @return Returns the messagePattern.
    */
   private String getMessagePattern ()
   {
      return mMessagePattern;
   }

   /**
    * Init of the enum.
    */
   public static void initialize ()
   {
      try
      {
         final JAXBContext jaxbContext
            = JAXBContext.newInstance(CHECKSTYLE_MESSAGE_JAXB_CONTEXT,
               CheckstyleFindingType.class.getClassLoader());
         final Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
         final CheckstyleMessages messageCollection
            = (CheckstyleMessages) unmarshaller.unmarshal(
                  CheckstyleFindingType.class.getClassLoader().
                      getResourceAsStream(CHECKSTYLE_MESSAGE_FILE));
         for (final FindingData e
             : (List<FindingData>) messageCollection.getFindingType())
         {
            new CheckstyleFindingType(e.getSymbol(), e.getShortDescription(),
                  e.getDetailedDescription(), e.getMessagePattern());

         }
      }
      catch (Exception e)
      {
         throw new RuntimeException(
                 "Cannot initialize CheckstyleFindingTypes", e);
      }
   }
}
