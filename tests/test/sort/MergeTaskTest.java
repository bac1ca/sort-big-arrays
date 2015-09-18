package test.sort;

import java.util.Arrays;

import static org.junit.Assert.*;

public class MergeTaskTest {

    @org.junit.Test
    public void testCopy() throws Exception {
        final long size = 4 * 8;
        int[] data = {1,2,3,4,5,6,7,8};
        FileChannelMock arrCh = new FileChannelMock(data);
        FileChannelMock auxCh = new FileChannelMock(size);

        int bufferSize = 4 * 3;
        MergeTask.copy(arrCh, auxCh, bufferSize, 2, 6);

        int[] copy = {0,0,3,4,5,6,0,0};
        assertArrayEquals(data, arrCh.dataInt());
        assertArrayEquals(copy, auxCh.dataInt());
    }

    @org.junit.Test
    public void testMergeAlreadySorted() throws Exception {
        final long size = 4 * 9;
        int[] data = {1,2,3,4,5,6,7,8,9};
        FileChannelMock arrCh = new FileChannelMock(data);
        FileChannelMock auxCh = new FileChannelMock(size);

        int bufferSize = 4 * 3;
        MergeTask.merge(arrCh, auxCh, bufferSize, 0, 4, 9);

        assertArrayEquals(data, arrCh.dataInt());
        assertArrayEquals(data, auxCh.dataInt());
    }

    @org.junit.Test
    public void testMerge() throws Exception {
        final long size = 4 * 6;
        int[] data = {1,3,5,  2,4,6};
        int[] copy = Arrays.copyOf(data, data.length);
        FileChannelMock arrCh = new FileChannelMock(data);
        FileChannelMock auxCh = new FileChannelMock(size);

        int bufferSize = 4 * 2;
        MergeTask.merge(arrCh, auxCh, bufferSize, 0, 2, 6);

        int[] res = {1,2,3,4,5,6};
        assertArrayEquals(res, arrCh.dataInt());
        assertArrayEquals(copy, auxCh.dataInt());
    }
}