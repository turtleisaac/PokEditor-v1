//package babies;
//
//import framework.BinaryWriter;
//import framework.Buffer;
//
//import java.io.BufferedReader;
//import java.io.File;
//import java.io.FileReader;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Comparator;
//import java.util.Scanner;
//
//public class SpecialBabyFormEditor
//{
//    private static String path = System.getProperty("user.dir") + File.separator; //creates a new String field containing user.dir and File.separator (/ on Unix systems, \ on Windows)
//    private static String resourcePath = path + "Program Files" + File.separator;
//    private static String[] nameData;
//    private String gameCode;
//    private ArrayList<SpecialBabyFormData> dataList;
//    private Buffer starterBuffer;
//    private BinaryWriter writer;
//
//    public SpecialBabyFormEditor(String gameCode) throws IOException
//    {
//        this.gameCode = gameCode;
//
//        String entryPath = resourcePath + "EntryData.txt";
//
//
//        BufferedReader reader = new BufferedReader(new FileReader(entryPath));
//        ArrayList<String> nameList = new ArrayList<>();
//        String line;
//        while ((line = reader.readLine()) != null)
//        {
//            nameList.add(line);
//        }
//        nameData = nameList.toArray(new String[0]);
//        reader.close();
//    }
//
//
//
//    public byte[] changeSpecialBabies(byte[] babyData) throws IOException
//    {
//        dataList= new ArrayList<>();
//        for(int i= 0; i < babyData.length / 3; i++)
//        {
//
//        }
//
//        return editSpecialBabies();
//    }
//
//    private byte[] editSpecialBabies() throws IOException
//    {
//        Scanner scanner= new Scanner(System.in);
//        String ans;
//        int pokemonId;
//        int itemId;
//        int defaultId;
//        byte[] newBabyData= new byte[];
//
//        for(int i= 0; i < 3; i++)
//        {
//            pokemonId= 1;
//            System.out.println("Opening Cutscene Pokemon " + (i + 1) + " is currently: " + nameData[pokemonId] + ". Do you want to change it? (y/N)\n");
//            ans= scanner.nextLine();
//            System.out.println("\n");
//
//            if(ans.equalsIgnoreCase("y"))
//            {
//                System.out.println("Please enter the name of the Pokemon you wish to replace it with\n");
//                pokemonId= getPokemon(scanner.nextLine());
//                System.out.println("\nPokemon has been replaced with " + nameData[pokemonId] + "\n");
//            }
//            writer.writeInt(pokemonId);
//        }
//        writer.write(starterBuffer.readRemainder());
//        writer.close();
//
//        return newBabyData;
//    }
//
//    private void sort(File arr[])
//    {
//        Arrays.sort(arr, Comparator.comparingInt(SpecialBabyFormEditor::fileToInt));
//    }
//
//    private static int fileToInt(File f)
//    {
//        return Integer.parseInt(f.getName().split("\\.")[0]);
//    }
//
//    private int arrIdx;
//    private String[] input;
//
//    private void initializeIndex(String[] arr)
//    {
//        arrIdx = 0;
//        input = arr;
//    }
//
//    private String next()
//    {
//        try
//        {
//            return input[arrIdx++];
//        } catch (IndexOutOfBoundsException e)
//        {
//            return "";
//        }
//    }
//
//    private static int getPokemon(String pokemon)
//    {
//        for(int i= 0; i < nameData.length; i++)
//        {
//            if(pokemon.equalsIgnoreCase(nameData[i]))
//            {
//                return i;
//            }
//        }
//        throw new RuntimeException("Invalid pokemon entered: " + pokemon);
//    }
//}
