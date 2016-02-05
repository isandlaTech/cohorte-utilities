package org.cohorte.utilities.installer.panels;

import static org.cohorte.utilities.installer.CInstallerTools.getServiceLogger;

import java.awt.Dimension;
import java.awt.LayoutManager2;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

import org.cohorte.utilities.installer.IConstants;
import org.psem2m.utilities.logging.IActivityLogger;

import com.izforge.izpack.api.data.Panel;
import com.izforge.izpack.api.resource.Resources;
import com.izforge.izpack.gui.IzPanelLayout;
import com.izforge.izpack.gui.LabelFactory;
import com.izforge.izpack.gui.log.Log;
import com.izforge.izpack.installer.data.GUIInstallData;
import com.izforge.izpack.installer.gui.InstallerFrame;
import com.izforge.izpack.installer.gui.IzPanel;

public class CPanelUpdater extends IzPanel implements IConstants {

	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4377975291575437456L;

	/**
	 * Logger
	 */
	private final IActivityLogger pLogger;
	
	private GUIInstallData pInstallData;
	
	private final JLabel pHeader;
	
	final JRadioButton pUpdateButton;
	
	//final JRadioButton pInstallButton;
	
	final JRadioButton pUninstallButton;
	
	String pInstalledVersion;
    String pNewVersion;
	
    JLabel pInstalledVersionLabel;
    JLabel pNewVersionLabel;
    JLabel pDescriptionLabel;
    
    
	public CPanelUpdater(Panel panel, InstallerFrame parent, GUIInstallData installData, Resources resources, Log log)
	{
	    this(panel, parent, installData, new IzPanelLayout(log), resources);
	}
	
	public CPanelUpdater(Panel panel, InstallerFrame parent, GUIInstallData installData, LayoutManager2 layout,
            Resources resources)
	{
		super(panel, parent, installData, layout, resources);
				
		// init logger
        pLogger = getServiceLogger();
        
        pInstallData = installData;
        
        JSeparator wVerticalSep = new JSeparator(SwingConstants.VERTICAL);
        wVerticalSep.setPreferredSize(new Dimension(3,50));
        
        pHeader = LabelFactory.create(getString("installer.updater.header"), LEADING, true);
        add(pHeader, NEXT_LINE);
        
        add(wVerticalSep, NEXT_LINE);
        
        pInstalledVersionLabel = LabelFactory.create("", LEADING, true);
        pNewVersionLabel = LabelFactory.create("", LEADING, true);
        
        add(pInstalledVersionLabel, NEXT_LINE);
        add(pNewVersionLabel, NEXT_LINE);
        
        add(wVerticalSep, NEXT_LINE);
                
        pUpdateButton = new JRadioButton(getString("installer.updater.updateButton"));
        pUpdateButton.addActionListener(new ActionListener() {			
			//@Override
			public void actionPerformed(ActionEvent e) {
				selectUpdate();
			}
		});
        add(pUpdateButton, NEXT_LINE);
        /*
        pInstallButton = new JRadioButton(getString("installer.updater.installButton"));
        pInstallButton.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				selectInstall();
			}
		});
        add(pInstallButton, NEXT_LINE);
        */
        pUninstallButton = new JRadioButton(getString("installer.updater.uninstallButton"));
        pUninstallButton.addActionListener(new ActionListener() {			
			//@Override
			public void actionPerformed(ActionEvent e) {
				selectUninstall();
			}
		});
        add(pUninstallButton, NEXT_LINE);
        
        ButtonGroup wButtonsGroup = new ButtonGroup();
        //wButtonsGroup.add(pInstallButton);
        wButtonsGroup.add(pUpdateButton);
        wButtonsGroup.add(pUninstallButton);
        
        add(wVerticalSep, NEXT_LINE);
                
        pDescriptionLabel = LabelFactory.create("", LEADING, true);
        JPanel wDescriptionPanel = new JPanel();
        //wDescriptionPanel.setLayout(new BoxLayout(wDescriptionPanel, BoxLayout.PAGE_AXIS));
        wDescriptionPanel.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createTitledBorder(""),
                        BorderFactory.createEmptyBorder(10,10,10,10)));
        wDescriptionPanel.add(pDescriptionLabel);
        
        add(wDescriptionPanel, NEXT_LINE);
        
        getLayoutHelper().completeLayout();
	}
	
	@Override
	public void panelActivate() {
		// TODO Auto-generated method stub
		super.panelActivate();		
		pInstalledVersion = installData.getVariable(INSTALLER__ALREADY_INSTALLED_VERSION);
	    pNewVersion = installData.getVariable(INSTALLER__NEW_VERSION);
	    pInstalledVersionLabel.setText("<html>"+getString("installer.updater.installedVersionLabel") + "<font color='red'>"+pInstalledVersion + "</font></html>");
        pNewVersionLabel.setText("<html>"+getString("installer.updater.newVersionLabel") + "<font color='green'>"+pNewVersion+"</font></html>");
        selectUpdate();
        
	}

	private void selectUpdate() {
		pUpdateButton.setSelected(true);
		//pInstallButton.setSelected(false);
		pUninstallButton.setSelected(false);
		pInstallData.setVariable(INSTALLER__DO_ACTION, "update");
		pDescriptionLabel.setText("<html><font>"+getString("installer.updater.updateButtonDescription")+"</font></html>");
	}
	
	/*
	private void selectInstall() {
		pUpdateButton.setSelected(false);
		pInstallButton.setSelected(true);
		pUninstallButton.setSelected(false);
		pInstallData.setVariable(INSTALLER__DO_ACTION, "install");
		pDescriptionLabel.setText("<html><font>"+getString("installer.updater.installButtonDescription")+"</font></html>");
	}
	*/
	
	private void selectUninstall() {
		pUpdateButton.setSelected(false);
		//pInstallButton.setSelected(false);
		pUninstallButton.setSelected(true);
		pInstallData.setVariable(INSTALLER__DO_ACTION, "uninstall");
		pDescriptionLabel.setText("<html><font>"+getString("installer.updater.uninstallButtonDescription")+"</font></html>");
	}
}
