/**  */
package cz.cuni.mff.peckam.bioinf.alignments;

import java.util.Iterator;
import java.util.List;

/**
 * The result of aligning two sequences.
 * 
 * @author Martin Pecka
 */
public class AlignmentResult
{
    /** The first sequence. */
    private final List<Character>      seq1;

    /** The second sequence. */
    private final List<Character>      seq2;

    /** Score of the alignment. */
    private final int                  score;

    /** The traceback of the alignment (path from the start of the alignment to its end through the values table). */
    private final List<Tuple<Integer>> traceback;

    /** Cached result of {@link #toString()}. */
    private String                     toStringCached = null;

    /**
     * @param seq1 The first sequence.
     * @param seq2 The second sequence.
     * @param score Score of the alignment.
     * @param traceback The traceback of the alignment (path from the start of the alignment to its end through the
     *            values table).
     */
    public AlignmentResult(List<Character> seq1, List<Character> seq2, int score, List<Tuple<Integer>> traceback)
    {
        this.seq1 = seq1;
        this.seq2 = seq2;
        this.score = score;
        this.traceback = traceback;
    }

    /**
     * @return The first sequence.
     */
    public List<Character> getSeq1()
    {
        return seq1;
    }

    /**
     * @return The second sequence.
     */
    public List<Character> getSeq2()
    {
        return seq2;
    }

    /**
     * @return Score of the alignment.
     */
    public int getScore()
    {
        return score;
    }

    @Override
    public String toString()
    {
        if (toStringCached == null) {
            final StringBuilder resultTop = new StringBuilder();
            final StringBuilder resultMiddle = new StringBuilder();
            final StringBuilder resultBottom = new StringBuilder();

            resultTop.append("Score: ").append(score).append("\n");

            final int start1 = traceback.get(0).elem1;
            final int start2 = traceback.get(0).elem2;
            final int maxStart = Math.max(start1, start2);

            for (int i = 0; i < maxStart; i++) {
                resultTop.append((i < maxStart - start1) ? " " : seq1.get(i - (maxStart - start1)));
                resultMiddle.append(" ");
                resultBottom.append((i < maxStart - start2) ? " " : seq2.get(i - (maxStart - start2)));
            }

            Tuple<Integer> previous = null;
            final Iterator<?> seq1it = seq1.listIterator(start1);
            final Iterator<?> seq2it = seq2.listIterator(start2);

            for (Tuple<Integer> coords : traceback) {
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

            toStringCached = resultTop.append(resultMiddle).append(resultBottom).toString();
        }
        return toStringCached;
    }
}
