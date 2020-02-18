package com.az.lb.views.team;


import com.az.lb.MainView;
import com.az.lb.UserContext;
import com.az.lb.model.Org;
import com.az.lb.model.Team;
import com.az.lb.servise.PersonService;
import com.az.lb.servise.TeamPersonService;
import com.az.lb.servise.TeamService;
import com.vaadin.flow.component.ItemLabelGenerator;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.listbox.ListBox;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.Renderer;
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

    private H2 header;

    private ComboBox<Team> teamCmb;
    private ListBox assignedMembers;
    private ListBox availableMembers;
    private Button addAll;
    private Button addOne;
    private Button removeOne;
    private Button removeAll;

    private HorizontalLayout mainHl;


    public AssignedPersons(@Autowired UserContext userContext) {

        this.userContext = userContext;

        setId("team-view");

        addAll = new Button(">>");
        addOne = new Button(">");
        removeOne = new Button("<");
        removeAll = new Button("<<");


        HorizontalLayout hlMain = new HorizontalLayout();

        availableMembers = new ListBox<>();

        hlMain.add(availableMembers);
        hlMain.add(new VerticalLayout(
                addAll, addOne, removeOne, removeAll
        ));
        assignedMembers = new ListBox<>();
        hlMain.add(assignedMembers);

        teamCmb = new ComboBox<>("Teams");
        teamCmb.setAllowCustomValue(false);
        teamCmb.setItemLabelGenerator(Team::getName);
        teamCmb.addValueChangeListener(e -> {
            final boolean valSelected = e.getValue() != null;
            availableMembers.setEnabled(valSelected);
            assignedMembers.setEnabled(valSelected);
            addAll.setEnabled(valSelected);
            addOne.setEnabled(valSelected);
            removeOne.setEnabled(valSelected);
            removeAll.setEnabled(valSelected);
            if (valSelected) {
                repopulateMembers(
                        userContext.getOrg(),
                        e.getValue().getId().toString()
                );

            }
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

        repopulateMembers(org, tidValue);

    }

    private void repopulateMembers(Org org, String tidValue) {
        teamCmb.setItems(service.findTeams(org));

        if (Strings.isNotEmpty(tidValue)) {
            availableMembers.setItems(
                    personService.findAllOutOfTeam(UUID.fromString(tidValue), org.getId()));
            assignedMembers.setItems(
                    personService.findAllInTeam(UUID.fromString(tidValue)) );
        }
    }

    @Override
    public void setParameter(BeforeEvent event,
                             @OptionalParameter String parameter) {

    }



}
