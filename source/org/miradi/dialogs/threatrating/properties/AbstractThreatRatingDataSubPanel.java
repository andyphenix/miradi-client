/*
Copyright 2005-2022, Foundations of Success, Bethesda, Maryland
on behalf of the Conservation Measures Partnership ("CMP").
Material developed between 2005-2013 is jointly copyright by Beneficent Technology, Inc. ("The Benetech Initiative"), Palo Alto, California.

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

package org.miradi.dialogs.threatrating.properties;

import org.miradi.actions.Actions;
import org.miradi.dialogs.base.ObjectDataInputPanel;
import org.miradi.main.CommandExecutedEvent;
import org.miradi.main.EAM;
import org.miradi.objecthelpers.ORef;
import org.miradi.objecthelpers.ORefList;
import org.miradi.objecthelpers.ThreatTargetVirtualLinkHelper;
import org.miradi.objects.AbstractThreatRatingData;
import org.miradi.objects.Cause;
import org.miradi.objects.ProjectMetadata;
import org.miradi.project.Project;
import org.miradi.schemas.CauseSchema;
import org.miradi.schemas.ProjectMetadataSchema;
import org.miradi.schemas.TargetSchema;

public abstract class AbstractThreatRatingDataSubPanel extends ObjectDataInputPanel
{
    public AbstractThreatRatingDataSubPanel(Project projectToUse, Actions actions) throws Exception
    {
        super(projectToUse, ORef.createInvalidWithType(AbstractThreatRatingData.getThreatRatingDataObjectType(projectToUse)));

        rebuild();
    }

    private void rebuild() throws Exception
    {
        removeAll();
        getFields().clear();

        int threatRatingDataObjectType = AbstractThreatRatingData.getThreatRatingDataObjectType(getProject());
        addFields(threatRatingDataObjectType);

        updateFieldsFromProject();

        doLayout();

        validate();
        repaint();
    }

    protected abstract void addFields(int threatRatingDataObjectType) throws Exception;

    private ORef getTargetRef()
    {
        return getSelectedRefs().getRefForType(TargetSchema.getObjectType());
    }

    private ORef getThreatRef()
    {
        return getSelectedRefs().getRefForType(CauseSchema.getObjectType());
    }

    @Override
    public void commandExecuted(CommandExecutedEvent event)
    {
        super.commandExecuted(event);

        if(event.isSetDataCommandWithThisTypeAndTag(ProjectMetadataSchema.getObjectType(), ProjectMetadata.TAG_THREAT_RATING_MODE))
        {
            setObjectRefs(getSelectedRefs());
        }
    }

    @Override
    public void setObjectRefs(ORef[] orefsToUse)
    {
        try
        {
            ORefList refList = new ORefList(orefsToUse);

            ORef threatRef = getThreatRef();
            ORef targetRef = getTargetRef();

            threatRatingData = null;

            if (threatRef.isValid() && targetRef.isValid())
            {
                Cause cause = (Cause) getProject().findObject(threatRef);
                if (ThreatTargetVirtualLinkHelper.canSupportThreatRatings(getProject(), cause, targetRef))
                    threatRatingData = AbstractThreatRatingData.findOrCreateThreatRatingData(getProject(), threatRef, targetRef);
            }

            if (threatRatingData != null)
                refList.add(threatRatingData.getRef());

            super.setObjectRefs(refList.toArray());

            rebuild();
        }
        catch (Exception e)
        {
            EAM.logException(e);
        }
    }

    private AbstractThreatRatingData threatRatingData;
}
