package org.activityinfo.ui.client.component.importDialog.mapping;

import com.google.common.collect.Lists;
import org.activityinfo.core.shared.form.tree.FieldComponent;
import org.activityinfo.core.shared.form.tree.FieldPath;
import org.activityinfo.core.shared.form.tree.FormTree;
import org.activityinfo.core.shared.importing.model.ImportModel;
import org.activityinfo.i18n.shared.I18N;

import java.util.List;

/**
 * Manages the presentation of the field to the user
 */
public class FieldChoicePresenter {

    private final ImportModel model;

    public FieldChoicePresenter(ImportModel model) {
        this.model = model;
    }


    public List<FieldModel> getOptions() {

        List<FieldModel> options = Lists.newArrayList();

        // add existing fields
        collectFields(model.getFormTree().getRootFields(), options);

        return options;
    }

    private void collectFields(List<FormTree.Node> children, List<FieldModel> options) {
        for(FormTree.Node node : children) {
            switch(node.getFieldType()) {
                case REFERENCE:
                    collectFields(node.getChildren(), options);
                    break;
                case GEOGRAPHIC_POINT:
                    options.add(new FieldModel(I18N.CONSTANTS.latitude(),
                            node.getPath().component(FieldComponent.LATITUDE)));
                    options.add(new FieldModel(I18N.CONSTANTS.longitude(),
                            node.getPath().component(FieldComponent.LONGITUDE)));
                    break;
                default:
                    options.add(new FieldModel(label(node), node.getPath()));
                    break;
            }
        }
    }

    public String label(FieldPath path) {
        FormTree.Node node = model.getFormTree().getNodeByPath(path);
        return label(node);
    }

    private String label(FormTree.Node node) {
        if(node.getPath().isNested()) {
            return node.getParent().getField().getLabel().getValue() + " " +
                    node.getField().getLabel().getValue();
        } else {
            return node.getField().getLabel().getValue();
        }
    }
}
