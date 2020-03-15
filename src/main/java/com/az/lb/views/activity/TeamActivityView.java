package com.az.lb.views.activity;

import com.az.lb.MainView;
import com.az.lb.UserContext;
import com.az.lb.model.Team;
import com.az.lb.servise.TeamService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H5;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.function.ValueProvider;
import com.vaadin.flow.router.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;

@Route(value = "TeamActivity", layout = MainView.class)
@RouteAlias(value = "", layout = MainView.class)
@PageTitle("Team's activity")
@CssImport("styles/views/dashboard/dashboard-view.css")
public class TeamActivityView extends VerticalLayout implements AfterNavigationObserver {

    @Autowired
    private TeamService service;

    private final Grid<Team> grid;

    private final ActivityDateDialog activityDateDialog;

    private final UserContext userContext;

    public TeamActivityView(@Autowired UserContext userContext) {

        setId("dashboard-view");

        this.userContext = userContext;

        this.grid = new Grid<Team>();
        this.grid.setId("list");
        this.grid.setHeightFull();
        this.grid.addColumn((ValueProvider<Team, String>) Team::getName)
                .setHeader("Team name")
                .setSortable(true);

        this.grid.addColumn(new ComponentRenderer<>(team -> {
            return new Button(
                    "Date",
                    e-> { newActivity(team); }
                    );
        }))
        .setAutoWidth(true)
        .setTextAlign(ColumnTextAlign.END);

        this.grid.addItemClickListener(
                item -> {
                    getUI().ifPresent(ui -> {
                        userContext.setSelectedTeam(item.getItem());
                        userContext.setSelectedDate(LocalDate.now());
                        ui.getPage().setLocation("/PersonActivity");
                    });
                }
        );

        this.activityDateDialog =  new ActivityDateDialog("Activity date");

        add(
                this.activityDateDialog,
                new H5("Team's activity"),
                this.grid
        );

    }

    private void newActivity(Team team) {
        activityDateDialog
                .message("Select activity date")
                .confirmText("Go")
                .onCancel(e -> {
                    activityDateDialog.close();})
                .onConfirm(e -> {
                    activityDateDialog.close();
                    getUI().ifPresent(ui -> {
                        userContext.setSelectedTeam(team);
                        userContext.setSelectedDate(activityDateDialog.getLocalDate());
                        ui.getPage().setLocation("/PersonActivity");
                    });
                })
                .open();
    }






    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        // Lazy init of the grid items, happens only when we are sure the view will be
        // shown to the user
        userContext.getOrg();
        grid.setItems(service.findAll());
        grid.getDataProvider().refreshAll();
    }


}