<?xml version="1.0" encoding="UTF-8"?>
<!--
   $Id: generate-log-message-info.xsl 1610 2010-03-11 08:19:11Z amandel $

   Generator for the LogMessageInfo enumeration.

   Author: Michael Griffel
  -->
<xsl:stylesheet
   version="1.0"
   xmlns="http://www.jcoderz.org/app-info-v1.0"
   xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
   xmlns:xs="http://www.w3.org/2001/XMLSchema"
   xmlns:redirect="http://xml.apache.org/xalan/redirect"
   xmlns:xalan2="http://xml.apache.org/xslt"
   extension-element-prefixes="redirect">

<xsl:include href="libcommon.xsl"/>

<xsl:output method="text"
            encoding="UTF-8"/>

<xsl:param name="application-short-name" select="."/>
<xsl:param name="application-name" select="."/>
<xsl:param name="outdir" select="'.'"/>
<!--
   If set log messages are generated that allow the usage of a custom logger
   the custom logger class must support a logp(Level level, String id,
   String sourceClass, String sourceMethod, String msg, Object param) method.
   If the custom logger class is 'java.util.logging.Logger' the logp of this
   logger is used.  -->
<xsl:param name="custom-logger-class" select="''"/>
<!-- Usefull to disallow logging using the logger of the loggable impl
  class (could be used if custom-logger-class is set) -->
<xsl:param name="allow-use-of-base-logger" select="'true'"/>

<xsl:variable name="DEFAULT_STRING_SEPARATOR" select="'|'"/>

<xsl:template match="/">
   <xsl:variable name="group.count"
         select="count(//group[ ../@short-name = $application-short-name
                       or ../@name       = $application-name ])"/>
   <xsl:if test="not($group.count)">
      <xsl:message terminate="yes">Cannot find any group ELEMENT for
      application name '<xsl:value-of select="$application-short-name"/>'.
      </xsl:message>
   </xsl:if>
   <!-- log to out -->
   Generating classes to directory <xsl:value-of select="$outdir"/>.
   Found <xsl:value-of select="$group.count"/> groups for application <xsl:value-of select="$application-short-name"/>.

   <xsl:for-each select="//group[ ../@short-name = $application-short-name
                               or ../@name       = $application-name ]">
      <xsl:variable name="package.dir"><xsl:value-of
         select="$outdir"/>/<xsl:value-of
            select="translate(@package, '.', '/')"/></xsl:variable>
      <xsl:variable name="classname"><xsl:call-template
               name="shortnameToJava">
            <xsl:with-param name="s" select="@short-name"/>
         </xsl:call-template>LogMessage</xsl:variable>
      <xsl:variable name="file"><xsl:value-of
         select="$package.dir"/>/<xsl:value-of
            select="$classname"/>.java</xsl:variable>
      <redirect:write file="{$file}">
         <xsl:call-template name="generate-log-message-info">
            <xsl:with-param name="group" select="."/>
            <xsl:with-param name="classname" select="$classname"/>
            <xsl:with-param name="package" select="@package"/>
         </xsl:call-template>
      </redirect:write>
      <xsl:call-template name="generate-exceptions">
         <xsl:with-param name="group" select="."/>
         <xsl:with-param name="package" select="@package"/>
         <xsl:with-param name="package.dir" select="$package.dir"/>
      </xsl:call-template>
   </xsl:for-each>

   <!-- generate application exception(s) -->
   <xsl:for-each select="//application[@short-name = $application-short-name
                           or @name = $application-name]">
      <xsl:variable name="package.dir"><xsl:value-of
         select="$outdir"/>/<xsl:value-of
            select="translate(@package, '.', '/')"/></xsl:variable>
      <xsl:if test="./@base-exception">
         <xsl:variable name="classname"><xsl:call-template name="asJavaIdentifier">
            <xsl:with-param name="name" select="concat(./@short-name, '_EXCEPTION')"/>
            </xsl:call-template>
         </xsl:variable>
         <xsl:variable name="file"><xsl:value-of
            select="$package.dir"/>/<xsl:value-of
               select="$classname"/>.java</xsl:variable>
         <redirect:write file="{$file}">
            <xsl:call-template name="generate-group-exception">
               <xsl:with-param name="package" select="@package"/>
               <xsl:with-param name="classname" select="$classname"/>
               <xsl:with-param name="base-exception" select="./@base-exception"/>
               <xsl:with-param name="abstract" select="'abstract'"/>
            </xsl:call-template>
         </redirect:write>
      </xsl:if>
      <xsl:if test="./@base-runtime-exception">
         <xsl:variable name="classname"><xsl:call-template name="asJavaIdentifier">
            <xsl:with-param name="name" select="concat(./@short-name, '_RUNTIME_EXCEPTION')"/>
            </xsl:call-template>
         </xsl:variable>
         <xsl:variable name="file"><xsl:value-of
            select="$package.dir"/>/<xsl:value-of
               select="$classname"/>.java</xsl:variable>
         <redirect:write file="{$file}">
            <xsl:call-template name="generate-group-exception">
               <xsl:with-param name="package" select="@package"/>
               <xsl:with-param name="classname" select="$classname"/>
               <xsl:with-param name="base-exception" select="./@base-runtime-exception"/>
               <xsl:with-param name="abstract" select="'abstract'"/>
            </xsl:call-template>
         </redirect:write>
      </xsl:if>
   </xsl:for-each>

</xsl:template>


<xsl:template name="generate-exceptions">
   <xsl:param name="group" select="."/>
   <xsl:param name="package" select="'org.jcoderz.fixme'"/>
   <xsl:param name="package.dir" select="'fixme'"/>

   <xsl:for-each select="$group//message">
      <xsl:if test="./@base-exception">
         <xsl:variable name="classname"><xsl:call-template name="asJavaIdentifier">
            <xsl:with-param name="name" select="concat(./@name, '_EXCEPTION')"/>
            </xsl:call-template>
         </xsl:variable>
         <xsl:variable name="file"><xsl:value-of
            select="$package.dir"/>/<xsl:value-of
               select="$classname"/>.java</xsl:variable>
         <redirect:write file="{$file}">
            <xsl:call-template name="generate-exception">
               <xsl:with-param name="message" select="."/>
               <xsl:with-param name="package" select="$package"/>
               <xsl:with-param name="classname" select="$classname"/>
               <xsl:with-param name="name" select="./@name"/>
            </xsl:call-template>
         </redirect:write>
      </xsl:if>
   </xsl:for-each>

   <xsl:if test="./@base-exception">
      <xsl:variable name="classname"><xsl:call-template name="asJavaIdentifier">
         <xsl:with-param name="name" select="concat(./@name, '_EXCEPTION')"/>
         </xsl:call-template>
      </xsl:variable>
      <xsl:variable name="file"><xsl:value-of
         select="$package.dir"/>/<xsl:value-of
            select="$classname"/>.java</xsl:variable>
      <redirect:write file="{$file}">
         <xsl:call-template name="generate-group-exception">
            <xsl:with-param name="package" select="$package"/>
            <xsl:with-param name="classname" select="$classname"/>
            <xsl:with-param name="base-exception" select="./@base-exception"/>
            <xsl:with-param name="short-name" select="./@short-name"/>
            <xsl:with-param name="abstract" select="'abstract'"/>
         </xsl:call-template>
      </redirect:write>
   </xsl:if>

   <xsl:if test="./@base-runtime-exception">
      <xsl:variable name="classname"><xsl:call-template name="asJavaIdentifier">
         <xsl:with-param name="name" select="concat(./@name, '_RUNTIME_EXCEPTION')"/>
         </xsl:call-template>
      </xsl:variable>
      <xsl:variable name="file"><xsl:value-of
         select="$package.dir"/>/<xsl:value-of
            select="$classname"/>.java</xsl:variable>
      <redirect:write file="{$file}">
         <xsl:call-template name="generate-group-exception">
            <xsl:with-param name="package" select="$package"/>
            <xsl:with-param name="classname" select="$classname"/>
            <xsl:with-param name="base-exception" select="./@base-runtime-exception"/>
            <xsl:with-param name="short-name" select="./@short-name"/>
            <xsl:with-param name="abstract" select="'abstract'"/>
         </xsl:call-template>
      </redirect:write>
   </xsl:if>

