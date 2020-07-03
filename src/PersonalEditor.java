import java.io.*;
import java.util.*;

public class PersonalEditor
{

    public static void main(String[] args) throws IOException {
        PersonalEditor personalEditor= new PersonalEditor();
        personalEditor.csvToPersonal2("personalData.csv","tmLearnsetData.csv","moo");
    }

    private static String path= System.getProperty("user.dir") + File.separator; //creates a new String field containing user.dir and File.separator (/ on Unix systems, \ on Windows)
    private String dataPath= path;
    private static final String[] typeArr= {"Normal", "Fighting", "Flying", "Poison", "Ground", "Rock", "Bug", "Ghost", "Steel", "Fairy", "Fire", "Water","Grass","Electric","Psychic","Ice","Dragon","Dark"};
    private static final String[] eggGroupArr= {"~","Monster","Water 1","Bug","Flying","Field","Fairy","Grass","Human-Like","Water 3","Mineral","Amorphous","Water 2","Ditto","Dragon","Undiscovered"};
    private static final String[] growthTableIdArr= {"Medium Fast","Erratic","Fluctuating","Medium Slow","Fast","Slow","Medium Fast","Medium Fast"};
    private static String resourcePath= path + "Program Files" + File.separator;
    private static String[] nameData;
    private static String[] tmData;
    private static String[] itemData;
    private static String[] abilityData;
    private static String[] tmNameData;

    public PersonalEditor() throws IOException
    {
        BufferedReader reader= new BufferedReader(new FileReader(resourcePath + "EntryData.txt"));
        ArrayList<String> nameList= new ArrayList<>();
        String line;
        while((line= reader.readLine()) != null)
        {
            nameList.add(line);
        }
        nameData= nameList.toArray(new String[0]);
        reader.close();

        reader= new BufferedReader(new FileReader(resourcePath + "TmList.txt"));
        ArrayList<String> tmList= new ArrayList<>();

        while((line= reader.readLine()) != null)
        {
            tmList.add(line);
        }
        tmData= tmList.toArray(new String[0]);
        reader.close();

        reader= new BufferedReader(new FileReader(resourcePath + "ItemList.txt"));
        ArrayList<String> itemList= new ArrayList<>();

        while((line= reader.readLine()) != null)
        {
            itemList.add(line);
        }
        itemData= itemList.toArray(new String[0]);
        reader.close();

        reader= new BufferedReader(new FileReader(resourcePath + "AbilityList.txt"));
        ArrayList<String> abilityList= new ArrayList<>();

        while((line= reader.readLine()) != null)
        {
            abilityList.add(line);
        }
        abilityData= abilityList.toArray(new String[0]);
        reader.close();

        reader= new BufferedReader(new FileReader(resourcePath + "TmNameList.txt"));
        ArrayList<String> tmNameList= new ArrayList<>();

        while((line= reader.readLine()) != null)
        {
            tmNameList.add(line);
        }
        tmNameData= tmNameList.toArray(new String[0]);
        reader.close();
    }

