import java.util.*;

// Complexity
// Overall time complexity: O(M log M + M log N).
// Space N - number of cars
public class SimpleCarRentalService {

  static class Request {
    String category;
    int startTime;
    int endTime;

    public Request(String category, int startTime, int endTime) {
      this.category = category;
      this.startTime = startTime;
      this.endTime = endTime;
    }
  }


  private int minimumCarsRequired(List<Request> requests) {

    requests.sort(Comparator.comparing(carRequest -> carRequest.startTime));
    int minCars = 0;
    PriorityQueue<Integer> usedCars = new PriorityQueue<>();

    for (Request request : requests) {
      while (!usedCars.isEmpty() && usedCars.peek() <= request.startTime) {
        usedCars.poll(); // remove the car from the queue
      }
      usedCars.offer(request.endTime);
      minCars = Math.max(minCars, usedCars.size());
    }

    return minCars;
  }

  public static void main(String[] args) {
    // Initialize the car rental system
    SimpleCarRentalService rentalSystem = new SimpleCarRentalService();

    // Add cars to the system
    // Format for start time and end time: [startTime (ms), endTime (ms)]
    //rentalSystem.addCar("C001", "Basic", 0, 10000);  // Available from 0 to 10000 ms
    //rentalSystem.addCar("C002", "Premium", 0, 15000); // Available from 0 to 15000 ms
    //rentalSystem.addCar("C003", "Enterprise", 0, 20000); // Available from 0 to 20000 ms

    // Example list of customer requests
    List<Request> requests = Arrays.asList(
        new Request("Basic", 5000, 6000),    // Basic request from 5000 to 10000 ms
        new Request("Premium", 5000, 6000), // Premium request from 12000 to 16000 ms
//        new Request("Enterprise", 18000, 22000), // Enterprise request from 18000 to 22000 ms
        new Request("Basic", 6000, 7000),    // Basic request from 9000 to 13000 ms
        new Request("Premium", 14000, 18000), // Premium request from 14000 to 18000 ms
        new Request("Basic", 6000, 7000)    // Premium request from 14000 to 18000 ms
    );

    // Get the minimum number of cars required to fulfill the requests
    int carsRequired = rentalSystem.minimumCarsRequired(requests);

    System.out.println("Minimum number of cars required: " + carsRequired);
  }
}
