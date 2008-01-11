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
package org.gdms.driver;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.gdms.driver.driverManager.Driver;
import org.gdms.driver.driverManager.DriverLoadException;
import org.gdms.driver.driverManager.DriverManager;

/**
 * Utility method for the drivers
 */
public class DriverUtilities {

	private static int BUF_SIZE = 50000;

	/**
	 * Translates the specified code by using the translation table specified by
	 * the two last arguments. If there is no translation a RuntimeException is
	 * thrown.
	 *
	 * @param code
	 *            code to translate
	 * @param source
	 *            keys on the translation table
	 * @param target
	 *            translation to the keys
	 *
	 * @return translated code
	 */
	public static int translate(int code, int[] source, int[] target) {
		for (int i = 0; i < source.length; i++) {
			if (code == source[i]) {
				return target[i];
			}
		}

		throw new RuntimeException("code mismatch");
	}

	public static long copy(File input, File output) throws IOException {
		FileInputStream in = null;
		try {
			in = new FileInputStream(input);
			return copy(in, output);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
				}
			}
		}
	}

	public static long copy(File input, File output, byte[] copyBuffer)
			throws IOException {
		FileInputStream in = null;
		FileOutputStream out = null;
		try {
			in = new FileInputStream(input);
			out = new FileOutputStream(output);
			return copy(in, out, copyBuffer);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
				}
			}
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
				}
			}
		}
	}

	public static long copy(InputStream in, File outputFile) throws IOException {
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(outputFile);
			return copy(in, out);
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
				}
			}
		}
	}

	public static long copy(InputStream in, OutputStream out)
			throws IOException {
		byte[] buf = new byte[BUF_SIZE];
		return copy(in, out, buf);
	}

	public static long copy(InputStream in, OutputStream out, byte[] copyBuffer)
			throws IOException {
		long bytesCopied = 0;
		int read = -1;

		while ((read = in.read(copyBuffer, 0, copyBuffer.length)) != -1) {
			out.write(copyBuffer, 0, read);
			bytesCopied += read;
		}
		return bytesCopied;
	}

	public static ReadOnlyDriver getDriver(DriverManager dm, File file) {
		String[] names = dm.getDriverNames();
		for (int i = 0; i < names.length; i++) {
			Driver driver = dm.getDriver(names[i]);
			if (driver instanceof FileDriver) {
				if (((FileDriver) driver).fileAccepted(file)) {
					return (ReadOnlyDriver) driver;
				}
			}
		}

		throw new DriverLoadException("No suitable driver for "
				+ file.getAbsolutePath());
	}

	public static ReadOnlyDriver getDriver(DriverManager dm, String prefix) {
		String[] names = dm.getDriverNames();
		for (int i = 0; i < names.length; i++) {
			Driver driver = dm.getDriver(names[i]);
			if (driver instanceof DBDriver) {
				if (((DBDriver) driver).prefixAccepted(prefix)) {
					return (ReadOnlyDriver) driver;
				}
			}
		}

		throw new DriverLoadException("No suitable driver for " + prefix);
	}

}
