package com.az.lb.views.team;


import com.az.lb.MainView;
import com.az.lb.UserContext;
import com.az.lb.model.Org;
import com.az.lb.model.Person;
import com.az.lb.model.Team;
import com.az.lb.security.SecurityUtils;
import com.az.lb.servise.PersonService;
import com.az.lb.servise.TeamPersonService;
import com.az.lb.servise.TeamService;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.HtmlContainer;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.listbox.ListBox;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.TextRenderer;
import com.vaadin.flow.router.*;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.material.Material;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Route(value = AssignedPersons.ROUTE, layout = MainView.class)
@PageTitle("Assigned persons")
@Secured({"ADM", "USER"})
public class AssignedPersons extends VerticalLayout implements AfterNavigationObserver/*, HasUrlParameter<String>*/{

    public static final String ROUTE = "AssignedPersons";

    @Autowired
    private TeamPersonService teamPersonService;

    @Autowired
    private TeamService service;

    @Autowired
    private PersonService personService;

    @Autowired
    private UserContext userContext;

    private List<Person> availableMembers;
    private List<Person> assignedMembers;

    private HtmlContainer header;

    private ComboBox<Team> teamCmb;
    private ListBox<Person> assignedMembersLb;
    private ListBox<Person> availableMembersLb;
    private Button addAllBtn;
    private Button addOneBtn;
    private Button removeOneBtn;
    private Button removeAllBtn;

    private HorizontalLayout mainHl;


    public AssignedPersons(@Autowired UserContext userContext) {

        setId("team-view");

        this.userContext = userContext;



        this.addAllBtn = new Button(">>");
        this.addOneBtn = new Button(">");
        this.removeOneBtn = new Button("<");
        this.removeAllBtn = new Button("<<");


        HorizontalLayout hlMain = new HorizontalLayout();


        availableMembersLb = new ListBox<>();
        availableMembersLb.setRenderer(new TextRenderer<>(p -> p.getFullName() ));
        availableMembersLb.setMinWidth("200px");
        availableMembersLb.setMaxWidth("300px");
        availableMembersLb.setWidth("250px");
        availableMembersLb.setHeight("500px");

        VerticalLayout verticalLayout = new VerticalLayout(
                new Html("<div><br/><br/><br/></div>"), addAllBtn, addOneBtn, removeOneBtn, removeAllBtn
        );
        verticalLayout.setMinWidth("100px");
        verticalLayout.setMaxWidth("100px");
        verticalLayout.setWidth("100px");



        assignedMembersLb = new ListBox<>();
        assignedMembersLb.setRenderer(new TextRenderer<>(p -> p.getFullName() ));
        assignedMembersLb.setMinWidth("200px");
        assignedMembersLb.setMaxWidth("300px");
        assignedMembersLb.setWidth("250px");
        assignedMembersLb.setHeight("500px");

        hlMain.add(
                new VerticalLayout(
                        new Label("Available persons"),
                        availableMembersLb
                )
        );

        hlMain.add(verticalLayout);

        hlMain.add(
                new VerticalLayout(
                        new Label("Assigned persons"),
                        assignedMembersLb
                )
        );



        teamCmb = new ComboBox<>();
        teamCmb.setAllowCustomValue(false);
        teamCmb.setItemLabelGenerator(Team::getName);
        teamCmb.addValueChangeListener(e -> {
            if (e.getValue() != null) {
                repopulateMembers( userContext.getOrg(), e.getValue().getId() );
            }
        });

        availableMembersLb.addValueChangeListener(  e -> changeControlsVisibility() );

        assignedMembersLb.addValueChangeListener(e -> changeControlsVisibility()  );

        addAllBtn.addClickListener( e-> assignAllPersons());

        addOneBtn.addClickListener( e-> assignOnePerson());

        removeOneBtn.addClickListener( e-> removeOnePerson());

        removeAllBtn.addClickListener( e-> removeAllPersons());

        header = new H6("Select team");
        HorizontalLayout htTitle = new HorizontalLayout(
                header, teamCmb
        );

        add(
                new H4("Assign members to the team"),
                htTitle,
                hlMain
        );

        if(!SecurityUtils.hasRole("ADM")) {
            addAllBtn.setEnabled(false);
            addOneBtn.setEnabled(false);
            removeOneBtn.setEnabled(false);
            removeAllBtn.setEnabled(false);
        }

    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {

        final Org org = userContext.getOrg();

        final UUID selectedTeamId = userContext.getSelectedTeam().getId();

        final List<Team> teams = service.findTeams(org);

        teams.stream().filter(t -> selectedTeamId.equals(t.getId())).findFirst().ifPresent(

                team -> {

                    teamCmb.setItems(teams);

                    teamCmb.setValue(team);

                }

        );

        changeControlsVisibility();

    }

    private void repopulateMembers(Org org, UUID selectedTeamId) {

        availableMembers =  personService.findAllOutOfTeam(selectedTeamId, org.getId());
        assignedMembers =  personService.findAllInTeam(selectedTeamId);

        availableMembersLb.setItems(availableMembers);
        assignedMembersLb.setItems(assignedMembers );

        changeControlsVisibility();

    }

    private void assignAllPersons() {
        availableMembers.forEach(
                p -> { teamPersonService.assignPerson(p.getId(), teamCmb.getValue().getId());  }
        );
        repopulateMembers(userContext.getOrg(),  teamCmb.getValue().getId());
    }

    private void assignOnePerson() {
        teamPersonService.assignPerson(availableMembersLb.getValue().getId(), teamCmb.getValue().getId());
        repopulateMembers(userContext.getOrg(),  teamCmb.getValue().getId());
    }

    private void removeOnePerson() {
        teamPersonService.unassignPerson(assignedMembersLb.getValue().getId(), teamCmb.getValue().getId());
        repopulateMembers(userContext.getOrg(),  teamCmb.getValue().getId());
    }

    private void removeAllPersons() {
        assignedMembers.forEach(
                p -> {teamPersonService.unassignPerson(p.getId(), teamCmb.getValue().getId()); }
        );
        repopulateMembers(userContext.getOrg(),  teamCmb.getValue().getId());
    }


    private void changeControlsVisibility() {
        final boolean teamSelected = teamCmb.getValue() != null;

        availableMembersLb.setEnabled(teamSelected);
        assignedMembersLb.setEnabled(teamSelected);

        if(SecurityUtils.hasRole("ADM")) {
            addAllBtn.setEnabled(teamSelected && !availableMembers.isEmpty());
            addOneBtn.setEnabled(teamSelected && availableMembersLb.getValue() != null);
            removeOneBtn.setEnabled(teamSelected && assignedMembersLb.getValue() != null);
            removeAllBtn.setEnabled(teamSelected && !assignedMembers.isEmpty());
        } else {

            addAllBtn.setEnabled(false);
            addOneBtn.setEnabled(false);
            removeOneBtn.setEnabled(false);
            removeAllBtn.setEnabled(false);

        }
    }


}
