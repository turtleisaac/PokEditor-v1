import java.io.*;
import java.util.*;
/**
 * Author: turtleisaac
 * Start Date: 5/18/2020
 * End Date: 5/28/2020
 *
 * Thanks/ Credits to:
 * PlatinumMaster for writing the python NARCTool version I used as a reference
 * Vendor, FrankieD, and Zekromaegis for updating and releasing new versions of PlatinumMaster's NARCTool
 * FrankieD for assisting me as we figured out how to read a narc and correctly extract other files from it
 */
public class NarcEditor
{
    public static void main(String[] args) throws Exception
    {
        NarcEditor narc= new NarcEditor(); //creates new NarcEditor object
        if(args.length != 0) //checks if arguments were provided
        {
            if(args[0].toLowerCase().equals("unpack")) //if first argument is unpack
            {
                narc.unpack(args[1]); //run NARC.unpack with second argument as parameter
                System.exit(0);
            }
            else if(args[0].toLowerCase().equals("pack"))
            {
                narc.pack(narc.extractPath + args[1], args[2]);
                System.exit(0);
            }
        }
            throw new RuntimeException("\nInvalid arguments. Usage is as follows: java -jar NARCtowl.jar <arguments> \n Unpacking: java -jar NARCtowl.jar unpack <file name (include .narc)> \n Packing: java -jar NARCtowl.jar pack <name of directory in extracted folder> <name of file to create (do not include .narc)> \n   Note: Narc must be in the same folder as NARCtowl.jar \n   Note: Directory to construct narc from must be in \"extracted\" directory");
    }

    private String path= System.getProperty("user.dir") + File.separator; //creates a new String field containing user.dir and File.separator (/ on Unix systems, \ on Windows)
    private String extractPath= path + "temp" + File.separator; //creates a new String field containing path, the directory "extracted", and File.separator (/ on Unix systems, \ on Windows)
    public static String[] extensionStrings; //creates a new String[] to be filled later (see static)
    private ArrayList<byte[]> fileExtensions = new ArrayList<>(); //creates a new ArrayList of byte[] to contain the file extension hex strings

    private static final int MAX_SIZE = 1024*1024;

    static
    {
        extensionStrings= new String[]{".bmd0", ".btx0", ".ncsr", ".nclr",  ".ncgr", ".nanr", ".nmar", ".nmcr", ".ncer"}; //creates a String[] that contains each file extension corresponding to the hex string at the same index in identifiers.hex
    }

    public NarcEditor() throws IOException
    {
        String extensionPath= path + "Program Files" + File.separator + "identifiers.hex"; //creates a String containing the path to identifiers.hex (constant)
        for(int i= 0; i < new File(extensionPath).length()/4; i++) //goes through the file four bytes at a time
        {
            Buffer extensions = new Buffer(extensionPath); //creates a new Buffer object for reading from identifiers.hex
            fileExtensions.add(extensions.readBytes(4)); //adds the current set of four bytes to the fileExtensions ArrayList as a byte[]
            extensions.close(); //closes Buffer object extensions' internal BufferedInputStream and flushes data
        }
    }