</xsl:template>


<xsl:template name="generate-exception">
   <xsl:param name="message" select="''"/>
   <xsl:param name="package" select="'fixme'"/>
   <xsl:param name="classname" select="'fixme'"/>
   <xsl:param name="name" select="'fixme'"/>
   <xsl:param name="abstract" select="''"/>
   <xsl:call-template name="java-copyright-header"/>
   <xsl:variable name="group-classname"><xsl:call-template
         name="shortnameToJava">
      <xsl:with-param name="s" select="../@short-name"/>
   </xsl:call-template>LogMessage</xsl:variable>
   <xsl:variable name="constant-name"><xsl:call-template
         name="asJavaConstantName">
      <xsl:with-param name="value" select="$name"/>
   </xsl:call-template></xsl:variable>
   <xsl:variable name="inner-classname"><xsl:call-template
         name="asJavaIdentifier">
      <xsl:with-param name="name" select="$constant-name"/>
   </xsl:call-template></xsl:variable>
   <xsl:variable name="display-name"><xsl:call-template
         name="asDisplayName">
      <xsl:with-param name="name" select="$name"/>
   </xsl:call-template></xsl:variable>
   <xsl:variable name="tokens"><xsl:call-template name="find-tokens">
         <xsl:with-param name="s" select="$message/text"/>
      </xsl:call-template>
   </xsl:variable>
   <xsl:variable name="has-expicit-parameters"><xsl:call-template name="has-expicit-parameters">
         <xsl:with-param name="tokens" select="$tokens"/>
      </xsl:call-template>
   </xsl:variable>
package <xsl:value-of select="$package"/>;

import java.io.Serializable;

/**
 * <xsl:value-of select="$message/description"/>
 *
 * <xsl:value-of select="$message/text"/>
 *
 * This exception encapsulates the log message
 * {@link <xsl:value-of select="$package"/>.<xsl:value-of select="$group-classname"/>#<xsl:value-of select="$constant-name"/>}.
 *
 * @author generated
 */
public <xsl:value-of select="$abstract"/> class <xsl:value-of select="$classname"/>
      extends <xsl:value-of select="$message/@base-exception"/>
{
   /** use this serialVersionUID for serialization. */
   static final long serialVersionUID = 1L;

   <xsl:if test="$has-expicit-parameters != 'false'"><xsl:call-template
      name="exception-clazz-member-list">
            <xsl:with-param name="tokens" select="$tokens"/></xsl:call-template></xsl:if>

   /**
    * Constructs a <xsl:value-of select="$display-name"/> exception with a &lt;tt>cause&lt;/tt>.<xsl:call-template
      name="inner-clazz-log-parameter-javadoc">
                <xsl:with-param name="tokens" select="$tokens"/>
    </xsl:call-template>
    * @param cause the cause of this exception.
    */
   public <xsl:value-of select="$classname"/> (<xsl:call-template name="inner-clazz-log-parameter-list">
            <xsl:with-param name="tokens" select="concat($tokens, $DEFAULT_STRING_SEPARATOR, 'Throwable:cause')"/>
         </xsl:call-template>)
   {
      super(<xsl:value-of select="$group-classname"/>.<xsl:value-of select="$constant-name"/>, cause);
      <xsl:if test="$has-expicit-parameters != 'false'">
      <xsl:call-template name="exception-clazz-member-init">
         <xsl:with-param name="tokens" select="$tokens"/>
      </xsl:call-template>
      <xsl:value-of select="$group-classname"/>.<xsl:value-of select="$inner-classname"/>.addParameters(this,
         <xsl:call-template name="inner-clazz-log-parameter-call">
            <xsl:with-param name="tokens" select="$tokens"/>
         </xsl:call-template>);</xsl:if>
      logCreation();
   }

   /**
    * Constructs a <xsl:value-of select="$display-name"/> exception with a &lt;tt>cause&lt;/tt>.<xsl:call-template
      name="inner-clazz-log-parameter-javadoc">
                <xsl:with-param name="tokens" select="$tokens"/>
    </xsl:call-template>
    */
   public <xsl:value-of select="$classname"/> (<xsl:if
      test="normalize-space($tokens)"><xsl:call-template name="inner-clazz-log-parameter-list">
            <xsl:with-param name="tokens" select="$tokens"/>
         </xsl:call-template>
      </xsl:if>)
   {
      super(<xsl:value-of select="$group-classname"/>.<xsl:value-of select="$constant-name"/>);
      <xsl:if test="$has-expicit-parameters != 'false'">
      <xsl:call-template name="exception-clazz-member-init">
         <xsl:with-param name="tokens" select="$tokens"/>
      </xsl:call-template>
      <xsl:value-of select="$group-classname"/>.<xsl:value-of select="$inner-classname"/>.addParameters(this,
         <xsl:call-template name="inner-clazz-log-parameter-call">
            <xsl:with-param name="tokens" select="$tokens"/>
         </xsl:call-template>);</xsl:if>
      logCreation();
   }

   /**
    * Constructor getting an log message info.
    * This constructor is for generated exceptions internal use only.
    *
    * @param messageInfo the log message info for this exception
    */
   protected <xsl:value-of select="$classname"/> (<xsl:value-of select="$group-classname"/> messageInfo)
   {
      super(messageInfo);
      <xsl:if test="normalize-space($tokens)"><xsl:call-template
            name="exception-clazz-member-init">
            <xsl:with-param name="tokens" select="$tokens"/>
            <xsl:with-param name="with" select="'null'"/>
         </xsl:call-template></xsl:if>
   }

   /**
    * Constructor getting an log message info and a root exception.
    * This constructor is for generated exceptions internal use only.
    *
    * @param messageInfo the log message info for this exception
    * @param cause the problem that caused this exception to be thrown
    */
   protected <xsl:value-of select="$classname"/> (<xsl:value-of select="$group-classname"/> messageInfo, Throwable cause)
   {
      super(messageInfo, cause);
      <xsl:if test="normalize-space($tokens)"><xsl:call-template
            name="exception-clazz-member-init">
            <xsl:with-param name="tokens" select="$tokens"/>
            <xsl:with-param name="with" select="'null'"/>
         </xsl:call-template></xsl:if>
   }

      <xsl:if test="$custom-logger-class">
      /**
       * Logs the exception
       * &lt;tt>"<xsl:value-of select="normalize-space($message/text)"/>"&lt;/tt>
       * to the custom logger.
       * @param logger the custom logger to log to.
       */
      public void log (<xsl:value-of select="$custom-logger-class"/> logger)
      {
        logger.logp(getLogMessageInfo().getLogLevel(),
          <xsl:if test="$custom-logger-class != 'java.util.logging.Logger'">
            getLogMessageInfo().getSymbol(),</xsl:if>
            getSourceClass(),
            getSourceMethod(), <xsl:if test="$custom-logger-class = 'java.util.logging.Logger'">
            getLogMessageInfo().getSymbol() + "|" + </xsl:if>
            getTrackingNumber() + "|" + getMessage(),
            (Object) this);
      }
      </xsl:if>

   <xsl:if test="normalize-space($tokens)">
      <xsl:call-template
         name="exception-clazz-member-getter">
         <xsl:with-param name="tokens" select="$tokens"/>
      </xsl:call-template>
   </xsl:if>
}

