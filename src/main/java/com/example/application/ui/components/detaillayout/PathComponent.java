package com.example.application.ui.components.detaillayout;

import com.example.application.ui.components.detaillayout.detailcomponents.DetailComponent;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class PathComponent extends HorizontalLayout{

    public interface PathSwitchListener {
        void switchView(DetailComponent detailComponent);
    }

    private final HorizontalLayout currentPathLayout = new HorizontalLayout(); //TODO use sorted map
    private final List<Div> pathElementOrderList = new ArrayList<>();
    //List to get the index of the item from when switching
    private final List<DetailComponent> pathComponentOrderList = new ArrayList<>();

    private final PathSwitchListener pathSwitchListener;

    private final DetailComponent baseComponent;

    public PathComponent(PathSwitchListener pathSwitchListener, String basePathName, DetailComponent basePathComponent){
        this.pathSwitchListener = pathSwitchListener;
        this.baseComponent = basePathComponent;

        Label startLabel = new Label(basePathName);
        Div startListenerDiv = new Div(startLabel);
        startListenerDiv.addClickListener(e-> this.switchView(basePathComponent));
        add(startListenerDiv);
        add(currentPathLayout);
    }

    public void createPathElement(String schemaName, DetailComponent detailComponent) {
        Label startLabel = new Label(schemaName);
        Div listenerDiv = new Div(startLabel);
        listenerDiv.addClickListener(e-> this.switchView(detailComponent));

        pathElementOrderList.add(listenerDiv);
        pathComponentOrderList.add(detailComponent);
        currentPathLayout.add(listenerDiv);
    }

    public void clearPathElements(){
        currentPathLayout.removeAll();
        pathElementOrderList.clear();
        pathComponentOrderList.clear();
    }

    private void switchView(DetailComponent target){

        //collect all detail components that are after our component
        int targetIndex = pathComponentOrderList.indexOf(target);

        if(targetIndex == -1 && target != baseComponent) {
            log.error("TargetIndex not found while switching in the detailview"); //TODO besser schreiben
            return;
        }
        for(int i = pathElementOrderList.size()-1; i > targetIndex; i--){
            currentPathLayout.remove(pathElementOrderList.get(i));
            pathElementOrderList.remove(pathElementOrderList.get(i));
            pathComponentOrderList.remove(i);
        }

        pathSwitchListener.switchView(target);
    }
}