    public void unpack(String narc) throws Exception
    {
        Scanner scanner = new Scanner(System.in); //creates Scanner object

        String noExtension= narc.substring(0,narc.length()-5);
        if(noExtension.contains(File.separator))
        {
            noExtension= noExtension.split(File.separator)[narc.split(File.separator).length-1];
        }
        if(!new File(extractPath + noExtension).exists()) //checks if "extracted" directory in user.dir does not exist
        {
            if (!new File(extractPath + noExtension).mkdirs()) //if it does not exist, creates "extracted" directory and directory matching name of parameter narc if able to, otherwise see RuntimeException
            {
                throw new RuntimeException("Could not create narc directory. Check write permissions");
            }
        }
        else //if a folder matching the name of the input narc already exists within "extracted"
        {
            while(true) //this makes sure that the program will keep asking if the user inputs an invalid response
            {
                System.out.println("A directory named " + noExtension + " already exists. Do you want to overwrite? (y/N)"); //asks user to confirm overwriting existing folder
                String input= scanner.nextLine().toLowerCase(); //prompt for user input
                if(input.equals("y")) //checks to see if the user typed "y", meaning "yes"
                {
                    if(!clearDirectory(new File(extractPath + noExtension))) //clears specified directory if able to, otherwise see RuntimeException
                    {
                        throw new RuntimeException("Could not delete contents of existing " + noExtension + " directory. Check write permissions");
                    }
                    if(!new File(extractPath + noExtension).delete()) //deletes existing specified directory if able to, otherwise see RuntimeException
                    {
                        throw new RuntimeException("Could not delete existing " + noExtension + " directory. Check write permissions");
                    }
                    if(!new File(extractPath + noExtension).mkdir()) //creates new folder matching the name of the input narc if able to, otherwise see RuntimeException
                    {
                        throw new RuntimeException("Could not create " + noExtension + " directory. Check write permissions.");
                    }
                    break; //escapes the infinite loop and continues to narc unpacking
                }
                else if(input.equals("n")) //checks to see if the user typed "N", meaning "no"
                {
                    System.out.println("Process aborted"); //alerts user to the process being aborted
                    System.exit(0); //escapes the infinite loop by ending program
                }
                else //if the user input does not match either "y" or "N"
                {
                    System.out.println("User input does not match either specified input. Please try again."); //tells user to try again, returns to top of while-loop following this
                }
            }
        }
        extractPath+= noExtension + File.separator;

            //Begins the unpacking of narc
        Buffer buffer = new Buffer(narc); //creates new "Buffer" object using the path to the narc as a parameter

        String magic = buffer.readString(4); //sets String "magic" to the ascii value of the first 4 bytes
        if (!magic.equals("NARC")) { //if the file doesn't start with the NARC hex identifier
            throw new RuntimeException("Not a NARC file"); //end program, throws runtime exception
        }
        int constant = buffer.readInt(); //data between NARC hex identifier and the file size, is constant in all narcs
        int fileSize = buffer.readInt(); //how many bytes are in the file
        short headerSize = buffer.readShort(); //no idea tbh
        short numSections = buffer.readShort(); //no idea tbh

            //File Allocation Table Block (FATB)
        String fatbMagic= buffer.readString(4); //sets String "fatbMagic" to the BTAF identifier
        int fatbSize= buffer.readInt(); //obtains size of the incremental length table (number of bytes)
        int numFiles= buffer.readInt(); //obtains number of files to be made

        ArrayList<NarcSubFile> subFiles = new ArrayList<>(); //creates an ArrayList of NarcSubFile objects that contain the starting offset, ending offset, and name of each subfile
        int lastEnd = 0;
        int count= 0;
        for(int i= 0; i < numFiles; i++) //runs numFiles times, assigns starting offset, ending offset, and name data representing one file to each index
        {
            int startingOffset= buffer.readInt(); //the next four bytes, which contain the starting offset
            int endingOffset= buffer.readInt(); //the next four bytes, which contain the ending offset
            if (startingOffset != lastEnd) {
                count++;
            }
            int startEndOffset= startingOffset-lastEnd;
            lastEnd = endingOffset;
            int id = i; //creates a final int variable that can be used in calling the interface NarcSubFile, as the int "i" is not immutable and constantly changing
            subFiles.add(new NarcSubFile() { //adds a new NarcSubFile object to the ArrayList
                @Override
                public int getStartingOffset() {
                    return startingOffset; //stores the starting offset in the object
                }

                @Override
                public int getEndingOffset() {
                    return endingOffset; //stores the ending offset in the object
                }

                @Override
                public int getTrashBytes() {
                    return startEndOffset;
                } //stores the number of trash bytes to remove later on

                @Override
                public String getName() {
                    return "" + id; //stores the "name" (number in this scenario) in the object
                }
            });
        }
            //File Name Table Block (FNTB)
        String fntbMagic= buffer.readString(4); //sets String "fntbMagic" to the BTNF identifier
        int fntbSize= buffer.readInt(); //obtains the size of the fntb (number of bytes)
        int fntbStartOffset= buffer.readInt(); //the next four bytes, which contain the starting offset of the fntb
        int fntbFirstFilePos= buffer.readShort(); //the next two bytes, which contain the first file position
        int fntbNDir= buffer.readShort(); //the next two bytes, which contain the number of directories


            //File Images (FIMG)
        int fimgOffset= buffer.getPosition(); //gets current position
        String fimgMagic= buffer.readString(4); //sets String "fimgMagic" to the GMIF identifier
        int fimgSize= buffer.readInt(); //obtains the size of the fimg (number of bytes)
        BufferedOutputStream out; //creates new BufferedOutputStream to be defined later in for-loop

        for(int i= 0; i < numFiles; i++) //goes through each file in ArrayList of NarcSubFile objects
        {
            int trashBytes= subFiles.get(i).getTrashBytes(); //sets int "trashBytes" to the number of trash bytes between previous and current file
            while(trashBytes != 0) //while there are still trash bytes
            {
                int throwAway= buffer.readByte(); //reads one byte and "throws it away"
                trashBytes--; //lowers the amount of trash bytes left by one
            }
            byte[] bytes; //creates a new byte[] to be defined later in for-loop
            String extension= ""; //creates a new String to contain the file extension of the current file being made
            bytes= buffer.readBytes(subFiles.get(i).getEndingOffset()-subFiles.get(i).getStartingOffset()); //reads the amount of bytes equal to the length of the current file
                byte[] identifier= new byte[4]; //creates a new byte[] of size 4 to contain the first four bytes of a file so file extension can be appended properly
                System.arraycopy(bytes, 0, identifier, 0,4); //copies the first four bytes of the current file's contents to the identifier byte[]
                int index= -1; //creates a new int variable and sets value to -1
                for(int j= 0; j < fileExtensions.size(); j++) //goes through each byte[] in the fileExtensions ArrayList
                {
                    byte[] extensionArr= fileExtensions.get(j); //creates a new byte[] and sets it to the contents of the current element in the fileExtensions ArrayList
                    if(compareBytes(identifier,extensionArr)) //if the two byte[] are the same
                    {
                        index= j; //changes index to the current index in fileExtensions
                    }
                }
                if(index != -1) //if index was changed and a matching file extension did exist
                {
                    extension= extensionStrings[index]; //sets extension to the value of element "index" in the String[] extensionStrings
                }
                else //if there were no matching byte[] in fileExtensions
                {
                    extension= ".bin"; //sets extension to the default ".bin"
                }

                //writes data to output files
            out= new BufferedOutputStream(new FileOutputStream(extractPath + subFiles.get(i).getName() + extension)); //creates new BufferedOutputStream object using specified name/ numbering from subFiles ArrayList of NarcSubFiles
            out.write(bytes); //writes the bytes read from the narc located in byte[] bytes to the output file
            out.close(); //closes BufferedOutputStream and flushes data
        }
        buffer.close(); //closes Buffer object buffer's internal BufferedInputStream and flushes data
        System.out.println("Process completed. Output directory can be found at: " + extractPath + "\n");
    }

