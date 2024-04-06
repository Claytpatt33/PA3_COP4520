import java.util.concurrent.*;
import java.util.*;
import java.util.stream.*;

class MarsRoverTemperatureModule {
    private static final int NUMBER_OF_SENSORS = 8;
    private static final List<ConcurrentLinkedQueue<Integer>> temperatureReadings = new CopyOnWriteArrayList<>();

    public static void main(String[] args) throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(NUMBER_OF_SENSORS);
        initializeSharedMemory();

        for (int i = 0; i < NUMBER_OF_SENSORS; i++) {
            final int sensorId = i;
            executor.submit(() -> {
                Random random = new Random();
                while (!Thread.currentThread().isInterrupted()) {
                    int temperature = -100 + random.nextInt(171); 
                    temperatureReadings.get(sensorId).offer(temperature);
                    try {
                        
                        TimeUnit.SECONDS.sleep(5);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            });
        }

        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(MarsRoverTemperatureModule::generateReport, 0, 1, TimeUnit.MINUTES);

        TimeUnit.MINUTES.sleep(2);
        executor.shutdownNow();
        scheduler.shutdownNow();
    }

    private static void initializeSharedMemory() {
        for (int i = 0; i < NUMBER_OF_SENSORS; i++) {
            temperatureReadings.add(new ConcurrentLinkedQueue<>());
        }
    }

    private static void generateReport() {
        List<Integer> allTemperatures = temperatureReadings.stream()
                .flatMap(Queue::stream)
                .collect(Collectors.toList());

        if (allTemperatures.isEmpty()) {
            System.out.println("No temperature readings available.");
            return;
        }
        List<Integer> top5Highest = allTemperatures.stream()
            .sorted(Comparator.reverseOrder())
            .limit(5)
            .collect(Collectors.toList());
        List<Integer> top5Lowest = allTemperatures.stream()
            .sorted()
            .limit(5)
            .collect(Collectors.toList());

        int maxDiff = 0;
        int startInterval = 0;
        for (int i = 0; i < allTemperatures.size() - 10 && i + 10 < allTemperatures.size(); i++) {
            int diff = Collections.max(allTemperatures.subList(i, i + 10)) - Collections.min(allTemperatures.subList(i, i + 10));
            if (diff > maxDiff) {
                maxDiff = diff;
                startInterval = i;
            }
        }

        System.out.println("Temperature Report:");
        System.out.println("Top 5 Highest Temperatures: " + top5Highest);
        System.out.println("Top 5 Lowest Temperatures: " + top5Lowest);
        System.out.println("Interval with largest temperature difference starts at reading " + startInterval + " with a difference of " + maxDiff + "F");

        temperatureReadings.forEach(Queue::clear);
    }
}
