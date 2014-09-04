/* 
Copyright 2005-2014, Foundations of Success, Bethesda, Maryland
(on behalf of the Conservation Measures Partnership, "CMP") and 
Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 

This file is part of Miradi

Miradi is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License version 3, 
as published by the Free Software Foundation.

Miradi is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with Miradi.  If not, see <http://www.gnu.org/licenses/>. 
*/ 

package org.miradi.migrations.forward;

import org.miradi.main.EAM;
import org.miradi.migrations.NewlyAddedFieldsMigration;
import org.miradi.migrations.RawProject;
import org.miradi.schemas.DiagramFactorSchema;

import java.util.HashMap;

public class MigrationTo16 extends NewlyAddedFieldsMigration
{
	public MigrationTo16(RawProject rawProjectToUse)
	{
		super(rawProjectToUse, DiagramFactorSchema.getObjectType());
	}
	
	@Override
	protected HashMap<String, String> createFieldsToLabelMapToModify()
	{
		HashMap<String, String> fieldsToAdd = new HashMap<String, String>();
		fieldsToAdd.put(TAG_HEADER_HEIGHT, EAM.text("Header Height"));
		
		return fieldsToAdd;
	}

	@Override
	protected int getToVersion()
	{
		return VERSION_TO;
	}
	
	@Override
	protected int getFromVersion() 
	{
		return VERSION_FROM;
	}
	
	@Override
	protected String getDescription()
	{
		return EAM.text("This migration adds a new field to the Diagram Factor properties.");
	}
	
	public static final int VERSION_FROM = 15;
	public static final int VERSION_TO = 16;

    public static final String TAG_HEADER_HEIGHT = "HeaderHeight";
}
