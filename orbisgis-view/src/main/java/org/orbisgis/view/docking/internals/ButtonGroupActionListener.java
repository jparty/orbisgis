
package org.orbisgis.view.docking.internals;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.action.DockAction;
import bibliothek.gui.dock.action.DropDownAction;
import bibliothek.gui.dock.common.action.CRadioGroup;
import bibliothek.gui.dock.event.DropDownActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Set;
import org.orbisgis.view.docking.actions.CToggleButton;

/**
 * CDropDownAction is not suitable for CRadioGroup, this listener 
 * will desactivate all CRadioAction when an action occur on a CAction
 */
public class ButtonGroupActionListener implements DropDownActionListener,ActionListener {
    private CRadioGroup radioGroup;

    public ButtonGroupActionListener(CRadioGroup radioGroup) {
        this.radioGroup = radioGroup;
    }
    private void deselectAllButtons() {
        CToggleButton tb = new CToggleButton();
        radioGroup.add(tb);
        tb.setSelected(true);
        radioGroup.remove(tb);      
    }
    public void selectionChanged(DropDownAction action, Set<Dockable> dockables, DockAction selection) {
        deselectAllButtons();
    }

    public void actionPerformed(ActionEvent ae) {
        deselectAllButtons();
    }
    
}