</xsl:template>


<xsl:template name="generate-group-exception">
   <xsl:param name="package" select="'fixme'"/>
   <xsl:param name="classname" select="'fixme'"/>
   <xsl:param name="base-exception" select="'fixme'"/>
   <xsl:param name="short-name" select="''"/>
   <xsl:param name="abstract" select="''"/>
   <xsl:call-template name="java-copyright-header"/>
   <xsl:variable name="group-classname">
   <xsl:choose>
      <xsl:when test="$short-name">
         <xsl:call-template
            name="shortnameToJava">
            <xsl:with-param name="s" select="$short-name"/>
         </xsl:call-template>LogMessage</xsl:when>
      <xsl:otherwise>LogMessageInfo</xsl:otherwise>
         </xsl:choose>
   </xsl:variable>
   <xsl:variable name="display-name"><xsl:call-template
         name="asDisplayName">
      <xsl:with-param name="name" select="$classname"/>
   </xsl:call-template></xsl:variable>
package <xsl:value-of select="$package"/>;

import java.io.Serializable;
import <xsl:value-of select="$base-exception"/>;

import org.jcoderz.commons.LogMessageInfo;

/**
 * This is the base exception for all message in
 * the package {@link <xsl:value-of select="$package"/>}.
 *
 * @author generated
 */
public <xsl:value-of select="$abstract"/> class <xsl:value-of select="$classname"/>
      extends <xsl:value-of select="$base-exception"/>
{
   /** use this serialVersionUID for serialization. */
   static final long serialVersionUID = 1L;

   /**
    * Constructor getting an log message info.
    * This constructor is for generated exceptions internal use only.
    *
    * @param messageInfo the log message info for this exception
    */
   protected <xsl:value-of select="$classname"/> (<xsl:value-of select="$group-classname"/> messageInfo)
   {
      super(messageInfo);
   }

   /**
    * Constructor getting an log message info and a root exception.
    * This constructor is for generated exceptions internal use only.
    *
    * @param messageInfo the log message info for this exception
    * @param cause the problem that caused this exception to be thrown
    */
   protected <xsl:value-of select="$classname"/> (<xsl:value-of select="$group-classname"/> messageInfo, Throwable cause)
   {
      super(messageInfo, cause);
   }

}
</xsl:template>


<xsl:template name="generate-log-message-info">
   <xsl:param name="group" select="."/>
   <xsl:param name="classname" select="'RequiredParameter'"/>
   <xsl:param name="package" select="'org.jcoderz.fixme'"/>

   <xsl:call-template name="java-copyright-header"/>
package <xsl:value-of select="$package"/>;

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
 * Enumeration of all <xsl:value-of select="$group/@name"/> log messages.
 *
 * &lt;p>Instances of this class are immutable.&lt;/p>
 *
 * The following <xsl:value-of select="$group/@name"/> are defined:
 * &lt;ul&gt;<xsl:for-each select="$group//message">
 *    &lt;li&gt;<xsl:value-of select="$classname"/>.<xsl:value-of select="@name"/>&lt;/li&gt;</xsl:for-each>
 * &lt;/ul&gt;
 *
 * @author generated
 */
public abstract class <xsl:value-of select="$classname"/>
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
   private <xsl:value-of select="$classname"/> (String symbol, int id, Level level,
         String text, String solution, BusinessImpact businessImpact,
         Category category, String[] params, String appName, String appNameAbbr,
         String groupName, String groupNameAbbr)
   {
      super(symbol, id, level, text, solution, businessImpact, category, params,
            appName, appNameAbbr, groupName, groupNameAbbr);
   }
<xsl:for-each select="$group//message">
   <xsl:variable name="inner-classname"><xsl:call-template
      name="asJavaIdentifier">
         <xsl:with-param name="name" select="./@name"/>
      </xsl:call-template>
   </xsl:variable>
   /**
    <xsl:choose>
      <xsl:when test="description">
       <xsl:value-of select="normalize-space(description)"/>
      </xsl:when>
      <xsl:when test="text">
       <xsl:value-of select="normalize-space(text)"/>
      </xsl:when>
    </xsl:choose>
    * .
    * &lt;p>This class holds the static data for the &lt;code&gt;<xsl:value-of select="./@name"/>&lt;/code&gt;
    * log message.&lt;/p>
    * &lt;p>It holds the following parameters:&lt;/p>
    * &lt;ul>
    * <xsl:if test="text">
       &lt;li>Message: <xsl:value-of select="normalize-space(text)"/>&lt;/li>
      </xsl:if>
      <xsl:if test="description">
       &lt;li>Description: <xsl:value-of select="normalize-space(description)"/>&lt;/li>
      </xsl:if>
      <xsl:if test="solution">
       &lt;li>Solution <xsl:value-of select="normalize-space(solution)"/>&lt;/li>
      </xsl:if>
      <xsl:if test="procedure">
       &lt;li>Procedure <xsl:value-of select="normalize-space(procedure)"/>&lt;/li>
      </xsl:if>
      <xsl:if test="verification">
       &lt;li>Verification <xsl:value-of select="normalize-space(verification)"/>&lt;/li>
      </xsl:if>
    * &lt;/ul>
    */
   public static final class <xsl:value-of select="$inner-classname"/>
         extends <xsl:value-of select="$classname"/>
         implements Serializable
   {  <xsl:call-template name="generate-inner-clazz">
         <xsl:with-param name="message" select="."/>
         <xsl:with-param name="classname" select="$inner-classname"/>
         <xsl:with-param name="constant" select="./@name"/>
      </xsl:call-template>
   }
</xsl:for-each>

<xsl:for-each select="$group//message">
   <xsl:variable name="inner-classname"><xsl:call-template
      name="asJavaIdentifier">
         <xsl:with-param name="name" select="./@name"/>
      </xsl:call-template>
   </xsl:variable>
   /** The log message for the event &lt;code&gt;<xsl:value-of select="./@name"/>&lt;/code&gt;. */
   public static final <xsl:value-of select="$inner-classname"/><xsl:text> </xsl:text><xsl:value-of select="./@name"/>
         = new <xsl:value-of select="$inner-classname"/>();
</xsl:for-each>

   private static final Map FROM_INT_MAP = new HashMap();
   private static final Map FROM_STRING_MAP = new HashMap();

   static
   {  <xsl:for-each select="$group//message">
      addToMaps(<xsl:value-of select="./@name"/>);</xsl:for-each>
   }

   private static final void addToMaps (<xsl:value-of select="$classname"/> error)
   {
      FROM_INT_MAP.put(new Integer(error.toInt()), error);
      FROM_STRING_MAP.put(error.toString(), error);
   }

   /**
    * Factory method to create a <xsl:value-of select="$classname"/> class from its
    * string representation.
    * @param value the string representation.
    * @return <xsl:value-of select="$classname"/> class from its string representation.
    */
   public static final <xsl:value-of select="$classname"/> fromString (String value)
   {
      final <xsl:value-of select="$classname"/> result
            = (<xsl:value-of select="$classname"/>) FROM_STRING_MAP.get(value);
      if (result == null)
      {
         throw new IllegalArgumentException(
               value + " is not a valid string "
               + "representation for <xsl:value-of select="$classname"/>");
      }
      return result;
   }

   /**
    * Factory method to create a <xsl:value-of select="$classname"/> class from its
    * integer representation.
    * @param value the integer representation.
    * @return <xsl:value-of select="$classname"/> class from its integer representation.
    */
   public static final <xsl:value-of select="$classname"/> fromInt (int value)
   {
      final <xsl:value-of select="$classname"/> result
            = (<xsl:value-of select="$classname"/>) FROM_INT_MAP.get(new Integer(value));
      if (result == null)
      {
         throw new IllegalArgumentException(
               value + " is not a valid int "
               + "representation for <xsl:value-of select="$classname"/>");
      }
      return result;
   }
}

