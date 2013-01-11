/**  */
package cz.cuni.mff.peckam.bioinf.superstring;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * A dummy genome assembly strategy.
 * 
 * @author Martin Pecka
 */
public class SimpleGenomeAssemblyStrategy implements GenomeAssemblyStrategy
{

    /** The remaining reads to work on. */
    private List<GenomeRead> reads;

    @Override
    public void init(List<GenomeRead> reads)
    {
        this.reads = reads;
        Collections.sort(this.reads, new Comparator<GenomeRead>() {
            @Override
            public int compare(GenomeRead o1, GenomeRead o2)
            {
                if (o1.getRead().size() > o2.getRead().size()) {
                    return -1;
                } else if (o1.getRead().size() == o2.getRead().size()) {
                    return 0;
                } else {
                    return 1;
                }
            }
        });
    }

    @Override
    public GenomeRead getFirstRead()
    {
        return reads.get(0);
    }

    @Override
    public List<GenomeRead> getRemainingReads()
    {
        return reads;
    }

    @Override
    public boolean hasMoreReads()
    {
        return !reads.isEmpty();
    }

    @Override
    public void readUsed(GenomeRead read)
    {
        reads.remove(read);
    }

}
