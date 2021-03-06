/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2012 IRSTV (FR CNRS 2488)
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * OrbisGIS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.orbisgis.view.toc.actions.cui.legends.model;

import org.orbisgis.legend.thematic.LineParameters;
import org.orbisgis.legend.thematic.map.MappedLegend;
import org.orbisgis.legend.thematic.recode.AbstractRecodedLegend;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.EventObject;

/**
 * A cell editor dedicated to the management of keys in a recoded legend. It embeds a simple JTextField that is used
 * to edit the value in the table.
 * @author alexis
 */
public abstract class KeyEditorUniqueValue<U extends LineParameters> extends AbstractCellEditor implements TableCellEditor, ActionListener {
    protected static final String EDIT = "edit";
    private JTextField field;
    private String val;
    private MappedLegend<String, U> rl;

    /**
     * Build a cell editor dedicated to the management of keys in a recoded legend.
     */
    public KeyEditorUniqueValue(){
        field = new JTextField(25);
        field.setActionCommand(EDIT);
        field.addActionListener(this);
    }

    @Override
    public boolean isCellEditable(EventObject event){
        if(event instanceof MouseEvent){
            MouseEvent me = (MouseEvent) event;
            return me.getClickCount()>=2;
        }
        return false;
    }

    @Override
    public Object getCellEditorValue() {
        return val;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getActionCommand().equals(EDIT)){
            U lp = rl.get(val);
            rl.remove(val);
            String k = ((AbstractRecodedLegend)rl).getNotUsedKey(field.getText());
            rl.put(k, lp);
            fireEditingStopped();
        }
    }

    /**
     * Sets the associated Legend.
     * @param rl The legend we want to associate.
     */
    protected void setLegend(MappedLegend<String, U> rl) {
        this.rl = rl;
    }

    /**
     * Gets the text field used for edition.
     * @return
     */
    public JTextField getField() {
        return field;
    }

    /**
     * Gets the stored string value
     * @return the stored string value
     */
    public String getVal() {
        return val;
    }

    /**
     * Sets the stored string value
     * @param val the new stored string value.
     */
    public void setVal(String val) {
        this.val = val;
    }
}