</xsl:template>

<xsl:template name="asUniqueNumber">
   <xsl:param name="application-id" select="'0'"/>
   <xsl:param name="group-id" select="'0'"/>
   <xsl:param name="message-id" select="'0'"/>
   <xsl:value-of select="format-number(
        ($application-id * 256 * 256 * 256)
      + ($group-id * 256 * 256)
      + $message-id,
         '##########')"/>
</xsl:template>


<xsl:template name="generate-inner-clazz">
   <xsl:param name="message" select="."/>
   <xsl:param name="classname" select="''"/>
   <xsl:param name="constant" select="''"/>
   <xsl:variable name="appName" select="../../@name"/>
   <xsl:variable name="appNameAbbr" select="../../@short-name"/>
   <xsl:variable name="groupName" select="../@name"/>
   <xsl:variable name="groupNameAbbr" select="../@short-name"/>
   <xsl:variable name="intValue">
   <xsl:call-template name="asUniqueNumber">
            <xsl:with-param name="application-id" select="../../@id"/>
            <xsl:with-param name="group-id" select="../@id"/>
            <xsl:with-param name="message-id" select="@id"/>
        </xsl:call-template>
   </xsl:variable>
      /** The unique integer value of the log message &lt;code&gt;<xsl:value-of select="./@name"/>&lt;/code&gt;. */
      public static final int INT_VALUE = <xsl:value-of select="$intValue"/>;
   <xsl:variable name="tokens"><xsl:call-template name="find-tokens">
         <xsl:with-param name="s" select="$message/text"/>
      </xsl:call-template>
   </xsl:variable>
   <xsl:call-template name="inner-clazz-declare-parameters">
      <xsl:with-param name="tokens" select="$tokens"/>
   </xsl:call-template>
   <xsl:variable name="modified-text">
      <xsl:call-template name="replace-token-with-number">
         <xsl:with-param name="text" select="$message/text"/>
         <xsl:with-param name="tokens" select="$tokens"/>
      </xsl:call-template>
   </xsl:variable>
   <xsl:variable name="loglevel">
      <xsl:choose>
         <xsl:when test="contains($message/@level, '.')">
            <xsl:value-of select="$message/@level"/>
         </xsl:when>
         <xsl:when test="$message/@level">
            Level.<xsl:value-of select="$message/@level"/>
         </xsl:when>
         <xsl:otherwise>Level.OFF</xsl:otherwise> <!-- default log level -->
      </xsl:choose>
   </xsl:variable>
   <xsl:variable name="business-impact">
      <xsl:choose>
         <xsl:when test="$message/@business-impact">
            <xsl:value-of select="$message/@business-impact"/>
         </xsl:when>
         <xsl:otherwise>UNDEFINED</xsl:otherwise> <!-- default business-impact -->
      </xsl:choose>
   </xsl:variable>
   <xsl:variable name="category">
      <xsl:choose>
         <xsl:when test="$message/@category">
            <xsl:value-of select="$message/@category"/>
         </xsl:when>
         <xsl:otherwise>UNDEFINED</xsl:otherwise> <!-- default category -->
      </xsl:choose>
   </xsl:variable>
   <xsl:variable name="symbol"><xsl:value-of
         select="$appNameAbbr"/>_<xsl:value-of
         select="$groupNameAbbr"/>_<xsl:value-of
         select="$message/@name"/></xsl:variable>
   <xsl:variable name="has-expicit-parameters"><xsl:call-template name="has-expicit-parameters">
         <xsl:with-param name="tokens" select="$tokens"/>
   </xsl:call-template></xsl:variable>

      /**
       * The unique symbolic String of the log message
       * &lt;code&gt;<xsl:value-of select="./@name"/>&lt;/code&gt;.
    <!-- Additional JavaDoc tags that can be picked up by external tools if needed.  -->
    <xsl:choose>
      <xsl:when test="$message/description">
       * @description <xsl:value-of select="normalize-space($message/description)"/>
      </xsl:when>
      <xsl:when test="$message/text">
       * @description <xsl:value-of select="normalize-space($message/text)"/>
      </xsl:when>
    </xsl:choose>
    <xsl:if test="$message/solution">
       * @solution <xsl:value-of select="normalize-space($message/solution)"/>
    </xsl:if>
    <xsl:if test="$message/procedure">
       * @procedure <xsl:value-of select="normalize-space($message/procedure)"/>
    </xsl:if>
    <xsl:if test="$message/verification">
       * @verification <xsl:value-of select="normalize-space($message/verification)"/>
    </xsl:if>
       */
      public static final String SYMBOL
        = "<xsl:value-of select="$symbol"/>";

      /** The LogLevel to use. */
      public static final Level LOG_LEVEL
        = <xsl:value-of select="$loglevel"/>;

      /** The serialVersionUID used for serialization. */
      static final long serialVersionUID = 1;

      /**
       * Constructor.
       */
      private <xsl:value-of select="$classname"/> ()
      {
         // Do not access static members of this class here in the
         // constructor!
         // They might be not initialized (if the outer class
         // static constant is used to access the object ex.
         // FOO_MESSAGE.log()
         super(
               "<xsl:value-of select="$symbol"/>",
               <xsl:value-of select="$intValue"/>,
               <xsl:value-of select="$loglevel"/>,
               "<xsl:value-of select="normalize-space($modified-text)"/>",
               "<xsl:value-of select="normalize-space($message/solution)"/>",
               BusinessImpact.<xsl:value-of select="$business-impact"/>,
               Category.<xsl:value-of select="$category"/>,
               new String[]
                  {<xsl:call-template
                  name="inner-clazz-construct-parameter-array">
                  <xsl:with-param name="tokens" select="$tokens"/>
               </xsl:call-template>
                  },
               "<xsl:value-of select="$appName"/>",
               "<xsl:value-of select="$appNameAbbr"/>",
               "<xsl:value-of select="$groupName"/>",
               "<xsl:value-of select="$groupNameAbbr"/>");
      }
      <xsl:variable name="isAudit" select="boolean($message/@category = 'AUDIT')"/>
      <xsl:variable name="log-event-class"><xsl:choose>
            <xsl:when test="$message/@category = 'AUDIT'">AuditLogEvent</xsl:when>
            <xsl:otherwise>LogEvent</xsl:otherwise></xsl:choose>
      </xsl:variable>
      <xsl:if test="not($message/@base-exception)">
        <xsl:if test="$allow-use-of-base-logger">
      /**
       * Logs the message
       * &lt;tt>"<xsl:value-of select="normalize-space($message/text)"/>"&lt;/tt>
       * with the given parameters.<xsl:call-template
                  name="inner-clazz-log-parameter-javadoc">
                  <xsl:with-param name="tokens" select="$tokens"/>
                  <xsl:with-param name="isAudit" select="$isAudit"/>
               </xsl:call-template>
       */
      public static void log (<xsl:call-template
                  name="inner-clazz-log-parameter-list">
                  <xsl:with-param name="tokens" select="$tokens"/>
                  <xsl:with-param name="isAudit" select="$isAudit"/>
               </xsl:call-template>
            )
      { <xsl:choose>
            <xsl:when test="normalize-space($tokens)">
         addParameters(new <xsl:value-of select="$log-event-class"/>(<xsl:value-of
            select="$constant"/><xsl:if test="$isAudit">, auditPrincipal</xsl:if>)
               <xsl:call-template
                  name="inner-clazz-log-parameter-call">
                  <xsl:with-param name="tokens" select="$tokens"/>
                  <xsl:with-param name="isFirst" select="false()"/>
               </xsl:call-template>).log();</xsl:when>
            <xsl:otherwise>
         new <xsl:value-of select="$log-event-class"/>(<xsl:value-of
            select="$constant"/><xsl:if test="$isAudit">, auditPrincipal</xsl:if>).log();</xsl:otherwise></xsl:choose>
      }

      /**
       * Logs the message
       * &lt;tt>"<xsl:value-of select="normalize-space($message/text)"/>"&lt;/tt>
       * with the given parameters.<xsl:call-template
                  name="inner-clazz-log-parameter-javadoc">
                  <xsl:with-param name="tokens" select="$tokens"/>
                  <xsl:with-param name="isAudit" select="$isAudit"/>
               </xsl:call-template>
       * @param cause the Throwable that causes this message to be logged.
       */
      public static void log (<xsl:call-template
                  name="inner-clazz-log-parameter-list">
                  <xsl:with-param name="tokens" select="concat($tokens, $DEFAULT_STRING_SEPARATOR, 'Throwable:cause')"/>
                  <xsl:with-param name="isAudit" select="$isAudit"/></xsl:call-template>
            )
      {<xsl:choose>
            <xsl:when test="normalize-space($tokens)">
         addParameters(new <xsl:value-of select="$log-event-class"/>(<xsl:value-of
           select="$constant"/><xsl:if test="$isAudit">, auditPrincipal</xsl:if>, cause)<xsl:call-template
                  name="inner-clazz-log-parameter-call">
                  <xsl:with-param name="tokens" select="$tokens"/>
                  <xsl:with-param name="isFirst" select="false()"/>
               </xsl:call-template>).log();</xsl:when>
            <xsl:otherwise>
         new <xsl:value-of select="$log-event-class"/>(<xsl:value-of
            select="$constant"/><xsl:if test="$isAudit">, auditPrincipal</xsl:if>, cause).log();</xsl:otherwise></xsl:choose>
      }
        </xsl:if> <!-- <xsl:if test="$allow-use-of-base-logger"> -->

        <xsl:if test="$custom-logger-class">
      /**
       * Logs the message
       * &lt;tt>"<xsl:value-of select="normalize-space($message/text)"/>"&lt;/tt>
       * with the given parameters to the custom logger.
       * @param logger the custom logger to log to.<xsl:call-template
                  name="inner-clazz-log-parameter-javadoc">
                  <xsl:with-param name="tokens" select="$tokens"/>
                  <xsl:with-param name="isAudit" select="$isAudit"/>
               </xsl:call-template>
       */
      public static void log (<xsl:value-of select="$custom-logger-class"/> logger<xsl:call-template
                  name="inner-clazz-log-parameter-list">
                  <xsl:with-param name="tokens" select="$tokens"/>
                  <xsl:with-param name="isAudit" select="$isAudit"/>
                  <xsl:with-param name="isFirst" select="false()"/>
          </xsl:call-template>)
      {
        final <xsl:value-of select="$log-event-class"/> logEvent
          = new <xsl:value-of select="$log-event-class"/>(<xsl:value-of
              select="$constant"/><xsl:if test="$isAudit">, auditPrincipal</xsl:if>);
           <xsl:if test="$has-expicit-parameters != 'false'">
        addParameters(logEvent
               <xsl:call-template
                  name="inner-clazz-log-parameter-call">
                  <xsl:with-param name="tokens" select="$tokens"/>
                  <xsl:with-param name="isFirst" select="false()"/>
               </xsl:call-template>);
           </xsl:if>

        logger.logp(<xsl:value-of select="$constant"/>.getLogLevel(),
          <xsl:if test="$custom-logger-class != 'java.util.logging.Logger'">
           <xsl:value-of select="$constant"/>.getSymbol(),</xsl:if>
           logEvent.getSourceClass(),
           logEvent.getSourceMethod(), <xsl:if test="$custom-logger-class = 'java.util.logging.Logger'">
           <xsl:value-of select="$constant"/>.getSymbol() + "|" + </xsl:if>
           logEvent.getTrackingNumber() + "|" + logEvent.getMessage(),
           (Object) logEvent);
      }

      /**
       * Logs the message
       * &lt;tt>"<xsl:value-of select="normalize-space($message/text)"/>"&lt;/tt>
       * with the given parameters to the custom logger.
       * @param logger the custom logger to log to.<xsl:call-template
                  name="inner-clazz-log-parameter-javadoc">
                  <xsl:with-param name="tokens" select="$tokens"/>
                  <xsl:with-param name="isAudit" select="$isAudit"/>
               </xsl:call-template>
       * @param cause the Throwable that causes this message to be logged.
       */
      public static void log (<xsl:value-of select="$custom-logger-class"/> logger<xsl:call-template
                  name="inner-clazz-log-parameter-list">
                  <xsl:with-param name="tokens" select="concat($tokens, $DEFAULT_STRING_SEPARATOR, 'Throwable:cause')"/>
                  <xsl:with-param name="isAudit" select="$isAudit"/>
                  <xsl:with-param name="isFirst" select="false()"/>
          </xsl:call-template>
            )
      {
        final <xsl:value-of select="$log-event-class"/> logEvent
          = new <xsl:value-of select="$log-event-class"/>(<xsl:value-of
              select="$constant"/><xsl:if test="$isAudit">, auditPrincipal</xsl:if>,
              cause);
           <xsl:if test="$has-expicit-parameters != 'false'">
        addParameters(logEvent,
               <xsl:call-template
                  name="inner-clazz-log-parameter-call">
                  <xsl:with-param name="tokens" select="$tokens"/>
                  <xsl:with-param name="isAudit" select="$isAudit"/>
               </xsl:call-template>);
           </xsl:if>

         logger.logp(<xsl:value-of select="$constant"/>.getLogLevel(),
          <xsl:if test="$custom-logger-class != 'java.util.logging.Logger'">
           <xsl:value-of select="$constant"/>.getSymbol(),</xsl:if>
           logEvent.getSourceClass(),
           logEvent.getSourceMethod(), <xsl:if test="$custom-logger-class = 'java.util.logging.Logger'">
           <xsl:value-of select="$constant"/>.getSymbol() + "|" + </xsl:if>
           logEvent.getTrackingNumber() + "|" + logEvent.getMessage(),
           (Object) logEvent);
      }

        </xsl:if>

      <!-- special factory methods for audit log events -->
      <xsl:if test="$isAudit">
      /**
       * Creates the audit log event
       * &lt;tt>"<xsl:value-of select="normalize-space($message/text)"/>"&lt;/tt>
       * with the given parameters.<xsl:call-template
                  name="inner-clazz-log-parameter-javadoc">
                  <xsl:with-param name="tokens" select="$tokens"/>
                  <xsl:with-param name="isAudit" select="$isAudit"/>
               </xsl:call-template>
       */
      public static AuditLogEvent create (<xsl:call-template
                  name="inner-clazz-log-parameter-list">
                  <xsl:with-param name="tokens" select="$tokens"/>
                  <xsl:with-param name="isAudit" select="$isAudit"/>
               </xsl:call-template>
            )
      { <xsl:choose>
            <xsl:when test="normalize-space($tokens)">
         return (AuditLogEvent) addParameters(new <xsl:value-of select="$log-event-class"/>(<xsl:value-of
            select="$constant"/><xsl:if test="$isAudit">, auditPrincipal</xsl:if>),
               <xsl:call-template
                  name="inner-clazz-log-parameter-call">
                  <xsl:with-param name="tokens" select="$tokens"/>
                  <xsl:with-param name="isAudit" select="$isAudit"/>
               </xsl:call-template>);</xsl:when>
            <xsl:otherwise>
         return new <xsl:value-of select="$log-event-class"/>(<xsl:value-of
            select="$constant"/><xsl:if test="$isAudit">, auditPrincipal</xsl:if>);</xsl:otherwise></xsl:choose>
      }

      /**
       * Creates the audit log event
       * &lt;tt>"<xsl:value-of select="normalize-space($message/text)"/>"&lt;/tt>
       * with the given parameters.<xsl:call-template
                  name="inner-clazz-log-parameter-javadoc">
                  <xsl:with-param name="tokens" select="$tokens"/>
                  <xsl:with-param name="isAudit" select="$isAudit"/>
               </xsl:call-template>
       * @param cause the Throwable that causes this message to be logged.
       */
      public static AuditLogEvent create (<xsl:call-template
                  name="inner-clazz-log-parameter-list">
                  <xsl:with-param name="tokens" select="$tokens"/>
                  <xsl:with-param name="isAudit" select="$isAudit"/>
               </xsl:call-template><xsl:if test="$isAudit or normalize-space($tokens)">,</xsl:if>
               Throwable cause
            )
      {<xsl:choose>
            <xsl:when test="normalize-space($tokens)">
         return (AuditLogEvent) addParameters(new <xsl:value-of select="$log-event-class"/>(<xsl:value-of select="$constant"/><xsl:if test="$isAudit">, auditPrincipal</xsl:if>, cause),
               <xsl:call-template
                  name="inner-clazz-log-parameter-call">
                  <xsl:with-param name="tokens" select="$tokens"/>
                  <xsl:with-param name="isAudit" select="$isAudit"/>
               </xsl:call-template>);</xsl:when>
            <xsl:otherwise>
         return new <xsl:value-of select="$log-event-class"/>(<xsl:value-of
            select="$constant"/><xsl:if test="$isAudit">, auditPrincipal</xsl:if>, cause);</xsl:otherwise></xsl:choose>
      }
      </xsl:if>

      </xsl:if>

      /**
       * Adds the given message parameters to the &lt;tt>loggable&lt;/tt>.
       * @param loggable the loggable to initialize. <xsl:call-template
                  name="inner-clazz-log-parameter-javadoc">
                  <xsl:with-param name="tokens" select="$tokens"/>
               </xsl:call-template>
       * @return the given initialized &lt;tt>loggable&lt;/tt>.
       */
      public static Loggable addParameters (Loggable loggable<xsl:call-template
                  name="inner-clazz-log-parameter-list-sub">
                  <xsl:with-param name="tokens" select="$tokens"/>
                  <xsl:with-param name="isFirst" select="false()"/>
               </xsl:call-template>)
      {<xsl:call-template
                  name="inner-clazz-construct-parameter-setter">
                  <xsl:with-param name="tokens" select="$tokens"/>
               </xsl:call-template>
         return loggable;
      }

      private Object readResolve ()
      {
         return fromInt(INT_VALUE);
      }
