package com.medical.projet.java.utility;

// TODO: Auto-generated Javadoc
/**
 * The Class AppMemory.
 */
public class AppMemory {

    /**
     * Prints the memory usage.
     */
    public static void printMemoryUsage() {
        System.out.println("//////////////Memory/////////////////");
        // Get the runtime object
        Runtime runtime = Runtime.getRuntime();

        // Total memory in the JVM
        long totalMemory = runtime.totalMemory()  / (1024 * 1024);
        // Free memory in the JVM
        long freeMemory = runtime.freeMemory() / (1024 * 1024);
        // Maximum available memory
        long maxMemory = runtime.maxMemory() / (1024 * 1024);
        // Calculate used memory
        long usedMemory = totalMemory - freeMemory / (1024 * 1024);

        // Create a memory usage string
        String memoryUsage = String.format("Total: %d MB / Free: %d MB /  Max: %d MB / Used: %d MB ", totalMemory, freeMemory, maxMemory, usedMemory);

        // Print memory usage on the same line
        System.out.println(memoryUsage);

    }
}
