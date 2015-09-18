package test.sort;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.Arrays;

/**
 * Mock class for FileChannel,
 * overloads methods: size(), read(buf, pos), write(buf, pos), toString()
 * and provide additional: dataInt()
 */
public class FileChannelMock extends FileChannel {

    private final long size;
    private final int[] data;

    public FileChannelMock(int[] data) {
        this.size = data.length * 4;
        this.data = data;
    }

    public FileChannelMock(long size) {
        assert (size % 4) == 0;
        this.size = size;
        data = new int[(int) (size / 4)];
    }

    @Override
    public long size() throws IOException {
        return size;
    }

    @Override
    public int read(ByteBuffer dst, long position) throws IOException {
        final int idxInit = (int) (position / 4);
        int idx = idxInit;

        while (dst.position() < dst.limit()) {
            if (idx == data.length) {
                break;
            }
            dst.putInt(data[idx++]);
        }
        int readBytes = (idx - idxInit) * 4;
        return readBytes;
    }

    @Override
    public int write(ByteBuffer src, long position) throws IOException {
        final int idxInit = (int) (position / 4);
        int idx = idxInit;

        while (src.hasRemaining()) {
            data[idx++] = src.getInt();
        }
        int writeBytes = (idx - idxInit) * 4;
        return writeBytes;
    }

    @Override
    public String toString() {
        return Arrays.toString(data);
    }

    /**
     * Returns contents of the FileChannel as integer array
     * @return contents of the FileChannel as integer array
     */
    public int[] dataInt() {
        return data;
    }

    // --------------------------------------------
    // NOT SUPPORTED FOR TESTS
    // --------------------------------------------

    @Override
    public long position() throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public FileChannel position(long newPosition) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public int read(ByteBuffer dst) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public int write(ByteBuffer src) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public long read(ByteBuffer[] dsts, int offset, int length) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public long write(ByteBuffer[] srcs, int offset, int length) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public FileChannel truncate(long size) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void force(boolean metaData) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public long transferTo(long position, long count, WritableByteChannel target) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public long transferFrom(ReadableByteChannel src, long position, long count) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public MappedByteBuffer map(MapMode mode, long position, long size) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public FileLock lock(long position, long size, boolean shared) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public FileLock tryLock(long position, long size, boolean shared) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void implCloseChannel() throws IOException {
        throw new UnsupportedOperationException();
    }
}
