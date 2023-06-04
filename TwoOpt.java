import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Scanner;

public class TwoOpt {

    private static File inputFile;
    private static File inputFile2;
    private static ArrayList<int []> cities;
    private static int[] route;
    private static int minDistance;



    public static void main(String args[]){


        cities = new ArrayList<>();

        try {
            inputFile = new File("example-input-3.txt");

            Scanner scanner = new Scanner(inputFile);

            while (scanner.hasNext()) {

                scanner.nextInt(); //pass the city id
                int xCoordinate = scanner.nextInt();
                int yCoordinate = scanner.nextInt();

                int[] city = {xCoordinate, yCoordinate};
                cities.add(city);
            }

        }
            catch (Exception e) {
                System.out.println( inputFile.getName() + " couldn't opened");
                System.exit(0);
            }

        int numberOfCities = cities.size();  // Number of cities is found
        int halfNumberOfCities = (int) Math.ceil(numberOfCities / 2.0);

        route = new int[halfNumberOfCities];



        try {
            inputFile2 = new File("example-input-3-processed.txt");

            Scanner scanner2 = new Scanner(inputFile2);

            minDistance = scanner2.nextInt();

            int i = 0;
            while (scanner2.hasNext()) {
                route[i] = scanner2.nextInt();
                i++;
            }

            scanner2.close();
        }
        catch (Exception e) {
            System.out.println(inputFile2.getName() + " couldn't opened!");
            System.exit(0);
        }







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








    public static int[] swap_2_Points(int [] currentRoute, int i, int j){
        int[] newRoute = new int[currentRoute.length];

        for(int a = 0 ; a < i ; a++){
            newRoute[a] = currentRoute[a];
        }

        for(int b = j+1 ; b < currentRoute.length ; b++){
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
        int[] newRoute = new int[currentRoute.length];

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

        for(int a = k+1 ; a<currentRoute.length ; a++ ){
            newRoute[a] = currentRoute[a];
        }

        return newRoute;
    }

}
