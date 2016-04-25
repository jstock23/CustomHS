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

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * Created by jstockwell on 4/24/16.
 *
 * The main frame seen by the user. Holds the various UI elements.
 */
public class CustomizerFrame extends JFrame implements ActionListener {


    public CustomizerFrame() {

        setTitle("CustomHS");
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setVisible(true);

        IO.loadOrCreatePropertiesFile();
        if (!IO.hasDirectoryAndLocale())
            queryForDirectory(true);
    }

    protected void queryForDirectory(boolean showInstructions) {

        if (showInstructions) {
            JOptionPane.showMessageDialog(this, "Please select the Hearthstone folder.");
        }

        //let the user tell us where Hearthstone is installed.
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        int returnVal = chooser.showOpenDialog(CustomizerFrame.this);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            System.out.println("Chosen file: \"" + file + "\"");
            String fileString = file.toString();
            if (fileString.endsWith("Hearthstone")) {
                //ok, seems good so far
                int result = IO.setDirectory(file);
                if (result != 1)
                    invalidDirectory(IO.SET_DIRECTORY_ERROR_MESSAGES[result]);
            } else {
                int indexOfSlash = fileString.lastIndexOf('/');
                String folderName;
                if (indexOfSlash != -1)
                    folderName = fileString.substring(indexOfSlash + 1);
                else {
                    //there don't seem to be any slashes... so... then just use that... probably unnecessary
                    folderName = fileString;
                }
                invalidDirectory("It looks like you chose a folder called \"" + folderName + "\". Please select the \"Hearthstone\" folder.");
            }
        } else {
            //user didn't select anything, so close
            setVisible(false);
            dispose();
        }
    }

    /** Called when something went wrong selecting the install location. Brings up JOptionPane with custom message to retry */
    private void invalidDirectory(String message) {
        Object[] options = { "Try Again", "Cancel" };
        int result = JOptionPane.showOptionDialog(this, message, "Error",
                JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[0]);
        if (result == 0)
            queryForDirectory(false);
        else {
            //user does not want to try again... so just close
            setVisible(false);
            dispose();
        }



    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }
}
