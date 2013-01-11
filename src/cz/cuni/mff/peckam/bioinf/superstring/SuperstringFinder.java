/**  */
package cz.cuni.mff.peckam.bioinf.superstring;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import cz.cuni.mff.peckam.bioinf.alignments.AlignmentProblem;
import cz.cuni.mff.peckam.bioinf.alignments.AlignmentResult;
import cz.cuni.mff.peckam.bioinf.alignments.OverlapAlignmentProblem;

/**
 * 
 *
 * @author Martin Pecka
 */
public class SuperstringFinder
{

    /** Strategy of the finder. */
    private final GenomeAssemblyStrategy strategy;

    /** The maximum number of iterations. */
    private final static int             MAX_ITERATIONS = 1000000;

    /** Minimum similarity factor of a string to be treated as substring of consensus. */
    private final static double          MIN_SUBSTRING_SIMILARITY = 0.9;

    /**
     * @param args String filename (the file to read input from).
     */
    public static void main(String[] args)
    {
        final long startTime = System.currentTimeMillis();

        final GenomeAssemblyInput input = GenomeAssemblyInput.readFromFileWithOriginalSequence(new File(args[0]));
        System.err.println("Parse completed.");

        final GenomeAssemblyStrategy strategy = new SimpleGenomeAssemblyStrategy();

        final SuperstringFinder finder = new SuperstringFinder(strategy);
        List<Character> result = finder.assembleGenome(input);

        System.err.println((System.currentTimeMillis() - startTime) / 1000 + " s runtime");

        if (input.getOriginalSequence() == null) {
            System.out.println(result);
        } else {
            final AlignmentProblem problem = new OverlapAlignmentProblem(input.getOriginalSequence(), result, 1, -1, -1);
            System.out.println(problem.compute());
        }
    }

    /**
     * @param strategy Strategy of the finder.
     */
    public SuperstringFinder(GenomeAssemblyStrategy strategy)
    {
        this.strategy = strategy;
    }

    /**
     * Perform genome assembly on the given input.
     * 
     * @param input The input to work on.
     * 
     * @return The assembled genome.
     */
    public List<Character> assembleGenome(GenomeAssemblyInput input)
    {
        final List<GenomeRead> reads = new ArrayList<>();
        for (List<Character> read : input.getReads()) {
            reads.add(new GenomeRead(read));
        }
        strategy.init(reads);

        System.err.println("Init completed.");

        final GenomeRead firstRead = strategy.getFirstRead();
        final List<Character> result = new LinkedList<>(firstRead.getRead());
        strategy.readUsed(firstRead);

        // merge all subsequences of the initial sequence
        {
            final List<GenomeRead> readsUsed = new LinkedList<>();
            for (GenomeRead read : strategy.getRemainingReads()) {
                final AlignmentProblem overlapProblem = new OverlapAlignmentProblem(result, read.getRead(), 1, -1, -1);
                final AlignmentResult overlapResult = overlapProblem.compute();
                if (overlapResult.doesSeq1ContainSeq2()
                        && overlapResult.getScore() > overlapResult.getSeq2().size() * MIN_SUBSTRING_SIMILARITY) {
                    overlapResult.mergeSeq2ToSeq1();
                    readsUsed.add(read);
                }
            }
            // separated to avoid ConcurrentModificationException
            for (GenomeRead read : readsUsed) {
                strategy.readUsed(read);
            }
        }

        System.err.println("Pre-clean completed.");

        int i = 0;
        outer: while (strategy.hasMoreReads()) {
            AlignmentResult maxResult = null;
            GenomeRead maxRead = null;
            for (GenomeRead read : strategy.getRemainingReads()) {
                final AlignmentProblem overlapProblem = new OverlapAlignmentProblem(result, read.getRead(), 1, -1, -1);
                final AlignmentResult overlapResult = overlapProblem.compute();
                if (overlapResult.doesSeq1ContainSeq2()
                        && overlapResult.getScore() > overlapResult.getSeq2().size() * MIN_SUBSTRING_SIMILARITY) {
                    overlapResult.mergeSeq2ToSeq1();
                    strategy.readUsed(read);
                    continue outer; // without this, a ConcurrentModificationException could be thrown
                } else if (maxResult == null || overlapResult.getScore() > maxResult.getScore()) {
                    maxResult = overlapResult;
                    maxRead = read;
                }
            }

            if (maxResult != null && maxRead != null) {
                maxResult.mergeSeq2ToSeq1();
                strategy.readUsed(maxRead);
            }

            i++;
            if (i > MAX_ITERATIONS) {
                System.err.println("Maximum number of iterations reached");
                return result;
            }

            System.err.println("Remaining reads to merge: " + strategy.getRemainingReads().size());
        }

        return result;
    }
}
