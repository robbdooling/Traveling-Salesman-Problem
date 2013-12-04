/*
 * Robb Dooling
 * robbdooling@gmail.com
 * http://www.cs.rit.edu/~rlc/Courses/Algorithms/Projects/20131/Proj1/proj1.html
 * http://www.cs.rit.edu/~rlc/Courses/Algorithms/Projects/20131/Proj2/proj2.html
 */

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.text.*;

public class Graph {
    
    // n = number of nodes
    int n; 
    private ArrayList<Vertex> vertices;
    private double[][] adjacencyMatrix;
    private DecimalFormat df = new DecimalFormat("0.00");
        
    /* Constructor takes 2 parameters:
        an int representing the number of nodes
        a long to use for the random number generator
    */
    public Graph(int _n, long seed) {
        
        // intialize number of nodes in graph
        n = _n;
        
        // initialize data structures 
        vertices = new ArrayList<Vertex>();
        adjacencyMatrix = new double[n][n];
        
        // use seed to create random number generator
        Random xGenerator = new Random(seed);
        Random yGenerator = new Random(2 * seed);        
        
        // generate unique coordinates for vertices
        for (int i = 0; i < n; i++) {
            Vertex v = generateUniqueCoordinate(xGenerator, yGenerator);
            vertices.add(v);
        }
        
        // generate adjacency matrix based on the vertices
        for (int i = 0; i < n; i++)
        {
            for (int j = 0; j < n; j++)
            {
                adjacencyMatrix[i][j] = getDistance(vertices.get(i), vertices.get(j));
            }
        }
	
    }
            
    public Boolean isInDescendingOrder(ArrayList<Integer> path) {
        boolean isDescending = true;
        for (int i = 0; i < path.size() - 1; i++) {
            if (path.get(i) < path.get(i+1)) {
                isDescending = false;
            }
        }
        return isDescending;
    }
        
    /* generateUniqueCoordinate takes 3 parameters:
        an int n with the number of nodes (to use for the random number generator)
        a Random number generator for x values
        a Random number generator for y values
    */
    public Vertex generateUniqueCoordinate(Random xGenerator, Random yGenerator) {
        // generate a random x and y
        int x = xGenerator.nextInt(n);
        int y = yGenerator.nextInt(n);
        Vertex v = new Vertex(x, y);
        boolean isUnique = true;
        
        // check if x does not already exist in vertices
        if (vertices.size() > 0) {
            for (int j = 0; j < vertices.size(); j++) {
                if (vertices.get(j).x() == x) {
                    // if x already exists, start over (generate new coordinates)
                    v = generateUniqueCoordinate(xGenerator, yGenerator);
                }
            }
        }
        return v;
    }

    /* getDistance takes 2 parameters:
        a starting Vertex (x, y coordinate)
        an ending Vertex (x, y coordinate)
        returns the absolute distance between the two
    */
    public double getDistance(Vertex a, Vertex b) {
        double yDistance = Math.abs(b.y() - a.y());
        double xDistance = Math.abs(b.x() - a.x());
        // distance = square root of (yDistance^2 + xDistance^2)
        double distance = Math.sqrt(Math.pow(yDistance, 2) + Math.pow(xDistance, 2));
        return distance;
    }
        
	
    public Vertex getVertex(int i) {
	return vertices.get(i);
    }
    public ArrayList<Vertex> getVertices() {
        return vertices;    
    }
    
    public double[][] getAdjacencyMatrix() {
        return adjacencyMatrix;
    }
    
    public String getVerticesString() {
        String verticesString = "X-Y Coordinates:\n";
        for (int i = 0; i < vertices.size(); i++) {
	    verticesString += "v" + i + ": (" + vertices.get(i).x() + "," + vertices.get(i).y() + ") ";
	}
        verticesString += "\n";
        return verticesString;
    }
    
    public String getMatrixString() {
        String matrixString = "";
        // print numbered columns (will look like this:   0   1   2...)
	for (int i = 0; i < n; i++)
	{
	    matrixString += "      " + i;
	}
	
	// Print each row
	for (int i = 0; i < n; i++)
	{
            matrixString += "\n\n" + i;
	    for (int j = 0; j < n; j++)
	    {
                matrixString += "   " + df.format(adjacencyMatrix[i][j]);
	    }
	}
        return matrixString;
    }
}