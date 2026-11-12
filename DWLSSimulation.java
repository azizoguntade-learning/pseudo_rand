import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class DWLSSimulation {
    public static void main(String[] args) {
        String filePath = "affectance.txt";
        
        // Map to track cumulative incoming affectance per receiver node: Receiver ID -> Total Load
        Map<Long, Double> receiverLoadMap = new HashMap<>();
        
        double failureThreshold = 1.0;
        long linesProcessed = 0;
        long shedCount = 0;

        System.out.println("Reading " + filePath + " and evaluating DWLS thresholds...");
        long startTime = System.currentTimeMillis();

        // Using BufferedReader for high-performance streaming of large text files
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                // Split CSV format: sender, receiver, affectance
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    long receiver = Long.parseLong(parts[1].trim());
                    double affectance = Double.parseDouble(parts[2].trim());

                    // Accumulate total interference on this receiver
                    receiverLoadMap.put(receiver, receiverLoadMap.getOrDefault(receiver, 0.0) + affectance);
                    linesProcessed++;

                    // Progress indicator every 10 million lines
                    if (linesProcessed % 10000000 == 0) {
                        System.out.println("Processed " + (linesProcessed / 1000000) + " million lines...");
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading the affectance file.");
            e.printStackTrace();
        }

        // Evaluate DWLS load shedding: Check which receivers breached the 1.0 failure threshold
        for (Map.Entry<Long, Double> entry : receiverLoadMap.entrySet()) {
            if (entry.getValue() > failureThreshold) {
                shedCount++;
            }
        }

        long endTime = System.currentTimeMillis();
        
        // Print out simulation metrics
        System.out.println("\n--- DWLS Simulation Results ---");
        System.out.println("Total lines parsed: " + linesProcessed);
        System.out.println("Unique receivers evaluated: " + receiverLoadMap.size());
        System.out.println("Nodes exceeding 1.0 threshold (Load Shedding triggered): " + shedCount);
        System.out.println("Execution time: " + (endTime - startTime) + " ms.");
    }
}