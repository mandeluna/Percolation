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

	class Site {
		boolean isOpen;
		int parent;

		public String toString() {
			return String.format("{%d:%b}", parent, isOpen);
		}
	}

	// By convention, the row and column indices are integers between 1 and n,
	// where (1, 1) is the upper-left site
	Site[] sites;

	// keep track of the size of each tree so we can construct a weighted tree
	int[] size;

	// Introduce 2 virtual sites (and connections to top and bottom).
	// Percolates iff virtual top site is connected to virtual bottom site
	int vTop, vBottom;

	// need to keep track of grid size for index offset calculation
	int n;

	// create n-by-n grid, with all sites blocked
	public Percolation(int n) {
		if (n <= 0) {
			throw new IllegalArgumentException();
		}
		this.n = n;
		sites = new Site[n*n + 2];
		size = new int[n*n + 2];
		vTop = n*n;
		vBottom = n*n+1;

		// initially all sites are closed
		for (int i=0; i < sites.length; i++) {
			sites[i] = new Site();
			sites[i].isOpen = false;
			sites[i].parent = i;
			size[i] = 1;
		}

		// connect vTop to first n-1 nodes (the top row)
		sites[vTop].isOpen = true;
		for (int i=0; i < n; i++) {
			union(vTop, i);
		}

		// connect vBottom to last n-1 nodes (the bottom row)
		sites[vBottom].isOpen = true;
		for (int i=n*(n-1); i < n*n; i++) {
			union(vBottom, i);
		}
	}

	// find the root node, compress the path
	int root(int i) {
		while (i != sites[i].parent) {
			// make every other node point to its grandparent
			sites[i].parent = sites[sites[i].parent].parent;
			i = sites[i].parent;
		}
		return i;
	}

	// join two nodes
	void union(int p, int q) {
		int i = root(p);
		int j = root(q);
		if (i == j) {
			return;
		}
		if (size[i] < size[j]) {
			sites[i].parent = j;
			size[j] = size[i] + size[j];
		}
		else {
			sites[j].parent = i;
			size[i] = size[j] + size[i];
		}
	}

	boolean find(int p, int q) {
		return root(p) == root(q);
	}

	// convert the row/column numbers to index in sites array
	int index(int row, int col) {
		return (row - 1) * n + (col - 1);
	}

	// for easy adjacency calculation
	int[][] offsets = {{0,1},{1,0},{0,-1},{-1,0}};

	// open site (row, col) if it is not open already
	// to model a site as open, connect it to its 4 adjacent open sites
	public void open(int row, int col) {
		int index = index(row, col);
		if (index < 0 || index > n*n) {
			throw new IllegalArgumentException();
		}
		sites[index].isOpen = true;
		for (int[] offset : offsets) {
			int nrow = row + offset[0];
			int ncol = col + offset[1];
			if ((nrow > 0) && (nrow <= n) && (ncol > 0) && (ncol <= n)) {
				// if neighboring cell is open, 
				if (isOpen(nrow, ncol)) {
					int neighbor = index(nrow, ncol);
					union(index, neighbor);
				}
			}
		}
	}

	// is site (row, col) open?
	public boolean isOpen(int row, int col) {
		int index = index(row, col);
		if (index < 0 || index > n*n) {
			throw new IllegalArgumentException();
		}
		return sites[index].isOpen;
	}

	// A full site is an open site that can be connected to an open site
	// in the top row via a chain of neighboring (left, right, up, down) open sites.
	public boolean isFull(int row, int col) {
		int index = index(row, col);
		if (index < 0 || index > n*n) {
			throw new IllegalArgumentException();
		}
		return sites[index].isOpen && find(vTop, index);
	}

	// We say the system percolates if there is a full site in the bottom row.
	// In other words, a system percolates if we fill all open sites connected
	// to the top row and that process fills some open site on the bottom row.
	public boolean percolates() {
		return find(vTop, vBottom);
	}

	// test client (optional)
	public static void main(String[] args) {
		
	}
}
