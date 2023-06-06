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
import java.util.Scanner;

public class TwoOpt {

    private static File citiesFile;
    private static File routeFile;
    private static ArrayList<int []> cities;
    private static int[] route;
    private static int minDistance;
    private static FileWriter file;
    private static int ROUTE_LENGTH = 0;
    private static double timerMinute = 30;
    private static long startTime;


    public static void main(String args[]) throws IOException {


        cities = new ArrayList<>();

        try {
            citiesFile = new File("50thousand.txt");

            Scanner scanner = new Scanner(citiesFile);

            while (scanner.hasNext()) {

                scanner.nextInt(); //pass the city id
                int xCoordinate = scanner.nextInt();
                int yCoordinate = scanner.nextInt();

                int[] city = {xCoordinate, yCoordinate};
                cities.add(city);
            }

        }
            catch (Exception e) {
                System.out.println( citiesFile.getName() + " couldn't opened");
                System.exit(0);
            }

        int numberOfCities = cities.size();  // Number of cities is found
        int halfNumberOfCities = (int) Math.ceil(numberOfCities / 2.0);

        route = new int[halfNumberOfCities];
        ROUTE_LENGTH = halfNumberOfCities;


        try {
            routeFile = new File("50thousand-processed.txt");

            Scanner scanner2 = new Scanner(routeFile);

            minDistance = scanner2.nextInt();

            int i = 0;
            while (scanner2.hasNext()) {
                route[i] = scanner2.nextInt();
                i++;
            }

            scanner2.close();
        }
        catch (Exception e) {
            System.out.println(routeFile.getName() + " couldn't opened!");
            System.exit(0);
        }


        System.out.println("Optimizing... Applying 2-opt algorithm...");

        startTime = System.currentTimeMillis();
        int opt2Distance = Opt_2();

        System.out.println("\n2-opt optimized distance: " + opt2Distance);


        //PRINTING THE RESULTS TO FILE
        try {
            file = new FileWriter(  citiesFile.getName().split("\\.")[0] + "-2opt.txt");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        file.write(minDistance + "\n");
        for(int i = 0; i < halfNumberOfCities; i++){
            file.write(route[i] + "\n");
        }
        file.close();

    }


    public static int Opt_2() {
        int newDistance = minDistance;
        int numberOfSwaps = 1;
        int temp;
        int[] newRoute;

        while (numberOfSwaps != 0 && (startTime + (timerMinute * 60 * 1000)) > System.currentTimeMillis()) {
            numberOfSwaps = 0;
            int previousI = 1;
            for (int i = 1; i < ROUTE_LENGTH - 2; i++) {
                for (int j = i + 1; j < ROUTE_LENGTH - 1; j++) {
                    if (findDistance(route[i], route[i - 1]) + findDistance(route[j + 1],
                            route[j]) >= findDistance(route[i], route[j + 1]) + findDistance(
                            route[i - 1], route[j])) {

                        newRoute = swap_2_Points(route,i,j);

                        newDistance = newDistance - findDistance(route[i], route[i - 1]) - findDistance(route[j + 1], route[j]);
                        newDistance = newDistance + findDistance(route[i], route[j + 1]) + findDistance(route[i - 1], route[j]);

                        if (newDistance < minDistance) {
                            minDistance = newDistance;
                            route = newRoute;
                            numberOfSwaps++;
                        }


                    }

                    if(previousI != i) {
                        System.out.println("Processing: " + (i + 1) + "/" + ROUTE_LENGTH);
                        previousI = i;
                    }
                    newDistance = minDistance;
                }
            }
        }
        return minDistance;
    }




    public static int[] swap_2_Points(int [] currentRoute, int i, int j){
        int[] newRoute = new int[ROUTE_LENGTH];

        for(int a = 0 ; a < i ; a++){
            newRoute[a] = currentRoute[a];
        }

        for(int b = j+1 ; b < ROUTE_LENGTH ; b++){
            newRoute[b] = currentRoute[b];
        }

        int reverse = 0;
        for(int c = i ; c <= j ; c++){
            newRoute[c] = currentRoute[j-reverse];
            reverse++;
        }


        return newRoute;
    }


    public static int findDistance(int city1, int city2){

        int x = cities.get(city1)[0];
        int y = cities.get(city1)[1];

        int x1 = cities.get(city2)[0];
        int y1 = cities.get(city2)[1];

        return (int) Math.round(Math.sqrt((x1-x)*(x1-x) + (y1-y)*(y1-y)));
    }


    //erenin
    public static int[] swap_3_Points_4(int [] currentRoute, int i, int j,int k){
        int[] newRoute = new int[ROUTE_LENGTH];

        for(int a = 0 ; a <= i-1 ; a++){
            newRoute[a] = currentRoute[a];
        }

        int index = 0;
        for(int a = j ; a <= i ; a--){
            newRoute[i+index] = currentRoute[a];
            index++;
        }

        for(int a = k ; a >= j + 1 ; a--){
            newRoute[i+index] = currentRoute[a];
            index++;
        }

        for(int a = k+1 ; a<ROUTE_LENGTH ; a++ ){
            newRoute[a] = currentRoute[a];
        }

        return newRoute;
    }

}
