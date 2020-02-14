package com.az.lb.views.dashboard;

import com.az.lb.UserContext;
import com.az.lb.model.Team;
import com.az.lb.servise.TeamService;
import com.az.lb.views.ConfirmDialog;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.function.ValueProvider;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;

import com.az.lb.MainView;

@Route(value = "Dashboard", layout = MainView.class)
@RouteAlias(value = "", layout = MainView.class)
@PageTitle("Dashboard")
@CssImport("styles/views/dashboard/dashboard-view.css")
public class DashboardView extends VerticalLayout implements AfterNavigationObserver {

    @Autowired
    private TeamService service;

    private final Grid<Team> grid;
    private final ConfirmDialog confirmDialog;
    private final TeamEditDialog teamDialog;

    @Autowired
    private UserContext userContext;

    public DashboardView(@Autowired UserContext userContext) {

        this.userContext = userContext;
        setId("dashboard-view");
        grid = new Grid<Team>();
        grid.setId("list");
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_COMPACT,
                GridVariant.MATERIAL_COLUMN_DIVIDERS);
        grid.setHeightFull();
        grid.addColumn((ValueProvider<Team, String>) team -> team.getName()).setHeader("Name").setSortable(true);

        grid.addColumn(new ComponentRenderer<>(team -> {
            Button deleteBtn = new Button("Del", e -> {  removeTeam(team);   });

            Button editBtn = new Button("Edit", e -> { editTeam(team); });
            Button teamBtn = new Button("Members");
            Button activityBtn = new Button("Activity");

            return new HorizontalLayout(
                    deleteBtn, editBtn, teamBtn, activityBtn
            );
        }));


        final Button addBtn = new Button("Add");
        teamDialog = new TeamEditDialog("Add new team");
        add(teamDialog);
        addBtn.addClickListener(event -> { newTeam(); });

        confirmDialog = new ConfirmDialog("Please confirm", "");
        add(confirmDialog);

        HorizontalLayout hl = new HorizontalLayout(addBtn);
        hl.setAlignItems(Alignment.CENTER);

        add(
                new H2("Teams"),
                hl,
                grid
        );

    }

    private void newTeam() {
        teamDialog
                .message("New team")
                .onCancel(e -> {grid.getDataProvider().refreshAll();})
                .onConfirm(e -> {
                    Team team = service.createNewTeam(
                            userContext.getOrg().getId().toString(),
                            teamDialog.input.getValue());
                    grid.setItems(service.findAll());
                    grid.getDataProvider().refreshAll();
                    teamDialog.close();
                })
                .open(); //teamDialog.input.getElement().callJsFunction("focus");
    }


    private void editTeam(Team team) {

        teamDialog
                .message("Edit " + team.getName())
                .teamName(team.getName())
                .onCancel(e -> {
                    grid.getDataProvider().refreshAll();
                    teamDialog.close();
                })
                .onConfirm( e-> {
                    team.setName(teamDialog.input.getValue());
                    service.update(team);
                    grid.getDataProvider().refreshAll();
                    teamDialog.close();
                })
                .open();

    }

    private void removeTeam(Team team) {
        confirmDialog
                .message("Do you want to delete the team " + team.getName() + " ?")
                .onCancel(e -> {
                    grid.getDataProvider().refreshAll();
                    confirmDialog.close();
                })
                .onConfirm(e -> {
                    service.deleteTeam(team.getId());
                    ((ListDataProvider) grid.getDataProvider()).getItems().remove(team);
                    grid.getDataProvider().refreshAll();
                    confirmDialog.close();
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
