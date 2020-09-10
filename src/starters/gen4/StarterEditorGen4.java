package starters.gen4;

import framework.BinaryWriter;
import framework.Buffer;

import java.io.*;
import java.util.*;

public class StarterEditorGen4
{
    private static String path = System.getProperty("user.dir") + File.separator; //creates a new String field containing user.dir and File.separator (/ on Unix systems, \ on Windows)
    private static String resourcePath = path + "Program Files" + File.separator;
    private static String[] nameData;
    private String gameCode;
    Buffer starterBuffer;
    BinaryWriter writer;

    public StarterEditorGen4(String gameCode) throws IOException
    {
        this.gameCode = gameCode;

        String entryPath = resourcePath + "EntryData.txt";


        BufferedReader reader = new BufferedReader(new FileReader(entryPath));
        ArrayList<String> nameList = new ArrayList<>();
        String line;
        while ((line = reader.readLine()) != null)
        {
            nameList.add(line);
        }
        nameData = nameList.toArray(new String[0]);
        reader.close();
    }

    public void changeStarters(String startersFile) throws IOException
    {
        starterBuffer= new Buffer(startersFile);
        writer= new BinaryWriter(startersFile + "Recompile");
        int offset;

        String noRegion= gameCode.substring(0,3).toLowerCase();
        switch (noRegion)
        {
            case "cpu": //Platinum
                if(gameCode.substring(3).equalsIgnoreCase("j"))
                {
                    offset = 0x1bac;
                } else
                {
                    offset = 0x1bc0;
                }
                editStarterData(offset);
                break;

            case "apa": //Pearl
            case "ada"://Diamond
                if(gameCode.substring(3).equalsIgnoreCase("j"))
                {
                    offset = 0x30;
                } else
                {
                    offset = 0x1b88;
                }
                editStarterData(offset);
                break;

            default:
                throw new RuntimeException("This editor can't be used with Gen 5 or HGSS currently.");
        }
    }

    private void editStarterData(int offset) throws IOException
    {
        Scanner scanner= new Scanner(System.in);
        String ans;
        int pokemonId;

        writer.write(starterBuffer.readBytes(offset));
        for(int i= 0; i < 3; i++)
        {
            pokemonId= starterBuffer.readInt();
            System.out.println("Starter " + (i + 1) + " is currently: " + nameData[pokemonId] + ". Do you want to change it? (y/N)\n");
            ans= scanner.nextLine();
            System.out.println("\n");

            if(ans.equalsIgnoreCase("y"))
            {
                System.out.println("Please enter the name of the Pokemon you wish to replace it with\n");
                pokemonId= getPokemon(scanner.nextLine());
                System.out.println("\nStarter has been replaced with " + nameData[pokemonId] + "\n");
            }
            writer.writeInt(pokemonId);
        }
        writer.write(starterBuffer.readRemainder());
    }

    private void sort(File arr[])
    {
        Arrays.sort(arr, Comparator.comparingInt(StarterEditorGen4::fileToInt));
    }

    private static int fileToInt(File f)
    {
        return Integer.parseInt(f.getName().split("\\.")[0]);
    }

    private int arrIdx;
    private String[] input;

    private void initializeIndex(String[] arr)
    {
        arrIdx = 0;
        input = arr;
    }

    private String next()
    {
        try
        {
            return input[arrIdx++];
        } catch (IndexOutOfBoundsException e)
        {
            return "";
        }
    }

    private static int getPokemon(String pokemon)
    {
        for(int i= 0; i < nameData.length; i++)
        {
            if(pokemon.equalsIgnoreCase(nameData[i]))
            {
                return i;
            }
        }
        throw new RuntimeException("Invalid pokemon entered: " + pokemon);
    }
}
