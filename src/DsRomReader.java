import java.io.*;
import java.util.*;

public class DsRomReader
{

    public static void main(String[] args) throws Exception
    {
        DsRomReader reader= new DsRomReader();
        reader.readRom(args);
    }

    private String path= System.getProperty("user.dir") + File.separator;
    private String[] romCapacities= new String[12];
    private String rom;
    private String tempPath= "temp" + File.separator;
    private String tempPathUnpack= tempPath;
    private Buffer buffer;
    private RomData romData;
    private ArrayList<FimgEntry> fimgEntries;
    private ArrayList<FimgEntry> sortedEntries;
    private int fileOffset;
    private int length;
    private int fileID;
    private int newFileLength;
    private String type;

    public DsRomReader()
    {
        Arrays.fill(romCapacities,"");
        romCapacities[6]= "8MB";
        romCapacities[7]= "16MB";
        romCapacities[8]= "32MB";
        romCapacities[9]= "64MB";
        romCapacities[10]= "128MB";
        romCapacities[11]= "256MB";
    }

    public void readRom(String[] args) throws Exception
    {
        String rom= args[args.length-1];
        this.rom= path + rom;
        String substring = rom.substring(0, rom.length() - 4);
        buffer= new Buffer(rom);
        readHeader();
        readFatb();
        grabFile(args);
        //System.out.println("Identical directories: " + compareDirs(new File(tempPathUnpack),new File(tempPathUnpack + "Recompile")));
        clearDirectory(new File(path + "temp"));
    }

