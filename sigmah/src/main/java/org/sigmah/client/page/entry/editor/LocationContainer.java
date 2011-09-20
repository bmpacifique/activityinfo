package org.sigmah.client.page.entry.editor;

import java.util.Map;

import org.sigmah.client.EventBus;
import org.sigmah.client.dispatch.Dispatcher;
import org.sigmah.shared.dto.ActivityDTO;
import org.sigmah.shared.dto.SiteDTO;
import org.sigmah.shared.util.mapping.BoundingBoxDTO;

import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout.HBoxLayoutAlign;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayoutData;

public class LocationContainer extends LayoutContainer {

	private ActivityDTO currentActivity;
	private LocationListView locations;
	private MapFieldSet map;
	private Dispatcher service;
	private EventBus eventBus;
	private MapPresenter mapPresenter;
	private AdminFieldSetPresenter adminPresenter;
	private SiteDTO site;

	public LocationContainer(Dispatcher service, EventBus eventBus, ActivityDTO activity) {
		this.service=service;
		this.eventBus=eventBus;
		this.currentActivity=activity;
		
		initializeComponent();
	}

	private void initializeComponent() {
		HBoxLayout layout = new HBoxLayout();
		layout.setHBoxLayoutAlign(HBoxLayoutAlign.STRETCH);
		setLayout(layout);
		HBoxLayoutData dataAdminFields = new HBoxLayoutData();
		
		AdminFieldSet adminFieldSet = new AdminFieldSet(currentActivity);
		add(adminFieldSet, dataAdminFields);
		
		createLocationList();
		add(locations);
		
		createMap();
		HBoxLayoutData dataMap = new HBoxLayoutData();
		dataMap.setFlex(1.0);
		add(map);
		
		adminPresenter = new AdminFieldSetPresenter(service, currentActivity, adminFieldSet);
		mapPresenter = new MapPresenter(map);
        adminPresenter.setListener(new AdminFieldSetPresenter.Listener() {
            @Override
            public void onBoundsChanged(String name, BoundingBoxDTO bounds) {
                mapPresenter.setBounds(name, bounds);
            }

            @Override
            public void onModified() {
            }
        });
	}

	private void createMap() {
		map = new MapFieldSet(currentActivity.getDatabase().getCountry());
	}

	private void createLocationList() {
		locations = new LocationListView();
	}

	public Map<String, Object> getChanges() {
		return adminPresenter.getChangeMap();
	}

	public boolean isDirty() {
		return adminPresenter.isDirty();
	}

	public Map<String, Object> getAllValues() {
		return adminPresenter.getChangeMap();
	}

	public void setSite(SiteDTO site) {
		this.site=site;
		adminPresenter.setSite(site);
	}
	
}
