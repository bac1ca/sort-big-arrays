package test.sort;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.READ;
import static java.nio.file.StandardOpenOption.WRITE;

public class Main {

    public static final int DEFAULT_CHUNK_SIZE = 4 * 1024 * 1024; // 4Mb

    public static void main(String[] args) throws IOException {
        // parse args
        final int threads;
        final Path pathIn;
        final Path pathOut;
        final Path pathAux;
        final int chunkSize;

        try {
            threads = Integer.parseInt(args[0]);
            pathIn  = Paths.get(args[1]);
            if (!Files.exists(pathIn)) {
                throw new IllegalArgumentException();
            }
            pathOut = Paths.get(args[2]);
            pathAux = Paths.get("tmp-sort.bin");
            chunkSize = args.length == 4 ?
                Integer.parseInt(args[3]) * 1024 : DEFAULT_CHUNK_SIZE;

        } catch (RuntimeException e) {
            printHelp();
            return;
        }

        System.out.println("threads      = " + threads);
        System.out.println("chunk (byte) = " + chunkSize);
        System.out.println("input  file  = " + pathIn);
        System.out.println("output file  = " + pathOut);

        sort(threads, chunkSize, pathIn, pathOut, pathAux);
        Files.delete(pathAux);
    }

    static void sort(int threads, int chunkSize, Path pathIn,
                     Path pathOut, Path pathAux) throws IOException {

        final ExecutorService executor = Executors.newFixedThreadPool(threads);

        // open file channels and sort
        try (FileChannel in  = (FileChannel.open(pathIn, READ));
             FileChannel out = (FileChannel.open(pathOut, CREATE, READ, WRITE));
             FileChannel aux = (FileChannel.open(pathAux, CREATE, READ, WRITE))) {

            final long size = in.size();
            if (size % 4 != 0) {
                throw new IllegalArgumentException("file size should be multiple of four.");
            }

            long time = System.currentTimeMillis();
            Sort.sort(executor, chunkSize, in, out, aux);

            System.out.println("size (Kb): " + (size / 1024) +
                    ", threads: " + threads + ", chunk (byte): " + chunkSize +
                    ", sort time (ms): " + (System.currentTimeMillis() - time));
        } finally {
            executor.shutdown();
        }
    }

    private static void printHelp() {
        final String help =
            "invalid arguments, usage: \n" +
            "  Sort <threads number> <input file> <output file> [chunk_size]\n" +
            "    - [chunk size] in Kb, optional parameter";
        System.out.println(help);
    }
}