    public void readHeader() throws IOException
    {
        String title= buffer.readString(12).trim();
        String gameCode= buffer.readString(4);
        String makerCode= buffer.readString(2);
        int deviceCode= buffer.readByte();
        int encryptionSeed= buffer.readByte();
        byte romChipCapacity= buffer.readBytes(1)[0];
        byte[] reserved1= buffer.readBytes(7);
        byte reserved2= (byte)buffer.readByte();
        int systemRegion= buffer.readByte();
        int romVersion= buffer.readByte();
        int autoStartFlag= buffer.readByte();

        int arm9Offset= buffer.readInt();
        if(arm9Offset < 0x4000) {
            throw new RuntimeException("Invalid ROM Header: ARM9 Offset");
        }
        int arm9EntryAddress= buffer.readInt();
        if(!(arm9EntryAddress >= 0x2000000 && arm9EntryAddress <= 0x23BFE00)) {
            throw new RuntimeException("Invalid ROM Header: ARM9 Entry Address");
        }
        int arm9LoadAddress= buffer.readInt();
        if(!(arm9LoadAddress >= 0x2000000 && arm9LoadAddress <= 0x23BFE00)) {
            throw new RuntimeException("Invalid ROM Header: ARM9 RAM Address");
        }
        int arm9Length= buffer.readInt();
        if(arm9Length > 0x3BFE00) {
            throw new RuntimeException("Invalid ROM Header: ARM9 Size");
        }
        int arm7Offset= buffer.readInt();
        if(arm7Offset < 0x8000) {
            throw new RuntimeException("Invalid ROM Header: ARM7 Offset");
        }
        int arm7EntryAddress= buffer.readInt();
        if(!((arm7EntryAddress >= 0x2000000 && arm7EntryAddress <= 0x23BFE00) || (arm7EntryAddress >= 0x37F8000 && arm7EntryAddress <= 0x3807E00))) {
            throw new RuntimeException("Invalid ROM Header: ARM7 Entry Address");
        }
        int arm7LoadAddress= buffer.readInt();
        if(!((arm7LoadAddress >= 0x2000000 && arm7LoadAddress <= 0x23BFE00) || (arm7LoadAddress >= 0x37F8000 && arm7LoadAddress <= 0x3807E00))) {
            throw new RuntimeException("Invalid ROM Header: ARM7 Load Address");
        }
        int arm7Length= buffer.readInt();
        if(arm7Length > 0x3BFE0) {
            throw new RuntimeException("Invalid ROM Header: ARM7 Size");
        }

        int fntbOffset= buffer.readInt();
        int fntbLength= buffer.readInt();

        int fatbOffset= buffer.readInt();
        int fatbLength= buffer.readInt();

        int arm9OverlayOffset= buffer.readInt();
        int amr9OverlayLength= buffer.readInt();

        int arm7OverlayOffset= buffer.readInt();
        int arm7OverlayLength= buffer.readInt();

        int normalCardControlRegisterSettings= buffer.readInt();
        int secureCardControlRegisterSettings= buffer.readInt();

        int iconBannerOffset= buffer.readInt();
        short secureAreaCrc= buffer.readShort();
        short secureTransferTimeout= buffer.readShort();
        int arm9Autoload= buffer.readInt();
        int arm7Autoload= buffer.readInt();
        byte[] secureDisable= buffer.readBytes(8);

        int romLength= buffer.readInt();
        int headerLength= buffer.readInt();

        byte[] reserved3= buffer.readBytes(212);
        byte[] reserved4= buffer.readBytes(16);
        byte[] nintendoLogo= buffer.readBytes(0x156);
        short nintendoLogoCrc= buffer.readShort();
        short headerCrc= buffer.readShort();
        int debugRomOffset= buffer.readInt();
        int debugLength= buffer.readInt();
        int debugRamOffset= buffer.readInt();
        int reserved5= buffer.readInt();
        byte[] reserved6= buffer.readBytes(144);

        romData= new RomData() {
            @Override
            public String getTitle() {
                return title;
            }

            @Override
            public String getGameCode() {
                return gameCode;
            }

            @Override
            public String getMakerCode() {
                return makerCode;
            }

            @Override
            public int getDeviceCode() {
                return deviceCode;
            }

            @Override
            public int getEncryptionSeed() {
                return encryptionSeed;
            }

            @Override
            public byte getDeviceCapacity() {
                return romChipCapacity;
            }

            @Override
            public byte[] getReserved() {
                return reserved1;
            }

            @Override
            public byte getReserved2() {
                return reserved2;
            }

            @Override
            public int getSystemRegion() {
                return systemRegion;
            }

            @Override
            public int getRomVersion() {
                return romVersion;
            }

            @Override
            public int getAutoStartFlag() {
                return autoStartFlag;
            }

            @Override
            public int getArm9Offset() {
                return arm9Offset;
            }

            @Override
            public int getArm9EntryAddress() {
                return arm9EntryAddress;
            }

            @Override
            public int getArm9LoadAddress() {
                return arm9LoadAddress;
            }

            @Override
            public int getArm9Length() {
                return arm9Length;
            }

            @Override
            public int getArm7Offset() {
                return arm7Offset;
            }

            @Override
            public int getArm7EntryAddress() {
                return arm7EntryAddress;
            }

            @Override
            public int getArm7LoadAddress() {
                return arm7LoadAddress;
            }

            @Override
            public int getArm7Length() {
                return arm7Length;
            }

            @Override
            public int getFntbOffset() {
                return fntbOffset;
            }

            @Override
            public int getFntbLength() {
                return fntbLength;
            }

            @Override
            public int getFatbOffset() {
                return fatbOffset;
            }

            @Override
            public int getFatbLength() {
                return fatbLength;
            }

            @Override
            public int getArm9OverlayOffset() {
                return arm9OverlayOffset;
            }

            @Override
            public int getArm9OverlayLength() {
                return amr9OverlayLength;
            }

            @Override
            public int getArm7OverlayOffset() {
                return arm7OverlayOffset;
            }

            @Override
            public int getArm7OverlayLength() {
                return arm7OverlayLength;
            }

            @Override
            public int getNormalCardControlRegisterSettings() {
                return normalCardControlRegisterSettings;
            }

            @Override
            public int getSecureCardControlRegisterSettings() {
                return secureCardControlRegisterSettings;
            }

            @Override
            public int getIconBannerOffset() {
                return iconBannerOffset;
            }

            @Override
            public int getSecureAreaCrc() {
                return secureAreaCrc;
            }

            @Override
            public short getSecureTransferTimeout() {
                return secureTransferTimeout;
            }

            @Override
            public int getArm9Autoload() {
                return arm9Autoload;
            }

            @Override
            public int getArm7Autoload() {
                return arm7Autoload;
            }

            @Override
            public byte[] getSecureDisable() {
                return secureDisable;
            }

            @Override
            public int getRomLength() {
                return romLength;
            }

            @Override
            public int getHeaderLength() {
                return headerLength;
            }

            @Override
            public byte[] getReserved3() {
                return reserved3;
            }

            @Override
            public byte[] getReserved4() {
                return reserved4;
            }

            @Override
            public byte[] getNintendoLogo() {
                return nintendoLogo;
            }

            @Override
            public short getNintendoLogoCrc() {
                return nintendoLogoCrc;
            }

            @Override
            public short getHeaderCrc() {
                return headerCrc;
            }

            @Override
            public int getDebugRomOffset() {
                return debugRomOffset;
            }

            @Override
            public int getDebugLength() {
                return debugLength;
            }

            @Override
            public int getDebugRamOffset() {
                return debugRamOffset;
            }

            @Override
            public int getReserved5() {
                return reserved5;
            }

            @Override
            public byte[] getReserved6() {
                return reserved6;
            }
        };

        System.out.println("Title: " + romData.getTitle());
        System.out.println("Game Code: " + romData.getGameCode());
        System.out.println("Maker Code: " + romData.getMakerCode());
        System.out.println("Device Code: " + romData.getDeviceCode());
        System.out.println("Encryption Seed: " + romData.getEncryptionSeed());
        System.out.println("File Length: " + romCapacities[romData.getDeviceCapacity()]);
        System.out.println("Reserved 1: " + Arrays.toString(romData.getReserved()));
        System.out.println("Reserved 2: " + romData.getReserved2());
        System.out.println("System Region: " + romData.getSystemRegion());
        System.out.println("Rom Version: " + romData.getRomVersion());
        System.out.println("Internal Flags: " + romData.getAutoStartFlag());
        System.out.println("Arm9 Offset: " + romData.getArm9Offset());
        System.out.println("Arm9 Entry Address: " + romData.getArm9EntryAddress());
        System.out.println("Arm9 Load Address: " + romData.getArm9LoadAddress());
        System.out.println("Arm9 Length: " + romData.getArm9Length());
        System.out.println("Arm7 Offset: " + romData.getArm7Offset());
        System.out.println("Arm7 Entry Address: " + romData.getArm7EntryAddress());
        System.out.println("Arm7 Load Address" + romData.getArm7LoadAddress());
        System.out.println("Arm7 Length: " + romData.getArm7Length());
        System.out.println("Fntb Offset: " + romData.getFntbOffset());
        System.out.println("Fntb Length: " + romData.getFntbLength());
        System.out.println("Fatb Offset: " + romData.getFatbOffset());
        System.out.println("Fatb Length: " + romData.getFatbLength());
        System.out.println("Arm9 Overlay Offset: " + romData.getArm9OverlayOffset());
        System.out.println("Arm9 Overlay Length: " + romData.getArm9OverlayLength());
        System.out.println("Arm7 Overlay Offset: " + romData.getArm7OverlayOffset());
        System.out.println("Arm7 Overlay Length: " + romData.getArm7OverlayLength());
        System.out.println("Normal Card Control Register Settings: " + romData.getNormalCardControlRegisterSettings());
        System.out.println("Secure Card Control Register Settings: " + romData.getSecureCardControlRegisterSettings());
        System.out.println("Icon Banner Offset: " + romData.getIconBannerOffset());
        System.out.println("Secure Area Crc: " + romData.getSecureAreaCrc());
        System.out.println("Secure Transfer Timeout: " + romData.getSecureTransferTimeout());
        System.out.println("Arm9 Autoload: " + romData.getArm9Autoload());
        System.out.println("Arm7 Autoload: " + romData.getArm7Autoload());
        System.out.println("Secure Disable: " + Arrays.toString(romData.getSecureDisable()));
        System.out.println("Rom Length: " + romData.getRomLength());
        System.out.println("Header Length: " + romData.getHeaderLength());
        System.out.println("Reserved 3: " + Arrays.toString(romData.getReserved3()));
        System.out.println("Reserved 4: " + Arrays.toString(romData.getReserved4()));
        System.out.println("Nintendo Logo: " + Arrays.toString(romData.getNintendoLogo()));
        System.out.println("Nintendo Logo Crc: " + romData.getNintendoLogoCrc());
        System.out.println("Header Crc: " + romData.getHeaderCrc());
        System.out.println("Debug Rom Offset: " + romData.getDebugRomOffset());
        System.out.println("Debug Length: " + romData.getDebugLength());
        System.out.println("Debug Ram Offset: " + romData.getDebugRamOffset());
        System.out.println("Reserved 5: " + romData.getReserved5());
        System.out.println("Reserved 6: " + Arrays.toString(romData.getReserved6()));

        System.out.println("End of header: " + buffer.getPosition() + "\n");
    }


