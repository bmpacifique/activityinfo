package org.activityinfo.server.mail;

/*
 * #%L
 * ActivityInfo Server
 * %%
 * Copyright (C) 2009 - 2013 UNICEF
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import org.activityinfo.server.database.hibernate.entity.Domain;
import org.activityinfo.server.database.hibernate.entity.User;

public class InvitationMessage extends MessageModel {
    private User newUser;
    private User invitingUser;
    private Domain domain;

    public InvitationMessage(User newUser, User invitingUser, Domain domain) {
        this.newUser = newUser;
        this.invitingUser = invitingUser;
        this.domain = domain;
    }

    public User getNewUser() {
        return newUser;
    }

    public User getInvitingUser() {
        return invitingUser;
    }

    @Override
    public User getRecipient() {
        return newUser;
    }

    public Domain getDomain() {
        return domain;
    }

    public void setDomain(Domain domain) {
        this.domain = domain;
    }

    public String getHost() {
        return domain.getHost();
    }
}
