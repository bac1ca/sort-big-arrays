package test.sort;

import static org.junit.Assert.*;

public class ArrayTest {

    @org.junit.Test
    public void testReadIndex() throws Exception {
        final int[] arr = {1, 2 ,3, 4, 5, 6, 7, 8};
        final FileChannelMock channel = new FileChannelMock(arr);

        final int BUF_SIZE = 4 * 4;
        final Array array = new Array(channel, BUF_SIZE);
        assertEquals(arr.length, array.size());

        int tmp[] = new int[arr.length];

        int count = 0;
        for (Array.ReadIndex i = array.newReadIndex(0);
             i.getIndex() < array.size(); i.increment()) {

            tmp[count++] = i.getValue();
        }
        assertArrayEquals(arr, tmp);
    }


    @org.junit.Test
    public void testReadIndex2() throws Exception {
        final int[] arr = {1, 2, 3};
        final FileChannelMock channel = new FileChannelMock(arr);

        final int BUF_SIZE = 4 * 2;
        final Array array = new Array(channel, BUF_SIZE);
        assertEquals(arr.length, array.size());

        int tmp[] = new int[arr.length];
        Array.ReadIndex i = array.newReadIndex(0);
        tmp[0] = i.getValue(); i.increment();
        tmp[1] = i.getValue(); i.increment();
        tmp[2] = i.getValue(); i.increment();
        assertArrayEquals(arr, tmp);

        try {
            assertEquals(3, i.getIndex());
            i.getValue();
            fail();
        } catch (ArrayIndexOutOfBoundsException correct) {}

        Array.ReadIndex j = array.newReadIndex(1);
        assertEquals(j.getValue(), 2); j.increment();
        assertEquals(j.getValue(), 3); j.increment();
    }

    @org.junit.Test
    public void testWriteIndex() throws Exception {
        final long size = 4 * 9;
        final FileChannelMock channel = new FileChannelMock(size);

        final int BUF_SIZE = 4 * 4;
        final Array array = new Array(channel, BUF_SIZE);

        Array.WriteIndex i = array.newWriteIndex(0);
        final int[] arr = {1, 2, 3, 4, 5, 6, 7, 8, 9};
        for (int a : arr) {
            i.putAndInrement(a);
        }
        i.close();
        assertEquals(9, i.getIndex());
        assertArrayEquals(arr, channel.dataInt());

        try {
            i.putAndInrement(10);
            fail();
        } catch (IndexOutOfBoundsException correct) {}
    }

    @org.junit.Test
    public void testWriteMiddle() throws Exception {
        final int[] data = {1, 1, 1, 1, 1};
        final FileChannelMock channel = new FileChannelMock(data);

        final int BUF_SIZE = 4 * 2;
        final Array array = new Array(channel, BUF_SIZE);

        Array.WriteIndex i = array.newWriteIndex(1);
        i.putAndInrement(5);
        i.putAndInrement(5);
        i.putAndInrement(5);
        i.close();

        final int[] arr = {1, 5, 5, 5, 1};
        assertEquals(4, i.getIndex());
        assertArrayEquals(arr, channel.dataInt());
    }

    @org.junit.Test
    public void testReadWrite() throws Exception {
        final long size = 4 * 7;
        final FileChannelMock channel = new FileChannelMock(size);

        final int BUF_SIZE = 4 * 3;
        final Array array = new Array(channel, BUF_SIZE);

        Array.WriteIndex i = array.newWriteIndex(0);
        Array.WriteIndex j = array.newWriteIndex(3);

        // write
        i.putAndInrement(1);
        i.putAndInrement(2);
        i.putAndInrement(3);
        j.putAndInrement(4);
        j.putAndInrement(5);
        j.putAndInrement(6);
        j.putAndInrement(7);
        i.close();
        j.close();

        // read
        final int[] arr = {1,2,3,4,5,6,7};
        final int[] tmp = new int[7];
        int count = 0;
        for (Array.ReadIndex k = array.newReadIndex(0);
             k.getIndex() < array.size(); k.increment()) {

            tmp[count++] = k.getValue();
        }
        assertArrayEquals(arr, tmp);
        assertArrayEquals(arr, channel.dataInt());
    }
}