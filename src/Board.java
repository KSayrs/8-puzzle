import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.StdOut;

import java.util.Arrays;

/**
 * Created by Katyana on 4/22/2016.
 *
 * Board Object for solving an N-by-N 8-puzzle.
 *
 * Please note that in order to save calculation time, one can just refer to the Manhattan and Hamming
 * variables instead of recalculating them every time, though that option is still open.
 *
 */

public class Board {
    private final int[][] tilec; // copy of tile array
    private final int N;         // length
    public int manhattan;
    public int hamming;
    private int spaceLocation;   // location of the zero blank tile
    private int[] spaceCoord;    // coordinates of the zero blank tile
    private int[] oneD;          // one-dimensional version of board


    // construct a board from an N-by-N array of tiles
    public Board(int[][] tiles){

        // defensive copy
        tilec = Arrays.copyOf(tiles, tiles.length);
        //StdOut.println(tiles.length);

        // find N-by-N
        this.N = this.size(); // this should be fine

        // initial manhattan calculation for tilec
        this.manhattan = manhattan();

        // initial hamming calculation for tilec
        this.hamming = hamming();

        spaceLocation = getSpaceLocation(tilec); // space location for empty tile
        spaceCoord = getSpaceCoord(tilec);       // i and j space locations

        oneD = convertArray(tilec);
    }

    // (where tiles[i][j] = tile at row i, column j)
    // return tile at row i, column j (or 0 if blank)
    public int tileAt(int i, int j){
        if(i < 0 || i>this.size()-1 || j <0 || j>this.size()-1){ throw new java.lang.IndexOutOfBoundsException("i and j not within range"); }

        if (tilec[i][j] != 0) {     // 0 is blank
            return tilec[i][j];
        }
        else return 0;
    }

    // board size N
    public int size(){
        return tilec.length;
    }

    // number of tiles out of place - Gonna take N^2 time no matter what, where N= the row or col length
    public int hamming(){
        int count = 0;
        for(int i=0; i<N; i++){
            for(int j=0; j<N; j++){
                if(tileAt(i, j) != (this.size()*i+j+1) && tileAt(i, j) != 0){  // double check this math
                    count++;
                }
            }
        }
        return count;
    }

    // sum of Manhattan distances between tiles and goal
    public int manhattan(){
        int total = 0;
        for(int i=0; i<N; i++){
            for(int j=0; j<N; j++){
                if(tileAt(i, j) != N*i+j+1 && tileAt(i, j) != 0){  // discount empty
                    total += (Math.abs(getRRow(tileAt(i, j)) - i) + Math.abs(getRCol(tileAt(i, j)) - j)); // check math
                }
            }
        }
        return total;
    }

    // for an out-of-place int, get the right row
    private int getRRow(int num){
        if(num%N > 0){
            return num/N;
        }
        else return num/(N-1);
    }

    // for an out-of-place int, get the right col
    private int getRCol(int num){
        return (num-1)% N;
    }

    // returns the location of a square in terms of a single array
    private int getSquareLocation(int row, int col){
        return (N)*(row) + (col);
    }

    // is this board the goal board?
    public boolean isGoal(){
        if(this.hamming == 0) return true; // Can't believe I didn't see this sooner
        else return false;
    }

    // convert board to one-dimensional array
    private static int[] convertArray(int[][] arr) {
        int[] oneDArray = new int[arr.length * arr.length];
        for(int i = 0; i < arr.length; i ++) {
            for(int s = 0; s < arr.length; s ++) {
                oneDArray[(i * arr.length) + s] = arr[i][s];
            }
        }
        return oneDArray;
    }

    // is this board solvable?
    // Does a board count as solvable if it's already solved? A question for the ages. This program says no, since
    // it's just following the math rules. Fortunately it doesn't matter for the Solver.
    public boolean isSolvable(){
        int inversions = 0;

        for(int i=0; i<oneD.length; i++){
            for(int j=oneD.length-1; j>i; j--){
                if(oneD[j]<oneD[i] && oneD[j]!=0 && oneD[j]!=oneD[i]){
                    inversions++;
                }
            }
        }

        if(N%2!=0){     // if board size is odd
            return inversions % 2 == 0;
        }
        else {          // if board size is even
            return !((inversions + spaceCoord[0])%2 == 0);
        }
    }

