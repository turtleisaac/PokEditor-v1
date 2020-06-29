public class Data
{
    private int integer;
    private String string;
    private short numShort;
    private byte numByte;
    private String type;



    public Data(int x)
    {
        integer= x;
        type= "int";
    }

    public Data(String x)
    {
        string= x;
        type= "String";
    }

    public Data(short x)
    {
        numShort= x;
        type= "short";
    }

    public Data(byte x)
    {
        numByte= x;
        type= "byte";
    }

    public int getInt()
    {
        return integer;
    }

    public String getString()
    {
        return string;
    }

    public short getShort()
    {
        return numShort;
    }

    public byte getByte()
    {
        return numByte;
    }



}
