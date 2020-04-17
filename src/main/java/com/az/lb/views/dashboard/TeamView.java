package com.az.lb.views.dashboard;

import com.az.lb.MainView;
import com.az.lb.UserContext;
import com.az.lb.model.Team;
import com.az.lb.security.SecurityUtils;
import com.az.lb.servise.TeamService;
import com.az.lb.servise.mail.MailService;
import com.az.lb.views.ConfirmDialog;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.function.ValueProvider;
import com.vaadin.flow.router.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;

@Route(value = "Teams", layout = MainView.class)
@RouteAlias(value = "Teams", layout = MainView.class)
@PageTitle("Teams")
@CssImport("styles/views/dashboard/dashboard-view.css")
@Secured({"ADM", "USER"})
public class TeamView extends VerticalLayout implements AfterNavigationObserver {

    private static final Logger logger = LoggerFactory.getLogger(TeamView.class);

    @Autowired
    private TeamService service;

    private final Grid<Team> grid;
    private final ConfirmDialog confirmDialog;
    private final TeamEditDialog teamDialog;

    private UserContext userContext;

    public TeamView(@Autowired UserContext userContext) {

        setId("dashboard-view");

        this.userContext = userContext;

        this.grid = new Grid<Team>();
        this.grid.setId("list");
        this.grid.setHeightFull();
        this.grid.addColumn((ValueProvider<Team, String>) team ->
                team.getName()).setHeader("Name").setSortable(true);



        this.grid.addComponentColumn(
                i -> {

                    Button deleteBtn = new Button(new Icon(VaadinIcon.DEL), e -> removeTeam(i));

                    Button editBtn = new Button(new Icon(VaadinIcon.EDIT), e -> editTeam(i));

                    Button membersBtn = new Button(new Icon(VaadinIcon.USERS), e -> editMembers(i));

                    if (!SecurityUtils.hasRole("ADM")) {
                        deleteBtn.setEnabled(false);
                        editBtn.setEnabled(false);
                        //membersBtn.setEnabled(false);
                    }

                    HorizontalLayout hl = new HorizontalLayout(
                            deleteBtn, editBtn, membersBtn);

                    return hl;
                }
        ).setAutoWidth(true)
                .setFlexGrow(0)
                .setTextAlign(ColumnTextAlign.END);


        this.confirmDialog = new ConfirmDialog("Please confirm", "");

        final Button addBtn = new Button("Add");
        final FlexLayout addButtonWrapper = new FlexLayout(addBtn);
        addButtonWrapper.setJustifyContentMode(JustifyContentMode.END);

        addBtn.addClickListener(event -> {
            newTeam();
        });


        final HorizontalLayout hl = new HorizontalLayout(
                new H4("Teams"),
                addButtonWrapper
        );
        hl.expand(addButtonWrapper);
        hl.setWidthFull();

        this.teamDialog = new TeamEditDialog("Add new team");

        add(this.confirmDialog);

        add(this.teamDialog);

        add(
                hl,
                grid
        );

        if (!SecurityUtils.hasRole("ADM")) {
            addBtn.setEnabled(false);
        }

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
                    grid.setItems(service.findTeams(userContext.getOrg()));
                    grid.getDataProvider().refreshAll();
                    teamDialog.close();
                })
                .open(); //teamDialog.input.getElement().callJsFunction("focus");
    }


    private void editTeam(Team team) {

        teamDialog
                .message("Edit")
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
                    try {
                        service.deleteTeam(team.getId());
                        ((ListDataProvider) grid.getDataProvider()).getItems().remove(team);
                        grid.getDataProvider().refreshAll();
                        confirmDialog.close();
                    } catch (Exception ex) {
                        logger.warn("Cannot remove team ", e);
                        Notification.show("Can't remove team");
                    }

                })
                .open();
    }


    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        // Lazy init of the grid items, happens only when we are sure the view will be
        // shown to the user
        grid.setItems(service.findTeams(userContext.getOrg()));
        grid.getDataProvider().refreshAll();
    }


}
