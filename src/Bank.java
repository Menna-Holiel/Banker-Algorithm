public interface Bank {
    void addCustomer(int threadNum, int[] maxDemand);
    void getState();
    boolean requestResources(int threadNum, int[] request);
    void releaseResources(int threadNum, int[] release);
}
