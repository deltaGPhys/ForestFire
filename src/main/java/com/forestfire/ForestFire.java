package com.forestfire;

import java.util.Arrays;
import java.util.Random;
import java.util.stream.IntStream;

public class ForestFire {

    private int dimension;
    private int[][] world;
    private SimpleWindow displayWindow;
    private double p;
    private double f;

    public ForestFire() {

    }

    public ForestFire(Integer dimension, double p, double f) {
        this.displayWindow = new SimpleWindow(dimension);
        this.dimension = dimension;
        this.world = createRandomStart(this.dimension);
        this.p = p;
        this.f = f;
     }

    public ForestFire(Integer dimension, int[][] startmatrix, double p, double f) {
        this.displayWindow = new SimpleWindow(dimension);
        this.dimension = dimension;
        this.world = startmatrix;
        this.p = p;
        this.f = f;
    }

    public static void main(String[] args) {
        ForestFire sim = new ForestFire(200,.005,.0001);
        int[][] endingWorld = sim.simulate(201);
    }

    public int[][] createRandomStart(Integer dimension) {
        int[][] array = new int[dimension][dimension];
        IntStream.range(0,dimension)
            .forEach(row -> IntStream.range(0,dimension)
            .forEach(col -> array[col][row] = new Random().nextInt(2)));
        return array;
    }

    public int[][] simulate(Integer maxGenerations) {
        int[][] current = this.world;
        int[][] next = new int[this.dimension][this.dimension];
        for (int gen = 1; gen < maxGenerations; gen++) {
            this.displayWindow.display(current, gen);

            for (int i = 0; i < this.dimension; i++) {
                for (int j = 0; j < this.dimension; j++) {
                    next[i][j] = determineCellState(i,j,current);
                }
            }

            copyAndZeroOut(next, current);
            this.displayWindow.sleep(125);
        }
        return current;
    }

    public void copyAndZeroOut(int[][] next, int[][] current) {
        for (int i = 0; i < next[0].length; i++) {
            current[i] = Arrays.copyOf(next[i],next[i].length);
            Arrays.fill(next[i],0);
        }
    }

    public int determineCellState(int row, int col, int[][] world) {
        int dim = this.dimension; // just to control length of the expressions below :)
        int[] neighbors =
                new int[] {world[row][(col+1)%dim], world[row][(dim+col-1)%dim],
                world[(row+1)%dim][col], world[(dim+row-1)%dim][col],
                world[(row+1)%dim][(col+1)%dim], world[(dim+row-1)%dim][(col+1)%dim],
                world[(row+1)%dim][(dim+col-1)%dim], world[(dim+row-1)%dim][(dim+col-1)%dim]};
        int countFire = (int) Arrays.stream(neighbors).filter(cell -> cell == 2).count();
        int countTrees = (int) Arrays.stream(neighbors).filter(cell -> cell == 1).count();

        if (world[row][col] == 1) { // trees
            return (countFire > 0 || new Random().nextDouble() <= f) ? 2 : 1;
        } else if (world[row][col] == 0) { // empties
            return (new Random().nextDouble() <= p*(countTrees+1)) ? 1 : 0;
        } else if (world[row][col] == 2) { // on fire
            return 0;
        }
        return 0;
    }


    public void printMatrix(int[][] array) {
        for (int[] row : array) {
            for (int val : row) {
                System.out.print(val + " ");
            }
            System.out.println("");
        }
    }
}
