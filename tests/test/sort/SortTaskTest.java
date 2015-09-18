package test.sort;

import static org.junit.Assert.assertArrayEquals;

public class SortTaskTest {

    @org.junit.Test
    public void testSort() throws Exception {
        int[] data = {4,3,2,1,4,3,2,1};
        FileChannelMock in = new FileChannelMock(data);
        FileChannelMock out = new FileChannelMock(4 * 8);

        SortTask.sort(in, out, 0, 4);
        int[] result = {1,2,3,4,0,0,0,0};
        assertArrayEquals(result, out.dataInt());

        SortTask.sort(in, out, 0, 4);
        SortTask.sort(in, out, 4, 8);
        int[] result2 = {1,2,3,4,1,2,3,4};
        assertArrayEquals(result2, out.dataInt());
        assertArrayEquals(data, in.dataInt());
    }

    @org.junit.Test
    public void testSortMiddle() throws Exception {
        int[] data = {4,3,2,1,4,3,2,1};
        FileChannelMock in = new FileChannelMock(data);
        FileChannelMock out = new FileChannelMock(4 * 8);

        int[] result = {0,0,1,2,3,4,0,0};

        SortTask.sort(in, out, 2, 6);
        assertArrayEquals(result, out.dataInt());
        SortTask.sort(in, out, 2, 6);
        assertArrayEquals(result, out.dataInt());
    }

}