# half-travelling-salesman-problem
This is a group project of 3 people. Half travelling salesman problem is solved.

Travelling salesman problem is an NP-hard problem. It is not possible to get an optimal solution in a short time for large inputs. Therefore, we try to find the solution as much as closer to the optimal solution by heuristic algorithms. In this homework we deal with half-TSP problem which means only half of the cities will be visited. To get a good approximation, we used nearest neighbor algorithm to find the initial route and then 2-opt and 3-opt algorithms to optimize the initial route found by nearest neighbor algorithm.

## Usage

1. Processing: Open "ProcessHalfTsp" java source code. In the beginning of the main function, enter your input file name. Compile and run. You will have an processed output.


2. Optimzing: Choose one of the optimizing code below. (Suggestion: Use ThreeOptRandom for large inputs)

	a) Open "TwoOpt" java source code. In the first file reading, enter your input file; in the second file reading enter the processed file.(You may set a timer from the global variable "timer" in minutes.) Compile and run.

	b) Open "ThreeOpt" java source code. In the first file reading, enter your input file; in the second file reading enter the processed file. (You may set a timer from the global variable "timer" in minutes.) Compile and run.

	c) Open "ThreeOptRandom" java source code. In the first file reading, enter your input file; in the second file reading enter the processed file. Set the timer from the global variable "timer" in minutes. Compile and run.

# How it works?

## Processing

We begin by reading the input. The input will be stored in ArrayList of int array. Int array will be size of 2 to store the x and values. Then we find the city number and half city number. 
After reading the input, we divide our map (input) into areas. Initially there will be √𝑛2 ×√𝑛2 areas which means all the cities are distributed normally. Areas will be kept in a 2D matrix of Integer ArrayList. Each element will represent an area. Putting the cities in areas is such an easy task. We first find the area’s length and width. Then we divide those values by x and y values of cities. However, we would like to remark that our area indices start from bottom of the matrix, this means areas[0][0] will represent the bottom-left of the matrix. This is because of positive x and y coordinates’ origin location.

![Picture1](https://github.com/ItsYusufDemir/half-travelling-salesman-problem/assets/104091838/b4c90cb2-e855-4efc-91e1-9a52957f0bcd)

After dividing the map, we find each area’s total number of cities. Then, we find the average number of cities and standard deviation. In statistics, the data that 3 standard deviations far away from the average is considered as outliers. In a normally distributed population, that means approximately 0.3% of the population. However, we need more elimination in our data since we only use the 50% of the input. Therefore, we initially eliminate the 𝑎𝑣𝑒𝑟𝑎𝑔𝑒−2×𝑠𝑡𝑎𝑛𝑑𝑎𝑟𝑑 𝑑𝑒𝑣𝑖𝑎𝑡𝑖𝑜𝑛 of the data. Next, we use a nearest neighbor algorithm. Then we decrease 2 by 0.01 in each iteration because we do not know 2 will give us the best route. We decrease it until it becomes 1 because eliminating one standard deviation from the average will eliminate most of the cities therefore, we do not decrease more. But of course, those assumptions are for normally distributed data. If we eliminate more than 50% of the data mistakenly, we skip that iteration.


Another problem is that initial number of areas might not be suitable for given data. Therefore, we have another inner loop that decreases the number of aeras by 1. (If we do not have enough time, we might decrease by larger numbers) At the end, we find the route with minimum distance thanks to nearest neighbor algorithm. But, before we start the nearest neighbor algorithm, we need to find the starting city. Starting city is very important, we must select the city from the densest area. To do that, we divide the map into 4 squares, Then, we select the square with maximum total number of cities. We do it recursively and find the best city.

![Picture2](https://github.com/ItsYusufDemir/half-travelling-salesman-problem/assets/104091838/47e6677c-bbce-4599-ba2f-a37e82b0d79b)

Nearest neighbor algorithm is the easiest way to solve the problem with good enough result. As we stated above, we will also do an optimization after finding a route by nearest neighbor algorithm. Now, let’s talk about this algorithm. We will take advantage of dividing the map into areas. In this first place, we try to find the nearest neighbor in the same area of given city. If we do not find it, then we go one level outer, we search for the nearest neighbor in those areas. If we do not find it, then we go one level outer again and again. If we find it in a level, we find the distance and then we also search for the areas that are far away by that distance from the given city.

![Picture3](https://github.com/ItsYusufDemir/half-travelling-salesman-problem/assets/104091838/707c3988-6057-4fab-ba68-b9112d7f889f)

Now, let's see the results,

![result](https://github.com/ItsYusufDemir/half-travelling-salesman-problem/assets/104091838/b21e8087-3973-48f9-9175-2bb9433cbca7)

After processing, we apply 2-opt and 3-opt for optimization.




