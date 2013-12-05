/*
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
    static ArrayList<Vertex> priorityQueue = new ArrayList<Vertex>();
    static Vertex[] parents;
    static boolean[] visited;
    static ArrayList<Vertex> tourVertices = new ArrayList<Vertex>();
    static double mstWeight = 0.0;
    static double mstDistance = 0.0;
    
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
	
	if ((n < 1) || (n > 13)) {
	    System.out.println("Number of vertices must be between 1 and 13");
	    System.exit(0);
	}
	long startTime = System.currentTimeMillis();

	// create graph, vertices, adjacency matrix, and paths
	g = new Graph(n, seed);
	
	// generate graph edges
	double[][] adjMatrix = g.getAdjacencyMatrix();
	
	// set up array to hold parents
	parents = new Vertex[n];
	parents[0] = new Vertex(0, 0);
	parents[0].setDist(-1);
	
	// Initialize the priority queue to contain every vertex with equal priority, infinity
	for (int i = 0; i < n; i++) {
	    priorityQueue.add(g.getVertex(i));
	    priorityQueue.get(i).setDist(Double.MAX_VALUE);
	    priorityQueue.get(i).setNumber(i);
	}
    
	// then a single vertex is chosen and its priority is changed to 0
	priorityQueue.get(0).setDist(0);
	// Remember, we want low key values to represent high priorities,
	// so we should use a min heap instead of a max heap
	
	// while PQ is not empty
	while (priorityQueue.size() > 0) {
	        
	    // u = deleteMin(PQ)
	    Vertex u = deleteMin(priorityQueue);
	        
	    // for each v adjacent to u, do
	    for (int i = 0; i < priorityQueue.size(); i++) {
		if (priorityQueue.get(i) != u) {
		    Vertex v = priorityQueue.get(i);
		        
		    // if (v is in PQ and weight (u, v) < PQ[v].getPriority())
		    //parent[v] = u
		    //PQ[v].setPriority(weight(u, v))
		    
		    if (g.getDistance(u, v) < v.getDist()) {
			parents[v.getNumber()] = u;
			priorityQueue.get(i).setDist(g.getDistance(u, v));
		    }
		}
	    }
	}
	
	// use information on parents to construct the minimum spanning tree
	// check each branch to see whether it is visited
	visited = new boolean[n];
	
	// always start tour with vertex 0 
	tourVertices.add(g.getVertex(0));
	constructMST(0);
	
	// always end tour with vertex 0 
	tourVertices.add(g.getVertex(0));
	
	// Begin output
	if (n <= 10) {
	    // Print vertices
	    System.out.println(g.getVerticesString());
	    
	    // Print adjacency matrix
	    System.out.println("Adjacency matrix of graph weights:\n\n" + g.getMatrixString() + "\n");
	    
	    // Print greedy graph
	    System.out.println("Minimum Spanning Tree:\nAdjacency matrix of graph weights:\n");
	        
	    for (int i = 0; i < adjMatrix.length; i++) {
		System.out.print("      " + i);
	    } 
	        
	    for (int i = 0; i < adjMatrix.length; i++) {
		System.out.print("\n\n" + i + "   ");
		for (int j = 0; j < adjMatrix.length; j++) {
		    if (parents[i].getNumber() == j || parents[j].getNumber() == i) {
			System.out.print(df.format(adjMatrix[i][j]) + "   ");
			mstWeight += adjMatrix[i][j];
		    }
		    else {
			System.out.print("0.00   ");
		    }
		}
	    }
	        
	    // divide mstDistance by 2 to get correct result because it counts elements twice
	    System.out.println("\n\nTotal weight of mst: " + df.format(mstWeight/2) + "\n");
	        
	    System.out.println("Pre-order traversal:\n");
	    // use size() - 1 because tour goes to 0 again at end and we don't want to print it
	    for (int i = 0; i < tourVertices.size() - 1; i++) {
		System.out.println("Parent of " + tourVertices.get(i).getNumber() + " is " + parents[i].getNumber());
	    }
	}
	
	for (int i = 1; i < tourVertices.size(); i++) {
	    mstDistance += g.getDistance(tourVertices.get(i), tourVertices.get(i-1));
	}
	
	System.out.print("\nDistance using mst: " + df.format(mstDistance) + " for path ");
	for (int i = 0; i < tourVertices.size(); i++) {
	    System.out.print(tourVertices.get(i).getNumber() + " ");
	}
	
	long endTime = System.currentTimeMillis();
	System.out.println("\nRuntime for Mst TSP   : " + (endTime - startTime) + " milliseconds");
    }
    
    private static Vertex deleteMin(ArrayList<Vertex> priorityQueue) {
	double maximumDist = Double.MAX_VALUE;
	int minimumIndex = 0;
	
	for (int i = 0; i < priorityQueue.size(); i++) {
	    if (priorityQueue.get(i).getDist() < maximumDist) {
		minimumIndex = i;
		maximumDist = priorityQueue.get(i).getDist();
	    }
	}
	
	Vertex minimum = priorityQueue.get(minimumIndex);
	priorityQueue.remove(minimumIndex);
	return minimum;
    }
    
    public static void constructMST(int node)
    {
	for (int i = 0; i < parents.length; i++) {
	    if (node == parents[i].getNumber() && visited[i] == false) {
		tourVertices.add(g.getVertex(i));
		constructMST(i);
	    }
	}
	visited[node] = true;
    }
}