package org.activityinfo.ui.client;

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

import com.extjs.gxt.ui.client.event.Listener;
import com.google.gwt.core.client.JavaScriptException;
import com.google.gwt.gears.client.Factory;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.activityinfo.legacy.client.DispatchEventSource;
import org.activityinfo.legacy.client.remote.cache.DefaultDispatchListener;
import org.activityinfo.legacy.shared.Log;
import org.activityinfo.legacy.shared.command.CreateEntity;
import org.activityinfo.legacy.shared.command.Delete;
import org.activityinfo.legacy.shared.command.UpdateEntity;
import org.activityinfo.legacy.shared.command.result.CommandResult;
import org.activityinfo.ui.client.page.NavigationEvent;
import org.activityinfo.ui.client.page.NavigationHandler;

/**
 * Tracks usage of the application and reports to Google Analytics
 *
 * @author Alex Bertram
 */
@Singleton
public class UsageTracker {

    private static final int VISITOR_SCOPE = 1;
    private static final int SESSION_SCOPE = 2;
    private static final int PAGE_SCOPE = 3;

    @Inject
    public UsageTracker(EventBus eventBus,
                        DispatchEventSource commandEventSource) {

        // Note whether this user has gears installed
        setCustomVar(1, "gears", isGearsInstalled() ? "installed"
                : "not installed", VISITOR_SCOPE);

        // Track internal page movements
        eventBus.addListener(NavigationHandler.NAVIGATION_AGREED,
                new Listener<NavigationEvent>() {
                    @Override
                    public void handleEvent(NavigationEvent event) {
                        trackPageView(event.getPlace().getPageId().toString());
                    }
                });

        // Track successful creates by user
        commandEventSource.registerListener(CreateEntity.class,
                new DefaultDispatchListener<CreateEntity>() {
                    @Override
                    public void onSuccess(CreateEntity command, CommandResult result) {
                        trackPageView("/crud/" + command.getEntityName()
                                + "/create");
                    }
                });
        // Track successful updates by user
        commandEventSource.registerListener(UpdateEntity.class,
                new DefaultDispatchListener<UpdateEntity>() {
                    @Override
                    public void onSuccess(UpdateEntity command, CommandResult result) {
                        trackPageView("/crud/" + command.getEntityName()
                                + "/update");
                    }
                });
        // Track successful deletes by user
        commandEventSource.registerListener(Delete.class,
                new DefaultDispatchListener<Delete>() {
                    @Override
                    public void onSuccess(Delete command, CommandResult result) {
                        trackPageView("/crud/" + command.getEntityName()
                                + "/delete");
                    }
                });
    }

    private boolean isGearsInstalled() {
        return Factory.getInstance() != null;
    }

    private void trackPageView(String pageName) {
        Log.trace("Pageview tracked: " + pageName);
        try {
            doTrackPageView(pageName);
        } catch (JavaScriptException e) {
            Log.error("pageTracker.trackPageview() threw exception", e);
        }
    }

    private native void doTrackPageView(String pageName) /*-{
        $wnd._gaq.push([ '_trackPageview', pageName ]);
    }-*/;

    private void setCustomVar(int slot, String variableName, String value,
                              int scope) {
        try {
            doSetCustomVar(slot, variableName, value, scope);
        } catch (JavaScriptException e) {
            Log.error("pageTracker.setCustomVar() threw exception", e);
        }
    }

    private native void doSetCustomVar(int slot, String variableName,
                                       String value, int scope) /*-{
        $wnd._gaq.push([ '_setCustomVar', slot, variableName, value, scope ]);
    }-*/;
}
