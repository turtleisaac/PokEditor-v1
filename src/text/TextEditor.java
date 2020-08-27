package text;

import framework.BinaryWriter;
import framework.Buffer;
import framework.CsvReader;

import java.io.*;
import java.util.*;

public class TextEditor
{
    public static void main(String[] args) throws IOException
    {
        TextEditor textEditor= new TextEditor();
        textEditor.textToCsv("msg2");
    }

    private static String path = System.getProperty("user.dir") + File.separator; //creates a new String field containing user.dir and File.separator (/ on Unix systems, \ on Windows)
    private String dataPath = path;
    private static String resourcePath = path + "Program Files" + File.separator;
    private static final String[] typeArr = {"Normal", "Fighting", "Flying", "Poison", "Ground", "Rock", "Bug", "Ghost", "Steel", "Fire", "Water", "Grass", "Electric", "Psychic", "Ice", "Dragon", "Dark", "Fairy"};
    private static final String[] categories = {"Physical", "Special", "Status"};
    private static final String[] statusArr = {"None", "Sleep", "Poison", "Burn", "Freeze", "Paralysis", "Confusion", "Infatuation"};
    private static String[] nameData;
    private static String[] moveData;
    private static String[] effects;
    private static String[] flags;
    private static String[] targets;
    private static String[] tmData;
    private static String[] itemData;
    private static String[] abilityData;

    public TextEditor() throws IOException {
        String entryPath = resourcePath + "EntryData.txt";
        String movePath = resourcePath + "MoveList.txt";


        BufferedReader reader = new BufferedReader(new FileReader(entryPath));
        ArrayList<String> nameList = new ArrayList<>();
        String line;
        while ((line = reader.readLine()) != null) {
            nameList.add(line);
        }
        nameData = nameList.toArray(new String[0]);
        reader.close();

        reader = new BufferedReader(new FileReader(movePath));
        ArrayList<String> moveList = new ArrayList<>();

        while ((line = reader.readLine()) != null) {
            moveList.add(line);
        }
        moveData = moveList.toArray(new String[0]);
        reader.close();

        reader = new BufferedReader(new FileReader(resourcePath + "Effects.txt"));
        ArrayList<String> effectList = new ArrayList<>();
        while ((line = reader.readLine()) != null) {
            effectList.add(line);
        }
        effects = effectList.toArray(new String[0]);
        reader.close();


        flags = new String[500];
        for (int i = 0; i < flags.length; i++) {
            flags[i] = "" + i;
        }

        reader = new BufferedReader(new FileReader(resourcePath + "TmList.txt"));
        ArrayList<String> tmList = new ArrayList<>();

        while ((line = reader.readLine()) != null) {
            tmList.add(line);
        }
        tmData = tmList.toArray(new String[0]);
        reader.close();

        reader = new BufferedReader(new FileReader(resourcePath + "ItemList.txt"));
        ArrayList<String> itemList = new ArrayList<>();

        while ((line = reader.readLine()) != null) {
            itemList.add(line);
        }
        itemData = itemList.toArray(new String[0]);
        reader.close();

        reader = new BufferedReader(new FileReader(resourcePath + "AbilityList.txt"));
        ArrayList<String> abilityList = new ArrayList<>();

        while ((line = reader.readLine()) != null) {
            abilityList.add(line);
        }
        abilityData = abilityList.toArray(new String[0]);
        reader.close();

        targets = new String[0x81];
        targets[0] = "One opponent";
        targets[1] = "Automatic";
        targets[2] = "Random";
        targets[4] = "Both opponents";
        targets[8] = "Both opponents and ally";
        targets[16] = "User";
        targets[32] = "User's side of field";
        targets[64] = "Entire field";
        targets[128] = "Opponent's side of field";
    }

