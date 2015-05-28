package org.activityinfo.geoadmin.merge2.view.swing;

import org.activityinfo.geoadmin.merge2.model.ImportModel;
import org.activityinfo.geoadmin.merge2.view.ImportView;
import org.activityinfo.store.ResourceStore;
import org.activityinfo.store.ResourceStoreImpl;
import org.activityinfo.geoadmin.merge2.view.swing.merge.MergeStep;
import org.activityinfo.geoadmin.model.ActivityInfoClient;
import org.activityinfo.model.legacy.CuidAdapter;
import org.activityinfo.model.resource.ResourceId;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;


public class MergeDialog extends JFrame {

    private List stepList;
    private JPanel stepPanel;
    
    private java.util.List<Step> steps;

    public MergeDialog(ImportView viewModel) {
        super("Merge");
        setSize(650, 350);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        steps = new ArrayList<>();
        steps.add(new MatchColumnStep(viewModel));
        steps.add(new MergeStep(viewModel));
        
        stepPanel = new JPanel(new BorderLayout());
        
        stepList = new List();
        stepList.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                stepPanel.removeAll();
                stepPanel.add(steps.get(stepList.getSelectedIndex()).createView(), BorderLayout.CENTER);
                stepPanel.validate();
            }
        });
        
        updateStepList();
        
        JButton okButton = new JButton("Merge");
        JButton cancelButton = new JButton("Cancel");
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        buttonPanel.add(cancelButton);
        buttonPanel.add(okButton);

        getContentPane().add(stepList, BorderLayout.WEST);
        getContentPane().add(stepPanel, BorderLayout.CENTER);
        getContentPane().add(buttonPanel, BorderLayout.PAGE_END);
    }

    private void updateStepList() {
        stepList.removeAll();
        for(Step step : steps) {
            stepList.add(step.getLabel());
        }
    }

    public static void main(String[] args) {

        ActivityInfoClient client = new ActivityInfoClient("http://localhost:8898/resources", "akbertram@gmail.com", "notasecret");
        ResourceStore resourceStore = new ResourceStoreImpl(client);
        
        ImportModel modelStore = new ImportModel(resourceStore, 
                ResourceId.valueOf("file:///home/alex/dev/activityinfo-beta/geoadmin/src/test/resources/mg/communes.shp"),
                CuidAdapter.adminLevelFormClass(1511));
        
        ImportView viewModel = new ImportView(resourceStore, modelStore);
        
        MergeDialog dialog = new MergeDialog(viewModel);
        dialog.setVisible(true);
        
    }
    
}