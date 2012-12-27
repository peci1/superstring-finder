package cz.cuni.mff.peckam.bioinf.alignments;
/**  */



/**
 * A tuple of objects.
 * 
 * @author Martin Pecka
 * @param <T> Type of the elements in the tuple.
 */
public class Tuple<T>
{
    /** The first element. */
    public final T elem1;
    /** The second element. */
    public final T elem2;

    /**
     * @param elem1 The first element.
     * @param elem2 The second element.
     */
    public Tuple(T elem1, T elem2)
    {
        this.elem1 = elem1;
        this.elem2 = elem2;
    }

    @Override
    public String toString()
    {
        return "<" + elem1 + "; " + elem2 + ">";
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((elem1 == null) ? 0 : elem1.hashCode());
        result = prime * result + ((elem2 == null) ? 0 : elem2.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Tuple<?> other = (Tuple<?>) obj;
        if (elem1 == null) {
            if (other.elem1 != null)
                return false;
        } else if (!elem1.equals(other.elem1))
            return false;
        if (elem2 == null) {
            if (other.elem2 != null)
                return false;
        } else if (!elem2.equals(other.elem2))
            return false;
        return true;
    }
}
