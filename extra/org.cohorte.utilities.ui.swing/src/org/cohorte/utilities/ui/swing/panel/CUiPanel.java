package org.cohorte.utilities.ui.swing.panel;

import javax.swing.Icon;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.psem2m.utilities.IXDescriber;
import org.psem2m.utilities.logging.IActivityLogger;

/**
 * @author ogattaz
 *
 */
public class CUiPanel implements IUiPanel, IUiPanelControler, IXDescriber {

	private JFrame pFrame;
	private final Icon pIcon;
	protected final IActivityLogger pLogger;
	private final String pName;
	protected JPanel pPanel = null;
	protected final IUiPanelsControler pPanelsControler;

	private final String pTip;

	/**
	 * @param aParent
	 */
	protected CUiPanel(IActivityLogger aLogger, final String aName,
			final String aTip, final Icon aIcon,
			final IUiPanelsControler aPanelsControler) {

		super();
		pLogger = aLogger;
		pName = aName;
		pTip = aTip;
		pIcon = aIcon;
		pPanelsControler = aPanelsControler;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.psem2m.utilities.IXDescriber#addDescriptionInBuffer(java.lang.Appendable
	 * )
	 */
	@Override
	public Appendable addDescriptionInBuffer(Appendable aBuffer) {
		return aBuffer;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.psem2m.isolates.ui.admin.api.IUiAdminPanel#getIcon()
	 */
	@Override
	public Icon getIcon() {

		return pIcon;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.psem2m.isolates.ui.admin.api.IUiAdminPanel#getName()
	 */
	@Override
	public String getName() {

		return pName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.psem2m.isolates.ui.admin.api.IUiAdminPanel#getPanel()
	 */
	@Override
	public JPanel getPanel() {

		return pPanel;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.psem2m.isolates.ui.admin.api.IUiAdminPanel#getConroler()
	 */
	@Override
	public IUiPanelsControler getPanelsControler() {

		return pPanelsControler;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.psem2m.isolates.ui.admin.api.IUiAdminPanel#getTip()
	 */
	@Override
	public String getTip() {

		return pTip;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.psem2m.isolates.ui.admin.api.IUiAdminPanel#hasPanel()
	 */
	@Override
	public boolean hasPanel() {

		return getPanel() != null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.psem2m.isolates.ui.admin.api.IUiAdminPanel#hasControler()
	 */
	@Override
	public boolean hasPanelsControler() {

		return getPanelsControler() != null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.psem2m.isolates.ui.admin.api.IUiAdminPanel#pack()
	 */
	@Override
	public void pack() {

		pFrame.pack();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.psem2m.isolates.ui.admin.api.IUiAdminPanelControler#setUiAdminFont
	 * (org.psem2m.isolates.ui.admin.api.EUiAdminFont)
	 */
	@Override
	public void setUiFont(final EUiFont aUiAdminFont) {

		pLogger.logInfo(this, "setUiFont", "Font=[%s]", aUiAdminFont.name());

		// to be overwriten
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.psem2m.utilities.IXDescriber#toDescription()
	 */
	@Override
	public String toDescription() {
		return addDescriptionInBuffer(new StringBuilder(16)).toString();
	}

}
