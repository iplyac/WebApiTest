package com.qa.utils;

public class StringUtils
{

    /**
     * The method replaces all slashes '/' with '\\\\/'
     * 
     * @param str
     * @return escaped string
     */
    public static String escapeSlashes(String str)
    {
        return str.replaceAll("\\/", "\\\\/");
    }

    /**
     * Reverse string
     * @param str
     * @return String
     */
    public static String reverse(String str)
    {
        return new StringBuilder(str).reverse().toString();
    }
    /**
     * Remove zeros in float string values
     * @param str
     * @return String
     */
    public static String removeLastZeros(String str)
    {
        return str.indexOf(".")<0?str:str.replaceAll("0*$", "").replaceAll("\\.$", "");
    }
    
    public static String urlEncode(String str){
        return str.replace(" ", "%20");
    }
}