    public void pack(String directory, String name) throws IOException
    {
        if(new File(path + "temp" + File.separator + name + ".narc").exists()) //if a narc matching parameter name exists
        {
            Scanner scanner= new Scanner(System.in); //creates a new Scanner object
            while(true) //will keep running until valid input is received
            {
                System.out.println("A file named " + name + ".narc already exists. Overwrite? (y/N)");
                String input= scanner.nextLine().toLowerCase(); //prompts user for response
                if(input.equals("y")) //if user input equals "y", meaning "yes"
                {
                    break; //escapes the infinite loop and continues to narc packing
                }
                if(input.equals("n")) //if user input equals "N", meaning "no"
                {
                    System.out.println("Process aborted");
                    System.exit(0); //closes program
                }
                else //if the user input does not match either "y" or "N"
                {
                    System.out.println("User input does not match either specified input. Please try again."); //tells user to try again, returns to top of while-loop following this
                }
            }
        }
        Buffer fimgBuffer; //creates new Buffer object

        ArrayList<TableSubFile> fimgTable= new ArrayList<>(); //creates new ArrayList of tableSubFile instances
        int fimgPosition= 0; //creates a counter to track the length of the fimg section
        File file; //creates a File object to be defined later in for-loop
        List<File> fileList = new ArrayList<>(Arrays.asList(Objects.requireNonNull(new File(directory).listFiles()))); //creates a List of File objects representing every file in specified parameter directory
        fileList.removeIf(File::isHidden); //removes all File objects from List that are hidden

        File[] files = fileList.toArray(new File[0]); //creates an array of File objects using the contents of the modified List
        sort(files); //sorts files numerically (0.bin, 1.bin, 2.bin, etc...)

        for(int i= 0; i < files.length; i++) //goes through each file in order
        {
            if(!(file = files[i]).isHidden()) //prevents the program from reading hidden files (damn Mac trash files)
            {
                int fileLength= (int)file.length(); //tracks the length of the current file
                fimgBuffer= new Buffer(file.toString()); //sets the Buffer object to the current file
                byte[] contents= fimgBuffer.readBytes(fileLength); //contains the contents of the file
                int fimgPosition2= fimgPosition; //creates a local, final variable to hold the fimgPosition data

                fimgTable.add(new TableSubFile() {
                    @Override
                    public int getStartingOffset() {
                        return fimgPosition2;
                    }

                    @Override
                    public int getEndingOffset() {
                        return fimgPosition2+contents.length;
                    }

                    @Override
                    public byte[] getFileContents() {
                        return contents;
                    }

                    @Override
                    public int length() {
                        return contents.length;
                    }
                });
                fimgPosition+= contents.length; //increments fimgPosition by the length of the file contents for purpose of keeping track of starting offsets/ ending offsets and the length of the section
                fimgBuffer.close();
            }
        }

        MemBuf fimgBuf = MemBuf.create(); //creates new MemBuf object of maximum size of 1 MB for fimg section
        int fimgSize= fimgPosition+8; //creates new int variable to hold length of fimg table. Additional 8 bytes are for the header length
        MemBuf.MemBufWriter writer = fimgBuf.writer().writeBytes(0x47, 0x4D, 0x49, 0x46).writeInt(fimgSize); //writes fimg magic number
        int count= 1;

        for(int i= 0; i < fimgTable.size(); i++) //goes through each TableSubFile object in the ArrayList
        {
            TableSubFile tableSubFile= fimgTable.get(i);
            writer.write(tableSubFile.getFileContents()); //writes contents of each TableSubFile object
            if(tableSubFile.getStartingOffset()%4 != 0) //sees if file doesn't start at an offset divisible by 4
            {
                writer.skip(4-(tableSubFile.getStartingOffset()%4)); //if so, skip 4 - remainder amount of bytes
                fimgSize+= 4-(tableSubFile.getStartingOffset()%4);
                System.out.println("File " + i + " padding added: " + (4-(tableSubFile.getStartingOffset()%4)));
                fimgTable.set(i, new TableSubFile() {
                    @Override
                    public int getStartingOffset() {
                        return tableSubFile.getStartingOffset()+ 4-(tableSubFile.getStartingOffset()%4);
                    }

                    @Override
                    public int getEndingOffset() {
                        return tableSubFile.getEndingOffset()+ 4-(tableSubFile.getStartingOffset()%4);
                    }

                    @Override
                    public byte[] getFileContents() {
                        return tableSubFile.getFileContents();
                    }

                    @Override
                    public int length() {
                        return tableSubFile.getFileContents().length;
                    }
                });
            }
            count++;
        }


        MemBuf fntbBuf = MemBuf.create(); //creates new MemBuf object of maximum size of 1 MB for fntb section
        int fntbSize= 16; //creates and sets variable to 16 (length of fntb in all HGSS files) (constant)
        fntbBuf.writer().writeBytes(0x42, 0x54, 0x4E, 0x46, 0x10, 0x00, 0x00, 0x00, 0x04, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01, 0x00); //writes fntb data (constant)


        MemBuf fatbBuf= MemBuf.create(); //creates new MemBuf object of maximum size of 1 MB for fatb section
        writer = fatbBuf.writer(); //initializes writer
        writer.writeBytes(0x42, 0x54, 0x41, 0x46); //writes fatb header
        int fatbSize= (fimgTable.size()*8)+12; //sets size of fatb section to the amount of files multiplied by 8, then adds 12 bytes for fatb header
        writer.writeInt(fatbSize); //writes file size as four bytes
        writer.writeInt(fimgTable.size()); //writes number of files in the narc as four bytes
        for(TableSubFile tableSubFile : fimgTable) //goes through each TableSubFile object in the ArrayList
        {
            writer.writeInt(tableSubFile.getStartingOffset()); //writes the starting offset as four bytes
            writer.writeInt(tableSubFile.getEndingOffset()); //writes the ending offset as four bytes
        }

        BinaryWriter narcWriter= new BinaryWriter(path + "temp" + File.separator + name + ".narc"); //creates new file to output to in user directory with parameter "name" + .narc
        narcWriter.writeBytes(0x4E, 0x41, 0x52, 0x43, 0xFE, 0xFF, 0x00, 0x01); //writes the narc magic number and constant data (constant)
        int fileLength= fimgSize+fatbSize+fntbSize+16; //creates new int variable and sets it to combined lengths of fimg section, fntb section, fatb section, and file header
        narcWriter.writeInt(fileLength); //writes the file length as four bytes
        narcWriter.writeShort((short)0x10); //writes the header size as two bytes (16 bytes in every narc) (constant)
        narcWriter.writeShort((short)0x03); //writes the number of sections as two bytes (3 sections in every narc) (constant)
        narcWriter.write(fatbBuf.reader().getBuffer()); //writes the entire fatb buffer
        narcWriter.write(fntbBuf.reader().getBuffer()); //writes the entire fntb buffer
        narcWriter.write(fimgBuf.reader().getBuffer()); //writes the entire fimg buffer
        narcWriter.close(); //closes writer and flushes data

//        System.out.println("Process completed. Output file can be found at: " + path + "temp" + File.separator + name + ".narc");

    }

