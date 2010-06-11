/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the geo-informatic team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 * 
 *  
 *  Lead Erwan BOCHER, scientific researcher, 
 *
 *  Developer lead : Pierre-Yves FADET, computer engineer. 
 *  
 *  User support lead : Gwendall Petit, geomatic engineer. 
 * 
 * Previous computer developer : Thomas LEDUC, scientific researcher, Fernando GONZALEZ
 * CORTES, computer engineer.
 * 
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 * 
 * Copyright (C) 2010 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
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
 * For more information, please consult: <http://orbisgis.cerma.archi.fr/>
 * <http://sourcesup.cru.fr/projects/orbisgis/>
 * 
 * or contact directly: 
 * erwan.bocher _at_ ec-nantes.fr 
 * Pierre-Yves.Fadet _at_ ec-nantes.fr
 * gwendall.petit _at_ ec-nantes.fr
 **/

package org.orbisgis.core.ui.plugins.views.editor;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import org.orbisgis.core.edition.EditableElement;

public class TransferableEditableElement implements Transferable {

	public static final DataFlavor editableElementFlavor = new DataFlavor(
			EditableElement.class, "Editable element");

	private EditableElement[] elements;

	public TransferableEditableElement(EditableElement[] elements) {
		this.elements = elements;
	}

	public Object getTransferData(DataFlavor flavor)
			throws UnsupportedFlavorException, IOException {
		Object ret = null;
		if (flavor.equals(editableElementFlavor)) {
			ret = elements;
		} else if (flavor.equals(DataFlavor.stringFlavor)) {
			String retString = "";
			String separator = "";
			for (EditableElement element : elements) {
				retString = retString + separator + element.getId();
				separator = ", ";
			}
			ret = retString;
		}

		return ret;
	}

	public DataFlavor[] getTransferDataFlavors() {
		return (new DataFlavor[] { editableElementFlavor,
				DataFlavor.stringFlavor });
	}

	public boolean isDataFlavorSupported(DataFlavor flavor) {
		return (flavor.equals(editableElementFlavor) || flavor
				.equals(DataFlavor.stringFlavor));
	}
}
