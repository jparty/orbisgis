/*
 * The GDMS library (Generic Datasources Management System)
 * is a middleware dedicated to the management of various kinds of
 * data-sources such as spatial vectorial data or alphanumeric. Based
 * on the JTS library and conform to the OGC simple feature access
 * specifications, it provides a complete and robust API to manipulate
 * in a SQL way remote DBMS (PostgreSQL, H2...) or flat files (.shp,
 * .csv...). GDMS is produced  by the geomatic team of the IRSTV
 * Institute <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * This file is part of GDMS.
 *
 * GDMS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GDMS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with GDMS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://orbisgis.cerma.archi.fr/>
 *    <http://sourcesup.cru.fr/projects/orbisgis/>
 *    <http://listes.cru.fr/sympa/info/orbisgis-developers/>
 *    <http://listes.cru.fr/sympa/info/orbisgis-users/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
package org.gdms.sql.indexes.hashMap;

import java.io.File;

/**
 * DOCUMENT ME!
 * 
 * @author Fernando Gonz�lez Cort�s
 */
public class TestHashTable {
	/**
	 * DOCUMENT ME!
	 * 
	 * @param args
	 *            DOCUMENT ME!
	 * 
	 * @throws Exception
	 *             DOCUMENT ME!
	 */
	public static void main(String[] args) throws Exception {
		File temp = File.createTempFile("gdms", "tmp");
		temp.deleteOnExit();

		Index idx = new DiskIndex(temp);
		idx.start();

		String[] cadenas = new String[] { "hola", "adios", "ata logo",
				"deica logo", "hasta luego", "au", "bye", "ciao", "que te den",
				"venga" };

		for (int i = 0; i < cadenas.length; i++) {
			idx.add(cadenas[i], i);
		}

		for (int i = 0; i < cadenas.length; i++) {
			PositionIterator it = idx.getPositions(cadenas[i]);
			System.out.println("cadenas[" + i + "] = " + cadenas[i]);

			while (it.hasNext()) {
				int element = (int) it.next();
				System.out.println(element);
			}
		}

		idx.stop();
	}
}