    private int SS_HG_FIRST_FILE= 0x81;
    private int D_P_PT_FIRST_FILE= 0x7A;

    public void readFatb()
    {
        int firstFileID= 0;
        switch (romData.getTitle())
        {
            case "POKEMON SS" :

            case "POKEMON HG" :
                firstFileID= SS_HG_FIRST_FILE;
                break;

            case "POKEMON D" :

            case "POKEMON P" :

            case "POKEMON PL" :
                firstFileID= D_P_PT_FIRST_FILE;
                break;
        }
        System.out.println("First file ID: " + firstFileID + "\n");

        int fatbPos= 0;
        buffer.skipTo(romData.getFatbOffset());
        System.out.println(buffer.getPosition() + "\n");
        fimgEntries= new ArrayList<>();
        System.out.println("Length: " + romData.getFatbLength()/8 + " files");
        int lastEnd= 0;


        for(int i= 0; i < romData.getFatbLength()/8; i++)
        {
            System.out.println("Fatb Offset: " + buffer.getPosition());
            long startingOffset= buffer.readUIntI();
            long endingOffset= buffer.readUIntI();
            fatbPos+= 4;
            System.out.println("ID: 0x" + Integer.toHexString(i));
            System.out.println("Starting Offset: " +startingOffset);
            System.out.println("Ending Offset: " + endingOffset);
            System.out.println("Length: " + (endingOffset-startingOffset) + "\n");

            long gap= 0;
            if(i > firstFileID)
            {
                gap= startingOffset-lastEnd;
            }
            System.out.println(gap);

            if(gap % 4 != 0)
            {
//                throw new RuntimeException("Gap between previous file and file with ID " + i + " is negative: " + gap);
                //throw new RuntimeException("Gap isn't a multiple of 4");
            }
            int finalI = i;
            long finalDiff = gap;
            fimgEntries.add(new FimgEntry() {
                @Override
                public int getId() {
                    return finalI;
                }

                @Override
                public long getStartingOffset() {
                    return startingOffset;
                }

                @Override
                public long getEndingOffset() {
                    return endingOffset;
                }

                @Override
                public long getGap() {
                    return finalDiff;
                }
            });
            lastEnd= (int) endingOffset;
        }
        System.out.println("Number of recorded entries: " + fimgEntries.size());


    }

    private static final int PERSONALJ= 0x83;
    private static final int LEARNSETJ= 0xA2;
    private static final int EVOLUTIONJ= 0xA3;
    private static final int GROWTHJ= 0x84;
    private static final int ENCOUNTERSS= 0x109;
    private static final int ENCOUNTERHG= 0xA6;

    private static final int PERSONALPT= 0x1A3;
    private static final int LEARNSETPT= 0x1A7;
    private static final int EVOLUTIONPT= 0x1A1;
    private static final int GROWTHPT= 0x1A2;
    private static final int ENCOUNTERPT= 0x14A;

    private static final int PERSONALDP= 0x148;
    private static final int LEARNSETDP= 0x147;
    private static final int EVOLUTIONDP= 0x144;
    private static final int GROWTHDP= 0x145;
    private static final int ENCOUNTERDP= 0x108;