    public void personalToCSV(String personalDir) throws IOException
    {
        dataPath+= personalDir;

        Buffer personalBuffer;
        ArrayList<PersonalData> dataList= new ArrayList<>();

        List<File> fileList = new ArrayList<>(Arrays.asList(Objects.requireNonNull(new File(dataPath).listFiles()))); //creates a List of File objects representing every file in specified parameter directory
        fileList.removeIf(File::isHidden); //removes all File objects from List that are hidden

        File[] files = fileList.toArray(new File[0]); //creates an array of File objects using the contents of the modified List
        sort(files); //sorts files numerically (0.bin, 1.bin, 2.bin, etc...)
        File file;
        int count= 0;

        for(int i= 0; i < files.length; i++)
        {
            int localCount= count;
            System.out.println(nameData[i]);
            file= files[i];
            personalBuffer= new Buffer(file.toString());
            int hp= personalBuffer.readByte();
            int atk= personalBuffer.readByte();
            int def= personalBuffer.readByte();
            int spe= personalBuffer.readByte();
            int spAtk= personalBuffer.readByte();
            int spDef= personalBuffer.readByte();
            int type1= personalBuffer.readByte();
            int type2= personalBuffer.readByte();
            int catchRate= personalBuffer.readByte();
            int baseExp= personalBuffer.readByte();

            short evYields= personalBuffer.readShort();
            int hpYield= getHp(evYields);
            int speYield= getSpe(evYields);
            int atkYield= getAtk(evYields);
            int defYield= getDef(evYields);
            int spAtkYield= getSpAtk(evYields);
            int spDefYield= getSpDef(evYields);
            int evPadded= getPadded(evYields);


            int uncommonItem= personalBuffer.readShort();
            int rareItem= personalBuffer.readShort();
            int genderRatio= personalBuffer.readByte();
            int hatchMultiplier= personalBuffer.readByte();
            int baseHappiness= personalBuffer.readByte();
            int expRate= personalBuffer.readByte();
            int eggGroup1= personalBuffer.readByte();
            int eggGroup2= personalBuffer.readByte();
            int ability1= personalBuffer.readByte();
            int ability2= personalBuffer.readByte();
            int runChance= personalBuffer.readByte();
            int dexColor= personalBuffer.readByte();
            personalBuffer.readShort(); // 2 bytes padding
            byte[] tmLearnset= personalBuffer.readBytes(16);


            dataList.add(new PersonalData() {
                @Override
                public int getNum() {
                    return localCount;
                }

                @Override
                public int getHP() {
                    return hp;
                }

                @Override
                public int getAtk() {
                    return atk;
                }

                @Override
                public int getDef() {
                    return def;
                }

                @Override
                public int getSpe() {
                    return spe;
                }

                @Override
                public int getSpAtk() {
                    return spAtk;
                }

                @Override
                public int getSpDef() {
                    return spDef;
                }

                @Override
                public int getType1() {
                    return type1;
                }

                @Override
                public int getType2() {
                    return type2;
                }

                @Override
                public int getCatchRate() {
                    return catchRate;
                }

                @Override
                public int getBaseExp() {
                    return baseExp;
                }

                @Override
                public int getHpEv() {
                    return hpYield;
                }

                @Override
                public int getSpeEv() {
                    return speYield;
                }

                @Override
                public int getAtkEv() {
                    return atkYield;
                }

                @Override
                public int getDefEv() {
                    return defYield;
                }

                @Override
                public int getSpAtkEv() {
                    return spAtkYield;
                }

                @Override
                public int getSpDefEv() {
                    return spDefYield;
                }

                @Override
                public int getPadding() {
                    return evPadded;
                }

                @Override
                public int getUncommonItem() {
                    return uncommonItem;
                }

                @Override
                public int getRareItem() {
                    return rareItem;
                }

                @Override
                public int getGenderRatio() {
                    return genderRatio;
                }

                @Override
                public int getHatchMultiplier() {
                    return hatchMultiplier;
                }

                @Override
                public int getBaseHappiness() {
                    return baseHappiness;
                }

                @Override
                public int getExpRate() {
                    return expRate;
                }

                @Override
                public int getEggGroup1() {
                    return eggGroup1;
                }

                @Override
                public int getEggGroup2() {
                    return eggGroup2;
                }

                @Override
                public int getAbility1() {
                    return ability1;
                }

                @Override
                public int getAbility2() {
                    return ability2;
                }

                @Override
                public int getRunChance() {
                    return runChance;
                }

                @Override
                public int getDexColor() {
                    return dexColor;
                }

                @Override
                public boolean getTm(int idx) {
                    assert idx >= 0 && idx < 128;
                    return (tmLearnset[idx/8] & 1<<(idx%8)) != 0;
                }
            });

            personalBuffer.close();
            count++;
        }


        String[][] pokeTable= new String[dataList.size()+1][30];
        for(int row= 0; row < dataList.size(); row++)
        {
            pokeTable[row][0]= "" + dataList.get(row).getNum();
            pokeTable[row][1]= nameData[row];
            pokeTable[row][2]= "" + dataList.get(row).getHP();
            pokeTable[row][3]= "" + dataList.get(row).getAtk();
            pokeTable[row][4]= "" + dataList.get(row).getDef();
            pokeTable[row][5]= "" + dataList.get(row).getSpe();
            pokeTable[row][6]= "" + dataList.get(row).getSpAtk();
            pokeTable[row][7]= "" + dataList.get(row).getSpDef();
            pokeTable[row][8]= typeArr[dataList.get(row).getType1()];
            pokeTable[row][9]= typeArr[dataList.get(row).getType2()];
            pokeTable[row][10]= "" + dataList.get(row).getCatchRate();
            pokeTable[row][11]= "" + dataList.get(row).getBaseExp();
            pokeTable[row][12]= "" + dataList.get(row).getHpEv();
            pokeTable[row][13]= "" + dataList.get(row).getSpeEv();
            pokeTable[row][14]= "" + dataList.get(row).getAtkEv();
            pokeTable[row][15]= "" + dataList.get(row).getDefEv();
            pokeTable[row][16]= "" + dataList.get(row).getSpAtkEv();
            pokeTable[row][17]= "" + dataList.get(row).getSpDefEv();
            pokeTable[row][18]= itemData[dataList.get(row).getUncommonItem()];
            pokeTable[row][19]= itemData[dataList.get(row).getRareItem()];
            pokeTable[row][20]= "" + dataList.get(row).getGenderRatio();
            pokeTable[row][21]= "" + dataList.get(row).getHatchMultiplier();
            pokeTable[row][22]= "" + dataList.get(row).getBaseHappiness();
            pokeTable[row][23]= growthTableIdArr[dataList.get(row).getExpRate()];
            pokeTable[row][24]= eggGroupArr[dataList.get(row).getEggGroup1()];
            pokeTable[row][25]= eggGroupArr[dataList.get(row).getEggGroup2()];
            pokeTable[row][26]= abilityData[dataList.get(row).getAbility1()];
            pokeTable[row][27]= abilityData[dataList.get(row).getAbility2()];
            pokeTable[row][28]= "" + dataList.get(row).getRunChance();
            pokeTable[row][29]= "" + dataList.get(row).getDexColor();
        }

        BufferedWriter writer= new BufferedWriter(new FileWriter(path + "personalData.csv"));
        String line;
        writer.write("ID Number,Name,HP,Attack,Defense,Speed,Sp. Atk,Sp. Def,Type 1,Type 2,Catch Rate,Exp Drop,HP EV Yield,Spe EV Yield,Attack EV Yield,Defense EV Yield,Sp. Atk EV Yield,Sp. Def EV Yield,Uncommon Held Item,Rare Held Item,Gender Ratio,Hatch Multiplier,Base Happiness,Growth Rate,Egg Group 1,Egg Group 2,Ability 1,Ability 2,Run Chance (Safari Zone only),DO NOT TOUCH\n");
        for(int row= 0; row < dataList.size(); row++)
        {
            line= "";
            for(int col= 0; col < pokeTable[0].length; col++)
            {
                line+= pokeTable[row][col] + ",";
            }
            line+= "\n";
            writer.write(line);
        }
        writer.close();

        String[][] tmTable= new String[nameData.length][100];
        for(int mon= 0; mon < dataList.size(); mon++)
        {
            for(int tm= 0; tm < 100; tm++)
            {
                tmTable[mon][tm]= Boolean.toString(dataList.get(mon).getTm(tm));
            }
        }

        writer= new BufferedWriter(new FileWriter(path + "tmLearnsetData.csv"));
        writer.write("ID Number,Name,");
        for(int i= 0; i < 100; i++)
        {
            writer.write(tmData[i] + ",");
        }
        writer.write("\n,,");
        for(int i= 0; i < 100; i++)
        {
            writer.write(tmNameData[i] + ",");
        }
        writer.write("\n");
        for(int row= 0; row < dataList.size(); row++)
        {
            line= dataList.get(row).getNum() + "," + nameData[row] + ",";
            for(int col= 0; col < tmTable[0].length; col++)
            {
                line+= tmTable[row][col] + ",";
            }
            line+= "\n";
            writer.write(line);
        }
        writer.close();
    }



