# PokEditor
Author: Turtleisaac


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

\<editor name> toCsv \<name of rom>

# How to edit

 After using the program to generate the spreadsheet(s), import the .csv files into any spreadsheet editor like Google Sheets or Microsoft Excel. Edit to your heart's content, then export/ download the spreadsheets as a .csv, place them in the same folder as PokEditor.jar, and make the name of the file match the name it originally had, but with "Recompile" appended to the end of the name (ex: personalDataRecompile.csv). You can then either run the program a second time using the same arguments as before, or if you never exited the comand line, simply press enter for the program to continue doing its work.

 If you did not add new entries, the program will prompt you to type in a name for the output rom. Be sure to include .nds in the name, or else the file will not have a file extension. If you did add new entries, such as additional personal data files for new pokemon/ forms, the program will instead output a narc file and instructions on how to insert it into the game, as PokEditor is currently unable to rebuild roms when a larger/ smaller narc is recompiled.

