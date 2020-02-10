package com.az.lb.views.team;


import com.az.lb.MainView;
import com.az.lb.UserContext;
import com.az.lb.model.Team;
import com.az.lb.servise.TeamService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.*;
import org.springframework.beans.factory.annotation.Autowired;

@Route(value = "Team", layout = MainView.class)
//@RouteAlias(value = "", layout = MainView.class)
@PageTitle("Team")
//@CssImport("styles/views/dashboard/dashboard-view.css")
public class TeamView extends VerticalLayout implements AfterNavigationObserver {


    @Autowired
    private TeamService service;

    @Autowired
    private UserContext userContext;

    private final Grid<Team> grid;


    public TeamView(@Autowired UserContext userContext) {

        this.userContext = userContext;

        setId("team-view");
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
        Button addBtn = new Button("Add");


        /*Dialog dialog = new Dialog(new Label("Please provide new team name " + userContext.getOrg().getName()));
        Input input = new Input();
        dialog.add(new Div());
        dialog.add(input);
        dialog.add(new Div());
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


        addBtn.addClickListener(event -> {
            dialog.open();
            input.getElement().callJsFunction("focus");
        });*/

        add(addBtn, grid);
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        // Lazy init of the grid items, happens only when we are sure the view will be
        // shown to the user
        userContext.getOrg();
        grid.setItems(service.findAll());
    }


}
