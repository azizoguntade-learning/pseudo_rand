import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class DataGenerator {
    public static void main(String[] args) {
        // Old target: 3,162 nodes naturally produces ~10 million pairs
        // New Target: 10,000 nodes produces 100 million pairs
        long numRequests = 10000; 
        Random pseudoRand = new Random();
        long linesWritten = 0;
        
        System.out.println("Starting brute-force data generation for " + numRequests + " nodes...");
        long startTime = System.currentTimeMillis();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("affectance.txt"))) {
            
            // Unoptimized O(N^2) nested loops: Every sender compares to every receiver
            for (long sender = 1; sender <= numRequests; sender++) {
                for (long receiver = 1; receiver <= numRequests; receiver++) {
                    
                    // A node cannot interfere with itself
                    if (sender != receiver) {
                        
                        // Simulate a random distance between 1.0 and 10.0 units
                        double distance = 1.0 + (pseudoRand.nextDouble() * 9.0);

                        // Inverse-square law: affectance decreases exponentially as distance increases
                        double affectance = 1.0 / (distance * distance);
                        
                        // Write the brute-force pair to the file
                        writer.write(sender + "," + receiver + "," + String.format("%.4f", affectance) + "\n");
                        linesWritten++;
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error writing to file.");
            e.printStackTrace();
        }

        long endTime = System.currentTimeMillis();
        System.out.println("Generation complete! Wrote " + linesWritten + " lines in " + (endTime - startTime) + " ms.");
    }
}