    public void csvToPersonal(String personalCsv, String tmLearnsetCsv, String outputDir) throws IOException
    {
        String personalPath= path + personalCsv;
        String tmPath= path + tmLearnsetCsv;

        String outputPath;
        if(outputDir.contains("Recompile"))
        {
            outputPath= path + "temp" + File.separator+ outputDir;
        }
        else
        {
            outputPath= path + File.separator + outputDir;
        }

        int xValue;
        int yValue;

        BufferedReader reader= new BufferedReader(new FileReader(personalPath));
        if(!personalCsv.substring(personalCsv.length()-4).equals(".csv"))
        {
            throw new RuntimeException("The provided personal data file is not a .csv");
        }
        if(!tmLearnsetCsv.substring(tmLearnsetCsv.length()-4).equals(".csv"))
        {
            throw new RuntimeException("The provided TM learnset data file is not a .csv");
        }

        if(!new File(outputPath).exists())
        {
            if(!new File(outputPath).mkdir())
            {
                throw new RuntimeException("Could not create output directory");
            }
        }


        ArrayList<String> personalLines= new ArrayList<>();
        String line;

        while((line=reader.readLine()) != null)
        {
            personalLines.add(line);
        }
        reader.close();
        personalLines.remove(0);
        xValue= personalLines.get(0).split(",").length;
        yValue= personalLines.size();

        for(int row= 0; row < yValue; row++)
        {
            for(int i= 0; i < 2; i++)
            {
                line= personalLines.get(row);
                personalLines.set(row, line.substring(line.indexOf(",")+1));
            }
            personalLines.set(row,personalLines.get(row).substring(0,personalLines.get(row).length()-1).trim());
        }

        String[] csvRow;
        ArrayList<PersonalData> personalData= new ArrayList<>();
        for(int row= 0; row < yValue; row++)
        {
            csvRow= personalLines.get(row).split(",");
            String[] finalCsvRow = csvRow;
            int finalRow = row;

            personalData.add(new PersonalData() {
                @Override
                public int getNum() {
                    return finalRow;
                }

                @Override
                public int getHP() {
                    return Integer.parseInt(finalCsvRow[0]);
                }

                @Override
                public int getAtk() {
                    return Integer.parseInt(finalCsvRow[1]);
                }

                @Override
                public int getDef() {
                    return Integer.parseInt(finalCsvRow[2]);
                }

                @Override
                public int getSpe() {
                    return Integer.parseInt(finalCsvRow[3]);
                }

                @Override
                public int getSpAtk() {
                    return Integer.parseInt(finalCsvRow[4]);
                }

                @Override
                public int getSpDef() {
                    return Integer.parseInt(finalCsvRow[5]);
                }

                @Override
                public int getType1() {
                    return getType(finalCsvRow[6]);
                }

                @Override
                public int getType2() {
                    return getType(finalCsvRow[7]);
                }

                @Override
                public int getCatchRate() {
                    return Integer.parseInt(finalCsvRow[8]);
                }

                @Override
                public int getBaseExp() {
                    return Integer.parseInt(finalCsvRow[9]);
                }

                @Override
                public int getHpEv() {
                    return Integer.parseInt(finalCsvRow[10]);
                }

                @Override
                public int getSpeEv() {
                    return Integer.parseInt(finalCsvRow[11]);
                }

                @Override
                public int getAtkEv() {
                    return Integer.parseInt(finalCsvRow[12]);
                }

                @Override
                public int getDefEv() {
                    return Integer.parseInt(finalCsvRow[13]);
                }

                @Override
                public int getSpAtkEv() {
                    return Integer.parseInt(finalCsvRow[14]);
                }

                @Override
                public int getSpDefEv() {
                    return Integer.parseInt(finalCsvRow[15]);
                }

                @Override
                public int getPadding() {
                    return 0;
                }

                @Override
                public int getUncommonItem() {
                    return getItem(finalCsvRow[16]);
                }

                @Override
                public int getRareItem() {
                    return getItem(finalCsvRow[17]);
                }

                @Override
                public int getGenderRatio() {
                    return Integer.parseInt(finalCsvRow[18]);
                }

                @Override
                public int getHatchMultiplier() {
                    return Integer.parseInt(finalCsvRow[19]);
                }

                @Override
                public int getBaseHappiness() {
                    return Integer.parseInt(finalCsvRow[20]);
                }

                @Override
                public int getExpRate() {
                    return getGrowthRate(finalCsvRow[21]);
                }

                @Override
                public int getEggGroup1() {
                    return getEggGroup(finalCsvRow[22]);
                }

                @Override
                public int getEggGroup2() {
                    return getEggGroup(finalCsvRow[23]);
                }

                @Override
                public int getAbility1() {
                    return getAbility(finalCsvRow[24]);
                }

                @Override
                public int getAbility2() {
                    return getAbility(finalCsvRow[25]);
                }

                @Override
                public int getRunChance() {
                    return Integer.parseInt(finalCsvRow[26]);
                }

                @Override
                public int getDexColor() {
                    return Integer.parseInt(finalCsvRow[27]);
                }

                @Override
                public boolean getTm(int idx) {
                    return false;
                }
            });
        }



        CsvReader csvReader= new CsvReader(tmPath);
        csvReader.skipLine();
        BitStream[] tmLearnsetData = new BitStream[csvReader.length()-1];
        for(int i= 0; i < tmLearnsetData.length; i++)
        {
            tmLearnsetData[i] = new BitStream();

            String[] strs = csvReader.next();
            for (String str : strs) {
                tmLearnsetData[i].append(Boolean.parseBoolean(str));
            }

            tmLearnsetData[i].append(false, 28);
            System.out.println(tmLearnsetData[i]);
        }

        outputPath+= File.separator;
        BinaryWriter writer;
        for(int i= 0; i < personalData.size(); i++)
        {

            PersonalData data= personalData.get(i);
            writer= new BinaryWriter(new File(outputPath + i + ".bin"));
            writer.writeBytes(data.getHP(), data.getAtk(), data.getDef(), data.getSpe(), data.getSpAtk(), data.getSpDef(), data.getType1(), data.getType2(), data.getCatchRate(), data.getBaseExp());
            writer.writeShort(parseShort("0000", evYieldBinary(data.getSpDefEv()), evYieldBinary(data.getSpAtkEv()), evYieldBinary(data.getDefEv()), evYieldBinary(data.getAtkEv()), evYieldBinary(data.getSpeEv()), evYieldBinary(data.getHpEv())));
            writer.writeShort((short)data.getUncommonItem());
            writer.writeShort((short)data.getRareItem());
            writer.writeBytes(data.getGenderRatio(),data.getHatchMultiplier(),data.getBaseHappiness(),data.getExpRate(),data.getEggGroup1(),data.getEggGroup2(),data.getAbility1(),data.getAbility2(),data.getRunChance(),data.getDexColor());
            writer.writeBytes(0x00,0x00);
            System.out.println(i + ":   " + tmLearnsetData[i]);
            writer.write(tmLearnsetData[i].toBytes());
        }
    }

