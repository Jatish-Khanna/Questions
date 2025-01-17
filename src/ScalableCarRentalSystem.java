import java.util.*;

// time and space
// O(MlogM+M⋅ log N + M.K)
// O(N+M)
// where M is the number of request
// N is the number of cars
// K is the number of categories
// when K is small
public class ScalableCarRentalSystem {

  // Represents a car with an ID, category, start time, and end time
  static class Car {
    String category; // "Basic", "Premium", "Enterprise"
    long endTime;    // End time in milliseconds

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
    String requestedCategory; // The requested car category ("Basic", "Premium", "Enterprise")
    long startTime;  // The time the customer wants to start the rental
    long endTime;    // The time the customer wants to return the car

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

  // The car rental system that uses separate priority queues for each category
  static class CarRentalSystem {
    private final Map<String, PriorityQueue<Car>> carCategoryQueues;
    private final Map<String, Integer> carsNeeded = new HashMap<>();

    private final Map<String, List<String>> categoriesPossible = new HashMap<>();

    public CarRentalSystem() {
      // Initialize separate priority queues for each category
      carCategoryQueues = new HashMap<>();
      carCategoryQueues.put("Basic", new PriorityQueue<>(Comparator.comparingLong(car -> car.endTime)));
      carCategoryQueues.put("Premium", new PriorityQueue<>(Comparator.comparingLong(car -> car.endTime)));
      carCategoryQueues.put("Enterprise", new PriorityQueue<>(Comparator.comparingLong(car -> car.endTime)));
      getCategoryOrder();
    }

    // Add cars to the system
    public void addCar(String category, long endTime) {
      Car car = new Car(category, endTime);
      carCategoryQueues.get(category).offer(car);
    }

    // Find the minimum number of cars required to fulfill requests
    public int minimumCarsRequired(List<Request> requests) {
      int carsUsed = 0;
      requests.sort(Comparator.comparing(request -> request.startTime)); // M log M
      // where M is the number of request

      // Process each request
      for (Request request : requests) { // M
        long requestStart = request.startTime;
        long requestEnd = request.endTime;
        String category = getCategory(request, requestStart); // get the category

        // If the request could not be fulfilled, print a message
        if (category == null) {
          ++carsUsed;
          category = request.requestedCategory;
          carsNeeded.merge(request.requestedCategory, 1, Integer::sum);
          System.out.println("No available car for the " + request + " request.");
        } else {
          var categoryQueue = carCategoryQueues.get(category);
          Car car = categoryQueue.poll();
          // Check if the car is available and suitable for the request
          // The car is available for this time period
          System.out.println("Booking " + car + " for " + request + " request.");
        }
        addCar(category, requestEnd); // log N
      }

      return carsUsed;
    }

    private String getCategory(Request request, long requestStart) {
      // Try to fulfill the request with the requested category or higher
      for (String category : categoriesPossible.get(request.requestedCategory)) { // K
        PriorityQueue<Car> categoryQueue = carCategoryQueues.get(category);

        // Find the first available car in this category
        if (!categoryQueue.isEmpty() && categoryQueue.peek().endTime <= requestStart) {
          return category;
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

    // Add cars to the system
    // Format for start time and end time: [startTime (ms), endTime (ms)]
    //rentalSystem.addCar("C001", "Basic", 0, 10000);  // Available from 0 to 10000 ms
    //rentalSystem.addCar("C002", "Premium", 0, 15000); // Available from 0 to 15000 ms
    //rentalSystem.addCar("C003", "Enterprise", 0, 20000); // Available from 0 to 20000 ms

    // Example list of customer requests
    List<Request> requests = Arrays.asList(
        new Request("Basic", 5000, 10000),    // Basic request from 5000 to 10000 ms
        new Request("Premium", 12000, 16000), // Premium request from 12000 to 16000 ms
        new Request("Enterprise", 18000, 22000), // Enterprise request from 18000 to 22000 ms
        new Request("Basic", 9000, 13000),    // Basic request from 9000 to 13000 ms
        new Request("Premium", 14000, 18000), // Premium request from 14000 to 18000 ms
        new Request("Premium", 2000, 3000)  // Premium request from 14000 to 18000 ms
    );

    // Get the minimum number of cars required to fulfill the requests
    int carsRequired = rentalSystem.minimumCarsRequired(requests);

    System.out.println("Minimum number of cars required: " + carsRequired);
    System.out.println("Cars required by the category: " + rentalSystem.carsNeeded);
  }
}
