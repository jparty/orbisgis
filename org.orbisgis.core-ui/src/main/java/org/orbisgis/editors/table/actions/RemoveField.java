package org.orbisgis.editors.table.actions;

import javax.swing.JOptionPane;

import org.gdms.data.DataSource;
import org.gdms.driver.DriverException;
import org.orbisgis.Services;
import org.orbisgis.editors.table.Selection;
import org.orbisgis.editors.table.action.ITableColumnAction;
import org.orbisgis.errorManager.ErrorManager;

public class RemoveField implements ITableColumnAction {

	@Override
	public boolean accepts(DataSource dataSource, Selection selection,
			int selectedColumn) {
		return (selectedColumn != -1) && dataSource.isEditable();
	}

	@Override
	public void execute(DataSource dataSource, Selection selection,
			int selectedColumnIndex) {
		try {
			int option = JOptionPane.showConfirmDialog(null, "Delete field "
					+ dataSource.getFieldName(selectedColumnIndex) + "?",
					"Remove field", JOptionPane.YES_NO_OPTION);
			if (option == JOptionPane.YES_OPTION) {
				dataSource.removeField(selectedColumnIndex);
			}
		} catch (DriverException e) {
			Services.getService(ErrorManager.class).error(
					"Cannot remove field", e);
		}
	}

}