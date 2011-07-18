/*
 * Generated source file, not in CVS/SVN repository
 */
package org.jcoderz.commons;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import org.jcoderz.commons.LogMessageInfoImpl;
import org.jcoderz.commons.BusinessImpact;
import org.jcoderz.commons.Category;
import org.jcoderz.commons.LogEvent;
import org.jcoderz.commons.AuditLogEvent;
import org.jcoderz.commons.Loggable;
import org.jcoderz.commons.AuditPrincipal;

/**
 * Enumeration of all Runtime Error log messages.
 *
 * <p>Instances of this class are immutable.</p>
 *
 * The following Runtime Error are defined:
 * <ul>
 *    <li>RteLogMessage.INTERNAL_ERROR</li>
 *    <li>RteLogMessage.ARGUMENT_MALFORMED</li>
 *    <li>RteLogMessage.ASSERTION_FAILED</li>
 *    <li>RteLogMessage.INCONSISTENT_DATABASE</li>
 *    <li>RteLogMessage.REMOTE_CALL_FAILURE</li>
 *    <li>RteLogMessage.DATABASE_INTEGRITY_ERROR</li>
 *    <li>RteLogMessage.DATABASE_ACCESS_FAILED</li>
 *    <li>RteLogMessage.ARGUMENT_MAX_LENGTH_VIOLATION</li>
 *    <li>RteLogMessage.ARGUMENT_MIN_LENGTH_VIOLATION</li>
 *    <li>RteLogMessage.ARGUMENT_MAX_VALUE_VIOLATION</li>
 *    <li>RteLogMessage.ARGUMENT_MIN_VALUE_VIOLATION</li>
 *    <li>RteLogMessage.ARGUMENT_PATTERN_VIOLATION</li>
 *    <li>RteLogMessage.ARGUMENT_FRACTION_DIGITS_VIOLATION</li>
 *    <li>RteLogMessage.UNEXPECTED_EXCEPTION</li>
 *    <li>RteLogMessage.EXCEPTION_CREATED</li>
 *    <li>RteLogMessage.RUNTIME_EXCEPTION_CREATED</li>
 * </ul>
 *
 * @author generated
 */
