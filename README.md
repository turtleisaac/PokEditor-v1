# PokEditor

Author: Turtleisaac

Multi-purpose in-depth editor for Pokémon Gen 4 and 5 game data

Written entirely in Java and is completely OS-agnostic. Java 8 or greater is required.

**Note:** This is a Command Line Tool. You can't just run it by double clicking the .jar file, as this has no GUI. You need to run it through Terminal (macOS/ Linux) or cmd (Windows)

![PokEditor Personal Editor](https://i.imgur.com/YyBOyCY.png)

# Usage

java -jar PokEditor.jar \<arguments>

Example: java -jar PokEditor.jar personal HeartGold.nds

# List of Spreadsheet-Based Editors/ Tools

* personal

* learnsets

* encounters

* evolutions

* growth

* items

* moves

# List of Command Line-Based Editors/ Tools

* starters

* intro

* opening

* narc

# Arguments

* \<editor name> \<name of rom> (This is used to run the different editors/ tools for each data type)

* help \<editor name> (This is used to view information on each editor/ tool)

# Help Command

 To use the help command, either run PokEditor with no arugments, run PokEditor with the argument "help", or run PokEditor with the argument "help", followed by the editor you wish to view information on (one of the entries on the bulleted lists above). 
 
 ![Help Main Menu](https://i.imgur.com/oK6C9Qx.png)
 
 ![Help Personal Data Editor](https://i.imgur.com/x0gXBBv.png)

# How to edit (Instructions here are for the spreadsheet-based tools only)

 The recommended method of editing is to use the [PokEditor Google Sheets templates](https://drive.google.com/drive/folders/1hlKiP7V31Ddj4WmKnjK7lfhT88yPjB55?usp=sharing), which will require that you have a Google account. Double click on the template for the game you wish to use (note: not all Pokémon DS game templates are currently available due to work still being carried out on them), then wait for the spreadsheet to load. Click "File", "Make a Copy", then choose what to name the copy and where in your Google Drive you want the copy to be placed. 
 
 When you are ready to turn your edits into an edited rom, you can run the PokEditor.jar file through cmd using any of the arguments listed above, and it will output new .csv files that contain the data read from the rom (**NOTE:** you need to have a rom placed in the PokEditor folder, and its name should not have any spaces in it). These files represent which spreadsheet(s) you need to download off of Google Sheets, and are named what you **need** to change your downloaded file(s)'s names to, with the addition of the word "Recompile" with no spaces in between (The program outputs a file called "personalData.csv", so the spreadsheet you download for that becomes "personalDataRecompile.csv"). Go to the spreadsheet(s) you need to download, click "File", "Download", then "Comma-separated values (.csv, current sheet)". This will download the spreadsheet as a .csv file. Repeat until you have every sheet you need for the editor you wish to run downloaded. After this point, simply move these files to the PokEditor folder and rename them to match the scheme explained above. This is the message that the program prints out when it is time to make sure that the edited and properly renamed files are in the PokEditor folder:
 
 ![Edit Data Prompt](https://i.imgur.com/vjPIqPP.png)
 
  (You may need to press enter twice)
 
 If you did not add new entries (this is usually the case), the program will prompt you to type in a name for the output rom. Be sure to include .nds in the name, or else the file will not have a file extension. If you did add new entries, such as additional personal data files for new pokemon/ forms, the program will instead output a narc file and instructions on how to insert it into the game, as PokEditor is currently unable to rebuild roms when a larger/ smaller narc is recompiled. (**NOTE:** If you did not add new entries/ remove old entries and PokEditor exits and tells you that your narc is of a different size than the original, that means that there is a bug in the program. Please report this error to me using the Issues tab here on GitHub or on [this Discord server](https://discord.gg/cTKQq5Y)).

# How to generate spreadsheets containing data you have already edited in another tool

 First off, I recommend using the help commands to see what running each editor will allow you to edit. Once you know what it is that you want to edit, run PokEditor utilizing the proper arguments. The program may ask you to type some information in to help it carry out the operations. Once you go through all of the prompts, you should eventually reach the point where the program prints out the following message:
 
 ![Edit Data Prompt](https://i.imgur.com/vjPIqPP.png)


 After using the program to generate the spreadsheet(s), import the .csv files into any spreadsheet editor like Google Sheets or Microsoft Excel. The recommended method is to use the [PokEditor Google Sheets templates](https://drive.google.com/drive/folders/1hlKiP7V31Ddj4WmKnjK7lfhT88yPjB55?usp=sharing) (see information on this further up in the readme) and use "File" + "Import" on whatever sheet you want to overwrite with new data, choose the sheet output by the program, then select "Replace current sheet" (repeat for each sheet the program produced). Edit to your heart's content, then export/ download the spreadsheets as a .csv, place them in the same folder as PokEditor.jar, and make the name of the file match the name it originally had, but with "Recompile" appended to the end of the name (ex: personalDataRecompile.csv). You can then either run the program a second time using the same arguments as before, or if you never exited the comand line, simply press enter for the program to continue doing its work.

 If you did not add new entries (this is usually the case), the program will prompt you to type in a name for the output rom. Be sure to include .nds in the name, or else the file will not have a file extension. If you did add new entries, such as additional personal data files for new pokemon/ forms, the program will instead output a narc file and instructions on how to insert it into the game, as PokEditor is currently unable to rebuild roms when a larger/ smaller narc is recompiled. (**NOTE:** If you did not add new entries/ remove old entries and PokEditor exits and tells you that your narc is of a different size than the original, that means that there is a bug in the program. Please report this error to me using the Issues tab here on GitHub or on [this Discord server](https://discord.gg/cTKQq5Y)).
