package cz.cuni.mff.peckam.bioinf.alignments;

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
    protected final List<Character>          seq1;
    /** The second sequence. */
    protected final List<Character>          seq2;
    /** The scoring matrix. */
    protected final ScoringMatrix<Character> scoringMatrix;
    
    /**
     * @param seq1 The first sequence.
     * @param seq2 The second sequence.
     * @param scoringMatrix The scoring matrix.
     */
    public AlignmentProblem(List<Character> seq1, List<Character> seq2, ScoringMatrix<Character> scoringMatrix)
    {
        // +1 for the zeroth initialization row and column
        super(seq1.size() + 1, seq2.size() + 1);

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

        final StringBuilder resultTop = new StringBuilder();
        final StringBuilder resultMiddle = new StringBuilder();
        final StringBuilder resultBottom = new StringBuilder();

        final int start1 = trace.get(0).elem1;
        final int start2 = trace.get(0).elem2;
        final int maxStart = Math.max(start1, start2);

        for (int i = 0; i < maxStart; i++) {
            resultTop.append((i < maxStart - start1) ? " " : seq1.get(i - (maxStart - start1)));
            resultMiddle.append(" ");
            resultBottom.append((i < maxStart - start2) ? " " : seq2.get(i - (maxStart - start2)));
        }

        Tuple<Integer> previous = null;
        final Iterator<?> seq1it = seq1.listIterator(start1);
        final Iterator<?> seq2it = seq2.listIterator(start2);

        for (Tuple<Integer> coords : trace) {
            // we wanna skip the first iteration
            // FIXME sometimes we don't wanna skip it (if it starts from the beginning of seq1?)
            if (previous == null) {
                previous = coords;
                continue;
            }

            boolean wasIndel = false;
            if (coords.elem1 == previous.elem1) {
                resultTop.append("-");
                resultMiddle.append("-");
                wasIndel = true;
            } else {
                resultTop.append(seq1it.next());
            }

            if (coords.elem2 == previous.elem2) {
                resultBottom.append("-");
                resultMiddle.append("-");
                wasIndel = true;
            } else {
                resultBottom.append(seq2it.next());
            }

            if (!wasIndel) {
                if (seq1.get(coords.elem1 - 1).equals(seq2.get(coords.elem2 - 1))) {
                    resultMiddle.append("|");
                } else {
                    resultMiddle.append("x");
                }
            }

            previous = coords;
        }

        while (seq1it.hasNext())
            resultTop.append(seq1it.next());

        while (seq2it.hasNext())
            resultBottom.append(seq2it.next());

        resultTop.append("\n");
        resultMiddle.append("\n");

        return resultTop.append(resultMiddle).append(resultBottom).toString();
    }

    @Override
    protected Integer findResult()
    {
        Tuple<Integer> resultCoords = getTracebackLastCoords();
        return valuesTable[resultCoords.elem1][resultCoords.elem2];
    }
}