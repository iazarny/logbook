package com.az.lb;

import com.az.lb.views.activity.TeamActivityView;
import com.az.lb.views.dashboard.TeamView;
import com.az.lb.views.masterdetail.PersonView;
import com.az.lb.views.org.OrganizationSettingView;
import com.az.lb.views.report.ReportDailyView;
import com.az.lb.views.report.ReportPersonView;
import com.az.lb.views.report.ReportTaskView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * The main view is a top-level placeholder for other views.
 */
@JsModule("./styles/shared-styles.js")
@PWA(name = "Log Book", shortName = "Log Book")
@Theme(value = Lumo.class, variant = Lumo.LIGHT)
public class MainView extends AppLayout {

    private final Accordion accordion;

    public MainView() {

        this.accordion =  new Accordion( );
        this.accordion.add(
                "Activity",
                new VerticalLayout(
                        new RouterLink("Activity", TeamActivityView.class)
                )
        );
        this.accordion.add(
                "Reports",
                new VerticalLayout(
                        new RouterLink("Daily", ReportDailyView.class),
                        new RouterLink("Tasks", ReportTaskView.class),
                        new RouterLink("Person", ReportPersonView.class)
                )
        );
        this.accordion.add(
                "Settings",
                new VerticalLayout(
                        new RouterLink("Persons", PersonView.class),
                        new RouterLink("Teams ", TeamView.class),
                        new RouterLink("Organization", OrganizationSettingView.class)
                        )
        );
        addToDrawer(this.accordion );
        //addToNavbar(new Label("Log book"));
    }


    @Override
    protected void afterNavigation() {
        super.afterNavigation();
    }


}
