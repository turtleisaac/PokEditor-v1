# PokEditor

Author: Turtleisaac

Multi-purpose in-depth editor for Pokémon Gen 4 and 5 game data

Written entirely in Java and is completely OS-agnostic. Java 8 or greater is required.

# Usage

java -jar PokEditor.jar \<arguments>

# List of Editors

personal

learnsets

encounters

evolutions

growth

items

# Arguments

\<editor name> \<name of rom>

# How to edit

 The recommended method of editing is to use the [PokEditor Google Sheets templates](https://drive.google.com/drive/folders/1hlKiP7V31Ddj4WmKnjK7lfhT88yPjB55?usp=sharing), which will require that you have a Google account. Double click on the template for the game you wish to use (note: not all Pokémon DS game templates are currently available due to work still being carried out on them), then wait for the spreadsheet to load. Click "File", "Make a Copy", then choose what to name the copy and where in your Google Drive you want the copy to be placed. 
 
 When you are ready to turn your edits into an edited rom, you can run the PokEditor.jar file through cmd using any of the arguments listed above, and it will output new .csv files that contain the data read from the rom (**NOTE:** you need to have a rom placed in the PokEditor folder, and its name should not have any spaces in it). These files represent which spreadsheet(s) you need to download off of Google Sheets, and are named what you **need** to change your downloaded file(s)'s names to, with the addition of the word "Recompile" with no spaces in between (The program outputs a file called "personalData.csv", so the spreadsheet you download for that becomes "personalDataRecompile.csv"). Go to the spreadsheet(s) you need to download, click "File", "Download", then "Comma-separated values (.csv, current sheet)". This will download the spreadsheet as a .csv file. Repeat until you have every sheet you need for the editor you wish to run downloaded. After this point, simply move these files to the PokEditor folder and rename them to match the scheme explained above.

 If you did not add new entries (this is usually the case), the program will prompt you to type in a name for the output rom. Be sure to include .nds in the name, or else the file will not have a file extension. If you did add new entries, such as additional personal data files for new pokemon/ forms, the program will instead output a narc file and instructions on how to insert it into the game, as PokEditor is currently unable to rebuild roms when a larger/ smaller narc is recompiled.

# How to generate spreadsheets containing data you have already edited in another tool

 After using the program to generate the spreadsheet(s), import the .csv files into any spreadsheet editor like Google Sheets or Microsoft Excel. Edit to your heart's content, then export/ download the spreadsheets as a .csv, place them in the same folder as PokEditor.jar, and make the name of the file match the name it originally had, but with "Recompile" appended to the end of the name (ex: personalDataRecompile.csv). You can then either run the program a second time using the same arguments as before, or if you never exited the comand line, simply press enter for the program to continue doing its work.

 If you did not add new entries (this is usually the case), the program will prompt you to type in a name for the output rom. Be sure to include .nds in the name, or else the file will not have a file extension. If you did add new entries, such as additional personal data files for new pokemon/ forms, the program will instead output a narc file and instructions on how to insert it into the game, as PokEditor is currently unable to rebuild roms when a larger/ smaller narc is recompiled.
