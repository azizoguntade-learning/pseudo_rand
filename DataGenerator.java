import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class DataGenerator {
    public static void main(String[] args) {
        // Scaled-down Network: 3,162 nodes naturally produces ~10 million pairs
        long numRequests = 3162; 
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
                        
                        // Generate a standard random affectance value between 0.0 and 1.0
                        double affectance = pseudoRand.nextDouble();
                        
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