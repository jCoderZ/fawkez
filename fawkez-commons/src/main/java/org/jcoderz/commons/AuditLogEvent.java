/*
 * $Id: AuditLogEvent.java 1011 2008-06-16 17:57:36Z amandel $
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
package org.jcoderz.commons;

import org.jcoderz.commons.util.HashCodeUtil;

/**
 * This is the base class for audit log events.
 *
 * <p>The base class of this AuditLogEvent is Throwable but instances
 * of this class are not expected to be thrown.</p>
 *
 * <p>Most functionality is implemented and documented by the
 * {@link org.jcoderz.commons.LoggableImpl} which is used a member of
 * objects of this class.</p>
 *
 * @see org.jcoderz.commons
 * @author Andreas Mandel
 * @author Michael Griffel
 */
public class AuditLogEvent
      extends LogEvent
{
   /** Key used for the audit principal parameter object. */
   public static final String AUDIT_PRINCIPAL_PARAMETER_NAME
         = "_AUDIT_PRINCIPAL";

   /**
    * The class fingerprint that is set to indicate serialization
    * compatibility with a previous version of the class.
    * Corresponds to CVS revision 1.3 of the class.
    */
   static final long serialVersionUID = 3L;

   /** the principal of the AuditLogEvent */
   private final AuditPrincipal mAuditPrincipal;

   /** The hashcode value, lazy initialized. */
   private transient int mLazyHashCode = 0;

   /**
    * Constructor to create a AuditLogEvent instance with the minimum
    * mandatory parameters.
    * @param messageInfo the log message info of this audit log event.
    * @param principal the audit principal that cause this audit log event.
    */
   public AuditLogEvent (LogMessageInfo messageInfo, AuditPrincipal principal)
   {
      super(messageInfo);
      mAuditPrincipal = principal;
      addParameter(AUDIT_PRINCIPAL_PARAMETER_NAME, principal);
   }

   /**
    * Constructor to create a AuditLogEvent instance with a
    * given root <tt>cause</tt>.
    * @param messageInfo the log message info of this audit log event.
    * @param principal the audit principal that cause this audit log event.
    * @param cause the cause of this audit log event.
    */
   public AuditLogEvent (LogMessageInfo messageInfo, AuditPrincipal principal,
         Throwable cause)
   {
      super(messageInfo, cause);
      mAuditPrincipal = principal;
      addParameter(AUDIT_PRINCIPAL_PARAMETER_NAME, principal);
   }

   /**
    * Returns the audit principal of this audit log event.
    * @return the audit principal of this audit log event.
    */
   public final AuditPrincipal getAuditPrincipal ()
   {
      return mAuditPrincipal;
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
      boolean equals = false;
      if (obj instanceof AuditLogEvent)
      {
         final AuditLogEvent algEvent = (AuditLogEvent) obj;
         if (mAuditPrincipal.equals(algEvent.getAuditPrincipal())
               && getLogMessageInfo().equals(algEvent.getLogMessageInfo())
               && getCause().equals(algEvent.getCause()))
         {
            equals = true;
         }
      }
      return equals;
   }

   /**
    * Override hashCode.
    * @return the Object's hashcode.
    */
   public int hashCode ()
   {
      if (mLazyHashCode == 0)
      {
         mLazyHashCode = HashCodeUtil.SEED;
         mLazyHashCode = HashCodeUtil.hash(mLazyHashCode, mAuditPrincipal);
         mLazyHashCode = HashCodeUtil.hash(mLazyHashCode, getLogMessageInfo());
         mLazyHashCode = HashCodeUtil.hash(mLazyHashCode, getCause());
      }
      return mLazyHashCode;
   }
}