    public void grabFile(String[] args) throws Exception
    {
        tempPath+= args[0] + ".narc";
        tempPathUnpack+= args[0];
        Scanner scanner= new Scanner(System.in);
        fileOffset= 0;
        length= 0;
        String type= args[0].toLowerCase();
        this.type= type;
        String in= "";

        switch (romData.getTitle())
        {
            case "POKEMON HG":
                switch(type) {
                    case "personal":
                        fileOffset= (int) fimgEntries.get(PERSONALJ).getStartingOffset();
                        length= (int) fimgEntries.get(PERSONALJ).getEndingOffset()-fileOffset;
                        fileID= PERSONALJ;
                        break;
                    case "learnsets":
                        fileOffset= (int) fimgEntries.get(LEARNSETJ).getStartingOffset();
                        length= (int) fimgEntries.get(LEARNSETJ).getEndingOffset()-fileOffset;
                        fileID= LEARNSETJ;
                        break;
                    case "evolutions":
                        fileOffset= (int) fimgEntries.get(EVOLUTIONJ).getStartingOffset();
                        length= (int) fimgEntries.get(EVOLUTIONJ).getEndingOffset()-fileOffset;
                        fileID= EVOLUTIONJ;
                        break;
                    case "growth":
                        fileOffset= (int) fimgEntries.get(GROWTHJ).getStartingOffset();
                        length= (int) fimgEntries.get(GROWTHJ).getEndingOffset()-fileOffset;
                        fileID= GROWTHJ;
                        break;
                    case "encounters":
                        fileOffset= (int) fimgEntries.get(ENCOUNTERHG).getStartingOffset();
                        length= (int) fimgEntries.get(ENCOUNTERHG).getEndingOffset()-fileOffset;
                        fileID= ENCOUNTERHG;
                        break;
                    default:
                        throw new RuntimeException("Invalid arguments");
                }
            break;

            case "POKEMON SS":
                switch(type) {
                    case "personal":
                        fileOffset= (int) fimgEntries.get(PERSONALJ).getStartingOffset();
                        length= (int) fimgEntries.get(PERSONALJ).getEndingOffset()-fileOffset;
                        fileID= PERSONALJ;
                        break;
                    case "learnsets":
                        fileOffset= (int) fimgEntries.get(LEARNSETJ).getStartingOffset();
                        length= (int) fimgEntries.get(LEARNSETJ).getEndingOffset()-fileOffset;
                        fileID= LEARNSETJ;
                        break;
                    case "evolutions":
                        fileOffset= (int) fimgEntries.get(EVOLUTIONJ).getStartingOffset();
                        length= (int) fimgEntries.get(EVOLUTIONJ).getEndingOffset()-fileOffset;
                        fileID= EVOLUTIONJ;
                        break;
                    case "growth":
                        fileOffset= (int) fimgEntries.get(GROWTHJ).getStartingOffset();
                        length= (int) fimgEntries.get(GROWTHJ).getEndingOffset()-fileOffset;
                        fileID= GROWTHJ;
                        break;
                    case "encounters":
                        fileOffset= (int) fimgEntries.get(ENCOUNTERSS).getStartingOffset();
                        length= (int) fimgEntries.get(ENCOUNTERSS).getEndingOffset()-fileOffset;
                        fileID= ENCOUNTERSS;
                        break;
                    default:
                        throw new RuntimeException("Invalid arguments");
                }
                break;

            case "POKEMON PL":
                switch(type) {
                    case "personal":
                        fileOffset= (int) fimgEntries.get(PERSONALPT).getStartingOffset();
                        length= (int) fimgEntries.get(PERSONALPT).getEndingOffset()-fileOffset;
                        fileID= PERSONALPT;
                        break;
                    case "learnsets":
                        fileOffset= (int) fimgEntries.get(LEARNSETPT).getStartingOffset();
                        length= (int) fimgEntries.get(LEARNSETPT).getEndingOffset()-fileOffset;
                        fileID= LEARNSETPT;
                        break;
                    case "evolutions":
                        fileOffset= (int) fimgEntries.get(EVOLUTIONPT).getStartingOffset();
                        length= (int) fimgEntries.get(EVOLUTIONPT).getEndingOffset()-fileOffset;
                        fileID= EVOLUTIONPT;
                        break;
                    case "growth":
                        fileOffset= (int) fimgEntries.get(GROWTHPT).getStartingOffset();
                        length= (int) fimgEntries.get(GROWTHPT).getEndingOffset()-fileOffset;
                        fileID= GROWTHPT;
                        break;
                    case "encounters":
                        fileOffset= (int) fimgEntries.get(ENCOUNTERPT).getStartingOffset();
                        length= (int) fimgEntries.get(ENCOUNTERPT).getEndingOffset()-fileOffset;
                        fileID= ENCOUNTERPT;
                        break;
                    default:
                        throw new RuntimeException("Invalid arguments");
                }
                break;

            case "POKEMON P":
                switch(type) {
                    case "personal":
                        fileOffset= (int) fimgEntries.get(PERSONALDP).getStartingOffset();
                        length= (int) fimgEntries.get(PERSONALDP).getEndingOffset()-fileOffset;
                        fileID= PERSONALDP;
                        break;
                    case "learnsets":
                        fileOffset= (int) fimgEntries.get(LEARNSETDP).getStartingOffset();
                        length= (int) fimgEntries.get(LEARNSETDP).getEndingOffset()-fileOffset;
                        fileID= LEARNSETDP;
                        break;
                    case "evolutions":
                        fileOffset= (int) fimgEntries.get(EVOLUTIONDP).getStartingOffset();
                        length= (int) fimgEntries.get(EVOLUTIONDP).getEndingOffset()-fileOffset;
                        fileID= EVOLUTIONDP;
                        break;
                    case "growth":
                        fileOffset= (int) fimgEntries.get(GROWTHDP).getStartingOffset();
                        length= (int) fimgEntries.get(GROWTHDP).getEndingOffset()-fileOffset;
                        fileID= GROWTHDP;
                        break;
                    case "encounters":
                        fileOffset= (int) fimgEntries.get(ENCOUNTERDP).getStartingOffset();
                        length= (int) fimgEntries.get(ENCOUNTERDP).getEndingOffset()-fileOffset;
                        fileID= ENCOUNTERDP;
                        break;
                    default:
                        throw new RuntimeException("Invalid arguments");
                }
                break;

            case "POKEMON D":
                switch(type) {
                    case "personal":
                        fileOffset= (int) fimgEntries.get(PERSONALDP).getStartingOffset();
                        length= (int) fimgEntries.get(PERSONALDP).getEndingOffset()-fileOffset;
                        fileID= PERSONALDP;
                        break;
                    case "learnsets":
                        fileOffset= (int) fimgEntries.get(LEARNSETDP).getStartingOffset();
                        length= (int) fimgEntries.get(LEARNSETDP).getEndingOffset()-fileOffset;
                        fileID= LEARNSETDP;
                        break;
                    case "evolutions":
                        fileOffset= (int) fimgEntries.get(EVOLUTIONDP).getStartingOffset();
                        length= (int) fimgEntries.get(EVOLUTIONDP).getEndingOffset()-fileOffset;
                        fileID= EVOLUTIONDP;
                        break;
                    case "growth":
                        fileOffset= (int) fimgEntries.get(GROWTHDP).getStartingOffset();
                        length= (int) fimgEntries.get(GROWTHDP).getEndingOffset()-fileOffset;
                        fileID= GROWTHDP;
                        break;
                    case "encounters":
                        fileOffset= (int) fimgEntries.get(ENCOUNTERDP-1).getStartingOffset();
                        length= (int) fimgEntries.get(ENCOUNTERDP-1).getEndingOffset()-fileOffset;
                        fileID= ENCOUNTERDP-1;
                        break;
                    default:
                        throw new RuntimeException("Invalid arguments");
                }
                break;

            default:
                System.out.println("Invalid rom header. Please specify what game this is using the following options: Diamond, Pearl, Platinum, HeartGold, SoulSilver");
                in= scanner.nextLine();
                in= in.toLowerCase();
                switch(in) {
                    case "diamond":
                        switch(type) {
                            case "personal":
                                fileOffset= (int) fimgEntries.get(PERSONALDP).getStartingOffset();
                                length= (int) fimgEntries.get(PERSONALDP).getEndingOffset()-fileOffset;
                                fileID= PERSONALDP;
                                break;
                            case "learnsets":
                                fileOffset= (int) fimgEntries.get(LEARNSETDP).getStartingOffset();
                                length= (int) fimgEntries.get(LEARNSETDP).getEndingOffset()-fileOffset;
                                fileID= LEARNSETDP;
                                break;
                            case "evolutions":
                                fileOffset= (int) fimgEntries.get(EVOLUTIONDP).getStartingOffset();
                                length= (int) fimgEntries.get(EVOLUTIONDP).getEndingOffset()-fileOffset;
                                fileID= EVOLUTIONDP;
                                break;
                            case "growth":
                                fileOffset= (int) fimgEntries.get(GROWTHDP).getStartingOffset();
                                length= (int) fimgEntries.get(GROWTHDP).getEndingOffset()-fileOffset;
                                fileID= GROWTHDP;
                                break;
                            case "encounters":
                                fileOffset= (int) fimgEntries.get(ENCOUNTERDP-1).getStartingOffset();
                                length= (int) fimgEntries.get(ENCOUNTERDP-1).getEndingOffset()-fileOffset;
                                fileID= ENCOUNTERDP-1;
                                break;
                            default:
                                throw new RuntimeException("Invalid arguments");
                        }
                        break;

                    case "pearl":
                        switch(type) {
                            case "personal":
                                fileOffset= (int) fimgEntries.get(PERSONALDP).getStartingOffset();
                                length= (int) fimgEntries.get(PERSONALDP).getEndingOffset()-fileOffset;
                                fileID= PERSONALDP;
                                break;
                            case "learnsets":
                                fileOffset= (int) fimgEntries.get(LEARNSETDP).getStartingOffset();
                                length= (int) fimgEntries.get(LEARNSETDP).getEndingOffset()-fileOffset;
                                fileID= LEARNSETDP;
                                break;
                            case "evolutions":
                                fileOffset= (int) fimgEntries.get(EVOLUTIONDP).getStartingOffset();
                                length= (int) fimgEntries.get(EVOLUTIONDP).getEndingOffset()-fileOffset;
                                fileID= EVOLUTIONDP;
                                break;
                            case "growth":
                                fileOffset= (int) fimgEntries.get(GROWTHDP).getStartingOffset();
                                length= (int) fimgEntries.get(GROWTHDP).getEndingOffset()-fileOffset;
                                fileID= GROWTHDP;
                                break;
                            case "encounters":
                                fileOffset= (int) fimgEntries.get(ENCOUNTERDP).getStartingOffset();
                                length= (int) fimgEntries.get(ENCOUNTERDP).getEndingOffset()-fileOffset;
                                fileID= ENCOUNTERDP;
                                break;
                            default:
                                throw new RuntimeException("Invalid arguments");
                        }
                        break;

                    case"platinum":
                        switch(type) {
                            case "personal":
                                fileOffset= (int) fimgEntries.get(PERSONALPT).getStartingOffset();
                                length= (int) fimgEntries.get(PERSONALPT).getEndingOffset()-fileOffset;
                                fileID= PERSONALPT;
                                break;
                            case "learnsets":
                                fileOffset= (int) fimgEntries.get(LEARNSETPT).getStartingOffset();
                                length= (int) fimgEntries.get(LEARNSETPT).getEndingOffset()-fileOffset;
                                fileID= LEARNSETPT;
                                break;
                            case "evolutions":
                                fileOffset= (int) fimgEntries.get(EVOLUTIONPT).getStartingOffset();
                                length= (int) fimgEntries.get(EVOLUTIONPT).getEndingOffset()-fileOffset;
                                fileID= EVOLUTIONPT;
                                break;
                            case "growth":
                                fileOffset= (int) fimgEntries.get(GROWTHPT).getStartingOffset();
                                length= (int) fimgEntries.get(GROWTHPT).getEndingOffset()-fileOffset;
                                fileID= GROWTHPT;
                                break;
                            case "encounters":
                                fileOffset= (int) fimgEntries.get(ENCOUNTERPT).getStartingOffset();
                                length= (int) fimgEntries.get(ENCOUNTERPT).getEndingOffset()-fileOffset;
                                fileID= ENCOUNTERPT;
                                break;
                            default:
                                throw new RuntimeException("Invalid arguments");
                        }
                        break;

                    case "heartgold":
                        switch(type) {
                            case "personal":
                                fileOffset= (int) fimgEntries.get(PERSONALJ).getStartingOffset();
                                length= (int) fimgEntries.get(PERSONALJ).getEndingOffset()-fileOffset;
                                fileID= PERSONALJ;
                                break;
                            case "learnsets":
                                fileOffset= (int) fimgEntries.get(LEARNSETJ).getStartingOffset();
                                length= (int) fimgEntries.get(LEARNSETJ).getEndingOffset()-fileOffset;
                                fileID= LEARNSETJ;
                                break;
                            case "evolutions":
                                fileOffset= (int) fimgEntries.get(EVOLUTIONJ).getStartingOffset();
                                length= (int) fimgEntries.get(EVOLUTIONJ).getEndingOffset()-fileOffset;
                                fileID= EVOLUTIONJ;
                                break;
                            case "growth":
                                fileOffset= (int) fimgEntries.get(GROWTHJ).getStartingOffset();
                                length= (int) fimgEntries.get(GROWTHJ).getEndingOffset()-fileOffset;
                                fileID= GROWTHJ;
                                break;
                            case "encounters":
                                fileOffset= (int) fimgEntries.get(ENCOUNTERHG).getStartingOffset();
                                length= (int) fimgEntries.get(ENCOUNTERHG).getEndingOffset()-fileOffset;
                                fileID= ENCOUNTERHG;
                                break;
                            default:
                                throw new RuntimeException("Invalid arguments");
                        }
                        break;

                    case "soulsilver":
                        switch(type) {
                            case "personal":
                                fileOffset= (int) fimgEntries.get(PERSONALJ).getStartingOffset();
                                length= (int) fimgEntries.get(PERSONALJ).getEndingOffset()-fileOffset;
                                fileID= PERSONALJ;
                                break;
                            case "learnsets":
                                fileOffset= (int) fimgEntries.get(LEARNSETJ).getStartingOffset();
                                length= (int) fimgEntries.get(LEARNSETJ).getEndingOffset()-fileOffset;
                                fileID= LEARNSETJ;
                                break;
                            case "evolutions":
                                fileOffset= (int) fimgEntries.get(EVOLUTIONJ).getStartingOffset();
                                length= (int) fimgEntries.get(EVOLUTIONJ).getEndingOffset()-fileOffset;
                                fileID= EVOLUTIONJ;
                                break;
                            case "growth":
                                fileOffset= (int) fimgEntries.get(GROWTHJ).getStartingOffset();
                                length= (int) fimgEntries.get(GROWTHJ).getEndingOffset()-fileOffset;
                                fileID= GROWTHJ;
                                break;
                            case "encounters":
                                fileOffset= (int) fimgEntries.get(ENCOUNTERSS).getStartingOffset();
                                length= (int) fimgEntries.get(ENCOUNTERSS).getEndingOffset()-fileOffset;
                                fileID= ENCOUNTERSS;
                                break;
                            default:
                                throw new RuntimeException("Invalid arguments");
                        }
                        break;

                    default:
                        throw new RuntimeException("Invalid arguments");
                }
        }

        buffer.skipTo(fileOffset);
        System.out.println("Current Location: " + buffer.getPosition());
        if(!new File(path + "temp").exists() && !new File(path + "temp").mkdir())
        {
            throw new RuntimeException("Failed to create temp directory. Check write perms.");
        }
        BinaryWriter writer= new BinaryWriter(tempPath);
        System.out.println("Read and wrote " + length + " bytes.");
        for(int i= 0; i < length; i++)
        {
            writer.writeByte((byte) buffer.readByte());
        }
        NarcEditor narc= new NarcEditor(); //creates new NarcEditor object
        narc.unpack(tempPath); //run NarcEditor.unpack() with narc extracted from rom as parameter

        switch (args[0].toLowerCase()) {
            case "personal":
                PersonalEditor personalEditor = new PersonalEditor();
                if (args[1].equals("toCsv")) {
                    personalEditor.personalToCSV(tempPathUnpack);
                } else if (args[1].equals("toPersonal")) {
                    personalEditor.csvToPersonal(args[2], args[3], args[4]);
                } else {
                    throw new RuntimeException("Invalid arguments");
                }
                break;
            case "learnsets":
                LearnsetEditor learnsetEditor = new LearnsetEditor();
                if (args[1].equals("toCsv")) {
                    learnsetEditor.learnsetToCsv(tempPathUnpack);
                } else if (args[1].equals("toLearnsets")) {
                    learnsetEditor.csvToLearnsets(args[2], args[3]);
                } else {
                    throw new RuntimeException("Invalid arguments");
                }

                break;
            case "evolutions":
                EvolutionEditor evolutionEditor = new EvolutionEditor();
                if (args[1].equals("toCsv")) {
                    evolutionEditor.evolutionToCsv(tempPathUnpack, false);
                } else if (args[1].equals("toEvolutions")) {
                    evolutionEditor.csvToEvolutions(tempPathUnpack, args[3]);
                } else {
                    throw new RuntimeException("Invalid arguments");
                }

                break;
            case "growth":
                GrowthEditor growthEditor = new GrowthEditor();
                if (args[1].equals("toCsv")) {
                    growthEditor.growthToCsv(tempPathUnpack);
                } else if (args[1].equals("toGrowth")) {
                    growthEditor.csvToGrowth(args[2], args[3]);
                } else {
                    throw new RuntimeException("Invalid arguments");
                }

                break;
            case "encounters":
                if(romData.getTitle().equals("POKEMON HG") || romData.getTitle().equals("POKEMON SS"))
                {
                    EncounterEditor encounterEditor= new EncounterEditor();
                    if (args[1].equals("toCsv")) {
                        encounterEditor.encountersToCsv(tempPathUnpack);
                    } else if (args[1].equals("toEncounters")) {
                        encounterEditor.csvToEncounters(args[2],args[3]);
                    } else {
                        throw new RuntimeException("Invalid arguments");
                    }
                }
                else
                {
                    SinnohEncounterEditor encounterEditor = new SinnohEncounterEditor();
                    if (args[1].equals("toCsv")) {
                        encounterEditor.encountersToCsv(tempPathUnpack);
                    } else if (args[1].equals("toEncounters")) {
                        encounterEditor.csvToEncounters(args[2],args[3]);
                    } else {
                        throw new RuntimeException("Invalid arguments");
                    }
                }

                break;
            default:
                throw new RuntimeException("Invalid arguments");
        }

        System.out.println("\nAfter making all edits to the csv file(s), export them with the same name(s) as they had originally, but with \"Recompile\" appended prior to the file extension. Place them in the same folder they were output in.\nPress Enter to continue.");
        scanner.nextLine();

        switch (args[0].toLowerCase()) {
            case "personal":
                PersonalEditor personalEditor = new PersonalEditor();
                personalEditor.csvToPersonal("personalDataRecompile.csv", "tmLearnsetDataRecompile.csv", type + "Recompile");

                break;
            case "learnsets":
                LearnsetEditor learnsetEditor = new LearnsetEditor();
                learnsetEditor.csvToLearnsets("LearnsetRecompile.csv", type + "Recompile");

                break;
            case "evolutions":
                EvolutionEditor evolutionEditor = new EvolutionEditor();
                evolutionEditor.csvToEvolutions("EvolutionDataRecompile.csv", type + "Recompile");

                break;
            case "growth":
                GrowthEditor growthEditor = new GrowthEditor();
                growthEditor.csvToGrowth("GrowthTableRecompile.csv", type + "Recompile");

                break;
            case "encounters":
                if(romData.getTitle().equals("POKEMON HG") || romData.getTitle().equals("POKEMON SS"))
                {
                    EncounterEditor encounterEditor = new EncounterEditor();
                    encounterEditor.csvToEncounters("Encounters",type + "Recompile");
                }
                else
                {
                    SinnohEncounterEditor encounterEditor = new SinnohEncounterEditor();
                    encounterEditor.csvToEncounters("Encounters",type + "Recompile");
                }

                break;
            default:
                throw new RuntimeException("Invalid arguments");
        }

        narc.pack(tempPathUnpack + "Recompile",type + "Recompile");

        if(length != new File(path + "temp" + File.separator + type + "Recompile.narc").length())
        {
            System.out.println("The file you have recompiled is different in length from the original file. Recompiling roms using a file of different length is unsupported at the moment, so please use Tinke to insert the narc into your rom.\nUse this website to find out where the file is meant to go in Tinke: https://projectpokemon.org/rawdb/");

            Buffer narcBuffer= new Buffer(path + "temp" + File.separator + type + "Recompile.narc");
            BinaryWriter narcWriter= new BinaryWriter(path + type + "Recompile.narc");
            narcWriter.write(narcBuffer.readBytes((int)new File(path + "temp" + File.separator + type + "Recompile.narc").length()));
            narcBuffer.close();
            narcWriter.close();
            clearDirectory(new File(path + "temp"));
            System.exit(0);
        }
        replaceFile(args);
    }