    public void csvToPersonal2(String personalCsv, String tmLearnsetCsv, String outputDir) throws IOException
    {
        String personalPath= path + personalCsv;
        String tmPath= path + tmLearnsetCsv;

        String outputPath;
        if(outputDir.contains("Recompile"))
        {
            outputPath= path + "temp" + File.separator+ outputDir;
        }
        else
        {
            outputPath= path + File.separator + outputDir;
        }

        int xValue;
        int yValue;

        if(!personalCsv.substring(personalCsv.length()-4).equals(".csv"))
        {
            throw new RuntimeException("The provided personal data file is not a .csv");
        }
        if(!tmLearnsetCsv.substring(tmLearnsetCsv.length()-4).equals(".csv"))
        {
            throw new RuntimeException("The provided TM learnset data file is not a .csv");
        }

        if(!new File(outputPath).exists() && !new File(outputPath).mkdirs())
        {
            throw new RuntimeException("Could not create output directory");
        }

        ArrayList<PersonalData> personalList= new ArrayList<>();
        CsvReader personalReader= new CsvReader(personalPath);

        long[][] tmLongs= new long[personalReader.length()][2];
        CsvReader tmReader= new CsvReader(tmPath);
        for(int i= 0; i < personalReader.length(); i++)
        {
            String[] mon= personalReader.next();
            String[] tmLearnset= tmReader.next();
            int finalI = i;
            personalList.add(new PersonalData() {
                @Override
                public int getNum() {
                    return finalI;
                }

                @Override
                public int getHP() {
                    return Integer.parseInt(mon[0]);
                }

                @Override
                public int getAtk() {
                    return Integer.parseInt(mon[1]);
                }

                @Override
                public int getDef() {
                    return Integer.parseInt(mon[2]);
                }

                @Override
                public int getSpe() {
                    return Integer.parseInt(mon[3]);
                }

                @Override
                public int getSpAtk() {
                    return Integer.parseInt(mon[4]);
                }

                @Override
                public int getSpDef() {
                    return Integer.parseInt(mon[5]);
                }

                @Override
                public int getType1() {
                    return getType(mon[6]);
                }

                @Override
                public int getType2() {
                    return getType(mon[7]);
                }

                @Override
                public int getCatchRate() {
                    return Integer.parseInt(mon[8]);
                }

                @Override
                public int getBaseExp() {
                    return Integer.parseInt(mon[9]);
                }

                @Override
                public int getHpEv() {
                    return Integer.parseInt(mon[10]);
                }

                @Override
                public int getSpeEv() {
                    return Integer.parseInt(mon[11]);
                }

                @Override
                public int getAtkEv() {
                    return Integer.parseInt(mon[12]);
                }

                @Override
                public int getDefEv() {
                    return Integer.parseInt(mon[13]);
                }

                @Override
                public int getSpAtkEv() {
                    return Integer.parseInt(mon[14]);
                }

                @Override
                public int getSpDefEv() {
                    return Integer.parseInt(mon[15]);
                }

                @Override
                public int getPadding() {
                    return 0;
                }

                @Override
                public int getUncommonItem() {
                    return getItem(mon[16]);
                }

                @Override
                public int getRareItem() {
                    return getItem(mon[17]);
                }

                @Override
                public int getGenderRatio() {
                    return Integer.parseInt(mon[18]);
                }

                @Override
                public int getHatchMultiplier() {
                    return Integer.parseInt(mon[19]);
                }

                @Override
                public int getBaseHappiness() {
                    return Integer.parseInt(mon[20]);
                }

                @Override
                public int getExpRate() {
                    return getGrowthRate(mon[21]);
                }

                @Override
                public int getEggGroup1() {
                    return getEggGroup(mon[22]);
                }

                @Override
                public int getEggGroup2() {
                    return getEggGroup(mon[23]);
                }

                @Override
                public int getAbility1() {
                    return getAbility(mon[24]);
                }

                @Override
                public int getAbility2() {
                    return getAbility(mon[25]);
                }

                @Override
                public int getRunChance() {
                    return Integer.parseInt(mon[26]);
                }

                @Override
                public int getDexColor() {
                    return Integer.parseInt(mon[27]);
                }

                @Override
                public boolean getTm(int idx) {
                    assert idx >= 0 && idx < 128;
                    return tmLearnset[idx].toLowerCase().equals("true");
                }
            });

            StringBuilder tmString= new StringBuilder("0000000000000000000000000000");
            for(int tm= 100; tm != -1; tm--)
            {
                if(personalList.get(i).getTm(tm))
                {
                    tmString.append("1");
                }
                else
                {
                    tmString.append("0");
                }
            }
            tmLongs[i][0]= Long.parseLong(tmString.substring(0,64),2);
            tmLongs[i][1]= Long.parseLong(tmString.substring(64),2);
        }

        BinaryWriter writer;
        for(int i= 0; i < personalList.size(); i++)
        {
            writer= new BinaryWriter(outputPath + File.separator + i + ".bin");
            PersonalData personalData= personalList.get(i);

            writer.writeBytes(personalData.getHP(),personalData.getAtk(),personalData.getDef(),personalData.getSpe(),personalData.getSpAtk(),personalData.getSpDef(),personalData.getType1(),personalData.getType2(),personalData.getCatchRate(),personalData.getBaseExp());
            writer.writeShort(parseShort(Integer.toBinaryString(personalData.getHpEv()),Integer.toBinaryString(personalData.getSpeEv()),Integer.toBinaryString(personalData.getAtkEv()),Integer.toBinaryString(personalData.getDefEv()),Integer.toBinaryString(personalData.getSpAtkEv()),Integer.toBinaryString(personalData.getSpDefEv()),"0000"));
            writer.writeShorts(personalData.getUncommonItem(),personalData.getRareItem());
            writer.writeBytes();
        }
    }