    public void packRewrite(String directory, String output)
    {
        String dataPath= path + directory;
        ArrayList<TableSubFile> dataList= new ArrayList<>();

        List<File> fileList = new ArrayList<>(Arrays.asList(new File(dataPath).listFiles())); //creates a List of File objects representing every file in specified parameter directory
        fileList.removeIf(File::isHidden); //removes all File objects from List that are hidden

        File[] files = fileList.toArray(new File[0]); //creates an array of File objects using the contents of the modified List
        sort(files); //sorts files numerically (0.bin, 1.bin, 2.bin, etc...)

        int fimgPosition= 0;
        for(int i= 0; i < files.length; i++)
        {
            File file= files[i];
            Buffer buffer= new Buffer(file.toString());
            int start= fimgPosition;
            byte[] contents= buffer.readBytes(12);

            dataList.add(new TableSubFile() {
                @Override
                public int getStartingOffset() {
                    return fimgPosition;
                }

                @Override
                public int getEndingOffset() {
                    return 0;
                }

                @Override
                public byte[] getFileContents() {
                    return contents;
                }

                @Override
                public int length() {
                    return 0;
                }
            });

        }
    }

    private boolean compareBytes(byte[] one, byte[] two) //compares two byte arrays of length 4
    {
        return (one[0] == two[0] && one[1] == two[1] && one[2] == two[2] && one[3] == two[3]); //checks to see if all bytes match
    }

    private void sort(File arr[])
    {
        Arrays.sort(arr, Comparator.comparingInt(NarcEditor::fileToInt));
    }

    private static int fileToInt(File f)
    {
        return  Integer.parseInt(f.getName().split("\\.")[0]);
    }

    private boolean clearDirectory(File directory)
    {
        for(File file : directory.listFiles())
        {
            if(!file.delete())
            {
                return false;
            }

        }
        return true;
    }
}
