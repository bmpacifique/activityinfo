package org.activityinfo.ui.client.component.table;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.RangeChangeEvent;
import org.activityinfo.core.client.InstanceQuery;
import org.activityinfo.core.client.ProjectionKeyProvider;
import org.activityinfo.core.client.ResourceLocator;
import org.activityinfo.core.shared.Projection;
import org.activityinfo.core.shared.criteria.Criteria;
import org.activityinfo.core.shared.criteria.CriteriaIntersection;
import org.activityinfo.core.shared.form.FormClass;
import org.activityinfo.core.shared.form.tree.FieldPath;
import org.activityinfo.ui.client.component.table.action.*;
import org.activityinfo.ui.client.component.table.filter.FilterCellAction;
import org.activityinfo.ui.client.component.table.filter.FilterHeader;
import org.activityinfo.ui.client.style.table.CellTableResources;
import org.activityinfo.ui.client.widget.CellTable;
import org.activityinfo.ui.client.widget.loading.LoadingState;
import org.activityinfo.ui.client.widget.loading.TableLoadingIndicator;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Reusable component to display Instances in a table
 */
public class InstanceTable implements IsWidget {

    private static final Logger LOGGER = Logger.getLogger(InstanceTable.class.getName());

    /**
     * The default column width, in {@code em}
     */
    public static final int COLUMN_WIDTH = 10;
    public static final int PAGE_SIZE = 100;

    private final ResourceLocator resourceLocator;

    private final CellTable<Projection> table;
    private final TableLoadingIndicator loadingIndicator;
    private final MultiSelectionModel<Projection> selectionModel = new MultiSelectionModel<>(new ProjectionKeyProvider());
    private final List<TableHeaderAction> headerActions;
    private final InstanceTableView tableView;

    private Set<FieldPath> fields = Sets.newHashSet();
    private Criteria criteria;
    private FormClass rootFormClass;

    public InstanceTable(InstanceTableView tableView) {
        this.tableView = tableView;
        this.resourceLocator = tableView.getResourceLocator();
        CellTableResources.INSTANCE.cellTableStyle().ensureInjected();

        final TableHeaderActionBrowserEventHandler headerActionEventHandler = new TableHeaderActionBrowserEventHandler(this);
        table = new CellTable<Projection>(PAGE_SIZE, CellTableResources.INSTANCE) {
            @Override
            protected void onBrowserEvent2(Event event) {
                super.onBrowserEvent2(event);
                headerActionEventHandler.onBrowserEvent(event);
            }
        };
        table.setSkipRowHoverCheck(true);
        table.setSkipRowHoverFloatElementCheck(true);
        table.setSkipRowHoverStyleUpdate(true);
        table.setHeaderBuilder(new InstanceTableHeaderBuilder(this));

        // Set the table to fixed width: we will provide explicit
        // column widths
        table.setWidth("100%", true);
        table.setSelectionModel(selectionModel);
        table.addRangeChangeHandler(new RangeChangeEvent.Handler() {
            @Override
            public void onRangeChange(RangeChangeEvent event) {
                table.redrawHeaders();
            }
        });

        // Create our loading indicator which can also show failure
        loadingIndicator = new TableLoadingIndicator();
        loadingIndicator.getRetryButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                reload();
            }
        });
        table.setLoadingIndicator(loadingIndicator.asWidget());

        headerActions = createHeaderActions();

        table.getEventBus().addHandler(CellTable.ScrollEvent.TYPE, new CellTable.ScrollHandler() {
            @Override
            public void onScroll(CellTable.ScrollEvent event) {
                handleScroll(event);
            }
        });
    }

    private void handleScroll(CellTable.ScrollEvent event) {
        // todo
    }

    private List<TableHeaderAction> createHeaderActions() {
        final List<TableHeaderAction> actions = new ArrayList<>();
        actions.add(new NewHeaderAction(this));
        actions.add(new DeleteHeaderAction(this));
        actions.add(new EditHeaderAction(this));
        actions.add(new ChooseColumnsHeaderAction(this));
        return actions;
    }

    public void setCriteria(Criteria criteria) {
        this.criteria = criteria;
    }

    public void setColumns(List<FieldColumn> columns) {
        removeAllColumns();
        for (FieldColumn column : columns) {
            final FilterCellAction filterAction = new FilterCellAction(this, column);
            table.addColumn(column, new FilterHeader(column, filterAction));
            fields.addAll(column.getFieldPaths());
        }

        reload();
    }

    private void removeAllColumns() {
        while (table.getColumnCount() > 0) {
            table.removeColumn(0);
        }
    }

    public void reload() {
        loadingIndicator.onLoadingStateChanged(LoadingState.LOADING, null);

        InstanceQuery query = new InstanceQuery(Lists.newArrayList(fields), buildQueryCriteria());
        resourceLocator.query(query).then(new AsyncCallback<List<Projection>>() {
            @Override
            public void onFailure(Throwable caught) {
                LOGGER.log(Level.SEVERE, "Failed to load instances. Criteria = " +
                        criteria + ", fields = " + fields, caught);
                loadingIndicator.onLoadingStateChanged(LoadingState.FAILED, caught);
            }

            @Override
            public void onSuccess(List<Projection> result) {
                table.setRowData(result);
            }
        });
    }

    private Criteria buildQueryCriteria() {
        // we want the intersection of the base (class) criteria and
        // each of the column filters: the rows that satisfy the class AND
        // the Col1 filter AND Col2 filter
        final List<Criteria> intersection = Lists.newArrayList(criteria);
        for (int i = 0; i < table.getColumnCount(); i++) {
            final FieldColumn column = (FieldColumn) table.getColumn(i);
            final Criteria columnCriteria = column.getCriteria();
            if (columnCriteria != null) {
                intersection.add(columnCriteria);
            }
        }
        return new CriteriaIntersection(intersection);
    }

    public MultiSelectionModel<Projection> getSelectionModel() {
        return selectionModel;
    }

    @Override
    public Widget asWidget() {
        return table;
    }

    public CellTable<Projection> getTable() {
        return table;
    }

    public ResourceLocator getResourceLocator() {
        return resourceLocator;
    }

    public void setRootFormClass(FormClass rootFormClass) {
        this.rootFormClass = rootFormClass;
    }

    public FormClass getRootFormClass() {
        return rootFormClass;
    }

    public List<TableHeaderAction> getHeaderActions() {
        return headerActions;
    }

    public InstanceTableView getTableView() {
        return tableView;
    }
}
