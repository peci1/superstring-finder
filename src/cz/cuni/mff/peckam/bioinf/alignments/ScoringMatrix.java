package cz.cuni.mff.peckam.bioinf.alignments;
/**  */


/**
 * A scoring matrix.
 * 
 * @author Martin Pecka
 * @param <T> Type of the elements to score.
 */
public interface ScoringMatrix<T>
{
    /**
     * Return the score corresponding to the given elements.
     * 
     * @param elem1 First element.
     * @param elem2 Second element.
     * 
     * @return The score (negative if penalty).
     */
    int getScore(T elem1, T elem2);

    /**
     * @return The gap open penalty. <code>null</code> if the penalty is not used.
     */
    Integer getGapOpenPenalty();

    /**
     * @return The gap extension penalty.
     */
    int getGapExtendPenalty();
}