    public void textToCsv(String textDir) throws IOException {
        dataPath += textDir;

        Buffer buffer;
        CharTable charTable= new CharTable();
        BufferedWriter writer= new BufferedWriter(new FileWriter(path + File.separator + "ExtractedText.txt"));
        ArrayList<String> dataList= new ArrayList<>();

        List<File> fileList= new ArrayList<>(Arrays.asList(Objects.requireNonNull(new File(dataPath).listFiles()))); //creates a List of File objects representing every file in specified parameter directory
        fileList.removeIf(File::isHidden); //removes all File objects from List that are hidden

        File[] files= fileList.toArray(new File[0]); //creates an array of File objects using the contents of the modified List
        sort(files); //sorts files numerically (0.bin, 1.bin, 2.bin, etc...)
        File file;

        boolean flag= false, flag2= false;

        for (int i= 0; i < files.length; i++)
        {
            file= files[i];

            writer.write("\nBank: " + i + "\n");
            buffer= new Buffer(file.toString());

            try
            {
                int numStrings= buffer.readUIntS();
                System.out.println("File: " + i + ", " + numStrings);
                int seed= buffer.readUIntS();

                int num= ((seed * 0x2fd) & 0xffff);
                int num2, num3, num4;

                int[] offsets= new int[numStrings];
                int[] sizes= new int[numStrings];

                for (int x= 0; x < numStrings; x++)
                {
                    num2= (num * (x + 1)) & 0xffff;
                    num3= num2 | (num2 << 16);

                    offsets[x]= buffer.readInt();
                    offsets[x]= offsets[x] ^ num3;

                    sizes[x]= buffer.readInt();
                    sizes[x]= sizes[x] ^ num3;
                }

                for (int j= 0; j < numStrings; j++)
                {
                    num= ((0x91bd3 * (j + 1)) & 0xffff);
                    StringBuilder text= new StringBuilder();

                    for (int k= 0; k < sizes[j]; k++)
                    {
                        num4= buffer.readUIntS();
                        num4= (num4 ^ num);

                        if (num4 == 57344 || num4 == 9660 || num4 == 9661 || num4 == 61696 || num4 == 65534 || num4 == 65535)
                        {
                            if (num4 == 57344)
                                text.append("\\n");
                            if (num4 == 9660)
                                text.append("\\r");
                            if (num4 == 9661)
                                text.append("\\f");
                            if (num4 == 61696)
                                flag2 = true;
                            if (num4 == 65534)
                            {
                                text.append("\\v");
                                flag= true;
                            }
                        } else
                        {
                            if (flag)
                            {
                                if (Integer.toHexString(num4).length() < 4)
                                {
                                    StringBuilder temp= new StringBuilder();
                                    for (int index= Integer.toHexString(num4).length(); index < 4; index++)
                                    {
                                        temp.append("0");
                                    }
                                    text.append(temp.append(Integer.toHexString(num4)));
                                } else
                                {
                                    text.append(Integer.toHexString(num4));
                                }
                                flag= false;
                            } else
                            {
                                if (flag2)
                                {
                                    int num5= 0;
                                    int num6= 0;
                                    String str= "";
                                    while (true)
                                    {
                                        if (num5 >= 15)
                                        {
                                            num5 -= 15;
                                            if (num5 > 0)
                                            {
                                                int num8= (num6 | (num4 << 9 - num5 & 511));
                                                if ((num8 & 255) == 255)
                                                {
                                                    break;
                                                }

                                                if (num8 != 0 && num8 != 1)
                                                {
                                                    String str2= charTable.getCharacter(num8);
                                                    text.append(str2);
                                                    if (str2.equals("0"))
                                                    {
                                                        if (Integer.toHexString(num8).length() < 4)
                                                        {
                                                            StringBuilder temp= new StringBuilder();
                                                            for (int index= Integer.toHexString(num8).length(); index < 4; index++)
                                                            {
                                                                temp.append("0");
                                                            }

                                                            text.append("\\x").append(temp).append(Integer.toHexString(num8));
                                                        } else
                                                        {
                                                            text.append("\\x").append(Integer.toHexString(num8));
                                                        }
                                                    }
                                                }
                                            }
                                        } else
                                        {
                                            int num8= (num4 >> num5 & 511);
                                            if ((num8 & 255) == 255)
                                            {
                                                break;
                                            }

                                            if (num8 != 0 && num8 != 1)
                                            {
                                                String str3= charTable.getCharacter(num8);
                                                text.append(str3);
                                                if (str3.equals("0"))
                                                {
                                                    if (Integer.toHexString(num8).length() < 4)
                                                    {
                                                        StringBuilder temp= new StringBuilder();
                                                        for (int index= Integer.toHexString(num8).length(); index < 4; index++)
                                                        {
                                                            temp.append("0");
                                                        }

                                                        text.append("\\x").append(temp).append(Integer.toHexString(num8));
                                                    } else
                                                    {
                                                        text.append("\\x").append(Integer.toHexString(num8));
                                                    }
                                                }
                                            }
                                            num5 += 9;
                                            if (num5 < 15)
                                            {
                                                num6= (num4 >> num5 & 511);
                                                num5 += 9;
                                            }
                                            num += 18749;
                                            num &= 65535;
                                            num4= buffer.readUIntS();
                                            num4 ^= num;
                                            k++;
                                        }
                                    }
                                    text.append(str);
                                } else
                                {
                                    String str3= charTable.getCharacter(num4);
                                    text.append(str3);
                                    if (str3.equals("0"))
                                    {
                                        if (Integer.toHexString(num4).length() < 4)
                                        {
                                            StringBuilder temp= new StringBuilder();
                                            for (int index= Integer.toHexString(num4).length(); index < 4; index++)
                                            {
                                                temp.append("0");
                                            }

                                            text.append("\\x").append(temp).append(Integer.toHexString(num4));
                                        } else
                                        {
                                            text.append("\\x").append(Integer.toHexString(num4));
                                        }
                                    }
                                }
                            }
                        }
                        num += 18749;
                        num &= 65535;
                    }

                    System.out.print("    ");

                    String str= text.toString();
                    StringBuilder line= new StringBuilder();

                    if(str.contains("\\v"))
                    {
//                        if(i == 17)
//                        {
//                            System.out.println("MOOO");
//                        }
                        while(str.contains("\\v"))
                        {
                            int idx= str.indexOf("\\v");
                            if(idx != 0)
                            {
                                line.append(str, 0, idx);
                            }
                            str= str.substring(idx + 2);
                            line.append("VAR(");

                            line.append(Integer.parseInt(str.substring(0,4),16) + ",");
                            str= str.substring(str.indexOf("\\") + 3);


                            if(str.indexOf("\\\\x") == 4)
                            {
                                str= str.substring(str.indexOf("\\") + 3);
                                line.append(Integer.parseInt(str.substring(0,4),16));
                                str= str.substring(4);
                            }
                            else
                            {
                                line.append(charTable.writeCharacter(str.substring(4,5)));
                                str= str.substring(5);
                            }
                            line.append(")");

//                            str= str.substring(3);
                        }
                        line.append(str);
                    }
                    else
                    {
                        line.append(str);
                    }

                    dataList.add(line.toString());
                    System.out.println(line.toString());
                    writer.write("    " + line.toString() + "\n");
                    writer.flush();
                }
            }
            catch(Exception e)
            {
                e.printStackTrace();
                writer.write("ERROR\n");
                writer.flush();
            }
        }


//        BufferedWriter writer = new BufferedWriter(new FileWriter(path + "textData.csv"));
//        writer.write("ID Number,Name,\n"); //header in spreadsheet output
//        String line;
//        for (int row = 0; row < dataList.size(); row++) {
//            line = row + "," + textData[row] + ",";
//            for (int col = 0; col < textTable[0].length; col++) {
//                line += textTable[row][col] + ",";
//            }
//            line += "\n";
//            writer.write(line);
//        }
//        writer.close();
    }


