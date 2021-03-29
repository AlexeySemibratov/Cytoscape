package org.cytoscape.internal.utils;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class TableUtils {

    public static DefaultTableModel createTableWithNoEditableCells() {
        DefaultTableModel tableModel = new DefaultTableModel() {
            private static final long serialVersionUID = 199479757891004L;

            @Override
            public boolean isCellEditable(int i, int j) {
                return false;
            }
        };
        return tableModel;
    }

    public static class NTTableModel extends DefaultTableModel {

        private static final long serialVersionUID = 199479757891004L;

        private Color lastColumnColor = Color.CYAN;

        @Override
        public boolean isCellEditable(int i, int j) {
            return (j == this.getColumnCount() - 1) ? true : false;
        }

        public void setLastColumnColor(Color color) {
            this.lastColumnColor = color;
        }

        public Color getLastColumnColor() {
            return lastColumnColor;
        }

    }

    ;


    public static DefaultTableCellRenderer tableRenderer() {
        return new DefaultTableCellRenderer() {

            @Override
            public Component getTableCellRendererComponent
                    (JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (column == table.getColumnCount() - 1) {
                    c.setFont(new Font("TimesRoman", Font.BOLD, 12));
                } else {
                    c.setBackground(Color.WHITE);
                }
                return c;
            }

        };
    }
}
