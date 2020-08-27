package learnsets;

import framework.BinaryWriter;
import framework.Buffer;
import framework.CsvReader;

import java.io.*;
import java.util.*;

public class LearnsetEditor
{
    private static String path= System.getProperty("user.dir") + File.separator; //creates a new String field containing user.dir and File.separator (/ on Unix systems, \ on Windows)
    private String dataPath= path;
    private static String resourcePath= path + "Program Files" + File.separator;
    private static String[] nameData;
    private static String[] moveData;
    private static boolean gen5;


    public LearnsetEditor() throws IOException
    {
        Scanner scanner= new Scanner(System.in);
        String entryPath= resourcePath;
        String movePath= resourcePath;

        System.out.println("Gen 4 or 5?");
        String gen= scanner.nextLine().toLowerCase();
        switch (gen) {
            case "4" :
                entryPath+= "EntryData.txt";
                movePath+= "MoveList.txt";
                break;
            case "5" :
                System.out.println("Black and White 1, or Black and White 2? (1 or 2)");
                String version= scanner.nextLine().toLowerCase();
<<<<<<< HEAD
                movePath+= "MoveList.txt";
=======
                movePath+= "MoveList Rebooted.txt";
>>>>>>> refs/remotes/origin/master
                switch (version) {
                    case "1":
                        entryPath += "EntryDataGen5-1.txt";
                        break;
                    case "2":
                        entryPath += "EntryDataGen5-2.txt";
                        break;
                    default:
                        throw new RuntimeException("Invalid arguments");
                }
                break;
            default:
                throw new RuntimeException("Invalid arguments");
        }
<<<<<<< HEAD
        gen5 = gen.equals("5");
=======
        this.gen5 = gen.equals("5");
>>>>>>> refs/remotes/origin/master

        BufferedReader reader= new BufferedReader(new FileReader(entryPath));
        ArrayList<String> nameList= new ArrayList<>();
        String line;
        while((line= reader.readLine()) != null)
        {
            nameList.add(line);
        }
        nameData= nameList.toArray(new String[0]);
        reader.close();

        reader= new BufferedReader(new FileReader(movePath));
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
        ArrayList<ArrayList<MoveLearnsetData>> dataList= new ArrayList<>();

        List<File> fileList = new ArrayList<>(Arrays.asList(Objects.requireNonNull(new File(dataPath).listFiles()))); //creates a List of File objects representing every file in specified parameter directory
        fileList.removeIf(File::isHidden); //removes all File objects from List that are hidden

        File[] files = fileList.toArray(new File[0]); //creates an array of File objects using the contents of the modified List
        sort(files); //sorts files numerically (0.bin, 1.bin, 2.bin, etc...)
        File file;

        for(int i= 0; i < files.length; i++)
        {
            file= files[i];
            learnsetBuffer= new Buffer(file.toString());
            int numMoves;
<<<<<<< HEAD
            learnsetBuffer.skipTo((int) (file.length()-4));
            byte[] last4= learnsetBuffer.readRemainder();
            byte[] properDelimeter= new byte[] {(byte) 0xFF, (byte) 0xFF,0x00,0x00};

            if(!gen5) //gen 4
            {
                if(Arrays.equals(last4, properDelimeter))
                {
                    numMoves= (int) ((file.length()-4)/2);
                }
                else
                {
                    numMoves= (int) ((file.length()-2)/2);
                }
            }
            else //gen 5
=======
            if(!gen5)
            {
                numMoves= (int) ((file.length()-4)/2);
            }
            else
>>>>>>> refs/remotes/origin/master
            {
                numMoves= (int) ((file.length()-4)/4);
            }

<<<<<<< HEAD
            learnsetBuffer= new Buffer((file.toString()));
=======
>>>>>>> refs/remotes/origin/master
            System.out.println(nameData[i] + " numMoves: " + numMoves);
            ArrayList<MoveLearnsetData> moveList= new ArrayList<>();
            for(int m= 0; m < numMoves; m++)
            {
                if(!gen5)
                {
                    short move= learnsetBuffer.readShort();
                    System.out.print("  " + moveData[getMoveId(move)]);
                    System.out.println(": " + getLevelLearned(move));
                    moveList.add(new MoveLearnsetData() {
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
                else
                {
                    int move= learnsetBuffer.readUIntS();
                    int level= learnsetBuffer.readUIntS();
                    System.out.print("  " + moveData[move]);
                    System.out.println(": " + level);
                    moveList.add(new MoveLearnsetData() {
                        @Override
                        public int getID() {
                            return move;
                        }

                        @Override
                        public int getLevel() {
                            return level;
                        }
                    });
                }

            }
            dataList.add(moveList);
        }

<<<<<<< HEAD
        String[][] learnsetTable;
        if(gen5)
        {
            learnsetTable= new String[dataList.size()][80];
        }
        else
        {
            learnsetTable= new String[dataList.size()][40];
        }

=======
        String[][] learnsetTable= new String[dataList.size()][80];
>>>>>>> refs/remotes/origin/master
        for (String[] row : learnsetTable) {
            Arrays.fill(row, "");
        }

        for(int row= 0; row < dataList.size(); row++)
        {
            int idx= 0;
            ArrayList<MoveLearnsetData> pokemon= dataList.get(row);
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
                line+= learnsetTable[row][col] + ",";
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
<<<<<<< HEAD
            int numMoves= thisLine.length/ 2;
=======
>>>>>>> refs/remotes/origin/master
            BinaryWriter writer= new BinaryWriter(outputPath + File.separator + i + ".bin");
            for(int m= 0; m < thisLine.length; m+= 2)
            {
                System.out.println("    " + thisLine[m]);
                int moveID= getMove(thisLine[m]);
                int level= Integer.parseInt(thisLine[m+1]);
                MoveLearnsetData thisMove= new MoveLearnsetData() {
                    @Override
                    public int getID() {
                        return moveID;
                    }

                    @Override
                    public int getLevel() {
                        return level;
                    }
                };
                if(!gen5)
                {
                    writer.writeShort(produceLearnData(thisMove));
                }
                else
                {
                    writer.writeShort((short) moveID);
                    writer.writeShort((short) level);
                }
            }
<<<<<<< HEAD

            if(!gen5)
            {
                if(numMoves % 2 == 0)
                {
                    writer.write(new byte[] {(byte) 0xFF, (byte) 0xFF,0x00,0x00});
                }
                else
                {
                    writer.write(new byte[] {(byte) 0xFF, (byte) 0xFF});
                }
            }
            else
            {
                writer.write(new byte[] {(byte) 0xFF, (byte) 0xFF,0x00,0x00});
            }

=======
            writer.write(new byte[] {(byte) 0xFF, (byte) 0xFF,0x00,0x00});
>>>>>>> refs/remotes/origin/master
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

    private static short produceLearnData(MoveLearnsetData moveList)
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
}
