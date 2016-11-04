import edu.princeton.cs.algs4.WeightedQuickUnionUF;

/*
 * We model a percolation system using an n-by-n grid of sites.
 * 
 * Each site is either open or blocked.
 * 
 * A full site is an open site that can be connected to an open site
 * in the top row via a chain of neighboring (left, right, up, down) open sites.
 * 
 * We say the system percolates if there is a full site in the bottom row.
 * In other words, a system percolates if we fill all open sites connected
 * to the top row and that process fills some open site on the bottom row.
 */
public class Percolation {

    // By convention, the row and column indices are integers between 1 and n,
    // where (1, 1) is the upper-left site
    private WeightedQuickUnionUF uf;

    // keep track of the open state of each cell independently of connectedness
    private boolean[] open;

    // Introduce 2 virtual sites (and connections to top and bottom).
    // Percolates iff virtual top site is connected to virtual bottom site
    private int vTop, vBottom;

    // need to keep track of grid size for index offset calculation
    private int n;

    // for easy adjacency calculation
    private int[][] offsets = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}};

    // create n-by-n grid, with all sites blocked
    public Percolation(int n) {
        if (n <= 0) {
            throw new IllegalArgumentException();
        }
        this.n = n;
        uf = new WeightedQuickUnionUF(n*n + 2);
        open = new boolean[n*n + 2];
        vTop = n*n;
        vBottom = n*n+1;

        // initially all sites are blocked
        for (int i = 0; i < uf.count(); i++) {
            open[i] = false;
        }

        // connect open vTop to first n-1 nodes (the top row)
        open[vTop] = true;
        for (int i = 0; i < n; i++) {
            uf.union(vTop, i);
        }

        // connect open vBottom to last n-1 nodes (the bottom row)
        open[vBottom] = true;
        for (int i = n*(n-1); i < n*n; i++) {
            uf.union(vBottom, i);
        }
    }

    // convert the row/column numbers to index in sites array
    private int index(int row, int col) {
        if (col < 1 || col > n || row < 1 || row > n) {
            throw new IndexOutOfBoundsException();
        }
        return (row - 1) * n + (col - 1);
    }

    // is site (row, col) open?
    public boolean isOpen(int row, int col) {
        int index = index(row, col);
        return open[index];
    }

    // A full site is an open site that can be connected to an open site
    // in the top row via a chain of neighboring (left, right, up, down) open sites.
    public boolean isFull(int row, int col) {
        int index = index(row, col);
        return open[index] && uf.find(vTop) == uf.find(index);
    }

    // open site (row, col) if it is not open already
    // to model a site as open, connect it to its 4 adjacent open sites
    public void open(int row, int col) {
        // avoid messing up size array
        if (isOpen(row, col)) {
            return;
        }
        int index = index(row, col);
        open[index] = true;
        for (int[] offset : offsets) {
            int nrow = row + offset[0];
            int ncol = col + offset[1];
            if ((nrow > 0) && (nrow <= n) && (ncol > 0) && (ncol <= n)) {
                // if neighboring cell is open, 
                if (isOpen(nrow, ncol)) {
                    int neighbor = index(nrow, ncol);
                    uf.union(index, neighbor);
                }
            }
        }
    }

    // We say the system percolates if there is a full site in the bottom row.
    // In other words, a system percolates if we fill all open sites connected
    // to the top row and that process fills some open site on the bottom row.
    public boolean percolates() {
        // edge case: vTop and vBottom might be virtually connected but n[0] is blocked
        if (n == 1) {
            return open[0];
        }
        return uf.find(vTop) == uf.find(vBottom);
    }

    // test client (optional)
    public static void main(String[] args) {
        
    }
}
