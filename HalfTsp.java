import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class HalfTsp {

    static int numberOfCities = 0;
    static int halfNumberOfCities;
    static int maxX = 0;
    static int minX = 0;
    static int maxY = 0;
    static int minY = 0;
    private static FileWriter file;
    private static FileWriter file2;
    private static ArrayList<Integer>[][] areas;
    private static ArrayList<int []> cities;
    private static int xGridSize;
    private static int yGridSize;
    private static int numberOfAreas;
    private static boolean isRemoved[];

    public static void main(String[] args) throws Exception {

        long start = System.currentTimeMillis();
        Scanner input = new Scanner(System.in);


        cities = new ArrayList<>();

        try {
            File inputFile = new File("example-input-3.txt");

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


        numberOfCities = cities.size();
        halfNumberOfCities = (int) Math.ceil(numberOfCities / 2.0);


        numberOfAreas = (int)Math.round(Math.sqrt(numberOfCities/2)); //Let's initialize number of areas per line by square root of n/2


        double standardDeviation = 0;
        int lowerLimit = 0;
        double averageDensity = 0;
        int numberOfCityEliminated = 0;
        int minDistance = Integer.MAX_VALUE;
        double eliminatePercentage = 0;
        int route[] = new int[halfNumberOfCities];
        int lastNumberOfAreas = numberOfAreas;


        //You should add here a for loop and change the lower limit by constant factor: 1.5std, 1.25std, etc
        do {

            //Remove the cities as they visited, store that information into an array
            isRemoved = new boolean[numberOfCities];
            for(int i = 0; i < numberOfCities; i++)
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

            int currentLowerLimit = (int) Math.floor(currentAverageDensity - 1.5 * currentStandardDeviation);
            int currentNumberOfCityEliminated = 0;


            //Eliminate the cities below the limit
            for (int i = 0; i < numberOfAreas; i++) {

                for (int j = 0; j < numberOfAreas; j++) {

                    if (areas[i][j].size() <= currentLowerLimit) {
                        eliminateArea(i,j);
                        currentNumberOfCityEliminated += areas[i][j].size();
                    }
                }

            }

            double currentEliminatePercentage = ((double)currentNumberOfCityEliminated/numberOfCities)*100;


            if(currentEliminatePercentage >= 50) {
                numberOfAreas--;
                continue;
            }


            // Applying the nearest neighbor algorithm
            int currentRoute[] = new int[halfNumberOfCities];


            //Choosing the first city and adding it into the route
            int startingCity = findStartingCity(0,0, numberOfAreas -1, numberOfAreas -1);
            isRemoved[startingCity] = true;
            currentRoute[0] = startingCity;


            int currentDistance = 0;

            for(int i = 1; i < halfNumberOfCities; i++){

                int newCity = findNearestNeighbor(startingCity);
                isRemoved[newCity] = true;
                currentDistance += findDistance(startingCity, newCity);
                startingCity = newCity;
                currentRoute[i] = newCity;
            }

            currentDistance += findDistance(currentRoute[0], currentRoute[halfNumberOfCities -1]); //Going back to where we started


            if(currentDistance < minDistance) {
                standardDeviation = currentStandardDeviation;
                lowerLimit = currentLowerLimit;
                averageDensity = currentAverageDensity;
                numberOfCityEliminated = currentNumberOfCityEliminated;
                eliminatePercentage = currentEliminatePercentage;
                minDistance = currentDistance;
                lastNumberOfAreas = numberOfAreas;

                route = currentRoute;
            }



            file = new FileWriter("data.txt");


            for(int i = 0; i < halfNumberOfCities; i++){
                file.write(cities.get(route[i])[0] + " " + cities.get(route[i])[1] + "\n");
            }
            file.close();



            numberOfAreas--;

        } while(numberOfAreas > 0);



        System.out.println("Input Data Statistics: ");
        System.out.println("------------------------");
        System.out.println("Map divided into " + lastNumberOfAreas + "x" + lastNumberOfAreas + " areas.");
        System.out.println("Average density: " + ((int)(averageDensity * 100) / 100.0));
        System.out.println("Standard deviation: " + ((int)(standardDeviation * 100)) / 100.0);
        System.out.println("Lower limit: " + lowerLimit);
        System.out.println("Number of city eliminated: " + numberOfCityEliminated + " (" + eliminatePercentage + "%)");
        System.out.println("\nRoute distance: " + minDistance);



        try {
            file = new FileWriter("route.txt");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }





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
        int y = cities.get(city2)[1];

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




}
