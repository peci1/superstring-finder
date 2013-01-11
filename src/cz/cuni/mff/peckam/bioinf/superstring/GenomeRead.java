/**  */
package cz.cuni.mff.peckam.bioinf.superstring;

import java.util.List;

/**
 * A container for a sequence of genetic bases.
 * 
 * @author Martin Pecka
 */
public class GenomeRead
{
    /** The contained read. */
    private final List<Character> read;

    /**
     * @param read The read.
     */
    public GenomeRead(List<Character> read)
    {
        this.read = read;
    }

    /**
     * @return The read.
     */
    public List<Character> getRead()
    {
        return read;
    }

    // it is important not to redefine hashCode(), since we wanna use this in a Set and we want it to be fast!
}
