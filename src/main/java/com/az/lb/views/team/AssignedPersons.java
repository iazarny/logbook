package com.az.lb.views.team;


import com.az.lb.MainView;
import com.az.lb.UserContext;
import com.az.lb.model.Org;
import com.az.lb.model.Person;
import com.az.lb.model.Team;
import com.az.lb.servise.PersonService;
import com.az.lb.servise.TeamPersonService;
import com.az.lb.servise.TeamService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.listbox.ListBox;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.TextRenderer;
import com.vaadin.flow.router.*;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Route(value = "Team", layout = MainView.class)
//@RouteAlias(value = "", layout = MainView.class)
@PageTitle("Team")
//@CssImport("styles/views/dashboard/dashboard-view.css")
//https://github.com/vaadin/flow/issues/3628
public class AssignedPersons extends VerticalLayout implements AfterNavigationObserver, HasUrlParameter<String> {


    private final String TID = "tid";

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

    private H2 header;

    private ComboBox<Team> teamCmb;
    private ListBox<Person> assignedMembersLb;
    private ListBox<Person> availableMembersLb;
    private Button addAllBtn;
    private Button addOneBtn;
    private Button removeOneBtn;
    private Button removeAllBtn;

    private HorizontalLayout mainHl;


    public AssignedPersons(@Autowired UserContext userContext) {

        this.userContext = userContext;

        setId("team-view");

        addAllBtn = new Button(">>");
        addOneBtn = new Button(">");
        removeOneBtn = new Button("<");
        removeAllBtn = new Button("<<");


        HorizontalLayout hlMain = new HorizontalLayout();

        availableMembersLb = new ListBox<>();
        availableMembersLb.setRenderer(new TextRenderer<>(p -> p.getFirstName() + " " + p.getLastName() ));

        hlMain.add(availableMembersLb);

        hlMain.add(new VerticalLayout(
                addAllBtn, addOneBtn, removeOneBtn, removeAllBtn
        ));

        assignedMembersLb = new ListBox<>();
        assignedMembersLb.setRenderer(new TextRenderer<>(p -> p.getFirstName() + " " + p.getLastName() ));
        hlMain.add(assignedMembersLb);

        teamCmb = new ComboBox<>("Teams");
        teamCmb.setAllowCustomValue(false);
        teamCmb.setItemLabelGenerator(Team::getName);
        teamCmb.addValueChangeListener(e -> {

            if (e.getValue() != null) {
                repopulateMembers(
                        userContext.getOrg(),
                        e.getValue().getId()
                );

            }
        });

        availableMembersLb.addValueChangeListener(
                e -> {
                    changeControlsVisibility();
                }
        );

        assignedMembersLb.addValueChangeListener(
                e -> {
                    changeControlsVisibility();
                }
        );

        addAllBtn.addClickListener( e-> {
            assignAllPersons();

        });

        addOneBtn.addClickListener( e-> {
            assignOnePerson();

        });

        removeOneBtn.addClickListener( e-> {

            removeOnePerson();

        });

        removeAllBtn.addClickListener( e-> {
            removeAllPersons();

        });


        add(
                new H2("Team members"),
                teamCmb,
                hlMain
        );



    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {

        final Org org = userContext.getOrg();

        final QueryParameters queryParameters = event.getLocation().getQueryParameters();

        final Map<String, List<String>> parametersMap = queryParameters.getParameters();

        final String tidValue = parametersMap.getOrDefault(TID, Collections.singletonList("")).get(0);

        if (Strings.isNotEmpty(tidValue)) {

            final UUID selectedTeamId = UUID.fromString(tidValue);

            final List<Team> teams = service.findTeams(org);

            teams.stream().filter(t -> selectedTeamId.equals(t.getId())).findFirst().ifPresent(

                    team -> {

                        teamCmb.setItems(teams);

                        teamCmb.setValue(team);

                    }

            );


        }

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

        addAllBtn.setEnabled(teamSelected && !availableMembers.isEmpty());
        addOneBtn.setEnabled(teamSelected && availableMembersLb.getValue() != null);
        removeOneBtn.setEnabled(teamSelected && assignedMembersLb.getValue() != null);
        removeAllBtn.setEnabled(teamSelected && !assignedMembers.isEmpty());
    }

    @Override
    public void setParameter(BeforeEvent event,
                             @OptionalParameter String parameter) {

    }



}
