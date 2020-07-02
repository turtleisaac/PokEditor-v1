import java.io.*;
import java.util.*;

public class LearnsetEditor
{
    private static String path= System.getProperty("user.dir") + File.separator; //creates a new String field containing user.dir and File.separator (/ on Unix systems, \ on Windows)
    private String dataPath= path;
    private static final String[] typeArr= {"Normal", "Fighting", "Flying", "Poison", "Ground", "Rock", "Bug", "Ghost", "Steel", "Fairy", "Fire", "Water","Grass","Electric","Psychic","Ice","Dragon","Dark"};
    private static String resourcePath= path + "Program Files" + File.separator;
    private static String[] nameData;
    private static String[] moveData;


    public LearnsetEditor() throws IOException {
        BufferedReader reader= new BufferedReader(new FileReader(resourcePath + "EntryData.txt"));
        ArrayList<String> nameList= new ArrayList<>();
        String line;
        while((line= reader.readLine()) != null)
        {
            nameList.add(line);
        }
        nameData= nameList.toArray(new String[0]);
        reader.close();

        reader= new BufferedReader(new FileReader(resourcePath + "MoveList.txt"));
        ArrayList<String> moveList= new ArrayList<>();

        while((line= reader.readLine()) != null)
        {
            moveList.add(line);
        }
        moveData= moveList.toArray(new String[0]);
        reader.close();
    }

    public void learnsetToCsv(String learnsetDir) throws IOException {
        dataPath+= learnsetDir;

        Buffer learnsetBuffer;
        ArrayList<ArrayList<MoveData>> dataList= new ArrayList<>();

        List<File> fileList = new ArrayList<>(Arrays.asList(Objects.requireNonNull(new File(dataPath).listFiles()))); //creates a List of File objects representing every file in specified parameter directory
        fileList.removeIf(File::isHidden); //removes all File objects from List that are hidden

        File[] files = fileList.toArray(new File[0]); //creates an array of File objects using the contents of the modified List
        sort(files); //sorts files numerically (0.bin, 1.bin, 2.bin, etc...)
        File file;
        int count= 0;

        for(int i= 0; i < files.length; i++)
        {
//            file= files[i];
//            learnsetBuffer= new Buffer(file.toString());
//            int finalCount= count;
//            count++;
//
//            int fileLength= (int)(file.length()-4)/2;
//
//            short[] moveArr= new short[20];
//            int numMoves= 0;
//            for(int m= 0; m < (file.length()-4)/2; m++)
//            {
//                short thisMove= learnsetBuffer.readShort();
//                System.out.println(thisMove);
//                moveArr[m]= thisMove;
//                numMoves++;
//            }
//            short[] theseMoves= new short[numMoves];
//            System.arraycopy(moveArr,0,theseMoves,0,numMoves);
//
//            dataList.add(new LearnsetData() {
//                @Override
//                public int getNum() {
//                    return finalCount;
//                }
//
//                @Override
//                public int getlength() {
//                    return fileLength;
//                }
//
//                @Override
//                public short[] getMoves() {
//                    return theseMoves;
//                }
//            });
//            learnsetBuffer.close();
//            System.out.println(nameData[finalCount] + " moves learned: " + moveArr.length);
            file= files[i];
            learnsetBuffer= new Buffer(file.toString());
            int numMoves= (int) ((file.length()-2)/2);
            if(hasPadding(file))
            {
                numMoves= (int) ((file.length()-4)/2);

            }
            System.out.println(nameData[i] + " numMoves: " + numMoves);
            ArrayList<MoveData> moveList= new ArrayList<>();
            if(i != 0 && i != 132 && i != 494 && i != 495)
            {
                for(int m= 0; m < numMoves; m++)
                {
                    short move= learnsetBuffer.readShort();
                    System.out.print("  " + moveData[getMoveId(move)]);
                    System.out.println(": " + getLevelLearned(move));
                    moveList.add(new MoveData() {
                        @Override
                        public int getID() {
                            return getMoveId(move);
                        }

                        @Override
                        public int getLevel() {
                            return getLevelLearned(move);
                        }
                    });
                }
            }

            dataList.add(moveList);
        }

        String[][] learnsetTable= new String[dataList.size()][40];
        for (String[] row : learnsetTable) {
            Arrays.fill(row, "");
        }
//        for(int row= 0; row < dataList.size(); row++)
//        {
//            for(int col= 0; col < dataList.get(row).size(); col++)
//            {
//                MoveData move= dataList.get(row).get(col);
//                if(col % 2 == 0)
//                {
//                    System.out.println(nameData[row] + ": " + move.getID());
//                    learnsetTable[row][col]= moveData[move.getID()];
//                }
//                else
//                {
//                    learnsetTable[row][col]= "" + move.getLevel();
//                }
//            }
//        }

        for(int row= 0; row < dataList.size(); row++)
        {
            int idx= 0;
            ArrayList<MoveData> pokemon= dataList.get(row);
            for(int col= 0; col < pokemon.size(); col++)
            {
                learnsetTable[row][idx++]= moveData[pokemon.get(col).getID()];
                learnsetTable[row][idx++]= "" + pokemon.get(col).getLevel();
            }
        }

        BufferedWriter writer= new BufferedWriter(new FileWriter(path + "Learnset.csv"));
        writer.write("ID Number,Name,");
        for(int i= 0; i < 20; i++)
        {
            writer.write("Move,Level,");
        }
        writer.write("\n");

        String line;
        for(int row= 0; row < dataList.size(); row++)
        {
            line= row + "," + nameData[row] + ",";
            for(int col= 0; col < learnsetTable[0].length; col++)
            {
                line+=learnsetTable[row][col] + ",";
            }
            line+= "\n";
            writer.write(line);
        }
        writer.close();
    }

