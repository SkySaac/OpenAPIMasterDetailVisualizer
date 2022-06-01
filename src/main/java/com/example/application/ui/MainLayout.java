package com.example.application.ui;


import com.example.application.ui.accesspoint.AccessPoint;
import com.example.application.ui.route.AboutRoute;
import com.example.application.ui.route.MasterDetailRoute;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.RouteParameters;
import com.vaadin.flow.router.RouterLink;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * The main view is a top-level placeholder for other views.
 */
@Slf4j
public class MainLayout extends AppLayout {

    public static class MenuItemInfo extends ListItem {

        private final Class<? extends Component> view;

        /**
         * A simple navigation item component, based on ListItem element.
         */
        public MenuItemInfo(String menuTitle, String iconClass, Class<? extends Component> view) {
            this.view = view;
            RouterLink link = new RouterLink();
            link.addClassNames("menu-item-link");
            link.setRoute(view, new RouteParameters("tag", menuTitle));

            Span text = new Span(menuTitle);
            text.addClassNames("menu-item-text");

            link.add(new LineAwesomeIcon(iconClass), text);
            add(link);
        }

        public Class<?> getView() {
            return view;
        }
        /**
         * Simple wrapper to create icons using LineAwesome iconset. See
         * https://icons8.com/line-awesome
         */
        @NpmPackage(value = "line-awesome", version = "1.3.0")
        public static class LineAwesomeIcon extends Span {

            public LineAwesomeIcon(String lineawesomeClassnames) {
                addClassNames("menu-item-icon");
                if (!lineawesomeClassnames.isEmpty()) {
                    addClassNames(lineawesomeClassnames);
                }
            }

        }

    }
    private H1 viewTitle;
    private Nav nav;

    private Map<String,MenuItemInfo> addedNavTargets = new HashMap<>();


    public MainLayout() {
        setPrimarySection(Section.DRAWER);
        addToNavbar(true, createHeaderContent());
        addToDrawer(createDrawerContent());
        AccessPoint.setMainLayout(this);
    }

    public void removeNavigationTarget(String tagName){
        log.info("Removing navigation target: "+ tagName);
        if(addedNavTargets.containsKey(tagName)) {
            nav.remove(addedNavTargets.get(tagName));
            addedNavTargets.remove(tagName);
        }
    } //TODO remove Nav target

    public void addNavigationTarget(String tagName){
        log.info("Adding a MasterDetailView with the name: "+tagName); //TODO add replacement of " " ? maybe %20 directly ?
        MenuItemInfo menuItemInfo = new MenuItemInfo(tagName, "la la-columns", MasterDetailRoute.class);
        nav.add(menuItemInfo);
        addedNavTargets.put(tagName,menuItemInfo);
    }

    private Component createHeaderContent() {
        DrawerToggle toggle = new DrawerToggle();
        toggle.addClassNames("view-toggle");
        toggle.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
        toggle.getElement().setAttribute("aria-label", "Menu toggle");

        viewTitle = new H1();
        viewTitle.addClassNames("view-title");

        Header header = new Header(toggle, viewTitle);
        header.addClassNames("view-header");
        return header;
    }

    private Component createDrawerContent() {
        H2 appName = new H2("My Bachelor");
        appName.addClassNames("app-name");

        this.nav = createNavigation();
        com.vaadin.flow.component.html.Section section = new com.vaadin.flow.component.html.Section(appName,
                nav, createFooter());
        section.addClassNames("drawer-section");
        return section;
    }

    private Nav createNavigation() {
        Nav nav = new Nav();
        nav.addClassNames("menu-item-container");
        nav.getElement().setAttribute("aria-labelledby", "views");

        // Wrap the links in a list; improves accessibility
        UnorderedList list = new UnorderedList();
        list.addClassNames("navigation-list");
        nav.add(list);

        for (MenuItemInfo menuItem : createBasicMenuItems()) {
            list.add(menuItem);
        }
        return nav;
    }

    private MenuItemInfo[] createBasicMenuItems() {
        return new MenuItemInfo[]{ //
                new MenuItemInfo("About", "la la-file", AboutRoute.class)
        };
    }

    private Footer createFooter() {
        Footer layout = new Footer();
        layout.addClassNames("footer");

        return layout;
    }

    @Override
    protected void afterNavigation() {
        super.afterNavigation();
        viewTitle.setText(getCurrentPageTitle());
    }

    private String getCurrentPageTitle() {
        PageTitle title = getContent().getClass().getAnnotation(PageTitle.class);
        return title == null ? "" : title.value();
    }
}