    public void replaceFile(String[] args) throws Exception
    {
        Scanner scanner= new Scanner(System.in);

        System.out.println("Please enter the name to be given to the output rom (include .nds)");
        String name= scanner.nextLine();

        BinaryWriter writer= new BinaryWriter(path + "temp" + File.separator + "rom.nds");
        Buffer romBuffer= new Buffer(rom);

//        writer.write(romBuffer.readBytes(romData.getFatbOffset() + (fileID*8))); //copies all bytes from base rom between 0x00 and FATB entry for file that was extracted and edited

//        ArrayList<FimgEntry> newFimgEntries= new ArrayList<>();
//        for(int i= 0; i < fileID; i++)
//        {
//            newFimgEntries.add(fimgEntries.get(i));
//        }
//
//        newFileLength= (int) new File(path + "temp" + File.separator + type + "Recompile.narc").length(); //gets the length of the repacked narc
//        int diff= newFileLength-length; //the difference between the length of the new file and the length of the original
//        int start= romBuffer.readInt(); //the offset that the file starts at in the FIMG table
//        writer.writeInt(start); //copies start offset to new rom
//        int end= romBuffer.readInt()+diff; //the offset that the new file will end at in the FIMG table
//        writer.writeInt(end); //writes new ending offset to new rom
//        int finalStart = start; //stores a local, final copy of start
//        int finalEnd = end; //stores a local, final copy of end
//        newFimgEntries.add(new FimgEntry() { //changes the contents of the program's internal FATB table for the edited file
//            @Override
//            public int getId() {
//                return fileID;
//            }
//
//            @Override
//            public long getStartingOffset() {
//                return finalStart;
//            }
//
//            @Override
//            public long getEndingOffset() {
//                return finalEnd;
//            }
//
//            @Override
//            public long getGap() {
//                return fimgEntries.get(0).getGap();
//            }
//        });
//
//        start= romBuffer.readInt()+diff;
//        int gap= 0;
//        if(start % 4 != 0)
//        {
//            gap= 4-(start%4);
//        }
//        start+= gap;
//        writer.writeInt(start);
//        diff+= gap;
//        end= romBuffer.readInt()+diff;
//        writer.writeInt(end);
//        int finalStart2 = start;
//        int finalEnd2 = end;
//        int finalGap1 = gap;
//        newFimgEntries.add(new FimgEntry() {
//            @Override
//            public int getId() {
//                return fileID+1;
//            }
//
//            @Override
//            public long getStartingOffset() {
//                return finalStart2;
//            }
//
//            @Override
//            public long getEndingOffset() {
//                return finalEnd2;
//            }
//
//            @Override
//            public long getGap() {
//                return finalGap1;
//            }
//        });
//
//        for(int i= (fileID+2); i < romData.getFatbLength()/8; i++) //goes through the remainder of the FIMG and copies it to the new rom, with the necessary alterations
//        {
//            start= romBuffer.readInt()+diff;
//            writer.writeInt(start);
//            end= romBuffer.readInt()+diff;
//            writer.writeInt(end);
//            int finalIdx = i;
//            int finalStart1 = start;
//            int finalEnd1 = end;
//            int finalGap = gap;
//            newFimgEntries.add(new FimgEntry() {
//                @Override
//                public int getId() {
//                    return finalIdx;
//                }
//
//                @Override
//                public long getStartingOffset() {
//                    return finalStart1;
//                }
//
//                @Override
//                public long getEndingOffset() {
//                    return finalEnd1;
//                }
//
//                @Override
//                public long getGap() {
//                    return finalGap;
//                }
//            });
//        }


//        writer.write(romBuffer.readBytes(fileOffset-romData.getFatbOffset()+romData.getFatbLength())); //copies all bytes between the end of the FATB to the start of the file to be replaced in the FIMG from the base rom to the new rom

        writer.write(romBuffer.readBytes(fileOffset));
        Buffer narcBuffer= new Buffer(path + "temp" + File.separator + type + "Recompile.narc"); //creates a new Buffer object to read through the repacked, modified narc
//        writer.write(narcBuffer.readBytes(newFileLength)); //writes the entire modified narc to the new rom
        writer.write(narcBuffer.readBytes(length));
        romBuffer.skipBytes(length); //skips past the original file in the Buffer reading the original rom
        writer.write(romBuffer.readRemainder());

//        int b;
//        while((b= romBuffer.readByte()) == 0xff)
//        {
//            System.out.println("moo");
//            writer.writeByte((byte)0xff);
//        }
//        writer.writeByteNumTimes((byte) 0xff,gap);
//        writer.writeByte((byte)b);
//        writer.write(romBuffer.readBytes((int) (new File(rom).length()-romBuffer.getPosition())));

        writer= new BinaryWriter(path + name);
        romBuffer= new Buffer(path + "temp" + File.separator + "rom.nds");
        writer.write(romBuffer.readBytes((int) new File(path + "temp" + File.separator + "rom.nds").length()));
        System.out.println("\nProcess completed. Output file can be found at: " + path + name);

        //writer.write(romBuffer.readBytes((int) (new File(rom).length()-fimgEntries.get(fileID+1).getStartingOffset()))); //writes all bytes from the starting offset of the file after the modified file to the end of the rom
////        writer.write(romBuffer.readBytes((int) (new File(rom).length())-romBuffer.getPosition()));
//        for(int i= fileOffset + newFileLength; i < fimgEntries.get(fileID+1).getStartingOffset(); i++)
//        {
//            writer.writeByte((byte) 0xff);
//        }
//        writer.close();
//        romBuffer.close();
        }






