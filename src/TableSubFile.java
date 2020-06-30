
public interface TableSubFile
{
    int getStartingOffset();
    int getEndingOffset();
    byte[] getFileContents();
    int length();

}
