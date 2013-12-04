/*
 * Robb Dooling
 * robbdooling@gmail.com
 * http://www.cs.rit.edu/~rlc/Courses/Algorithms/Projects/20131/Proj1/proj1.html
 */

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.*;

public class OptimalTSP {
    
    // n = number of nodes
    static int n = 0;
    static ArrayList<Integer> currentPath = new ArrayList<Integer>();
    static ArrayList<ArrayList<Integer>> paths = new ArrayList<ArrayList<Integer>>();
	
    static ArrayList<Double> pathDistances = new ArrayList<Double>();
    static double distance;
	    
	    
    static DecimalFormat df = new DecimalFormat("0.00");
    
    public static void main (String[] args) {
	
	long seed = 0;
	
	if (args.length != 2) {
	    System.out.println("Usage: java OptimalTSP n seed");
	    System.exit(0);
	}
	
	try {
	    n = Integer.parseInt(args[0]);
	    seed = Long.parseLong(args[1]);
	}
	catch (NumberFormatException e)
	{
	    System.out.println("Command line args must be integers");
	    System.exit(0);
	}
	
	if ((n < 1) || (n > 13)) {
	    System.out.println("Number of vertices must be between 1 and 13");
	    System.exit(0);
	}

	// create graph, vertices, adjacency matrix, and paths
	
	long startTime = System.currentTimeMillis();
		
	Graph g = new Graph(n, seed);
	
	// generate first path
        for (int i = 1; i < n; i++) {
            currentPath.add(i);
        }
        
        ArrayList<Integer> newPath = new ArrayList<Integer>(currentPath);
        paths.add(newPath);
        
        // generate the rest of the paths (permutations)
        while (generateNextPath(currentPath) == false) {
            newPath = new ArrayList<Integer>(currentPath);
            paths.add(newPath);
        }
        
        // add a 0 at the beginning AND end of each path
        for (int i = 0; i < paths.size(); i++) {
            ArrayList<Integer> pathToModify = paths.get(i);
            pathToModify.add(0,0);
            pathToModify.add(0);
            paths.set(i, pathToModify);
        }
            
        pathDistances = generatePathDistances(paths, g.getAdjacencyMatrix());
	
	// Begin output
	if (n <= 10) {
	    // Print vertices
	    System.out.println(g.getVerticesString());
	
	    // Print adjacency matrix
	    System.out.println(g.getMatrixString() + "\n");
	}
	
	if (n <= 5) {
	    // Print possible paths
	    System.out.println(getPathsString());
	}
	
	long endTime = System.currentTimeMillis();
	
	// Print optimal path
	System.out.println(getOptimalString());
	
	System.out.println("Runtime for optimal TSP   : " + (endTime - startTime) + " milliseconds");
    }
    
    public static ArrayList<Double> generatePathDistances(ArrayList<ArrayList<Integer>> paths, double[][] adjacencyMatrix) {
        ArrayList<Double> pathDistances = new ArrayList<Double>();
        double currentDistance = 0.0;
        String roundedDistance = "";
        for (int i = 0; i < paths.size(); i++) {
            for (int j = 0; j < paths.get(i).size() - 1; j++) {
                int first = paths.get(i).get(j);
                int second = paths.get(i).get(j+1);
                currentDistance += adjacencyMatrix[first][second];
            }
            roundedDistance = String.format("%.4g%n", currentDistance);
            currentDistance = Double.parseDouble(roundedDistance);
            pathDistances.add(currentDistance);
            currentDistance = 0.0;
        }
        return pathDistances;
    }
    
     public static Boolean generateNextPath(ArrayList<Integer> path) {
        
        Boolean isDescending = true;
        int toBeSwapped = 0;
        int swapWithMe = path.size();
        int swapWithMeIndex = 0;
        
        // scan backwards
        for (int i = path.size() - 1; i > 0; i--) {
            // if an element is less than a digit after it
            if (path.get(i-1) < path.get(i)) {
                
                // swap that one with the smallest digit to the right that is greater than that one
                toBeSwapped = path.get(i-1);
                swapWithMe = n;
                for (int j = i-1; j < path.size(); j++) {
                    if ((toBeSwapped < path.get(j)) && (swapWithMe > path.get(j))) {
                        swapWithMe = path.get(j);
                        swapWithMeIndex = j;
                    }
                }
                
                // perform the swap
                swap(path, (i-1), swapWithMeIndex);
                
                // put all the remaining digits to the right of that one (before it was swapped) in increasing order
                // create a new arrayList to hold this suffix
                ArrayList<Integer> suffix = new ArrayList<Integer>();
                while (i < path.size()) {
                    int newElement = path.get(i);
                    path.remove(i);
                    suffix.add(newElement);
                }
                
                // sort the suffix and then add it
                Collections.sort(suffix);
                path.addAll(suffix);
                
                isDescending = false;
                break;
            }
        }
        
        return isDescending;
    }
    
    public static ArrayList<Integer> swap(ArrayList<Integer> arrayList, int i, int j) {
        int temp = arrayList.get(i);
        arrayList.set(i, arrayList.get(j));
        arrayList.set(j, temp);
        return arrayList;
    }
    
    public static String getPathsString() {
        String pathsString = "";
        double distance = 0.0;
        
        for (int i = 0; i < paths.size(); i++) {
            pathsString += "Path: ";
            // add entire path
            for (int j = 0; j < paths.get(i).size(); j++) {
                pathsString += paths.get(i).get(j) + " ";
            }
            
            // add distance to end of string
            pathsString += " distance = " + df.format(pathDistances.get(i)) + "\n";
        }
        return pathsString;
    }
    
    public static String getOptimalString() {
        String optimalString = "";
        double optimalDistance = Double.MAX_VALUE;
        int optimalIndex = 0;
        
        for (int i = 0; i < pathDistances.size(); i++)
        {
            if (pathDistances.get(i) < optimalDistance) {
                optimalDistance = pathDistances.get(i);
                optimalIndex = i;
            }
        }

        ArrayList<Integer> optimalPath = paths.get(optimalIndex);
        
        optimalString = "Optimal distance: " + df.format(optimalDistance) + " for path";
        for (int i = 0; i < optimalPath.size(); i++) {
            optimalString += " " + optimalPath.get(i);
        }
        return optimalString;
    }
    
}