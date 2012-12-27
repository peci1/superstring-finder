package cz.cuni.mff.peckam.bioinf.alignments;

import java.util.ArrayList;
import java.util.List;



/**
 * A general skeleton of a dynamic programming problem.
 * 
 * @author Martin Pecka
 * @param <T> Type of the numbers this problem copes with.
 * @param <Result> Type of the result.
 */
public abstract class DynamicProgrammingProblem<T extends Number, Result>
{
    /** The first dimension of the DP table. */
    protected final int   width;

    /** The second dimension of the DP table. */
    protected final int   height;

    /** Table with the computed values. */
    protected final T[][] valuesTable;

    /** Table for tracing back the result. */
    protected Tuple<Integer>[][] tracebackTable;

    /**
     * Create the dynamic problem.
     * 
     * @param width First dimension of the DP table.
     * @param height Second dimension of the DP table.
     */
    @SuppressWarnings({ "unchecked" })
    public DynamicProgrammingProblem(int width, int height)
    {
        this.width = width;
        this.height = height;

        valuesTable = createValuesArray(width, height);
        tracebackTable = new Tuple[width][height];
    }

    /**
     * Compute the problem's solution.
     * 
     * @return The solution of the problem.
     */
    public final Result compute()
    {
        System.gc();

        init();

        Tuple<Integer> coords = getCoordsAfterInit();

        final int iterations = width * height;

        for (int i = 0; coords.elem1 < width && coords.elem2 < height; i++) {
            valuesTable[coords.elem1][coords.elem2] = computeValueAndSaveTraceback(coords);

            coords = nextCoords(coords);

            if ((10 * i) / iterations != (10 * (i - 1)) / iterations)
                System.err.println((100 * i) / iterations + "%");
        }

        System.gc();

        return findResult();
    }

    /**
     * Create an empty values array of the desired dimensions.
     * 
     * @param width First dimension of the array.
     * @param height Second dimension of the array.
     * 
     * @return The array.
     */
    protected abstract T[][] createValuesArray(int width, int height);

    /**
     * Fill {@link #valuesTable} and tracebackTable with inizialization values.
     * <p>
     * Called by {@link #compute()}, don't call it manually.
     */
    protected abstract void init();

    /**
     * Return the coordinates where to start DP procedure after initialization.
     * <p>
     * Called by {@link #compute()}, don't call it manually.
     * 
     * @return The coordinates. Must be in interval [0, {@link #width}]x[0, {@link #height}].
     */
    protected abstract Tuple<Integer> getCoordsAfterInit();

    /**
     * Perform one DP computation.
     * <p>
     * Called by {@link #compute()}, don't call it manually.
     * 
     * @param coords The coordinates of the item to compute.
     * @return The computed value.
     */
    protected abstract T computeValueAndSaveTraceback(Tuple<Integer> coords);

    /**
     * Get the coordinates for the next step.
     * <p>
     * Override this to get another directions of computation.
     * 
     * @param coords Current coordinates.
     * @return The next step's coordinates.
     */
    protected Tuple<Integer> nextCoords(Tuple<Integer> coords)
    {
        int i, j;
        if (coords.elem1 + 1 < width) {
            i = coords.elem1 + 1;
            j = coords.elem2;
        } else {
            // not 0, since we assume that the first column is filled in init()
            i = 1;
            j = coords.elem2 + 1;
        }
        
        return new Tuple<>(i, j);
    }

    /**
     * After the computation is completed, find the result in the table.
     * 
     * @return The result of the computation, eg. some optimal value or so.
     */
    protected abstract Result findResult();
    
    /**
     * Generate a traceback path from the given element.
     * 
     * @param lastElemCoords Coordinates of the element to traceback.
     * 
     * @return The best path from start to the given element.
     */

    public final List<Tuple<Integer>> traceback(Tuple<Integer> lastElemCoords)
    {
        final List<Tuple<Integer>> result = new ArrayList<>();

        result.add(lastElemCoords);

        Tuple<Integer> coords = lastElemCoords;
        while (!shouldTracebackStop(coords)) {
            coords = tracebackTable[coords.elem1][coords.elem2];
            result.add(0, coords);
        }

        // we don't want to have the zeroth column and row in traceback
        if (result.get(0) == null || (result.get(0).elem1 == 0 && result.get(0).elem2 == 0))
            result.remove(0);

        return result;
    }

    /**
     * @return The coordinates of the point where to start traceback from.
     */
    protected abstract Tuple<Integer> getTracebackLastCoords();

    /**
     * Returns true if the traceback loop should not continue.
     * 
     * @param coords The coordinates we ask for continuation.
     * @return True if the traceback loop should stop.
     */
    protected abstract boolean shouldTracebackStop(Tuple<Integer> coords);
}