public abstract class RteLogMessage
      extends LogMessageInfoImpl
{
   /** use this serialVersionUID for serialization. */
   static final long serialVersionUID = 1L;

   /**
    * Private constructor used by the inner classes.
    * @param symbol The symbolic name of the log message.
    * @param id The unique numeric id of the log message.
    * @param level The log level that is used to log such a message.
    * @param text The detailed text of the log message.
    * @param solution The solution text of the log message.
    * @param businessImpact The business impact of the log message.
    * @param category The category of the log message.
    * @param params The paramter list used in the detailed text.
    * @param appName The application name.
    * @param appNameAbbr The application name abbreviation.
    * @param groupName The group name.
    * @param groupNameAbbr The group name abbreviation.
    */
   private RteLogMessage (String symbol, int id, Level level,
         String text, String solution, BusinessImpact businessImpact,
         Category category, String[] params, String appName, String appNameAbbr,
         String groupName, String groupNameAbbr)
   {
      super(symbol, id, level, text, solution, businessImpact, category, params,
            appName, appNameAbbr, groupName, groupNameAbbr);
   }

   /**
    The system encountered an unexpected condition, or contains a software bug. Details: {TECHNICAL_DESCRIPTION}.
    * .
    * <p>This class holds the static data for the <code>INTERNAL_ERROR</code>
    * log message.</p>
    * <p>It holds the following parameters:</p>
    * <ul>
    * 
       <li>Message: The system encountered an unexpected condition, or contains a software bug. Details: {TECHNICAL_DESCRIPTION}.</li>
      
       <li>Solution Review the log file to determine the problem that led to the error condition.</li>
      
    * </ul>
    */
   public static final class InternalError
         extends RteLogMessage
         implements Serializable
   {  
      /** The unique integer value of the log message <code>INTERNAL_ERROR</code>. */
      public static final int INT_VALUE = 16842753;
   
      /** The parameter <code>TECHNICAL_DESCRIPTION</code> for the log message text. */
      public static final String PARAM_TECHNICAL_DESCRIPTION
            = "TECHNICAL_DESCRIPTION";
      

      /**
       * The unique symbolic String of the log message
       * <code>INTERNAL_ERROR</code>.
    
    
       * @description The system encountered an unexpected condition, or contains a software bug. Details: {TECHNICAL_DESCRIPTION}.
       * @solution Review the log file to determine the problem that led to the error condition.
       */
      public static final String SYMBOL
        = "FWK_RTE_INTERNAL_ERROR";

      /** The LogLevel to use. */
      public static final Level LOG_LEVEL
        = 
            Level.SEVERE;

      /** The serialVersionUID used for serialization. */
      static final long serialVersionUID = 1;

      /**
       * Constructor.
       */
      private InternalError ()
      {
         // Do not access static members of this class here in the
         // constructor!
         // They might be not initialized (if the outer class
         // static constant is used to access the object ex.
         // FOO_MESSAGE.log()
         super(
               "FWK_RTE_INTERNAL_ERROR",
               16842753,
               
            Level.SEVERE,
               "The system encountered an unexpected condition, or contains a software bug. Details: {0}.",
               "Review the log file to determine the problem that led to the error condition.",
               BusinessImpact.UNDEFINED,
               Category.UNDEFINED,
               new String[]
                  {
                     "TECHNICAL_DESCRIPTION"   // PARAM_TECHNICAL_DESCRIPTION
                  },
               "FawkeZ",
               "FWK",
               "Runtime Error",
               "RTE");
      }
      

      /**
       * Adds the given message parameters to the <tt>loggable</tt>.
       * @param loggable the loggable to initialize. 
       * @param technicalDescription The technical description used in the message.
       * @return the given initialized <tt>loggable</tt>.
       */
      public static Loggable addParameters (Loggable loggable, String technicalDescription)
      {
         loggable.addParameter(PARAM_TECHNICAL_DESCRIPTION, technicalDescription);
         return loggable;
      }

      private Object readResolve ()
      {
         return fromInt(INT_VALUE);
      }

   }

   /**
    The given argument is at least partly malformed. Argument ''{ARGUMENT_NAME}'' cannot be set to value ''{Serializable:ARGUMENT_VALUE}''. Detailed description: {HINT}.
    * .
    * <p>This class holds the static data for the <code>ARGUMENT_MALFORMED</code>
    * log message.</p>
    * <p>It holds the following parameters:</p>
    * <ul>
    * 
       <li>Message: The given argument is at least partly malformed. Argument ''{ARGUMENT_NAME}'' cannot be set to value ''{Serializable:ARGUMENT_VALUE}''. Detailed description: {HINT}.</li>
      
       <li>Solution Supply a correct value for the argument.</li>
      
    * </ul>
    */
   public static final class ArgumentMalformed
         extends RteLogMessage
         implements Serializable
   {  
      /** The unique integer value of the log message <code>ARGUMENT_MALFORMED</code>. */
      public static final int INT_VALUE = 16842754;
   
      /** The parameter <code>ARGUMENT_NAME</code> for the log message text. */
      public static final String PARAM_ARGUMENT_NAME
            = "ARGUMENT_NAME";
      
      /** The parameter <code>ARGUMENT_VALUE</code> for the log message text. */
      public static final String PARAM_ARGUMENT_VALUE
            = "ARGUMENT_VALUE";
      
      /** The parameter <code>HINT</code> for the log message text. */
      public static final String PARAM_HINT
            = "HINT";
      

      /**
       * The unique symbolic String of the log message
       * <code>ARGUMENT_MALFORMED</code>.
    
    
       * @description The given argument is at least partly malformed. Argument ''{ARGUMENT_NAME}'' cannot be set to value ''{Serializable:ARGUMENT_VALUE}''. Detailed description: {HINT}.
       * @solution Supply a correct value for the argument.
       */
      public static final String SYMBOL
        = "FWK_RTE_ARGUMENT_MALFORMED";

      /** The LogLevel to use. */
      public static final Level LOG_LEVEL
        = 
            Level.WARNING;

      /** The serialVersionUID used for serialization. */
      static final long serialVersionUID = 1;

      /**
       * Constructor.
       */
      private ArgumentMalformed ()
      {
         // Do not access static members of this class here in the
         // constructor!
         // They might be not initialized (if the outer class
         // static constant is used to access the object ex.
         // FOO_MESSAGE.log()
         super(
               "FWK_RTE_ARGUMENT_MALFORMED",
               16842754,
               
            Level.WARNING,
               "The given argument is at least partly malformed. Argument ''{0}'' cannot be set to value ''{1}''. Detailed description: {2}.",
               "Supply a correct value for the argument.",
               BusinessImpact.UNDEFINED,
               Category.UNDEFINED,
               new String[]
                  {
                     "ARGUMENT_NAME" ,  // PARAM_ARGUMENT_NAME
                     "ARGUMENT_VALUE" ,  // PARAM_ARGUMENT_VALUE
                     "HINT"   // PARAM_HINT
                  },
               "FawkeZ",
               "FWK",
               "Runtime Error",
               "RTE");
      }
      

      /**
       * Adds the given message parameters to the <tt>loggable</tt>.
       * @param loggable the loggable to initialize. 
       * @param argumentName The argument name used in the message.
       * @param argumentValue The argument value used in the message.
       * @param hint The hint used in the message.
       * @return the given initialized <tt>loggable</tt>.
       */
      public static Loggable addParameters (Loggable loggable, String argumentName, Serializable argumentValue, String hint)
      {
         loggable.addParameter(PARAM_ARGUMENT_NAME, argumentName);
         loggable.addParameter(PARAM_ARGUMENT_VALUE, argumentValue);
         loggable.addParameter(PARAM_HINT, hint);
         return loggable;
      }

      private Object readResolve ()
      {
         return fromInt(INT_VALUE);
      }

   }

   /**
    Assertion failed. A condition is tested and found to be false. Message: {MESSAGE}.
    * .
    * <p>This class holds the static data for the <code>ASSERTION_FAILED</code>
    * log message.</p>
    * <p>It holds the following parameters:</p>
    * <ul>
    * 
       <li>Message: Assertion failed. A condition is tested and found to be false. Message: {MESSAGE}.</li>
      
       <li>Solution .</li>
      
    * </ul>
    */
   public static final class AssertionFailed
         extends RteLogMessage
         implements Serializable
   {  
      /** The unique integer value of the log message <code>ASSERTION_FAILED</code>. */
      public static final int INT_VALUE = 16842755;
   
      /** The parameter <code>MESSAGE</code> for the log message text. */
      public static final String PARAM_MESSAGE
            = "MESSAGE";
      

      /**
       * The unique symbolic String of the log message
       * <code>ASSERTION_FAILED</code>.
    
    
       * @description Assertion failed. A condition is tested and found to be false. Message: {MESSAGE}.
       * @solution .
       */
      public static final String SYMBOL
        = "FWK_RTE_ASSERTION_FAILED";

      /** The LogLevel to use. */
      public static final Level LOG_LEVEL
        = 
            Level.SEVERE;

      /** The serialVersionUID used for serialization. */
      static final long serialVersionUID = 1;

      /**
       * Constructor.
       */
      private AssertionFailed ()
      {
         // Do not access static members of this class here in the
         // constructor!
         // They might be not initialized (if the outer class
         // static constant is used to access the object ex.
         // FOO_MESSAGE.log()
         super(
               "FWK_RTE_ASSERTION_FAILED",
               16842755,
               
            Level.SEVERE,
               "Assertion failed. A condition is tested and found to be false. Message: {0}.",
               ".",
               BusinessImpact.UNDEFINED,
               Category.UNDEFINED,
               new String[]
                  {
                     "MESSAGE"   // PARAM_MESSAGE
                  },
               "FawkeZ",
               "FWK",
               "Runtime Error",
               "RTE");
      }
      

      /**
       * Adds the given message parameters to the <tt>loggable</tt>.
       * @param loggable the loggable to initialize. 
       * @param message The message used in the message.
       * @return the given initialized <tt>loggable</tt>.
       */
      public static Loggable addParameters (Loggable loggable, String message)
      {
         loggable.addParameter(PARAM_MESSAGE, message);
         return loggable;
      }

      private Object readResolve ()
      {
         return fromInt(INT_VALUE);
      }

   }

   /**
    A value read from the database was invalid. The database value {Serializable:ARGUMENT_VALUE} of column {COLUMN} in table {TABLE} could not be converted into the java type {TYPE}. Possibly the database has been modified manually, or the software that wrote the database contains a bug.
    * .
    * <p>This class holds the static data for the <code>INCONSISTENT_DATABASE</code>
    * log message.</p>
    * <p>It holds the following parameters:</p>
    * <ul>
    * 
       <li>Message: A value read from the database was invalid. The database value {Serializable:ARGUMENT_VALUE} of column {COLUMN} in table {TABLE} could not be converted into the java type {TYPE}. Possibly the database has been modified manually, or the software that wrote the database contains a bug.</li>
      
       <li>Solution Correct the value in the database if modified manually.</li>
      
    * </ul>
    */
   public static final class InconsistentDatabase
         extends RteLogMessage
         implements Serializable
   {  
      /** The unique integer value of the log message <code>INCONSISTENT_DATABASE</code>. */
      public static final int INT_VALUE = 16842756;
   
      /** The parameter <code>ARGUMENT_VALUE</code> for the log message text. */
      public static final String PARAM_ARGUMENT_VALUE
            = "ARGUMENT_VALUE";
      
      /** The parameter <code>COLUMN</code> for the log message text. */
      public static final String PARAM_COLUMN
            = "COLUMN";
      
      /** The parameter <code>TABLE</code> for the log message text. */
      public static final String PARAM_TABLE
            = "TABLE";
      
      /** The parameter <code>TYPE</code> for the log message text. */
      public static final String PARAM_TYPE
            = "TYPE";
      

      /**
       * The unique symbolic String of the log message
       * <code>INCONSISTENT_DATABASE</code>.
    
    
       * @description A value read from the database was invalid. The database value {Serializable:ARGUMENT_VALUE} of column {COLUMN} in table {TABLE} could not be converted into the java type {TYPE}. Possibly the database has been modified manually, or the software that wrote the database contains a bug.
       * @solution Correct the value in the database if modified manually.
       */
      public static final String SYMBOL
        = "FWK_RTE_INCONSISTENT_DATABASE";

      /** The LogLevel to use. */
      public static final Level LOG_LEVEL
        = 
            Level.SEVERE;

      /** The serialVersionUID used for serialization. */
      static final long serialVersionUID = 1;

      /**
       * Constructor.
       */
      private InconsistentDatabase ()
      {
         // Do not access static members of this class here in the
         // constructor!
         // They might be not initialized (if the outer class
         // static constant is used to access the object ex.
         // FOO_MESSAGE.log()
         super(
               "FWK_RTE_INCONSISTENT_DATABASE",
               16842756,
               
            Level.SEVERE,
               "A value read from the database was invalid. The database value {0} of column {1} in table {2} could not be converted into the java type {3}. Possibly the database has been modified manually, or the software that wrote the database contains a bug.",
               "Correct the value in the database if modified manually.",
               BusinessImpact.UNDEFINED,
               Category.UNDEFINED,
               new String[]
                  {
                     "ARGUMENT_VALUE" ,  // PARAM_ARGUMENT_VALUE
                     "COLUMN" ,  // PARAM_COLUMN
                     "TABLE" ,  // PARAM_TABLE
                     "TYPE"   // PARAM_TYPE
                  },
               "FawkeZ",
               "FWK",
               "Runtime Error",
               "RTE");
      }
      

      /**
       * Adds the given message parameters to the <tt>loggable</tt>.
       * @param loggable the loggable to initialize. 
       * @param argumentValue The argument value used in the message.
       * @param column The column used in the message.
       * @param table The table used in the message.
       * @param type The type used in the message.
       * @return the given initialized <tt>loggable</tt>.
       */
      public static Loggable addParameters (Loggable loggable, Serializable argumentValue, String column, String table, String type)
      {
         loggable.addParameter(PARAM_ARGUMENT_VALUE, argumentValue);
         loggable.addParameter(PARAM_COLUMN, column);
         loggable.addParameter(PARAM_TABLE, table);
         loggable.addParameter(PARAM_TYPE, type);
         return loggable;
      }

      private Object readResolve ()
      {
         return fromInt(INT_VALUE);
      }

   }

   /**
    The call to a internal remote object failed.
    * .
    * <p>This class holds the static data for the <code>REMOTE_CALL_FAILURE</code>
    * log message.</p>
    * <p>It holds the following parameters:</p>
    * <ul>
    * 
       <li>Message: The call to a internal remote object failed.</li>
      
       <li>Solution Review the log file to determine the problem that led to the error condition.</li>
      
    * </ul>
    */
   public static final class RemoteCallFailure
         extends RteLogMessage
         implements Serializable
   {  
      /** The unique integer value of the log message <code>REMOTE_CALL_FAILURE</code>. */
      public static final int INT_VALUE = 16842757;
   

      /**
       * The unique symbolic String of the log message
       * <code>REMOTE_CALL_FAILURE</code>.
    
    
       * @description The call to a internal remote object failed.
       * @solution Review the log file to determine the problem that led to the error condition.
       */
      public static final String SYMBOL
        = "FWK_RTE_REMOTE_CALL_FAILURE";

      /** The LogLevel to use. */
      public static final Level LOG_LEVEL
        = 
            Level.SEVERE;

      /** The serialVersionUID used for serialization. */
      static final long serialVersionUID = 1;

      /**
       * Constructor.
       */
      private RemoteCallFailure ()
      {
         // Do not access static members of this class here in the
         // constructor!
         // They might be not initialized (if the outer class
         // static constant is used to access the object ex.
         // FOO_MESSAGE.log()
         super(
               "FWK_RTE_REMOTE_CALL_FAILURE",
               16842757,
               
            Level.SEVERE,
               "The call to a internal remote object failed.",
               "Review the log file to determine the problem that led to the error condition.",
               BusinessImpact.UNDEFINED,
               Category.UNDEFINED,
               new String[]
                  {
                  },
               "FawkeZ",
               "FWK",
               "Runtime Error",
               "RTE");
      }
      

      /**
       * Adds the given message parameters to the <tt>loggable</tt>.
       * @param loggable the loggable to initialize. 
       * @return the given initialized <tt>loggable</tt>.
       */
      public static Loggable addParameters (Loggable loggable)
      {
         return loggable;
      }

      private Object readResolve ()
      {
         return fromInt(INT_VALUE);
      }

   }

   /**
    While accessing table {TABLE}, the following problem was encountered: {PROBLEM}.
    * .
    * <p>This class holds the static data for the <code>DATABASE_INTEGRITY_ERROR</code>
    * log message.</p>
    * <p>It holds the following parameters:</p>
    * <ul>
    * 
       <li>Message: While accessing table {TABLE}, the following problem was encountered: {PROBLEM}.</li>
      
       <li>Solution Review the log file to determine the problem that led to the error condition.</li>
      
    * </ul>
    */
   public static final class DatabaseIntegrityError
         extends RteLogMessage
         implements Serializable
   {  
      /** The unique integer value of the log message <code>DATABASE_INTEGRITY_ERROR</code>. */
      public static final int INT_VALUE = 16842758;
   
      /** The parameter <code>TABLE</code> for the log message text. */
      public static final String PARAM_TABLE
            = "TABLE";
      
      /** The parameter <code>PROBLEM</code> for the log message text. */
      public static final String PARAM_PROBLEM
            = "PROBLEM";
      

      /**
       * The unique symbolic String of the log message
       * <code>DATABASE_INTEGRITY_ERROR</code>.
    
    
       * @description While accessing table {TABLE}, the following problem was encountered: {PROBLEM}.
       * @solution Review the log file to determine the problem that led to the error condition.
       */
      public static final String SYMBOL
        = "FWK_RTE_DATABASE_INTEGRITY_ERROR";

      /** The LogLevel to use. */
      public static final Level LOG_LEVEL
        = 
            Level.SEVERE;

      /** The serialVersionUID used for serialization. */
      static final long serialVersionUID = 1;

      /**
       * Constructor.
       */
      private DatabaseIntegrityError ()
      {
         // Do not access static members of this class here in the
         // constructor!
         // They might be not initialized (if the outer class
         // static constant is used to access the object ex.
         // FOO_MESSAGE.log()
         super(
               "FWK_RTE_DATABASE_INTEGRITY_ERROR",
               16842758,
               
            Level.SEVERE,
               "While accessing table {0}, the following problem was encountered: {1}.",
               "Review the log file to determine the problem that led to the error condition.",
               BusinessImpact.UNDEFINED,
               Category.UNDEFINED,
               new String[]
                  {
                     "TABLE" ,  // PARAM_TABLE
                     "PROBLEM"   // PARAM_PROBLEM
                  },
               "FawkeZ",
               "FWK",
               "Runtime Error",
               "RTE");
      }
      

      /**
       * Adds the given message parameters to the <tt>loggable</tt>.
       * @param loggable the loggable to initialize. 
       * @param table The table used in the message.
       * @param problem The problem used in the message.
       * @return the given initialized <tt>loggable</tt>.
       */
      public static Loggable addParameters (Loggable loggable, String table, String problem)
      {
         loggable.addParameter(PARAM_TABLE, table);
         loggable.addParameter(PARAM_PROBLEM, problem);
         return loggable;
      }

      private Object readResolve ()
      {
         return fromInt(INT_VALUE);
      }

   }

   /**
    The system encountered an exception while accessing a database.
    * .
    * <p>This class holds the static data for the <code>DATABASE_ACCESS_FAILED</code>
    * log message.</p>
    * <p>It holds the following parameters:</p>
    * <ul>
    * 
       <li>Message: The system encountered an exception while accessing a database.</li>
      
       <li>Solution Review the log file to determine the problem that led to the error condition.</li>
      
    * </ul>
    */
   public static final class DatabaseAccessFailed
         extends RteLogMessage
         implements Serializable
   {  
      /** The unique integer value of the log message <code>DATABASE_ACCESS_FAILED</code>. */
      public static final int INT_VALUE = 16842759;
   

      /**
       * The unique symbolic String of the log message
       * <code>DATABASE_ACCESS_FAILED</code>.
    
    
       * @description The system encountered an exception while accessing a database.
       * @solution Review the log file to determine the problem that led to the error condition.
       */
      public static final String SYMBOL
        = "FWK_RTE_DATABASE_ACCESS_FAILED";

      /** The LogLevel to use. */
      public static final Level LOG_LEVEL
        = 
            Level.SEVERE;

      /** The serialVersionUID used for serialization. */
      static final long serialVersionUID = 1;

      /**
       * Constructor.
       */
      private DatabaseAccessFailed ()
      {
         // Do not access static members of this class here in the
         // constructor!
         // They might be not initialized (if the outer class
         // static constant is used to access the object ex.
         // FOO_MESSAGE.log()
         super(
               "FWK_RTE_DATABASE_ACCESS_FAILED",
               16842759,
               
            Level.SEVERE,
               "The system encountered an exception while accessing a database.",
               "Review the log file to determine the problem that led to the error condition.",
               BusinessImpact.UNDEFINED,
               Category.UNDEFINED,
               new String[]
                  {
                  },
               "FawkeZ",
               "FWK",
               "Runtime Error",
               "RTE");
      }
      

      /**
       * Adds the given message parameters to the <tt>loggable</tt>.
       * @param loggable the loggable to initialize. 
       * @return the given initialized <tt>loggable</tt>.
       */
      public static Loggable addParameters (Loggable loggable)
      {
         return loggable;
      }

      private Object readResolve ()
      {
         return fromInt(INT_VALUE);
      }

   }

   /**
    The given argument is at least partly malformed. Argument ''{ARGUMENT_NAME}'' cannot be set to value ''{Serializable:ARGUMENT_VALUE}''. The length of the Value ({Integer:ARGUMENT_LENGTH}) exceeds the allowed maximum length ({Integer:MAX_LENGTH}) for {Class:ARGUMENT_CLASS}.
    * .
    * <p>This class holds the static data for the <code>ARGUMENT_MAX_LENGTH_VIOLATION</code>
    * log message.</p>
    * <p>It holds the following parameters:</p>
    * <ul>
    * 
       <li>Message: The given argument is at least partly malformed. Argument ''{ARGUMENT_NAME}'' cannot be set to value ''{Serializable:ARGUMENT_VALUE}''. The length of the Value ({Integer:ARGUMENT_LENGTH}) exceeds the allowed maximum length ({Integer:MAX_LENGTH}) for {Class:ARGUMENT_CLASS}.</li>
      
       <li>Solution Supply a correct value for the argument.</li>
      
    * </ul>
    */
   public static final class ArgumentMaxLengthViolation
         extends RteLogMessage
         implements Serializable
   {  
      /** The unique integer value of the log message <code>ARGUMENT_MAX_LENGTH_VIOLATION</code>. */
      public static final int INT_VALUE = 16842760;
   
      /** The parameter <code>ARGUMENT_NAME</code> for the log message text. */
      public static final String PARAM_ARGUMENT_NAME
            = "ARGUMENT_NAME";
      
      /** The parameter <code>ARGUMENT_VALUE</code> for the log message text. */
      public static final String PARAM_ARGUMENT_VALUE
            = "ARGUMENT_VALUE";
      
      /** The parameter <code>ARGUMENT_LENGTH</code> for the log message text. */
      public static final String PARAM_ARGUMENT_LENGTH
            = "ARGUMENT_LENGTH";
      
      /** The parameter <code>MAX_LENGTH</code> for the log message text. */
      public static final String PARAM_MAX_LENGTH
            = "MAX_LENGTH";
      
      /** The parameter <code>ARGUMENT_CLASS</code> for the log message text. */
      public static final String PARAM_ARGUMENT_CLASS
            = "ARGUMENT_CLASS";
      

      /**
       * The unique symbolic String of the log message
       * <code>ARGUMENT_MAX_LENGTH_VIOLATION</code>.
    
    
       * @description The given argument is at least partly malformed. Argument ''{ARGUMENT_NAME}'' cannot be set to value ''{Serializable:ARGUMENT_VALUE}''. The length of the Value ({Integer:ARGUMENT_LENGTH}) exceeds the allowed maximum length ({Integer:MAX_LENGTH}) for {Class:ARGUMENT_CLASS}.
       * @solution Supply a correct value for the argument.
       */
      public static final String SYMBOL
        = "FWK_RTE_ARGUMENT_MAX_LENGTH_VIOLATION";

      /** The LogLevel to use. */
      public static final Level LOG_LEVEL
        = 
            Level.WARNING;

      /** The serialVersionUID used for serialization. */
      static final long serialVersionUID = 1;

      /**
       * Constructor.
       */
      private ArgumentMaxLengthViolation ()
      {
         // Do not access static members of this class here in the
         // constructor!
         // They might be not initialized (if the outer class
         // static constant is used to access the object ex.
         // FOO_MESSAGE.log()
         super(
               "FWK_RTE_ARGUMENT_MAX_LENGTH_VIOLATION",
               16842760,
               
            Level.WARNING,
               "The given argument is at least partly malformed. Argument ''{0}'' cannot be set to value ''{1}''. The length of the Value ({2}) exceeds the allowed maximum length ({3}) for {4}.",
               "Supply a correct value for the argument.",
               BusinessImpact.UNDEFINED,
               Category.UNDEFINED,
               new String[]
                  {
                     "ARGUMENT_NAME" ,  // PARAM_ARGUMENT_NAME
                     "ARGUMENT_VALUE" ,  // PARAM_ARGUMENT_VALUE
                     "ARGUMENT_LENGTH" ,  // PARAM_ARGUMENT_LENGTH
                     "MAX_LENGTH" ,  // PARAM_MAX_LENGTH
                     "ARGUMENT_CLASS"   // PARAM_ARGUMENT_CLASS
                  },
               "FawkeZ",
               "FWK",
               "Runtime Error",
               "RTE");
      }
      

      /**
       * Adds the given message parameters to the <tt>loggable</tt>.
       * @param loggable the loggable to initialize. 
       * @param argumentName The argument name used in the message.
       * @param argumentValue The argument value used in the message.
       * @param argumentLength The argument length used in the message.
       * @param maxLength The max length used in the message.
       * @param argumentClass The argument class used in the message.
       * @return the given initialized <tt>loggable</tt>.
       */
      public static Loggable addParameters (Loggable loggable, String argumentName, Serializable argumentValue, Integer argumentLength, Integer maxLength, Class argumentClass)
      {
         loggable.addParameter(PARAM_ARGUMENT_NAME, argumentName);
         loggable.addParameter(PARAM_ARGUMENT_VALUE, argumentValue);
         loggable.addParameter(PARAM_ARGUMENT_LENGTH, argumentLength);
         loggable.addParameter(PARAM_MAX_LENGTH, maxLength);
         loggable.addParameter(PARAM_ARGUMENT_CLASS, argumentClass);
         return loggable;
      }

      private Object readResolve ()
      {
         return fromInt(INT_VALUE);
      }

   }

   /**
    The given argument is at least partly malformed. Argument ''{ARGUMENT_NAME}'' cannot be set to value ''{Serializable:ARGUMENT_VALUE}''. The length of the Value ({Integer:ARGUMENT_LENGTH}) is below the allowed minimum length ({Integer:MIN_LENGTH}) for {Class:ARGUMENT_CLASS}.
    * .
    * <p>This class holds the static data for the <code>ARGUMENT_MIN_LENGTH_VIOLATION</code>
    * log message.</p>
    * <p>It holds the following parameters:</p>
    * <ul>
    * 
       <li>Message: The given argument is at least partly malformed. Argument ''{ARGUMENT_NAME}'' cannot be set to value ''{Serializable:ARGUMENT_VALUE}''. The length of the Value ({Integer:ARGUMENT_LENGTH}) is below the allowed minimum length ({Integer:MIN_LENGTH}) for {Class:ARGUMENT_CLASS}.</li>
      
       <li>Solution Supply a correct value for the argument.</li>
      
    * </ul>
    */
   public static final class ArgumentMinLengthViolation
         extends RteLogMessage
         implements Serializable
   {  
      /** The unique integer value of the log message <code>ARGUMENT_MIN_LENGTH_VIOLATION</code>. */
      public static final int INT_VALUE = 16842761;
   
      /** The parameter <code>ARGUMENT_NAME</code> for the log message text. */
      public static final String PARAM_ARGUMENT_NAME
            = "ARGUMENT_NAME";
      
      /** The parameter <code>ARGUMENT_VALUE</code> for the log message text. */
      public static final String PARAM_ARGUMENT_VALUE
            = "ARGUMENT_VALUE";
      
      /** The parameter <code>ARGUMENT_LENGTH</code> for the log message text. */
      public static final String PARAM_ARGUMENT_LENGTH
            = "ARGUMENT_LENGTH";
      
      /** The parameter <code>MIN_LENGTH</code> for the log message text. */
      public static final String PARAM_MIN_LENGTH
            = "MIN_LENGTH";
      
      /** The parameter <code>ARGUMENT_CLASS</code> for the log message text. */
      public static final String PARAM_ARGUMENT_CLASS
            = "ARGUMENT_CLASS";
      

      /**
       * The unique symbolic String of the log message
       * <code>ARGUMENT_MIN_LENGTH_VIOLATION</code>.
    
    
       * @description The given argument is at least partly malformed. Argument ''{ARGUMENT_NAME}'' cannot be set to value ''{Serializable:ARGUMENT_VALUE}''. The length of the Value ({Integer:ARGUMENT_LENGTH}) is below the allowed minimum length ({Integer:MIN_LENGTH}) for {Class:ARGUMENT_CLASS}.
       * @solution Supply a correct value for the argument.
       */
      public static final String SYMBOL
        = "FWK_RTE_ARGUMENT_MIN_LENGTH_VIOLATION";

      /** The LogLevel to use. */
      public static final Level LOG_LEVEL
        = 
            Level.WARNING;

      /** The serialVersionUID used for serialization. */
      static final long serialVersionUID = 1;

      /**
       * Constructor.
       */
      private ArgumentMinLengthViolation ()
      {
         // Do not access static members of this class here in the
         // constructor!
         // They might be not initialized (if the outer class
         // static constant is used to access the object ex.
         // FOO_MESSAGE.log()
         super(
               "FWK_RTE_ARGUMENT_MIN_LENGTH_VIOLATION",
               16842761,
               
            Level.WARNING,
               "The given argument is at least partly malformed. Argument ''{0}'' cannot be set to value ''{1}''. The length of the Value ({2}) is below the allowed minimum length ({3}) for {4}.",
               "Supply a correct value for the argument.",
               BusinessImpact.UNDEFINED,
               Category.UNDEFINED,
               new String[]
                  {
                     "ARGUMENT_NAME" ,  // PARAM_ARGUMENT_NAME
                     "ARGUMENT_VALUE" ,  // PARAM_ARGUMENT_VALUE
                     "ARGUMENT_LENGTH" ,  // PARAM_ARGUMENT_LENGTH
                     "MIN_LENGTH" ,  // PARAM_MIN_LENGTH
                     "ARGUMENT_CLASS"   // PARAM_ARGUMENT_CLASS
                  },
               "FawkeZ",
               "FWK",
               "Runtime Error",
               "RTE");
      }
      

      /**
       * Adds the given message parameters to the <tt>loggable</tt>.
       * @param loggable the loggable to initialize. 
       * @param argumentName The argument name used in the message.
       * @param argumentValue The argument value used in the message.
       * @param argumentLength The argument length used in the message.
       * @param minLength The min length used in the message.
       * @param argumentClass The argument class used in the message.
       * @return the given initialized <tt>loggable</tt>.
       */
      public static Loggable addParameters (Loggable loggable, String argumentName, Serializable argumentValue, Integer argumentLength, Integer minLength, Class argumentClass)
      {
         loggable.addParameter(PARAM_ARGUMENT_NAME, argumentName);
         loggable.addParameter(PARAM_ARGUMENT_VALUE, argumentValue);
         loggable.addParameter(PARAM_ARGUMENT_LENGTH, argumentLength);
         loggable.addParameter(PARAM_MIN_LENGTH, minLength);
         loggable.addParameter(PARAM_ARGUMENT_CLASS, argumentClass);
         return loggable;
      }

      private Object readResolve ()
      {
         return fromInt(INT_VALUE);
      }

   }

   /**
    The given argument is at least partly malformed. Argument ''{ARGUMENT_NAME}'' cannot be set to value ''{Serializable:ARGUMENT_VALUE}''. The value exceeds the allowed maximum ({Number:MAX_VALUE}) for {Class:ARGUMENT_CLASS}.
    * .
    * <p>This class holds the static data for the <code>ARGUMENT_MAX_VALUE_VIOLATION</code>
    * log message.</p>
    * <p>It holds the following parameters:</p>
    * <ul>
    * 
       <li>Message: The given argument is at least partly malformed. Argument ''{ARGUMENT_NAME}'' cannot be set to value ''{Serializable:ARGUMENT_VALUE}''. The value exceeds the allowed maximum ({Number:MAX_VALUE}) for {Class:ARGUMENT_CLASS}.</li>
      
       <li>Solution Supply a correct value for the argument.</li>
      
    * </ul>
    */
   public static final class ArgumentMaxValueViolation
         extends RteLogMessage
         implements Serializable
   {  
      /** The unique integer value of the log message <code>ARGUMENT_MAX_VALUE_VIOLATION</code>. */
      public static final int INT_VALUE = 16842762;
   
      /** The parameter <code>ARGUMENT_NAME</code> for the log message text. */
      public static final String PARAM_ARGUMENT_NAME
            = "ARGUMENT_NAME";
      
      /** The parameter <code>ARGUMENT_VALUE</code> for the log message text. */
      public static final String PARAM_ARGUMENT_VALUE
            = "ARGUMENT_VALUE";
      
      /** The parameter <code>MAX_VALUE</code> for the log message text. */
      public static final String PARAM_MAX_VALUE
            = "MAX_VALUE";
      
      /** The parameter <code>ARGUMENT_CLASS</code> for the log message text. */
      public static final String PARAM_ARGUMENT_CLASS
            = "ARGUMENT_CLASS";
      

      /**
       * The unique symbolic String of the log message
       * <code>ARGUMENT_MAX_VALUE_VIOLATION</code>.
    
    
       * @description The given argument is at least partly malformed. Argument ''{ARGUMENT_NAME}'' cannot be set to value ''{Serializable:ARGUMENT_VALUE}''. The value exceeds the allowed maximum ({Number:MAX_VALUE}) for {Class:ARGUMENT_CLASS}.
       * @solution Supply a correct value for the argument.
       */
      public static final String SYMBOL
        = "FWK_RTE_ARGUMENT_MAX_VALUE_VIOLATION";

      /** The LogLevel to use. */
      public static final Level LOG_LEVEL
        = 
            Level.WARNING;

      /** The serialVersionUID used for serialization. */
      static final long serialVersionUID = 1;

      /**
       * Constructor.
       */
      private ArgumentMaxValueViolation ()
      {
         // Do not access static members of this class here in the
         // constructor!
         // They might be not initialized (if the outer class
         // static constant is used to access the object ex.
         // FOO_MESSAGE.log()
         super(
               "FWK_RTE_ARGUMENT_MAX_VALUE_VIOLATION",
               16842762,
               
            Level.WARNING,
               "The given argument is at least partly malformed. Argument ''{0}'' cannot be set to value ''{1}''. The value exceeds the allowed maximum ({2}) for {3}.",
               "Supply a correct value for the argument.",
               BusinessImpact.UNDEFINED,
               Category.UNDEFINED,
               new String[]
                  {
                     "ARGUMENT_NAME" ,  // PARAM_ARGUMENT_NAME
                     "ARGUMENT_VALUE" ,  // PARAM_ARGUMENT_VALUE
                     "MAX_VALUE" ,  // PARAM_MAX_VALUE
                     "ARGUMENT_CLASS"   // PARAM_ARGUMENT_CLASS
                  },
               "FawkeZ",
               "FWK",
               "Runtime Error",
               "RTE");
      }
      

      /**
       * Adds the given message parameters to the <tt>loggable</tt>.
       * @param loggable the loggable to initialize. 
       * @param argumentName The argument name used in the message.
       * @param argumentValue The argument value used in the message.
       * @param maxValue The max value used in the message.
       * @param argumentClass The argument class used in the message.
       * @return the given initialized <tt>loggable</tt>.
       */
      public static Loggable addParameters (Loggable loggable, String argumentName, Serializable argumentValue, Number maxValue, Class argumentClass)
      {
         loggable.addParameter(PARAM_ARGUMENT_NAME, argumentName);
         loggable.addParameter(PARAM_ARGUMENT_VALUE, argumentValue);
         loggable.addParameter(PARAM_MAX_VALUE, maxValue);
         loggable.addParameter(PARAM_ARGUMENT_CLASS, argumentClass);
         return loggable;
      }

      private Object readResolve ()
      {
         return fromInt(INT_VALUE);
      }

   }

   /**
    The given argument is at least partly malformed. Argument ''{ARGUMENT_NAME}'' cannot be set to value ''{Serializable:ARGUMENT_VALUE}''. The value is below {Number:MIN_VALUE}, the allowed minimum for {Class:ARGUMENT_CLASS}.
    * .
    * <p>This class holds the static data for the <code>ARGUMENT_MIN_VALUE_VIOLATION</code>
    * log message.</p>
    * <p>It holds the following parameters:</p>
    * <ul>
    * 
       <li>Message: The given argument is at least partly malformed. Argument ''{ARGUMENT_NAME}'' cannot be set to value ''{Serializable:ARGUMENT_VALUE}''. The value is below {Number:MIN_VALUE}, the allowed minimum for {Class:ARGUMENT_CLASS}.</li>
      
       <li>Solution Supply a correct value for the argument.</li>
      
    * </ul>
    */
   public static final class ArgumentMinValueViolation
         extends RteLogMessage
         implements Serializable
   {  
      /** The unique integer value of the log message <code>ARGUMENT_MIN_VALUE_VIOLATION</code>. */
      public static final int INT_VALUE = 16842763;
   
      /** The parameter <code>ARGUMENT_NAME</code> for the log message text. */
      public static final String PARAM_ARGUMENT_NAME
            = "ARGUMENT_NAME";
      
      /** The parameter <code>ARGUMENT_VALUE</code> for the log message text. */
      public static final String PARAM_ARGUMENT_VALUE
            = "ARGUMENT_VALUE";
      
      /** The parameter <code>MIN_VALUE</code> for the log message text. */
      public static final String PARAM_MIN_VALUE
            = "MIN_VALUE";
      
      /** The parameter <code>ARGUMENT_CLASS</code> for the log message text. */
      public static final String PARAM_ARGUMENT_CLASS
            = "ARGUMENT_CLASS";
      

      /**
       * The unique symbolic String of the log message
       * <code>ARGUMENT_MIN_VALUE_VIOLATION</code>.
    
    
       * @description The given argument is at least partly malformed. Argument ''{ARGUMENT_NAME}'' cannot be set to value ''{Serializable:ARGUMENT_VALUE}''. The value is below {Number:MIN_VALUE}, the allowed minimum for {Class:ARGUMENT_CLASS}.
       * @solution Supply a correct value for the argument.
       */
      public static final String SYMBOL
        = "FWK_RTE_ARGUMENT_MIN_VALUE_VIOLATION";

      /** The LogLevel to use. */
      public static final Level LOG_LEVEL
        = 
            Level.WARNING;

      /** The serialVersionUID used for serialization. */
      static final long serialVersionUID = 1;

      /**
       * Constructor.
       */
      private ArgumentMinValueViolation ()
      {
         // Do not access static members of this class here in the
         // constructor!
         // They might be not initialized (if the outer class
         // static constant is used to access the object ex.
         // FOO_MESSAGE.log()
         super(
               "FWK_RTE_ARGUMENT_MIN_VALUE_VIOLATION",
               16842763,
               
            Level.WARNING,
               "The given argument is at least partly malformed. Argument ''{0}'' cannot be set to value ''{1}''. The value is below {2}, the allowed minimum for {3}.",
               "Supply a correct value for the argument.",
               BusinessImpact.UNDEFINED,
               Category.UNDEFINED,
               new String[]
                  {
                     "ARGUMENT_NAME" ,  // PARAM_ARGUMENT_NAME
                     "ARGUMENT_VALUE" ,  // PARAM_ARGUMENT_VALUE
                     "MIN_VALUE" ,  // PARAM_MIN_VALUE
                     "ARGUMENT_CLASS"   // PARAM_ARGUMENT_CLASS
                  },
               "FawkeZ",
               "FWK",
               "Runtime Error",
               "RTE");
      }
      

      /**
       * Adds the given message parameters to the <tt>loggable</tt>.
       * @param loggable the loggable to initialize. 
       * @param argumentName The argument name used in the message.
       * @param argumentValue The argument value used in the message.
       * @param minValue The min value used in the message.
       * @param argumentClass The argument class used in the message.
       * @return the given initialized <tt>loggable</tt>.
       */
      public static Loggable addParameters (Loggable loggable, String argumentName, Serializable argumentValue, Number minValue, Class argumentClass)
      {
         loggable.addParameter(PARAM_ARGUMENT_NAME, argumentName);
         loggable.addParameter(PARAM_ARGUMENT_VALUE, argumentValue);
         loggable.addParameter(PARAM_MIN_VALUE, minValue);
         loggable.addParameter(PARAM_ARGUMENT_CLASS, argumentClass);
         return loggable;
      }

      private Object readResolve ()
      {
         return fromInt(INT_VALUE);
      }

   }

   /**
    The given argument is at least partly malformed. Argument ''{ARGUMENT_NAME}'' cannot be set to value ''{Serializable:ARGUMENT_VALUE}''. The value does not match the restrictive pattern ''{String:PATTERN}'' for {Class:ARGUMENT_CLASS}.
    * .
    * <p>This class holds the static data for the <code>ARGUMENT_PATTERN_VIOLATION</code>
    * log message.</p>
    * <p>It holds the following parameters:</p>
    * <ul>
    * 
       <li>Message: The given argument is at least partly malformed. Argument ''{ARGUMENT_NAME}'' cannot be set to value ''{Serializable:ARGUMENT_VALUE}''. The value does not match the restrictive pattern ''{String:PATTERN}'' for {Class:ARGUMENT_CLASS}.</li>
      
       <li>Solution Supply a correct value for the argument.</li>
      
    * </ul>
    */
   public static final class ArgumentPatternViolation
         extends RteLogMessage
         implements Serializable
   {  
      /** The unique integer value of the log message <code>ARGUMENT_PATTERN_VIOLATION</code>. */
      public static final int INT_VALUE = 16842764;
   
      /** The parameter <code>ARGUMENT_NAME</code> for the log message text. */
      public static final String PARAM_ARGUMENT_NAME
            = "ARGUMENT_NAME";
      
      /** The parameter <code>ARGUMENT_VALUE</code> for the log message text. */
      public static final String PARAM_ARGUMENT_VALUE
            = "ARGUMENT_VALUE";
      
      /** The parameter <code>PATTERN</code> for the log message text. */
      public static final String PARAM_PATTERN
            = "PATTERN";
      
      /** The parameter <code>ARGUMENT_CLASS</code> for the log message text. */
      public static final String PARAM_ARGUMENT_CLASS
            = "ARGUMENT_CLASS";
      

      /**
       * The unique symbolic String of the log message
       * <code>ARGUMENT_PATTERN_VIOLATION</code>.
    
    
       * @description The given argument is at least partly malformed. Argument ''{ARGUMENT_NAME}'' cannot be set to value ''{Serializable:ARGUMENT_VALUE}''. The value does not match the restrictive pattern ''{String:PATTERN}'' for {Class:ARGUMENT_CLASS}.
       * @solution Supply a correct value for the argument.
       */
      public static final String SYMBOL
        = "FWK_RTE_ARGUMENT_PATTERN_VIOLATION";

      /** The LogLevel to use. */
      public static final Level LOG_LEVEL
        = 
            Level.WARNING;

      /** The serialVersionUID used for serialization. */
      static final long serialVersionUID = 1;

      /**
       * Constructor.
       */
      private ArgumentPatternViolation ()
      {
         // Do not access static members of this class here in the
         // constructor!
         // They might be not initialized (if the outer class
         // static constant is used to access the object ex.
         // FOO_MESSAGE.log()
         super(
               "FWK_RTE_ARGUMENT_PATTERN_VIOLATION",
               16842764,
               
            Level.WARNING,
               "The given argument is at least partly malformed. Argument ''{0}'' cannot be set to value ''{1}''. The value does not match the restrictive pattern ''{2}'' for {3}.",
               "Supply a correct value for the argument.",
               BusinessImpact.UNDEFINED,
               Category.UNDEFINED,
               new String[]
                  {
                     "ARGUMENT_NAME" ,  // PARAM_ARGUMENT_NAME
                     "ARGUMENT_VALUE" ,  // PARAM_ARGUMENT_VALUE
                     "PATTERN" ,  // PARAM_PATTERN
                     "ARGUMENT_CLASS"   // PARAM_ARGUMENT_CLASS
                  },
               "FawkeZ",
               "FWK",
               "Runtime Error",
               "RTE");
      }
      

      /**
       * Adds the given message parameters to the <tt>loggable</tt>.
       * @param loggable the loggable to initialize. 
       * @param argumentName The argument name used in the message.
       * @param argumentValue The argument value used in the message.
       * @param pattern The pattern used in the message.
       * @param argumentClass The argument class used in the message.
       * @return the given initialized <tt>loggable</tt>.
       */
      public static Loggable addParameters (Loggable loggable, String argumentName, Serializable argumentValue, String pattern, Class argumentClass)
      {
         loggable.addParameter(PARAM_ARGUMENT_NAME, argumentName);
         loggable.addParameter(PARAM_ARGUMENT_VALUE, argumentValue);
         loggable.addParameter(PARAM_PATTERN, pattern);
         loggable.addParameter(PARAM_ARGUMENT_CLASS, argumentClass);
         return loggable;
      }

      private Object readResolve ()
      {
         return fromInt(INT_VALUE);
      }

   }

   /**
    The given argument is at least partly malformed. Argument ''{ARGUMENT_NAME}'' cannot be set to value ''{Serializable:ARGUMENT_VALUE}''. The value has {Number:ARGUMENT_FRACTION_DIGITS} fraction digits and exceeds the allowed maximum {Number:MAX_FRACTION_DIGITS} for {Class:ARGUMENT_CLASS}.
    * .
    * <p>This class holds the static data for the <code>ARGUMENT_FRACTION_DIGITS_VIOLATION</code>
    * log message.</p>
    * <p>It holds the following parameters:</p>
    * <ul>
    * 
       <li>Message: The given argument is at least partly malformed. Argument ''{ARGUMENT_NAME}'' cannot be set to value ''{Serializable:ARGUMENT_VALUE}''. The value has {Number:ARGUMENT_FRACTION_DIGITS} fraction digits and exceeds the allowed maximum {Number:MAX_FRACTION_DIGITS} for {Class:ARGUMENT_CLASS}.</li>
      
       <li>Solution Supply a correct value for the argument.</li>
      
    * </ul>
    */
   public static final class ArgumentFractionDigitsViolation
         extends RteLogMessage
         implements Serializable
   {  
      /** The unique integer value of the log message <code>ARGUMENT_FRACTION_DIGITS_VIOLATION</code>. */
      public static final int INT_VALUE = 16842765;
   
      /** The parameter <code>ARGUMENT_NAME</code> for the log message text. */
      public static final String PARAM_ARGUMENT_NAME
            = "ARGUMENT_NAME";
      
      /** The parameter <code>ARGUMENT_VALUE</code> for the log message text. */
      public static final String PARAM_ARGUMENT_VALUE
            = "ARGUMENT_VALUE";
      
      /** The parameter <code>ARGUMENT_FRACTION_DIGITS</code> for the log message text. */
      public static final String PARAM_ARGUMENT_FRACTION_DIGITS
            = "ARGUMENT_FRACTION_DIGITS";
      
      /** The parameter <code>MAX_FRACTION_DIGITS</code> for the log message text. */
      public static final String PARAM_MAX_FRACTION_DIGITS
            = "MAX_FRACTION_DIGITS";
      
      /** The parameter <code>ARGUMENT_CLASS</code> for the log message text. */
      public static final String PARAM_ARGUMENT_CLASS
            = "ARGUMENT_CLASS";
      

      /**
       * The unique symbolic String of the log message
       * <code>ARGUMENT_FRACTION_DIGITS_VIOLATION</code>.
    
    
       * @description The given argument is at least partly malformed. Argument ''{ARGUMENT_NAME}'' cannot be set to value ''{Serializable:ARGUMENT_VALUE}''. The value has {Number:ARGUMENT_FRACTION_DIGITS} fraction digits and exceeds the allowed maximum {Number:MAX_FRACTION_DIGITS} for {Class:ARGUMENT_CLASS}.
       * @solution Supply a correct value for the argument.
       */
      public static final String SYMBOL
        = "FWK_RTE_ARGUMENT_FRACTION_DIGITS_VIOLATION";

      /** The LogLevel to use. */
      public static final Level LOG_LEVEL
        = 
            Level.WARNING;

      /** The serialVersionUID used for serialization. */
      static final long serialVersionUID = 1;

      /**
       * Constructor.
       */
      private ArgumentFractionDigitsViolation ()
      {
         // Do not access static members of this class here in the
         // constructor!
         // They might be not initialized (if the outer class
         // static constant is used to access the object ex.
         // FOO_MESSAGE.log()
         super(
               "FWK_RTE_ARGUMENT_FRACTION_DIGITS_VIOLATION",
               16842765,
               
            Level.WARNING,
               "The given argument is at least partly malformed. Argument ''{0}'' cannot be set to value ''{1}''. The value has {2} fraction digits and exceeds the allowed maximum {3} for {4}.",
               "Supply a correct value for the argument.",
               BusinessImpact.UNDEFINED,
               Category.UNDEFINED,
               new String[]
                  {
                     "ARGUMENT_NAME" ,  // PARAM_ARGUMENT_NAME
                     "ARGUMENT_VALUE" ,  // PARAM_ARGUMENT_VALUE
                     "ARGUMENT_FRACTION_DIGITS" ,  // PARAM_ARGUMENT_FRACTION_DIGITS
                     "MAX_FRACTION_DIGITS" ,  // PARAM_MAX_FRACTION_DIGITS
                     "ARGUMENT_CLASS"   // PARAM_ARGUMENT_CLASS
                  },
               "FawkeZ",
               "FWK",
               "Runtime Error",
               "RTE");
      }
      

      /**
       * Adds the given message parameters to the <tt>loggable</tt>.
       * @param loggable the loggable to initialize. 
       * @param argumentName The argument name used in the message.
       * @param argumentValue The argument value used in the message.
       * @param argumentFractionDigits The argument fraction digits used in the message.
       * @param maxFractionDigits The max fraction digits used in the message.
       * @param argumentClass The argument class used in the message.
       * @return the given initialized <tt>loggable</tt>.
       */
      public static Loggable addParameters (Loggable loggable, String argumentName, Serializable argumentValue, Number argumentFractionDigits, Number maxFractionDigits, Class argumentClass)
      {
         loggable.addParameter(PARAM_ARGUMENT_NAME, argumentName);
         loggable.addParameter(PARAM_ARGUMENT_VALUE, argumentValue);
         loggable.addParameter(PARAM_ARGUMENT_FRACTION_DIGITS, argumentFractionDigits);
         loggable.addParameter(PARAM_MAX_FRACTION_DIGITS, maxFractionDigits);
         loggable.addParameter(PARAM_ARGUMENT_CLASS, argumentClass);
         return loggable;
      }

      private Object readResolve ()
      {
         return fromInt(INT_VALUE);
      }

   }

   /**
    An exeption occured that was not expected in the current code.
    * .
    * <p>This class holds the static data for the <code>UNEXPECTED_EXCEPTION</code>
    * log message.</p>
    * <p>It holds the following parameters:</p>
    * <ul>
    * 
       <li>Message: An exeption occured that was not expected in the current code.</li>
      
       <li>Solution .</li>
      
    * </ul>
    */
   public static final class UnexpectedException
         extends RteLogMessage
         implements Serializable
   {  
      /** The unique integer value of the log message <code>UNEXPECTED_EXCEPTION</code>. */
      public static final int INT_VALUE = 16842766;
   

      /**
       * The unique symbolic String of the log message
       * <code>UNEXPECTED_EXCEPTION</code>.
    
    
       * @description An exeption occured that was not expected in the current code.
       * @solution .
       */
      public static final String SYMBOL
        = "FWK_RTE_UNEXPECTED_EXCEPTION";

      /** The LogLevel to use. */
      public static final Level LOG_LEVEL
        = 
            Level.SEVERE;

      /** The serialVersionUID used for serialization. */
      static final long serialVersionUID = 1;

      /**
       * Constructor.
       */
      private UnexpectedException ()
      {
         // Do not access static members of this class here in the
         // constructor!
         // They might be not initialized (if the outer class
         // static constant is used to access the object ex.
         // FOO_MESSAGE.log()
         super(
               "FWK_RTE_UNEXPECTED_EXCEPTION",
               16842766,
               
            Level.SEVERE,
               "An exeption occured that was not expected in the current code.",
               ".",
               BusinessImpact.UNDEFINED,
               Category.UNDEFINED,
               new String[]
                  {
                  },
               "FawkeZ",
               "FWK",
               "Runtime Error",
               "RTE");
      }
      

      /**
       * Adds the given message parameters to the <tt>loggable</tt>.
       * @param loggable the loggable to initialize. 
       * @return the given initialized <tt>loggable</tt>.
       */
      public static Loggable addParameters (Loggable loggable)
      {
         return loggable;
      }

      private Object readResolve ()
      {
         return fromInt(INT_VALUE);
      }

   }

   /**
    An Exception with symbol {SYMBOL} has been created. Exception Message is: {MESSAGE}
    * .
    * <p>This class holds the static data for the <code>EXCEPTION_CREATED</code>
    * log message.</p>
    * <p>It holds the following parameters:</p>
    * <ul>
    * 
       <li>Message: An Exception with symbol {SYMBOL} has been created. Exception Message is: {MESSAGE}</li>
      
       <li>Solution This fact is just written for the records here for the records. There is normally no action needed. This is just for debugging purposes.</li>
      
    * </ul>
    */
   public static final class ExceptionCreated
         extends RteLogMessage
         implements Serializable
   {  
      /** The unique integer value of the log message <code>EXCEPTION_CREATED</code>. */
      public static final int INT_VALUE = 16842767;
   
      /** The parameter <code>SYMBOL</code> for the log message text. */
      public static final String PARAM_SYMBOL
            = "SYMBOL";
      
      /** The parameter <code>MESSAGE</code> for the log message text. */
      public static final String PARAM_MESSAGE
            = "MESSAGE";
      

      /**
       * The unique symbolic String of the log message
       * <code>EXCEPTION_CREATED</code>.
    
    
       * @description An Exception with symbol {SYMBOL} has been created. Exception Message is: {MESSAGE}
       * @solution This fact is just written for the records here for the records. There is normally no action needed. This is just for debugging purposes.
       */
      public static final String SYMBOL
        = "FWK_RTE_EXCEPTION_CREATED";

      /** The LogLevel to use. */
      public static final Level LOG_LEVEL
        = 
            Level.FINE;

      /** The serialVersionUID used for serialization. */
      static final long serialVersionUID = 1;

      /**
       * Constructor.
       */
      private ExceptionCreated ()
      {
         // Do not access static members of this class here in the
         // constructor!
         // They might be not initialized (if the outer class
         // static constant is used to access the object ex.
         // FOO_MESSAGE.log()
         super(
               "FWK_RTE_EXCEPTION_CREATED",
               16842767,
               
            Level.FINE,
               "An Exception with symbol {0} has been created. Exception Message is: {1}",
               "This fact is just written for the records here for the records. There is normally no action needed. This is just for debugging purposes.",
               BusinessImpact.UNDEFINED,
               Category.UNDEFINED,
               new String[]
                  {
                     "SYMBOL" ,  // PARAM_SYMBOL
                     "MESSAGE"   // PARAM_MESSAGE
                  },
               "FawkeZ",
               "FWK",
               "Runtime Error",
               "RTE");
      }
      
      /**
       * Logs the message
       * <tt>"An Exception with symbol {SYMBOL} has been created. Exception Message is: {MESSAGE}"</tt>
       * with the given parameters.
       * @param symbol The symbol used in the message.
       * @param message The message used in the message.
       */
      public static void log (String symbol, String message
            )
      { 
         addParameters(new LogEvent(EXCEPTION_CREATED)
               , symbol, message).log();
      }

      /**
       * Logs the message
       * <tt>"An Exception with symbol {SYMBOL} has been created. Exception Message is: {MESSAGE}"</tt>
       * with the given parameters.
       * @param symbol The symbol used in the message.
       * @param message The message used in the message.
       * @param cause the Throwable that causes this message to be logged.
       */
      public static void log (String symbol, String message, Throwable cause
            )
      {
         addParameters(new LogEvent(EXCEPTION_CREATED, cause), symbol, message).log();
      }
        

      /**
       * Adds the given message parameters to the <tt>loggable</tt>.
       * @param loggable the loggable to initialize. 
       * @param symbol The symbol used in the message.
       * @param message The message used in the message.
       * @return the given initialized <tt>loggable</tt>.
       */
      public static Loggable addParameters (Loggable loggable, String symbol, String message)
      {
         loggable.addParameter(PARAM_SYMBOL, symbol);
         loggable.addParameter(PARAM_MESSAGE, message);
         return loggable;
      }

      private Object readResolve ()
      {
         return fromInt(INT_VALUE);
      }

   }

   /**
    An RuntimeException with symbol {SYMBOL} has been created. Exception Message is: {MESSAGE}
    * .
    * <p>This class holds the static data for the <code>RUNTIME_EXCEPTION_CREATED</code>
    * log message.</p>
    * <p>It holds the following parameters:</p>
    * <ul>
    * 
       <li>Message: An RuntimeException with symbol {SYMBOL} has been created. Exception Message is: {MESSAGE}</li>
      
       <li>Solution This fact is just written for the records here for the records. There is normally no action needed. This is just for debugging purposes.</li>
      
    * </ul>
    */
   public static final class RuntimeExceptionCreated
         extends RteLogMessage
         implements Serializable
   {  
      /** The unique integer value of the log message <code>RUNTIME_EXCEPTION_CREATED</code>. */
      public static final int INT_VALUE = 16842768;
   
      /** The parameter <code>SYMBOL</code> for the log message text. */
      public static final String PARAM_SYMBOL
            = "SYMBOL";
      
      /** The parameter <code>MESSAGE</code> for the log message text. */
      public static final String PARAM_MESSAGE
            = "MESSAGE";
      

      /**
       * The unique symbolic String of the log message
       * <code>RUNTIME_EXCEPTION_CREATED</code>.
    
    
       * @description An RuntimeException with symbol {SYMBOL} has been created. Exception Message is: {MESSAGE}
       * @solution This fact is just written for the records here for the records. There is normally no action needed. This is just for debugging purposes.
       */
      public static final String SYMBOL
        = "FWK_RTE_RUNTIME_EXCEPTION_CREATED";

      /** The LogLevel to use. */
      public static final Level LOG_LEVEL
        = 
            Level.FINE;

      /** The serialVersionUID used for serialization. */
      static final long serialVersionUID = 1;

      /**
       * Constructor.
       */
      private RuntimeExceptionCreated ()
      {
         // Do not access static members of this class here in the
         // constructor!
         // They might be not initialized (if the outer class
         // static constant is used to access the object ex.
         // FOO_MESSAGE.log()
         super(
               "FWK_RTE_RUNTIME_EXCEPTION_CREATED",
               16842768,
               
            Level.FINE,
               "An RuntimeException with symbol {0} has been created. Exception Message is: {1}",
               "This fact is just written for the records here for the records. There is normally no action needed. This is just for debugging purposes.",
               BusinessImpact.UNDEFINED,
               Category.UNDEFINED,
               new String[]
                  {
                     "SYMBOL" ,  // PARAM_SYMBOL
                     "MESSAGE"   // PARAM_MESSAGE
                  },
               "FawkeZ",
               "FWK",
               "Runtime Error",
               "RTE");
      }
      
      /**
       * Logs the message
       * <tt>"An RuntimeException with symbol {SYMBOL} has been created. Exception Message is: {MESSAGE}"</tt>
       * with the given parameters.
       * @param symbol The symbol used in the message.
       * @param message The message used in the message.
       */
      public static void log (String symbol, String message
            )
      { 
         addParameters(new LogEvent(RUNTIME_EXCEPTION_CREATED)
               , symbol, message).log();
      }

      /**
       * Logs the message
       * <tt>"An RuntimeException with symbol {SYMBOL} has been created. Exception Message is: {MESSAGE}"</tt>
       * with the given parameters.
       * @param symbol The symbol used in the message.
       * @param message The message used in the message.
       * @param cause the Throwable that causes this message to be logged.
       */
      public static void log (String symbol, String message, Throwable cause
            )
      {
         addParameters(new LogEvent(RUNTIME_EXCEPTION_CREATED, cause), symbol, message).log();
      }
        

      /**
       * Adds the given message parameters to the <tt>loggable</tt>.
       * @param loggable the loggable to initialize. 
       * @param symbol The symbol used in the message.
       * @param message The message used in the message.
       * @return the given initialized <tt>loggable</tt>.
       */
      public static Loggable addParameters (Loggable loggable, String symbol, String message)
      {
         loggable.addParameter(PARAM_SYMBOL, symbol);
         loggable.addParameter(PARAM_MESSAGE, message);
         return loggable;
      }

      private Object readResolve ()
      {
         return fromInt(INT_VALUE);
      }

   }

   /** The log message for the event <code>INTERNAL_ERROR</code>. */
   public static final InternalError INTERNAL_ERROR
         = new InternalError();

   /** The log message for the event <code>ARGUMENT_MALFORMED</code>. */
   public static final ArgumentMalformed ARGUMENT_MALFORMED
         = new ArgumentMalformed();

   /** The log message for the event <code>ASSERTION_FAILED</code>. */
   public static final AssertionFailed ASSERTION_FAILED
         = new AssertionFailed();

   /** The log message for the event <code>INCONSISTENT_DATABASE</code>. */
   public static final InconsistentDatabase INCONSISTENT_DATABASE
         = new InconsistentDatabase();

   /** The log message for the event <code>REMOTE_CALL_FAILURE</code>. */
   public static final RemoteCallFailure REMOTE_CALL_FAILURE
         = new RemoteCallFailure();

   /** The log message for the event <code>DATABASE_INTEGRITY_ERROR</code>. */
   public static final DatabaseIntegrityError DATABASE_INTEGRITY_ERROR
         = new DatabaseIntegrityError();

   /** The log message for the event <code>DATABASE_ACCESS_FAILED</code>. */
   public static final DatabaseAccessFailed DATABASE_ACCESS_FAILED
         = new DatabaseAccessFailed();

   /** The log message for the event <code>ARGUMENT_MAX_LENGTH_VIOLATION</code>. */
   public static final ArgumentMaxLengthViolation ARGUMENT_MAX_LENGTH_VIOLATION
         = new ArgumentMaxLengthViolation();

   /** The log message for the event <code>ARGUMENT_MIN_LENGTH_VIOLATION</code>. */
   public static final ArgumentMinLengthViolation ARGUMENT_MIN_LENGTH_VIOLATION
         = new ArgumentMinLengthViolation();

   /** The log message for the event <code>ARGUMENT_MAX_VALUE_VIOLATION</code>. */
   public static final ArgumentMaxValueViolation ARGUMENT_MAX_VALUE_VIOLATION
         = new ArgumentMaxValueViolation();

   /** The log message for the event <code>ARGUMENT_MIN_VALUE_VIOLATION</code>. */
   public static final ArgumentMinValueViolation ARGUMENT_MIN_VALUE_VIOLATION
         = new ArgumentMinValueViolation();

   /** The log message for the event <code>ARGUMENT_PATTERN_VIOLATION</code>. */
   public static final ArgumentPatternViolation ARGUMENT_PATTERN_VIOLATION
         = new ArgumentPatternViolation();

   /** The log message for the event <code>ARGUMENT_FRACTION_DIGITS_VIOLATION</code>. */
   public static final ArgumentFractionDigitsViolation ARGUMENT_FRACTION_DIGITS_VIOLATION
         = new ArgumentFractionDigitsViolation();

   /** The log message for the event <code>UNEXPECTED_EXCEPTION</code>. */
   public static final UnexpectedException UNEXPECTED_EXCEPTION
         = new UnexpectedException();

   /** The log message for the event <code>EXCEPTION_CREATED</code>. */
   public static final ExceptionCreated EXCEPTION_CREATED
         = new ExceptionCreated();

   /** The log message for the event <code>RUNTIME_EXCEPTION_CREATED</code>. */
   public static final RuntimeExceptionCreated RUNTIME_EXCEPTION_CREATED
         = new RuntimeExceptionCreated();


   private static final Map FROM_INT_MAP = new HashMap();
   private static final Map FROM_STRING_MAP = new HashMap();

   static
   {  
      addToMaps(INTERNAL_ERROR);
      addToMaps(ARGUMENT_MALFORMED);
      addToMaps(ASSERTION_FAILED);
      addToMaps(INCONSISTENT_DATABASE);
      addToMaps(REMOTE_CALL_FAILURE);
      addToMaps(DATABASE_INTEGRITY_ERROR);
      addToMaps(DATABASE_ACCESS_FAILED);
      addToMaps(ARGUMENT_MAX_LENGTH_VIOLATION);
      addToMaps(ARGUMENT_MIN_LENGTH_VIOLATION);
      addToMaps(ARGUMENT_MAX_VALUE_VIOLATION);
      addToMaps(ARGUMENT_MIN_VALUE_VIOLATION);
      addToMaps(ARGUMENT_PATTERN_VIOLATION);
      addToMaps(ARGUMENT_FRACTION_DIGITS_VIOLATION);
      addToMaps(UNEXPECTED_EXCEPTION);
      addToMaps(EXCEPTION_CREATED);
      addToMaps(RUNTIME_EXCEPTION_CREATED);
   }

   private static final void addToMaps (RteLogMessage error)
   {
      FROM_INT_MAP.put(new Integer(error.toInt()), error);
      FROM_STRING_MAP.put(error.toString(), error);
   }

   /**
    * Factory method to create a RteLogMessage class from its
    * string representation.
    * @param value the string representation.
    * @return RteLogMessage class from its string representation.
    */
   public static final RteLogMessage fromString (String value)
   {
      final RteLogMessage result
            = (RteLogMessage) FROM_STRING_MAP.get(value);
      if (result == null)
      {
         throw new IllegalArgumentException(
               value + " is not a valid string "
               + "representation for RteLogMessage");
      }
      return result;
   }

   /**
    * Factory method to create a RteLogMessage class from its
    * integer representation.
    * @param value the integer representation.
    * @return RteLogMessage class from its integer representation.
    */
   public static final RteLogMessage fromInt (int value)
   {
      final RteLogMessage result
            = (RteLogMessage) FROM_INT_MAP.get(new Integer(value));
      if (result == null)
      {
         throw new IllegalArgumentException(
               value + " is not a valid int "
               + "representation for RteLogMessage");
      }
      return result;
   }
}