    public void csvToLearnsets(String learnsetCsv, String outputDir) throws IOException
    {
        String learnsetPath= path + learnsetCsv;
        String outputPath;

        if(outputDir.contains("Recompile"))
        {
            outputPath= path + "temp" + File.separator+ outputDir;
        }
        else
        {
            outputPath= path + File.separator + outputDir;
        }

        if(!new File(outputPath).exists() && !new File(outputPath).mkdir())
        {
            throw new RuntimeException("Could not create output directory. Check write permissions");
        }

        CsvReader csvReader= new CsvReader(learnsetPath);
        for(int i= 0; i < csvReader.length(); i++)
        {
            System.out.println(nameData[i]);
            String[] thisLine= csvReader.next();
            BinaryWriter writer= new BinaryWriter(outputPath + File.separator + i + ".bin");
            for(int m= 0; m < thisLine.length; m+= 2)
            {
                System.out.println("    " + thisLine[m]);
                int moveID= getMove(thisLine[m]);
                int level= Integer.parseInt(thisLine[m+1]);
                MoveData thisMove= new MoveData() {
                    @Override
                    public int getID() {
                        return moveID;
                    }

                    @Override
                    public int getLevel() {
                        return level;
                    }
                };
                writer.writeShort(produceLearnData(thisMove));
            }
            writer.write(new byte[] {(byte) 0xFF, (byte) 0xFF,0x00,0x00});
            writer.close();
        }
    }




    private void sort (File arr[])
    {
        Arrays.sort(arr, Comparator.comparingInt(LearnsetEditor::fileToInt));
    }

    private static int fileToInt (File f)
    {
        return Integer.parseInt(f.getName().split("\\.")[0]);
    }


    private static int getMoveId (short x)
    {
        return x & 0x1FF;
    }

    private static int getLevelLearned (short x)
    {
        return (x >> 9) & 0x7F;
    }


    private static int getType(String type)
    {
        for(int i= 0; i < typeArr.length; i++)
        {
            if(type.equals(typeArr[i]))
            {
                return i;
            }
        }
        throw new RuntimeException("Invalid type entered");
    }

    private static int getMove(String move)
    {
        for(int i= 0; i < moveData.length; i++)
        {
            if(move.equals(moveData[i]))
            {
                return i;
            }
        }
        throw new RuntimeException("Invalid move entered: " + move);
    }

    private static int getPokemon(String pokemon)
    {
        for(int i= 0; i < nameData.length; i++)
        {
            if(pokemon.equals(nameData[i]))
            {
                return i;
            }
        }
        throw new RuntimeException("Invalid pokemon entered");
    }

    private static short parseShort(String... evs)
    {
        String fullEv= "";
        for(String ev : evs)
        {
            fullEv+= ev;
        }

        short num= 0;
        for(int i= 0; i < fullEv.length(); i++)
        {
            String thisEv= fullEv.substring(fullEv.length()-i-1,fullEv.length()-i);
            if(thisEv.equals("1"))
            {
                num+= Math.pow(2,i);
            }
        }
        return num;
    }


    private static short produceLearnData(MoveData moveList)
    {
        int id= moveList.getID();
        String idBinary= Integer.toBinaryString(id);
        int level= moveList.getLevel();
        String levelBinary= Integer.toBinaryString(level);

        while(idBinary.length() != 9)
        {
            idBinary= "0" + idBinary;
        }
        while(levelBinary.length() != 7)
        {
            levelBinary= "0" + levelBinary;
        }

        System.out.print("        " + levelBinary);
        System.out.println(idBinary);
        return parseShort(levelBinary,idBinary);
    }

    private static boolean hasPadding(File file)
    {
        int length= (int)file.length();
        Buffer buffer= new Buffer(file.toString());
        byte[] bytes= buffer.readBytes(length);

        return bytes[length-1] == 0x00;
    }
}