</xsl:template>

<xsl:template name="inner-clazz-declare-parameters">
   <xsl:param name="tokens" select="''"/>
   <xsl:variable name="type-token">
      <xsl:call-template name="string-set-next-token">
         <xsl:with-param name="s" select="$tokens"/>
      </xsl:call-template>
   </xsl:variable>
   <xsl:variable name="token" select="substring-after($type-token, ':')"/>
   <xsl:if test="normalize-space($token)">
     <xsl:variable name="constant_token">
        <xsl:call-template name="asJavaConstantName">
           <xsl:with-param name="value" select="normalize-space($token)"/>
        </xsl:call-template>
     </xsl:variable>
      /** The parameter &lt;code&gt;<xsl:value-of select="$token"/>&lt;/code&gt; for the log message text. */
      public static final String PARAM_<xsl:value-of select="$constant_token"/>
            = "<xsl:value-of select="$constant_token"/>";
      <xsl:call-template name="inner-clazz-declare-parameters">
         <xsl:with-param name="tokens"
            select="substring-after($tokens, $DEFAULT_STRING_SEPARATOR)"/>
      </xsl:call-template>
   </xsl:if>
</xsl:template>

<xsl:template name="inner-clazz-construct-parameter-array">
   <xsl:param name="tokens" select="''"/>
   <xsl:variable name="type-token">
      <xsl:call-template name="string-set-next-token">
         <xsl:with-param name="s" select="$tokens"/>
      </xsl:call-template>
   </xsl:variable>
   <xsl:variable name="token" select="substring-after($type-token, ':')"/>
   <xsl:if test="normalize-space($token)">
     <xsl:variable name="constant_token">
        <xsl:call-template name="asJavaConstantName">
           <xsl:with-param name="value" select="normalize-space($token)"/>
        </xsl:call-template>
     </xsl:variable>
                     "<xsl:value-of select="$constant_token"/>" <xsl:if
               test="contains($tokens, $DEFAULT_STRING_SEPARATOR)">
         <xsl:text>,</xsl:text>
      </xsl:if>  // PARAM_<xsl:value-of select="$constant_token"/>
      <xsl:call-template name="inner-clazz-construct-parameter-array">
         <xsl:with-param name="tokens"
            select="substring-after($tokens, $DEFAULT_STRING_SEPARATOR)"/>
      </xsl:call-template>
   </xsl:if>
