package com.az.lb.views.dashboard;

import com.az.lb.UserContext;
import com.az.lb.model.Team;
import com.az.lb.servise.TeamService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.springframework.beans.factory.annotation.Autowired;

import com.az.lb.backend.BackendService;
import com.az.lb.backend.Employee;
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

import java.util.UUID;

@Route(value = "Dashboard", layout = MainView.class)
@RouteAlias(value = "", layout = MainView.class)
@PageTitle("Dashboard")
@CssImport("styles/views/dashboard/dashboard-view.css")
public class DashboardView extends VerticalLayout implements AfterNavigationObserver {

    @Autowired
    private TeamService service;

    private final Grid<Team> grid;

    @Autowired
    private UserContext userContext;

    public DashboardView(@Autowired UserContext userContext) {

        this.userContext = userContext;
        setId("dashboard-view");
        grid = new Grid<>();
        grid.setId("list");
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_COMPACT, GridVariant.MATERIAL_COLUMN_DIVIDERS);
        grid.setHeightFull();
        grid.addColumn(new ComponentRenderer<>(team -> {
            Button deleteBtn = new Button("Del"); // for team without any activities or assigned teams
            deleteBtn.addClickListener( e -> { removeTeam(team.getId()); } );
            Button editBtn = new Button("Edit");
            Button teamBtn = new Button("Members");
            Button activityBtn = new Button("Activity");
            VerticalLayout vl = new VerticalLayout(
                    new H3(team.getName())/*,
                    new Label("Todo some info")*/
            );
            HorizontalLayout hl = new HorizontalLayout(
                    vl,
                    deleteBtn, editBtn, teamBtn, activityBtn
            );

            return hl;
        }));
        final Button addBtn = new Button("Add");
        final TeamEditDialog newTeamDialog =  new TeamEditDialog("Add new team");
        addBtn.addClickListener(event -> {
            newTeamDialog.open();
            newTeamDialog.input.setValue("");
            newTeamDialog.input.getElement().callJsFunction("focus");
            newTeamDialog.confirmButton.addClickListener(
                    e -> {
                        Team team = service.createNewTeam(
                                userContext.getOrg().getId().toString(),
                                newTeamDialog.input.getValue());
                        grid.getDataProvider().refreshAll();
                        newTeamDialog.close();
                    }
            );
        });

        add(newTeamDialog);
        HorizontalLayout hl = new HorizontalLayout(addBtn);
        hl.setAlignItems(Alignment.END);
        add(hl, grid);

    }

    private void removeTeam(UUID id) {
        System.out.println(">>>>>>>>>>>>>>>>>> id " + id);
    }


    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        // Lazy init of the grid items, happens only when we are sure the view will be
        // shown to the user
        grid.setItems(service.findAll());
        grid.getDataProvider().refreshAll();
    }



}
