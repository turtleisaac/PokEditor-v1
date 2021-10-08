package framework;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class CsvReader
{
    private final String[][] in;
    private int line;

    public CsvReader(String filePath) throws IOException
    {
        in= getData(filePath,2,1);
        line= 0;
    }

    public CsvReader(String filePath, int firstX, int firstY) throws IOException
    {
        in= getData(filePath,firstX,firstY);
        line= 0;
    }

    private String[][] getData(String filePath, int firstX, int firstY) throws IOException
    {
        ArrayList<String> fileLines= new ArrayList<>();
        BufferedReader reader= new BufferedReader(new FileReader(filePath));
        String line;
        while((line= reader.readLine()) != null)
        {
            fileLines.add(line);
        }
        reader.close();
        for(; firstY != 0; firstY--)
        {
            fileLines.remove(0);
        }


        String[][] fileData= new String[fileLines.size()][];
        int x;
        for(int i= 0; i < fileLines.size(); i++)
        {
            x= firstX;
            String thisLine= fileLines.get(i);
            for(; x != 0; x--)
            {
                thisLine= thisLine.substring(thisLine.indexOf(",")+1);
            }
            thisLine= thisLine.replaceAll("×","x");
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

    public void print()
    {
        for(String[] arr : in)
        {
            System.out.println(Arrays.toString(arr));
        }
    }
}
