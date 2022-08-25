package com.example.application.ui;


import com.example.application.ui.other.AccessPoint;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.PreserveOnRefresh;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.context.annotation.SessionScope;

/**
 * The main view is a top-level placeholder for other views.
 */
@Slf4j
@PreserveOnRefresh
@UIScope
public class MainLayout extends AppLayout {

    public static class MenuItemInfo extends ListItem {

        /**
         * A simple navigation item component, based on ListItem element.
         */
        public MenuItemInfo(String menuTitle, String navRoute, String iconClass) {
            Div link = new Div();
            link.addClassNames("menu-item-link");
            //link.setRoute(view, new RouteParameters("tag", navRoute));
            link.addClickListener(e ->
                    UI.getCurrent().navigate(navRoute));

            Span text = new Span(menuTitle);
            text.addClassNames("menu-item-text");

            link.add(new LineAwesomeIcon(iconClass), text);
            add(link);
        }

        /**
         * Simple wrapper to create icons using LineAwesome iconset. See
         * <a href="https://icons8.com/line-awesome">...</a>
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


    public MainLayout() {
        setPrimarySection(Section.DRAWER);
        addToNavbar(true, createHeaderContent());
        addToDrawer(createDrawerContent());
        AccessPoint.setMainLayout(this);
    }

    public void removeAll() {
        nav.removeAll();
        nav.add(createBasicMenuItems());
    }

    public void addNavigationTarget(String tagName, boolean isMDV, String navRoute) {
        if (navRoute.startsWith("/"))
            navRoute = navRoute.substring(1);

        log.info("Adding a View with the name: {} and route: {}", tagName, navRoute); //TODO add replacement of " " ? maybe %20 directly ?
        MenuItemInfo menuItemInfo;
        if (isMDV) {
            menuItemInfo = new MenuItemInfo(tagName, "/masterDetail/" + navRoute, "las la-table");
        } else {
            menuItemInfo = new MenuItemInfo(tagName, "/list/" + navRoute, "las la-list");
        }
        nav.add(menuItemInfo);
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

        list.add(createBasicMenuItems());

        return nav;
    }

    private MenuItemInfo createBasicMenuItems() {
        return new MenuItemInfo("About", "about", "la la-file");
    }

    private Footer createFooter() {
        Footer layout = new Footer();
        layout.addClassNames("footer");

        return layout;
    }

    @Override
    protected void afterNavigation() {
        super.afterNavigation();
        viewTitle.setText(getCurrentPageTitle()); //TODO change title on nav
    }

    private String getCurrentPageTitle() {
        PageTitle title = getContent().getClass().getAnnotation(PageTitle.class);
        return title == null ? "" : title.value();
    }
}
