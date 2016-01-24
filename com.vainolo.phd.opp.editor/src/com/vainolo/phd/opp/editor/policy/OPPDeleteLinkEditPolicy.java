/*******************************************************************************
 * Copyright (c) 2015 Arieh "Vainolo" Bibliowicz and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package com.vainolo.phd.opp.editor.policy;

import org.eclipse.gef.editpolicies.ConnectionEditPolicy;
import org.eclipse.gef.requests.GroupRequest;

import com.vainolo.phd.opp.model.OPPLink;
import com.vainolo.phd.opp.editor.command.OPPLinkDeleteCommand;

public class OPPDeleteLinkEditPolicy extends ConnectionEditPolicy {

  @Override
  protected OPPLinkDeleteCommand getDeleteCommand(GroupRequest request) {
    OPPLinkDeleteCommand command = new OPPLinkDeleteCommand();
    command.setLink((OPPLink) getHost().getModel());
    return command;
  }
}