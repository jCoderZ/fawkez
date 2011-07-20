<?xml version="1.0" encoding="UTF-8"?>
<!--
   $Id: generate-simple-types.xsl 1546 2009-08-03 09:03:08Z amandel $

   Simple type generator. Support type-safe enumerations and restricted
   strings.

   Author: Michael Griffel
  -->
<xsl:stylesheet
   version="1.0"
   xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
   xmlns:xs="http://www.w3.org/2001/XMLSchema"
   xmlns:redirect="http://xml.apache.org/xalan/redirect"
   xmlns:xalan2="http://xml.apache.org/xslt"
   extension-element-prefixes="redirect">

<xsl:include href="libcommon.xsl"/>

<xsl:output method="text"
            encoding="ISO-8859-1"/>

<xsl:strip-space elements="*"/>

<xsl:param name="outdir" select="'.'"/>

<xsl:template match="/">
   <!-- log to out -->
   Generating classes to directory <xsl:value-of select="$outdir"/>.
   Found <xsl:value-of select="count(//enumeration)"/> enumerations,
   <xsl:value-of select="count(//restrictedString)"/> restricted strings,
   <xsl:value-of select="count(//fixPointNumber)"/> fix point number,
   <xsl:value-of select="count(//restrictedLong)"/> restricted longs,
   <xsl:value-of select="count(//regexString)"/> regex strings and
   <xsl:value-of select="count(//valueObject)"/> value objects.
   <xsl:apply-templates/>
</xsl:template>

<xsl:template match="enumeration">
   <xsl:variable name="package.dir"><xsl:value-of
      select="$outdir"/>/<xsl:value-of
         select="translate(@package, '.', '/')"/></xsl:variable>

   <xsl:variable name="file"><xsl:value-of
      select="$package.dir"/>/<xsl:value-of
         select="@classname"/>.java</xsl:variable>

   <redirect:write file="{$file}">

   <xsl:call-template name="simple-enum-generator">
      <xsl:with-param name="classname" select="@classname"/>
      <xsl:with-param name="package" select="@package"/>
      <xsl:with-param name="values" select=".//value"/>
      <xsl:with-param name="javadoc" select="./description"/>
      <xsl:with-param name="implements" select="@implements"/>
   </xsl:call-template>

   </redirect:write>

   <xsl:if test="@user-type = 'true' or @user-type = 'string'">
     <xsl:variable name="user-type-file"><xsl:value-of
        select="$package.dir"/>/<xsl:value-of
           select="@classname"/>UserType.java</xsl:variable>

     <redirect:write file="{$user-type-file}">
       <xsl:call-template name="restricted-string-user-type">
          <xsl:with-param name="classname"
            select="concat(@classname, 'UserType')"/>
          <xsl:with-param name="type-classname"
            select="@classname"/>
          <xsl:with-param name="package" select="@package"/>
          <xsl:with-param name="min-length" select="1"/>
          <xsl:with-param name="max-length" select="99"/>
       </xsl:call-template>
     </redirect:write>
   </xsl:if>

   <xsl:if test="@user-type = 'numeric' or @user-type = 'integer'">
     <xsl:variable name="user-type-file"><xsl:value-of
        select="$package.dir"/>/<xsl:value-of
           select="@classname"/>UserType.java</xsl:variable>

     <redirect:write file="{$user-type-file}">
       <xsl:call-template name="restricted-int-user-type">
          <xsl:with-param name="classname"
            select="concat(@classname, 'UserType')"/>
          <xsl:with-param name="type-classname"
            select="@classname"/>
          <xsl:with-param name="package" select="@package"/>
       </xsl:call-template>
     </redirect:write>
   </xsl:if>
</xsl:template>

<xsl:template match="restrictedString">
   <xsl:variable name="package.dir"><xsl:value-of
      select="$outdir"/>/<xsl:value-of
         select="translate(@package, '.', '/')"/></xsl:variable>

   <xsl:variable name="file"><xsl:value-of
      select="$package.dir"/>/<xsl:value-of
         select="@classname"/>.java</xsl:variable>

   <redirect:write file="{$file}">

   <xsl:call-template name="restricted-string">
      <xsl:with-param name="classname" select="@classname"/>
      <xsl:with-param name="package" select="@package"/>
      <xsl:with-param name="min-length" select="@min-length"/>
      <xsl:with-param name="max-length" select="@max-length"/>
      <xsl:with-param name="implements" select="@implements"/>
      <xsl:with-param name="constants" select=".//constant"/>
      <xsl:with-param name="token-type" select="@token-type"/>
      <xsl:with-param name="regex" select="@regex"/>
   </xsl:call-template>

   </redirect:write>
   <xsl:if test="@user-type = 'true'">
     <xsl:variable name="user-type-file"><xsl:value-of
        select="$package.dir"/>/<xsl:value-of
           select="@classname"/>UserType.java</xsl:variable>

     <redirect:write file="{$user-type-file}">
       <xsl:call-template name="restricted-string-user-type">
          <xsl:with-param name="classname"
            select="concat(@classname, 'UserType')"/>
          <xsl:with-param name="type-classname"
            select="@classname"/>
          <xsl:with-param name="package" select="@package"/>
          <xsl:with-param name="min-length" select="@min-length"/>
          <xsl:with-param name="max-length" select="@max-length"/>
       </xsl:call-template>
     </redirect:write>
   </xsl:if>