</xsl:template>

<xsl:template name="inner-clazz-log-parameter-list">
   <xsl:param name="tokens" select="''"/>
   <xsl:param name="isAudit" select="false()"/>
   <xsl:param name="isFirst" select="true()"/>

   <xsl:if test="$isAudit"><xsl:if test="not($isFirst)">, </xsl:if>
      AuditPrincipal auditPrincipal</xsl:if>
    <xsl:call-template name="inner-clazz-log-parameter-list-sub">
       <xsl:with-param name="tokens" select="$tokens"/>
       <xsl:with-param name="isFirst" select="not($isAudit) and $isFirst"/>
    </xsl:call-template>
</xsl:template>
<xsl:template name="inner-clazz-log-parameter-list-sub">
   <xsl:param name="tokens" select="''"/>
   <xsl:param name="isFirst"/>
   <xsl:variable name="type-token">
      <xsl:call-template name="string-set-next-token">
         <xsl:with-param name="s" select="$tokens"/>
      </xsl:call-template>
   </xsl:variable>
   <xsl:variable name="token" select="substring-after($type-token, ':')"/>
   <xsl:variable name="display_token">
      <xsl:call-template name="asJavaParameter">
         <xsl:with-param name="name" select="$token"/>
      </xsl:call-template>
   </xsl:variable>
   <xsl:variable name="type" select="substring-before($type-token, ':')"/>
   <xsl:choose>
     <xsl:when test="normalize-space($token) and normalize-space($type)">
           <xsl:if test="not($isFirst)"><xsl:text>, </xsl:text></xsl:if>
              <xsl:value-of select="$type"/><xsl:text> </xsl:text><xsl:value-of select="$display_token"/>
        <xsl:call-template name="inner-clazz-log-parameter-list-sub">
           <xsl:with-param name="tokens"
              select="substring-after($tokens, $DEFAULT_STRING_SEPARATOR)"/>
           <xsl:with-param name="isFirst" select="false()"/>
        </xsl:call-template>
     </xsl:when>
     <xsl:otherwise>
       <xsl:if test="normalize-space($tokens)">
          <xsl:call-template name="inner-clazz-log-parameter-list-sub">
             <xsl:with-param name="tokens"
                select="substring-after($tokens, $DEFAULT_STRING_SEPARATOR)"/>
             <xsl:with-param name="isFirst" select="$isFirst"/>
          </xsl:call-template>
       </xsl:if>
     </xsl:otherwise>
   </xsl:choose>
