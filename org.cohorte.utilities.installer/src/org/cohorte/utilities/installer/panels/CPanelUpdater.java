package org.cohorte.utilities.installer.panels;

import java.awt.LayoutManager2;

import com.izforge.izpack.api.data.Panel;
import com.izforge.izpack.api.resource.Resources;
import com.izforge.izpack.gui.IzPanelLayout;
import com.izforge.izpack.gui.log.Log;
import com.izforge.izpack.installer.data.GUIInstallData;
import com.izforge.izpack.installer.gui.InstallerFrame;

/**
 * MOD_OG_20160715 console mode
 * 
 * @author ogattaz
 *
 */
@Deprecated
public class CPanelUpdater extends CUpdaterPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4377975291575437456L;

	/**
	 * @param panel
	 * @param parent
	 * @param installData
	 * @param resources
	 * @param log
	 */
	public CPanelUpdater(Panel panel, InstallerFrame parent, GUIInstallData installData, Resources resources,
			Log log) {
		this(panel, parent, installData, new IzPanelLayout(log), resources);
	}

	/**
	 * @param panel
	 * @param parent
	 * @param installData
	 * @param layout
	 * @param resources
	 */
	public CPanelUpdater(Panel panel, InstallerFrame parent, GUIInstallData installData,
			LayoutManager2 layout, Resources resources) {
		super(panel, parent, installData, layout, resources);
	}
}