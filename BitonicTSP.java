/*
 * BitonicTSP - Solve the Traveling Sealesman problem using a bitonic tour
 *
 * Robb Dooling
 * robbdooling@gmail.com
 * http://www.cs.rit.edu/~rlc/Courses/Algorithms/Projects/20131/Proj4/proj4.html
 */

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.text.*;

public class BitonicTSP {
    
    static Graph g;
    static double bitonicDistance = 0.0;
    static ArrayList<Vertex> unsortedVertices;
    static ArrayList<Vertex> sortedVertices;
    
    static double[][] adjMatrix;
    
    static double[][] lTable;
    static int[][] nTable;
    
    static ArrayList<Integer> bitonicTour;
    
    public static void main (String[] args) {
	
	int n = 0;
	long seed = 0;
	DecimalFormat df = new DecimalFormat("0.00");
	
	if (args.length != 2) {
	    System.out.println("Usage: java BitonicTSP n seed");
	    System.exit(0);
	}
	
	try {
	    n = Integer.parseInt(args[0]);
	    seed = Long.parseLong(args[1]);
	}
	catch (NumberFormatException e) {
	    System.out.println("Command line args must be integers");
	    System.exit(0);
	}
	
	if ((n < 1) || (n > 1013)) {
	    System.out.println("Number of vertices must be between 1 and 13");
	    System.exit(0);
	}
	
	// create graph, vertices, adjacency matrix, and paths
	g = new Graph(n, seed);
	
	// generate graph edges
	adjMatrix = g.getAdjacencyMatrix();
	
	// initialize lTable and nTable
	lTable = new double[n][n];
	nTable = new int[n][n];
	for (int i = 0; i < n; i++) {
	    for (int j = 0; j < n; j++) {
		lTable[i][j] = 0.00;
		nTable[i][j] = -1;
	    }
	}
	
	long startTime = System.currentTimeMillis();
		
	// sort vertices
	unsortedVertices = g.getVertices();
	sortedVertices = g.sortVertices(unsortedVertices);
	
	// fill L-Table and N-Table
	fillTables(sortedVertices, n);
	
	// Construct Bitonic tour
	bitonicTour = findBitonicTour(n);
		
	long endTime = System.currentTimeMillis();
	
	// Begin output
	if (n <= 10) {
	    // Print vertices
	    System.out.println("X-Y Coordinates: \n" + g.getVerticesString(unsortedVertices));
	    
	    // Print adjacency matrix
	    System.out.println("Adjacency matrix of graph weights:\n\n" + g.getMatrixString() + "\n");
	    
	    // Print sorted vertices
	    System.out.println("Sorted X-Y Coordinates: \n" + g.getVerticesString(sortedVertices));
	    
	    // Print L-Table
	    System.out.print("L-Table:");
	    for (int i = 0; i < lTable.length; i++) {
		System.out.print("\n ");
		for (int j = 0; j < lTable[i].length; j++) {
		    System.out.print(df.format(lTable[i][j]) + "  ");
		}
	    }
	    
	    // Print N-Table
	    System.out.print("\n\nN-Table:");
	    for (int i = 0; i < nTable.length; i++) {
		System.out.print("\n");
		for (int j = 0; j < nTable[i].length; j++) {
		    // if negative and first column, just print
		    // if negative anywhere else OR positive and first column,
		    // print with one extra space before
		    // otherwise, print with two extra spaces before
		    
		    if (nTable[i][j] < 0 && j == 0) {
			System.out.print(nTable[i][j]);
		    }
		    else if (nTable[i][j] < 0 || j == 0) {
			System.out.print(" " + nTable[i][j]);
		    }
		    else {
			System.out.print("  " + nTable[i][j]);
		    }
		    
		}
	    }
	}
	
	for (int i = 1; i < bitonicTour.size(); i++) {
	    bitonicDistance += adjMatrix[bitonicTour.get(i)][bitonicTour.get(i-1)];
	}
	
	// print bitonic tour
	System.out.print("\n\nDistance using bitonic: " + df.format(bitonicDistance) + " for path ");
	for (int i = 0; i < bitonicTour.size(); i++) {
	    System.out.print(bitonicTour.get(i) + " ");
	}
	
	System.out.println("\nRuntime for Bitonic TSP   : " + (endTime - startTime) + " milliseconds");
    }
    
    static void fillTables(ArrayList<Vertex> vertices, int n) {
	
	for (int j = 1; j < n; j++) {
	    for (int i = 0; i < j; i++) {
		if ((i == 0) && (j == 1)) {
		    lTable[i][j] = g.getDist(vertices.get(i), vertices.get(j));
		    nTable[i][j] = i;
		}
		else if (j > i + 1) {

		    lTable[i][j] = lTable[i][j-1] + g.getDist(vertices.get(j-1), vertices.get(j));
		    nTable[i][j] = j - 1;
		}
		else {
		    lTable[i][j] = Double.MAX_VALUE;
		    for (int k = 0; k < i; k++) {
			double q = lTable[k][i] + g.getDist(vertices.get(k), vertices.get(j));
			if (q < lTable[i][j]) {
			    lTable[i][j] = q;
			    nTable[i][j] = k;
			}
		    }
		}
	    }
	}
    }
    
    static ArrayList<Integer> findBitonicTour(int n) {
	
	ArrayList<Integer> bitonic1 = new ArrayList<Integer>();
	ArrayList<Integer> bitonic2 = new ArrayList<Integer>();
	bitonic1.add(0);
	bitonic2.add(0);
	
	int zeroIndex = 0;
	// find index of vertex 0 in sorted vertices
	for (int i = 0; i < n; i++) {
	    if (sortedVertices.get(i).getNumber() == 0) {
		zeroIndex = i;
		break;
	    }
	}
	
	// add number left of 0 to tour so far
	if (zeroIndex > 0) {
	    bitonic2.add(sortedVertices.get(zeroIndex-1).getNumber());
	} else {
	    bitonic2.add(sortedVertices.get(n-1).getNumber());
	}

	boolean LtoR = true;
	
	int current = 0;
	int next = nTable[n-2][n-1];
	
	int j = n;
	// 3 nodes already decided (0 at beginning, 0 at end, second to last)
	while (j > 3) {
	    
	    if (sortedVertices.get(current).x() < sortedVertices.get(next).x()) {
		LtoR = true;
	    } else {
		LtoR = false;
	    }
	    
	    if (LtoR) {
		bitonic1.add(next);
	    } else {
		bitonic2.add(next);
	    }
	    
	    current = next;
	    
	    // search that column for smallest value greater than 0
	    double candidateValue = Double.MAX_VALUE;
	    for (int k = 0; k < n; k++) {
		if (lTable[k][current] > 0.00 && lTable[k][current] < candidateValue) {
		    candidateValue = lTable[k][current];
		    next = nTable[k][current];
		}
	    }
	    	    
	    j--;
	}
	
	// add remaining vertex
	for (int k = 1; k < n; k++) {
	    if ((bitonic1.contains(k) == false) && (bitonic2.contains(k) == false)) {
		bitonic1.add(k);
		break;
	    }
	}
	
	// add other half of path
	for (int k = bitonic2.size() - 1; k > -1; k--)
	{
	    bitonic1.add(bitonic2.get(k));
	}
	
	return bitonic1;
    }
}