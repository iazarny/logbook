package com.az.lb.views.dashboard;

import com.az.lb.UserContext;
import com.az.lb.model.Team;
import com.az.lb.servise.TeamService;
import com.az.lb.views.ConfirmDialog;
import com.az.lb.views.activity.ActivityDateDialog;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H5;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
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
@RouteAlias(value = "Dashboard", layout = MainView.class)
@PageTitle("Dashboard")
@CssImport("styles/views/dashboard/dashboard-view.css")
public class TeamView extends VerticalLayout implements AfterNavigationObserver {

    @Autowired
    private TeamService service;

    private final Grid<Team> grid;
    private final ConfirmDialog confirmDialog;
    private final TeamEditDialog teamDialog;
    private final ActivityDateDialog activityDateDialog;

    private UserContext userContext;

    public TeamView(@Autowired UserContext userContext) {

        setId("dashboard-view");

        this.userContext = userContext;

        this.grid = new Grid<Team>();
        this.grid.setId("list");
        //this.grid.addThemeVariants(GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_COMPACT,
        //      GridVariant.MATERIAL_COLUMN_DIVIDERS);
        this.grid.setHeightFull();
        this.grid.addColumn((ValueProvider<Team, String>) team ->
                team.getName()).setHeader("Name").setSortable(true);



        this.grid.addComponentColumn(
                i -> {

                    Icon deleteIcon = new Icon(VaadinIcon.CROSS_CUTLERY);
                    deleteIcon.addClickListener(
                            e -> removeTeam(i)
                    );
                    final FlexLayout deleteButtonWrapper = new FlexLayout(deleteIcon);
                    deleteButtonWrapper.setJustifyContentMode(FlexComponent.JustifyContentMode.END);


                    Icon editIcon = new Icon(VaadinIcon.PENCIL);
                    editIcon.addClickListener(
                            e -> editTeam(i)
                    );
                    final FlexLayout editButtonWrapper = new FlexLayout(editIcon);
                    editButtonWrapper.setJustifyContentMode(FlexComponent.JustifyContentMode.END);

                    Icon membersIcon = new Icon(VaadinIcon.USERS);
                    membersIcon.addClickListener(
                            e -> editMembers(i)
                    );
                    final FlexLayout membersButtonWrapper = new FlexLayout(membersIcon);
                    membersButtonWrapper.setJustifyContentMode(FlexComponent.JustifyContentMode.END);

                    HorizontalLayout hl = new HorizontalLayout(
                            deleteButtonWrapper, editButtonWrapper, membersButtonWrapper);

                    return hl;
                }
        ).setAutoWidth(true)
                .setFlexGrow(0)
                .setTextAlign(ColumnTextAlign.END);


        this.activityDateDialog = new ActivityDateDialog("Activity date");

        this.confirmDialog = new ConfirmDialog("Please confirm", "");

        final Button addBtn = new Button("Add");
        final FlexLayout addButtonWrapper = new FlexLayout(addBtn);
        addButtonWrapper.setJustifyContentMode(JustifyContentMode.END);

        addBtn.addClickListener(event -> {
            newTeam();
        });


        final HorizontalLayout hl = new HorizontalLayout(
                new H5("Teams"),
                addButtonWrapper
        );
        hl.expand(addButtonWrapper);
        hl.setWidthFull();

        this.teamDialog = new TeamEditDialog("Add new team");

        add(this.activityDateDialog);

        add(this.confirmDialog);

        add(this.teamDialog);

        add(
                hl,
                grid
        );

    }

    private void editMembers(Team team) {
        getUI().ifPresent(ui -> {
            userContext.setSelectedTeam(team);
            ui.getPage().setLocation("/AssignedPersons");
        });
    }




    private void newTeam() {
        teamDialog
                .message("New team")
                .onCancel(e -> {
                    teamDialog.close();
                })
                .onConfirm(e -> {
                    Team team = service.createNewTeam(
                            userContext.getOrg().getId().toString(),
                            teamDialog.getValue());
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
                .onConfirm(e -> {
                    team.setName(teamDialog.getValue());
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
