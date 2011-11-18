package org.sigmah.client.page.common;

import org.sigmah.client.i18n.I18N;
import org.sigmah.client.page.common.widget.MappingComboBox;
import org.sigmah.shared.dto.ReportSubscriptionDTO;
import org.sigmah.shared.report.model.layers.MapLayer;

import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.FormEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.ListField;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.extjs.gxt.ui.client.widget.form.TextField;

public class SubscribeForm extends FormPanel {

	public SubscribeForm(){
		
		
		createLayout();
		
	}
	
	public void createLayout(){
		
		setLabelWidth(110);
		
		final TextField<String> title = new TextField<String>();
		title.setFieldLabel(I18N.CONSTANTS.title());
		title.setAllowBlank(false);
		title.setName("title");
		add(title);
		
		Radio weekly = new Radio();
		weekly.setBoxLabel(I18N.CONSTANTS.weekly());
		weekly.setValue(true);
		Radio monthly = new Radio();
		monthly.setBoxLabel(I18N.CONSTANTS.monthly());
		
		final RadioGroup emailFrequency = new RadioGroup();
		emailFrequency.setFieldLabel(I18N.CONSTANTS.emailFrequency());
		emailFrequency.setOrientation(Orientation.VERTICAL);
		emailFrequency.add(weekly);
		emailFrequency.add(monthly);
		
		
		add(emailFrequency);
		
		final MappingComboBox dayOfWeek = new MappingComboBox();
		dayOfWeek.setAllowBlank(false);
		dayOfWeek.setFieldLabel(I18N.CONSTANTS.dayOfWeek());
		dayOfWeek.add("mon", "Monday");
		dayOfWeek.add("tue", "Tuesday");
		dayOfWeek.add("wed", "Wednesday");
		dayOfWeek.add("thr", "Thursday");
		dayOfWeek.add("fri", "Friday");
		dayOfWeek.add("sat", "Saturday");
		dayOfWeek.add("sun", "Sunday");
		add(dayOfWeek);
		
		final MappingComboBox dayOfMonth = new MappingComboBox();
		dayOfMonth.setAllowBlank(false);
		dayOfMonth.hide();
		dayOfMonth.setFieldLabel(I18N.CONSTANTS.dayOfMonth());
		for(int i=1; i<=31; i++){
			dayOfMonth.add(String.valueOf(i), String.valueOf(i));
		}
		add(dayOfMonth);
		
		emailFrequency.addListener(Events.Change, new Listener<BaseEvent>(){
		       public void handleEvent(BaseEvent be) {
		           if(emailFrequency.getValue().getBoxLabel() == I18N.CONSTANTS.weekly()){
		        	   dayOfMonth.hide();
		        	   dayOfWeek.show();
		        	 
		           }  else if (emailFrequency.getValue().getBoxLabel() == I18N.CONSTANTS.monthly()) {
		        	   dayOfWeek.hide();
		        	   dayOfMonth.show();
		        	   
		           }
		        }
		});
		ListField<ReportSubscriptionDTO> subscribers = new ListField<ReportSubscriptionDTO>();
//		subscribers.a
//		subscribers.setFieldLabel(I18N.CONSTANTS.subscribers());
		
		ListStore<ReportSubscriptionDTO> store = new ListStore<ReportSubscriptionDTO>();
		//store.add(ReportSubscriptionDTO)
		add(subscribers);

		newEmailPanel();
		
	}
	
	public void newEmailPanel(){
		
		HorizontalPanel hr = new HorizontalPanel();
		
		TextField<String> newEmail = new TextField<String>();
		newEmail.setValue(I18N.CONSTANTS.enterNewEmail());
		hr.add(newEmail);
		
		Button addEmail = new Button();
		addEmail.setText(I18N.CONSTANTS.add());
		hr.add(addEmail);
		
		Button removeEmail = new Button();
		removeEmail.setText(I18N.CONSTANTS.remove());
		hr.add(removeEmail);
		
		add(hr);
	}
	
}
