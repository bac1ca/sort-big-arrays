package test.sort;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;

public class SortTask implements Runnable {
    private final long lo;
    private final long hi;
    private final FileChannel in;
    private final FileChannel out;

    /**
     * @see #sort(FileChannel, FileChannel, long, long) operation
     */
    public SortTask(FileChannel in, FileChannel out, long lo, long hi) {
        this.lo = lo;
        this.hi = hi;
        this.in = in;
        this.out = out;
    }

    /**
     * Copies part of input file-channel, sorts and writes
     * to destination file-channel
     *
     * @param in source file-channel (unsorted)
     * @param out
     * @param lo the index of the first element, inclusive, to be sorted
     * @param hi the index of the last element, exclusive, to be sorted
     * @throws IOException if any IO error occurs
     */
    static void sort(FileChannel in, FileChannel out, long lo, long hi)
        throws IOException {
        if (lo < 0 || hi <= lo) throw new IllegalArgumentException();

        final int size = (int) (hi - lo);
        final int[] arr = new int[size];
        final int sizeInBytes = size * Integer.BYTES;
        final long position = lo * Integer.BYTES;

        // read part of file
        final ByteBuffer bb = ByteBuffer.allocate(sizeInBytes);
        in.read(bb, position);

        bb.flip();
        bb.asIntBuffer().get(arr);

        // sort
        Arrays.sort(arr);

        // write to file
        bb.clear();
        for (int a : arr) {
            bb.putInt(a);
        }
        bb.flip();
        out.write(bb, position);

        if (bb.hasRemaining()) throw new IllegalStateException();
    }

    @Override
    public void run() {
        try {
            sort(in, out, lo, hi);
        } catch (IOException ioe) {
            ioe.printStackTrace(System.err);
            throw new RuntimeException(ioe);
        }
    }
}
