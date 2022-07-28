package com.example.application.ui.components.detaillayout;

import com.example.application.ui.components.detaillayout.detailcomponents.DetailComponent;
import com.example.application.ui.components.detaillayout.detailcomponents.TextComponent;

public interface DetailSwitchListener extends TextComponent.UrlNavigationListener {
    void switchToObject(DetailComponent source, String title, DetailComponent target);

    void switchToArray(DetailComponent source, String title, DetailComponent target);

}
