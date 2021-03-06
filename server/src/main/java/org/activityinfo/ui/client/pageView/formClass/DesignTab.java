package org.activityinfo.ui.client.pageView.formClass;

import com.google.common.base.Function;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import org.activityinfo.core.client.ResourceLocator;
import org.activityinfo.model.form.FormInstance;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.promise.Promise;
import org.activityinfo.ui.client.component.formdesigner.FormDesignerPanel;
import org.activityinfo.ui.client.widget.DisplayWidget;

import javax.annotation.Nullable;

/** *
 * Created by Mithun on 4/3/2014.
 */
public class DesignTab implements DisplayWidget<FormInstance> {

    private ResourceLocator resourceLocator;
    private FlowPanel panel;

    public DesignTab(ResourceLocator resourceLocator) {
        this.resourceLocator = resourceLocator;
        this.panel = new FlowPanel();
    }

    @Override
    public Promise<Void> show(FormInstance value) {
        return this.resourceLocator.getFormClass(value.getId())
                .then(new Function<FormClass, Void>() {
                    @Nullable
                    @Override
                    public Void apply(FormClass formClass) {
                        if (formClass.hasSubformField()) {
                            panel.add(new NotSupportedFormClassPanel());
                        } else {
                            panel.add(new FormDesignerPanel(resourceLocator, formClass));
                        }
                        return null;
                    }
                });
    }

    @Override
    public Widget asWidget() {
        return panel;
    }
}
