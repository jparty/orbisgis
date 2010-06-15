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
package org.orbisgis.core.ui.editorViews.toc;

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.TreeCellRenderer;

import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.driver.DriverException;
import org.orbisgis.core.Services;
import org.orbisgis.core.layerModel.ILayer;
import org.orbisgis.core.renderer.legend.Legend;
import org.orbisgis.core.renderer.se.Rule;
import org.orbisgis.core.sif.CRFlowLayout;

public class TocRenderer extends TocAbstractRenderer implements
		TreeCellRenderer {
	private static final Color SELECTED = Color.lightGray;

	private static final Color DESELECTED = Color.white;

	private static final Color SELECTED_FONT = Color.white;

	private static final Color DESELECTED_FONT = Color.black;

	private Toc toc;

	private TOCRenderPanel ourJPanel;

	public TocRenderer(Toc toc) {
		this.toc = toc;
	}

	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean selected, boolean expanded, boolean leaf, int row,
			boolean hasFocus) {
		if (value instanceof ILayer) {
			ourJPanel = new LayerRenderPanel();
			ourJPanel.setNodeCosmetic(tree, (ILayer) value, selected, expanded,
					leaf, row, hasFocus);

			return ourJPanel.getJPanel();
		} else {
			TocTreeModel.RuleNode ruleNode = (TocTreeModel.RuleNode) value;
			ILayer layer = ruleNode.getLayer();

			try {
				if (layer.isVectorial()) {
					ourJPanel = new RuleRenderPanel();
					ourJPanel.setNodeCosmetic(tree, layer, ruleNode
							.getRuleIndex(), selected, expanded, leaf, row,
							hasFocus);
					return ourJPanel.getJPanel();
				}

				else if (layer.isWMS()) {
					WMSLegendRenderPanel ourJPanel = new WMSLegendRenderPanel();
					ourJPanel.setNodeCosmetic(tree, layer,
							ruleNode.getRuleIndex(), selected, expanded,
							leaf, row, hasFocus);
					return ourJPanel.getJPanel();
				}
				
				else {
					RasterLegendRenderPanel ourJPanel = new RasterLegendRenderPanel();
					ourJPanel.setNodeCosmetic(tree, ruleNode.getLayer(),
							ruleNode.getRuleIndex(), selected, expanded,
							leaf, row, hasFocus);
					return ourJPanel.getJPanel();
				}
				
			} catch (DriverException e) {
				e.printStackTrace();
			}

		}
		return tree;
	}

	public Rectangle getCheckBoxBounds() {
		return ourJPanel.getCheckBoxBounds();
	}

	public class LayerRenderPanel implements TOCRenderPanel {
		private JCheckBox check;

		private JLabel iconAndLabel;

		private JPanel jpanel;

		public LayerRenderPanel() {
			FlowLayout fl = new FlowLayout(CRFlowLayout.LEADING);
			fl.setHgap(0);
			jpanel = new JPanel();
			jpanel.setLayout(fl);
			check = new JCheckBox();
			iconAndLabel = new JLabel();
			jpanel.add(check);
			jpanel.add(iconAndLabel);
		}

		public void setNodeCosmetic(JTree tree, ILayer node, boolean selected,
				boolean expanded, boolean leaf, int row, boolean hasFocus) {
			check.setVisible(true);
			check.setSelected(node.isVisible());

			Icon icon = null;
			try {
				icon = getLayerIcon(node);
			} catch (DriverException e) {
			} catch (IOException e) {
			}
			if (null != icon) {
				iconAndLabel.setIcon(icon);
			}
			String name = node.getName();
			SpatialDataSourceDecorator dataSource = node.getDataSource();
			if ((dataSource != null) && (dataSource.isModified())) {
				name += "*";
			}
			iconAndLabel.setText(name);
			iconAndLabel.setVisible(true);

			if (toc.isActive(node)) {
				Font font = iconAndLabel.getFont();
				font = font.deriveFont(Font.ITALIC, font.getSize());
				iconAndLabel.setFont(font);
			}

			if (selected) {
				jpanel.setBackground(SELECTED);
				check.setBackground(SELECTED);
				iconAndLabel.setForeground(SELECTED_FONT);
			} else {
				jpanel.setBackground(DESELECTED);
				check.setBackground(DESELECTED);
				iconAndLabel.setForeground(DESELECTED_FONT);
			}
		}

		public Rectangle getCheckBoxBounds() {
			return check.getBounds();
		}

		@Override
		public Component getJPanel() {

			return jpanel;
		}

		@Override
		public void setNodeCosmetic(JTree tree, ILayer layer, int ruleIndex,
				boolean selected, boolean expanded, boolean leaf, int row,
				boolean hasFocus) {
		}

	}

	public class RuleRenderPanel implements TOCRenderPanel {

		private JCheckBox check;

		private JLabel label;

		private JPanel jpanel;

		private JPanel pane;

		public RuleRenderPanel() {
			jpanel = new JPanel();
			check = new JCheckBox();
			check.setAlignmentY(Component.TOP_ALIGNMENT);
			label = new JLabel();
			label.setAlignmentY(Component.TOP_ALIGNMENT);
			pane = new JPanel();
			pane.setLayout(new BoxLayout(pane, BoxLayout.X_AXIS));
			pane.add(check);
			pane.add(label);
			pane.setBackground(DESELECTED);
			check.setBackground(DESELECTED);
			jpanel.add(pane); // really useful ?
		}

		@Override
		public void setNodeCosmetic(JTree tree, ILayer node, int ruleIndex,
				boolean selected, boolean expanded, boolean leaf, int row,
				boolean hasFocus) {

			check.setVisible(true);

			try {
				check.setSelected(node.getRenderingRule().get(ruleIndex).isVisible());
				jpanel.setBackground(DESELECTED);

				Graphics2D dummyGraphics = new BufferedImage(10, 10,
						BufferedImage.TYPE_INT_ARGB).createGraphics();

				Rule rule = node.getRenderingRule().get(ruleIndex);


				if (selected) {
					jpanel.setBackground(SELECTED);
					check.setBackground(SELECTED);
					pane.setBackground(SELECTED);
					label.setForeground(SELECTED_FONT);
				} else {
					jpanel.setBackground(DESELECTED);
					check.setBackground(DESELECTED);
					pane.setBackground(DESELECTED);
					label.setForeground(DESELECTED_FONT);
				}

				label.setText(rule.getName());
				label.setVisible(true);
				
				// What's the best size to represent this legend ?
				/*int[] imageSize = rule.getImageSize(dummyGraphics);
				if ((imageSize[0] != 0) && (imageSize[1] != 0)) {
					BufferedImage legendImage = new BufferedImage(imageSize[0],
							imageSize[1], BufferedImage.TYPE_INT_ARGB);
					rule.drawImage(legendImage.createGraphics());
					ImageIcon imageIcon = new ImageIcon(legendImage);
					lblLegend.setIcon(imageIcon);
					lblLegend.setVisible(true);
				}*/

			} catch (Exception e) {
				Services.getErrorManager().error(
						"Cannot access the legends in layer " + node.getName(),
						e);
			}
		}

		@Override
		public Rectangle getCheckBoxBounds() {
			return check.getBounds();

		}

		@Override
		public Component getJPanel() {
			return jpanel;
		}

		@Override
		public void setNodeCosmetic(JTree tree, ILayer value, boolean selected,
				boolean expanded, boolean leaf, int row, boolean hasFocus) {

		}
	}

	public class RasterLegendRenderPanel {

		private JLabel lblLegend;
		private JPanel jpanel;

		public RasterLegendRenderPanel() {
			FlowLayout fl = new FlowLayout(FlowLayout.LEADING);
			fl.setHgap(0);
			jpanel = new JPanel();
			jpanel.setLayout(fl);
			lblLegend = new JLabel();
			jpanel.add(lblLegend);
		}

		public void setNodeCosmetic(JTree tree, ILayer node, int legendIndex,
				boolean selected, boolean expanded, boolean leaf, int row,
				boolean hasFocus) {
			try {
				jpanel.setBackground(DESELECTED);
				Graphics2D dummyGraphics = new BufferedImage(10, 10,
						BufferedImage.TYPE_INT_ARGB).createGraphics();
				Legend legend = node.getRenderingLegend()[legendIndex];
				int[] imageSize = legend.getImageSize(dummyGraphics);
				if ((imageSize[0] != 0) && (imageSize[1] != 0)) {
					BufferedImage legendImage = new BufferedImage(imageSize[0],
							imageSize[1], BufferedImage.TYPE_INT_ARGB);
					legend.drawImage(legendImage.createGraphics());
					ImageIcon imageIcon = new ImageIcon(legendImage);
					lblLegend.setIcon(imageIcon);
				}
			} catch (DriverException e) {
				Services.getErrorManager().error(
						"Cannot access the legends in layer " + node.getName(),
						e);
			}
		}

		public Component getJPanel() {
			return jpanel;
		}

	}

	public class WMSLegendRenderPanel {

		private JLabel lblLegend;
		private JPanel jpanel;

		public WMSLegendRenderPanel() {
			FlowLayout fl = new FlowLayout(FlowLayout.LEADING);
			fl.setHgap(0);
			jpanel = new JPanel();
			jpanel.setLayout(fl);
			lblLegend = new JLabel();
			jpanel.add(lblLegend);
		}

		public void setNodeCosmetic(JTree tree, ILayer node, int legendIndex,
				boolean selected, boolean expanded, boolean leaf, int row,
				boolean hasFocus) {
			try {
				jpanel.setBackground(DESELECTED);
				Graphics2D dummyGraphics = new BufferedImage(10, 10,
						BufferedImage.TYPE_INT_ARGB).createGraphics();
				Legend legend = node.getRenderingLegend()[legendIndex];
				int[] imageSize = legend.getImageSize(dummyGraphics);
				if ((imageSize[0] != 0) && (imageSize[1] != 0)) {
					BufferedImage legendImage = new BufferedImage(imageSize[0],
							imageSize[1], BufferedImage.TYPE_INT_ARGB);
					legend.drawImage(legendImage.createGraphics());
					ImageIcon imageIcon = new ImageIcon(legendImage);
					lblLegend.setIcon(imageIcon);
				}
			} catch (DriverException e) {
				Services.getErrorManager().error(
						"Cannot access the legends in layer " + node.getName(),
						e);
			}
		}

		public Component getJPanel() {
			return jpanel;
		}

	}

	

}
