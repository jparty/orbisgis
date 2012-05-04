/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able
 * to manipulate and create vector and raster spatial information. OrbisGIS
 * is distributed under GPL 3 license. It is produced  by the geo-informatic team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OrbisGIS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://orbisgis.cerma.archi.fr/>
 *    <http://sourcesup.cru.fr/projects/orbisgis/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */

package org.orbisgis.view.map.tools.generated;

import java.awt.Graphics;
import java.util.ArrayList;

import javax.swing.ImageIcon;

import org.apache.log4j.Logger;
import org.orbisgis.core.layerModel.MapContext;
import org.orbisgis.view.map.tool.Automaton;
import org.orbisgis.view.map.tool.DrawingException;
import org.orbisgis.view.map.tool.FinishedAutomatonException;
import org.orbisgis.view.map.tool.NoSuchTransitionException;
import org.orbisgis.view.map.tool.ToolManager;
import org.orbisgis.view.map.tool.TransitionException;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;


public abstract class Drag implements Automaton {
        protected final static I18n I18N = I18nFactory.getI18n(Drag.class);
	private static Logger logger = Logger.getLogger(Drag.class);

	private String status = "Standby";

	private MapContext ec;

	private ToolManager tm;

        @Override
	public String[] getTransitionLabels() {
		return new String[0];
	}

        @Override
	public String[] getTransitionCodes() {
		return new String[0];
	}

        @Override
	public void init(MapContext ec, ToolManager tm) throws TransitionException,
			FinishedAutomatonException {
		logger.info("status: " + status);
		this.ec = ec;
		this.tm = tm;
		status = "Standby";
		transitionTo_Standby(ec, tm);
		if (isFinished(status)) {
			throw new FinishedAutomatonException();
		}
	}

        @Override
	public void transition(String code) throws NoSuchTransitionException,
			TransitionException, FinishedAutomatonException {
		logger.info("transition code: " + code);

		if ("Standby".equals(status)) {

			if ("press".equals(code)) {
				String preStatus = status;
				try {
					status = "MouseDown";
					logger.info("status: " + status);
					double[] v = tm.getValues();
					for (int i = 0; i < v.length; i++) {
						logger.info("value: " + v[i]);
					}
					transitionTo_MouseDown(ec, tm);
					if (isFinished(status)) {
						throw new FinishedAutomatonException();
					}
					return;
				} catch (TransitionException e) {
					status = preStatus;
					throw e;
				}
			}

		}

		if ("MouseDown".equals(status)) {

			if ("release".equals(code)) {
				String preStatus = status;
				try {
					status = "MouseReleased";
					logger.info("status: " + status);
					double[] v = tm.getValues();
					for (int i = 0; i < v.length; i++) {
						logger.info("value: " + v[i]);
					}
					transitionTo_MouseReleased(ec, tm);
					if (isFinished(status)) {
						throw new FinishedAutomatonException();
					}
					return;
				} catch (TransitionException e) {
					status = preStatus;
					throw e;
				}
			}

		}

		if ("MouseReleased".equals(status)) {

			if ("finished".equals(code)) {
				String preStatus = status;
				try {
					status = "Standby";
					logger.info("status: " + status);
					double[] v = tm.getValues();
					for (int i = 0; i < v.length; i++) {
						logger.info("value: " + v[i]);
					}
					transitionTo_Standby(ec, tm);
					if (isFinished(status)) {
						throw new FinishedAutomatonException();
					}
					return;
				} catch (TransitionException e) {
					status = preStatus;
					throw e;
				}
			}

		}

		throw new NoSuchTransitionException(code);
	}

	public boolean isFinished(String status) {

		if ("Standby".equals(status)) {

			return false;

		}

		if ("MouseDown".equals(status)) {

			return false;

		}

		if ("MouseReleased".equals(status)) {

			return false;

		}

		throw new RuntimeException("Invalid status: " + status);
	}

        @Override
	public void draw(Graphics g) throws DrawingException {

		if ("Standby".equals(status)) {
			drawIn_Standby(g, ec, tm);
		}

		if ("MouseDown".equals(status)) {
			drawIn_MouseDown(g, ec, tm);
		}

		if ("MouseReleased".equals(status)) {
			drawIn_MouseReleased(g, ec, tm);
		}

	}

	public abstract void transitionTo_Standby(MapContext vc, ToolManager tm)
			throws FinishedAutomatonException, TransitionException;

	public abstract void drawIn_Standby(Graphics g, MapContext vc,
			ToolManager tm) throws DrawingException;

	public abstract void transitionTo_MouseDown(MapContext vc, ToolManager tm)
			throws FinishedAutomatonException, TransitionException;

	public abstract void drawIn_MouseDown(Graphics g, MapContext vc,
			ToolManager tm) throws DrawingException;

	public abstract void transitionTo_MouseReleased(MapContext vc,
			ToolManager tm) throws FinishedAutomatonException,
			TransitionException;

	public abstract void drawIn_MouseReleased(Graphics g, MapContext vc,
			ToolManager tm) throws DrawingException;

	protected void setStatus(String status) throws NoSuchTransitionException {
		this.status = status;
	}

	public String getStatus() {
		return status;
	}

        public String getMessage() {

		if ("Standby".equals(status)) {
			return I18N.tr("orbisgis.core.ui.editors.map.tool.pan_standby");
		}

		if ("MouseDown".equals(status)) {
			return I18N.tr("orbisgis.core.ui.editors.map.tool.pan_MouseDown");
		}

		if ("MouseReleased".equals(status)) {
			return "";
		}

		throw new RuntimeException();
	}

	public String getConsoleCommand() {
		return "drag";
	}

        @Override
	public String getTooltip() {
		return I18N.tr("orbisgis.core.ui.editors.map.tool.pan_tooltip");
	}

	protected ImageIcon mouseCursor;

        @Override
	public ImageIcon getImageIcon() {
		if (mouseCursor != null) {
			return mouseCursor;
		} else {
			return null;
		}
	}

        @Override
	public void toolFinished(MapContext vc, ToolManager tm)
			throws NoSuchTransitionException, TransitionException,
			FinishedAutomatonException {
	}

        @Override
	public java.awt.Point getHotSpotOffset() {
		return new java.awt.Point(8, 8);
	}

}
