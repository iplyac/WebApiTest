package com.qa.utils;


import java.io.File;
import java.util.List;

import org.apache.log4j.Logger;

public class AssertHelper
{
    private static Logger logger = Logger.getLogger(AssertHelper.class);
    /**
     * Throws fail with error message
     * @param msg
     */
    public static void fail(String msg)
    {
        try
        {

            org.junit.Assert.fail(msg);
        }
        catch (Error e)
        {
            logger.error(msg);
            throw e;
        }
    }
    
    /**
     * Assert that object's value is not null
     * @param obj
     */
    public static void assertNotNull(Object obj)
    {
        assertNotNull("Value "+obj+" is null", obj);
    }
    
    /**
     * Assert that object's value is null
     * @param obj
     */
    public static void assertNull(Object obj)
    {
        assertNull("Value "+obj+" is not null", obj);
    }
    
    /**
     * Assert that object's value is not null
     * @param msg - error message
     * @param obj
     */
    public static void assertNotNull(String msg, Object obj)
    {
        try
        {
            org.junit.Assert.assertNotNull(msg, obj);
        }
        catch (Error e)
        {
            logger.error(msg);
            throw e;
        }
    }
    
    /**
     * Assert that object's value is null
     * @param msg - error message
     * @param obj
     */
    public static void assertNull(String msg, Object obj)
    {
        try
        {
            org.junit.Assert.assertNull(msg, obj);
        }
        catch (Error e)
        {
            logger.error(msg);
            throw e;
        }
    }
    
    /**
     * Assert that string is empty
     * @param str
     */
    public static void assertEmpty(String str)
    {
        assertEmpty("String '"+str+"' is not empty", str);
    }
    
    /**
     * Assert that string is empty
     * @param msg 
     * @param str
     */
    public static void assertEmpty(String msg, String str)
    {
        assertTrue(msg, str.equals(""));
    }
    
    /**
     * Assert that string is not empty
     * @param str
     */
    public static void assertNotEmpty(String str)
    {
        assertNotEmpty("String '"+str+"' is empty", str);
    }
    
    /**
     * Assert that string is not empty
     * @param msg 
     * @param str
     */
    public static void assertNotEmpty(String msg, String str)
    {
        assertFalse(msg, str.equals(""));
    }
    
    /**
     * Assert that actual and expected values are equal
     * @param expected
     * @param actual
     */
    public static void assertEquals(Object expected, Object actual)
    {
        if (!actual.getClass().isInstance(expected))AssertHelper.fail("Uncomparable classes for assertion");
        assertEquals("", expected, actual);
    }
    
    /**
     * Assert that actual and expected values are equal
     * @param msg 
     * @param expected
     * @param actual
     */
    public static void assertEquals(String msg, Object expected, Object actual)
    {
        if (msg!="") msg+=": ";
        try
        {
            org.junit.Assert.assertEquals(msg, expected, actual);
        }
        catch (Error e)
        {
            logger.error(msg + "Actual value '" + actual + "' is not equal expected value '" + expected + "'");
            throw e;
        }
    }

    
    /**
     * Assert that actual and expected values are not equal
     * @param expected
     * @param actual
     */
    public static void assertNotEquals(Object expected, Object actual)
    {
        assertNotEquals(null, expected, actual);
    }
    
    /**
     * Assert that actual and expected values are not equal
     * @param msg 
     * @param expected
     * @param actual
     */
    public static void assertNotEquals(String msg, Object expected, Object actual)
    {
        if (msg!=null) msg+=": ";
        try
        {
            org.junit.Assert.assertNotEquals(msg, expected, actual);
        }
        catch (Error e)
        {
            logger.error(msg + "Actual value '" + actual + "' is equal expected value '" + expected + "'");
            throw e;
        }
    }

    /**
     * Assert that the specified boolean variable has true value
     * @param condition
     * @param errorMessage
     */
    public static void assertTrue(String errorMessage, boolean condition)
    {
        try
        {
            org.junit.Assert.assertTrue(errorMessage, condition);
        }
        catch (Error e)
        {
            logger.error(errorMessage);
            throw e;
        }
    }
    
