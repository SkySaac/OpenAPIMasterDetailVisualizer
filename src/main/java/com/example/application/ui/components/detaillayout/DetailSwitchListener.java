package com.example.application.ui.components.detaillayout;

import com.example.application.ui.components.detaillayout.detailcomponents.DetailComponent;

public interface DetailSwitchListener {
    void switchToObject(DetailComponent source, String title, DetailComponent target);

    void switchToArray(DetailComponent source, String title, DetailComponent target);
}