    public void csvReformat(String tmLearnsetCsv) throws IOException
    {
        ArrayList<String[][]> reformat= new ArrayList<>();
        CsvReader csvReader= new CsvReader(path + tmLearnsetCsv);

        for(int i= 0; i < csvReader.length(); i++)
        {
            String[][] edited= new String[4][25];
            String[] mon= csvReader.next();
            int idx= 0;
            for(int row= 0; row < 4; row++)
            {
                if(row == 1)
                {
                    idx= 1;
                }
                if(row == 2)
                {
                    idx= 2;
                }
                if(row == 3)
                {
                    idx= 3;
                }
                for(int col= 0; col < 25; col++)
                {
                    edited[row][col]= mon[idx+=4];
                }
            }
            reformat.add(edited);
        }

        BufferedWriter writer= new BufferedWriter(new FileWriter(path + "Reformatted TM Learnset.csv"));
        writer.write("Dex Number,Pokémon\n");
        for(int i= 0; i < reformat.size(); i++)
        {
            String[][] mon= reformat.get(i);
            writer.write(i + "," + nameData[i] + ",");
            int idx= 1;
            int hm= 1;
            for(int row= 0; row < mon.length; row++)
            {
                for(int col= 0; col < mon[0].length; col++)
                {
                    if(idx < 10)
                    {
                        writer.write(mon[row][col] + "," + "TM0" + idx + ",");
                        idx++;
                    }
                    else if(idx <= 92)
                    {
                        writer.write(mon[row][col] + "," + "TM" + idx + ",");
                        idx++;
                    }
                    else
                    {
                        writer.write(mon[row][col] + "," + "HM0" + hm + ",");
                        hm++;
                    }
                }
                if(hm != 8)
                {
                    writer.write("\n, ,");
                }
                else
                {
                    writer.write("\n");
                }
            }
        }
        writer.close();
    }






