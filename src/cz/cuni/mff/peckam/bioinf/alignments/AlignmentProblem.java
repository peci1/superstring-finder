package cz.cuni.mff.peckam.bioinf.alignments;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
/**  */



/**
 * 
 *
 * @author Martin Pecka
 */
public abstract class AlignmentProblem extends DynamicProgrammingProblem<Integer, Integer>
{

    /** The first sequence. */
    protected final Character[]              seq1;
    /** The second sequence. */
    protected final Character[]              seq2;
    /** The scoring matrix. */
    protected final ScoringMatrix<Character> scoringMatrix;
    
    /**
     * @param seq1 The first sequence.
     * @param seq2 The second sequence.
     * @param scoringMatrix The scoring matrix.
     */
    public AlignmentProblem(Character[] seq1, Character[] seq2, ScoringMatrix<Character> scoringMatrix)
    {
        // +1 for the zeroth initialization row and column
        super(seq1.length + 1, seq2.length + 1);

        this.seq1 = seq1;
        this.seq2 = seq2;
        this.scoringMatrix = scoringMatrix;
    }

    @Override
    protected Integer[][] createValuesArray(int width, int height)
    {
        return new Integer[width][height];
    }

    @Override
    protected Tuple<Integer> getCoordsAfterInit()
    {
        return new Tuple<>(1, 1);
    }

    /**
     * @return The traceback.
     */

    public String traceback()
    {
        final List<Tuple<Integer>> trace = traceback(getTracebackLastCoords());
        final StringBuilder result = new StringBuilder();

        Tuple<Integer> previous = null;
        final Iterator<?> seq1it = Arrays.asList(seq1).listIterator(Math.max(0, trace.get(0).elem1));
        final Iterator<?> seq2it = Arrays.asList(seq2).listIterator(Math.max(0, trace.get(0).elem2));

        for (Tuple<Integer> coords : trace) {
            // we wanna skip the first iteration
            if (previous == null) {
                previous = coords;
                continue;
            }

            if (coords.elem1 == previous.elem1) {
                result.append("-");
            } else {
                result.append(seq1it.next());
            }

            if (coords.elem2 == previous.elem2) {
                result.append("-");
            } else {
                result.append(seq2it.next());
            }

            result.append("\n");

            previous = coords;
        }

        return result.toString();
    }

    @Override
    protected Integer findResult()
    {
        Tuple<Integer> resultCoords = getTracebackLastCoords();
        return valuesTable[resultCoords.elem1][resultCoords.elem2];
    }
}