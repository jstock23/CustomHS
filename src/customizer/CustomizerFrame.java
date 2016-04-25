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
import java.awt.*;
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

        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2);

        setVisible(true);

        IO.loadOrCreatePropertiesFile();
        if (IO.getTimesWarned() < 3)
            if (warnUser() == 0)
                return;
        if (!IO.hasDirectoryAndLocale())
            queryForDirectory(true);
    }

    protected void queryForDirectory(boolean showInstructions) {

        //instruct the user that they need to find the hearthstone install folder
        Object[] options = { "OK", "Exit" };
        int intention = JOptionPane.showOptionDialog(this, "Please select the Hearthstone folder.", "",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
        if (intention != 0) {
            //user is easily intimidated by demands.
            setVisible(false);
            dispose();
            return;
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


    /** Warns the user not to use this software, as it could ruin their computer. */
    private int warnUser() {
        Object[] options = {"I understand", "Close"};
        int result = JOptionPane.showOptionDialog(this, "This is alpha software that could ruin your Hearthstone install,\nor your entire computer. " +
                "Please take the necessary precautions.", "WARNING", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[1]);
        //make sure they pressed "I understand", not just x
        if (result == 0) {
            //they clicked I understand... increment timesWarned
            IO.incTimesWarned();
            return 1;
        } else {
            //user is a chicken, abort
            setVisible(false);
            dispose();
        }
        return 0;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }
}
