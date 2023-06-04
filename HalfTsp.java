/* Authors: Eren Duyuk - 150120509
 *          Selin Aydın - 150120061
 *          Yusuf Demir - 150120032
 *
 * Date: 31.05.2023 14:27
 *
 * Description: Solving the half travelling salesman problem. This problem is different than the normal tsp problem.
 * Given n cities, we should find the best route by choosing n/2 cities.
 */

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class HalfTsp {

    private static int numberOfCities = 0;
    private static int halfNumberOfCities;
    private static int maxX = 0;
    private static int minX = 0;
    private static int maxY = 0;
    private static int minY = 0;
    private static File inputFile;
    private static FileWriter file;
    private static FileWriter file2;
    private static ArrayList<Integer>[][] areas;
    private static ArrayList<int []> cities;
    private static int xGridSize;
    private static int yGridSize;
    private static int numberOfAreas;
    private static boolean isRemoved[];
    private static int minDistance = Integer.MAX_VALUE;
    private static int[] route;


    public static void main(String[] args) throws Exception {

        long start = System.currentTimeMillis();
        Scanner input = new Scanner(System.in);


        cities = new ArrayList<>();

        try {
            inputFile = new File("example-input-3.txt");

            Scanner scanner = new Scanner(inputFile);

            while (scanner.hasNext()) {

                scanner.nextInt(); //pass the city id
                int xCoordinate = scanner.nextInt();

                if(xCoordinate > maxX)
                    maxX = xCoordinate;
                if(xCoordinate < minX)
                    minX = xCoordinate;


                int yCoordinate = scanner.nextInt();

                if(yCoordinate > maxY)
                    maxY = yCoordinate;
                if(yCoordinate < minY)
                    minY = xCoordinate;


                int[] city = {xCoordinate, yCoordinate};
                cities.add(city);
            }

            scanner.close();
        } catch (Exception e) {
            System.out.println("File couldn't opened!");
            System.exit(0);
        }


        numberOfCities = cities.size();  // Number of cities is found
        halfNumberOfCities = (int) Math.ceil(numberOfCities / 2.0);


        numberOfAreas = (int)Math.round(Math.sqrt(numberOfCities/2)); //Let's initialize number of areas per line by square root of n/2


        double standardDeviation = 0;
        int lowerLimit = 0;
        double averageDensity = 0;
        int numberOfCityEliminated = 0;
        double eliminatePercentage = 0;
        route = new int[halfNumberOfCities];
        int lastNumberOfAreas = numberOfAreas;
        int[] currentRoute = null;



        final double STANDARD_FACTOR = 0.1;
        double stdFactor = 2;
        int decreaseArea = (int) Math.ceil(numberOfAreas * 0.01);
        int counter = 0;
        System.out.print("Preprocessing data and applying the nearest neighbor algorithm... ");

        /* Here we apply the nearest neighbor algorithm. Data is chosen by eliminating some of them.
         * In the first place, we divide our map into areas. As a beginning, we set the number of columns and number of rows
         * as sqrt(n/2). Then we find sum of the number of cities for each areas. Then we find the standard deviation and eliminte the
         * average + standardFactor * standardDeviation, for example: average + 1.5standardDeviation
         * We try to find the best standardFactor and best division of areas for the given map.
         */
        while(stdFactor >= 1) { //Loop that controls the standard deviation factor

            numberOfAreas = (int)Math.round(Math.sqrt(numberOfCities/2));
            int processed = 0;
            int total = numberOfAreas;
            double progress = 0;

            do {

                //Remove the cities as they visited, store that information into an array
                isRemoved = new boolean[numberOfCities];
                for (int i = 0; i < numberOfCities; i++)
                    isRemoved[i] = false;

                xGridSize = (maxX - minX) / numberOfAreas + 1;
                yGridSize = (maxY - minY) / numberOfAreas + 1;
                areas = new ArrayList[numberOfAreas][numberOfAreas];

                //Initialize the 2D array
                for (int i = 0; i < numberOfAreas; i++) {
                    for (int j = 0; j < numberOfAreas; j++)
                        areas[i][j] = new ArrayList<>();
                }


                //Add the cities to appropriate areas
                for (int i = 0; i < numberOfCities; i++) {
                    int xCoordinate = cities.get(i)[0];
                    int yCoordinate = cities.get(i)[1];

                    int xIndex = xCoordinate / xGridSize;
                    int yIndex = yCoordinate / yGridSize;

                    areas[xIndex][yIndex].add(i);
                }


                //Calculate the standard deviation
                int mostIntense = 0;
                double currentAverageDensity = (double) numberOfCities / (numberOfAreas * numberOfAreas);
                double sumOfSquare = 0;

                for (int i = 0; i < numberOfAreas; i++) {

                    for (int j = 0; j < numberOfAreas; j++) {

                        int currentSize = areas[i][j].size();

                        if (currentSize > mostIntense)
                            mostIntense = currentSize;

                        sumOfSquare += Math.pow((currentSize - currentAverageDensity), 2);

                    }
                }

                double currentStandardDeviation = Math.sqrt(sumOfSquare / numberOfCities);

                int currentLowerLimit = (int) Math.floor(currentAverageDensity - stdFactor * currentStandardDeviation);
                int currentNumberOfCityEliminated = 0;


                //Eliminate the cities below the limit
                for (int i = 0; i < numberOfAreas; i++) {

                    for (int j = 0; j < numberOfAreas; j++) {

                        if (areas[i][j].size() <= currentLowerLimit) {
                            eliminateArea(i, j);
                            currentNumberOfCityEliminated += areas[i][j].size();
                        }
                    }

                }

                double currentEliminatePercentage = ((double) currentNumberOfCityEliminated / numberOfCities) * 100;


                if (currentEliminatePercentage >= 50) { //If we eliminate more than 50% of the data, then continue to next iteration
                    numberOfAreas--;
                    continue;
                }


                // Applying the nearest neighbor algorithm
                currentRoute = new int[halfNumberOfCities];


                //Choosing the first city and adding it into the route
                int startingCity = findStartingCity(0, 0, numberOfAreas - 1, numberOfAreas - 1);
                isRemoved[startingCity] = true;
                currentRoute[0] = startingCity;


                int currentDistance = 0;

                for (int i = 1; i < halfNumberOfCities; i++) {
                    int newCity = findNearestNeighbor(startingCity);
                    isRemoved[newCity] = true; //Eliminate the city that we found
                    currentDistance += findDistance(startingCity, newCity);
                    startingCity = newCity;
                    currentRoute[i] = newCity; //Add the found city into the route array
                }

                currentDistance += findDistance(currentRoute[0], currentRoute[halfNumberOfCities - 1]); //Add the distance where we stop and where we begin

                if (currentDistance < minDistance) {
                    standardDeviation = currentStandardDeviation;
                    lowerLimit = currentLowerLimit;
                    averageDensity = currentAverageDensity;
                    numberOfCityEliminated = currentNumberOfCityEliminated;
                    eliminatePercentage = currentEliminatePercentage;
                    minDistance = currentDistance;
                    lastNumberOfAreas = numberOfAreas;

                    route = currentRoute;
                }



                /*Decrease the number of areas by 1% of the sqrt(n/2).
                 *We might decrease it by 1 to get a better result, but it takes too much time for large inputs
                 */
                numberOfAreas = numberOfAreas - decreaseArea;

                //Print the processed percentage for user information
                if(processed/(double)total >= progress){
                    System.out.printf("Processing: %d/%d %.2f%%\n", counter + 1, (int)(1/STANDARD_FACTOR), (processed/(double)total) * 100);
                    processed = processed + decreaseArea;
                    progress += 0.01;
                }


            } while (numberOfAreas > 0);

            counter++;
            stdFactor -= STANDARD_FACTOR; //Decrease the standard deviation factor by decrease factor
        }

        System.out.println("Best route is found.\n");

        System.out.println("Input Data Statistics: ");
        System.out.println("------------------------");
        System.out.println("Map divided into " + lastNumberOfAreas + "x" + lastNumberOfAreas + " areas.");
        System.out.println("Average density: " + ((int)(averageDensity * 100) / 100.0) + " city/area");
        System.out.println("Standard deviation: " + ((int)(standardDeviation * 100)) / 100.0);
        System.out.println("Lower limit: " + lowerLimit + " city/area");
        System.out.println("Number of city eliminated: " + numberOfCityEliminated + " (" + ((int)(eliminatePercentage * 100))/100.0+ "%)");
        System.out.println("\nRoute distance: " + minDistance);


        System.out.println("\n\nOptimizing the results...");
        System.out.println("Applying the 2-opt algorithm...");
        int opt2_Distance = Opt_2();
        System.out.println("\n2-opt optimized distance: " + opt2_Distance);


        //PRINTING THE RESULTS TO FILE
        try {
            file2 = new FileWriter(inputFile.getName().split("\\.")[0] + "-processed.txt");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        try {
            file = new FileWriter("data.txt"); //We write the x and y values of chosen cities to see the distribution in Excel
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        //Print the output as, distance of the route and the cities that we visited
        file2.write(minDistance + "\n");
        for(int i = 0; i < halfNumberOfCities; i++){
            file2.write(route[i] + "\n");
            file.write( cities.get(route[i])[0] + " " + cities.get(route[i])[1] + "\n");
        }
        file.close();
        file2.close();




        long finish = System.currentTimeMillis();
        long timeElapsed = finish - start;
        long totalSeconds = timeElapsed / 1000;
        long seconds = totalSeconds % 60;
        long totalMinutes = totalSeconds / 60;
        long minutes = totalMinutes % 60;
        long totalHours = totalMinutes / 60;

        System.out.print("\n\nRunning time: " + totalHours + " hours, " + minutes + " minutes, " + seconds + " seconds");
    }


    public static int findNearestNeighbor(int cityId){ //Return the nearest city's id

        int x = cities.get(cityId)[0];
        int y = cities.get(cityId)[1];

        //Find the city's area indices
        int xIndex = x / xGridSize;
        int yIndex = y / yGridSize;


        int startingI = xIndex;
        int endingI = xIndex;
        int startingJ = yIndex;
        int endingJ = yIndex;
        int nearestCityId = scanAreas(cityId, startingI, endingI, startingJ, endingJ);

        while(nearestCityId == -1){ //While we do not find a city

            int newIndices[] = widenIndices(1, startingI, endingI, startingJ, endingJ); //Widen the indices by 1
            startingI = newIndices[0];
            endingI = newIndices[1];
            startingJ = newIndices[2];
            endingJ = newIndices[3];

            nearestCityId = scanAreas(cityId, startingI, endingI, startingJ, endingJ);
        }

        double minDistance = findDistance(cityId, nearestCityId);

        //Find the min grid size
        int minGridSize;
        int maxGridSize;
        if(xGridSize < yGridSize) {
            minGridSize = xGridSize;
            maxGridSize = yGridSize;
        }
        else {
            minGridSize = yGridSize;
            maxGridSize = xGridSize;
        }

        int numberOfLevel = (int) Math.ceil((maxGridSize + minDistance) / ((double) minGridSize)); //This represents how many level we should go out

        int newIndices[] = widenIndices(numberOfLevel, startingI, endingI, startingJ, endingJ);
        startingI = newIndices[0];
        endingI = newIndices[1];
        startingJ = newIndices[2];
        endingJ = newIndices[3];

        int possibleNearestCityId = scanAreas(cityId, startingI, endingI, startingJ, endingJ);

        if(possibleNearestCityId == -1)
            return nearestCityId;
        else if(findDistance(cityId, possibleNearestCityId) < minDistance)
            return possibleNearestCityId;
        else
            return nearestCityId;


    }

    public static int scanAreas(int cityId, int startingI, int endingI, int startingJ, int endingJ){

        int x = cities.get(cityId)[0];
        int y = cities.get(cityId)[1];

        double minDistance = Double.MAX_VALUE;
        int nearestCityId = -1;

        for(int i = startingI; i <= endingI; i++){

            for(int j = startingJ; j <= endingJ; j++){

                int currentNumberOfCities = areas[i][j].size();

                for(int k = 0; k < currentNumberOfCities; k++){ //Find the nearest distance in that area

                    int currentCityId = areas[i][j].get(k);

                    if(currentCityId == cityId || isRemoved[currentCityId])
                        continue;

                    int x1 = cities.get(currentCityId)[0];
                    int y1 = cities.get(currentCityId)[1];

                    double currentDistance = (x1-x)*(x1-x) + (y1-y)*(y1-y); //We do not take the root for now to decrease the running time
                    if(currentDistance < minDistance) {
                        minDistance = currentDistance;
                        nearestCityId = currentCityId;
                    }
                }

            }

        }


        return nearestCityId;
    }

    public static int[] widenIndices(int level, int startingX, int endingX, int startingY, int endingY){

        for(int i = 0; i < level; i++){
            if(startingX > 0)
                startingX--;
            if(endingX < numberOfAreas - 1)
                endingX++;
            if(startingY > 0)
                startingY--;
            if(endingY < numberOfAreas - 1)
                endingY++;

        }

        int newIndices[] = {startingX, endingX, startingY, endingY};


        return newIndices;
    }

    public static int findDistance(int city1, int city2){

        int x = cities.get(city1)[0];
        int y = cities.get(city1)[1];

        int x1 = cities.get(city2)[0];
        int y1 = cities.get(city2)[1];

        return (int) Math.round(Math.sqrt((x1-x)*(x1-x) + (y1-y)*(y1-y)));
    }

    public static int findStartingCity(int startingI, int startingJ, int endingI, int endingJ){

        int iDistance = endingI - startingI + 1;
        int jDistance = endingJ - startingJ + 1;


        if(jDistance == 1 && iDistance == 1){

            return areas[startingI][startingJ].get(0); //Return the first city of the dentist area
        }

        int area[] = new int[4];

        area[0] = findNumberOfCities(startingI, startingJ, startingI + iDistance/2 -1, startingJ + jDistance/2 - 1);
        area[1] = findNumberOfCities(startingI, startingJ + jDistance/2, startingI + iDistance/2 - 1, startingJ + jDistance -1);
        area[2] = findNumberOfCities(startingI + iDistance/2, startingJ, startingI + iDistance -1, startingJ + jDistance/2 - 1);
        area[3] = findNumberOfCities(startingI + iDistance/2, startingJ + jDistance/2, endingI, endingJ);

        int choice = 0;
        int max = 0;
        for(int i = 0; i < 4; i++){
            if(area[i]> max){
                max = area[0];
                choice = i;
            }
        }

        switch (choice){
            case 0: return findStartingCity(startingI, startingJ, startingI + iDistance/2 -1, startingJ + jDistance - 1);
            case 1: return findStartingCity(startingI, startingJ + jDistance/2, startingI + iDistance/2 - 1, startingJ + jDistance -1);
            case 2: return findStartingCity(startingI + iDistance/2, startingJ, startingI + iDistance -1, startingJ + jDistance/2 - 1);
            default: return findStartingCity(startingI + iDistance/2, startingJ + jDistance/2, endingI, endingJ);
        }
    }


    public static int findNumberOfCities(int startingX, int startingY, int endingX, int endingY){

        int totalNumberOfCities = 0;
        for(int i = startingX; i <= endingX; i++){

            for(int j = startingY; j <= endingY; j++){
                totalNumberOfCities += areas[i][j].size();
            }

        }

        return totalNumberOfCities;
    }

    //Eliminate all the cities in the area
    public static void eliminateArea(int i, int j){

        int areaSize = areas[i][j].size();
        for(int a = 0; a < areaSize; a++) {
            isRemoved[areas[i][j].get(a)] = true;
        }

    }

    public static int[] swap_2_Points(int [] currentRoute, int i, int j){
        int[] newRoute = new int[currentRoute.length];

        for(int a = 0 ; a < i ; a++){ //Take the route from 0 to ith city
            newRoute[a] = currentRoute[a];
        }

        for(int b = j+1 ; b < currentRoute.length ; b++){ //Take the route from
            newRoute[b] = currentRoute[b];
        }

        int reverse = 0;
        for(int c = i ; c <= j ; c++){
            newRoute[c] = currentRoute[j-reverse];
            reverse++;
        }


        return newRoute;
    }


    public static int Opt_2() {
        int newDistance = 0;
        int numberOfSwaps = 1;
        int temp;
        int[] newRoute;

        while (numberOfSwaps != 0) {
            numberOfSwaps = 0;
            for (int i = 1; i < route.length - 2; i++) {
                for (int j = i + 1; j < route.length - 1; j++) {
                    if (findDistance(route[i], route[i - 1]) + findDistance(route[j + 1],
                            route[j]) >= findDistance(route[i], route[j + 1]) + findDistance(
                            route[i - 1], route[j])) {

                        newRoute = swap_2_Points(route,i,j);

                        for(int k  = 0; k < route.length-1 ; k++){
                            newDistance += findDistance(newRoute[k],newRoute[k+1]);
                        }
                       newDistance += findDistance(route[0], route[route.length -1]);
                        if (newDistance < minDistance) {
                            minDistance = newDistance;
                            route = newRoute;
                            numberOfSwaps++;
                            //System.out.println("*" + minDistance);

                        }
                    }
                    newDistance = 0;
                }
            }
        }
        return minDistance;
    }
}