    private void clearDirectory(File directory)
    {
//        if(!directory.isDirectory())
//        {
//            throw new RuntimeException(directory.getName() + " is not a directory");
//        }
//
//        List<File> fileList = new ArrayList<>(Arrays.asList(new File(path + "temp").listFiles())); //creates a List of File objects representing every file in specified parameter directory
//        fileList.removeIf(File::isHidden); //removes all File objects from List that are hidden
//
//        File[] files = fileList.toArray(new File[0]); //creates an array of File objects using the contents of the modified List
//        Arrays.sort(files); //sorts files
//        File file;
//
//        for(int i= 0; i < files.length; i++)
//        {
//            file= files[i];
//            if(directory.isDirectory())
//            {
//                clearDirectory(file);
//            }
//            else
//            {
//                if(!file.delete())
//                {
//                    throw new RuntimeException("Unable to delete file " + file.getName() + ". Check write perms");
//                }
//            }
//        }
//        if(!directory.delete())
//        {
//            throw new RuntimeException("Unable to delete directory " + directory.getName() + ". Check write perms.");
//        }
        for(File subfile : directory.listFiles())
        {
            if(subfile.isDirectory())
            {
                clearDirectory(subfile);
            }
            else
            {
                subfile.delete();
            }
        }
        directory.delete();
    }

