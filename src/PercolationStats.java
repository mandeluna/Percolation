import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.StdStats;

public class PercolationStats {
    
    private double[] thresholds;
    private double mean;
    private double stddev;
    private int trials;

    // perform trials independent experiments on an n-by-n grid
    public PercolationStats(int n, int trials) {
        if ((n <= 0) || (trials <= 0)) {
            throw new java.lang.IllegalArgumentException();
        }
        // arbitrary seed to ensure we get the same results each time
//        StdRandom.setSeed(1000L);

        this.trials = trials;
        thresholds = new double[trials];
        for (int trial = 0; trial < trials; trial++) {
            Percolation perc = new Percolation(n);
            // number of sites that are opened
            int count = 0;
            while (!perc.percolates()) {
                int row = (int) Math.floor((StdRandom.uniform() * n)) + 1;
                int col = (int) Math.floor((StdRandom.uniform() * n)) + 1;
                if (!perc.isOpen(row, col)) {
                    perc.open(row, col);
                    count++;
                }
            }
            thresholds[trial] = (double) count / (n*n);
        }
        this.mean = StdStats.mean(thresholds);
        this.stddev = StdStats.stddev(thresholds);
    }

    // sample mean of percolation threshold
    public double mean() {
        return this.mean;
    }

    // sample standard deviation of percolation threshold
    public double stddev() {
        return this.stddev;
    }

    // low  endpoint of 95% confidence interval
    public double confidenceLo() {
        return this.mean - 1.96D * this.stddev / Math.sqrt(trials);
    }

    // high endpoint of 95% confidence interval
    public double confidenceHi() {
        return this.mean + 1.96D * this.stddev / Math.sqrt(trials);
    }

    // test client (described below)
    /*
     * Also, include a main() method that takes two command-line arguments n and T,
     * performs T independent computational experiments (discussed above) on an n-by-n grid,
     * and prints the mean, standard deviation, and the 95% confidence interval for the
     * percolation threshold.
     * 
     * Use StdRandom to generate random numbers;
     * use StdStats to compute the sample mean and standard deviation.
     * 
     *    % java PercolationStats 200 100
     *    mean                    = 0.5929934999999997
     *    stddev                  = 0.00876990421552567
     *    95% confidence interval = 0.5912745987737567, 0.5947124012262428
     *    
     *    % java PercolationStats 200 100
     *    mean                    = 0.592877
     *    stddev                  = 0.009990523717073799
     *    95% confidence interval = 0.5909188573514536, 0.5948351426485464
     *    
     *    
     *    % java PercolationStats 2 10000
     *    mean                    = 0.666925
     *    stddev                  = 0.11776536521033558
     *    95% confidence interval = 0.6646167988418774, 0.6692332011581226
     *    
     *    % java PercolationStats 2 100000
     *    mean                    = 0.6669475
     *    stddev                  = 0.11775205263262094
     *    95% confidence interval = 0.666217665216461, 0.6676773347835391
     */
    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Two command-line arguments are required");
            return;
        }
        int n = Integer.parseInt(args[0]);
        int trials = Integer.parseInt(args[1]);

        PercolationStats stats = new PercolationStats(n, trials);

        System.out.println("mean                    = " + stats.mean());
        System.out.println("stddev                  = " + stats.stddev());
        System.out.println("95% confidence interval = " + stats.confidenceLo() + ", " + stats.confidenceHi());
    }
}
