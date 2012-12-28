/**  */
package cz.cuni.mff.peckam.bioinf.alignments;

import java.util.Iterator;
import java.util.LinkedList;
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

    /**
     * @return True if seq1 contains seq2 as its part (with indels allowed).
     */
    public boolean doesSeq1ContainSeq2()
    {
        final int start1 = traceback.get(0).elem1;
        final int start2 = traceback.get(0).elem2;

        final int end1 = traceback.get(traceback.size()-1).elem1;
        final int end2 = traceback.get(traceback.size()-1).elem2;
        
        if (end1 < seq1.size() - 1 && start1 > 0) {
            return true;
        } else if (start1 > 0) {
            return end2 == seq2.size() - 1;
        } else if (end1 < seq1.size() - 1) {
            return start2 == 0;
        } else {
            return start2 == 0 && end2 == seq2.size() - 1;
        }
    }
    
    /**
     * Append seq2 after seq1 according to the alignment. If <code>{@link #doesSeq1ContainSeq2()} == true</code>, this
     * method does nothing.
     * <p>
     * Beware! This function alters the list provided to the result constructor, so subsequent calls of other methods on
     * this object would not be a good idea.
     * 
     * @return The first sequence with the second one appended.
     */
    public List<Character> mergeSeq2ToRightOfSeq1()
    {
        // nothing to append
        if (doesSeq1ContainSeq2())
            return seq1;

        final ResultWalkingEventHandler handler = new ResultWalkingEventHandler() {
            /** The characters of seq2 to append. */
            private List<Character> toAppend = new LinkedList<>();

            @Override
            public void afterAlignment(Character seq1Char, Character seq2Char)
            {
                if (seq2Char != null && seq1Char == null)
                    toAppend.add(seq2Char);
            }

            @SuppressWarnings("synthetic-access")
            @Override
            public void completed()
            {
                seq1.addAll(toAppend);
            }

            @Override
            public void init()
            {
            }

            @Override
            public void inAlignment(Character seq1Char, Character seq2Char)
            {
            }

            @Override
            public void beforeAlignment(Character seq1Char, Character seq2Char)
            {
            }
        };

        walkResult(handler);

        return seq1;
    }

    /**
     * @param handler Event handler for walking the result.
     */
    private void walkResult(ResultWalkingEventHandler handler)
    {
        handler.init();

        final int start1 = traceback.get(0).elem1;
        final int start2 = traceback.get(0).elem2;
        final int maxStart = Math.max(start1, start2);

        final Iterator<Character> seq1it = seq1.iterator();
        final Iterator<Character> seq2it = seq2.iterator();

        for (int i = 0; i < maxStart; i++) {
            final Character seq1Char = (i < maxStart - start1) ? null : seq1it.next();
            final Character seq2Char = (i < maxStart - start2) ? null : seq2it.next();
            handler.beforeAlignment(seq1Char, seq2Char);
        }

        Tuple<Integer> previous = null;

        for (Tuple<Integer> coords : traceback) {
            // we wanna skip the first iteration
            // FIXME sometimes we don't wanna skip it (if it starts from the beginning of seq1?)
            if (previous == null) {
                previous = coords;
                continue;
            }

            final Character seq1Char = (coords.elem1 == previous.elem1) ? null : seq1it.next();
            final Character seq2Char = (coords.elem2 == previous.elem2) ? null : seq2it.next();

            handler.inAlignment(seq1Char, seq2Char);

            previous = coords;
        }

        while (seq1it.hasNext() || seq2it.hasNext()) {
            final Character seq1Char = seq1it.hasNext() ? seq1it.next() : null;
            final Character seq2Char = seq2it.hasNext() ? seq2it.next() : null;

            handler.afterAlignment(seq1Char, seq2Char);
        }

        handler.completed();
    }

    @Override
    public String toString()
    {
        if (toStringCached == null) {
            final StringBuilder resultTop = new StringBuilder();
            final StringBuilder resultMiddle = new StringBuilder();
            final StringBuilder resultBottom = new StringBuilder();

            resultTop.append("Score: ").append(score).append("\n");

            final ResultWalkingEventHandler handler = new ResultWalkingEventHandler() {

                @Override
                public void init()
                {
                }

                @Override
                public void completed()
                {
                }

                @Override
                public void beforeAlignment(Character seq1Char, Character seq2Char)
                {
                    resultTop.append(seq1Char != null ? seq1Char : " ");
                    resultMiddle.append(" ");
                    resultBottom.append(seq2Char != null ? seq2Char : " ");
                }

                @Override
                public void inAlignment(Character seq1Char, Character seq2Char)
                {
                    resultTop.append(seq1Char != null ? seq1Char : "-");

                    if (seq1Char != null && seq2Char != null) {
                        resultMiddle.append(seq1Char.equals(seq2Char) ? "|" : "x");
                    } else {
                        resultMiddle.append("-");
                    }

                    resultBottom.append(seq2Char != null ? seq2Char : "-");

                }

                @Override
                public void afterAlignment(Character seq1Char, Character seq2Char)
                {
                    resultTop.append(seq1Char != null ? seq1Char : " ");
                    resultMiddle.append(" ");
                    resultBottom.append(seq2Char != null ? seq2Char : " ");
                }
            };
            walkResult(handler);

            resultTop.append("\n");
            resultMiddle.append("\n");

            toStringCached = resultTop.append(resultMiddle).append(resultBottom).toString();
        }
        return toStringCached;
    }

    /**
     * Handles events when walking through the result of alignment.
     * 
     * @author Martin Pecka
     */
    private interface ResultWalkingEventHandler
    {

        /**
         * Initialize the handler.
         */
        void init();

        /**
         * The walkthrough has been completed. Shutdown code.
         */
        void completed();

        /**
         * Walking a part before the start of the alignment.
         * 
         * @param seq1Char Character in the first sequence, or <code>null</code> if the sequence has not begun yet.
         * @param seq2Char Character in the second sequence, or <code>null</code> if the sequence has not begun yet.
         */
        void beforeAlignment(Character seq1Char, Character seq2Char);

        /**
         * Walking the alignment part.
         * 
         * @param seq1Char Character in the first sequence, or <code>null</code> for a gap.
         * @param seq2Char Character in the second sequence, or <code>null</code> for a gap.
         */
        void inAlignment(Character seq1Char, Character seq2Char);

        /**
         * Walking a part after the end of the alignment.
         * 
         * @param seq1Char Character in the first sequence, or <code>null</code> if the sequence has already ended.
         * @param seq2Char Character in the second sequence, or <code>null</code> if the sequence has already ended.
         */
        void afterAlignment(Character seq1Char, Character seq2Char);

    }
}
