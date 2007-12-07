package org.orbisgis.core.errorListener;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.sif.CRFlowLayout;
import org.sif.CarriageReturn;

public class ErrorPanel extends JPanel {

	private final static Icon warningIcon = new ImageIcon(ErrorPanel.class
			.getResource("warning.gif"));

	private final static Icon errorIcon = new ImageIcon(ErrorPanel.class
			.getResource("error.gif"));

	private static final String SHOW = "Show";
	private static final String DELETE = "Delete";
	private static final String DETAILS = "Show Details >>";
	private JPanel normalPanel;
	private JTextArea txtMessage;
	private JButton btnDetails;
	private JPanel extendedPanel;
	private JPanel controlButtonsPanel;
	private JButton btnDelete;
	private JButton btnShow;
	private JTable tbl;
	private ErrorsTableModel errorsModel;
	private NoWrapTextPane txtException;
	private MyListener myListener = new MyListener();
	private ErrorFrame frame;
	private Dimension expandedSize = null;
	private Dimension collapsedSize = null;

	private JLabel iconLabel;

	public ErrorPanel(ErrorFrame frame) {
		this.frame = frame;
		this.setLayout(new BorderLayout());
		this.add(getNormalPanel(), BorderLayout.NORTH);
		this.add(getExtendedPanel(), BorderLayout.CENTER);
		enableButtons();
	}

	private JPanel getExtendedPanel() {
		if (extendedPanel == null) {
			extendedPanel = new JPanel();
			BorderLayout borderLayout = new BorderLayout();
			borderLayout.setHgap(30);
			extendedPanel.setLayout(borderLayout);
			extendedPanel.add(new JSeparator(), BorderLayout.NORTH);
			errorsModel = new ErrorsTableModel();
			tbl = new JTable(errorsModel);
			tbl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			tbl.getSelectionModel().addListSelectionListener(myListener);
			tbl.addMouseListener(myListener);
			tbl.addComponentListener(new ComponentAdapter() {

				@Override
				public void componentShown(ComponentEvent e) {
					AutofitTableColumns.autoResizeTable(tbl, true);
					tbl.repaint();
				}

			});
			final JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
			JScrollPane scrollPane = new JScrollPane(tbl);
			scrollPane.setMinimumSize(new Dimension(300, 200));
			split.setLeftComponent(scrollPane);
			txtException = new NoWrapTextPane();
			split.setRightComponent(new JScrollPane(txtException));
			extendedPanel.add(split, BorderLayout.CENTER);
			extendedPanel.add(getControlButtonsPanel(), BorderLayout.EAST);
			extendedPanel.setVisible(false);
		}

		return extendedPanel;
	}

	private Component getControlButtonsPanel() {
		if (controlButtonsPanel == null) {
			controlButtonsPanel = new JPanel();
			controlButtonsPanel.setLayout(new CRFlowLayout());
			btnDelete = createButton(DELETE);
			controlButtonsPanel.add(btnDelete);
			controlButtonsPanel.add(new CarriageReturn());
			btnShow = createButton(SHOW);
			controlButtonsPanel.add(btnShow);
			controlButtonsPanel.add(new CarriageReturn());
		}

		return controlButtonsPanel;
	}

	private JButton createButton(String text) {
		JButton btn = new JButton(text);
		btn.setActionCommand(text);
		btn.addActionListener(myListener);
		return btn;
	}

	private JPanel getNormalPanel() {
		if (normalPanel == null) {
			normalPanel = new JPanel();
			BorderLayout borderLayout = new BorderLayout();
			normalPanel.setLayout(borderLayout);
			txtMessage = new JTextArea();
			txtMessage.setFont(UIManager.getFont("Label.font"));
			txtMessage.setEditable(false);
			txtMessage.setOpaque(false);
			txtMessage.setWrapStyleWord(true);
			txtMessage.setLineWrap(true);
			JPanel txtPnl = new JPanel();
			BorderLayout bl = new BorderLayout();
			bl.setVgap(20);
			txtPnl.setLayout(bl);
			txtPnl.add(txtMessage, BorderLayout.CENTER);
			txtPnl.add(new JLabel(), BorderLayout.NORTH);
			normalPanel.add(txtPnl, BorderLayout.CENTER);
			iconLabel = new JLabel(errorIcon);
			JPanel iconPnl = new JPanel();
			FlowLayout fl = new FlowLayout();
			fl.setHgap(20);
			fl.setVgap(20);
			iconPnl.setLayout(fl);
			iconPnl.add(iconLabel);
			normalPanel.add(iconPnl, BorderLayout.WEST);
			JPanel buttons = new JPanel();
			btnDetails = createButton(DETAILS);
			buttons.add(btnDetails);
			normalPanel.add(buttons, BorderLayout.SOUTH);
			normalPanel.add(new JLabel(""), BorderLayout.NORTH);
		}

		return normalPanel;
	}

	private class MyListener extends MouseAdapter implements ActionListener,
			ListSelectionListener, MouseListener {

		public void actionPerformed(ActionEvent e) {
			if (e.getActionCommand().equals(DETAILS)) {
				if (extendedPanel.isVisible()) {
					extendedPanel.setVisible(false);
					btnDetails.setText(DETAILS);
					expandedSize = frame.getSize();
					if (collapsedSize == null) {
						frame.pack();
						if (frame.getSize().width > 10) {
							collapsedSize = frame.getSize();
						}
					} else {
						frame.setSize(collapsedSize);
					}
				} else {
					extendedPanel.setVisible(true);
					btnDetails.setText("<< Hide Details");
					collapsedSize = frame.getSize();
					if (expandedSize == null) {
						frame.pack();
						if (frame.getSize().width > 10) {
							expandedSize = frame.getSize();
						}
					} else {
						frame.setSize(expandedSize);
					}
				}
			} else if (e.getActionCommand().equals(DELETE)) {
				if (tbl.getSelectedRow() != -1) {
					errorsModel.removeError(tbl.getSelectedRow());
				}
			} else if (e.getActionCommand().equals(SHOW)) {
				if (tbl.getSelectedRow() != -1) {
					txtException.setText(errorsModel.getTrace(tbl
							.getSelectedRow()));
					txtException.setCaretPosition(0);
				}
			}
		}

		public void valueChanged(ListSelectionEvent e) {
			enableButtons();
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			if (e.getClickCount() == 2) {
				txtException
						.setText(errorsModel.getTrace(tbl.getSelectedRow()));
				txtException.setCaretPosition(0);
			}
		}

	}

	private void enableButtons() {
		if (tbl.getSelectedRow() != -1) {
			btnDelete.setEnabled(true);
			btnShow.setEnabled(true);
		} else {
			btnDelete.setEnabled(false);
			btnShow.setEnabled(false);
		}
	}

	public void addError(ErrorMessage errorMessage) {
		txtMessage.setText(errorMessage.getUserMessage());
		if (errorMessage.isError()) {
			iconLabel.setIcon(errorIcon);
		} else {
			iconLabel.setIcon(warningIcon);
		}
		errorsModel.addError(errorMessage);
		tbl.getSelectionModel().setSelectionInterval(0, 0);
		txtException.setText(errorsModel.getTrace(tbl.getSelectedRow()));
		txtException.setCaretPosition(0);
	}

	public boolean isCollapsed() {
		return !extendedPanel.isVisible();
	}
}
