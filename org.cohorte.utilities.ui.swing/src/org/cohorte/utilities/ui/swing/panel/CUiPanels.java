package org.cohorte.utilities.ui.swing.panel;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;

import org.psem2m.utilities.CXStringUtils;
import org.psem2m.utilities.IXDescriber;
import org.psem2m.utilities.logging.IActivityLogger;

/**
 * @author ogattaz
 *
 */
public class CUiPanels implements IUiPanelsControler, IXDescriber {

	private final JFrame pFrame;

	private final IActivityLogger pLogger;

	private final JTabbedPane pTabbedPane;

	private List<CUiPanel> pUiPanels = new ArrayList<CUiPanel>();

	/**
	 * @param aParent
	 */
	public CUiPanels(IActivityLogger aLogger, final JFrame aFrame,
			final JTabbedPane aTabbedPane) {

		super();
		pLogger = aLogger;
		pFrame = aFrame;
		pTabbedPane = aTabbedPane;
	}

	/**
	 * @param aUiAdminPanel
	 */
	public void add(final CUiPanel aUiPanel) {
		// insert first
		add(aUiPanel, 0);
	}

	/**
	 * @param aUiPanel
	 * @param aLocation
	 */
	public void add(final CUiPanel aUiPanel, final EUiPanelLocation aLocation) {
		int wIdx;

		if (aLocation.isFIRST()) {
			wIdx = 0;
		} else if (aLocation.isLAST()) {
			wIdx = pUiPanels.size();
		} else {
			// TODO => ASCENDING & DESCENDING,
			wIdx = 0;
		}
		add(aUiPanel, wIdx);
	}

	/**
	 * @param aUiPanel
	 * @param aIndex
	 */
	public CUiPanel add(final CUiPanel aUiPanel, int aIndex) {

		if (aIndex < 0) {
			aIndex = 0;
		} else if (aIndex > pUiPanels.size()) {
			aIndex = pUiPanels.size();
		}

		pLogger.logInfo(this, "add", "index=[%d] panel=[%s]", aIndex, aUiPanel);
		pUiPanels.add(aIndex, aUiPanel);
		// pTabbedPane.add(aUiPanel.getPanel(), aIndex);
		pTabbedPane.insertTab(aUiPanel.getName(), aUiPanel.getIcon(),
				aUiPanel.getPanel(), aUiPanel.getTip(), aIndex);

		int wIdx = 0;
		for (Component wComponent : pTabbedPane.getComponents()) {
			pLogger.logInfo(this, "add", "component(%d)=[%s]", wIdx, wComponent
					.getClass().getSimpleName());
			wIdx++;
		}

		pFrame.pack();
		pTabbedPane.setSelectedIndex(aIndex);

		return aUiPanel;
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

		CXStringUtils.appendKeyValInBuff(aBuffer, "NbPanels", pUiPanels.size());
		return aBuffer;
	}

	/**
	 *
	 */
	public void destroy() {

		pLogger.logInfo(this, "remove", "clear [%s] panels", pUiPanels.size());
		pUiPanels.clear();
		pTabbedPane.removeAll();
	}

	/**
	 * @return
	 */
	List<String> getNames() {
		List<String> wNames = new ArrayList<String>();
		for (CUiPanel wPanel : pUiPanels) {
			wNames.add(wPanel.getName());
		}
		return wNames;
	}

	/**
	 * @param aUiAdminPanel
	 */
	public CUiPanel remove(final CUiPanel aUiPanel) {

		pLogger.logInfo(this, "remove", "panel=[%s]", aUiPanel);
		pUiPanels.remove(aUiPanel);
		pTabbedPane.remove(aUiPanel.getPanel());
		return aUiPanel;
	}

	/**
	 * @param aIndex
	 * @return
	 */
	public CUiPanel remove(int aIndex) {

		if (aIndex < 0) {
			aIndex = 0;
		} else if (aIndex > pUiPanels.size()) {
			aIndex = pUiPanels.size();
		}

		return remove(pUiPanels.get(aIndex));
	}

	/**
	 *
	 */
	public void removeAll() {
		pLogger.logInfo(this, "removeAll", "panels=[%s]",
				CXStringUtils.stringListToString(getNames()));
		pUiPanels.clear();
		pTabbedPane.removeAll();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cohorte.utilities.ui.swing.panel.IUiPanelsControler#select(int)
	 */
	@Override
	public void select(int aIndex) {

		if (aIndex < 0) {
			aIndex = 0;
		} else if (aIndex > pUiPanels.size()) {
			aIndex = pUiPanels.size();
		}
		pTabbedPane.setSelectedIndex(aIndex);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.psem2m.isolates.ui.admin.api.IUiAdminPanelControler#setUiAdminFont
	 * (org.psem2m.isolates.ui.admin.api.EUiAdminFont)
	 */
	@Override
	public void setUiFont(final EUiFont aUiFont) {

		for (CUiPanel wUiPanel : pUiPanels) {
			wUiPanel.setUiFont(aUiFont);
		}
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
