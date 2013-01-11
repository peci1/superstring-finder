package cz.cuni.mff.peckam.bioinf.alignments;

import java.util.List;

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
     * Find the best alignment which contains the end of the one sequence and the start of the other sequence (or one
     * sequence is completely contained in the other).
     * 
     * @param seq1 The first sequence.
     * @param seq2 The second sequence.
     * @param match The bonus for matching compounds.
     * @param mismatch Penalty for mismatch compounds. Negative value.
     * @param gap Penalty for gap. Negative value.
     */
    public OverlapAlignmentProblem(List<Character> seq1, List<Character> seq2, int match, int mismatch, int gap)
    {
        super(seq1, seq2, new LinearScoringMatrix<Character>(match, mismatch, gap));
    }

    @Override
    protected void init()
    {
        for (int i = 0; i < width; i++) {
            // 0 due to overlap alignment - we don't penalize gaps at the start of a sequence
            valuesTable[i][0] = 0;

            if (i > 0)
                tracebackTable[i][0] = new Tuple<>(i - 1, 0);
        }

        // j = 1, because position (0,0) has already been set
        for (int j = 1; j < height; j++) {
            // 0 due to overlap alignment - we don't penalize gaps at the start of a sequence
            valuesTable[0][j] = 0;
            tracebackTable[0][j] = new Tuple<>(0, j - 1);
        }
    }

    @Override
    protected Integer computeValueAndSaveTraceback(Tuple<Integer> coords)
    {
        final int i = coords.elem1, j = coords.elem2;

        // the -1 in seq1/2 indices is due to the zeroth initialization column and row
        final int seq1ToSeq2 = valuesTable[i - 1][j - 1] + scoringMatrix.getScore(seq1.get(i - 1), seq2.get(j - 1));
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

        return max;
    }

    @Override
    protected Tuple<Integer> getTracebackLastCoords()
    {
        // we start the traceback at the end of any word
        if (bestScoreCoords == null) {
            int bestScore = Integer.MIN_VALUE;
            for (int i = 0; i < height; i++) {
                if (valuesTable[width - 1][i] > bestScore) {
                    bestScoreCoords = new Tuple<>(width - 1, i);
                    bestScore = valuesTable[width - 1][i];
                }
            }
            for (int i = 0; i < width; i++) {
                if (valuesTable[i][height - 1] > bestScore) {
                    bestScoreCoords = new Tuple<>(i, height - 1);
                    bestScore = valuesTable[i][height - 1];
                }
            }
        }

        return bestScoreCoords;
    }

    @Override
    protected boolean shouldTracebackStop(Tuple<Integer> coords)
    {
        return coords == null || coords.equals(getCoordsAfterInit()) || coords.elem1 == 0 || coords.elem2 == 0;
    }
}