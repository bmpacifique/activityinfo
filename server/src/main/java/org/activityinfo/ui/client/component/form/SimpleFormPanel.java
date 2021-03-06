package org.activityinfo.ui.client.component.form;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;
import org.activityinfo.core.client.ResourceLocator;
import org.activityinfo.i18n.shared.I18N;
import org.activityinfo.model.date.DateRange;
import org.activityinfo.model.form.*;
import org.activityinfo.model.legacy.BuiltinFields;
import org.activityinfo.model.lock.LockEvaluator;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.type.FieldValue;
import org.activityinfo.model.type.ReferenceValue;
import org.activityinfo.model.type.attachment.AttachmentValue;
import org.activityinfo.model.type.enumerated.EnumValue;
import org.activityinfo.promise.Promise;
import org.activityinfo.ui.client.component.form.event.FieldMessageEvent;
import org.activityinfo.ui.client.component.form.field.FormFieldWidget;
import org.activityinfo.ui.client.component.form.field.FormFieldWidgetFactory;
import org.activityinfo.ui.client.component.form.field.NullFieldWidget;
import org.activityinfo.ui.client.widget.DisplayWidget;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Displays a simple view of the form, where users can edit instances
 */
public class SimpleFormPanel implements DisplayWidget<FormInstance> {

    private final FieldContainerFactory containerFactory;
    private final FormFieldWidgetFactory widgetFactory;

    private final FlowPanel panel;
    private final ScrollPanel scrollPanel;
    private final boolean withScroll;
    private final EventBus eventBus = new SimpleEventBus();

    private final Map<ResourceId, FieldContainer> containers = Maps.newHashMap();

    /**
     * The original, unmodified instance
     */
    private Resource instance;

    /**
     * A new version of the instance, being updated by the user
     */
    private FormInstance workingInstance;

    private FormClass formClass;
    private ResourceLocator locator;
    private final RelevanceHandler relevanceHandler;
    
    private boolean headingVisible = false;

    // validation form class is used to refer to "top-level" form class.
    // For example "Properties panel" renders current type-formClass but in order to validate expression we need
    // reference to formClass that is currently editing on FormDesigner.
    // it can be null.
    private FormClass validationFormClass = null;

    public SimpleFormPanel(ResourceLocator locator, FieldContainerFactory containerFactory,
                           FormFieldWidgetFactory widgetFactory) {
        this(locator, containerFactory, widgetFactory, true);
    }

    public SimpleFormPanel(ResourceLocator locator, FieldContainerFactory containerFactory,
                           FormFieldWidgetFactory widgetFactory, boolean withScroll) {
        FormPanelStyles.INSTANCE.ensureInjected();

        this.locator = locator;
        this.containerFactory = containerFactory;
        this.widgetFactory = widgetFactory;
        this.withScroll = withScroll;
        this.relevanceHandler = new RelevanceHandler(this);

        panel = new FlowPanel();
        panel.setStyleName(FormPanelStyles.INSTANCE.formPanel());
        scrollPanel = new ScrollPanel(panel);

        bindEvents();
    }

    public boolean isHeadingVisible() {
        return headingVisible;
    }

    public void setHeadingVisible(boolean headingVisible) {
        this.headingVisible = headingVisible;
    }

    private void bindEvents() {
        eventBus.addHandler(FieldMessageEvent.TYPE, new FieldMessageEvent.Handler() {
            @Override
            public void handle(FieldMessageEvent event) {
                showFieldMessage(event);
            }
        });
    }

    private void showFieldMessage(FieldMessageEvent event) {
        FieldContainer container = containers.get(event.getFieldId());
        if (event.isClearMessage()) {
            container.setValid();
        } else {
            container.setInvalid(event.getMessage());
        }
    }

    public FormInstance getInstance() {
        return workingInstance;
    }

    @Override
    public Promise<Void> show(final FormInstance instance) {
        return show(instance.asResource());
    }

    public Promise<Void> show(final Resource instance) {
        this.instance = instance;
        return locator.getFormClass(instance.getResourceId("classId")).join(new Function<FormClass, Promise<Void>>() {
            @Nullable
            @Override
            public Promise<Void> apply(@Nullable FormClass formClass) {
                return buildForm(formClass);
            }
        }).join(new Function<Void, Promise<Void>>() {
            @Override
            public Promise<Void> apply(@Nullable Void input) {
                return setValue(instance);
            }
        });
    }

    private Promise<Void> buildForm(final FormClass formClass) {
        this.formClass = formClass;
        this.relevanceHandler.formClassChanged();
        
        if(headingVisible) {
            FormHeading heading = new FormHeading();
            heading.setFormClass(formClass);
            panel.add(heading);
        }

        try {
            return createWidgets().then(new Function<Void, Void>() {
                @Nullable
                @Override
                public Void apply(@Nullable Void input) {
                    addFormElements(formClass, 0);
                    return null;
                }
            });

        } catch (Throwable caught) {
            return Promise.rejected(caught);
        }
    }

    private Promise<Void> createWidgets() {
        return Promise.forEach(formClass.getFields(), new Function<FormField, Promise<Void>>() {
            @Override
            public Promise<Void> apply(final FormField field) {
                return widgetFactory.createWidget(formClass, field, new ValueUpdater<FieldValue>() {
                    @Override
                    public void update(FieldValue value) {
                        onFieldUpdated(field, value);
                    }
                }, validationFormClass, eventBus).then(new Function<FormFieldWidget, Void>() {
                    @Override
                    public Void apply(@Nullable FormFieldWidget widget) {
                        containers.put(field.getId(), containerFactory.createContainer(field, widget, 4));
                        return null;
                    }
                });
            }
        });
    }

