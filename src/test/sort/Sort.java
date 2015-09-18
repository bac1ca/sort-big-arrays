package test.sort;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class Sort {

    /**
     * Core of sort algorithm.
     * It
     *  0. divides initial file to set of part;
     *  1. sorts each part (in heap) independently and concurrently;
     *  2. merges couples of already sorted part concurrently.
     *
     * @param executor executor service
     * @param chunkBytes size of chunk in bytes (multiple of four)
     * @param in source file-channel
     * @param out destination file-channel
     * @param aux auxiliary file channel
     * @throws IOException if any IO error occurs
     */
    static void sort(ExecutorService executor, int chunkBytes,
        FileChannel in, FileChannel out, FileChannel aux) throws IOException {

        long size = in.size();
        assert size % 4 == 0;
        assert chunkBytes % 4 == 0;
        final long N = size / 4;
        final int chunkSize = chunkBytes / 4;

        // partial sort
        System.out.println("partial sort...");
        List<Future<?>> futures = new ArrayList<>();
        for (long sz = 0; sz < N; sz += chunkSize) {
            Future<?> future = executor.submit(
                new SortTask(in, out, sz, Math.min(sz + chunkSize, N)));
            futures.add(future);
        }
        awaitForFinish(futures);
        System.out.println("partial sort finished...");


        // merge
        System.out.println("merge ...");
        futures.clear();
        for (int sz = chunkSize; sz < N; sz = sz + sz) {
            for (int lo = 0; lo < N - sz; lo += sz + sz) {
                Future<?> future = executor.submit(
                        new MergeTask(out, aux, chunkBytes,
                            lo, lo + sz - 1, Math.min(lo + sz + sz, N)));
                futures.add(future);
            }
            awaitForFinish(futures);
        }
        System.out.println("merge finished ...");
    }

    private static void awaitForFinish(List<Future<?>> futures) {
        for (Future<?> f : futures) {
            try {
                f.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace(System.err);
                throw new RuntimeException(e);
            }
        }
    }

}
