/**
 * Represents a single transmission request between a sender and a receiver.
 * Maintains the localized scheduling state for the discrete-event simulation.
 */
public class Request {
    private int requestID; 
    private int senderID;
    private int receiverID;
    private int curr_wind_index;
    private int rand_chosen_offset;

    /**
     * Initializes a new transmission request.
     *
     * @param requestID  The unique identifier for this request.
     * @param senderID   The unique identifier for the sending station.
     * @param receiverID The unique identifier for the receiving station.
     */
    public Request(int requestID, int senderID, int receiverID) {
        this.requestID = requestID;
        this.senderID = senderID;
        this.receiverID = receiverID;
        this.curr_wind_index = 0; 
    }

    /**
     * @return The unique identifier for this request.
     */
    public int getRequestID() { return requestID; }
    
    /**
     * @return The unique identifier for the sending station.
     */
    public int getSenderID() { return senderID; }

    /**
     * @return The unique identifier for the receiving station.
     */
    public int getReceiverID() { return receiverID; }

    /**
     * @return The current window index (i) for the sender.
     */
    public int getCurrWindIndex() { return curr_wind_index; }

    /**
     * @return The randomly chosen offset within the current window.
     */
    public int getRandChosenOffset() { return rand_chosen_offset; }

    /**
     * Assigns the first randomized offset before the simulation begins.
     *
     * @param offset The initial random integer chosen from [1, W].
     */
    public void setInitialOffset(int offset) {
        this.rand_chosen_offset = offset;
    }

    /**
     * Evaluates if the sender is scheduled to transmit in the given round.
     *
     * @param currentRound The current time step of the global simulation (t).
     * @param windowSize   The fixed size of the scheduling window (W).
     * @return true if the current round matches the sender's scheduled time; false otherwise.
     */
    public boolean isTransmitting(int currentRound, int windowSize) {
        return currentRound == (curr_wind_index * windowSize) + rand_chosen_offset;
    }

    /**
     * Advances the request's internal schedule to the next window.
     *
     * @param newRandomOffset The new random integer chosen from [1, W] for the upcoming window.
     */
    public void prepareNextWindow(int newRandomOffset) {
        curr_wind_index++;
        rand_chosen_offset = newRandomOffset;
    }
}