</xsl:template>

<xsl:template match="regexString">
   <xsl:variable name="package.dir"><xsl:value-of
      select="$outdir"/>/<xsl:value-of
         select="translate(@package, '.', '/')"/></xsl:variable>

   <xsl:variable name="file"><xsl:value-of
      select="$package.dir"/>/<xsl:value-of
         select="@classname"/>.java</xsl:variable>

  <redirect:write file="{$file}">

   <xsl:call-template name="regex-string">
      <xsl:with-param name="classname" select="@classname"/>
      <xsl:with-param name="package" select="@package"/>
      <xsl:with-param name="constants" select=".//constant"/>
      <xsl:with-param name="regex" select=".//regex"/>
   </xsl:call-template>

   </redirect:write>
</xsl:template>

<xsl:template match="fixPointNumber">
   <xsl:variable name="package.dir"><xsl:value-of
      select="$outdir"/>/<xsl:value-of
         select="translate(@package, '.', '/')"/></xsl:variable>

   <xsl:variable name="file"><xsl:value-of
      select="$package.dir"/>/<xsl:value-of
         select="@classname"/>.java</xsl:variable>

    <redirect:write file="{$file}">
        <xsl:call-template name="fix-point-number">
            <xsl:with-param name="classname" select="@classname"/>
            <xsl:with-param name="package" select="@package"/>
            <xsl:with-param name="fraction-digits" select="@fraction-digits"/>
            <xsl:with-param name="total-digits" select="@total-digits"/>
            <xsl:with-param name="min-value" select="@min-value"/>
            <xsl:with-param name="max-value" select="@max-value"/>
            <xsl:with-param name="implements" select="@implements"/>
            <xsl:with-param name="constants" select=".//constant"/>
        </xsl:call-template>
    </redirect:write>

   <xsl:if test="@user-type = 'true' or @user-type = 'big-decimal'">
     <xsl:variable name="user-type-file"><xsl:value-of
        select="$package.dir"/>/<xsl:value-of
           select="@classname"/>UserType.java</xsl:variable>

     <redirect:write file="{$user-type-file}">
       <xsl:call-template name="fix-point-user-type">
          <xsl:with-param name="classname"
            select="concat(@classname, 'UserType')"/>
          <xsl:with-param name="type-classname"
            select="@classname"/>
          <xsl:with-param name="package" select="@package"/>
       </xsl:call-template>
     </redirect:write>
   </xsl:if>

</xsl:template>

<xsl:template match="valueObject">
   <xsl:variable name="package.dir"><xsl:value-of
      select="$outdir"/>/<xsl:value-of
         select="translate(@package, '.', '/')"/></xsl:variable>

   <xsl:variable name="file"><xsl:value-of
      select="$package.dir"/>/<xsl:value-of
         select="@classname"/>.java</xsl:variable>

  <redirect:write file="{$file}">

   <xsl:call-template name="value-object-generator">
      <xsl:with-param name="classname" select="@classname"/>
      <xsl:with-param name="package" select="@package"/>
      <xsl:with-param name="object" select="."/>
   </xsl:call-template>

   </redirect:write>

</xsl:template>


<xsl:template match="restrictedLong">
   <xsl:variable name="package.dir"><xsl:value-of
      select="$outdir"/>/<xsl:value-of
         select="translate(@package, '.', '/')"/></xsl:variable>
   <xsl:variable name="file"><xsl:value-of
      select="$package.dir"/>/<xsl:value-of
         select="@classname"/>.java</xsl:variable>

   <redirect:write file="{$file}">

   <xsl:call-template name="restricted-long">
      <xsl:with-param name="classname" select="@classname"/>
      <xsl:with-param name="package" select="@package"/>
      <xsl:with-param name="min-value" select="@min-value"/>
      <xsl:with-param name="max-value" select="@max-value"/>
      <xsl:with-param name="implements" select="@implements"/>
      <xsl:with-param name="constants" select=".//constant"/>
   </xsl:call-template>

   <xsl:if test="@user-type = 'true' or @user-type = 'numeric' or @user-type = 'integer'">
     <xsl:variable name="user-type-file"><xsl:value-of
        select="$package.dir"/>/<xsl:value-of
           select="@classname"/>UserType.java</xsl:variable>

     <redirect:write file="{$user-type-file}">
       <xsl:call-template name="restricted-long-user-type">
          <xsl:with-param name="classname"
            select="concat(@classname, 'UserType')"/>
          <xsl:with-param name="type-classname"
            select="@classname"/>
          <xsl:with-param name="package" select="@package"/>
       </xsl:call-template>
     </redirect:write>
   </xsl:if>

   </redirect:write>
</xsl:template>


</xsl:stylesheet>
