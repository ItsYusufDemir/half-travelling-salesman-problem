/* Authors: Eren Duyuk - 150120509
 *          Selin Aydın - 150120061
 *          Yusuf Demir - 150120032
 *
 * Date: 31.05.2023 14:27
 *
 * Description: Solving the half travelling salesman problem. This problem is different than the normal tsp problem.
 * Given n cities, we should find the best route by choosing n/2 cities.
 */

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class ThreeOptRandom {

    private static File citiesFile;
    private static File routeFile;
    private static ArrayList<int[]> cities;
    private static int[] route;
    private static long minDistance;
    private static FileWriter file;
    private static long startTime;
    private static double timerMinute = 1;
    private static int ROUTE_LENGTH = 0;


    public static void main(String args[]) throws IOException {


        cities = new ArrayList<>();

        try {
            citiesFile = new File("test-input-3.txt");

            Scanner scanner = new Scanner(citiesFile);

            while (scanner.hasNext()) {

                scanner.nextInt(); //pass the city id
                int xCoordinate = scanner.nextInt();
                int yCoordinate = scanner.nextInt();

                int[] city = {xCoordinate, yCoordinate};
                cities.add(city);
            }

        } catch (Exception e) {
            System.out.println(citiesFile.getName() + " couldn't opened");
            System.exit(0);
        }


        int numberOfCities = cities.size();  // Number of cities is found
        int halfNumberOfCities = (int) Math.ceil(numberOfCities / 2.0);

        route = new int[halfNumberOfCities];
        ROUTE_LENGTH = halfNumberOfCities;


        try {
            routeFile = new File("test-input-3-processed-std_factor-0.1.txt");

            Scanner scanner2 = new Scanner(routeFile);

            minDistance = scanner2.nextInt();

            int i = 0;
            while (scanner2.hasNext()) {
                route[i] = scanner2.nextInt();
                i++;
            }

            scanner2.close();
        } catch (Exception e) {
            System.out.println(routeFile.getName() + " couldn't opened!");
            System.exit(0);
        }


        System.out.println("Optimizing... Applying 3-opt algorithm...");


        startTime = System.currentTimeMillis();

        if (checkDuplicate(route)) {
            System.out.println("Duplicate found in the input file");
            System.exit(0);
        }


       long opt3Distance = Opt_3();
        System.out.println("\n3-opt optimized distance: " + opt3Distance);
        System.out.println("\nRunning Time (minute): " + timerMinute);


        //PRINTING THE RESULTS TO FILE
        try {
            file = new FileWriter(citiesFile.getName().split("\\.")[0] + "-3opt-random-" + minDistance + "-" + ".txt");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        file.write(minDistance + "\n");
        for (int i = 0; i < halfNumberOfCities; i++) {
            file.write(route[i] + "\n");
        }
        file.close();

    }


    public static long Opt_3() {
        int numberOfSwaps = 1;
        long newDistance = minDistance;
        long tempDistance = 0;
        int[] newRoute;
        int[] tempRoute = route;
        int count = 0;

        while ((startTime + (timerMinute * 60 * 1000)) > System.currentTimeMillis()) {
            // Select three indices for swapping
            int[] indices = selectIndices();
            int i = indices[0];
            int j = indices[1];
            int k = indices[2];

            // Swap operation a
            if (findDistance(route[j], route[j + 1]) + findDistance(route[k + 1], route[k]) >= findDistance(route[j], route[k]) + findDistance(route[j + 1], route[k + 1])) {
                newRoute = swap_2_Points(route, j + 1, k);
                newDistance = findTotalDistance(newRoute);

                if (newDistance < minDistance) {
                    minDistance = newDistance;
                    tempRoute = newRoute;
                    numberOfSwaps++;
                    System.out.println("**" + minDistance);
                }
            }

            // Swap operation b
            if (findDistance(route[i], route[i - 1]) + findDistance(route[j], route[j + 1]) >= findDistance(route[j], route[i - 1]) + findDistance(route[j + 1], route[i])) {
                newRoute = swap_2_Points(route, i, j);
                newDistance = findTotalDistance(newRoute);

                if (newDistance < minDistance) {
                    minDistance = newDistance;
                    tempRoute = newRoute;
                    numberOfSwaps++;
                    System.out.println("**" + minDistance);
                }
            }

            // Swap operation c
            if (findDistance(route[i], route[i - 1]) + findDistance(route[k + 1], route[k]) >= findDistance(route[i - 1], route[k]) + findDistance(route[i], route[k + 1])) {
                newRoute = swap_2_Points(route, i - 1, k + 1);
                newDistance = findTotalDistance(newRoute);

                if (newDistance < minDistance) {
                    minDistance = newDistance;
                    tempRoute = newRoute;
                    numberOfSwaps++;
                    System.out.println("**" + minDistance);
                }
            }

            // Swap operation d
            if (findDistance(route[i - 1], route[i]) + findDistance(route[j + 1], route[j]) + findDistance(route[k + 1], route[k]) >= findDistance(route[i], route[k]) + findDistance(route[i - 1], route[j]) + findDistance(route[j + 1], route[k + 1])) {
                newRoute = swap_3_Points_4(route, i, j, k);
                newDistance = findTotalDistance(newRoute);

                if (newDistance < minDistance) {
                    minDistance = newDistance;
                    tempRoute = newRoute;
                    numberOfSwaps++;
                    System.out.println("**" + minDistance);
                }
            }

            // Swap operation e
            if (findDistance(route[i - 1], route[i]) + findDistance(route[j + 1], route[j]) + findDistance(route[k + 1], route[k]) >= findDistance(route[i], route[k + 1]) + findDistance(route[k], route[j]) + findDistance(route[j + 1], route[i - 1])) {
                newRoute = swap_3_Points_3(route, i, j, k);
                newDistance = findTotalDistance(newRoute);

                if (newDistance < minDistance) {
                    minDistance = newDistance;
                    tempRoute = newRoute;
                    numberOfSwaps++;
                    System.out.println("**" + minDistance);
                }
            }

            // Swap operation g
            if (findDistance(route[i - 1], route[i]) + findDistance(route[j + 1], route[j]) + findDistance(route[k + 1], route[k]) >= findDistance(route[j], route[k + 1]) + findDistance(route[k], route[i - 1]) + findDistance(route[j + 1], route[i])) {
                newRoute = swap_3_Points_1(route, i, j, k);
                newDistance = findTotalDistance(newRoute);

                if (newDistance < minDistance) {
                    minDistance = newDistance;
                    tempRoute = newRoute;
                    numberOfSwaps++;
                    System.out.println("**" + minDistance);
                }
            }

            // Swap operation h
            if (findDistance(route[i - 1], route[i]) + findDistance(route[j + 1], route[j]) + findDistance(route[k + 1], route[k]) >= findDistance(route[i], route[k]) + findDistance(route[j], route[k + 1]) + findDistance(route[j + 1], route[i - 1])) {
                newRoute = swap_3_Points_2(route, i, j, k);
                newDistance = findTotalDistance(newRoute);

                if (newDistance < minDistance) {
                    minDistance = newDistance;
                    tempRoute = newRoute;
                    numberOfSwaps++;
                    System.out.println("**" + minDistance);
                }
            }

            // Update the route if a better solution is found
            if (tempRoute != null)
                route = tempRoute;
        }

        return minDistance;
    }


    public static int[] swap_2_Points(int[] currentRoute, int i, int j) {
        int[] newRoute = new int[ROUTE_LENGTH];

        // Copy elements before index i from currentRoute to newRoute
        for (int a = 0; a < i; a++) {
            newRoute[a] = currentRoute[a];
        }

        // Copy elements after index j from currentRoute to newRoute
        for (int b = j + 1; b < ROUTE_LENGTH; b++) {
            newRoute[b] = currentRoute[b];
        }

        int reverse = 0;
        // Reverse the order of elements between indices i and j
        for (int c = i; c <= j; c++) {
            newRoute[c] = currentRoute[j - reverse];
            reverse++;
        }

        return newRoute;
    }


    public static long findDistance(int city1, int city2) {

        long x = cities.get(city1)[0];
        long y = cities.get(city1)[1];

        long x1 = cities.get(city2)[0];
        long y1 = cities.get(city2)[1];

        return (long) Math.round(Math.sqrt((x1 - x) * (x1 - x) + (y1 - y) * (y1 - y)));
    }


    public static int[] swap_3_Points_3(int[] currentRoute, int i, int j, int k) {
        int[] newRoute = new int[ROUTE_LENGTH];

        int index = 0;
        // Copy elements before index i from currentRoute to newRoute
        for (int a = 0; a <= i - 1; a++) {
            newRoute[index] = currentRoute[a];
            index++;
        }

        // Copy elements between indices j+1 and k from currentRoute to newRoute
        for (int a = j + 1; a <= k; a++) {
            newRoute[index] = currentRoute[a];
            index++;
        }

        // Copy elements in reverse order between indices j and i from currentRoute to newRoute
        for (int a = j; a >= i; a--) {
            newRoute[index] = currentRoute[a];
            index++;
        }

        // Copy elements after index k from currentRoute to newRoute
        for (int a = k + 1; a < ROUTE_LENGTH; a++) {
            newRoute[index] = currentRoute[a];
            index++;
        }

        return newRoute;
    }


    public static int[] swap_3_Points_1(int[] currentRoute, int i, int j, int k) {
        int[] newRoute = new int[ROUTE_LENGTH];

        int index = 0;
        // Copy elements before index i from currentRoute to newRoute
        for (int a = 0; a <= i - 1; a++) {
            newRoute[index] = currentRoute[a];
            index++;
        }

        // Copy elements in reverse order between indices k and j+1 from currentRoute to newRoute
        for (int a = k; a >= j + 1; a--) {
            newRoute[index] = currentRoute[a];
            index++;
        }

        // Copy elements between indices i and j from currentRoute to newRoute
        for (int a = i; a <= j; a++) {
            newRoute[index] = currentRoute[a];
            index++;
        }

        // Copy elements after index k from currentRoute to newRoute
        for (int a = k + 1; a < ROUTE_LENGTH; a++) {
            newRoute[index] = currentRoute[a];
            index++;
        }

        return newRoute;
    }


    public static int[] swap_3_Points_2(int[] currentRoute, int i, int j, int k) {
        int[] newRoute = new int[ROUTE_LENGTH];

        int index = 0;
        // Copy elements before index i from currentRoute to newRoute
        for (int a = 0; a <= i - 1; a++) {
            newRoute[index] = currentRoute[a];
            index++;
        }

        // Copy elements between indices j+1 and k from currentRoute to newRoute
        for (int a = j + 1; a <= k; a++) {
            newRoute[index] = currentRoute[a];
            index++;
        }

        // Copy elements between indices i and j from currentRoute to newRoute
        for (int a = i; a <= j; a++) {
            newRoute[index] = currentRoute[a];
            index++;
        }

        // Copy elements after index k from currentRoute to newRoute
        for (int a = k + 1; a < ROUTE_LENGTH; a++) {
            newRoute[index] = currentRoute[a];
            index++;
        }

        return newRoute;
    }


    public static int[] swap_3_Points_4(int[] currentRoute, int i, int j, int k) {
        int[] newRoute = new int[ROUTE_LENGTH];

        int index = 0;
        // Copy elements before index i from currentRoute to newRoute
        for (int a = 0; a <= i - 1; a++) {
            newRoute[index] = currentRoute[a];
            index++;
        }

        // Copy elements in reverse order between indices j and i from currentRoute to newRoute
        for (int a = j; a >= i; a--) {
            newRoute[index] = currentRoute[a];
            index++;
        }

        // Copy elements in reverse order between indices k and j+1 from currentRoute to newRoute
        for (int a = k; a >= j + 1; a--) {
            newRoute[index] = currentRoute[a];
            index++;
        }

        // Copy elements after index k from currentRoute to newRoute
        for (int a = k + 1; a < ROUTE_LENGTH; a++) {
            newRoute[index] = currentRoute[a];
            index++;
        }

        return newRoute;
    }


    public static int[] selectIndices() {
        // Generate random indices i, j, and k
        int i = (int) (Math.random() * (ROUTE_LENGTH - 5)) + 1;
        int j = (int) (Math.random() * (ROUTE_LENGTH - i - 4)) + i + 1;
        int k = (int) (Math.random() * (ROUTE_LENGTH - j - 3)) + j + 2;

        int temp[] = {i, j, k};

        return temp;
    }


    public static boolean checkDuplicate(int[] array) {
        // Create a sorted copy of the array
        int arr[] = array.clone();
        Arrays.sort(arr);

        // Check for duplicates in the sorted array
        for (int i = 0; i < arr.length - 1; i++) {
            if (arr[i] == arr[i + 1])
                return true;
        }

        return false;
    }


    public static long findTotalDistance(int[] newRoute){

        long newDistance = 0;
        for(int a  = 0; a < (ROUTE_LENGTH-1) ; a++){
            newDistance += findDistance(newRoute[a],newRoute[a+1]);
        }
        newDistance += findDistance(route[0], route[route.length - 1]);

        return newDistance;
    }
}