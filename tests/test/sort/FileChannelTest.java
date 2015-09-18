package test.sort;

import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.READ;
import static java.nio.file.StandardOpenOption.WRITE;
import static org.junit.Assert.*;

public class FileChannelTest {

    @org.junit.Test
    public void testInit() throws Exception {
        final long size = 4 * 31;
        FileChannelMock channel = new FileChannelMock(size);
        assertEquals(size, channel.size());
    }

    @org.junit.Test
    public void testFileChannel() throws Exception {
        final Path path = Paths.get("test.bin");
        try (FileChannel channel = (FileChannel.open(path, CREATE, READ, WRITE))) {
            testReadWriteChannel(channel, 4 * 4);
        }
        Files.delete(path);
    }

    @org.junit.Test
    public void testFileChannelMock() throws Exception {
        final long size = 4 * 8;
        FileChannelMock channel = new FileChannelMock(size);
        testReadWriteChannel(channel, 4 * 4);
    }

    @org.junit.Test
    public void testFileChannelMock2() throws Exception {
        final long size = 4 * 8;
        FileChannelMock channel = new FileChannelMock(size);
        testReadWriteChannel(channel, 4 * 5);
    }

    void testReadWriteChannel(FileChannel channel,
                              int bufferSize) throws Exception {
        ByteBuffer bb = ByteBuffer.allocate(bufferSize);
        bb.putInt(1).putInt(2).putInt(3).putInt(4);

        bb.flip();
        assertEquals(4 * 4, channel.write(bb, 0));

        bb.clear();
        bb.putInt(5).putInt(6).putInt(7).putInt(8);

        bb.flip();
        final long position = 4 * 4;
        assertEquals(4 * 4, channel.write(bb, position));

        // read
        bb = ByteBuffer.allocate(4 * 8);
        assertEquals(32, channel.read(bb, 0));

        bb.flip();
        int data[] = new int[8];
        bb.asIntBuffer().get(data);

        int arr[] = {1, 2, 3, 4, 5, 6, 7, 8};
        assertArrayEquals(arr, data);
    }

}