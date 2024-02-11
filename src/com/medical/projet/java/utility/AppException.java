/*
 * 
 */
package com.medical.projet.java.utility;

// TODO: Auto-generated Javadoc
/**
 * The Class AppException.
 */
public class AppException {

    /**
     * Prints the error.
     *
     * @param e the e
     */
    public static void printError(Exception e) {
        System.err.println("Problem : " + e.getMessage());
        System.err.println("Cause : "   + e.getCause());
        System.err.println("Type : "    + e.getClass());

        StackTraceElement[] stackTrace = e.getStackTrace();
        for (StackTraceElement element : stackTrace) {
            if (element.getClassName().startsWith("com.myjavafxtemplate")) {
                System.err.println("Location : " + element.getClassName() + "." + element.getMethodName() + "." + element.getLineNumber());
            }
        }
    }

}
