package com.example.application.ui.presenter;

import com.example.application.data.services.SampleDataProvider;
import com.example.application.data.services.SampleStructureProvider;
import com.example.application.data.structureModel.finalStructure.Component;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;

import java.util.HashMap;
import java.util.Map;

@Controller
@UIScope
@Slf4j
public class TagPresenter {
    private final Map<String, MasterDetailPresenter> presenters;

    private final SampleStructureProvider sampleStructureProvider;
    private final SampleDataProvider sampleDataProvider;

    public TagPresenter(SampleStructureProvider sampleStructureProvider, SampleDataProvider sampleDataProvider){
        this.sampleStructureProvider = sampleStructureProvider;
        this.sampleDataProvider = sampleDataProvider;
        presenters = new HashMap<>();

        registerSamplePresenters();
    }

    private void registerSamplePresenters(){
        Component component = sampleStructureProvider.getSampleComponent();
        registerPresenter("Artifact",component); //Siehe MainLayout
        registerPresenter("Contract",component);
    }

    public void registerPresenter(String name, Component component){
        //TODO check if presenter name already exists
        MasterDetailPresenter masterDetailPresenter = new MasterDetailPresenter(sampleStructureProvider,sampleDataProvider,component);
        presenters.put(name,masterDetailPresenter);
    }

    public MasterDetailPresenter getPresenter(String name){
        //TODO catch not existing presenter
        return presenters.get(name);
    }
}
