package test.sort;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static test.sort.DataUtil.*;

public class PerfTest {

    @org.junit.Test
    public void perfTests() throws Exception {
        testGenerateAndSort10Kb();
        testGenerateAndSort1Mb();
        testGenerateAndSort100Mb();
        testGenerateAndSort1Gb();
    }

    void testGenerateAndSort10Kb() throws Exception {
        final Path pathIn = Paths.get("in.bin");
        final int chunkSize = 128;

        try {
            DataUtil.generateArrayWithZeroSum(pathIn, SIZE_10Kb);
            sort(pathIn, 1, chunkSize);
            sort(pathIn, 2, chunkSize);
            sort(pathIn, 4, chunkSize);
            sort(pathIn, 8, chunkSize);
            sort(pathIn, 16, chunkSize);
            sort(pathIn, 32, chunkSize);
        } finally {
            Files.delete(pathIn);
        }
    }

    void testGenerateAndSort1Mb() throws Exception {
        final Path pathIn = Paths.get("in.bin");
        final int chunkSize = 32 * 1024;

        try {
            DataUtil.generateArrayWithZeroSum(pathIn, SIZE_1Mb);
            sort(pathIn, 1, chunkSize);
            sort(pathIn, 2, chunkSize);
            sort(pathIn, 4, chunkSize);
            sort(pathIn, 8, chunkSize);
            sort(pathIn, 16, chunkSize);
            sort(pathIn, 32, chunkSize);
        } finally {
            Files.delete(pathIn);
        }
    }

    void testGenerateAndSort100Mb() throws Exception {
        final Path pathIn = Paths.get("in.bin");
        final int chunkSize = 4 * 1024 * 1024;

        try {
            DataUtil.generateArrayWithZeroSum(pathIn, SIZE_100Mb);
            sort(pathIn, 1, chunkSize);
            sort(pathIn, 2, chunkSize);
            sort(pathIn, 4, chunkSize);
            sort(pathIn, 8, chunkSize);
            sort(pathIn, 16, chunkSize);
            sort(pathIn, 32, chunkSize);
        } finally {
            Files.delete(pathIn);
        }
    }

    //result:
    //  - env: old MackBook Air, CPU: 4core, worker threads: 4, JDK 1.8.0_20
    //  - file size: 1Gb, chuck size: 32Mb
    //  - sort time: 1m 54s
    void testGenerateAndSort1Gb() throws Exception {
        final Path pathIn = Paths.get("in.bin");
        final int chunkSize = 32 * 1024 * 1024;

        try {
            DataUtil.generateArrayWithZeroSum(pathIn, SIZE_1Gb);
            sort(pathIn,  1, chunkSize);
            sort(pathIn,  2, chunkSize);
            sort(pathIn,  4, chunkSize);
            sort(pathIn,  8, chunkSize / 2);
            sort(pathIn, 16, chunkSize / 4);
            sort(pathIn, 32, chunkSize / 8);
        } finally {
            Files.delete(pathIn);
        }
    }

    private void sort(Path pathIn, int threads, int chunkSize) throws IOException {
        final Path pathOut = Paths.get("out.sorted.bin");
        final Path pathAux = Paths.get("out.aux.bin");
        try {
            // sort
            Main.sort(threads, chunkSize, pathIn, pathOut, pathAux);
            // uncomment to check sort correctness
            //DataUtil.checkArrayWithZeroSum(pathOut);
        } finally {
            Files.delete(pathOut);
            Files.delete(pathAux);
        }
    }

}