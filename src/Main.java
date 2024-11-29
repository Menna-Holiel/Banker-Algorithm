
import java.util.Random;

public class Main {
    public static void main(String[] args) {
        int[] resources = {10, 5, 7}; // Example available resources
        BankImpl bank = new BankImpl(resources);

        // Add customers with their maximum demands
        bank.addCustomer(0, new int[]{7, 5, 3});
        bank.addCustomer(1, new int[]{3, 2, 2});
        bank.addCustomer(2, new int[]{9, 0, 2});
        bank.addCustomer(3, new int[]{2, 2, 2});
        bank.addCustomer(4, new int[]{4, 3, 3});

        System.out.println("FACTORY: created threads");

        // Create threads for each customer
        Thread[] customers = new Thread[5];
        Random random = new Random();

        for (int i = 0; i < customers.length; i++) {
            final int customerNum = i;
            customers[i] = new Thread(() -> {
                try {
                    while (true) {
                        // Randomly generate a request or release
                        boolean isRequest = random.nextBoolean();
                        int[] resourcesChange = new int[resources.length];

                        for (int j = 0; j < resources.length; j++) {
                            resourcesChange[j] = random.nextInt(bank.need[customerNum][j] + 1);
                        }

                        if (isRequest) {
                            synchronized (bank) {
                                System.out.println(
                                        "Customer #" + customerNum + " requesting " + arrayToString(resourcesChange) +
                                                " Available = " + arrayToString(bank.available)
                                );
                                if (!bank.requestResources(customerNum, resourcesChange)) {
                                    System.out.println("INSUFFICIENT RESOURCES");
                                }
                            }
                        } else {
                            synchronized (bank) {
                                for (int j = 0; j < resourcesChange.length; j++) {
                                    resourcesChange[j] = Math.min(resourcesChange[j], bank.allocation[customerNum][j]);
                                }
                                bank.releaseResources(customerNum, resourcesChange);
                                System.out.println(
                                        "Customer #" + customerNum + " releasing " + arrayToString(resourcesChange) +
                                                " Available = " + arrayToString(bank.available) +
                                                " Allocated = " + arrayToString(bank.allocation[customerNum])
                                );
                            }
                        }

                        Thread.sleep(random.nextInt(1000)); // Random delay to simulate processing
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }

        System.out.println("FACTORY: started threads");

        // Start all threads
        for (Thread customer : customers) {
            customer.start();
        }
    }

    private static String arrayToString(int[] array) {
        StringBuilder sb = new StringBuilder();
        for (int val : array) {
            sb.append(val).append(" ");
        }
        return sb.toString().trim();
    }
}
