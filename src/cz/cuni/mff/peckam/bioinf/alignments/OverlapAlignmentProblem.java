package cz.cuni.mff.peckam.bioinf.alignments;

/**
 * Overlap sequence alignment.
 * 
 * @author Martin Pecka
 */
public class OverlapAlignmentProblem extends AlignmentProblem
{

    /** Coordinates of the item with best score. */
    private Tuple<Integer> bestScoreCoords = null;

    /**
     * Find the best alignment which contains the end of the first sequence and the start of the second sequence.
     * 
     * @param seq1 The first sequence.
     * @param seq2 The second sequence.
     * @param match The bonus for matching compounds.
     * @param mismatch Penalty for mismatch compounds. Negative value.
     * @param gap Penalty for gap. Negative value.
     */
    public OverlapAlignmentProblem(Character[] seq1, Character[] seq2, int match, int mismatch, int gap)
    {
        super(seq1, seq2, new LinearScoringMatrix<Character>(match, mismatch, gap));
    }

    @Override
    protected void init()
    {
        for (int i = 0; i < width; i++) {
            // 0 due to overlap alignment - we don't penalize gaps at the start of the second sequence
            valuesTable[i][0] = 0;

            if (i > 0)
                tracebackTable[i][0] = new Tuple<>(i - 1, 0);
        }

        for (int j = 0; j < height; j++) {
            valuesTable[0][j] = j * scoringMatrix.getGapExtendPenalty();

            if (j > 0)
                tracebackTable[0][j] = new Tuple<>(0, j - 1);
        }

        bestScoreCoords = new Tuple<>(0, 0);
    }

    @Override
    protected Integer computeValueAndSaveTraceback(Tuple<Integer> coords)
    {
        final int i = coords.elem1, j = coords.elem2;

        // the -1 in seq1/2 indices is due to the zeroth initialization column and row
        final int seq1ToSeq2 = valuesTable[i - 1][j - 1] + scoringMatrix.getScore(seq1[i - 1], seq2[j - 1]);
        final int seq1ToGap = valuesTable[i - 1][j] + scoringMatrix.getGapExtendPenalty();
        final int gapToSeq2 = valuesTable[i][j - 1] + scoringMatrix.getGapExtendPenalty();

        int max;
        if (seq1ToSeq2 >= seq1ToGap && seq1ToSeq2 >= gapToSeq2) {
            tracebackTable[i][j] = new Tuple<>(i - 1, j - 1);
            max = seq1ToSeq2;
        } else if (seq1ToGap >= seq1ToSeq2 && seq1ToGap >= gapToSeq2) {
            tracebackTable[i][j] = new Tuple<>(i - 1, j);
            max = seq1ToGap;
        } else {
            tracebackTable[i][j] = new Tuple<>(i, j - 1);
            max = gapToSeq2;
        }

        if (max > valuesTable[bestScoreCoords.elem1][bestScoreCoords.elem2])
            bestScoreCoords = new Tuple<>(i, j);

        return max;
    }

    @Override
    protected Tuple<Integer> getTracebackLastCoords()
    {
        // TODO in overlap alignment, the traceback has to start in the last row!
        return bestScoreCoords;
    }

    @Override
    protected boolean shouldTracebackStop(Tuple<Integer> coords)
    {
        return coords == null || coords.equals(getCoordsAfterInit()) || valuesTable[coords.elem1][coords.elem2] == 0;
    }
}