package test.sort;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * This class represents Array abstraction on {@link FileChannel}.
 * It provides {@link Array.ReadIndex} for sequential access to Array elements
 * and {@link Array.WriteIndex} to write to array file sequentially.
 */
public class Array {

    private final long size;
    private final FileChannel channel;
    private final int chunkBytes;

    public Array(FileChannel channel, int chunkBytes) throws IOException {
        final long sz = channel.size();
        assert (sz % 4) == 0;
        assert (chunkBytes % 4) == 0;
        size = sz / 4;
        this.channel = channel;
        this.chunkBytes = chunkBytes;
    }

    public long size() {
        return size;
    }

    public ReadIndex newReadIndex(long idx) throws IOException {
        return new ReadIndex(idx);
    }

    public WriteIndex newWriteIndex(long idx) throws IOException {
        return new WriteIndex(idx);
    }

    public class WriteIndex {
        private long idx = -1;
        private ByteBuffer buf = ByteBuffer.allocate(chunkBytes);

        public WriteIndex(long idx) throws IOException {
            this.idx = idx;
        }

        public long getIndex() {
            return idx;
        }

        public void putAndInrement(int val) throws IOException {
            if (idx >= size) throw new ArrayIndexOutOfBoundsException();

            buf.putInt(val);
            idx++;

            if (buf.position() == buf.limit()) {
                flushBuffer();
            }
        }

        public void close() throws IOException {
            flushBuffer();
        }

        private void flushBuffer() throws IOException {
            buf.flip();
            final long position = idx * 4 - buf.limit();
            channel.write(buf, position);
            buf.clear();
        }

    }

    public class ReadIndex {
        private long idx = -1;
        private ByteBuffer buf = ByteBuffer.allocate(chunkBytes);

        private int cacheValue = -1;

        public ReadIndex(long idx) throws IOException {
            this.idx = idx;
            updateBuffer(idx);
            cacheValue = buf.getInt();
        }

        public int getValue() {
            if (idx < size) {
                return cacheValue;
            }
            throw new ArrayIndexOutOfBoundsException();
        }

        public long getIndex() {
            return idx;
        }

        public long increment() throws IOException {
            idx++;
            if (idx >= size) {
                return idx;
            }
            if (buf.position() == buf.limit()) {
                updateBuffer(idx);
            }
            cacheValue = buf.getInt();
            return idx;
        }

        private void updateBuffer(long idx) throws IOException {
            buf.clear();
            final long position = idx * 4;
            channel.read(buf, position);
            buf.flip();
        }

    }

}
