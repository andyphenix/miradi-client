/* 
* Copyright 2005-2008, Foundations of Success, Bethesda, Maryland 
* (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.miradi.views.reports;

import java.io.File;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.view.save.JRRtfSaveContributor;

public class MiradiRtfSaveContributor extends JRRtfSaveContributor
{
	@Override
	public void save(JasperPrint jasperPrint, File file) throws JRException
	{
		if(MiradiSaveContributorHelper.askUserForConfirmation("RTF Export", "reports/ReportSaveRTFInformation.txt"))
			super.save(jasperPrint, file);
	}

}
