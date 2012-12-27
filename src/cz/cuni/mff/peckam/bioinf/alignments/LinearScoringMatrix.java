package cz.cuni.mff.peckam.bioinf.alignments;
/**  */


/**
 * Linear scoring matrix.
 * 
 * @author Martin Pecka
 * @param <T> Type of the elements to score.
 */
public class LinearScoringMatrix<T> implements ScoringMatrix<T>
{

    /** The bonus for matching compounds. */
    private final int matchValue;
    /** Penalty for mismatch compounds. Negative value. */
    private final int mismatchValue;
    /** Penalty for gap open. Negative value. */
    private final Integer gapOpen;
    /** Penalty for gap extension. Negative value. */
    private final int     gapExt;

    /**
     * @param matchValue The bonus for matching compounds.
     * @param mismatchValue Penalty for mismatch compounds. Negative value.
     * @param gapValue Penalty for gap. Negative value.
     */
    public LinearScoringMatrix(int matchValue, int mismatchValue, int gapValue)
    {
        this.matchValue = matchValue;
        this.mismatchValue = mismatchValue;
        this.gapOpen = null;
        this.gapExt = gapValue;
    }

    /**
     * @param matchValue The bonus for matching compounds.
     * @param mismatchValue Penalty for mismatch compounds. Negative value.
     * @param gapOpen Penalty for gap open. Negative value.
     * @param gapExt Penalty for gap extension. Negative value.
     */
    public LinearScoringMatrix(int matchValue, int mismatchValue, int gapOpen, int gapExt)
    {
        this.matchValue = matchValue;
        this.mismatchValue = mismatchValue;
        this.gapOpen = gapOpen;
        this.gapExt = gapExt;
    }

    @Override
    public int getScore(T elem1, T elem2)
    {
        return (elem1.equals(elem2) ? matchValue : mismatchValue);
    }

    @Override
    public Integer getGapOpenPenalty()
    {
        return gapOpen;
    }

    @Override
    public int getGapExtendPenalty()
    {
        return gapExt;
    }

}
