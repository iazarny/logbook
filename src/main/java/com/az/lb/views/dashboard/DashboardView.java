package com.az.lb.views.dashboard;

import com.az.lb.UserContext;
import com.az.lb.model.Team;
import com.az.lb.servise.TeamService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.*;
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
@Route(value = "Dashboard", layout = MainView.class)
@RouteAlias(value = "", layout = MainView.class)
@PageTitle("Dashboard")
@CssImport("styles/views/dashboard/dashboard-view.css")
public class DashboardView extends Div implements AfterNavigationObserver {

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
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER,
                GridVariant.LUMO_NO_ROW_BORDERS);
        grid.setHeightFull();
        grid.addColumn(new ComponentRenderer<>(employee -> {
            H3 h3 = new H3(
                    employee.getName() + ", " + employee.getName());
            Anchor anchor = new Anchor("mailto:" + employee.getName(),
                    employee.getName());
            anchor.getElement().getThemeList().add("font-size-xs");
            Div div = new Div(h3, anchor);
            div.addClassName("employee-column");
            return div;
        }));

        //add(new Label("asdfasdf"), grid);
        final Button addBtn = new Button("Add");
        final Dialog dialog = createNewTeamDialog();
        addBtn.addClickListener(event -> {
            dialog.open();
            //input.getElement().callJsFunction("focus");
        });

        add(dialog);
        add(addBtn, grid);
    }



    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        // Lazy init of the grid items, happens only when we are sure the view will be
        // shown to the user
        grid.setItems(service.findAll());
    }


    private Dialog createNewTeamDialog() {
        final Dialog dialog = new Dialog(new Label("Please provide new team name " + userContext.getOrg().getName()));
        final Input input = new Input();
        dialog.add(new Div(),
                input,
                new Div());
        dialog.setCloseOnEsc(true);
        dialog.setCloseOnOutsideClick(true);

        NativeButton confirmButton = new NativeButton("New", event -> {
            Team team = service.createNewTeam(
                    userContext.getOrg().getId().toString(),
                    input.getValue());

            dialog.close();
            grid.getDataProvider().refreshAll();
        });
        NativeButton cancelButton = new NativeButton("Cancel", event -> {
            dialog.close();
        });
        dialog.add(confirmButton, cancelButton);

        return dialog;
    }
}
