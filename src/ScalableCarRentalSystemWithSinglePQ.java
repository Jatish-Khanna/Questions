import java.util.*;

// Time and space complexity analysis is updated to handle large K
// O(MlogM+M∗NlogN)
// O(N+M)

public class ScalableCarRentalSystemWithSinglePQ {

  // Represents a car with an ID, category, start time, and end time
  static class Car {
    String category;  // "Basic", "Premium", "Enterprise"
    long endTime;     // End time in milliseconds

    public Car(String category, long endTime) {
      this.category = category;
      this.endTime = endTime;
    }

    @Override
    public String toString() {
      return endTime + " (" + category + ")";
    }
  }

  // Represents a customer request
  static class Request {
    String requestedCategory;  // The requested car category ("Basic", "Premium", "Enterprise")
    long startTime;            // The time the customer wants to start the rental
    long endTime;              // The time the customer wants to return the car

    public Request(String requestedCategory, long startTime, long endTime) {
      this.requestedCategory = requestedCategory;
      this.startTime = startTime;
      this.endTime = endTime;
    }

    @Override
    public String toString() {
      return requestedCategory + " request from " + new Date(startTime) + " to " + new Date(endTime);
    }
  }

  // The car rental system that uses a single priority queue for all cars
  static class CarRentalSystem {
    private final PriorityQueue<Car> carQueue;  // Single priority queue to manage all cars
    private final Map<String, Integer> carsNeeded = new HashMap<>();  // Tracking number of cars needed

    private final Map<String, List<String>> categoriesPossible = new HashMap<>();

    public CarRentalSystem() {
      // Single priority queue to manage all cars by their availability (end time)
      carQueue = new PriorityQueue<>(Comparator.comparingLong(car -> car.endTime));
      getCategoryOrder();
    }

    // Add cars to the system
    public void addCar(String category, long endTime) {
      Car car = new Car(category, endTime);
      carQueue.offer(car);
    }

    // Find the minimum number of cars required to fulfill requests
    public int minimumCarsRequired(List<Request> requests) {
      int carsUsed = 0;
      requests.sort(Comparator.comparing(request -> request.startTime));  // Sort by start time
      Set<String> removedCategories;
      Map<String, Integer> activeCount = new HashMap<>();

      // Process each request
      for (Request request : requests) {
        long requestStart = request.startTime;
        long requestEnd = request.endTime;
        String category = request.requestedCategory;

        removedCategories = new HashSet<>();

        // poll N cars
        // Try to fulfill the request with the requested category or higher
        while (!carQueue.isEmpty() && carQueue.peek().endTime <= requestStart) {
          Car car = carQueue.poll(); // O(N log N) for polling cars
          removedCategories.add(car.category);  // Remove the car from the queue
          activeCount.merge(car.category, -1, Integer::sum);
          // N log N
        }

        String possibleCategory = assignCar(removedCategories, category, activeCount);

        // If the request could not be fulfilled by an available car, we need to add a new car
        if (possibleCategory == null) {
          carsUsed = Math.max(carsUsed, carQueue.size() + 1);
          possibleCategory = request.requestedCategory;
          carsNeeded.merge(possibleCategory, 1, Integer::sum);
          System.out.println("No available car for the " + request + " request.");
        }
        System.out.println("Booking " + possibleCategory + " for " + request + " request.");
        activeCount.merge(possibleCategory, 1, Integer::sum);
        addCar(possibleCategory, requestEnd);  // Add a new car to the queue
        // log N
      }

      return carsUsed;
    }

    private String assignCar(Set<String> removedCategories, String category, Map<String, Integer> activeCount) {
      // K categories
      for (String mappedCategory : categoriesPossible.get(category)) {
        if (removedCategories.contains(mappedCategory) ||
            activeCount.getOrDefault(mappedCategory, 0) < carsNeeded.getOrDefault(mappedCategory, 0)) {
          return mappedCategory;
        }
      }
      return null;
    }

    // Determine the order of category queues to search (Basic -> Premium -> Enterprise)
    private void getCategoryOrder() {
      categoriesPossible.put("Basic", List.of("Basic", "Premium", "Enterprise"));
      categoriesPossible.put("Premium", List.of("Premium", "Enterprise"));
      categoriesPossible.put("Enterprise", List.of("Enterprise"));
    }
  }

  public static void main(String[] args) {
    // Initialize the car rental system
    CarRentalSystem rentalSystem = new CarRentalSystem();

    // Example list of customer requests
    List<Request> requests = Arrays.asList(
        new Request("Basic", 5000, 6000),    // Basic request from 5000 to 10000 ms
        new Request("Premium", 5000, 6000), // Premium request from 12000 to 16000 ms
//        new Request("Enterprise", 18000, 22000), // Enterprise request from 18000 to 22000 ms
        new Request("Basic", 6000, 7000),    // Basic request from 9000 to 13000 ms
//        new Request("Premium", 14000, 18000), // Premium request from 14000 to 18000 ms
        new Request("Basic", 6000, 7000)    // Premium request from 14000 to 18000 ms
    );

    // Get the minimum number of cars required to fulfill the requests
    int carsRequired = rentalSystem.minimumCarsRequired(requests);

    System.out.println("Minimum number of cars required: " + carsRequired);
    System.out.println("Cars required by the category: " + rentalSystem.carsNeeded);
  }
}
