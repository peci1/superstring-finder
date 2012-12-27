/**  */
package cz.cuni.mff.peckam.bioinf.superstring;

import cz.cuni.mff.peckam.bioinf.alignments.OverlapAlignmentProblem;

/**
 * 
 *
 * @author Martin Pecka
 */
public class SuperstringFinder
{

    /**
     * @param args //TODO no args yet
     */
    public static void main(String[] args)
    {
        final OverlapAlignmentProblem problem = new OverlapAlignmentProblem(boxArray("pawheae".toCharArray()),
                boxArray("heagawghee".toCharArray()), 4, -1, -5);
        System.out.println("score: " + problem.compute());
        System.out.println(problem.traceback());
    }

    /**
     * Box a char[].
     * 
     * @param array The array to box.
     * @return The correcponding Character[].
     */
    private static Character[] boxArray(char[] array)
    {
        Character[] result = new Character[array.length];
        for (int i = 0; i < array.length; i++)
            result[i] = array[i];
        return result;
    }
}
