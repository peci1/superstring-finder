/**  */
package cz.cuni.mff.peckam.bioinf.superstring;

import java.util.ArrayList;
import java.util.List;

import cz.cuni.mff.peckam.bioinf.alignments.AlignmentResult;
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
        final OverlapAlignmentProblem problem = new OverlapAlignmentProblem(stringToCharList("pawheae"),
                stringToCharList("heagawghee"), 4, -1, -5);
        // final OverlapAlignmentProblem problem = new OverlapAlignmentProblem(stringToCharList("wheaelsder"),
        // stringToCharList("pawheae"), 4, -1, -5);
        // final OverlapAlignmentProblem problem = new
        // OverlapAlignmentProblem(stringToCharList("aaaaaaaaaaabbbbbaaaaaa"),
        // stringToCharList("cbbbae"), 4, -1, -5);
        // final OverlapAlignmentProblem problem = new OverlapAlignmentProblem(stringToCharList("abccccccc"),
        // stringToCharList("ab"), 4, -1, -5);
        // final OverlapAlignmentProblem problem = new OverlapAlignmentProblem(stringToCharList("abpafpaf"),
        // stringToCharList("bpaf"), 4, -1, -5);
        final AlignmentResult result = problem.compute();
        System.out.println(result);
    }

    /**
     * Convert a string to a list of its characters.
     * 
     * @param s String to convert.
     * @return List of the string's characters.
     */
    private static List<Character> stringToCharList(String s)
    {
        final List<Character> result = new ArrayList<>();
        for (int i = 0; i < s.length(); i++) {
            result.add(s.charAt(i));
        }

        return result;
    }
}
