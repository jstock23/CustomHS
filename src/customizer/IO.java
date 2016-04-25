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

/**
 * <h3>IO</h3>
 * <p>Handles IO tasks like reading and writing files.</p>
 */
public class IO {

    /** Location of Hearthstone installation */
    protected static String installLocation = "/files";
    /** Relative location of folder containing .txt files */
    final protected static String STRINGS_FOLDER = "Strings";
    /** Integers representing the various locales. Could maybe search for each and verify that it exists,
     * and save the user the trouble? */
    final protected static int EN_US = 100;
    /** Array of Strings to hold folder names of locale */
    final protected static String[] LOCALES = {
            "enUS"
    };
    /** Integers to represent the different .txt files uniquely */
    final protected static int GLOBALS = 0, GLUE = 1, GAMEPLAY = 2, MISSION = 3;
    /** Array of Strings of file names corresponding to the .txt files */
    final protected static String[] FILE_NAMES = {
            "GLOBAL.txt",
            "GLUE.txt",
            "GAMEPLAY.txt",
            "MISSION.txt"
    };


    static void save(String text, int locale, int stringsType) {

        String fileLocation = installLocation + STRINGS_FOLDER + '/' + LOCALES[locale] + '/' + FILE_NAMES[stringsType];

        try {
            PrintWriter writer = new PrintWriter(fileLocation, "UTF-8");
            writer.print(text);
            writer.close();
        } catch (IOException e) {
            System.out.println(e.toString());
        }

    }


}