    public void csvTo(String textCsv, String outputDir) throws IOException {
        String textPath = path + textCsv;
        String outputPath;

        if (outputDir.contains("Recompile")) {
            outputPath = path + "temp" + File.separator + outputDir;
        } else {
            outputPath = path + File.separator + outputDir;
        }

        if (!new File(outputPath).exists() && !new File(outputPath).mkdir()) {
            throw new RuntimeException("Could not create output directory. Check write permissions");
        }
        outputPath += File.separator;

        CsvReader csvReader = new CsvReader(textPath);
        BinaryWriter writer;
        for (int i = 0; i < csvReader.length(); i++) {
            initializeIndex(csvReader.next());
            writer = new BinaryWriter(outputPath + i + ".bin");

            writer.close();
        }

    }

    private void sort(File arr[]) {
        Arrays.sort(arr, Comparator.comparingInt(TextEditor::fileToInt));
    }

    private static int fileToInt(File f) {
        return Integer.parseInt(f.getName().split("\\.")[0]);
    }

    private int arrIdx;
    private String[] input;

    private void initializeIndex(String[] arr) {
        arrIdx = 0;
        input = arr;
    }

    private String next() {
        try {
            return input[arrIdx++];
        } catch (IndexOutOfBoundsException e) {
            return "";
        }
    }

    private static byte getType(String type) {
        for (int i = 0; i < typeArr.length; i++) {
            if (type.equals(typeArr[i])) {
                return (byte) i;
            }
        }

        throw new RuntimeException("Invalid type entered: " + type);
    }

    private static int getMove(String move) {
        for (int i = 0; i < moveData.length; i++) {
            if (move.equals(moveData[i])) {
                return i;
            }
        }
        throw new RuntimeException("Invalid move entered: " + move);
    }

    private static short getEffect(String effect) {
        for (int i = 0; i < effects.length; i++) {
            if (effect.equals(effects[i])) {
                return (short) i;
            }
        }
        throw new RuntimeException("Invalid effect entered: " + effect);
    }

    private static byte getCategory(String category) {
        for (int i = 0; i < categories.length; i++) {
            if (category.equals(categories[i])) {
                return (byte) i;
            }
        }
        throw new RuntimeException("Invalid category entered: " + category);
    }

    private static short getTargets(String target) {
        for (int i = 0; i < targets.length; i++) {
            if (target.equals(targets[i])) {
                return (byte) i;
            }
        }
        throw new RuntimeException("Invalid target(s) entered: " + target);
    }


}
