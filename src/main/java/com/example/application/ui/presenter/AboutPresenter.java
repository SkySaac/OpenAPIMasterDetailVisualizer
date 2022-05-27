package com.example.application.ui.presenter;

import com.example.application.data.services.SampleStructureProvider;
import com.example.application.data.services.Deprecated.StructureProvider;
import com.example.application.data.structureModel.OpenApi;
import com.example.application.ui.view.AboutView;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;

import java.io.IOException;

@Controller
@UIScope
@Slf4j
public class AboutPresenter implements AboutView.ActionListener {

    private AboutView view;

    public AboutView getView() {
        view = new AboutView(this);
        return view;
    }

    @Override
    public void action() {
        OpenApi openAPI = null;
        try {
            openAPI = StructureProvider.createTreeStructure(SampleStructureProvider.getSampleStructure());
        }catch(IOException e){
            System.out.println("Erroooorr, parseerror"); //TODO
            e.printStackTrace();
        }finally {
            if(openAPI != null)
                System.out.println(openAPI.getInfo().getTitle() + " has been found");

        }
    }
}