    private String hexFormat(String hexVal)
    {
        assert hexVal.length() <= 4;
        while(hexVal.length() != 4)
        {
            if(hexVal.length() != 3)
            {
                hexVal= "0" + hexVal;
            }
            else
            {
                hexVal= "f" + hexVal;
            }
        }
        return hexVal;
    }

    private boolean compareDirs(File dir1, File dir2)
    {
        File[] dir1List= dir1.listFiles();
        File[] dir2List= dir2.listFiles();
        if(dir1List.length != dir2List.length)
        {
            return false;
        }

        File dir1File;
        File dir2File;
        for(int i= 0; i < dir1List.length; i++)
        {
            dir1File= dir1List[i];
            dir2File= dir2List[i];
//            if(!dir1File.equals(dir2File))
//            {
//                //throw new RuntimeException("File \"" + dir1File.getName() + "\" in dir1 and file \"" + dir2File.getName() + "\" in dir2 do not match");
//                System.out.println("File \"" + dir1File.getName() + "\" in dir1 and file \"" + dir2File.getName() + "\" in dir2 do not match");
//            }
            if(dir1File.equals(dir2File))
            {
                System.out.println("File \"" + dir1File.getName() + "\" in dir1 and file \"" + dir2File.getName() + "\" in dir2 match");
            }
        }
        return true;
    }

    private void sort (File arr[])
    {
        Arrays.sort(arr, Comparator.comparingInt(DsRomReader::fileToInt));
    }

    private static int fileToInt (File f)
    {
        return Integer.parseInt(f.getName().split("\\.")[0]);
    }
}

