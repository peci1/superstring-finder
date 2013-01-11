/**  */
package cz.cuni.mff.peckam.bioinf.superstring;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * The input data for genome assembly.
 * 
 * @author Martin Pecka
 */
public class GenomeAssemblyInput
{
    /** The original sequence (may be <code>null</code>). */
    private final List<Character>       originalSequence;
    /** Reads corresponding to the sequence. */
    private final List<List<Character>> reads;

    /**
     * @param originalSequence The original sequence (may be <code>null</code>).
     * @param reads Reads corresponding to the sequence.
     */
    public GenomeAssemblyInput(List<Character> originalSequence, List<List<Character>> reads)
    {
        this.originalSequence = originalSequence;
        this.reads = reads;
    }

    /**
     * @return The original sequence (may be <code>null</code>).
     */
    public List<Character> getOriginalSequence()
    {
        return originalSequence;
    }

    /**
     * @return Reads corresponding to the sequence.
     */
    public List<List<Character>> getReads()
    {
        return reads;
    }

    /**
     * Read the genome assembly input from the given file.
     * 
     * @param file The file to read the input from.
     * 
     * @return The parsed input.
     */
    public static GenomeAssemblyInput readFromFileWithOriginalSequence(File file)
    {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            final List<Character> originalSequence = stringToCharList(reader.readLine());
            final List<List<Character>> reads = new ArrayList<>();

            while (true) {
                final String line = reader.readLine();
                if (line != null) {
                    reads.add(stringToCharList(line));
                } else {
                    break;
                }
            }

            return new GenomeAssemblyInput(originalSequence, reads);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
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
