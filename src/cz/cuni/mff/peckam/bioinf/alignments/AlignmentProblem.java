package cz.cuni.mff.peckam.bioinf.alignments;

import java.util.List;
/**  */



/**
 * 
 *
 * @author Martin Pecka
 */
public abstract class AlignmentProblem extends DynamicProgrammingProblem<Integer, AlignmentResult>
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

    @Override
    protected AlignmentResult findResult()
    {
        final Tuple<Integer> resultCoords = getTracebackLastCoords();
        final int score = valuesTable[resultCoords.elem1][resultCoords.elem2];
        final List<Tuple<Integer>> traceback = traceback(resultCoords);

        return new AlignmentResult(seq1, seq2, score, traceback);
    }
}