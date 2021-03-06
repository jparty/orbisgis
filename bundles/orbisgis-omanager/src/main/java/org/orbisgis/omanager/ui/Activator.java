/*
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

package org.orbisgis.omanager.ui;

import java.awt.Component;
import org.apache.felix.shell.gui.Plugin;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * Serve Plugin service.
 * @author Nicolas Fortin
 */
public class Activator implements BundleActivator, Plugin {
    private static final I18n I18N = I18nFactory.getI18n(MainPanel.class);
    private String shellName;
    private MainPanel mainPanel;
    private BundleContext bundleContext;

    @Override
    public void start(BundleContext bundleContext) throws Exception {
        this.bundleContext = bundleContext;
        shellName = I18N.tr("Fusion"); // Means Local&Repo on the same time
        bundleContext.registerService(Plugin.class,this,null);
    }

    @Override
    public void stop(BundleContext bundleContext) throws Exception {
        if(mainPanel!=null) {
            mainPanel.dispose();
        }
    }

    @Override
    public String getName() {
        return shellName;
    }

    @Override
    public Component getGUI() {
        if(mainPanel==null) {
            mainPanel = new MainPanel(bundleContext);
        }
        return mainPanel;
    }
}
