/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 * 
 *
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
 *
 * or contact directly:
 * info _at_ orbisgis.org
 */
package org.orbisgis.view.docking;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import javax.swing.Icon;


/**
 * @brief Parameters of a panel in a docking environnement
 * Theses parameters indicate the behavior related to a panel
 * in a docking environnement.
 * 
 * This class is created thanks to the NetBeans user interface.
 * Use the "Add property" NetBeans function to add properties easily.
 * See documentation related to java.beans management systems
 * 
 * @warning New properties must be linked with the current docking system {@link OrbisGISView} 
 */
public class DockingPanelParameters implements Serializable {
    private static final long serialVersionUID = 2L; /*<! Update this integer while adding properties (1 for each new property)*/
    
    private PropertyChangeSupport propertySupport;
   
    private String title;
    public static final String PROP_TITLE = "title";
    
    private Icon titleIcon = null;
    public static final String PROP_TITLEICON = "titleIcon";

    /**
     * Get the value of titleIcon
     *
     * @return the value of titleIcon
     */
    public Icon getTitleIcon() {
        return titleIcon;
    }

    /**
     * Set the value of titleIcon
     *
     * @param titleIcon new value of titleIcon
     */
    public void setTitleIcon(Icon titleIcon) {
        Icon oldTitleIcon = this.titleIcon;
        this.titleIcon = titleIcon;
        propertySupport.firePropertyChange(PROP_TITLEICON, oldTitleIcon, titleIcon);
    }


    /**
     * Get the value of title
     *
     * @return the value of title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Set the value of title
     *
     * @param title new value of title
     */
    public void setTitle(String title) {
        String oldTitle = this.title;
        this.title = title;
        propertySupport.firePropertyChange(PROP_TITLE, oldTitle, title);
    }
    
    /**
     * Default constructor
     */
    public DockingPanelParameters() {
        propertySupport = new PropertyChangeSupport(this);
    }
    
    /**
     * Add a property-change listener for all properties.
     * The listener is called for all properties.
     * @param listener The PropertyChangeListener instance
     * @note Use EventHandler.create to build the PropertyChangeListener instance
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertySupport.addPropertyChangeListener(listener);
    }
    /**
     * Add a property-change listener for a specific property.
     * The listener is called only when there is a change to 
     * the specified property.
     * @param prop The static property name PROP_..
     * @param listener The PropertyChangeListener instance
     * @note Use EventHandler.create to build the PropertyChangeListener instance
     */
    public void addPropertyChangeListener(String prop,PropertyChangeListener listener) {
        propertySupport.addPropertyChangeListener(prop, listener);
    }
    /**
     * Remove the specified listener from the list
     * @param listener The listener instance
     */
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertySupport.removePropertyChangeListener(listener);
    }
    
    /**
     * Remove the specified listener for a specified property from the list
     * @param prop The static property name PROP_..
     * @param listener The listener instance
     */
    public void removePropertyChangeListener(String prop,PropertyChangeListener listener) {
        propertySupport.removePropertyChangeListener(prop,listener);
    }
}