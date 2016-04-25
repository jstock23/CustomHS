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
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.StringTokenizer;

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
    private static int locale = -1;
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
            "Error: You have multiple locales? Please tell us to add support to that! Sorry!",
            "Error: Could not save directory for later... Sorry!"
    };
    /** The location of the properties file which will store info like Hearthstone directory */
    private static String propertiesFileLocation;
    /** The number of times the user has been warned that this could screw up their whole computer... */
    private static int timesWarned = 0;
    /** Text used in properties.txt file to indicate number of times the user was warned not to use this software. */
    private final static String WARNING_TEXT = "Number of times the user has been warned: ";



    /**Saves the text to the file given */
    static protected int save(String text, String fileName) {

        try {
            PrintWriter writer = new PrintWriter(fileName, "UTF-8");
            writer.print(text);
            writer.close();
        } catch (IOException e) {
            System.out.println(e.toString());
            return 0;
        } catch (Exception e) {
            return 0;
        }
        //seems to have worked?
        return 1;

    }

    static String loadTextFile(String fileLocation) {


        try {
            FileReader fileReader = new FileReader(fileLocation);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            //Make a string builder to add the lines to... should make it marginally faster than regular concatenation
            StringBuilder builder = new StringBuilder();
            String line = null;

            while ((line = bufferedReader.readLine()) != null) {
                builder.append(line).append('\n');
            }
            return builder.toString();

        } catch (FileNotFoundException e) {
            System.out.println("Error: Could not find the properties file to load...");
        } catch (IOException e) {
            System.out.println("Error: Problem occurred while trying to read properties file...");
        }

        return null;

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
        int didSaveProperties = savePropertiesToFile();
        if (didSaveProperties == 1)
            return 1;
        else
            return 5;
    }

    /** Prints out the directory where the class file is located... */
    protected static void printClassDirectory() {
        ClassLoader loader = IO.class.getClassLoader();
        System.out.println(loader.getResource("customizer/IO.class"));
    }

    /** Creates a file within the JAR file to save things to, so it can be movable and still retain it relatively */
    protected static void loadOrCreatePropertiesFile() {
        ClassLoader loader = IO.class.getClassLoader();
        URL classLocationURL = loader.getResource("customizer/IO.class");
        if (classLocationURL == null) {
            System.out.println("classLocationURL is null....");
            return;
        }
        String classLocation = classLocationURL.toString();
        String propertiesLocation;
        //remove the leading "file:" part
        classLocation = classLocation.replace("file:", "");
        //if it's been compiled into a .jar file, save the properties on the same level as it...
        int jarIndex = classLocation.indexOf("CustomHS.jar");
        if (jarIndex != -1) {
            //truncate just before the .jar file
            propertiesLocation = classLocation.substring(0, jarIndex);
            //remove the leading "jar:" part if it's there
            propertiesLocation = propertiesLocation.replace("jar:", "");
        } else {
            //either it's not compiled in a .jar, or it's being debugged, so set the properties file in the class location
            String IOClassName = "IO.class";
            propertiesLocation = classLocation.substring(0, classLocation.length() - IOClassName.length());
        }

        File propertiesFile = new File(propertiesLocation + "properties.txt");
        System.out.println("Potential properties file location: \"" + propertiesFile.getPath().toString() + "\"");
        boolean isFileAccessible = false;
        if (Files.exists(propertiesFile.toPath())) {
            System.out.println("Properties file already exists.");
            isFileAccessible = true;
        } else {
            System.out.println("Properties file does not exist, creating it...");
            boolean success = false;
            try {
                success = propertiesFile.createNewFile();
            } catch (IOException e) {
                System.out.println(e.toString());
            }
            if (success) {
                System.out.println("Seems to have worked...");
                isFileAccessible = true;
            } else
                System.out.println("Didn't work...");
        }
        //if the file is accessible, then store its location for use later
        if (isFileAccessible) {
            propertiesFileLocation = propertiesFile.getPath().toString();
            loadPropertiesFromFile();
        }

    }


    /** Loads the properties from the properties file */
    private static int loadPropertiesFromFile() {
        String contents = loadTextFile(propertiesFileLocation);
        if (contents.equals("") || contents == null)
            return 0;
        StringTokenizer tokenizer = new StringTokenizer(contents, "\n");
        System.out.println("Contents of properties file:\n\n\"" + contents + "\"");
        //ok, lets extract the info
        textFilesDirectoryString = tokenizer.nextToken();
        System.out.println("Loaded directory of text files: \"" + textFilesDirectoryString + "\"");
        //ok so now load the locale
        String localeSection = tokenizer.nextToken();
        for (int j = 0; j < LOCALES.length; j++) {
            if (localeSection.contains(LOCALES[j])) {
                locale = j;
                System.out.println("Loaded locale: " + LOCALES[j]);
                break;
            }
        }
        if (locale == -1)
            System.out.println("Error: Could not load valid locale...");
        //now check the number of times the user has been warned not to use this software
        timesWarned = Integer.parseInt(tokenizer.nextToken().substring(WARNING_TEXT.length()));
        System.out.println("User has been warned " + timesWarned + " times.");
        return 0;
    }


    /** Saves the properties to file */
    protected static int savePropertiesToFile() {
        if (propertiesFileLocation == null) {
            System.out.println("Can't save properties because there is no known place to save them to... Please inform us of this error.");
            return 0;
        }
        StringBuilder builder = new StringBuilder();
        if (textFilesDirectoryString == null || textFilesDirectoryString.equals(""))
            builder.append(" \n");
        else
            builder.append(textFilesDirectoryString).append('\n');
        if (locale == -1)
            builder.append("No locale set\n");
        else
            builder.append(LOCALES[locale]).append('\n');
        builder.append(WARNING_TEXT).append(timesWarned);
        return save(builder.toString(), propertiesFileLocation);
    }

    /** Returns true if there is a locale and text file directory */
    protected static boolean hasDirectoryAndLocale() {
        return textFilesDirectoryString != null && locale != -1;
    }

    /** Returns the number of times the user has been warned not to use this software */
    protected static int getTimesWarned() {
        return timesWarned;
    }

    /** Increments the number of times warned by 1 */
    protected static void incTimesWarned() {
        timesWarned++;
        savePropertiesToFile();
    }

}























