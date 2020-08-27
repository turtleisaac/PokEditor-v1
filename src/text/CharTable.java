package text;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;


public class CharTable
{
    private static HashMap<Integer,String> mapGet = new HashMap<>();
    private static HashMap<String,Integer> mapWrite = new HashMap<>();
//    public static void main(String[] args) throws IOException
//    {
//        CharTable table= new CharTable();
//        table.test();
//    }
    public CharTable() throws IOException
    {
        BufferedReader reader= new BufferedReader(new FileReader(System.getProperty("user.dir") + File.separator + "CharFix.txt"));
        String line;

        while((line= reader.readLine()) != null)
        {
            String[] lineArr= line.trim().split(" ");
            int hexId= Integer.parseInt(lineArr[0].substring(2),16);
            String character= lineArr[1];

            if(hexId != 0x1DE)
            {
                mapGet.put(hexId,character);
                mapWrite.put(character,hexId);
            }
            else
            {
                mapGet.put(hexId," ");
                mapWrite.put(" ",hexId);
            }
        }
    }

    public String getCharacter(int p)
    {
        return mapGet.getOrDefault(p, "0");
    }

    public int writeCharacter(String p)
    {
        return mapWrite.getOrDefault(p, 0);
    }

//    public void test() throws IOException
//    {
//        BufferedReader reader= new BufferedReader(new FileReader(System.getProperty("user.dir") + File.separator + "CharFix.txt"));
//        String line;
//
//        while((line= reader.readLine()) != null)
//        {
//            String[] lineArr= line.trim().split(" ");
//            if(lineArr.length != 2)
//            {
//                System.out.println(Arrays.toString(lineArr) + " MOOO");
//            }
//
//        }
//    }
}
