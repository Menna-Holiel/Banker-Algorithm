import java.util.Arrays;
public class BankImpl implements Bank{
    int numberOfCustomer = 10;
    int numberOfResources;
    int[] available;
    int[][] max;
    int[][] allocation;
    int [][] need;

    public BankImpl(int[] resources){
        this.available = Arrays.copyOf(resources, resources.length);
        this.numberOfResources = resources.length;
    }
    @Override
    public void addCustomer(int threadNum, int[] maxDemand) {
        if (maxDemand.length != numberOfResources) {
            throw new IllegalArgumentException("Invalid maxDemand length");
        }

        if(max == null){
            max = new int [numberOfCustomer][numberOfResources];
            allocation = new int[numberOfCustomer][numberOfResources];
            need = new int[numberOfCustomer][numberOfResources];
        }
        max[threadNum] = Arrays.copyOf(maxDemand, maxDemand.length);
        need[threadNum] = Arrays.copyOf(maxDemand, maxDemand.length);
    }

    @Override
    public void getState() {
        System.out.println("Available: " + Arrays.toString(available));
        System.out.println("Maximum: " + Arrays.deepToString(max));
        System.out.println("Allocation: " + Arrays.deepToString(allocation));
        System.out.println("Need: " + Arrays.deepToString(need));
    }

    @Override
    public synchronized boolean requestResources(int threadNum, int[] request) {
        if (request.length != numberOfResources) {
            throw new IllegalArgumentException("Invalid request length");
        }

        for (int i = 0; i < request.length; i++){
            if (request[i] > available[i]) {
                System.out.println("Request exceeds available resources");
                return false;
            }
            if (request[i] > need[threadNum][i]) {
                System.out.println("Request exceeds customer's need");
                return false;
            }
        }
        for (int i = 0; i < request.length; i++){
            available[i] -= request[i];
            allocation[threadNum][i] += request[i];
            need[threadNum][i] -= request[i];

        }
        if(isSafe()){
            return true;
        }else{
            // rollback
            for (int i = 0; i < request.length; i++){
                available[i] += request[i];
                allocation[threadNum][i] -= request[i];
                need[threadNum][i] += request[i];

            }
            System.out.println("Request denied to maintain system safety");
            return false;
        }

    }

    @Override
    public synchronized void releaseResources(int threadNum, int[] release) {
        if (release.length != numberOfResources) {
            throw new IllegalArgumentException("Invalid release length");
        }

        for (int i = 0; i < release.length; i++) {
            available[i] += release[i];
            allocation[threadNum][i] -= release[i];
            need[threadNum][i] += release[i];
        }
    }
private boolean isSafe(){
    int[] work = Arrays.copyOf(available, available.length);
    boolean[] finish = new boolean[max.length];

    for (int i = 0; i < max.length; i++) {
        if (!finish[i] && canFinish(work, need[i])) {
            for (int j = 0; j < work.length; j++) {
                work[j] += allocation[i][j];
            }
            finish[i] = true;
            i = -1; // Restart the check
        }
    }

    // Check if all processes can finish
    for (boolean f : finish) {
        if (!f) {
            return false;
        }
    }
    return true;
}
    private boolean canFinish(int[] work, int[] need) {
        for (int i = 0; i < work.length; i++) {
            if (work[i] < need[i]) {
                return false;
            }
        }
        return true;
    }
}

