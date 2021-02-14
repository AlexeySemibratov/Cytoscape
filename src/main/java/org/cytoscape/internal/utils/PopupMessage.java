package org.cytoscape.internal.utils;

import javax.swing.*;
import java.awt.*;

public class PopupMessage {

    public static void ErrorMessage(Exception ex){
        JOptionPane.showMessageDialog(null,ex.getMessage(), "Error",JOptionPane.ERROR_MESSAGE);
    }

    public static void ErrorMessage(String message){
        JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void WarningMessage(String message){
        JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.WARNING_MESSAGE);
    }

    public static int ConfirmMessage(String message){
        return JOptionPane.showConfirmDialog(null, message);
    }

    public static void Message(String message) {
        JOptionPane.showMessageDialog(null,message);
    }
}
