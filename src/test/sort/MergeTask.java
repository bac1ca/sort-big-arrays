package test.sort;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class MergeTask implements Runnable {
    private final long lo;
    private final long mid;
    private final long hi;
    private final FileChannel arrCh;
    private final FileChannel auxCh;
    private final int chunkBytes;

    /**
     * @see #merge(FileChannel, FileChannel, int, long, long, long) operation
     */
    public MergeTask(FileChannel arrCh, FileChannel auxCh, int chunkBytes,
                     long lo, long mid, long hi) {
        this.lo  = lo;
        this.mid = mid;
        this.hi  = hi;
        this.arrCh = arrCh;
        this.auxCh = auxCh;
        this.chunkBytes = chunkBytes;
    }

    /**
     * Copies elements from one file channel to another
     * @param arrCh copy from arrCh
     * @param auxCh copy to   auxCh
     * @param chunkBytes chuck size in bytes
     * @param lo the index of the first element, inclusive, to be copied
     * @param hi the index of the last element, exclusive, to be copied
     * @throws IOException if any IO error occurs
     */
    static void copy(FileChannel arrCh, FileChannel auxCh,
                     int chunkBytes, long lo, long hi) throws IOException {

        ByteBuffer bb = ByteBuffer.allocate(chunkBytes);
        final long posLo = lo * Integer.BYTES;
        final long posHi = hi * Integer.BYTES;

        for (long pos = posLo; pos < posHi; pos += chunkBytes) {
            int limit = ((pos + chunkBytes) > posHi ? (int) (posHi - pos) : chunkBytes);

            bb.clear();
            bb.limit(limit);
            arrCh.read(bb, pos);

            bb.flip();
            auxCh.write(bb, pos);
        }
    }

    /**
     * Merges two part of already sorted elements
     * @param arrCh target file channel
     * @param auxCh auxiliary file channel
     * @param chunkBytes chuck size in bytes
     * @param lo the index of the first element, inclusive, to be merged
     * @param hi the index of the last element, exclusive, to be merged
     * @throws IOException if any IO error occurs
     */
    static void merge(FileChannel arrCh, FileChannel auxCh, int chunkBytes,
                      long lo, long mid, long hi) throws IOException {

        // copy subarray to auxiliary file
        copy(arrCh, auxCh, chunkBytes, lo, hi);

        // Merge back to arr[lo..hi].
        Array arr = new Array(arrCh, chunkBytes);
        Array aux = new Array(auxCh, chunkBytes);

        Array.WriteIndex k = arr.newWriteIndex(lo);
        Array.ReadIndex  i = aux.newReadIndex(lo);
        Array.ReadIndex  j = aux.newReadIndex(mid + 1);

        for (long n = lo; n < hi; n++) {
            if      (i.getIndex() > mid)          {k.putAndInrement(j.getValue()); j.increment(); }
            else if (j.getIndex() >= hi)          {k.putAndInrement(i.getValue()); i.increment(); }
            else if (j.getValue() < i.getValue()) {k.putAndInrement(j.getValue()); j.increment(); }
            else                                  {k.putAndInrement(i.getValue()); i.increment(); }
        }
        k.close();
    }

    @Override
    public void run() {
        try {
            merge(arrCh, auxCh, chunkBytes, lo, mid, hi);
        } catch (IOException ioe) {
            ioe.printStackTrace(System.err);
            throw new RuntimeException(ioe);
        }
    }
}
