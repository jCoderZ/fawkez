/*
 * $Id: DbUtilTest.java 1568 2009-11-01 17:46:14Z amandel $
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
package org.jcoderz.commons.util;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Arrays;
import junit.framework.TestCase;

/**
 * Tests the DbUtil class.
 * @author Albrecht Messner
 */
public class DbUtilTest
      extends TestCase
{
   private static final int TEST_BATCH_SIZE = 10;
   private static final int TEST_LOOPS = 50;

   private static final String WRONG_TYPE_CODE = "Wrong type code";
   private static final String WRONG_METHOD_CALLED
         = "Wrong method called on prepared statement";

   /**
    * Tests whether a java.sql.Statement, Connection or ResultSet is
    * closed correctly.
    * @throws Exception if the testcase fails
    */
   @SuppressWarnings("rawtypes")
   public void testCloseObject ()
         throws Exception
   {
      final Class[] testInterfaces = {
            Statement.class,
            Connection.class,
            ResultSet.class
         };

      for (int i = 0; i < testInterfaces.length; i++)
      {
         final TestInvocationHandler invHandler = new TestInvocationHandler();
         final Object obj = Proxy.newProxyInstance(
               Statement.class.getClassLoader(),
               new Class[] {testInterfaces[i]},
               invHandler);
         if (obj instanceof Statement)
         {
            DbUtil.close((Statement) obj);
         }
         else if (obj instanceof Connection)
         {
            DbUtil.close((Connection) obj);
         }
         else
         {
            DbUtil.close((ResultSet) obj);
         }
         assertTrue("Close should have been called on the Statement object",
               invHandler.isCloseCalled());
      }
   }

   /**
    * Tests the setters for various data types on a prepared statement.
    * @throws Exception if the testcase fails
    */
   public void testParameterSetters ()
         throws Exception
   {
      final TestInvocationHandler invHandler = new TestInvocationHandler();
      final PreparedStatement pstmt
            = (PreparedStatement) Proxy.newProxyInstance(
               PreparedStatement.class.getClassLoader(),
               new Class[] {PreparedStatement.class},
               invHandler);

      final String s1 = "Hello World";
      DbUtil.setCharOrNull(pstmt, 0, s1);
      assertEquals(WRONG_METHOD_CALLED,
            "setString", invHandler.getLastMethod());
      DbUtil.setVarcharOrNull(pstmt, 0, s1);
      assertEquals(WRONG_METHOD_CALLED,
            "setString", invHandler.getLastMethod());
      DbUtil.setClobOrNull(pstmt, 0, s1);
      assertEquals(WRONG_METHOD_CALLED,
            "setCharacterStream", invHandler.getLastMethod());

      final Integer i = new Integer(0);
      DbUtil.setIntegerOrNull(pstmt, 0, i);
      assertEquals(WRONG_METHOD_CALLED,
            "setInt", invHandler.getLastMethod());

      final Long l = new Long(0);
      DbUtil.setLongOrNull(pstmt, 0, l);
      assertEquals(WRONG_METHOD_CALLED,
            "setLong", invHandler.getLastMethod());

      final Timestamp tstamp = new Timestamp(0);
      DbUtil.setTimestampOrNull(pstmt, 0, tstamp);
      assertEquals(WRONG_METHOD_CALLED,
            "setTimestamp", invHandler.getLastMethod());
   }

   /**
    * Tests the parameter setters with null parameters.
    * @throws Exception if the testcase fails
    */
   public void testSetNullParameters ()
         throws Exception
   {
      final TestInvocationHandler invHandler = new TestInvocationHandler();
      final PreparedStatement pstmt
            = (PreparedStatement) Proxy.newProxyInstance(
               PreparedStatement.class.getClassLoader(),
               new Class[] {PreparedStatement.class},
               invHandler);
      DbUtil.setCharOrNull(pstmt, 0, null);
      assertEquals(WRONG_TYPE_CODE, invHandler.getTypeCode(), Types.CHAR);
      DbUtil.setClobOrNull(pstmt, 0, null);
      assertEquals(WRONG_TYPE_CODE, invHandler.getTypeCode(), Types.CLOB);
      DbUtil.setIntegerOrNull(pstmt, 0, null);
      assertEquals(WRONG_TYPE_CODE, invHandler.getTypeCode(), Types.INTEGER);
      DbUtil.setLongOrNull(pstmt, 0, null);
      assertEquals(WRONG_TYPE_CODE, invHandler.getTypeCode(), Types.INTEGER);
      DbUtil.setTimestampOrNull(pstmt, 0, null);
      assertEquals(WRONG_TYPE_CODE,
            invHandler.getTypeCode(), Types.TIMESTAMP);
      DbUtil.setVarcharOrNull(pstmt, 0, null);
      assertEquals(WRONG_TYPE_CODE, invHandler.getTypeCode(), Types.VARCHAR);
   }

   /**
    * Tests closing of a null java.sql.Statement, Connection and ResultSet.
    */
   public void testCloseNullObjects ()
   {
      try
      {
         final Statement stmt = null;
         DbUtil.close(stmt);

         final Connection con = null;
         DbUtil.close(con);

         final ResultSet rset = null;
         DbUtil.close(rset);
      }
      catch (Exception x)
      {
         fail("No exception expected when closing a null object");
      }
   }

   public void xtestLimitedBatchSizePreparedStatement ()
         throws Exception
   {
      final PreparedStatementHandler pstmtHandler
            = new PreparedStatementHandler();
      final PreparedStatement pstmt
            = (PreparedStatement) Proxy.newProxyInstance(
               PreparedStatement.class.getClassLoader(),
               new Class[] {PreparedStatement.class},
               pstmtHandler);
      final PreparedStatement wrappedPstmt
            = DbUtil.getLimitedBatchSizePreparedStatement(
               pstmt, TEST_BATCH_SIZE);

      int batchCount = 0;
      for (int i = 0; i < TEST_LOOPS; i++)
      {
         wrappedPstmt.addBatch();
         batchCount++;
         if (batchCount == TEST_BATCH_SIZE + 1)
         {
            assertTrue("Batch update not called",
                  pstmtHandler.isExecuteBatchCalled());
            assertEquals("Wrong batch count",
                  pstmtHandler.getLastUpdatedCount(), TEST_BATCH_SIZE);
            batchCount = 1;
         }
      }
      final int[] updateCount = wrappedPstmt.executeBatch();
      assertEquals("Wrong update counts.", TEST_LOOPS, updateCount.length);
      for (int i = 0; i < updateCount.length; i++)
      {
         assertEquals(
               "Wrong update count", Statement.SUCCESS_NO_INFO, updateCount[i]);
      }
   }

   private static final class TestInvocationHandler
         implements InvocationHandler
   {
      private boolean mCloseCalled = false;
      private String mLastMethod;
      private int mTypeCode = 0;

      public Object invoke (Object proxy, Method method, Object[] args)
            throws Throwable
      {
         if (method.getName().equals("close"))
         {
            mCloseCalled = true;
         }
         else if (method.getName().equals("setNull"))
         {
            final Integer arg2 = (Integer) args[1];
            mTypeCode = arg2.intValue();
         }
         mLastMethod = method.getName();
         return null;
      }

      public boolean isCloseCalled ()
      {
         return mCloseCalled;
      }

      public String getLastMethod ()
      {
         return mLastMethod;
      }

      public int getTypeCode ()
      {
         return mTypeCode;
      }
   }

   private static final class PreparedStatementHandler
         implements InvocationHandler
   {
      private int mBatchCount = 0;
      private boolean mExecuteBatchCalled = false;
      private boolean mClearBatchCalled = true;
      private int mLastUpdated = 0;

      /** {@inheritDoc} */
      public Object invoke (Object proxy, Method method, Object[] args)
            throws Throwable
      {
         final Object result;

         if (method.getName().equals("addBatch"))
         {
            mBatchCount++;
            result = null;
         }
         else if (method.getName().equals("executeBatch"))
         {
            mExecuteBatchCalled = true;
            mLastUpdated = mBatchCount;
            mBatchCount = 0;
            result = new int[mLastUpdated];
            Arrays.fill((int[]) result, Statement.SUCCESS_NO_INFO);
         }
         else if (method.getName().equals("clearBatch"))
         {
            mClearBatchCalled = true;
            result = null;
         }
         else
         {
            throw new UnsupportedOperationException(method.getName());
         }

         return result;
      }

      /**
       * @return Returns the executeBatchCalled.
       */
      public boolean isExecuteBatchCalled ()
      {
         return mExecuteBatchCalled;
      }

      /**
       *
       */
      @SuppressWarnings("unused")
	  public void resetExecuteBatchCalled ()
      {
         mExecuteBatchCalled = false;
      }

      /**
       * @return Returns the batchCount.
       */
      public int getLastUpdatedCount ()
      {
         return mLastUpdated;
      }

      /**
       * @return Returns the clearBatchCalled.
       */
      @SuppressWarnings("unused")
	  public boolean isClearBatchCalled ()
      {
         return mClearBatchCalled;
      }

      /**
       *
       */
      @SuppressWarnings("unused")
	  public void resetClearBatchCalled ()
      {
         mClearBatchCalled = false;
      }
   }
}
