import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class CsvReader
{
    private final String[][] in;
    private int line;

    public CsvReader(String filePath) throws IOException
    {
        in= getData(filePath);
        line= 0;
    }

    private String[][] getData(String filePath) throws IOException
    {
        ArrayList<String> fileLines= new ArrayList<>();
        BufferedReader reader= new BufferedReader(new FileReader(filePath));
        String line;
        while((line= reader.readLine()) != null)
        {
            fileLines.add(line);
        }
        reader.close();
        fileLines.remove(0);

        String[][] fileData= new String[fileLines.size()][];
        for(int i= 0; i < fileLines.size(); i++)
        {
            String thisLine= fileLines.get(i);
            thisLine= thisLine.substring(thisLine.indexOf(",")+1);
            thisLine= thisLine.substring(thisLine.indexOf(",")+1);
            fileData[i]= thisLine.split(",");
        }
        return fileData;
    }

    public String[] next()
    {
        if(line == in.length)
        {
            return null;
        }
        else
        {
            return in[line++];
        }
    }

    public int length()
    {
        return in.length;
    }

    public void skipLine() {
        next();
    }

    public String[][] getCsv()
    {
        return in;
    }
}
