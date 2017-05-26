import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdOut;

/**
 * Created by Katyana on 4/22/2016.
 *
 * Solver class for 8-puzzle. Too slow to solve some puzzles in a reasonable amount of time.
 *
 */

public class Solver {

    private Node search;
    private final Board tilec;
    private final int N;
    private MinPQ<Node> pq;
    private Node finalSearch;

    // find a solution to the initial board (using the A* algorithm)
    public Solver(Board initial){

        N = initial.size();

        // initial board defensive copy
        int[][] tiles = new int[N][N];
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                tiles[i][j] = initial.tileAt(i, j);
            }
        }

        tilec = new Board(tiles);

        // initial search node
        search = new Node(tilec);
        search.prev = null;
        search.moves = 0;

        // priority queue
        pq = new MinPQ<>();
        pq.insert(search);

        while(true){
            finalSearch = run(pq);
            if(finalSearch != null) break;
        }
    }

    // find the solution board
    private Node run(MinPQ<Node> nodes){
        if(nodes.isEmpty()) return null;
        Node removed = nodes.delMin();
        if(removed.board.isGoal()) return removed;
        for (Board item : removed.board.neighbors()) {
            Node newNode = new Node(item, removed);
            nodes.insert(newNode);
        }
        return null;
    }

    // another private node class
    private class Node implements Comparable<Node> {
        private Board board;
        private Node prev;
        private int moves;

        //new
        private Node(Board board){
            this.board = board;
        }

        //newnew
        private Node(Board board, Node pre) {
            this.board = board;
            this.prev = pre;
            this.moves = this.prev.moves + 1;
        }

        public int compareTo(Node o) { // priority compare
            return (this.board.manhattan - o.board.manhattan) + (this.moves - o.moves);
        }
    }


    // min number of moves to solve initial board
    public int moves(){
        return finalSearch.moves;
    }

    // sequence of boards in a shortest solution - empty out the nodes
    public Iterable<Board> solution(){
        Stack<Board> moves = new Stack<>();
        while(finalSearch != null){     // iterate backwards through moves using the node pointers
            moves.push(finalSearch.board);
            finalSearch = finalSearch.prev;
        }
        return moves;
    }

    // solve a slider puzzle
    public static void main(String[] args){
        // create initial board from file
        In in = new In(args[0]);
        int N = in.readInt();
        int[][] tiles = new int[N][N];
        for (int i = 0; i < N; i++)
            for (int j = 0; j < N; j++)
                tiles[i][j] = in.readInt();
        Board initial = new Board(tiles);

        // check and see if the puzzle's already solved
        if (initial.isGoal()){
            StdOut.println("Puzzle is already solved");
        }

        // check if puzzle is solvable; if so, solve it and output solution
        if (initial.isSolvable()) {
            Solver solver = new Solver(initial);
            StdOut.println("Minimum number of moves = " + solver.moves());
            for (Board board : solver.solution())
                StdOut.println(board);
        }

        // if not, report unsolvable
        else {
            StdOut.println("Unsolvable puzzle");
        }
    }
}