package test.sort;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.Assert.*;

public class SortMockedTest {

    final int CPUs = Runtime.getRuntime().availableProcessors();

    @org.junit.Test
    public void testOneElementArray() throws Exception {
        final ExecutorService executor = Executors.newFixedThreadPool(CPUs);

        final int[] data = {1};
        final int[] copy = Arrays.copyOf(data, data.length);

        final FileChannelMock in = new FileChannelMock(data);
        final FileChannelMock out = new FileChannelMock(data.length * 4);
        final FileChannelMock aux = new FileChannelMock(data.length * 4);

        Sort.sort(executor, 4, in, out, aux);
        Arrays.sort(copy);

        assertArrayEquals(data, in.dataInt());
        assertArrayEquals(copy, out.dataInt());

        executor.shutdown();
    }

    @org.junit.Test
    public void testSort() throws Exception {
        final ExecutorService executor = Executors.newFixedThreadPool(CPUs);

        final int[] data = {9,8,7,6,5,4,3,2,1};
        final int[] copy = Arrays.copyOf(data, data.length);

        final FileChannelMock in = new FileChannelMock(data);
        final FileChannelMock out = new FileChannelMock(data.length * 4);
        final FileChannelMock aux = new FileChannelMock(data.length * 4);

        Sort.sort(executor, 4, in, out, aux);
        Arrays.sort(copy);

        assertArrayEquals(data, in.dataInt());
        assertArrayEquals(copy, out.dataInt());

        executor.shutdown();
    }

    @org.junit.Test
    public void testSortRandomArrays() throws Exception {
        final ExecutorService executor = Executors.newFixedThreadPool(CPUs);
        final Random r = new Random();

        for (int i = 128; i <= 512; i++) {
            System.out.println(i);

            final int[] data = randomArray(i);
            final int[] copy = Arrays.copyOf(data, data.length);

            final FileChannelMock in = new FileChannelMock(data);
            final FileChannelMock out = new FileChannelMock(data.length * 4);
            final FileChannelMock aux = new FileChannelMock(data.length * 4);

            final int chunkSize = 8 * 1024;
            Sort.sort(executor, chunkSize, in, out, aux);
            Arrays.sort(copy);
            assertArrayEquals(data, in.dataInt());
            assertArrayEquals(copy, out.dataInt());
        }
        executor.shutdown();
    }

    @org.junit.Test
    public void testSortBigArray() throws Exception {
        final ExecutorService executor = Executors.newFixedThreadPool(CPUs);

        final int[] data = randomArray(1024 * 1024 * 32);
        final int[] copy = Arrays.copyOf(data, data.length);

        final FileChannelMock in = new FileChannelMock(data);
        final FileChannelMock out = new FileChannelMock(data.length * 4);
        final FileChannelMock aux = new FileChannelMock(data.length * 4);

        final int chunkSize = 32 * 1024;
        Sort.sort(executor, chunkSize, in, out, aux);
        Arrays.sort(copy);
        assertArrayEquals(data, in.dataInt());
        assertArrayEquals(copy, out.dataInt());

        executor.shutdown();
    }

    private static int[] randomArray(int len) {
        final Random r = new Random();
        byte[] data = new byte[len * 4];
        int[] result = new int[len];

        r.nextBytes(data);
        ByteBuffer bb = ByteBuffer.wrap(data);
        bb.asIntBuffer().get(result);
        return result;
    }
}