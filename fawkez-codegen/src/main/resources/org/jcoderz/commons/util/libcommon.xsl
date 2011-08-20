<?xml version="1.0" encoding="UTF-8"?>
<!--
   $Id: libcommon.xsl 1646 2011-04-21 15:38:45Z amandel $

   Collects common XSL templates.

   Author: Michael Griffel, Andreas Mandel
  -->
<xsl:stylesheet
   version="1.0"
   xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
   xmlns:xs="http://www.w3.org/2001/XMLSchema">

<!-- ===============================================================
     C O N S T A N T S
     =============================================================== -->
<xsl:variable name="lowercase-a_z" select="'abcdefghijklmnopqrstuvwxyz'"/>
<xsl:variable name="uppercase-a_z" select="'ABCDEFGHIJKLMNOPQRSTUVWXYZ'"/>
<xsl:variable name="magic-hashes"  select="'##########################'"/>
<xsl:variable name="java-letter-or-digit"
  select="'abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ_$01234567890'"/>

<!-- ===============================================================
          _        _                             _   _
      ___| |_ _ __(_)_ __   __ _   ___  ___  ___| |_(_) ___  _ __
     / __| __| '__| | '_ \ / _` | / __|/ _ \/ __| __| |/ _ \| '_ \
     \__ \ |_| |  | | | | | (_| | \__ \  __/ (__| |_| | (_) | | | |
     |___/\__|_|  |_|_| |_|\__, | |___/\___|\___|\__|_|\___/|_| |_|
                           |___/
     =============================================================== -->
<!-- converts $s to upper case characters -->
<xsl:template name="toUpperCase">
   <xsl:param name="s"/>
   <xsl:value-of select="translate($s,
      $lowercase-a_z,
      $uppercase-a_z)"/>
</xsl:template>

<!-- converts $s to lower case characters -->
<xsl:template name="toLowerCase">
   <xsl:param name="s"/>
   <xsl:value-of select="translate($s,
      $uppercase-a_z,
      $lowercase-a_z)"/>
</xsl:template>

<!--
   Replaces the character 'char' w/ the string 'new'
  -->
<xsl:template name="replace-char">
   <xsl:param name="s" select="''"/>
   <xsl:param name="char" select="''"/>
   <xsl:param name="new" select="''"/>
   <xsl:param name="pos" select="1"/>
   <xsl:if test="$pos &lt;= string-length($s)">
         <!-- Contains upper case character at position $pos? -->
         <xsl:choose>
            <xsl:when test="substring($s, $pos, 1) = $char">
               <xsl:value-of select="$new"/>
            </xsl:when>
            <xsl:otherwise>
               <xsl:value-of select="substring($s, $pos, 1)"/>
            </xsl:otherwise>
         </xsl:choose>
         <xsl:call-template name="replace-char">
            <xsl:with-param name="s" select="$s"/>
            <xsl:with-param name="char" select="$char"/>
            <xsl:with-param name="new" select="$new"/>
            <xsl:with-param name="pos" select="$pos + 1"/>
         </xsl:call-template>
   </xsl:if>
</xsl:template>

<!--
   Replaces the non java symbol characters
  -->
<xsl:template name="replace-not-java-letters-or-digit">
   <xsl:param name="s" select="''"/>
   <xsl:param name="new" select="'_'"/>
   <xsl:param name="pos" select="1"/>
   <xsl:if test="$pos &lt;= string-length($s)">
     <xsl:variable name="current-char" select="substring($s, $pos, 1)"/>
         <!-- Contains upper case character at position $pos? -->
         <xsl:choose>
            <xsl:when test="not(contains($java-letter-or-digit, $current-char))">
               <xsl:value-of select="$new"/>
            </xsl:when>
            <xsl:otherwise>
               <xsl:value-of select="$current-char"/>
            </xsl:otherwise>
         </xsl:choose>
         <xsl:call-template name="replace-not-java-letters-or-digit">
            <xsl:with-param name="s" select="$s"/>
            <xsl:with-param name="new" select="$new"/>
            <xsl:with-param name="pos" select="$pos + 1"/>
         </xsl:call-template>
   </xsl:if>
</xsl:template>

<!--
   Replaces the string 'old' w/ the string 'new'
   Note: 'old' must not be a substring of 'new'!
  -->
<xsl:template name="replace-string">
   <xsl:param name="s" select="''"/>
   <xsl:param name="old" select="''"/>
   <xsl:param name="new" select="''"/>
   <xsl:choose>
      <xsl:when test="contains($s, $old)">
         <xsl:variable name="next"><xsl:value-of select="substring-before($s, $old)"/>
         <xsl:value-of select="$new"/>
         <xsl:value-of select="substring-after($s, $old)"/>
         </xsl:variable>
         <xsl:call-template name="replace-string">
            <xsl:with-param name="s" select="$next"/>
            <xsl:with-param name="old" select="$old"/>
            <xsl:with-param name="new" select="$new"/>
         </xsl:call-template>
      </xsl:when>
      <xsl:otherwise>
         <xsl:value-of select="$s"/>
      </xsl:otherwise>
   </xsl:choose>
</xsl:template>


<!-- ===============================================================
        _                                  _   _
       (_) __ ___   ____ _   ___  ___  ___| |_(_) ___  _ __
       | |/ _` \ \ / / _` | / __|/ _ \/ __| __| |/ _ \| '_ \
       | | (_| |\ V / (_| | \__ \  __/ (__| |_| | (_) | | | |
      _/ |\__,_| \_/ \__,_| |___/\___|\___|\__|_|\___/|_| |_|
     |__/
     =============================================================== -->

<!-- ===============================================================
     Apply jCoderZ classname rule, e.g.e FooID -> FooId
     =============================================================== -->
<xsl:template name="asCamelCase">
   <xsl:param name="s"/>
   <xsl:param name="pos" select="'1'"/>
   <xsl:param name="c_last" select="'a'"/>
   <xsl:variable name="c" select="substring($s, $pos, 1)"/>
   <xsl:variable name="c_next" select="substring(concat($s, 'A'), $pos + 1, 1)"/>
      <xsl:if test="$c">
         <xsl:variable name="c_new">
            <xsl:choose>
               <xsl:when test="translate($c_last, $lowercase-a_z, $magic-hashes) = '#'">
                  <xsl:value-of select="$c"/>
               </xsl:when>
               <xsl:when test="translate($c_next, $lowercase-a_z, $magic-hashes) = '#'">
                  <xsl:value-of select="$c"/>
               </xsl:when>
               <xsl:otherwise>
                  <xsl:value-of select="translate($c, $uppercase-a_z, $lowercase-a_z)"/>
               </xsl:otherwise>
            </xsl:choose>
         </xsl:variable>
         <xsl:value-of select="$c_new"/>
         <xsl:call-template name="asCamelCase">
            <xsl:with-param name="s" select="$s"/>
            <xsl:with-param name="pos" select="$pos + 1"/>
            <xsl:with-param name="c_last" select="$c"/>
         </xsl:call-template>
   </xsl:if>
</xsl:template>

<!-- ===============================================================
     Converts a abbr. to a Java string, e.g. 'RTE' -> 'Rte'
     =============================================================== -->
<xsl:template name="shortnameToJava">
   <xsl:param name="s"/>
   <xsl:value-of select="substring($s, 1, 1)"/><xsl:call-template
      name="toLowerCase"><xsl:with-param name="s"><xsl:value-of
         select="substring($s, 2)"/></xsl:with-param></xsl:call-template>
</xsl:template>

<!--
   Converts a string to a Java constant name
   examples:
      red to RED
      BingoBongoFooBar to BINGO_BONGO_FOO_BAR
      E-LBX-EXPIRED to E_LBX_EXPIRED
      EL CORTE INGLES to EL_CORTE_INGLES
      Visa Electron to VISA_ELECTRON
-->
<xsl:template name="asJavaConstantName">
   <xsl:param name="value"/>
   <xsl:variable name="the-clean-value">
     <xsl:call-template name="replace-not-java-letters-or-digit">
       <xsl:with-param name="s" select="$value"/>
     </xsl:call-template>
   </xsl:variable>
   <xsl:call-template name="asJavaConstantName-clean">
     <xsl:with-param name="value" select="$the-clean-value"/>
   </xsl:call-template>
</xsl:template>

<xsl:template name="asJavaConstantName-clean">
   <xsl:param name="value"/>

   <xsl:if test="contains('0123456789', substring($value, 1, 1))">
      <xsl:text>V_</xsl:text>
   </xsl:if>
   <xsl:choose>
      <!-- special for ABXtoXYZBingoRequest -->
      <xsl:when test="substring($value, 4, 2) = 'to'
         and not(contains($value, 'ProtocolEngine'))
         and not(contains($value, 'Customer'))">
         <xsl:variable name="mangled-value"><xsl:value-of
            select="substring($value,1,1)"/><xsl:call-template name="toLowerCase">
               <xsl:with-param name="s"><xsl:value-of
                  select="substring($value,2,2)"/></xsl:with-param>
            </xsl:call-template><xsl:text>To</xsl:text><xsl:value-of
               select="substring($value,6,1)"/><xsl:call-template
                  name="toLowerCase">
               <xsl:with-param name="s"><xsl:value-of
                  select="substring($value,7,2)"/></xsl:with-param>
            </xsl:call-template><xsl:value-of select="substring($value, 9)"/>
         </xsl:variable>
         <xsl:call-template name="asJavaConstantName-clean">
            <xsl:with-param name="value"><xsl:value-of
               select="$mangled-value"></xsl:value-of></xsl:with-param>
         </xsl:call-template>
      </xsl:when>
      <!-- no lowercase characters? -->
      <xsl:when test="not(contains(translate($value,
                           $lowercase-a_z,
                           $magic-hashes), '#'))">
         <!-- replace '-', '.' or ' ' with '_' -->
         <xsl:value-of select="translate($value, '- .', '___')"/>
      </xsl:when>
      <!-- whitespaces, '.' or '-' ? -->
      <xsl:when test="contains(translate($value, ' .-', '###'), '#')">
         <!-- replace special chars with '_' and insert Underscore + toUpperCase -->
         <xsl:variable name="foo"><xsl:call-template name="insertUnderscoreBeforeUpperCaseCharacter">
            <xsl:with-param name="s" select="$value"/>
         </xsl:call-template></xsl:variable>
         <xsl:call-template name="toUpperCase">
            <xsl:with-param name="s"><xsl:value-of
               select="translate($foo, ' -.', '___')"/></xsl:with-param>
         </xsl:call-template>
      </xsl:when>
      <xsl:otherwise>
         <xsl:call-template name="insertUnderscoreBeforeUpperCaseCharacter">
            <xsl:with-param name="s" select="$value"/>
         </xsl:call-template>
      </xsl:otherwise>
   </xsl:choose>
</xsl:template>

<!--
   Converts a CamelCase string to a human readable name.
   examples:
      red to red
      BingoBongoFooBar to bingo bongo foo bar
      E-LBX-EXPIRED to e lbx expired
      EL CORTE INGLES to el corte ingles
      Visa Electron to visa electron
      Visa_Electron to visa electron
  -->
<xsl:template name="asDisplayName">
   <xsl:param name="name"/>
   <xsl:param name="pos">1</xsl:param>
   <xsl:variable name="s" select="translate($name, '_- ', '   ')"/>
   <xsl:if test="$pos &lt;= string-length($s)">
         <!-- Contains upper case character at position $pos? -->
         <xsl:if test="contains(translate(substring($s, $pos, 1),
                              $uppercase-a_z,
                              $magic-hashes),
                                 '#') and $pos != 1">
            <!-- ... and previous character is not uppercase -->
            <xsl:if test="not(contains(translate(substring($s, $pos - 1, 1),
                              $uppercase-a_z,
                              $magic-hashes),
                                 '#')) and not(contains(translate(substring($s, $pos - 1, 1),
                                             ' ', '#'), '#'))">
               <xsl:text> </xsl:text>
            </xsl:if>
         </xsl:if>
         <xsl:call-template name="toLowerCase">
            <xsl:with-param name="s" select="substring($s, $pos, 1)"/>
         </xsl:call-template>
      <xsl:call-template name="asDisplayName">
         <xsl:with-param name="name" select="$name"/>
         <xsl:with-param name="pos" select="$pos + 1"/>
      </xsl:call-template>
   </xsl:if>
</xsl:template>

<!--
   Converts a string to a Java identifier.
   examples:
      FOO_BAR to FooBar
  -->
<xsl:template name="asJavaIdentifier">
   <xsl:param name="name"/>
   <xsl:param name="pos">1</xsl:param>
   <!-- normalize -->
   <xsl:variable name="s" select="translate($name, '-_ ', '   ')"/>
   <xsl:if test="$pos &lt;= string-length($s)">
      <xsl:choose>
         <xsl:when test="$pos = 1"> <!-- First char: force to upper case -->
            <xsl:value-of select="translate(substring($s, 1, 1),
                  $lowercase-a_z, $uppercase-a_z)"/>
         </xsl:when>
         <xsl:when test="substring($s, $pos, 1) = ' '"> <!-- whitespace? -->
         </xsl:when>
         <xsl:when test="substring($s, $pos - 1, 1) = ' '"> <!-- previous whitespace? -->
            <xsl:value-of select="translate(substring($s, $pos, 1),
                  $lowercase-a_z, $uppercase-a_z)"/>
         </xsl:when>
         <xsl:when test="contains($lowercase-a_z, substring($s, $pos - 1, 1))"> <!-- previous lowercase? -->
            <xsl:value-of select="substring($s, $pos, 1)"/>
         </xsl:when>
         <xsl:otherwise>
            <xsl:value-of select="translate(substring($s, $pos, 1),
                  $uppercase-a_z, $lowercase-a_z)"/>
         </xsl:otherwise>
      </xsl:choose>
      <xsl:call-template name="asJavaIdentifier">
         <xsl:with-param name="name" select="$name"/>
         <xsl:with-param name="pos" select="$pos + 1"/>
      </xsl:call-template>
   </xsl:if>
</xsl:template>


<!--
   Converts a string to a Java parameter.
   examples:
      FOO_BAR to fooBar
  -->
<xsl:template name="asJavaParameter">
   <xsl:param name="name"/>
   <xsl:param name="pos">1</xsl:param>
   <!-- normalize -->
   <xsl:variable name="s" select="translate($name, '-_ ', '   ')"/>
   <xsl:if test="$pos &lt;= string-length($s)">
      <xsl:if test="$pos = 1"> <!-- First char: force to lower case -->
         <xsl:value-of select="translate(substring($s, 1, 1),
               $uppercase-a_z, $lowercase-a_z)"/>
      </xsl:if>
      <xsl:call-template name="asJavaIdentifier">
         <xsl:with-param name="name" select="$name"/>
         <xsl:with-param name="pos" select="$pos + 1"/>
      </xsl:call-template>
   </xsl:if>
</xsl:template>


<xsl:template name="insertUnderscoreBeforeUpperCaseCharacter">
   <xsl:param name="s"/>
   <xsl:param name="pos">1</xsl:param>
   <xsl:if test="$pos &lt;= string-length($s)">
      <xsl:choose>
         <!-- Contains upper case character at position $pos? -->
         <xsl:when test="contains(translate(substring($s, $pos, 1),
                              $uppercase-a_z,
                              $magic-hashes),
                                 '#') and $pos != 1">
            <!-- ... and previous character is not uppercase nor ' ', '-', '.' or '_' -->
            <xsl:if test="not(contains(translate(substring($s, $pos - 1, 1),
                              $uppercase-a_z,
                              $magic-hashes),
                                 '#'))
                          and not(contains(translate(substring($s, $pos - 1, 1),
                              ' -._', '####'), '#'))">
               <xsl:text>_</xsl:text>
            </xsl:if>
            <xsl:value-of select="substring($s, $pos, 1)"/>
         </xsl:when>
         <xsl:otherwise>
            <xsl:call-template name="toUpperCase">
               <xsl:with-param name="s" select="substring($s, $pos, 1)"/>
            </xsl:call-template>
         </xsl:otherwise>
      </xsl:choose>
      <xsl:call-template name="insertUnderscoreBeforeUpperCaseCharacter">
         <xsl:with-param name="s" select="$s"/>
         <xsl:with-param name="pos" select="$pos + 1"/>
      </xsl:call-template>
   </xsl:if>
</xsl:template>

<!-- ===============================================================
     Generates a list of 'string constants' for each node in $items
     =============================================================== -->
<xsl:template name="java-string-constants">
   <xsl:param name="items"/>
   <xsl:param name="javadoc-prefix"/>

   <xsl:for-each select="$items">
   <xsl:sort select="."/>
   /**
    * <xsl:value-of select="$javadoc-prefix"/><xsl:text> &lt;code&gt;</xsl:text><xsl:value-of select="."/><xsl:text>&lt;/code&gt;.</xsl:text>
    */
   public static final String <xsl:call-template name="asJavaConstantName">
      <xsl:with-param name="value" select="."/>
   </xsl:call-template>
         = "<xsl:value-of select="."/>";
  </xsl:for-each>
</xsl:template>


<!-- ===============================================================
     escapes string to a java string.
     =============================================================== -->
<xsl:template name="java-string-escape">
   <xsl:param name="s" select="''"/>
   <xsl:variable name="stepOne">
   <xsl:call-template name="replace-char">
      <xsl:with-param name="s" select="$s"/>
      <xsl:with-param name="char" select="'\'"/>
      <xsl:with-param name="new" select="'\\'"/>
   </xsl:call-template>
   </xsl:variable>
   <xsl:call-template name="replace-char">
      <xsl:with-param name="s" select="$stepOne"/>
      <xsl:with-param name="char" select="'&quot;'"/>
      <xsl:with-param name="new" select="'\&quot;'"/>
   </xsl:call-template>
</xsl:template>


<!-- ===============================================================
     generates a single constant with string constructor
     =============================================================== -->
<xsl:template name="java-constant">
   <xsl:param name="type"/>
   <xsl:param name="name"/>
   <xsl:param name="value"/>
   <xsl:param name="comment"/>
   <xsl:param name="quote-char" select="'&quot;'"/>
   /**<xsl:text> </xsl:text><xsl:value-of select="normalize-space($comment)"/><xsl:text> </xsl:text>*/
   public static final <xsl:value-of select="$type"/><xsl:text> </xsl:text><xsl:value-of select="$name"/>
         = new <xsl:value-of select="$type"/>(<xsl:value-of select="$quote-char"/><xsl:value-of select="$value"/><xsl:value-of select="$quote-char"/>);
</xsl:template>

<!-- ===============================================================
     generates a single constant with fromString factory
     =============================================================== -->
<xsl:template name="java-constant-from-string">
   <xsl:param name="type"/>
   <xsl:param name="name"/>
   <xsl:param name="value"/>
   <xsl:param name="comment"/>
   <xsl:param name="quote-char" select="'&quot;'"/>
   /**<xsl:text> </xsl:text><xsl:value-of select="normalize-space($comment)"/><xsl:text> </xsl:text>*/
   public static final <xsl:value-of select="$type"/><xsl:text> </xsl:text><xsl:value-of select="$name"/>
         = <xsl:value-of select="$type"/>.fromString(<xsl:value-of select="$quote-char"/><xsl:value-of select="$value"/><xsl:value-of select="$quote-char"/>);
</xsl:template>

<!-- ===============================================================
     Value Object Generator
     =============================================================== -->
<xsl:template name="value-object-generator">
   <xsl:param name="classname"/>
   <xsl:param name="package"/>
   <xsl:param name="object"/>

   <xsl:variable name="name"><xsl:call-template
      name="asDisplayName"><xsl:with-param
         name="name" select="$classname"/></xsl:call-template></xsl:variable>
<xsl:call-template name="java-copyright-header"/>
package <xsl:value-of select="$package"/>;

import org.jcoderz.commons.util.HashCodeUtil;
import org.jcoderz.commons.util.ObjectUtil;

/**
 * <xsl:if test="not($object/description)"><xsl:value-of select="$name"/> value object.</xsl:if>
 * <xsl:value-of select="$object/description"/><xsl:call-template name="generate-xdoclet">
   <xsl:with-param name="doc-text" select="$object/xdoclet" />
   <xsl:with-param name="indent"><xsl:text> </xsl:text></xsl:with-param>
</xsl:call-template>
 * @author generated
 */
public <xsl:if test="$object/@final = 'true'">final </xsl:if>class <xsl:value-of select="$classname"/><xsl:if test="$object/@baseclass">
  extends <xsl:value-of select="$object/@baseclass"/></xsl:if><xsl:if test="$object/@serializable or $object/@implements">
      implements <xsl:if test="$object/@serializable">java.io.Serializable<xsl:if test="$object/@implements">, </xsl:if></xsl:if><xsl:value-of select="$object/@implements"/></xsl:if>
{
<xsl:if test="$object/@serializable">
   private static final long serialVersionUID = 1L;</xsl:if>
   <xsl:for-each select="$object/member">
   private <xsl:if test="@final = 'true' or ../@final = 'true'">final </xsl:if>
      <xsl:value-of select="@type"/> m<xsl:call-template
      name="asJavaIdentifier">
         <xsl:with-param name="name" select="@name"/></xsl:call-template>;</xsl:for-each>

   <xsl:variable name="minimum-argument-count"><xsl:copy-of
      select="count($object/member[not(@initial-value)][@final = 'true' or ../@final = 'true'])"/>
   </xsl:variable>
   <xsl:variable name="maximum-argument-count"><xsl:copy-of
      select="count($object/member[not(@initial-value)])"/>
   </xsl:variable>
   <xsl:if test="$minimum-argument-count != $maximum-argument-count">


   /**
    * Constructs a <xsl:value-of
      select="$classname"/> with the minimum arguments.<xsl:for-each
      select="$object/member[not(@initial-value)][@final = 'true' or ../@final = 'true']">
   <xsl:variable name="identifier"><xsl:call-template
      name="asJavaIdentifier">
         <xsl:with-param name="name" select="@name"/></xsl:call-template>
   </xsl:variable>
    * @param a<xsl:value-of select="$identifier"/> The <xsl:value-of
    select="normalize-space(.)"/>.<xsl:if test="@copyValue = 'clone'">
    *   The value is cloned before being stored.</xsl:if><xsl:if test="@copyValue = 'constructor'">
    *   The value is copied using the copy constructor before being stored.</xsl:if></xsl:for-each>
    */
    public <xsl:value-of select="$classname"/> (<xsl:for-each
      select="$object/member[not(@initial-value)][@final = 'true' or ../@final = 'true']">
       <xsl:variable name="identifier"><xsl:call-template
          name="asJavaIdentifier">
             <xsl:with-param name="name" select="./@name"/></xsl:call-template>
       </xsl:variable>
       <xsl:value-of select="@type"/> a<xsl:value-of select="$identifier"/>
       <xsl:if test="position() != last()">,
       </xsl:if>
       </xsl:for-each>)
    {  <xsl:for-each select="$object/member[not(@initial-value)][@final = 'true' or ../@final = 'true']">
       <xsl:variable name="identifier"><xsl:call-template
          name="asJavaIdentifier">
             <xsl:with-param name="name" select="@name"/></xsl:call-template>
       </xsl:variable>
       m<xsl:value-of select="$identifier"/> = <xsl:choose>
       <xsl:when test="@copyValue = 'clone'">a<xsl:value-of select="$identifier"/> == null
         ? null : (<xsl:value-of select="@type"/>) a<xsl:value-of select="$identifier"/>.clone();</xsl:when>
       <xsl:when test="@copyValue = 'constructor'">a<xsl:value-of select="$identifier"/> == null
         ? null : new <xsl:value-of select="@type"/>(a<xsl:value-of select="$identifier"/>);</xsl:when>
     <xsl:otherwise>a<xsl:value-of select="$identifier"/>;</xsl:otherwise></xsl:choose></xsl:for-each>
     <xsl:for-each select="$object/member[@initial-value]">
       m<xsl:call-template name="asJavaIdentifier">
         <xsl:with-param name="name" select="@name"/>
       </xsl:call-template> = <xsl:value-of select="@initial-value"/>;</xsl:for-each>
    }</xsl:if>

   /**
    * Constructs a <xsl:value-of
      select="$classname"/> with all arguments.<xsl:for-each
      select="$object/member[not(@initial-value)]">
   <xsl:variable name="identifier"><xsl:call-template
      name="asJavaIdentifier">
         <xsl:with-param name="name" select="@name"/></xsl:call-template>
   </xsl:variable>
    * @param a<xsl:value-of select="$identifier"/> The <xsl:value-of
    select="normalize-space(.)"/>.<xsl:if test="@copyValue = 'clone'">
    *   The value is cloned before being stored.</xsl:if><xsl:if test="@copyValue = 'constructor'">
    *   The value is copied using the copy constructor before being stored.</xsl:if></xsl:for-each>
    */
    public <xsl:value-of select="$classname"/> (<xsl:for-each
      select="$object/member[not(@initial-value)]">
       <xsl:variable name="identifier"><xsl:call-template
          name="asJavaIdentifier">
             <xsl:with-param name="name" select="@name"/></xsl:call-template>
       </xsl:variable>
       <xsl:value-of select="@type"/> a<xsl:value-of select="$identifier"/>
       <xsl:if test="position() != last()">,
       </xsl:if>
       </xsl:for-each>)
    {  <xsl:for-each select="$object/member[not(@initial-value)]">
       <xsl:variable name="identifier"><xsl:call-template
          name="asJavaIdentifier">
             <xsl:with-param name="name" select="@name"/></xsl:call-template>
       </xsl:variable>
       m<xsl:value-of select="$identifier"/> = <xsl:choose>
         <xsl:when test="@copyValue = 'clone'">a<xsl:value-of select="$identifier"/> == null
         ? null : (<xsl:value-of select="@type"/>) a<xsl:value-of select="$identifier"/>.clone();</xsl:when>
         <xsl:when test="@copyValue = 'constructor'">a<xsl:value-of select="$identifier"/> == null
         ? null : new <xsl:value-of select="@type"/>(a<xsl:value-of select="$identifier"/>);</xsl:when>
         <xsl:otherwise>a<xsl:value-of select="$identifier"/>;</xsl:otherwise>
     </xsl:choose></xsl:for-each>
     <xsl:for-each select="$object/member[@initial-value]">
       m<xsl:call-template name="asJavaIdentifier">
         <xsl:with-param name="name" select="@name"/>
        </xsl:call-template> = <xsl:value-of select="@initial-value"/>;</xsl:for-each>
    }

  <!-- Do not provide the copy constructor if we if we overide a baseclass.  -->
  <xsl:if test="not($object/@baseclass)">
   /**
    * Copy constructor for <xsl:value-of
      select="$classname"/>. No deep copy is performed.
    * @param a<xsl:value-of select="$classname"/> The <xsl:value-of select="$classname"/>
    *   to be copied.
    */
    public <xsl:value-of select="$classname"/> (<xsl:value-of
    select="$classname"/> a<xsl:value-of select="$classname"/>)
    {  <xsl:for-each select="$object/member">
       <xsl:variable name="identifier"><xsl:call-template
          name="asJavaIdentifier">
             <xsl:with-param name="name" select="@name"/></xsl:call-template>
       </xsl:variable>
       m<xsl:value-of select="$identifier"/>
         = a<xsl:value-of select="$classname"/>.get<xsl:value-of
            select="$identifier"/>();</xsl:for-each>
    }
  </xsl:if>

   <xsl:for-each select="$object/member">
   <xsl:variable name="display-name"><xsl:call-template name="asDisplayName"><xsl:with-param
         name="name" select="@name"/></xsl:call-template>
   </xsl:variable>
   <xsl:variable name="identifier"><xsl:call-template
      name="asJavaIdentifier">
         <xsl:with-param name="name" select="@name"/></xsl:call-template>
   </xsl:variable>
   <xsl:variable name="doc-plain">
     <xsl:value-of select="normalize-space(current())"/>
   </xsl:variable>
   <xsl:variable name="doc">
     <xsl:choose>
       <xsl:when test="normalize-space(current())"><xsl:value-of select="$doc-plain"/></xsl:when>
       <xsl:otherwise><xsl:value-of select="$display-name"/></xsl:otherwise>
     </xsl:choose>
   </xsl:variable>
   /**
    * Returns the <xsl:value-of select="$doc"/>. <xsl:call-template name="generate-xdoclet">
   <xsl:with-param name="doc-text" select="current()/xdoclet" />
   <xsl:with-param name="indent"><xsl:text>    </xsl:text></xsl:with-param>
</xsl:call-template><xsl:if test="@copyValue = 'clone'">
    * The value is cloned before being returned.</xsl:if><xsl:if test="@copyValue = 'constructor'">
    * The value is copied using the copy constructor before being returned.</xsl:if>
    * @return the <xsl:value-of select="$doc"/>.
    */
   public <xsl:value-of select="@type"/> get<xsl:value-of select="$identifier"/> ()
   {
     return <xsl:choose>
       <xsl:when test="@copyValue = 'clone'">m<xsl:value-of select="$identifier"/> == null
         ? null : (<xsl:value-of select="@type"/>) m<xsl:value-of select="$identifier"/>.clone();
       </xsl:when>
       <xsl:when test="@copyValue = 'constructor'">m<xsl:value-of select="$identifier"/> == null
         ? null : new <xsl:value-of select="@type"/>(m<xsl:value-of select="$identifier"/>);
       </xsl:when>
     <xsl:otherwise>m<xsl:value-of select="$identifier"/>;
     </xsl:otherwise>
   </xsl:choose>
   }
   <xsl:if test="@type = 'boolean' or @type = 'Boolean' or @type = 'java.lang.Boolean'">
   /**
    * Returns the <xsl:value-of select="$doc"/>. <xsl:call-template name="generate-xdoclet">
   <xsl:with-param name="doc-text" select="current()/xdoclet" />
   <xsl:with-param name="indent"><xsl:text>    </xsl:text></xsl:with-param>
</xsl:call-template><xsl:if test="@copyValue = 'clone'">
    * The value is cloned before being returned.</xsl:if><xsl:if test="@copyValue = 'constructor'">
    * The value is copied using the copy constructor before being returned.</xsl:if>
    * @return the <xsl:value-of select="$doc"/>.
    */
   public <xsl:value-of select="@type"/> is<xsl:value-of select="$identifier"/> ()
   {
     return get<xsl:value-of select="$identifier"/>();
   }
   </xsl:if>
   <xsl:if test="not(@final = 'true' or ../@final = 'true')">

   <xsl:variable name="setter-visibility"><xsl:choose><xsl:when
    test="@setter-visibility"><xsl:value-of select="@setter-visibility"/></xsl:when>
    <xsl:otherwise>public</xsl:otherwise></xsl:choose></xsl:variable>

   /**
    * Sets the <xsl:value-of select="$doc"/>.<xsl:if test="@copyValue = 'clone'">
    * The value is cloned before being stored.</xsl:if><xsl:if test="@copyValue = 'constructor'">
    * The value is copied using the copy constructor before being stored.</xsl:if>
    * @param a<xsl:value-of select="$identifier"/> the <xsl:value-of select="$doc"/> to be set.
    */
   <xsl:value-of select="$setter-visibility"/> void set<xsl:value-of select="$identifier"/> (<xsl:value-of select="@type"/> a<xsl:value-of select="$identifier"/>)
   {
      m<xsl:value-of select="$identifier"/> = <xsl:choose>
       <xsl:when test="@copyValue = 'clone'">a<xsl:value-of select="$identifier"/> == null
         ? null : (<xsl:value-of select="./@type"/>) a<xsl:value-of select="$identifier"/>.clone();
       </xsl:when>
       <xsl:when test="@copyValue = 'constructor'">a<xsl:value-of select="$identifier"/> == null
         ? null : new <xsl:value-of select="@type"/>(a<xsl:value-of select="$identifier"/>);
       </xsl:when>
     <xsl:otherwise>a<xsl:value-of select="$identifier"/>;
     </xsl:otherwise>
   </xsl:choose>
   }</xsl:if>
   </xsl:for-each>

   /**
    * Creates a String representation of this object holding the
    * state of all members.
    * @return a String representation of this object.
    */
   public String toString()
   {
      final StringBuffer buffer = new StringBuffer();
      buffer.append("[<xsl:value-of select="$classname"/>:");<xsl:if
       test="$object/@baseclass">
      buffer.append(" super: ");
      buffer.append(super.toString());</xsl:if><xsl:for-each
         select="$object/member"><xsl:variable
      name="identifier"><xsl:call-template
         name="asJavaIdentifier">
            <xsl:with-param name="name" select="@name"/></xsl:call-template>
      </xsl:variable>
      buffer.append(" m<xsl:value-of select="$identifier"/>: ");
      buffer.append(m<xsl:value-of select="$identifier"/>);</xsl:for-each>
      buffer.append("]");
      return buffer.toString();
   }

   /**
    * Override hashCode.
    *
    * @return the Objects hashcode.
    */
   public int hashCode()
   {
      int hashCode = <xsl:choose>
        <xsl:when test="$object/@baseclass">super.hashCode()</xsl:when>
        <xsl:otherwise>HashCodeUtil.SEED</xsl:otherwise>
      </xsl:choose>;
      <xsl:for-each
      select="$object/member[not(@identity-independent)]">
      <xsl:variable name="identifier"><xsl:call-template
         name="asJavaIdentifier">
            <xsl:with-param name="name" select="@name"/></xsl:call-template>
      </xsl:variable>
      hashCode = HashCodeUtil.hash(hashCode, get<xsl:value-of
            select="$identifier"/>());</xsl:for-each>
      return hashCode;
   }

   /**
    * Returns &lt;code>true&lt;/code> if this &lt;code><xsl:value-of select="$classname"/>&lt;/code>
    * is equal to &lt;tt>object&lt;/tt>.
    * @param object the object to compare to.
    * @return &lt;code>true&lt;/code> if this &lt;code><xsl:value-of select="$classname"/>&lt;/code>
    *       is equal to &lt;tt>object&lt;/tt>.
    */
   public boolean equals (Object object)
   {
      final boolean result;
      if (this == object)
      {
         result = true;
      }
      else if (object instanceof <xsl:value-of select="$classname"/>)
      {
         final <xsl:value-of select="$classname"/> o = (<xsl:value-of select="$classname"/>) object;
         result = <xsl:choose>
        <xsl:when test="$object/@baseclass">super.equals(object)</xsl:when>
        <xsl:otherwise>true</xsl:otherwise>
      </xsl:choose><xsl:for-each select="$object/member[not(@identity-independent)]">
            <xsl:variable name="identifier"><xsl:call-template
                  name="asJavaIdentifier"><xsl:with-param
                  name="name" select="@name"/></xsl:call-template>
            </xsl:variable>
               &amp;&amp; ObjectUtil.equals(get<xsl:value-of
                  select="$identifier"/>(), o.get<xsl:value-of
                  select="$identifier"/>())</xsl:for-each>;
      }
      else
      {
         result = false;
      }
      return result;
   }
}

</xsl:template>

<!-- ===============================================================
     Type-safe Enumeration Generator
     =============================================================== -->
<xsl:template name="simple-enum-generator">
   <xsl:param name="classname"/>
   <xsl:param name="package"/>
   <xsl:param name="values"/>
   <xsl:param name="javadoc"/>
   <xsl:param name="implements" select="''"/>
   <xsl:variable name="name"><xsl:call-template
      name="asDisplayName"><xsl:with-param
         name="name" select="$classname"/></xsl:call-template></xsl:variable>
   <xsl:variable name="class-javadoc"><xsl:choose>
      <xsl:when test="$javadoc"><xsl:value-of select="$javadoc"/></xsl:when>
      <xsl:otherwise>Enumerated type of a <xsl:value-of select="$name"/>.</xsl:otherwise>
      </xsl:choose></xsl:variable>
   <xsl:variable name="numeric" select="boolean($values/@numeric)"/>
<xsl:call-template name="java-copyright-header"/>
package <xsl:value-of select="$package"/>;


import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.HashMap;
import java.util.List;

<xsl:call-template name="simple-enum-generator-import-hook"/>

/**
 * <xsl:value-of select="$class-javadoc"/>
 *
 * Instances of this class are immutable.
 *
 * The following <xsl:value-of select="$name"/>s are defined:
 * &lt;ul&gt;<xsl:for-each select="$values">
 *    &lt;li&gt;<xsl:value-of select="$classname"/>.<xsl:call-template name="pic-symbolic-name"><xsl:with-param name="value" select="."/></xsl:call-template><xsl:if test="@numeric"> = <xsl:value-of select="@numeric"/></xsl:if> = '<xsl:value-of select="."/>'&lt;/li&gt;</xsl:for-each>
 * &lt;/ul&gt;
 *
 * <xsl:if test="$numeric">The values of this enum have beside the internal
 * sequential integer representation that is used for serialization
 * dedicated assigned numeric values that are used in the
 * &lt;code>toInt()&lt;/code> and &lt;code>fromInt()&lt;/code> methods.</xsl:if>
 * <xsl:if test="not($numeric)">The values of this enum have a internal
 * sequential integer representation starting with '0'.</xsl:if>
 *
 * @author generated
 */
public final class <xsl:value-of select="$classname"/>
        implements Serializable, org.jcoderz.commons.EnumType<xsl:if test="$implements">,
            <xsl:value-of select="$implements"/></xsl:if>
{
   /**
    * The name of this type.
    */
   public static final String TYPE_NAME = "<xsl:value-of select="$classname"/>";

   /** Ordinal of next <xsl:value-of select="$name"/> to be created. */
   private static int sNextOrdinal = 0;

   /** Maps a string representation to an enumerated value. */
   private static final Map&lt;String, <xsl:value-of select="$classname"/>>
      FROM_STRING = new HashMap&lt;String, <xsl:value-of select="$classname"/>>();
<xsl:for-each select="$values"><xsl:variable name="constant-name"><xsl:call-template name="pic-symbolic-name"><xsl:with-param
      name="value" select="."/></xsl:call-template></xsl:variable><xsl:if test="$numeric">
   /** Numeric representation for <xsl:value-of select="$classname"/><xsl:text> </xsl:text><xsl:value-of select="."/>. */
   public static final int <xsl:value-of select="$constant-name"/>_NUMERIC = <xsl:value-of select="@numeric"/>;
<xsl:if test="not(@numeric)">// FIXME: No Numeric defined in input file for this value.<xsl:message
          >No numeric representation defined for <xsl:value-of select="."/> in enumeration type <xsl:value-of select="$classname"/>.</xsl:message></xsl:if>
</xsl:if><xsl:choose><xsl:when test="not(@description)">
   /** The <xsl:value-of select="$classname"/><xsl:text> </xsl:text><xsl:value-of select="."/>. */</xsl:when><xsl:otherwise>
   /** <xsl:value-of select="./@description"/> (value: <xsl:value-of select="."/>). */</xsl:otherwise></xsl:choose>
   public static final <xsl:value-of select="$classname"/><xsl:text> </xsl:text><xsl:call-template name="pic-symbolic-name"><xsl:with-param name="value" select="."/></xsl:call-template>
      = new <xsl:value-of select="$classname"/>("<xsl:value-of select="."/>"<xsl:if
         test="$numeric">, <xsl:value-of select="$constant-name"/>_NUMERIC</xsl:if>);
</xsl:for-each>

   /** The serialVersionUID used for serialization. */
   static final long serialVersionUID = 1;

   /** Internal list of all available <xsl:value-of select="$classname"/>s */
   private static final <xsl:value-of select="$classname"/>[] PRIVATE_VALUES
         =
            {
               <xsl:for-each select="$values">
                  <xsl:value-of select="$classname"/>.<xsl:call-template name="pic-symbolic-name"><xsl:with-param name="value" select="."/></xsl:call-template>
               <xsl:if test="position() != last()">
               <xsl:text>,
               </xsl:text>
               </xsl:if></xsl:for-each>
            };

   /** Immutable list of the <xsl:value-of select="$classname"/>s. */
   public static final List VALUES
         = Collections.unmodifiableList(Arrays.asList(PRIVATE_VALUES));

   /**
    * Immutable map using the name string as key holding the
    * <xsl:value-of select="$classname"/>s as values.
    */
   public static final Map VALUE_MAP
         = Collections.unmodifiableMap(FROM_STRING);

   /** Assign a ordinal to this <xsl:value-of select="$name"/> */
   private final int mOrdinal = sNextOrdinal++;

   /** The name of the <xsl:value-of select="$name"/> */
   private final transient String mName;
<xsl:if test="$numeric">

   /** The numeric representation of the <xsl:value-of select="$name"/> */
   private final transient int mNumeric;
</xsl:if>
   /** Private Constructor */
   private <xsl:value-of select="$classname"/> (String name<xsl:if
    test="$numeric">, int numeric</xsl:if>)
   {
      mName = name;<xsl:if test="$numeric">
      mNumeric = numeric;</xsl:if>
      FROM_STRING.put(mName, this);
   }

<xsl:if test="not($numeric)">
   /**
    * Creates a <xsl:value-of select="$classname"/> object from its int representation.
    *
    * @param i the integer representation of the <xsl:value-of select="$name"/>.
    * @return the <xsl:value-of select="$classname"/> object represented by this int.
    * @throws ArgumentMalformedException If the assigned int value isn't
    *       listed in the internal <xsl:value-of select="$name"/> table.
    */
   public static <xsl:value-of select="$classname"/> fromInt (int i)
         throws ArgumentMalformedException
   {
      try
      {
         return PRIVATE_VALUES[i];
      }
      catch (ArrayIndexOutOfBoundsException e)
      {
         throw new ArgumentMalformedException(
               "<xsl:value-of select="$classname"/>",
               new Integer(i),
               "Illegal int representation of <xsl:value-of select="$classname"/>.");
      }
   }
</xsl:if>

<xsl:if test="$numeric">

   /**
    * Creates a <xsl:value-of select="$classname"/> object from its numeric
    * representation.
    *
    * @param i the integer representation of the <xsl:value-of select="$name"/>.
    * @return the <xsl:value-of select="$classname"/> object represented by this int.
    * @throws ArgumentMalformedException If the assigned int value isn't
    *       listed in the internal <xsl:value-of select="$name"/> table.
    */
   public static <xsl:value-of select="$classname"/> fromInt (int i)
         throws ArgumentMalformedException
   {
       final <xsl:value-of select="$classname"/> result;
       switch (i)
       {<xsl:for-each select="$values"><xsl:variable name="constant-name"><xsl:call-template name="pic-symbolic-name"><xsl:with-param
          name="value" select="."/></xsl:call-template></xsl:variable>
           case <xsl:value-of select="$constant-name"/>_NUMERIC:
               result = <xsl:value-of select="$constant-name"/>;
               break;</xsl:for-each>
           default:
               throw new ArgumentMalformedException(
                     "<xsl:value-of select="$classname"/>",
                     new Integer(i),
                     "Illegal int representation of <xsl:value-of select="$classname"/>.");
      }
      return result;
   }
</xsl:if>

   /**
    * Creates a <xsl:value-of select="$classname"/> object from its String representation.
    *
    * @param str the string representation of the
    *       <xsl:value-of select="$name"/>.
    * @return the <xsl:value-of select="$classname"/> object represented by this str.
    * @throws ArgumentMalformedException If the given str value isn't
    *       listed in the internal <xsl:value-of select="$name"/> table.
    */
   public static <xsl:value-of select="$classname"/> fromString (String str)
         throws ArgumentMalformedException
   {
      final <xsl:value-of select="$classname"/> result
            = (<xsl:value-of select="$classname"/>) FROM_STRING.get(str);
      if (result == null)
      {
         throw new ArgumentMalformedException(
               "<xsl:value-of select="$classname"/>",
               str,
               "Illegal string representation of <xsl:value-of select="$classname"/>, only "
                  + VALUES + " are allowed.");
      }
      return result;
   }

   /**
    * Returns the int representation of this <xsl:value-of select="$name"/>.
    *
    * @return the int representation of this <xsl:value-of select="$name"/>.
    */
   public int toInt ()
   {<xsl:if test="$numeric">
        return mNumeric;</xsl:if><xsl:if test="not($numeric)">
        return mOrdinal;</xsl:if>
   }

   /**
    * Returns the String representation of this <xsl:value-of select="$name"/>.
    *
    * @return the String representation of this <xsl:value-of select="$name"/>.
    */
   public String toString ()
   {
      return mName;
   }

   /**
    * Resolves instances being deserialized to a single instance
    * per <xsl:value-of select="$name"/>.
    */
   private Object readResolve ()
   {
      return PRIVATE_VALUES[mOrdinal];
   }
}
</xsl:template>

<xsl:template name="simple-enum-generator-import-hook" priority="-1">
import org.jcoderz.commons.ArgumentMalformedException;
</xsl:template>

<!-- find the best name for the symbol -->
<xsl:template name="pic-symbolic-name">
  <xsl:param name="value"/>
  <xsl:choose>
    <xsl:when test="$value/@symbol">
      <xsl:call-template name="asJavaConstantName"><xsl:with-param
      name="value" select="$value/@symbol"/></xsl:call-template>
    </xsl:when>
    <xsl:otherwise>
      <xsl:call-template name="asJavaConstantName"><xsl:with-param
      name="value" select="."/></xsl:call-template>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<!-- ===============================================================
     Restricted string generator
     =============================================================== -->
<xsl:template name="restricted-string">
   <xsl:param name="classname"/>
   <xsl:param name="package"/>
   <xsl:param name="min-length"/>
   <xsl:param name="max-length"/>
   <xsl:param name="constants"/>
   <xsl:param name="token-type" select="''"/>
   <xsl:param name="regex" select="''"/>
   <xsl:param name="implements" select="''"/>
   <xsl:variable name="classname-constant">TYPE_NAME</xsl:variable>
   <xsl:call-template name="java-copyright-header"/>
package <xsl:value-of select="$package"/>;


import java.io.Serializable;
<xsl:call-template name="restricted-string-import-hook">
   <xsl:with-param name="token-type" select="$token-type"/>
   <xsl:with-param name="regex" select="$regex"/>
</xsl:call-template>

/**
 * Holds the <xsl:value-of select="$classname"/>.
 * &lt;pre&gt;
 * String.type[<xsl:value-of select="$min-length"/>..<xsl:value-of select="$max-length"/>].
<xsl:if test="$regex"> * regular expression: <xsl:value-of select="$regex"/>
</xsl:if> * &lt;/pre&gt;
 * Instances of this class are immutable.
 *
 * &lt;p>This class implements the Comparable interface based on the natural
 * order of the String representation of its instances.&lt;/p>
 *
 * @author generated via stylesheet
 */
public final class <xsl:value-of select="$classname"/>
      implements Serializable, org.jcoderz.commons.RestrictedString, Comparable&lt;Object&gt;<xsl:if test="$implements">,
            <xsl:value-of select="$implements"/></xsl:if>
{
   /**
    * <xsl:value-of select="$classname"/> - the name of this type as string constant.
    */
   public static final String TYPE_NAME = "<xsl:value-of select="$classname"/>";

   /** The minimal length of <xsl:value-of select="$classname"/> (<xsl:value-of select="$min-length"/>). */
   public static final int MIN_LENGTH = <xsl:value-of select="$min-length"/>;

   /** The maximal length of <xsl:value-of select="$classname"/> (<xsl:value-of select="$max-length"/>). */
   public static final int MAX_LENGTH = <xsl:value-of select="$max-length"/>;
<xsl:if test="$regex">

   /** The regular expression matching <xsl:value-of select="$classname"/>. */
   public static final String REGULAR_EXPRESSION
         = "<xsl:call-template name="java-string-escape"><xsl:with-param name="s" select="$regex"/></xsl:call-template>";

   /** The compiled pattern for the regular expression. */
   public static final Pattern REGULAR_EXPRESSION_PATTERN
         = Pattern.compile(REGULAR_EXPRESSION);
</xsl:if>

<xsl:for-each select="$constants">
   <xsl:call-template name="java-constant">
      <xsl:with-param name="type" select="$classname"/>
      <xsl:with-param name="name" select="./@name"/>
      <xsl:with-param name="value" select="./@value"/>
      <xsl:with-param name="comment" select="./@comment"/>
   </xsl:call-template>
</xsl:for-each>
   /** The serialVersionUID used for serialization. */
   static final long serialVersionUID = 1;

   /** Holds the <xsl:value-of select="$classname"/>. */
   private final String m<xsl:value-of select="$classname"/>;

   /**
    * Creates a new instance of a <xsl:value-of select="$classname"/>.
    *
    * @param str the <xsl:value-of select="$classname"/> as string representation.
    * @throws ArgumentMalformedException If the given string &lt;code>str&lt;/code>
    *         violates the restriction of this type.
    */
   private <xsl:value-of select="$classname"/> (final String str)
         throws ArgumentMalformedException
   {
      Assert.notNull(str, TYPE_NAME);<xsl:if test="$min-length != 0">
      if (str.length() &lt; MIN_LENGTH)
      {
         throw new ArgumentMinLengthViolationException(
            <xsl:value-of select="$classname-constant"/>,
            str, new Integer(str.length()), new Integer(MIN_LENGTH),
            <xsl:value-of select="$classname"/>.class);
      }</xsl:if>
      if (str.length() &gt; MAX_LENGTH)
      {
         throw new ArgumentMaxLengthViolationException(
            <xsl:value-of select="$classname-constant"/>,
            str, new Integer(str.length()), new Integer(MAX_LENGTH),
            <xsl:value-of select="$classname"/>.class);
      }<xsl:if test="$regex">
      if (!REGULAR_EXPRESSION_PATTERN.matcher(str).matches())
      {
         throw new ArgumentPatternViolationException(
            <xsl:value-of select="$classname-constant"/>,
            str, REGULAR_EXPRESSION,
            <xsl:value-of select="$classname"/>.class);
      }</xsl:if><xsl:if test="$token-type">
      if (!XsdUtil.isValidToken(str))
      {
         throw new ArgumentMalformedException(
            <xsl:value-of select="$classname-constant"/>,
            str, "Token format restrictions violated.");
      }</xsl:if>
      m<xsl:value-of select="$classname"/> = str;
   }

   /**
    * Creates a <xsl:value-of select="$classname"/> object from the String representation.
    *
    * @param str The str representation of the <xsl:value-of select="$classname"/> to be returned.
    * @return The <xsl:value-of select="$classname"/> object represented by this str.
    * @throws ArgumentMalformedException If the given string &lt;code>str&lt;/code>
    *         violates the restriction of this type.
    */
   public static <xsl:value-of select="$classname"/> fromString (String str)
         throws ArgumentMalformedException
   {
      return new <xsl:value-of select="$classname"/>(str);
   }

   /**
    * Returns the String representation of this <xsl:value-of select="$classname"/>.
    *
    * @return The String representation of this <xsl:value-of select="$classname"/>.
    */
   public String toString ()
   {
      return m<xsl:value-of select="$classname"/>;
   }

   /**
    * Indicates whether some other object is "equal to" this one.
    *
    * @param obj the object to compare to.
    * @return true if this object is the same as the obj argument; false
    *         otherwise.
    */
   public boolean equals (Object obj)
   {
      return (this == obj)
        || (obj instanceof <xsl:value-of select="$classname"/>
            &amp;&amp; ((<xsl:value-of select="$classname"/>) obj).m<xsl:value-of select="$classname"/>.equals(
               m<xsl:value-of select="$classname"/>));
   }

   /**
    * Returns the hash code for the <xsl:value-of select="$classname"/>.
    *
    * @return the hash code for the <xsl:value-of select="$classname"/>.
    */
   public int hashCode ()
   {
      return m<xsl:value-of select="$classname"/>.hashCode();
   }

   /**
    * Compares this <xsl:value-of select="$classname"/> with an other.
    * The order is based on the natural order of the String representation
    * of the compared instances.
    *
    * @param o the Object to be compared.   
    * @return a negative integer, zero, or a positive integer as this
    *   object is less than, equal to, or greater than the specified object.
    * @throws ClassCastException if the specified object's type prevents
    *   it from being compared to this Object.
    * @see String#compareTo(Object)
    */
   public int compareTo(Object o)
   {
      return toString().compareTo(
        ((<xsl:value-of select="$classname"/>) o).toString());
   }
}
</xsl:template>

<xsl:template name="restricted-string-user-type">
   <xsl:param name="classname"/>
   <xsl:param name="type-classname"/>
   <xsl:param name="package"/>
   <xsl:param name="min-length"/>
   <xsl:param name="max-length"/>
   <xsl:variable name="classname-constant">TYPE_NAME</xsl:variable>
   <xsl:call-template name="java-copyright-header"/>
package <xsl:value-of select="$package"/>;

/**
 * Hibernate user type for the  <xsl:value-of select="$type-classname"/>.
 *
 * @author generated via stylesheet
 */
public final class <xsl:value-of select="$classname"/>
      extends org.jcoderz.commons.util.StringUserTypeBase
{
   /** The serialVersionUID used for serialization. */
   static final long serialVersionUID = 1;

   /** <xsl:choose>
        <xsl:when
          test="$min-length = 0">Holds the empty string representation of the type.</xsl:when>
          <xsl:otherwise>Holds null as empty representation of this type.</xsl:otherwise>
      </xsl:choose> */
   private static final <xsl:value-of select="$type-classname"/> EMPTY_OR_NULL
        = <xsl:choose><xsl:when test="$min-length = 0">
          <xsl:value-of select="$type-classname"/>.fromString("");</xsl:when>
          <xsl:otherwise>null;</xsl:otherwise>
          </xsl:choose>

  /**
   * Hibernate <tt><xsl:value-of select="$type-classname"/></tt> type as mapped
   * from this UserType.
   * @return this UserType as org.hibernate.type.Type.
   */
  public static org.hibernate.type.Type getType ()
  {
    return TypeHolder.TYPE;
  }

  /**
   * Creates a <xsl:value-of select="$type-classname"/> from its String
   * database representation.
   * @param value a string holding the database representation of the
   *    <xsl:value-of select="$type-classname"/>.
   * @return a <xsl:value-of select="$type-classname"/> representing the
   *    given string.
   * @see <xsl:value-of select="$type-classname"/>#fromString(String)
   */
  public Object fromString(String value)
  {
    return <xsl:value-of select="$type-classname"/>.fromString(value);
  }

  /** <xsl:choose>
        <xsl:when
          test="$min-length = 0">@return the empty string as null representation of the type.</xsl:when>
          <xsl:otherwise>@return &lt;code>null&lt;/code> as null representation of the type.</xsl:otherwise>
      </xsl:choose> */
  public Object getEmptyOrNull()
  {
    return EMPTY_OR_NULL;
  }

  /**
   * @return <xsl:value-of select="$type-classname"/>.class as the supported
   * class of this user type.
   */
  public Class returnedClass()
  {
    return <xsl:value-of select="$type-classname"/>.class;
  }

  /**
   * Class to lazy initialize the Hibernate Type adapter.
   */
   private static class TypeHolder
   {
      private static final org.hibernate.type.Type TYPE
        = new org.hibernate.type.CustomType(<xsl:value-of select="$classname"/>.class, null);
   }
}
</xsl:template>

<xsl:template name="restricted-string-import-hook" priority="-1">
<xsl:param name="token-type" select="''"/>
<xsl:param name="regex" select="''"/><xsl:if test="$regex">
import java.util.regex.Pattern;
</xsl:if>
import org.jcoderz.commons.ArgumentMinLengthViolationException;
import org.jcoderz.commons.ArgumentMaxLengthViolationException;
import org.jcoderz.commons.ArgumentMalformedException;<xsl:if test="$regex">
import org.jcoderz.commons.ArgumentPatternViolationException;</xsl:if><xsl:if test="$token-type">
import org.jcoderz.commons.util.XsdUtil;</xsl:if>
import org.jcoderz.commons.util.Assert;
</xsl:template>

<!-- ===============================================================
     Restricted long generator
     =============================================================== -->
<xsl:template name="restricted-long">
   <xsl:param name="classname"/>
   <xsl:param name="package"/>
   <xsl:param name="min-value"/>
   <xsl:param name="max-value"/>
   <xsl:param name="constants"/>
   <xsl:param name="implements"/>
   <xsl:variable name="classname-constant">TYPE_NAME</xsl:variable>
   <xsl:call-template name="java-copyright-header"/>
package <xsl:value-of select="$package"/>;

import java.io.Serializable;

import org.jcoderz.commons.util.HashCodeUtil;
<xsl:call-template name="restricted-long-import-hook" />

/**
 * Holds the <xsl:value-of select="$classname"/>.
 * &lt;pre&gt;
 * long[<xsl:value-of select="$min-value"/>..<xsl:value-of select="$max-value"/>].
 * &lt;/pre&gt;
 * Instances of this class are immutable.
 *
 * &lt;p>This class implements the Comparable interface based on the numeric
 * order of its instances.&lt;/p>
 *
 * @author generated via stylesheet
 */
public final class <xsl:value-of select="$classname"/>
      implements Serializable, org.jcoderz.commons.RestrictedLong, Comparable<xsl:if test="$implements">,
            <xsl:value-of select="$implements"/></xsl:if>
{
   /**
    * <xsl:value-of select="$classname"/> - the name of this type as string constant.
    */
   public static final String TYPE_NAME = "<xsl:value-of select="$classname"/>";

   /**
    * The minimum value of a <xsl:value-of select="$classname"/> (<xsl:value-of select="$min-value"/>).
    */
   public static final long MIN_VALUE = <xsl:value-of select="$min-value"/>;

   /**
    * The maximum value of a <xsl:value-of select="$classname"/> (<xsl:value-of select="$max-value"/>).
    */
   public static final long MAX_VALUE = <xsl:value-of select="$max-value"/>;

<xsl:for-each select="$constants">
   <xsl:call-template name="java-constant">
      <xsl:with-param name="type" select="$classname"/>
      <xsl:with-param name="name" select="./@name"/>
      <xsl:with-param name="value" select="./@value"/>
      <xsl:with-param name="comment" select="./@comment"/>
      <xsl:with-param name="quote-char" select="''"/>
   </xsl:call-template>
</xsl:for-each>
   /** The serialVersionUID used for serialization. */
   static final long serialVersionUID = 1;

   /** Holds the <xsl:value-of select="$classname"/>. */
   private final long m<xsl:value-of select="$classname"/>;
   /** Lazy initialized long object value. */
   private transient Long m<xsl:value-of select="$classname"/>LongObject;
   /** Lazy initialized hash code value. */
   private transient int mHashCode = 0;

   /**
    * Creates a new instance of a <xsl:value-of select="$classname"/>.
    *
    * @param id the <xsl:value-of select="$classname"/> as long representation
    * @throws ArgumentMalformedException If the given long &lt;code>id&lt;/code>
    *         violates the restriction of the type
    *         <xsl:value-of select="$classname"/>.
    * @throws ArgumentMinValueViolationException If the value of the given
    *         long &lt;code>id&lt;/code> is below {@link #MIN_VALUE}.
    * @throws ArgumentMaxValueViolationException If the value of the given
    *         long &lt;code>id&lt;/code> is above {@link #MAX_VALUE}.
    */
   private <xsl:value-of select="$classname"/> (long id)
   {
      if (id &lt; MIN_VALUE)
      {
         throw new ArgumentMinValueViolationException(
               TYPE_NAME, new Long(id), new Long(MIN_VALUE),
               <xsl:value-of select="$classname"/>.class);
      }
      if (id &gt; MAX_VALUE)
      {
         throw new ArgumentMaxValueViolationException(
               TYPE_NAME, new Long(id), new Long(MAX_VALUE),
               <xsl:value-of select="$classname"/>.class);
      }
      m<xsl:value-of select="$classname"/> = id;
   }

   /**
    * Construct a <xsl:value-of select="$classname"/> object from its long representation.
    * @param id the long representation of the <xsl:value-of select="$classname"/>
    * @return the <xsl:value-of select="$classname"/> object represented by the given long
    * @throws ArgumentMalformedException If the given long &lt;code>id&lt;/code>
    *         violates the restriction of the type
    *         <xsl:value-of select="$classname"/>.
    * @throws ArgumentMinValueViolationException If the value of the given
    *         long &lt;code>id&lt;/code> is below {@link #MIN_VALUE}.
    * @throws ArgumentMaxValueViolationException If the value of the given
    *         long &lt;code>id&lt;/code> is above {@link #MAX_VALUE}.
    */
   public static <xsl:value-of select="$classname"/> fromLong (long id)
         throws ArgumentMalformedException
   {
      return new <xsl:value-of select="$classname"/>(id);
   }

   /**
    * Construct a <xsl:value-of select="$classname"/> object from its string representation.
    * @param s the string representation of the <xsl:value-of select="$classname"/>
    * @return the <xsl:value-of select="$classname"/> object represented by the given string
    * @throws ArgumentMalformedException If the given string &lt;code>s&lt;/code>
    *         violates the restriction of the type
    *         <xsl:value-of select="$classname"/>.
    * @throws ArgumentMinValueViolationException If the value of the given
    *         long &lt;code>id&lt;/code> is below {@link #MIN_VALUE}.
    * @throws ArgumentMaxValueViolationException If the value of the given
    *         long &lt;code>id&lt;/code> is above {@link #MAX_VALUE}.
    */
   public static <xsl:value-of select="$classname"/> fromString (String s)
         throws ArgumentMalformedException
   {
      final long id;
      try
      {
         id = Long.parseLong(s);
      }
      catch (NumberFormatException e)
      {
         throw new ArgumentMalformedException(
               TYPE_NAME, s, "Invalid string representation", e);
      }
      return new <xsl:value-of select="$classname"/>(id);
   }

   /**
    * Construct a <xsl:value-of select="$classname"/> object from its Long representation.
    * @param id the Long representation of the <xsl:value-of select="$classname"/>
    * @return the <xsl:value-of select="$classname"/> object represented by the given Long
    * @throws ArgumentMalformedException If the given Long &lt;code>id&lt;/code>
    *         is null or violates the restriction of the type
    *         <xsl:value-of select="$classname"/>.
    * @throws ArgumentMinValueViolationException If the value of the given
    *         long &lt;code>id&lt;/code> is below {@link #MIN_VALUE}.
    * @throws ArgumentMaxValueViolationException If the value of the given
    *         long &lt;code>id&lt;/code> is above {@link #MAX_VALUE}.
    */
   public static <xsl:value-of select="$classname"/> fromLong (Long id)
         throws ArgumentMalformedException
   {
      Assert.notNull(id, "id");
      return new <xsl:value-of select="$classname"/>(id.longValue());
   }

   /**
    * Generates a random <xsl:value-of select="$classname"/> object.
    * @return a random <xsl:value-of select="$classname"/> object.
    */
   public static <xsl:value-of select="$classname"/> random ()
   {
      return new <xsl:value-of select="$classname"/>(RandomUtil.random(MIN_VALUE, MAX_VALUE));
   }

   /**
    * Returns the long representation of this <xsl:value-of select="$classname"/> object.
    * @return the long representation of this <xsl:value-of select="$classname"/> object.
    */
   public long toLong ()
   {
      return m<xsl:value-of select="$classname"/>;
   }

   /**
    * Returns the Long representation of this <xsl:value-of select="$classname"/> object.
    * @return the Long representation of this <xsl:value-of select="$classname"/> object.
    */
   public Long toLongObject ()
   {
      if (m<xsl:value-of select="$classname"/>LongObject == null)
      {
         m<xsl:value-of select="$classname"/>LongObject = new Long(m<xsl:value-of select="$classname"/>);
      }
      return m<xsl:value-of select="$classname"/>LongObject;
   }

   /** {@inheritDoc} */
   public String toString ()
   {
      return Long.toString(m<xsl:value-of select="$classname"/>);
   }

   /** {@inheritDoc} */
   public boolean equals (Object obj)
   {
      return (this == obj)
        || (obj instanceof <xsl:value-of select="$classname"/>
            &amp;&amp; ((<xsl:value-of select="$classname"/>) obj).m<xsl:value-of select="$classname"/>
               == m<xsl:value-of select="$classname"/>);
   }

   /** {@inheritDoc} */
   public int hashCode ()
   {
      if (mHashCode == 0)
      {
         mHashCode = HashCodeUtil.hash(HashCodeUtil.SEED, m<xsl:value-of select="$classname"/>);
      }
      return mHashCode;
   }

   /**
    * Compares this <xsl:value-of select="$classname"/> with an other.
    * The order is based on the numeric order of the
    * of the compared instances.
    *
    * @return a negative integer, zero, or a positive integer as this
    *   object is less than, equal to, or greater than the specified object.
    * @throws ClassCastException if the specified object's type prevents
    *   it from being compared to this Object.
    * @see Long#compareTo(Object)
    */
   public int compareTo(Object o)
   {
     final long thisVal = toLong();
     final long anotherVal = ((<xsl:value-of select="$classname"/>) o).toLong();
     return (thisVal &lt; anotherVal ? -1 : (thisVal == anotherVal ? 0 : 1));
   }

}
</xsl:template>

<xsl:template name="restricted-int-user-type">
   <xsl:param name="classname"/>
   <xsl:param name="type-classname"/>
   <xsl:param name="package"/>
   <xsl:param name="min-length"/>
   <xsl:param name="max-length"/>
   <xsl:variable name="classname-constant">TYPE_NAME</xsl:variable>
   <xsl:call-template name="java-copyright-header"/>
package <xsl:value-of select="$package"/>;

/**
 * Hibernate user type for the  <xsl:value-of select="$type-classname"/>.
 *
 * @author generated via stylesheet
 */
public final class <xsl:value-of select="$classname"/>
      extends org.jcoderz.commons.util.IntUserTypeBase
{
   /** The serialVersionUID used for serialization. */
   static final long serialVersionUID = 1;

   /**
    * Hibernate <tt><xsl:value-of select="$type-classname"/></tt> type as mapped
    * from this UserType.
    * @return this UserType as org.hibernate.type.Type.
    */
   public static org.hibernate.type.Type getType ()
   {
      return TypeHolder.TYPE;
   }

  /**
   * Creates a <xsl:value-of select="$type-classname"/> from its numeric
   * int database representation.
   * @param value a int holding the database representation of the
   *    <xsl:value-of select="$type-classname"/>.
   * @return a <xsl:value-of select="$type-classname"/> representing the
   *    given int.
   * @see <xsl:value-of select="$type-classname"/>#fromInt(int)
   */
  public Object fromInt(int value)
  {
    return <xsl:value-of select="$type-classname"/>.fromInt(value);
  }

  /**
   * Converts the <xsl:value-of select="$type-classname"/> to its numeric
   * int database representation.
   * @param value the <xsl:value-of select="$type-classname"/> to be
   *    converted.
   * @return a int representing the
   *    given <xsl:value-of select="$type-classname"/>.
   * @see <xsl:value-of select="$type-classname"/>#toInt()
   */
  public int toInt(Object value)
  {
    return ((<xsl:value-of select="$type-classname"/>) value).toInt();
  }

  /**
   * @return <xsl:value-of select="$type-classname"/>.class as the supported
   * class of this user type.
   */
  public Class returnedClass()
  {
    return <xsl:value-of select="$type-classname"/>.class;
  }


  /**
   * Class to lazy initialize the Hibernate Type adapter.
   */
   private static class TypeHolder
   {
      private static final org.hibernate.type.Type TYPE
        = new org.hibernate.type.CustomType(<xsl:value-of select="$classname"/>.class, null);
   }
}
</xsl:template>

<xsl:template name="restricted-long-user-type">
   <xsl:param name="classname"/>
   <xsl:param name="type-classname"/>
   <xsl:param name="package"/>
   <xsl:param name="min-length"/>
   <xsl:param name="max-length"/>
   <xsl:variable name="classname-constant">TYPE_NAME</xsl:variable>
   <xsl:call-template name="java-copyright-header"/>
package <xsl:value-of select="$package"/>;

/**
 * Hibernate user type for the  <xsl:value-of select="$type-classname"/>.
 *
 * @author generated via stylesheet
 */
public final class <xsl:value-of select="$classname"/>
      extends org.jcoderz.commons.util.LongUserTypeBase
{
   /** The serialVersionUID used for serialization. */
   static final long serialVersionUID = 1;

   /**
    * Hibernate <tt><xsl:value-of select="$type-classname"/></tt> type as mapped
    * from this UserType.
    * @return this UserType as org.hibernate.type.Type.
    */
   public static org.hibernate.type.Type getType ()
   {
      return TypeHolder.TYPE;
   }

  /**
   * Creates a <xsl:value-of select="$type-classname"/> from its numeric
   * long database representation.
   * @param value a long holding the database representation of the
   *    <xsl:value-of select="$type-classname"/>.
   * @return a <xsl:value-of select="$type-classname"/> representing the
   *    given long.
   * @see <xsl:value-of select="$type-classname"/>#fromLong(long)
   */
  public Object fromLong(long value)
  {
    return <xsl:value-of select="$type-classname"/>.fromLong(value);
  }

  /**
   * Converts the <xsl:value-of select="$type-classname"/> to its numeric
   * long database representation.
   * @param value the <xsl:value-of select="$type-classname"/> to be
   *    converted.
   * @return a long representing the
   *    given <xsl:value-of select="$type-classname"/>.
   * @see <xsl:value-of select="$type-classname"/>#toLong()
   */
  public long toLong(Object value)
  {
    return ((<xsl:value-of select="$type-classname"/>) value).toLong();
  }

  /**
   * @return <xsl:value-of select="$type-classname"/>.class as the supported
   * class of this user type.
   */
  public Class returnedClass()
  {
    return <xsl:value-of select="$type-classname"/>.class;
  }

  /**
   * Class to lazy initialize the Hibernate Type adapter.
   */
   private static class TypeHolder
   {
      private static final org.hibernate.type.Type TYPE
        = new org.hibernate.type.CustomType(<xsl:value-of select="$classname"/>.class, null);
   }
}
</xsl:template>

<xsl:template name="restricted-long-import-hook" priority="-1">
import org.jcoderz.commons.ArgumentMalformedException;
import org.jcoderz.commons.ArgumentMinValueViolationException;
import org.jcoderz.commons.ArgumentMaxValueViolationException;
import org.jcoderz.commons.util.RandomUtil;
import org.jcoderz.commons.util.Assert;
</xsl:template>

<!-- ===============================================================
     String that must match a regex.
     =============================================================== -->
<xsl:template name="regex-string">
   <xsl:param name="classname"/>
   <xsl:param name="package"/>
   <xsl:param name="constants" select="''"/>
   <xsl:param name="regex" select="'FIXME'"/>

   <xsl:variable name="classname-constant"><xsl:text>TYPE_NAME</xsl:text></xsl:variable>

   <xsl:call-template name="java-copyright-header"/>
package <xsl:value-of select="$package"/>;


import java.io.Serializable;
import java.util.regex.Pattern;
<xsl:call-template name="regex-string-import-hook"/>

/**
 * Type-safe string type.
 * &lt;pre&gt;
 * regular expression: <xsl:value-of select="$regex"/>
 * &lt;/pre&gt;
 * Instances of this class are immutable.
 *
 * @author generated
 */
public final class <xsl:value-of select="$classname"/>
      implements Serializable, org.jcoderz.commons.StrongType
{
   /**
    * The name of this type.
    */
   public static final String TYPE_NAME = "<xsl:value-of select="$classname"/>";

   /** The regular expression matching <xsl:value-of select="$classname"/>. */
   public static final String REGULAR_EXPRESSION
         = "<xsl:call-template name="java-string-escape"><xsl:with-param name="s" select="$regex"/></xsl:call-template>";

   /** The compiled pattern for the regular expression. */
   public static final Pattern REGULAR_EXPRESSION_PATTERN
         = Pattern.compile(REGULAR_EXPRESSION);
<xsl:for-each select="$constants">
   <xsl:call-template name="java-constant">
      <xsl:with-param name="type" select="$classname"/>
      <xsl:with-param name="name" select="./@name"/>
      <xsl:with-param name="value" select="./@value"/>
      <xsl:with-param name="comment" select="./@comment"/>
   </xsl:call-template>
</xsl:for-each>
   /** The serialVersionUID used for serialization. */
   static final long serialVersionUID = 1;

   /** Holds the <xsl:value-of select="$classname"/>. */
   private final String m<xsl:value-of select="$classname"/>;

   /**
    * Creates a new instance of a <xsl:value-of select="$classname"/>.
    *
    * @param str the <xsl:value-of select="$classname"/> as string representation
    * @throws ArgumentMalformedException If the given string &lt;code>str&lt;/code>
    *         does not conform to the Simpay Scheme representation of the
    *         <xsl:value-of select="$classname"/>.
    */
   private <xsl:value-of select="$classname"/> (final String str)
         throws ArgumentMalformedException
   {
      Assert.notNull(str, TYPE_NAME);
      if (!REGULAR_EXPRESSION_PATTERN.matcher(str).matches())
      {
         throw new ArgumentMalformedException(
            <xsl:value-of select="$classname-constant"/>,
            str,
            "Value must match regular expression " + REGULAR_EXPRESSION + ".");
      }

      m<xsl:value-of select="$classname"/> = str;
   }

   /**
    * Creates a <xsl:value-of select="$classname"/> object from SXP String representation.
    *
    * @param str The str representation of the <xsl:value-of select="$classname"/> to be returned.
    * @return The <xsl:value-of select="$classname"/> object represented by this str.
    * @throws ArgumentMalformedException If the given string &lt;code>s&lt;/code>
    *         does not conform to the Simpay Interface representation of
    *         <xsl:value-of select="$classname"/>.
    */
   public static <xsl:value-of select="$classname"/> fromString (String str)
         throws ArgumentMalformedException
   {
      return new <xsl:value-of select="$classname"/>(str);
   }

   /**
    * Returns the SXP String representation of this <xsl:value-of select="$classname"/>.
    *
    * @return The SXP String representation of this <xsl:value-of select="$classname"/>.
    */
   public String toString ()
   {
      return m<xsl:value-of select="$classname"/>;
   }

   /**
    * Indicates whether some other object is "equal to" this one.
    *
    * @param obj the object to compare to.
    * @return true if this object is the same as the obj argument; false
    *         otherwise.
    */
   public boolean equals (Object obj)
   {
      return (this == obj)
        || (obj instanceof <xsl:value-of select="$classname"/>
            &amp;&amp; ((<xsl:value-of select="$classname"/>) obj).m<xsl:value-of select="$classname"/>.equals(
               m<xsl:value-of select="$classname"/>));
   }

   /**
    * Returns the hash code for the <xsl:value-of select="$classname"/>.
    *
    * @return the hash code for the <xsl:value-of select="$classname"/>.
    */
   public int hashCode ()
   {
      return m<xsl:value-of select="$classname"/>.hashCode();
   }
}
</xsl:template>

<xsl:template name="regex-string-import-hook" priority="-1">
import org.jcoderz.commons.ArgumentMalformedException;
import org.jcoderz.commons.util.Assert;
</xsl:template>

<!-- ===============================================================
     Fix Point Number generator.
     =============================================================== -->
<xsl:template name="fix-point-number">
   <xsl:param name="classname"/>
   <xsl:param name="package"/>
   <xsl:param name="constants" select="''"/>
   <xsl:param name="implements" select="''"/>
   <xsl:param name="fraction-digits" select="'FIXME'"/>
   <xsl:param name="total-digits" select="'FIXME'"/>
   <xsl:param name="min-value" select="''"/>
   <xsl:param name="max-value" select="'FIXME'"/>

  <xsl:variable name="DECIMAL_SCALE">1<xsl:call-template name="repeat">
       <xsl:with-param name="pattern" select="'0'"/>
       <xsl:with-param name="count" select="$fraction-digits"/>
    </xsl:call-template>
  </xsl:variable>
  <xsl:variable name="MIN_INTERNAL">-<xsl:call-template name="repeat">
       <xsl:with-param name="pattern" select="'9'"/>
       <xsl:with-param
            name="count" select="$total-digits - $fraction-digits"/>
       </xsl:call-template>.<xsl:call-template name="repeat">
         <xsl:with-param name="pattern" select="'9'"/>
         <xsl:with-param name="count" select="$fraction-digits"/>
       </xsl:call-template></xsl:variable>
  <xsl:variable name="MAX_INTERNAL"><xsl:call-template name="repeat">
        <xsl:with-param name="pattern" select="'9'"/>
        <xsl:with-param
          name="count" select="$total-digits - $fraction-digits"/>
      </xsl:call-template>.<xsl:call-template name="repeat">
        <xsl:with-param name="pattern" select="'9'"/>
        <xsl:with-param name="count" select="$fraction-digits"/>
      </xsl:call-template></xsl:variable>

  <xsl:variable name="MAX_VALUE">
    <xsl:choose>
      <xsl:when test="not($max-value)"><xsl:value-of select="$MAX_INTERNAL"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:if test="$MAX_INTERNAL &lt; $max-value">
          <xsl:message terminate="yes">
             WARNING: Max value can not be represented by <xsl:value-of select="$classname"/>
          </xsl:message>
        </xsl:if>
        <xsl:value-of select="$max-value"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:variable>
  <xsl:variable name="MIN_VALUE">
    <xsl:choose>
      <xsl:when test="not($min-value)"><xsl:value-of select="$MIN_INTERNAL"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:if test="$MIN_INTERNAL &gt; $min-value">
          <xsl:message terminate="yes">
             WARNING: Min value can not be represented by <xsl:value-of select="$classname"/>
          </xsl:message>
        </xsl:if>
        <xsl:value-of select="$min-value"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:variable>

  <!-- TODO: Assert that given max and min-value are in the total-digits
             range supported -->
  <!-- TODO might dynamicly switch to int / BD? -->
  <xsl:variable name="backing-type">long</xsl:variable>
  <xsl:variable name="backing-class">Long</xsl:variable>
  <xsl:variable name="backing-char">L</xsl:variable>

<xsl:call-template name="java-copyright-header"/>
package <xsl:value-of select="$package"/>;


import java.io.Serializable;
import java.math.BigDecimal;

import org.jcoderz.commons.ArgumentMalformedException;
import org.jcoderz.commons.ArgumentMaxValueViolationException;
import org.jcoderz.commons.ArgumentMinValueViolationException;
import org.jcoderz.commons.ArgumentFractionDigitsViolationException;
import org.jcoderz.commons.FixPointNumber;
import org.jcoderz.commons.util.Assert;
import org.jcoderz.commons.util.HashCodeUtil;
import org.jcoderz.commons.util.NumberUtil;


/**
 * Fix point numeric to represent <xsl:value-of select="$classname"/>.
 *
 * &lt;p>Permitted values range from <xsl:value-of select="$MIN_VALUE"/> to
 * <xsl:value-of select="$MAX_VALUE"/>. It the number of decimal
 * digits supported is <xsl:value-of select="$fraction-digits"/> and
 * the total number of digits is <xsl:value-of select="$total-digits"/>&lt;/p>
 *
 * Instances of this class are immutable.
 *
 * @author generated
 */
public final class <xsl:value-of select="$classname"/>
    extends Number
    implements Comparable, FixPointNumber, Serializable<xsl:if test="$implements">,
            <xsl:value-of select="$implements"/></xsl:if>
{
    /**
     * <xsl:value-of select="$classname"/> - the name of this type as String constant.
     */
    public static final String TYPE_NAME = "<xsl:value-of select="$classname"/>";

    /** The preffered database representation of this type. */
    public static final String PREFERED_DATABASE_TYPE
        = "NUMBER(<xsl:value-of select="$total-digits"/>,<xsl:value-of select="$fraction-digits"/>)";

    /** The minimal unscaled value of <xsl:value-of select="$classname"/> ({@value}). */
    public static final <xsl:value-of select="$backing-type"/> MIN_VALUE_UNSCALED
        = <xsl:call-template name="power10precise">
          <xsl:with-param name="number" select="$MIN_VALUE"/>
          <xsl:with-param name="exp" select="$fraction-digits"/>
        </xsl:call-template><xsl:value-of select="$backing-char"/>;

    /** The maximal value of <xsl:value-of select="$classname"/> ({@value}). */
    public static final <xsl:value-of select="$backing-type"/> MAX_VALUE_UNSCALED
        = <xsl:call-template name="power10precise">
          <xsl:with-param name="number" select="$MAX_VALUE"/>
          <xsl:with-param name="exp" select="$fraction-digits"/>
        </xsl:call-template><xsl:value-of select="$backing-char"/>;

    /** The number of fraction digits (<xsl:value-of select="$fraction-digits"/>). */
    public static final int FRACTION_DIGITS
        = <xsl:value-of select="$fraction-digits"/>;

    /** The number of fraction digits (<xsl:value-of select="$fraction-digits"/>). */
    public static final int SCALE
        = FRACTION_DIGITS;

    /** The scale (<xsl:value-of select="$DECIMAL_SCALE"/>). */
    public static final <xsl:value-of select="$backing-type"/> DECIMAL_SCALE
        = <xsl:value-of select="$DECIMAL_SCALE"/>;

    /** The minimal scaled value of <xsl:value-of select="$classname"/> ({@value}). */
    public static final <xsl:value-of select="$backing-type"/> MIN_VALUE_SCALED
        = MIN_VALUE_UNSCALED / DECIMAL_SCALE;

    /** The maximal scaled value of <xsl:value-of select="$classname"/> ({@value}). */
    public static final <xsl:value-of select="$backing-type"/> MAX_VALUE_SCALED
        = MAX_VALUE_UNSCALED / DECIMAL_SCALE;

    /** The number of fraction digits as integer (<xsl:value-of select="$fraction-digits"/>).  */
    public static final Integer FRACTION_DIGITS_AS_INTEGER
        = new Integer(FRACTION_DIGITS);

    /** The maximum number of digits (<xsl:value-of select="$total-digits"/>). */
    public static final int TOTAL_DIGITS
        = <xsl:value-of select="$total-digits"/>;

    /** The maximum number of digits as Integer (<xsl:value-of select="$total-digits"/>).  */
    public static final Integer TOTAL_DIGITS_AS_INTEGER
        = new Integer(TOTAL_DIGITS);

    /** The minimal value of <xsl:value-of select="$classname"/> (<xsl:value-of select="$MIN_VALUE"/>). */
    public static final <xsl:value-of select="$classname"/> MIN_VALUE
        = new <xsl:value-of select="$classname"/>(MIN_VALUE_UNSCALED);

    /** The maximal value of <xsl:value-of select="$classname"/> (<xsl:value-of select="$MAX_VALUE"/>). */
    public static final <xsl:value-of select="$classname"/> MAX_VALUE
        = new <xsl:value-of select="$classname"/>(MAX_VALUE_UNSCALED);
<!-- It is important to define the constants at the very end of the statics
     -->
<xsl:for-each select="$constants">
   <xsl:call-template name="java-constant-from-string">
      <xsl:with-param name="type" select="$classname"/>
      <xsl:with-param name="name" select="./@name"/>
      <xsl:with-param name="value" select="./@value"/>
      <xsl:with-param name="comment" select="./@comment"/>
   </xsl:call-template>
</xsl:for-each>
    /** The serialVersionUID used for serialization. */
    static final long serialVersionUID = 1;


    /** Holds the <xsl:value-of select="$classname"/> in a unscaled <xsl:value-of select="$backing-type"/>. */
    private final <xsl:value-of select="$backing-type"/> mUnscaled;

    /** Lazy initialized internal String representation. */
    private volatile String mStringRepresentation = null;

    /** Lazy initialized internal BigDecimal representation. */
    private volatile BigDecimal mBigDecimal;


    /**
     * Creates a new instance of a <xsl:value-of select="$classname"/>.
     *
     * @param unscaledValue unscaled <xsl:value-of select="$backing-type"/> representation
     * @throws ArgumentMalformedException If the given
     *   unscaledValue violates the restriction
     *   of the <xsl:value-of select="$classname"/> type.
     */
    private <xsl:value-of select="$classname"/> (final <xsl:value-of select="$backing-type"/> unscaledValue)
          throws ArgumentMalformedException
    {
       if (unscaledValue &lt; MIN_VALUE_UNSCALED)
       {
          throw new ArgumentMinValueViolationException(
             TYPE_NAME,
             unscaledValue + "/" + DECIMAL_SCALE, MIN_VALUE,
             <xsl:value-of select="$classname"/>.class);
       }
       if (unscaledValue &gt; MAX_VALUE_UNSCALED)
       {
          throw new ArgumentMaxValueViolationException(
             TYPE_NAME, unscaledValue + "/" + DECIMAL_SCALE,
             MAX_VALUE, <xsl:value-of select="$classname"/>.class);
       }
       mUnscaled = unscaledValue;
    }

    /**
     * Creates a <xsl:value-of select="$classname"/> object from the String
     * representation.
     *
     * @param str The str representation of the <xsl:value-of select="$classname"/>
     *   to be returned.
     * @return The <xsl:value-of select="$classname"/> object represented by this str.
     * @throws ArgumentMalformedException If the given
     *   String violates the restriction
     *   of the <xsl:value-of select="$classname"/> type.
     */
    public static <xsl:value-of select="$classname"/> fromString (String str)
          throws ArgumentMalformedException
    {
        Assert.notNull(str, TYPE_NAME);

        final <xsl:value-of select="$classname"/> result;
        try
        {
            result = valueOf(new BigDecimal(str));
        }
        catch (NumberFormatException ex)
        {
            throw new ArgumentMalformedException(
                TYPE_NAME, str, "Invalid string representation", ex);
        }
        return result;

    }

    /**
     * Translates a &lt;tt>BigDecimal&lt;/tt> value into a
     * &lt;tt><xsl:value-of select="$classname"/>&lt;/tt>.
     *
     * @param bd the &lt;tt>BigDecimal&lt;/tt>.
     * @return a &lt;tt><xsl:value-of select="$classname"/>&lt;/tt> whose value is equal
     *  to bd.
     * @throws ArgumentMalformedException If the given
     *   &lt;tt>BigDecimal&lt;/tt> violates the restriction
     *   of the <xsl:value-of select="$classname"/> type.
     */
    public static <xsl:value-of select="$classname"/> valueOf (BigDecimal bd)
    {
        if (bd.scale() &gt; SCALE)
        {
            throw new ArgumentFractionDigitsViolationException(
                TYPE_NAME, bd, new Integer(bd.scale()),
                FRACTION_DIGITS_AS_INTEGER,
                <xsl:value-of select="$classname"/>.class);
        }
        return new <xsl:value-of select="$classname"/>(
            bd.setScale(SCALE).unscaledValue().<xsl:value-of select="$backing-type"/>Value());
    }

    /**
     * Translates a &lt;tt>long&lt;/tt> value into a
     * &lt;tt><xsl:value-of select="$classname"/>&lt;/tt>.
     *
     * @param val the &lt;tt>long&lt;/tt>.
     * @return a &lt;tt><xsl:value-of select="$classname"/>&lt;/tt> whose value is equal
     *  to the given long.
     * @throws ArgumentMalformedException If the given
     *   &lt;tt>long&lt;/tt> violates the restriction
     *   of the <xsl:value-of select="$classname"/> type.
     * @see BigDecimal#valueOf(long)
     */
    public static <xsl:value-of select="$classname"/> valueOf (long val)
    {
        if (val &lt; MIN_VALUE_SCALED)
        {
           throw new ArgumentMinValueViolationException(
              TYPE_NAME, new <xsl:value-of select="$backing-class"/>(val), MIN_VALUE,
              <xsl:value-of select="$classname"/>.class);
        }
        if (val &gt; MAX_VALUE_SCALED)
        {
           throw new ArgumentMaxValueViolationException(
              TYPE_NAME, new <xsl:value-of select="$backing-class"/>(val), MAX_VALUE,
              <xsl:value-of select="$classname"/>.class);
        }
        return new <xsl:value-of select="$classname"/>(val * DECIMAL_SCALE);
    }

    /**
     * Translates a &lt;tt>long&lt;/tt> with the given scale into a
     * &lt;tt><xsl:value-of select="$classname"/>&lt;/tt>.
     *
     * @param unscaledVal the unscaled value.
     * @param scale the scale to be applied.
     * @return a &lt;tt><xsl:value-of select="$classname"/>&lt;/tt> whose value is
     *         &lt;tt>(unscaledVal &amp;times; 10&lt;sup>-scale&lt;/sup>)&lt;/tt>.
     * @throws ArgumentMalformedException If the given
     *   long and scale violates the restriction
     *   of the <xsl:value-of select="$classname"/> type.
     * @see BigDecimal#valueOf(long, int)
     */
    public static <xsl:value-of select="$classname"/> valueOf (long unscaledVal, int scale)
    {
        return valueOf(BigDecimal.valueOf(unscaledVal, scale));
    }

    /**
     * Returns the unscaled long value of this <xsl:value-of select="$classname"/>.
     * The actual value is the returned value / DECIMAL_SCALE.
     * @return The unscaled long value of this <xsl:value-of select="$classname"/>.
     */
    public long unscaledLongValue ()
    {
        return mUnscaled;
    }

    /**
     * Returns the String representation of this <xsl:value-of select="$classname"/>.
     * The implementation does not apply any localization rules.
     * @return The String representation of this <xsl:value-of select="$classname"/>.
     * @see BigDecimal#toString()
     */
    public String toString ()
    {
        if (mStringRepresentation == null)
        {
            mStringRepresentation
                = NumberUtil.toString(mUnscaled, SCALE);
        }
       return mStringRepresentation;
    }

    /**
     * Returns the BigDecimal representation of this <xsl:value-of select="$classname"/>.
     * @return The BigDecimal representation of this <xsl:value-of select="$classname"/>.
     */
    public BigDecimal toBigDecimal ()
    {
        if (mBigDecimal == null)
        {
            mBigDecimal = BigDecimal.valueOf(mUnscaled, SCALE);
        }
       return mBigDecimal;
    }

    /** {@inheritDoc} */
    public boolean equals (Object obj)
    {
        return (this == obj)
          || (obj instanceof <xsl:value-of select="$classname"/>
             &amp;&amp; ((<xsl:value-of select="$classname"/>) obj).mUnscaled == mUnscaled);
    }

    /** {@inheritDoc} */
    public int hashCode ()
    {
        return HashCodeUtil.hash(HashCodeUtil.SEED, mUnscaled);
    }

    /** {@inheritDoc} */
    public double doubleValue ()
    {
        return toBigDecimal().doubleValue();
    }

    /** {@inheritDoc} */
    public float floatValue ()
    {
        return toBigDecimal().floatValue();
    }

    /** {@inheritDoc} */
    public int intValue ()
    {
        return toBigDecimal().intValue();
    }

    /** {@inheritDoc} */
    public long longValue ()
    {
        return toBigDecimal().longValue();
    }

    /** {@inheritDoc} */
    public int compareTo (Object o)
    {
        final long thisVal = mUnscaled;
        final long anotherVal = ((<xsl:value-of select="$classname"/>) o).mUnscaled;
        return (thisVal &lt; anotherVal ? -1 : (thisVal == anotherVal ? 0 : 1));
    }
}
</xsl:template> <!-- fix-point-number -->

<!-- TODO: We might map this to Long on DB??? as an option? -->
<xsl:template name="fix-point-user-type">
   <xsl:param name="classname"/>
   <xsl:param name="type-classname"/>
   <xsl:param name="package"/>
   <xsl:variable name="classname-constant">TYPE_NAME</xsl:variable>
   <xsl:call-template name="java-copyright-header"/>
package <xsl:value-of select="$package"/>;


import java.math.BigDecimal;

/**
 * Hibernate user type for the  <xsl:value-of select="$type-classname"/>.
 *
 * @author generated via stylesheet
 */
public final class <xsl:value-of select="$classname"/>
      extends org.jcoderz.commons.util.BigDecimalUserTypeBase
{
   /** The serialVersionUID used for serialization. */
   static final long serialVersionUID = 1;

   /**
    * Hibernate <tt><xsl:value-of select="$type-classname"/></tt> type as mapped
    * from this UserType.
    * @return this UserType as org.hibernate.type.Type.
    */
   public static org.hibernate.type.Type getType ()
   {
      return TypeHolder.TYPE;
   }

  /**
   * Creates a <xsl:value-of select="$type-classname"/> from its numeric
   * BigDecimal database representation.
   * @param value a BigDecimal holding the database representation of the
   *    <xsl:value-of select="$type-classname"/>.
   * @return a <xsl:value-of select="$type-classname"/> representing the
   *    given BigDecimal.
   * @see <xsl:value-of select="$type-classname"/>#valueOf(BigDecimal)
   */
  public Object fromBigDecimal(BigDecimal value)
  {
    return <xsl:value-of select="$type-classname"/>.valueOf(value);
  }

  /**
   * Converts the <xsl:value-of select="$type-classname"/> to its numeric
   * BigDecimal database representation.
   * @param value the <xsl:value-of select="$type-classname"/> to be
   *    converted.
   * @return a BigDecimal representing the
   *    given <xsl:value-of select="$type-classname"/>.
   * @see <xsl:value-of select="$type-classname"/>#toBigDecimal()
   */
  public BigDecimal toBigDecimal(Object value)
  {
    return ((<xsl:value-of select="$type-classname"/>) value).toBigDecimal();
  }

  /**
   * @return <xsl:value-of select="$type-classname"/>.class as the supported class of this user type.
   */
  public Class returnedClass()
  {
    return <xsl:value-of select="$type-classname"/>.class;
  }


   /**
    * Class to lazy initialize the Hibernate Type adapter.
    */
   private static class TypeHolder
   {
      private static final org.hibernate.type.Type TYPE
        = new org.hibernate.type.CustomType(<xsl:value-of select="$classname"/>.class, null);
   }
}
</xsl:template>

<xsl:template name="repeat">
   <xsl:param name="pattern"/>
   <xsl:param name="count"/>
  <xsl:if test="$count > 0">
    <xsl:call-template name="repeat">
      <xsl:with-param name="pattern" select="$pattern"/>
      <xsl:with-param name="count" select="$count - 1"/>
    </xsl:call-template>
    <xsl:value-of select="$pattern"/>
  </xsl:if>
</xsl:template>

<xsl:template name="power10precise">
   <xsl:param name="number"/>
   <xsl:param name="exp"/>
  <xsl:choose>
    <xsl:when test="$exp > 0">
      <xsl:call-template name="power10precise">
        <xsl:with-param name="number">
          <xsl:call-template name="times10precize">
            <xsl:with-param name="number" select="$number"/>
          </xsl:call-template>
        </xsl:with-param>
        <xsl:with-param name="exp" select="$exp - 1"/>
      </xsl:call-template>
    </xsl:when>
    <xsl:otherwise><xsl:value-of select="$number"/></xsl:otherwise>
    </xsl:choose>
</xsl:template>

<xsl:template name="times10precize">
   <xsl:param name="number"/>
  <xsl:choose>
    <xsl:when test="contains($number, '.')">
      <xsl:variable name="pos" select="string-length(substring-before($number, '.'))"/>
      <xsl:value-of
        select="concat(substring($number, 0, $pos + 1), substring($number, $pos + 2, 1))"/>
      <xsl:if test="string-length(substring($number, $pos + 3)) &gt; 0"
        >.<xsl:value-of select="substring($number, $pos + 3)"/>
      </xsl:if>
    </xsl:when>
    <xsl:otherwise><xsl:value-of select="$number"/>0</xsl:otherwise>
  </xsl:choose>
</xsl:template>

<!-- ===============================================================
     Outputs the jCoderZ Java copyright header
     =============================================================== -->
<xsl:template name="java-copyright-header" priority="-1">
<xsl:text>/*
 * Generated source file, not in CVS/SVN repository
 */</xsl:text>
</xsl:template>

<!--
  ** This template modifies an EJB-QL query replacing the abstract
  ** schema name with the given parameter $schemaName.
  ** The abstract schema name in the query is identified as the
  ** string between the 'FROM' and 'AS' keywords, like
  ** in "SELECT OBJECT(a) FROM abstractSchema AS a".
  -->
<xsl:template name="replace-schema-in-query">
   <xsl:param name="schemaName"/>
   <xsl:param name="query"/>
   <xsl:variable name="queryLowerCase">
      <xsl:call-template name="toLowerCase">
         <xsl:with-param name="s" select="$query"/>
      </xsl:call-template>
   </xsl:variable>
   <xsl:variable name="beforeFromIndex">
      <xsl:value-of select="string-length(substring-before($queryLowerCase, 'from'))"/>
   </xsl:variable>
   <xsl:variable name="beforeAsIndex">
      <xsl:value-of select="string-length(substring-before($queryLowerCase, 'as'))"/>
   </xsl:variable>
   <xsl:value-of select="substring($query, 1, $beforeFromIndex)"/> FROM <xsl:value-of select="$schemaName"/> <xsl:value-of select="substring($query, $beforeAsIndex)"/>
</xsl:template>

<!--
  ** This template extracts the package from a fully qualified class
  ** name.
  -->
<xsl:template name="package-from-class">
   <xsl:param name="class"/>
   <xsl:param name="count" select="0"/>
   <xsl:if test="contains($class, '.')">
      <xsl:if test="$count &gt; 0">
         <xsl:text>.</xsl:text>
      </xsl:if>
      <xsl:value-of select="substring-before($class, '.')"/>
      <xsl:call-template name="package-from-class">
         <xsl:with-param name="class" select="substring-after($class, '.')"/>
         <xsl:with-param name="count" select="$count + 1"/>
      </xsl:call-template>
   </xsl:if>
</xsl:template>

<!--
  ** Generate complex javadoc structure that might contain xdoclet tags
  ** as sublelements / attributes
  -->
<xsl:template name="generate-xdoclet">
  <xsl:param name="doc-text"/>
  <xsl:param name="indent"/>
  <xsl:apply-templates select="$doc-text" mode="generate-javadoc-">
    <xsl:with-param name="indent" select="$indent"/>
  </xsl:apply-templates>
</xsl:template>

<xsl:template match="*" mode="generate-javadoc-">
  <xsl:param name="indent"/>
  <xsl:apply-templates mode="generate-javadoc-content"
    select="*|comment()|processing-instruction()">
      <xsl:with-param name="indent" select="$indent"/>
  </xsl:apply-templates>
</xsl:template>

<xsl:template match="node()" mode="generate-javadoc-content">
  <xsl:param name="indent"/>
<xsl:text>
</xsl:text><xsl:value-of select="$indent"/>* @<xsl:value-of select="name(.)" />
  <xsl:apply-templates
    select="@*" mode="generate-javadoc-attributes"/>
</xsl:template>

<xsl:template match="@*" mode="generate-javadoc-attributes">
 <xsl:text> </xsl:text><xsl:value-of select="name(.)"/><xsl:if
   test="string-length(.) != 0">="<xsl:value-of select="." />"</xsl:if>
</xsl:template>

</xsl:stylesheet>
