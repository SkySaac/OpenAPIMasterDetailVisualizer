package openapivisualizer.application.ui.components.detaillayout;

import openapivisualizer.application.ui.components.detaillayout.detailcomponents.DetailComponent;
import openapivisualizer.application.ui.components.detaillayout.detailcomponents.TextComponent;

public interface DetailSwitchListener extends TextComponent.UrlNavigationListener {
    void switchToObject(DetailComponent source, String title, DetailComponent target);

    void switchToArray(DetailComponent source, String title, DetailComponent target);

}
