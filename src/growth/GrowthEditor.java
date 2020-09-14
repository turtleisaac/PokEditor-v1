package growth;

import framework.Buffer;
import framework.CsvReader;

import java.io.*;
import java.util.*;

public class GrowthEditor
{
    private static String path= System.getProperty("user.dir") + File.separator; //creates a new String field containing user.dir and File.separator (/ on Unix systems, \ on Windows)
    private String dataPath= path;
    private static final String[] typeArr= {"Normal", "Fighting", "Flying", "Poison", "Ground", "Rock", "Bug", "Ghost", "Steel", "Fairy", "Fire", "Water","Grass","Electric","Psychic","Ice","Dragon","Dark"};
    private static final String[] evolutionMethodArr= {"None","Happiness","Happiness (Day)","Happiness (Night)","Level Up","Trade","Trade (Item)","Use Item","Level (Attack > Defense)","Level (Attack = Defense)","Level (Attack < Defense)","Level (PID > 5)","Level (PID < 5)","Level (1 of 2)","Level (2 of 2)","Max Beauty","Use Item (Male)","Use Item (Female)","Use Item (Day)","Use Item (Night)","Attack Known","Pokemon in Party","Level (Male)","Level (Female)","Level (Mt. Coronet)","Level (Eterna Forest)","Level (Route 217)"};
    private static final String[] growthTableIdArr= {"Medium Fast","Erratic","Fluctuating","Medium Slow","Fast","Slow","Medium Fast","Medium Fast"};
    private static String resourcePath= path + "Program Files" + File.separator;
    private static String[] nameData;

    public GrowthEditor() throws IOException
    {
        BufferedReader reader= new BufferedReader(new FileReader(resourcePath + "EntryData.txt"));
        ArrayList<String> nameList= new ArrayList<>();
        String line;
        while((line= reader.readLine()) != null)
        {
            line= line.trim();
            nameList.add(line);
        }
        nameData= nameList.toArray(new String[0]);
        reader.close();
    }

    public void growthToCsv(String growthDir) throws IOException
    {
        dataPath+= growthDir;

        Buffer buffer;
        ArrayList<int[]> dataList= new ArrayList<>();

        List<File> fileList = new ArrayList<>(Arrays.asList(Objects.requireNonNull(new File(dataPath).listFiles()))); //creates a List of File objects representing every file in specified parameter directory
        fileList.removeIf(File::isHidden); //removes all File objects from List that are hidden

        File[] files = fileList.toArray(new File[0]); //creates an array of File objects using the contents of the modified List
        sort(files); //sorts files numerically (0.bin, 1.bin, 2.bin, etc...)

        for (File file : files)
        {
            int[] mon = new int[101];
            buffer = new Buffer(file.toString());
            for (int l = 0; l < mon.length; l++)
            {
                mon[l]= buffer.readInt();
            }
            buffer.close();
            dataList.add(mon);
        }

        BufferedWriter writer= new BufferedWriter(new FileWriter(path + "GrowthTable.csv"));
        writer.write("ID Number,Name,");
        for(int i= 0; i < 101; i++)
        {
            writer.write("Level " + i + ",");
        }
        writer.write("\n");

        for(int row= 0; row < dataList.size(); row++)
        {
            writer.write(row + "," + nameData[row] + ",");
            for(int col= 0; col < 101; col++)
            {
                writer.write(dataList.get(row)[col] + ",");
            }
            writer.write("\n");
        }
        writer.close();
    }

    public void csvToGrowth(String growthCsv, String outputDir) throws IOException
    {
        String outputPath;
        if(outputDir.contains("Recompile"))
        {
            outputPath= path + "temp" + File.separator+ outputDir;
        }
        else
        {
            outputPath= path + File.separator + outputDir;
        }

        String growthPath= path + growthCsv;
        CsvReader csvReader= new CsvReader(growthPath);

    }


    private void sort (File arr[])
    {
        Arrays.sort(arr, Comparator.comparingInt(GrowthEditor::fileToInt));
    }

    private static int fileToInt (File f)
    {
        return Integer.parseInt(f.getName().split("\\.")[0]);
    }
}