    // does this board equal y?
    public boolean equals(Object y){
        if(this == y) return true;  // if same reference spot

        if(y.getClass() != this.getClass()){ // if not same class
            // throw new java.lang.IllegalArgumentException("Non-comparable Objects");
            return false;
        }
        if(y == null) return false; // if null

        Board that = (Board) y;
        if(this.size() != that.size()) return false;        // different dimesions
        if(this.manhattan != that.manhattan) return false;  // different manhattan
        if(this.hamming != that.hamming) return false;      // different hamming
        if(this.spaceLocation != that.spaceLocation) return false; // different space spot
        if(this.spaceCoord != that.spaceCoord) return false; // this one shouldn't be needed but just in case

        // check array equivalencies
        for(int i=0;i<this.size();i++){
            for(int j=0;j<this.size();j++){
                if(this.tileAt(i,j) != that.tileAt(i, j)) return false;
            }
        }
        return true; // otherwise we're good
    }

    // find empty space location
    private int getSpaceLocation(int[][] arr){
        for(int i=0;i<N;i++){
            for(int j=0;j<N;j++){
                if(arr[i][j] == 0){
                    spaceLocation = getSquareLocation(i, j);
                    return spaceLocation;
                }
            }
        }
        throw new java.util.NoSuchElementException("Board is messed up, there's no empty tile");
    }

    // i and j coordinates of space location
    private int[] getSpaceCoord(int[][] arr){
        int[] spaceCoord = new int[2];
        for(int i=0;i<N;i++){
            for(int j=0;j<N;j++){
                if(arr[i][j] == 0){
                    spaceCoord[0] = i;
                    spaceCoord[1] = j;
                }
            }
        }
        return spaceCoord;
    }

    // all neighboring boards - FIFO
    public Iterable<Board> neighbors(){
        Queue<Board> neighbors = new Queue<>();
        int row = spaceCoord[0];
        int col = spaceCoord[1];

        // left
        if(col > 0) {
            if (!this.equals(new Board(swap(row, col, row, col - 1)))) {
                neighbors.enqueue(new Board(swap(row, col, row, col - 1)));
            }
        }

        // right
        if(col < N-1) {
            if (!this.equals(new Board(swap(row, col, row, col + 1)))){
                neighbors.enqueue(new Board(swap(row, col, row, col + 1)));
            }
        }

        // above
        if(row > 0) {
        if (!this.equals(new Board(swap(row, col, row - 1, col)))){
                neighbors.enqueue(new Board(swap(row, col, row - 1, col)));
            }
        }

        // below
        if(row < N-1) {
        if (!this.equals(new Board(swap(row, col, row + 1, col)))){
                neighbors.enqueue(new Board(swap(row, col, row + 1, col)));
            }
        }

        return neighbors;
    }

    // move a tile
    private int[][] swap(int i1, int j1, int i2, int j2){
        // copy a new array again
        int[][] newarr = new int[N][N];
        for(int i=0;i<N;i++){
            for(int j=0;j<N;j++){
                newarr[i][j] = tilec[i][j];
            }
        }

        int old = newarr[i1][j1];  // this should be the empty space
        int newe = newarr[i2][j2]; // swapping tile
        newarr[i1][j1] = newe;
        newarr[i2][j2] = old;
        return newarr;
    }

    // string representation of this board (in the output format specified below)
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append(N + "\n");
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                s.append(String.format("%2d ", tileAt(i, j)));
            }
            s.append("\n");
        }
        return s.toString();
    }

    // unit testing (required)
    public static void main(String[] args){
        // create initial board from file
        In in = new In(args[0]);
        int N = in.readInt();
        int[][] tiles = new int[N][N];
        for (int i = 0; i < N; i++)
            for (int j = 0; j < N; j++)
                tiles[i][j] = in.readInt();
        Board initial = new Board(tiles);
        // string form
        StdOut.println(initial.toString());
        // is it solvable
        StdOut.println("size:  " + initial.size());
        StdOut.println("Is goal?: " + initial.isGoal());
        StdOut.println("Is solvable?: " + initial.isSolvable());
        // manhattan and hamming
        StdOut.println("Manhattan: " + initial.manhattan);
        StdOut.println("Hamming: " + initial.hamming);
    }
}