package test.sort;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.*;
import java.util.Random;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.READ;
import static java.nio.file.StandardOpenOption.WRITE;

public class DataUtil {

    /**
     * Generates random array,
     * the sum of the elements equals 0
     *
     * @param size array size
     * @return random array
     */
    private static int[] nextArray(int size) {
        int[] result = new int[size];
        final Random r = new Random();
        for (int i = 0; i < size; ) {
            final int value = r.nextInt();
            result[i++] =  value;
            result[i++] = -value;
        }
        shuffle(result);
        return result;
    }

    private static void shuffle(int[] arr) {
        final Random r = new Random();

        for (int i = 0; i < arr.length; i++) {
            int randomIdx = r.nextInt(arr.length);
            int temp = arr[i];
            arr[i] = arr[randomIdx];
            arr[randomIdx] = temp;
        }
    }

    public static final int SIZE_1Kb   = 1;
    public static final int SIZE_10Kb  = 10;
    public static final int SIZE_1Mb   = 1024;
    public static final int SIZE_100Mb = 100 * 1024;
    public static final int SIZE_1Gb   = 1024 * 1024;

    /**
     * Generates file of random integers,
     * the sum of the elements equals 0
     * @param path path to output file
     * @param outSizeInKb size of generated file in Kb
     * @throws IOException
     */
    public static void generateArrayWithZeroSum(Path path, int outSizeInKb)
            throws IOException {
        if (outSizeInKb <= 0) throw new IllegalArgumentException();

        final int outSize = outSizeInKb * 1024;
        final int arrSize = 256;
        final int count = outSize / arrSize / 4; /* int = 4 byte */

        ByteBuffer byteBuff = ByteBuffer.allocate(4 * arrSize);
        try (FileChannel fc = (FileChannel.open(path, CREATE, WRITE))) {
            fc.position(0);
            for (int i = 0; i < count ; i++) {
                byteBuff.clear();  //make buffer ready for writing
                int[] array = nextArray(arrSize);
                IntBuffer intBuff = byteBuff.asIntBuffer();
                intBuff.put(array);
                intBuff.flip();
                while (byteBuff.hasRemaining()) {
                    fc.write(byteBuff);
                }
            }
        }
    }

    /**
     * Checks whether file sorted or not
     *
     * @param path to the file to be checked
     * @throws AssertionError if file is not sorted
     * @throws IOException
     */
    public static void checkArrayWithZeroSum(Path path) throws IOException {
        final long inSize = Files.size(path);
        final int arrSize = 128;
        final int count = (int) (inSize / arrSize / 4); /* int = 4 byte */

        int[] loArr = new int[arrSize];
        int[] hiArr = new int[arrSize];

        ByteBuffer bb = ByteBuffer.allocate(4 * arrSize);
        try (FileChannel fc = (FileChannel.open(path, READ))) {

            for (int i = 0; i < count / 2 ; i++) {

                final long pos = i * arrSize * 4;

                bb.clear();
                fc.read(bb, pos);
                bb.flip();
                bb.asIntBuffer().get(loArr);

                bb.clear();
                fc.read(bb, inSize - pos - arrSize * 4);
                bb.flip();
                bb.asIntBuffer().get(hiArr);

                assert loArr.length == hiArr.length;
                for (int j = 0; j < loArr.length; j++) {
                    int lo = loArr[j];
                    int hi = hiArr[loArr.length - (j + 1)];
                    //System.out.println("[" + lo + ", " + hi + "]");
                    if (-lo != hi) {
                        throw new AssertionError(lo + " : " + hi);
                    }
                }
            }
        }
    }

    /**
     * Generates [4,3,2,1,4,3,2,1,4,3,2,1,4,3,2,1] and writes it to file
     * @param file output file
     * @throws IOException
     */
    public static void generateSimpleArray(Path file) throws IOException {
        try (FileChannel fc = (FileChannel.open(file, CREATE, WRITE))) {
            fc.position(0);

            ByteBuffer bb = ByteBuffer.allocate(4 * 16);

            bb.putInt(4).putInt(3).putInt(2).putInt(1);
            bb.putInt(4).putInt(3).putInt(2).putInt(1);
            bb.putInt(4).putInt(3).putInt(2).putInt(1);
            bb.putInt(4).putInt(3).putInt(2).putInt(1);

            bb.flip();
            while (bb.hasRemaining()) {
                fc.write(bb);
            }
        }
    }

    /**
     * Reads file to head as integer array
     * @param file file name
     * @return integer array
     * @throws IllegalArgumentException if file size is incorrect
     * @throws IOException
     */
    public static int[] readArray(Path file) throws IOException {
        try (FileChannel fc = (FileChannel.open(file, READ))) {
            if (fc.size() % 4 != 0) throw new IllegalArgumentException();

            Array array = new Array(fc, 1024);
            int[] result = new int[(int) array.size()];

            int j = 0;
            for (Array.ReadIndex i = array.newReadIndex(0);
                 i.getIndex() < array.size(); i.increment()) {
                 result[j++] = i.getValue();
            }
            return result;
        }
    }

}
