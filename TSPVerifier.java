import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class TSPVerifier {

    static File routeFile;
    static File citiesFile;
    static long minDistance;
    static int[] route;
    private static ArrayList<int[]> cities;

    public static void main(String[] args) {


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


        try {
            routeFile = new File("test-input-3-processed-.txt");

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

        int totalDistance = 0;
        for(int i = 0; i < route.length - 1; i++){
            totalDistance += findDistance(route[i], route[i+1]);
        }
        totalDistance += findDistance(route[0], route[route.length - 1]);


        System.out.println("Calculated: " + totalDistance);





    }


    public static long findDistance(int city1, int city2){

        int x = cities.get(city1)[0];
        int y = cities.get(city1)[1];

        int x1 = cities.get(city2)[0];
        int y1 = cities.get(city2)[1];

        return (long) Math.round(Math.sqrt((x1-x)*(x1-x) + (y1-y)*(y1-y)));
    }


}
