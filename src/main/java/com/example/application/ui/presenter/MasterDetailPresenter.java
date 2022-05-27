package com.example.application.ui.presenter;

import com.example.application.data.services.SampleDataProvider;
import com.example.application.data.services.SampleStructureProvider;
import com.example.application.ui.view.MasterDetailView;
import com.vaadin.flow.component.Component;
import lombok.Getter;

public class MasterDetailPresenter {

    private MasterDetailView view;
    private final SampleStructureProvider sampleStructureProvider;
    private final SampleDataProvider sampleDataProvider;
    @Getter
    public com.example.application.data.structureModel.finalStructure.Component component;

    public MasterDetailPresenter(SampleStructureProvider sampleStructureProvider, SampleDataProvider sampleDataProvider, com.example.application.data.structureModel.finalStructure.Component component) {
        this.sampleStructureProvider = sampleStructureProvider;
        this.sampleDataProvider = sampleDataProvider;
        this.component = component;
    }

    public Component getView() {
        view = new MasterDetailView(component);

        view.setData(sampleDataProvider.getSampleData());
        return view;
    }

}
