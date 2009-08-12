/*
 * created 13.03.2005
 * 
 * $Id: ForeignKeyEditPolicy.java 3 2005-11-02 03:04:20Z csell $
 */
package com.byterefinery.rmbench.editpolicies;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.ConnectionEditPolicy;
import org.eclipse.gef.requests.GroupRequest;

import com.byterefinery.rmbench.editparts.ForeignKeyEditPart;
import com.byterefinery.rmbench.model.schema.ForeignKey;
import com.byterefinery.rmbench.operations.CommandAdapter;
import com.byterefinery.rmbench.operations.DeleteForeignKeyOperation;


public class ForeignKeyEditPolicy extends ConnectionEditPolicy {

	protected Command getDeleteCommand(GroupRequest deleteRequest) {
        ForeignKeyEditPart editPart = (ForeignKeyEditPart) getHost();
		return new CommandAdapter(new DeleteForeignKeyOperation((ForeignKey)editPart.getModel()));
	}
}
