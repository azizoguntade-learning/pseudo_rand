import java.util.LinkedList;
import java.util.ArrayList; 
import java.util.HashMap;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * The main execution environment for the Distributed Wireless Link Scheduling simulation.
 * This class manages the global discrete-time clock, tracks active transmission requests,
 * and evaluates signal interference (affectance) to determine request realization.
 */
public class PsuedoTrulyRand {
    
    public static void main(String[] args) {
        
        LinkedList<Double> trulyRandomNumbers = new LinkedList<>();
        LinkedList<Request> activeRequests = new LinkedList<>();
        HashMap<String, Double> affectanceTable = new HashMap<>();
        
        // Example initialization parameters
        int windowSize = 5; 
        int t = 1; 

        // --- DYNAMIC FILE INITIALIZATION ---
        try {
            // Read the Requests File 
            File requestsFile = new File("requests.txt");
            Scanner requestScanner = new Scanner(requestsFile);
            while (requestScanner.hasNextLine()) {
                String line = requestScanner.nextLine();
                String[] data = line.split(","); 
                
                int reqID = Integer.parseInt(data[0].trim());
                int sendID = Integer.parseInt(data[1].trim());
                int recID = Integer.parseInt(data[2].trim());
                
                Request newReq = new Request(reqID, sendID, recID);
                newReq.setInitialOffset(2); // Placeholder initial schedule
                activeRequests.add(newReq);
            }
            requestScanner.close(); 
            
            // Read the Random Numbers File 
            File randomFile = new File("random_numbers.txt");
            Scanner randomScanner = new Scanner(randomFile);
            while (randomScanner.hasNextLine()) {
                double randomVal = Double.parseDouble(randomScanner.nextLine().trim());
                trulyRandomNumbers.add(randomVal);
            }
            randomScanner.close();

            // Read the Affectance File 
            File affectanceFile = new File("affectance.txt");
            Scanner affectanceScanner = new Scanner(affectanceFile);
            while (affectanceScanner.hasNextLine()) {
                String[] affData = affectanceScanner.nextLine().split(",");
                String key = affData[0].trim() + "-" + affData[1].trim();
                double val = Double.parseDouble(affData[2].trim());
                affectanceTable.put(key, val);
            }
            affectanceScanner.close();
            
        } catch (FileNotFoundException e) {
            System.out.println("Error: One or more input files were not found.");
            System.out.println("Please ensure requests.txt, random_numbers.txt, and affectance.txt exist.");
            return; // Stop the program if the files are missing
        }
        // -----------------------------------
        
        // The discrete-event simulation loop
        while (!activeRequests.isEmpty()) {
            
            ArrayList<Request> transmittingThisRound = new ArrayList<>();
            ArrayList<Request> successfulRequests = new ArrayList<>(); 
            
            /* Identify Active Transmitters */
            for(int i = 0; i < activeRequests.size(); i++){
                Request currentReq = activeRequests.get(i);
                if(currentReq.isTransmitting(t, windowSize)){
                    transmittingThisRound.add(currentReq);
                }
            }
            
            /* Calculate Interference (Affectance) */
            for(int a = 0; a < transmittingThisRound.size(); a++){
                Request requestA = transmittingThisRound.get(a);
                double totalAffectance = 0.0; 
                
                for(int b = 0; b < transmittingThisRound.size(); b++){
                    if(a != b){
                        Request requestB = transmittingThisRound.get(b);
                        String lookupKey = requestB.getRequestID() + "-" + requestA.getRequestID();
                        double affectanceValue = affectanceTable.getOrDefault(lookupKey, 0.0);
                        totalAffectance += affectanceValue;                    
                    }
                }
                
                /* Request Realization */
                if(totalAffectance < 1.0){
                    successfulRequests.add(requestA);
                    System.out.println("Round " + t + ": Request " + requestA.getRequestID() + " realized!");
                }
            }
            
            // Remove all successfully realized requests from the master list in a single batch
            activeRequests.removeAll(successfulRequests);
            
            /* Window Progression */
            if (t % windowSize == 0){
                for(int l = 0; l < activeRequests.size(); l++) {
                    Request remainingReq = activeRequests.get(l);
                    
                    // Safety check: The document notes the need to load batches if this runs out
                    if (trulyRandomNumbers.isEmpty()) {
                        System.out.println("Error: Ran out of random numbers!");
                        return; 
                    }

                    double extractedRandom = trulyRandomNumbers.removeFirst(); 
                    int nextRandomOffset = (int)(extractedRandom * windowSize) + 1; 
                    remainingReq.prepareNextWindow(nextRandomOffset);
                }
            }
            
            t++; 
        }
        
        System.out.println("Simulation complete. All requests realized.");
    }
}