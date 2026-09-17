import java.util.LinkedList;
import java.util.ArrayList; 
import java.util.HashMap;

/**
 * The main execution environment for the Distributed Wireless Link Scheduling simulation.
 * This class manages the global discrete-time clock, tracks active transmission requests,
 * and evaluates signal interference (affectance) to determine request realization.
 */
public class PsuedoTrulyRand {
    
    /**
     * The master clock and simulation loop that processes active transmission requests.
     *
     * @param args Command-line arguments passed to the program.
     */  
    public static void main(String[] args) {
        
        LinkedList<Double> trulyRandomNumbers = new LinkedList<>();
        LinkedList<Request> activeRequests = new LinkedList<>();
        HashMap<String, Double> affectanceTable = new HashMap<>();
        
        // Example initialization parameters
        int windowSize = 5; 
        int t = 1; 

        // --- MOCK INITIALIZATION TESTS ---
        
        // Mock Requests
        Request req1 = new Request(1, 101, 201);
        req1.setInitialOffset(2); // Transmits when t = 2
        activeRequests.add(req1);

        Request req2 = new Request(2, 102, 202);
        req2.setInitialOffset(2); // Also transmits when t = 2
        activeRequests.add(req2);

        trulyRandomNumbers.add(0.15); 
        trulyRandomNumbers.add(0.72); 
        trulyRandomNumbers.add(0.45);
        trulyRandomNumbers.add(0.91);

        // Mock Affectance Table
        affectanceTable.put("2-1", 0.4); // Request 2 interferes with 1
        affectanceTable.put("1-2", 0.7); // Request 1 interferes with 2
        
        // -----------------------------------
        
        // Read file/inputs here to populate activeRequests and assign initial offsets
        
        // The discrete-event simulation loop
        while (!activeRequests.isEmpty()) {
            
            // Temporary lists to track state for the current round 't'
            ArrayList<Request> transmittingThisRound = new ArrayList<>();
            ArrayList<Request> successfulRequests = new ArrayList<>(); 
            
            /*
             * PHASE 1: Identify Active Transmitters
             * Iterate through all pending requests to determine which ones are 
             * scheduled to transmit in the current global time step 't'.
             */
            for(int i = 0; i < activeRequests.size(); i++){
                Request currentReq = activeRequests.get(i);
                if(currentReq.isTransmitting(t, windowSize)){
                    transmittingThisRound.add(currentReq);
                }
            }
            
            /*
             * PHASE 2: Calculate Interference (Affectance)
             * For every transmitting request, calculate the total interference
             * it receives from all OTHER active transmitters in this round.
             */
            for(int a = 0; a < transmittingThisRound.size(); a++){
                Request requestA = transmittingThisRound.get(a);
                double totalAffectance = 0.0; // Reset interference for requestA
                
                for(int b = 0; b < transmittingThisRound.size(); b++){
                    if(a != b){
                        Request requestB = transmittingThisRound.get(b);
                        
                        // Construct the lookup key: "InterferingID-ReceivingID"
                        String lookupKey = requestB.getRequestID() + "-" + requestA.getRequestID();

                        // Retrieve the value. getOrDefault prevents errors if a pair is missing.
                        double affectanceValue = affectanceTable.getOrDefault(lookupKey, 0.0);

                        // Add to the running total
                        totalAffectance += affectanceValue;                    
                    }
                }
                
                /*
                 * PHASE 3: Request Realization
                 * If the total incoming affectance is strictly less than 1.0, 
                 * the transmission is successful and flagged for removal.
                 */
                if(totalAffectance < 1.0){
                    successfulRequests.add(requestA);
                }
            }
            
            // Remove all successfully realized requests from the master list in a single batch
            activeRequests.removeAll(successfulRequests);
            
            /*
             * PHASE 4: Window Progression
             * Check if the current time step marks the end of a transmission window.
             * If so, assign a new randomized offset to all remaining requests.
             */
            if (t % windowSize == 0){
                for(int l = 0; l < activeRequests.size(); l++) {
                    // Pull the specific remaining request using the loop index 'l'
                    Request remainingReq = activeRequests.get(l);
                    
                    // "extract_first" from the random numbers list
                    double extractedRandom = trulyRandomNumbers.removeFirst(); 

                    // Scale the [0.0, 1.0) decimal to an integer between 1 and W
                    int nextRandomOffset = (int)(extractedRandom * windowSize) + 1; 
                    
                    // Call the method on the object to update its internal schedule
                    remainingReq.prepareNextWindow(nextRandomOffset);
                }
            }
            
            t++; // Advance the master clock to the next round
        }
        
        System.out.println("Simulation complete. All requests realized.");
    }
}