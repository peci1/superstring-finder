/**  */
package cz.cuni.mff.peckam.bioinf.superstring;

import java.util.List;

/**
 * Strategy for assembling a genome (heuristics).
 * 
 * @author Martin Pecka
 */
public interface GenomeAssemblyStrategy
{
    /**
     * Initialize the strategy.
     * 
     * @param reads The reads to work on.
     */
    void init(List<GenomeRead> reads);

    /**
     * Return the first read used as the base of the consensus sequence.
     * 
     * @return Return the first read used as the base of the consensus sequence.
     */
    GenomeRead getFirstRead();

    /**
     * Return the remaining unmerged reads.
     * 
     * @return Return the remaining unmerged reads.
     */
    List<GenomeRead> getRemainingReads();

    /**
     * Return true if there are some reads to work on.
     * 
     * @return Return true if there are some reads to work on.
     */
    boolean hasMoreReads();

    /**
     * Called whenever a read becomes merged to the consensus sequence.
     * 
     * @param read The merged read.
     */
    void readUsed(GenomeRead read);
}
