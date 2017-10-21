package org.cohorte.utilities.ui.swing.panel;

import javax.swing.Icon;
import javax.swing.JPanel;

/**
 * @author ogattaz
 *
 */
public interface IUiPanel {

	/**
	 * @return
	 */
	public Icon getIcon();

	/**
	 * @return
	 */
	public String getName();

	/**
	 * @return
	 */
	public JPanel getPanel();

	/**
	 * @return
	 */
	public IUiPanelsControler getPanelsControler();

	/**
	 * @return
	 */
	public String getTip();

	/**
	 * @return
	 */
	public boolean hasPanel();

	/**
	 * @return
	 */
	public boolean hasPanelsControler();

	/**
	 *
	 */
	public void pack();

}
