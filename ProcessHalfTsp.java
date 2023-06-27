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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class ProcessHalfTsp {

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
    private static int xAreaSize;
    private static int yAreaSize;
    private static int numberOfAreas;
    private static boolean isRemoved[];
    private static long minDistance = Long.MAX_VALUE;
    private static int[] route;


    public static void main(String[] args) throws Exception {

        long start = System.currentTimeMillis();
        Scanner input = new Scanner(System.in);


        cities = new ArrayList<>();
        //READING THE INPUT
        try {
            inputFile = new File("test-input-3.txt");

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
            System.out.println( inputFile.getName() + " couldn't opened!");
            System.exit(0);
        }




        numberOfCities = cities.size();  // Number of cities is found
        halfNumberOfCities = (int) Math.ceil(numberOfCities / 2.0); //We will visit only half of the given cities

        numberOfAreas = (int)Math.round(Math.sqrt(numberOfCities/2.0)); //Let's initialize number of areas per line by square root of n/2

        double standardDeviation = 0;
        int lowerLimit = 0;
        double averageDensity = 0;
        int numberOfCityEliminated = 0;
        double eliminatePercentage = 0;
        route = new int[halfNumberOfCities];
        int lastNumberOfAreas = numberOfAreas;
        int[] currentRoute = null;


        final double STANDARD_FACTOR = 0.01;
        double stdFactor = 2;
        int decreaseArea = (int) Math.ceil(numberOfAreas * 0.01);
        int counter = 0;
        System.out.print("Preprocessing data and applying the nearest neighbor algorithm... ");

        /* Here we apply the nearest neighbor algorithm. Data is chosen by eliminating some of them.
         * In the first place, we divide our map into areas. As a beginning, we set the number of columns and number of rows
         * as sqrt(n/2). Then we find sum of the number of cities for each area. Then we find the standard deviation and eliminate the
         * average + standardFactor * standardDeviation, for example: average + 1.5standardDeviation
         * We try to find the best standardFactor and best division of areas for the given map.
         */
        while(stdFactor >= 1) { //Loop that controls the standard deviation factor

            numberOfAreas = (int)Math.round(Math.sqrt(numberOfCities/2.0));
            int processed = 0;
            int total = numberOfAreas;
            double progress = 0;

            do {

                //Remove the cities as they visited, store that information into an array
                isRemoved = new boolean[numberOfCities];
                for (int i = 0; i < numberOfCities; i++)
                    isRemoved[i] = false;

                //We divide the map into areas. We find the length and width of those areas
                xAreaSize = (maxX - minX) / numberOfAreas + 1;
                yAreaSize = (maxY - minY) / numberOfAreas + 1;
                areas = new ArrayList[numberOfAreas][numberOfAreas]; //Areas variable is 2D matrix. Those will represent the areas.


                //Initialize the 2D array
                for (int i = 0; i < numberOfAreas; i++) {
                    for (int j = 0; j < numberOfAreas; j++)
                        areas[i][j] = new ArrayList<>();
                }


                //Add the cities to appropriate areas
                for (int i = 0; i < numberOfCities; i++) {
                    int xCoordinate = cities.get(i)[0];
                    int yCoordinate = cities.get(i)[1];

                    int xIndex = xCoordinate / xAreaSize;
                    int yIndex = yCoordinate / yAreaSize;

                    areas[xIndex][yIndex].add(i);
                }


                //Calculate the standard deviation of the areas
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

                int currentLowerLimit = (int) Math.floor(currentAverageDensity - stdFactor * currentStandardDeviation); //Find the lower limit that each area must have
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


                //NEAREST NEIGHBOR ALGORITHM
                currentRoute = new int[halfNumberOfCities];


                //Choosing the first city is very important. We have a method to find the best starting city
                int startingCity = findStartingCity(0, 0, numberOfAreas - 1, numberOfAreas - 1);
                isRemoved[startingCity] = true;
                currentRoute[0] = startingCity;


                long currentDistance = 0;

                for (int i = 1; i < halfNumberOfCities; i++) {
                    int newCity = findNearestNeighbor(startingCity);
                    isRemoved[newCity] = true; //Eliminate the city that we found
                    currentDistance += findDistance(startingCity, newCity);
                    startingCity = newCity;
                    currentRoute[i] = newCity; //Add the found city into the route array
                }

                currentDistance += findDistance(currentRoute[0], currentRoute[halfNumberOfCities - 1]); //Add the distance between last and first cities that we visited


                if (currentDistance < minDistance) { //If we find a better solution, we update it
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


            } while (numberOfAreas > 0); //Try all the possibilities for dividing the map

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



        //PRINTING THE RESULTS TO FILE
        try {
            file2 = new FileWriter(inputFile.getName().split("\\.")[0] + "-processed-std_factor-" + ((int)(STANDARD_FACTOR * 100))/100.0 + ".txt");
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

        //Determine which area the city belongs to
        int xIndex = x / xAreaSize;
        int yIndex = y / yAreaSize;


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

        double minDistance = findDistance(cityId, nearestCityId); //Find the distance between found city and given city

        /*Find the min grid size. We will search other nearest neighbor possibilities in particular areas.
          We do not have to check all areas. This will save time.
         */
        int minGridSize;
        int maxGridSize;
        if(xAreaSize < yAreaSize) {
            minGridSize = xAreaSize;
            maxGridSize = yAreaSize;
        }
        else {
            minGridSize = yAreaSize;
            maxGridSize = xAreaSize;
        }

        int numberOfLevel = (int) Math.ceil((maxGridSize + minDistance) / ((double) minGridSize)); //This represents how many farther areas we should look for

        int newIndices[] = widenIndices(numberOfLevel, startingI, endingI, startingJ, endingJ);
        startingI = newIndices[0];
        endingI = newIndices[1];
        startingJ = newIndices[2];
        endingJ = newIndices[3];

        int possibleNearestCityId = scanAreas(cityId, startingI, endingI, startingJ, endingJ);

        if(possibleNearestCityId == -1) //Return the city id that we found first
            return nearestCityId;
        else if(findDistance(cityId, possibleNearestCityId) < minDistance) //Return the city id that we found second (which is nearer than the first)
            return possibleNearestCityId;
        else
            return nearestCityId;

    }

    //Scan all the areas that is limited by coordinates to find the nearest city
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

    //We widen our limit coordinates to search for more areas
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

    //Find the distance between two cities
    public static long findDistance(int city1, int city2){

        long x = cities.get(city1)[0];
        long y = cities.get(city1)[1];

        long x1 = cities.get(city2)[0];
        long y1 = cities.get(city2)[1];

        long result = (long) Math.round(Math.sqrt((x1-x)*(x1-x) + (y1-y)*(y1-y)));

        return result;
    }

    /*Find the starting city. We divide our map into 4 squares. Than we count sum of number of cities for each.
      Then we call the function again to find the most dense area and best city recursively.
     */
    public static int findStartingCity(int startingI, int startingJ, int endingI, int endingJ){

        int iDistance = endingI - startingI + 1;
        int jDistance = endingJ - startingJ + 1;


        if(jDistance == 1 && iDistance == 1)
            return areas[startingI][startingJ].get(0); //Return the first city of the dentist area


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

    //Find number of cities in a limited coordinates
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


    public static boolean checkDuplicate(int[] array){

        int arr[] = array.clone();

        Arrays.sort(arr);
        for(int i = 0; i < arr.length - 1; i++){
            if(arr[i] == arr[i+1])
                return true;
        }

        return false;


    }

    public static long findTotalDistance(int[] newRoute){

        long newDistance = 0;
        newDistance += findDistance(newRoute[0], newRoute[newRoute.length -1]);
        for(int a  = 0; a < (newRoute.length-1) ; a++){
            long c = findDistance(newRoute[a],newRoute[a+1]);
            newDistance += c;
        }
        newDistance += findDistance(newRoute[0], newRoute[newRoute.length -1]);

        return newDistance;
    }

}