    private void sort (File arr[])
    {
        Arrays.sort(arr, Comparator.comparingInt(PersonalEditor::fileToInt));
    }

    private static int fileToInt (File f)
    {
        return Integer.parseInt(f.getName().split("\\.")[0]);
    }

    private static int getHp (short x)
    {
        return x & 0x03;
    }

    private static int getSpe (short x)
    {
        return (x >> 2) & 0x03;
    }

    private static int getAtk (short x)
    {
        return (x >> 4) & 0x03;
    }

    private static int getDef (short x)
    {
        return (x >> 6) & 0x03;
    }

    private static int getSpAtk (short x)
    {
        return (x >> 8) & 0x03;
    }

    private static int getSpDef (short x)
    {
        return (x >> 10) & 0x03;
    }

    private static int getPadded (short x)
    {
        return (x >> 12) & 0x0F;
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

    private static int getEggGroup(String group)
    {
        for(int i= 0; i < eggGroupArr.length; i++)
        {
            if(group.equals(eggGroupArr[i]))
            {
                return i;
            }
        }
        throw new RuntimeException("Invalid egg group entered");
    }

    private static int getAbility(String ability)
    {
        for(int i= 0; i < abilityData.length; i++)
        {
            if(ability.equals(abilityData[i]))
            {
                return i;
            }
        }
        throw new RuntimeException("Invalid ability entered");
    }

    private static int getItem(String item)
    {
        for(int i= 0; i < itemData.length; i++)
        {
            if(item.equals(itemData[i]))
            {
                return i;
            }
        }
        throw new RuntimeException("Invalid item entered");
    }

    private static int getGrowthRate(String growthRate)
    {
        for(int i= 0; i < growthTableIdArr.length; i++)
        {
            if(growthRate.equals(growthTableIdArr[i]))
            {
                return i;
            }
        }
        throw new RuntimeException("Invalid growth rate id entered");
    }

    private static String evYieldBinary(int num)
    {
        if(num > 3)
        {
            throw new RuntimeException("Invalid ev yield entered");
        }

        if(num == 0)
        {
            return "00";
        }
        else if(num == 1)
        {
            return "01";
        }
        else if(num == 2)
        {
            return "10";
        }
        else
        {
            return "11";
        }
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

    private static byte[] parseShorts(short[] shorts)
    {
        byte[] buf= new byte[shorts.length*2];
        for(int i= 0; i < shorts.length; i+=2)
        {
            short s= shorts[i];
            buf[i] = (byte) (s & 0xff);
            buf[i+1] = (byte) ((s >> 8) & 0xff);
        }
        return buf;
    }

    private static long parseLong(String fullLong)
    {
        long num= 0;
        for(int i= 0; i < fullLong.length(); i++)
        {
            String thisEv= fullLong.substring(fullLong.length()-i-1,fullLong.length()-i);
            if(thisEv.equals("1"))
            {
                num+= Math.pow(2,i);
            }
        }
        return num;
    }

    private static String[] reverse(String[] arr)
    {
        for(int i= 0; i < arr.length/2; i++)
        {
            String foo= arr[i];
            arr[i]= arr[arr.length-i-1];
            arr[arr.length-i-1]= foo;
        }
        return arr;
    }

    private static byte toByte(String[] arr) {
        int ret = 0;
        for (int i=0; i<arr.length; i++) {
            ret |= (Boolean.parseBoolean(arr[i])?1:0) << i;
        }
        return (byte) ret;
    }
}