    /**
     * Assert that the specified boolean variable has true value
     * @param condition
     */
    public static void assertTrue(boolean condition)
    {
        assertTrue("Assertion is failed", condition);
    }

    /**
     * Assert that the specified boolean variable has false value
     * @param condition
     * @param errorMessage
     */
    public static void assertFalse(String errorMessage, boolean condition)
    {
        try
        {
            org.junit.Assert.assertFalse(errorMessage, condition);
        }
        catch (Error e)
        {
            logger.error(errorMessage);
            throw e;
        }
    }
    
    /**
     * Assert that the specified boolean variable has false value
     * @param condition
     */
    public static void assertFalse(boolean condition)
    {
        assertFalse("Assertion is failed", condition);
    }
    
    /**
     * Assert that string contains substring
     * @param str 
     * @param substr 
     */
    public static void assertContains(String str, String substr)
    {
    	assertContains("", str, substr);
    }
    
    /**
     * Assert that string contains substring
     * @param msg 
     * @param str 
     * @param substr 
     */
    public static void assertContains(String msg, String str, String substr)
    {
    	if (msg!="") msg = msg+": ";
    	assertTrue(msg+"'"+str+"' doesn't contain '"+substr+"'", str.contains(substr));
    }
    
    /**
     * Assert that string matches expression
     * @param str 
     * @param expr
     */
    public static void assertMatches(String str, String expr)
    {
    	assertTrue("'"+str+"' doesn't match expression '"+expr+"'", str.matches(expr));
    }
    
    /**
     * Assert that file exist
     * @param file
     */
    public static void assertFileExistence(File file)
    {
        assertTrue(file+" doesn't exist", file.exists());
    }
    
    /**
     * Assert that file absent
     * @param file
     */
    public static void assertFileAbsence(File file)
    {
        assertFalse(file+" exists", file.exists());
    }
    
    /**
     * Assert that folder exists and contains files
     * @param file
     */
    public static void assertFolderIsNotEmpty(File file)
    {
        assertFileExistence(file);
        assertTrue(file+" is empty", file.listFiles().length>0);
    }
    
    /**
     * Assert that folder exists and does not contain files
     * @param file
     */
    public static void assertFolderIsEmpty(File file)
    {
        assertFileExistence(file);
        assertTrue(file+" is not empty", file.listFiles().length==0);
    }
    
    /**
     * Assert that line width of the text is less than the specified value
     * @param scriptText
     * @param lineWidth
     */
    public static void assertLineWidth(String scriptText, int lineWidth)
    {
        int cnt = -1;
        for (int i=0; i<scriptText.length(); i++)
        {
            int new_cnt = scriptText.indexOf('\n', cnt+1);
            if (new_cnt == -1) break;
            String line = scriptText.substring(cnt+1, new_cnt).trim();
            if (!(line.startsWith("'")&&line.endsWith("'")) && !(line.startsWith("\"")&&line.endsWith("\"")) && line.indexOf(' ')!=-1)
                AssertHelper.assertFalse("Line '"+line+"' has width more than "+lineWidth, new_cnt-cnt-1>lineWidth);
            cnt = new_cnt;
        }
    }
    /**
     * Assert equals arrays
     * @param actual
     * @param expected 
     */
    public static void assertEqualsArrays(Object[] expected, Object[] actual)
    {
        int length = actual.length;
        if (length!=expected.length)
        {
            assertFalse("Arrays length not equal : " + length + " : " + expected.length, true);
        }else
        {
            for (int i=0; i<length; i++) {
                Object o1 = actual[i];
                Object o2 = expected[i];
                if (!(o1==null ? o2==null : o1.equals(o2)))
                    assertFalse("Elements with index " + i + " are not equal" ,true);
            }
        }
    }
    
    /**
     * Assert equals lists
     * @param expected
     * @param actual
     */
    public static void assertEqualsLists(List<String> expected, List<String>actual)
    {
        if (expected.size()!=actual.size()){
            assertFalse("Arrays length not equal : " + expected.size() + " : " + actual.size(), true);
        }else
        {
            if (!actual.equals(expected))
                assertFalse("Lists are not equal" ,true);
        }
        
    }
}