</xsl:template>

<xsl:template name="has-expicit-parameters">
   <xsl:param name="tokens" select="''"/>
   <xsl:variable name="type-token">
      <xsl:call-template name="string-set-next-token">
         <xsl:with-param name="s" select="$tokens"/>
      </xsl:call-template>
   </xsl:variable>
   <xsl:variable name="token" select="substring-after($type-token, ':')"/>
   <xsl:variable name="type" select="substring-before($type-token, ':')"/>
   <xsl:choose>
     <xsl:when test="normalize-space($token) and normalize-space($type)">true</xsl:when>
     <xsl:when test="normalize-space($tokens)">
          <xsl:call-template name="has-expicit-parameters">
             <xsl:with-param name="tokens"
                select="substring-after($tokens, $DEFAULT_STRING_SEPARATOR)"/>
          </xsl:call-template>
     </xsl:when>
     <xsl:otherwise>false</xsl:otherwise>
   </xsl:choose>
</xsl:template>


<xsl:template name="exception-clazz-member-list">
   <xsl:param name="tokens" select="''"/>
   <xsl:variable name="type-token">
      <xsl:call-template name="string-set-next-token">
         <xsl:with-param name="s" select="$tokens"/>
      </xsl:call-template>
   </xsl:variable>
   <xsl:variable name="token" select="substring-after($type-token, ':')"/>
   <xsl:variable name="display_token">
      <xsl:call-template name="asJavaParameter">
         <xsl:with-param name="name" select="concat('M_', $token)"/>
      </xsl:call-template>
   </xsl:variable>
   <xsl:variable name="type" select="substring-before($type-token, ':')"/>
   <xsl:if test="normalize-space($token) and normalize-space($type)">
   private final <xsl:value-of select="$type"/><xsl:text> </xsl:text><xsl:value-of select="$display_token"/>;
      <xsl:call-template name="exception-clazz-member-list">
         <xsl:with-param name="tokens"
            select="substring-after($tokens, $DEFAULT_STRING_SEPARATOR)"/>
      </xsl:call-template>
   </xsl:if>
</xsl:template>


<xsl:template name="exception-clazz-member-init">
   <xsl:param name="tokens" select="''"/>
   <xsl:param name="with" select="'param'"/>
   <xsl:variable name="type-token">
      <xsl:call-template name="string-set-next-token">
         <xsl:with-param name="s" select="$tokens"/>
      </xsl:call-template>
   </xsl:variable>
   <xsl:variable name="token" select="substring-after($type-token, ':')"/>
   <xsl:variable name="member_token">
      <xsl:call-template name="asJavaParameter">
         <xsl:with-param name="name" select="concat('M_', $token)"/>
      </xsl:call-template>
   </xsl:variable>
   <xsl:variable name="display_token">
      <xsl:call-template name="asJavaParameter">
         <xsl:with-param name="name" select="$token"/>
      </xsl:call-template>
   </xsl:variable>
   <xsl:variable name="type" select="substring-before($type-token, ':')"/>
   <xsl:if test="normalize-space($token) and normalize-space($type)">
   <xsl:value-of select="$member_token"/> = <xsl:choose><xsl:when
      test="$with = 'param'"><xsl:value-of select="$display_token"/></xsl:when>
      <xsl:otherwise><xsl:value-of select="$with"/></xsl:otherwise></xsl:choose>;
      <xsl:call-template name="exception-clazz-member-init">
         <xsl:with-param name="tokens"
            select="substring-after($tokens, $DEFAULT_STRING_SEPARATOR)"/>
         <xsl:with-param name="with" select="$with"/>
      </xsl:call-template>
   </xsl:if>
</xsl:template>


<xsl:template name="exception-clazz-member-getter">
   <xsl:param name="tokens" select="''"/>
   <xsl:variable name="type-token">
      <xsl:call-template name="string-set-next-token">
         <xsl:with-param name="s" select="$tokens"/>
      </xsl:call-template>
   </xsl:variable>
   <xsl:variable name="token" select="substring-after($type-token, ':')"/>
   <xsl:variable name="display_token">
      <xsl:call-template name="asJavaParameter">
         <xsl:with-param name="name" select="$token"/>
      </xsl:call-template>
   </xsl:variable>
   <xsl:variable name="identifier">
      <xsl:call-template name="asJavaIdentifier">
         <xsl:with-param name="name" select="$token"/>
      </xsl:call-template>
   </xsl:variable>
   <xsl:variable name="member_token">
      <xsl:call-template name="asJavaParameter">
         <xsl:with-param name="name" select="concat('M_', $token)"/>
      </xsl:call-template>
   </xsl:variable>
   <xsl:variable name="type" select="substring-before($type-token, ':')"/>
   <xsl:if test="normalize-space($token) and normalize-space($type)">
   /**
    * Returns the value of the parameter &lt;tt><xsl:value-of select="$display_token"/>&lt;/tt>.
    * @return the value of the parameter &lt;tt><xsl:value-of select="$display_token"/>&lt;/tt>.
    */
   public <xsl:value-of select="$type"/> valueOf<xsl:value-of select="$identifier"/> ()
   {
      return <xsl:value-of select="$member_token"/>;
   }
      <xsl:call-template name="exception-clazz-member-getter">
         <xsl:with-param name="tokens"
            select="substring-after($tokens, $DEFAULT_STRING_SEPARATOR)"/>
      </xsl:call-template>
   </xsl:if>
</xsl:template>



<xsl:template name="inner-clazz-log-parameter-javadoc">
   <xsl:param name="tokens" select="''"/>
   <xsl:param name="isAudit" select="false()"/>
   <xsl:variable name="type-token">
      <xsl:call-template name="string-set-next-token">
         <xsl:with-param name="s" select="$tokens"/>
      </xsl:call-template>
   </xsl:variable>
   <xsl:variable name="token" select="substring-after($type-token, ':')"/>
   <xsl:variable name="type" select="substring-before($type-token, ':')"/>
   <xsl:variable name="display_token">
      <xsl:call-template name="asJavaParameter">
         <xsl:with-param name="name" select="$token"/>
      </xsl:call-template>
   </xsl:variable>
   <xsl:variable name="display_text">
      <xsl:call-template name="asDisplayName">
         <xsl:with-param name="name" select="$display_token"/>
      </xsl:call-template>
   </xsl:variable>
   <xsl:if test="$isAudit">
       * @param auditPrincipal The audit principal that causes this
       *      log event.
   </xsl:if>
   <xsl:if test="normalize-space($token) and normalize-space($type)">
       * @param <xsl:value-of select="$display_token"/> The <xsl:value-of
               select="$display_text"/> used in the message.<xsl:call-template
               name="inner-clazz-log-parameter-javadoc">
         <xsl:with-param name="tokens"
            select="substring-after($tokens, $DEFAULT_STRING_SEPARATOR)"/>
      </xsl:call-template>
   </xsl:if>
</xsl:template>


