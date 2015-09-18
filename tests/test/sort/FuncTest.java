package test.sort;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.fail;
import static test.sort.DataUtil.*;

public class FuncTest {

    @org.junit.Test
    public void testGenerateAndSort1Kb() throws Exception {
        final int threads = Runtime.getRuntime().availableProcessors();
        final Path pathIn  = Paths.get("OneKb.bin");
        final Path pathOut = Paths.get("OneKb.sorted.bin");
        final Path pathAux = Paths.get("OneKb.aux.bin");;
        final int chunkSize = 128;

        generateAndSort(SIZE_1Kb, threads, pathIn, pathOut, pathAux, chunkSize);
    }

    @org.junit.Test
    public void testGenerateAndSort1Mb() throws Exception {
        final int threads = Runtime.getRuntime().availableProcessors();
        final Path pathIn  = Paths.get("OneMb.bin");
        final Path pathOut = Paths.get("OneMb.sorted.bin");
        final Path pathAux = Paths.get("OneMb.aux.bin");;
        final int chunkSize = 128 * 1024;

        generateAndSort(SIZE_1Mb, threads, pathIn, pathOut, pathAux, chunkSize);
    }

    @org.junit.Test
    public void testGenerateAndSort10Mb() throws Exception {
        final int threads = Runtime.getRuntime().availableProcessors();
        final Path pathIn  = Paths.get("One10Mb.bin");
        final Path pathOut = Paths.get("One10Mb.sorted.bin");
        final Path pathAux = Paths.get("One10Mb.aux.bin");;
        final int chunkSize = 1024 * 1024;

        generateAndSort(SIZE_1Mb * 10, threads, pathIn, pathOut, pathAux, chunkSize);
    }

    //result:
    //  - env: old MackBook Air, CPU: 4core, JDK 1.8.0_20
    //  - file size: 1Gb, chuck size: 32Mb
    //  - sort time: 1m 58s
    @org.junit.Test
//    public void testGenerateAndSort1Gb() throws Exception {
//        final int threads = Runtime.getRuntime().availableProcessors();
//        final Path pathIn  = Paths.get("OneGb.bin");
//        final Path pathOut = Paths.get("OneGb.sorted.bin");
//        final Path pathAux = Paths.get("OneGb.aux.bin");;
//        final int chunkSize = 32 * 1024 * 1024;
//
//        generateAndSort(SIZE_1Gb, threads, pathIn, pathOut, pathAux, chunkSize);
//    }

    private void generateAndSort(int sizeInKb, int threads,
                                 Path pathIn, Path pathOut,
                                 Path pathAux, int chunkSize) throws IOException {
        if (!Files.exists(pathIn)) {
            DataUtil.generateArrayWithZeroSum(pathIn, sizeInKb);
        }
        // check that generated array is not sorted
        try {
            DataUtil.checkArrayWithZeroSum(pathIn);
            fail();
        } catch (AssertionError correct) {}

        // sort and check that output files is sorted
        Main.sort(threads, chunkSize, pathIn, pathOut, pathAux);
        DataUtil.checkArrayWithZeroSum(pathOut);

        Files.delete(pathIn);
        Files.delete(pathOut);
        Files.delete(pathAux);
    }

    @org.junit.Test
    public void testGenerateAndSortSimple() throws Exception {
        final int threads = Runtime.getRuntime().availableProcessors();
        final Path pathIn  = Paths.get("Simple.bin");
        final Path pathOut = Paths.get("Simple.sorted.bin");
        final Path pathAux = Paths.get("Simple.aux.bin");;
        final int chunkSize = 1024 * 1024;

        if (!Files.exists(pathIn)) {
            DataUtil.generateSimpleArray(pathIn);
        }

        Main.sort(threads, chunkSize, pathIn, pathOut, pathAux);
        int[] tmp = {1,1,1,1, 2,2,2,2, 3,3,3,3, 4,4,4,4};
        int[] arr = DataUtil.readArray(pathOut);

        assertArrayEquals(tmp, arr);

        Files.delete(pathIn);
        Files.delete(pathOut);
        Files.delete(pathAux);
    }

}