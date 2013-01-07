/**  */
package cz.cuni.mff.peckam.bioinf.superstring;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Generate test data for genome assembly.
 * 
 * @author Martin Pecka
 */
public class RandomAssemblyDataGenerator
{

    /**
     * @param args int length, int minReadLength, int maxReadLength, int numReads, double errorRate, String
     *            resultSavePath
     */
    public static void main(String[] args)
    {
        if (args.length != 6) {
            System.out.println("Please, enter these arguments: \n"+
                    "int length, int minReadLength, int maxReadLength, int numReads, double errorRate, string resultSavePath");
            System.exit(-1);
        }
        
        final int length = Integer.parseInt(args[0]);
        final int minReadLength = Integer.parseInt(args[1]);
        final int maxReadLength = Integer.parseInt(args[2]);
        final int numReads = Integer.parseInt(args[3]);
        final double errorRate = Double.parseDouble(args[4]);
        final String resultSavePath = args[5];
        
        final char[] originalSequence = new char[length];
        final List<char[]> reads = new ArrayList<>(numReads);
        
        final char[] chars = new char[] {'a', 'c', 'g', 't'};
        final Random random = new Random();
        
        // generate original sequence
        for (int i = 0; i < originalSequence.length; i++) {
            originalSequence[i] = chars[random.nextInt(chars.length)];
        }
        
        // generate reads
        for (int i = 0; i < numReads; i++) {
            final int readLength = minReadLength + random.nextInt(maxReadLength - minReadLength);
            final int readStart = random.nextInt(originalSequence.length - readLength);
            final char[] read = Arrays.copyOfRange(originalSequence, readStart, readStart + readLength);
            reads.add(read);

            // add errors to reads
            for (int j = 0; j < read.length; j++) {
                if (random.nextDouble() < errorRate) {
                    read[j] = chars[random.nextInt(chars.length)];
                }
            }
        }

        final File output = new File(resultSavePath);

        try (BufferedWriter outputWriter = new BufferedWriter(new FileWriter(output))) {
            outputWriter.write(originalSequence);
            outputWriter.write("\n");

            for (char[] read : reads) {
                outputWriter.write(read);
                outputWriter.write("\n");
            }

            outputWriter.flush();

            System.out.println("Generation complete");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
