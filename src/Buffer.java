import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Buffer {

    private static final int INITIAL_SIZE = 1024*64;

    private byte[] bytes = new byte[1024*64];
    private int position = 0;
    private int truePosition= 0;
    private int limit = 0;
    private String file;
    private final BufferedInputStream in;
    private boolean endOfFile;


    public Buffer(String file) {
        this.file= file;
        this.endOfFile = false;
        try {
            in = new BufferedInputStream(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

    }

    public int readInt() {
        require(4);

        int ret = readByte();
        ret |= readByte() << 8;
        ret |= readByte() << 16;
        ret |= readByte() << 24;

        return ret;
    }

    public long readUIntI() {
        return (long)readInt() & 0xffffffffL;
    }
    public long readUIntS() { return (long)readShort() & 0xffffL; }

    public long readLong() {
        require(8);

        long ret = readByte();
        ret |= readByte() << 8;
        ret |= readByte() << 16;
        ret |= readByte() << 24;
        ret |= (long)readByte() << 32;
        ret |= (long)readByte() << 40;
        ret |= (long)readByte() << 48;
        ret |= (long)readByte() << 56;

        return ret;
    }

    public short readShort() {
        require(2);

        int ret = readByte() | (readByte() << 8);

        return (short)ret;
    }

    public short[] readShorts(int size) {
        short[] ret= new short[size];
        for(int i= 0; i < size; i++){
            ret[i]= readShort();
        }
        return ret;
    }

    public String readString(int size) {
        require(size);
        String ret = new String(bytes, position, size);
        position += size;
        truePosition+= size;
        return ret;
    }

    public byte[] readBytes(int size) {

        byte[] ret = new byte[size];
        int offset = 0;
        while (size > 0) {
            int toRead = Math.min(size, bytes.length);
            require(toRead);
            System.arraycopy(bytes, position, ret, offset, toRead);
            position += toRead;
            offset += toRead;
            truePosition+= toRead;
            size -= toRead;
        }

        return ret;
    }
    public int readByte() {
        require(1);
        truePosition++;
        return bytes[position++] & 0xff;
    }

    public int getPosition()
    {
        return truePosition;
    }

    private void require(int size) {
        if (limit - position < size) {
            refill();
            if (limit - position < size) {
                throw new RuntimeException("Want "+size+" bytes but only "+limit+" bytes remain");
            }
        }
    }

    private void refill() {
        int remaining = limit - position;
        System.arraycopy(bytes, position, bytes, 0, remaining);
        position = 0;
        limit = remaining;

        int free = bytes.length - remaining;
        int read = 0;
        try {
            read = in.read(bytes, remaining, free);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (read == -1) {
            endOfFile = true;
        }
        else
        {
            limit += read;
        }
    }

    public void close() throws IOException
    {
        in.close();
    }

    public String getFile()
    {
        return file;
    }

    public boolean endOfFile() {
        return endOfFile;
    }

    public void skipBytes(int bytes) {
        byte[] throwAway= readBytes(bytes);
    }

    public void skipTo(int offset)
    {
        if(offset < truePosition) {
            throw new RuntimeException("Already beyond this offset");
        }
        byte[] throwAway= readBytes(offset-truePosition);
    }
}