<xsl:template name="inner-clazz-log-parameter-call">
   <xsl:param name="tokens" select="''"/>
   <xsl:param name="isFirst" select="true()"/>
   <xsl:variable name="type-token">
      <xsl:call-template name="string-set-next-token">
         <xsl:with-param name="s" select="$tokens"/>
      </xsl:call-template>
   </xsl:variable>
   <xsl:variable name="token" select="substring-after($type-token, ':')"/>
   <xsl:variable name="type" select="substring-before($type-token, ':')"/>
   <xsl:variable name="display_token">
      <xsl:call-template name="asJavaParameter">
         <xsl:with-param name="name" select="$token"/>
      </xsl:call-template>
   </xsl:variable>
   <xsl:choose>
     <xsl:when test="normalize-space($token) and normalize-space($type)">
       <xsl:if test="not($isFirst)"><xsl:text>, </xsl:text>
       </xsl:if><xsl:value-of select="$display_token"/>
       <xsl:call-template name="inner-clazz-log-parameter-call">
           <xsl:with-param name="tokens"
              select="substring-after($tokens, $DEFAULT_STRING_SEPARATOR)"/>
           <xsl:with-param name="isFirst" select="false()"/>
        </xsl:call-template>
     </xsl:when>
     <xsl:otherwise>
       <xsl:if test="$tokens">
         <xsl:call-template name="inner-clazz-log-parameter-call">
             <xsl:with-param name="tokens"
                select="substring-after($tokens, $DEFAULT_STRING_SEPARATOR)"/>
             <xsl:with-param name="isFirst" select="$isFirst"/>
          </xsl:call-template>
       </xsl:if>
     </xsl:otherwise>
   </xsl:choose>
</xsl:template>

<xsl:template name="inner-clazz-construct-parameter-setter">
   <xsl:param name="tokens" select="''"/>
   <xsl:variable name="type-token">
      <xsl:call-template name="string-set-next-token">
         <xsl:with-param name="s" select="$tokens"/>
      </xsl:call-template>
   </xsl:variable>
   <xsl:variable name="token" select="substring-after($type-token, ':')"/>
   <xsl:variable name="type" select="substring-before($type-token, ':')"/>
   <xsl:if test="normalize-space($token) and normalize-space($type)">
     <xsl:variable name="display_token">
        <xsl:call-template name="asJavaParameter">
           <xsl:with-param name="name" select="$token"/>
        </xsl:call-template>
     </xsl:variable>
     <xsl:variable name="constant_token">
        <xsl:call-template name="asJavaConstantName">
           <xsl:with-param name="value" select="normalize-space($token)"/>
        </xsl:call-template>
     </xsl:variable>
         loggable.addParameter(PARAM_<xsl:value-of
            select="$constant_token"/>, <xsl:value-of
            select="$display_token"/>);<xsl:call-template
            name="inner-clazz-construct-parameter-setter">
         <xsl:with-param name="tokens"
            select="substring-after($tokens, $DEFAULT_STRING_SEPARATOR)"/>
      </xsl:call-template>
   </xsl:if>
</xsl:template>


<xsl:template name="find-tokens">
   <xsl:param name="s" select="''"/>
   <xsl:param name="found-tokens" select="''"/>
   <xsl:choose>
   <xsl:when test="contains($s, '{') and contains($s, '}')">
      <xsl:variable name="raw-token"
         select="substring-before(substring-after($s, '{'), '}')"/>
      <xsl:variable name="token">
         <xsl:choose>
            <xsl:when test="contains($raw-token, ',')">
               <xsl:value-of select="substring-before($raw-token, ',')"/>
            </xsl:when>
            <xsl:otherwise>
               <xsl:value-of select="$raw-token"/>
            </xsl:otherwise>
         </xsl:choose>
      </xsl:variable>
      <xsl:variable name="new-found-tokens">
         <xsl:call-template name="append-to-string-set">
            <xsl:with-param name="set" select="$found-tokens"/>
            <xsl:with-param name="s" select="$token"/>
         </xsl:call-template>
      </xsl:variable>
      <xsl:call-template name="find-tokens">
         <xsl:with-param name="s" select="substring-after($s, '}')"/>
         <xsl:with-param name="found-tokens" select="$new-found-tokens"/>
      </xsl:call-template>
   </xsl:when>
   <xsl:otherwise>
      <xsl:value-of select="$found-tokens"/>
   </xsl:otherwise>
   </xsl:choose>
</xsl:template>

<xsl:template name="append-to-string-set">
   <xsl:param name="set" select="''"/>
   <xsl:param name="s" select="''"/>
   <xsl:param name="string-separator"
      select="$DEFAULT_STRING_SEPARATOR"/>
   <xsl:choose>
      <xsl:when test="not($set)">
         <xsl:value-of select="$s"/>
      </xsl:when>
      <xsl:when test="$set and contains($set, $s)">
         <xsl:value-of select="$set"/>
      </xsl:when>
      <xsl:otherwise>
         <xsl:value-of select="concat($set, $string-separator, $s)"/>
      </xsl:otherwise>
   </xsl:choose>
</xsl:template>


<xsl:template name="str-replace">
   <xsl:param name="s" select="''"/>
   <xsl:param name="old" select="''"/>
   <xsl:param name="new" select="''"/>
   <xsl:choose>
      <xsl:when test="contains($s, $old)">
         <xsl:value-of select="substring-before($s, $old)"/>
         <xsl:value-of select="$new"/>
         <xsl:call-template name="str-replace">
            <xsl:with-param name="s" select="substring-after($s, $old)"/>
            <xsl:with-param name="new" select="$new"/>
            <xsl:with-param name="old" select="$old"/>
         </xsl:call-template>
      </xsl:when>
      <xsl:otherwise>
         <xsl:value-of select="$s"/>
      </xsl:otherwise>
   </xsl:choose>
</xsl:template>

<xsl:template name="string-set-next-token">
   <xsl:param name="s" select="''"/>
   <xsl:param name="separator" select="$DEFAULT_STRING_SEPARATOR"/>
   <xsl:variable name="x">
      <xsl:choose>
         <xsl:when test="contains($s, $separator)">
            <xsl:value-of select="substring-before($s, $separator)"/>
         </xsl:when>
         <xsl:otherwise>
            <xsl:value-of select="$s"/>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:variable>
   <xsl:choose>
      <xsl:when test="not(normalize-space($x))">
         <xsl:value-of select="$x"/>
      </xsl:when>
      <xsl:when test="contains($x, ':')">
         <xsl:value-of select="$x"/>
      </xsl:when>
      <xsl:otherwise>
         <xsl:value-of select="concat('String:', $x)"/>
      </xsl:otherwise>
   </xsl:choose>
</xsl:template>


<xsl:template name="replace-token-with-number">
   <xsl:param name="text" select="''"/>
   <xsl:param name="tokens" select="''"/>
   <xsl:param name="number" select="'0'"/>
   <xsl:param name="string-separator" select="$DEFAULT_STRING_SEPARATOR"/>
   <xsl:variable name="pretoken">
      <xsl:call-template name="string-set-next-token">
         <xsl:with-param name="s" select="$tokens"/>
      </xsl:call-template>
   </xsl:variable>
   <xsl:variable name="token">
      <xsl:choose>
         <xsl:when test="contains($text, $pretoken)">
           <xsl:value-of select="$pretoken"/>
         </xsl:when>
         <xsl:otherwise>
           <xsl:value-of select="substring-after($pretoken, 'String:')"/>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:variable>
   <xsl:choose>
      <xsl:when test="normalize-space($token)">
         <xsl:variable name="modified-text">
            <xsl:call-template name="str-replace">
               <xsl:with-param name="s" select="$text"/>
               <xsl:with-param name="old" select="$token"/>
               <xsl:with-param name="new" select="$number"/>
            </xsl:call-template>
         </xsl:variable>
         <xsl:call-template name="replace-token-with-number">
            <xsl:with-param name="text" select="$modified-text"/>
            <xsl:with-param name="tokens"
               select="substring-after($tokens, $DEFAULT_STRING_SEPARATOR)"/>
            <xsl:with-param name="number" select="$number + 1"/>
         </xsl:call-template>
      </xsl:when>
      <xsl:otherwise>
         <xsl:value-of select="$text"/>
      </xsl:otherwise>
   </xsl:choose>
</xsl:template>

</xsl:stylesheet>
