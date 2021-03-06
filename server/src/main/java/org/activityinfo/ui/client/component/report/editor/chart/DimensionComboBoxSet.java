package org.activityinfo.ui.client.component.report.editor.chart;

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

import com.extjs.gxt.ui.client.data.BaseListLoader;
import com.extjs.gxt.ui.client.data.ListLoadResult;
import com.extjs.gxt.ui.client.data.ListLoader;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.StoreEvent;
import com.extjs.gxt.ui.client.store.StoreListener;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.toolbar.LabelToolItem;
import org.activityinfo.i18n.shared.I18N;
import org.activityinfo.legacy.client.Dispatcher;
import org.activityinfo.legacy.shared.reports.model.Dimension;
import org.activityinfo.legacy.shared.reports.model.PivotChartReportElement;
import org.activityinfo.legacy.shared.reports.model.PivotChartReportElement.Type;
import org.activityinfo.ui.client.EventBus;
import org.activityinfo.ui.client.component.report.editor.pivotTable.DimensionModel;
import org.activityinfo.ui.client.page.report.HasReportElement;
import org.activityinfo.ui.client.page.report.ReportChangeHandler;
import org.activityinfo.ui.client.page.report.ReportEventBus;

import java.util.List;

public class DimensionComboBoxSet implements HasReportElement<PivotChartReportElement> {

    private final ReportEventBus reportEventBus;
    private final DimensionProxy proxy;
    private final ListStore<DimensionModel> store;
    private final ListLoader<ListLoadResult<DimensionModel>> loader;
    private final LabelToolItem categoryLabel;
    private final LabelToolItem seriesLabel;
    private final DimensionCombo categoryCombo;
    private final DimensionCombo seriesCombo;

    private PivotChartReportElement model;
    private boolean fireEvents = true;

    public DimensionComboBoxSet(EventBus eventBus, Dispatcher dispatcher) {
        this.proxy = new DimensionProxy(dispatcher);
        this.loader = new BaseListLoader<ListLoadResult<DimensionModel>>(proxy);
        this.store = new ListStore<DimensionModel>(loader);
        this.categoryCombo = new DimensionCombo(store, new SelectionChangedListener<DimensionModel>() {

            @Override
            public void selectionChanged(SelectionChangedEvent<DimensionModel> se) {
                model.setCategoryDimension(se.getSelectedItem().getDimension());
                if (fireEvents) {
                    DimensionComboBoxSet.this.reportEventBus.fireChange();
                    DimensionComboBoxSet.this.categoryCombo.setValue(se.getSelectedItem().getDimension());
                }
            }
        });
        this.seriesCombo = new DimensionCombo(store, new SelectionChangedListener<DimensionModel>() {

            @Override
            public void selectionChanged(SelectionChangedEvent<DimensionModel> se) {
                model.setSeriesDimension(se.getSelectedItem().getDimension());
                if (fireEvents) {
                    DimensionComboBoxSet.this.reportEventBus.fireChange();
                    DimensionComboBoxSet.this.seriesCombo.setValue(se.getSelectedItem().getDimension());
                }
            }
        });
        this.categoryLabel = new LabelToolItem();
        this.seriesLabel = new LabelToolItem();
        this.reportEventBus = new ReportEventBus(eventBus, this);
        this.reportEventBus.listen(new ReportChangeHandler() {

            @Override
            public void onChanged() {
                updateSelectionWithoutEvents();
            }
        });
    }

    @Override
    public void bind(PivotChartReportElement model) {
        this.model = model;
        this.proxy.setModel(model);
        updateSelectionWithoutEvents();
    }

    @Override
    public PivotChartReportElement getModel() {
        return this.model;
    }

    @Override
    public void disconnect() {
        reportEventBus.disconnect();
    }

    private void updateSelectionWithoutEvents() {
        fireEvents = false;
        updateSelection();
        fireEvents = true;
    }

    private void updateSelection() {
        loader.load();
        seriesCombo.setValue(firstOrNull(model.getSeriesDimensions()));
        categoryCombo.setValue(firstOrNull(model.getCategoryDimensions()));
        updateLabels();
    }

    private void updateLabels() {
        Type type = model.getType();
        if (type == Type.ClusteredBar || type == Type.Bar) {
            categoryLabel.setText(I18N.CONSTANTS.horizontalAxis());
            seriesLabel.setText(I18N.CONSTANTS.bars());
        } else if (type == Type.Line) {
            categoryLabel.setText(I18N.CONSTANTS.horizontalAxis());
            seriesLabel.setText(I18N.CONSTANTS.lines());
        } else if (type == Type.Pie) {
            categoryLabel.setText(I18N.CONSTANTS.slices());
        }
        seriesCombo.setEnabled(type != Type.Pie);
        seriesCombo.setEnabled(type != Type.Pie);
    }

    private Dimension firstOrNull(List<Dimension> dimensions) {
        if (dimensions.isEmpty()) {
            return null;
        } else {
            return dimensions.get(0);
        }
    }

    public LabelToolItem getCategoryLabel() {
        return categoryLabel;
    }

    public LabelToolItem getSeriesLabel() {
        return seriesLabel;
    }

    public DimensionCombo getCategoryCombo() {
        return categoryCombo;
    }

    public DimensionCombo getSeriesCombo() {
        return seriesCombo;
    }

    private static class DimensionCombo extends ComboBox<DimensionModel> {
        private Dimension dimension;

        public DimensionCombo(ListStore<DimensionModel> store,
                              SelectionChangedListener<DimensionModel> changeListener) {
            this.store = store;
            setDisplayField("name");
            setEditable(false);
            setForceSelection(true);
            setTriggerAction(TriggerAction.ALL);
            addSelectionChangedListener(changeListener);

            store.addStoreListener(new StoreListener<DimensionModel>() {

                @Override
                public void storeDataChanged(StoreEvent<DimensionModel> se) {
                    tryUpdateSelection();
                }
            });
        }

        public void setValue(Dimension dimension) {
            this.dimension = dimension;
            tryUpdateSelection();
        }

        public void tryUpdateSelection() {
            // / try to update the combo box selection based on the current
            // value
            // / we may need to wait until we receive the list of dimensions
            // from the server
            // / because the model carries only the id, not the descriptive
            // label
            for (DimensionModel model : getStore().getModels()) {
                if (model.getDimension().equals(dimension)) {
                    setValue(model);
                    return;
                }
            }
            setValue((DimensionModel) null);
        }
    }

}
