/**
 CustomHS helps you customize in-game text for Hearthstone.
 Copyright (C) 2016  Jason Stockwell

 This program is free software; you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation; either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program; if not, write to the Free Software Foundation,
 Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301  USA
 */

package customizer;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by Jason Stockwell on 4/24/16.
 *
 * <h3>IO</h3>
 * <p>Handles IO tasks like reading and writing files.</p>
 */
public class IO {

    /** Location of Hearthstone installation */
    protected static String textFilesDirectoryString;
    /** Relative location of folder containing .txt files */
    final private static String STRINGS_FOLDER = "Strings";
    /** Integers representing the various locales. Could maybe search for each and verify that it exists,
     * and save the user the trouble? */
    final protected static int EN_US = 0;
    /** Array of Strings to hold folder names of locale */
    final private static String[] LOCALES = {
            "enUS"
    };
    /** Stores the locale of the current user, for use later. */
    private static int locale;
    /** Integers to represent the different .txt files uniquely */
    final protected static int GLOBALS = 0, GLUE = 1, GAMEPLAY = 2, MISSION = 3;
    /** Array of Strings of file names corresponding to the .txt files */
    final private static String[] FILE_NAMES = {
            "GLOBAL.txt",
            "GLUE.txt",
            "GAMEPLAY.txt",
            "MISSION.txt"
    };
    /** Error messages to be given when the directory can't be set */
    final protected static String[] SET_DIRECTORY_ERROR_MESSAGES = {
            "Error: Unknown Error",
            "No error!",
            "Error: There should be a folder \"Strings\" under \"Hearthstone\".",
            "Error: Could not find a valid locale, please tell us and we will add it! Sorry!",
            "Error: You have multiple locales? Please tell us to add support to that! Sorry!"
    };


    static void save(String text, int locale, int stringsType) {

        if (textFilesDirectoryString == null) {
            System.out.println("Error: Can't save because we don't know where to save to!");
            return;
        }

        String fileLocation = textFilesDirectoryString;

        try {
            PrintWriter writer = new PrintWriter(fileLocation, "UTF-8");
            writer.print(text);
            writer.close();
        } catch (IOException e) {
            System.out.println(e.toString());
        }

    }

    /** Sets the directory of the "Hearthstone" folder. */
    public static int setDirectory(File directory) {
        //check for the Strings folder
        Path path = Paths.get(directory.toString(), STRINGS_FOLDER);
        if (!Files.exists(path))
            return 2;
        //check and see which locale is under the strings folder
        //if there are multiple locales for some reason, well that's useful info... maybe unnecessary
        boolean[] isValidLocale = new boolean[LOCALES.length];
        int numLocales = 0;
        //check for each locale
        for (int j = 0; j < LOCALES.length; j++) {
            if (Files.exists(Paths.get(directory.toString(), STRINGS_FOLDER, LOCALES[j]))) {
                isValidLocale[j] = true;
                numLocales++;
            }
        }
        //is there a valid locale folder?
        if (numLocales == 0)
            return 3;
        //are there multiple locales?
        if (numLocales > 1)
            return 4;
        //OK, one locale, so lets set the path and be done with it
        for (int x = 0; x < isValidLocale.length; x++) {
            if (isValidLocale[x]) {
                locale = x;
                break;
            }
        }
        textFilesDirectoryString = Paths.get(directory.toString(), STRINGS_FOLDER, LOCALES[locale]).toString();
        System.out.println("Text files location: \"" + textFilesDirectoryString + "\"");
        return 1;
    }
}