    public Promise<Void> setValue(Resource instance) {
        this.instance = instance;
        this.workingInstance = FormInstance.fromResource(instance);

        List<Promise<Void>> tasks = Lists.newArrayList();

        for (FieldContainer container : containers.values()) {
            FormField field = container.getField();
            FieldValue value = workingInstance.get(field.getId(), field.getType());

            if(value != null && value.getTypeClass() == field.getType().getTypeClass()) {
                tasks.add(container.getFieldWidget().setValue(value));
            } else {
                container.getFieldWidget().clearValue();
            }
            container.setValid();
        }

        return Promise.waitAll(tasks).then(new Function<Void, Void>() {
            @Nullable
            @Override
            public Void apply(@Nullable Void input) {
                relevanceHandler.onValueChange(); // invoke relevance handler once values are set
                return null;
            }
        });
    }

    private void addFormElements(FormElementContainer container, int depth) {
        for (FormElement element : container.getElements()) {
            if (element instanceof FormSection) {
                panel.add(createHeader(depth, ((FormSection) element)));
                addFormElements((FormElementContainer) element, depth + 1);
            } else if (element instanceof FormField) {
                FormField formField = (FormField) element;
                if (formField.isVisible()) {
                    FieldContainer fieldContainer = containers.get(formField.getId());
                    if (!(fieldContainer.getFieldWidget() instanceof NullFieldWidget)) {
                        panel.add(fieldContainer);
                    }
                }
            }
        }
    }

    public void onFieldUpdated(FormField field, FieldValue newValue) {
        if (!Objects.equals(workingInstance.get(field.getId()), newValue)) {
            workingInstance.set(field.getId(), newValue);
            relevanceHandler.onValueChange(); // skip handler must be applied after workingInstance is updated
        }
        validateField(containers.get(field.getId()));
    }

    private boolean validateField(FieldContainer container) {
        FormField field = container.getField();
        FieldValue value = getCurrentValue(field);
        if (value != null && value.getTypeClass() != field.getType().getTypeClass()) {
            value = null;
        }

        if (!container.getFieldWidget().isValid()) {
            return false;
        }

        Optional<Boolean> validatedBuiltInDates = validateBuiltinDates(container, field);
        if (validatedBuiltInDates.isPresent()) {
            return validatedBuiltInDates.get();
        }

        if (field.isRequired() && isEmpty(value) && field.isVisible() && !container.getFieldWidget().isReadOnly()) { // if field is not visible user doesn't have chance to fix it
            container.setInvalid(I18N.CONSTANTS.requiredFieldMessage());
            return false;
        } else {
            container.setValid();
            return true;
        }
    }

    private Optional<Boolean> validateBuiltinDates(FieldContainer container, FormField field) {
        if (BuiltinFields.isBuiltInDate(field.getId())) {
            DateRange dateRange = BuiltinFields.getDateRange(workingInstance, formClass);

            if (!formClass.getLocks().isEmpty()) {
                if (new LockEvaluator(formClass).isLockedSilently(workingInstance)) {
                    getFieldContainer(BuiltinFields.getStartDateField(formClass).getId()).setInvalid(I18N.CONSTANTS.siteIsLocked());
                    getFieldContainer(BuiltinFields.getEndDateField(formClass).getId()).setInvalid(I18N.CONSTANTS.siteIsLocked());
                    return Optional.of(false);
                }
            }

            if (!dateRange.isValidWithNull()) {
                container.setInvalid(I18N.CONSTANTS.inconsistentDateRangeWarning());
                return Optional.of(false);
            } else {
                if (dateRange.isValid()) {
                    getFieldContainer(BuiltinFields.getStartDateField(formClass).getId()).setValid();
                    getFieldContainer(BuiltinFields.getEndDateField(formClass).getId()).setValid();
                    return Optional.of(true);
                }
            }
        }
        return Optional.absent();
    }

    private boolean isEmpty(FieldValue value) {
        return value == null ||
                (value instanceof EnumValue && ((EnumValue) value).getResourceIds().isEmpty()) ||
                (value instanceof ReferenceValue && ((ReferenceValue) value).getResourceIds().isEmpty()) ||
                (value instanceof AttachmentValue && ((AttachmentValue) value).getValues().isEmpty());
    }

    public boolean validate() {
        boolean valid = true;
        for (FieldContainer container : this.containers.values()) {
            if (!validateField(container)) {
                valid = false;
            }
        }
        return valid;
    }

    private FieldValue getCurrentValue(FormField field) {
        return workingInstance.get(field.getId());
    }

    private Widget createHeader(int depth, FormSection section) {
        String hn = "h" + (3 + depth);
        return new HTML("<" + hn + ">" + SafeHtmlUtils.htmlEscape(section.getLabel()) + "</" + hn + ">");
    }

    @Override
    public Widget asWidget() {
        return withScroll ? scrollPanel : panel;
    }

    public FormClass getFormClass() {
        return formClass;
    }

    public FieldContainer getFieldContainer(ResourceId fieldId) {
        return containers.get(fieldId);
    }

    public ResourceLocator getLocator() {
        return locator;
    }

    public Map<ResourceId, FieldContainer> getContainers() {
        return containers;
    }

    public void setValidationFormClass(FormClass validationFormClass) {
        this.validationFormClass = validationFormClass;
    }

    public FormClass getValidationFormClass() {
        return validationFormClass;
    }

    public RelevanceHandler getRelevanceHandler() {
        return relevanceHandler;
    }
}
