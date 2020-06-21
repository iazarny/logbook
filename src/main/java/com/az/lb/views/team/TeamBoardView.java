package com.az.lb.views.team;



import com.az.lb.MainView;
import com.az.lb.UserContext;
import com.az.lb.model.Person;
import com.az.lb.model.PersonActivity;
import com.az.lb.model.PersonPhoto;
import com.az.lb.model.Team;
import com.az.lb.servise.PersonPhotoService;
import com.az.lb.servise.PersonService;
import com.az.lb.servise.TeamService;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.H6;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Page;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Route(value = TeamBoardView.ROUTE, layout = MainView.class)
@PageTitle("Team board")
@Secured({"ADM", "USER"})
@CssImport("styles/views/team/team-board.css")
public class TeamBoardView extends VerticalLayout implements AfterNavigationObserver {

    public static final String ROUTE = "TeamBoardView";

    private final List<PersonPhoto> data = new ArrayList<>();

    @Autowired
    private UserContext userContext;

    @Autowired
    private PersonPhotoService personPhotoService;

    @Autowired
    private PersonService personService;

    @Autowired
    private TeamService teamService;

    private final ComboBox<Team> teamCmb;

    private VerticalLayout board;

    public TeamBoardView(@Autowired UserContext userContext) {

        setId("team-board");

        this.userContext = userContext;

        UI.getCurrent().getPage().addJavaScript("./js/VideoRecorder.js");

        teamCmb = new ComboBox<>();
        teamCmb.setAllowCustomValue(false);
        teamCmb.setItemLabelGenerator(Team::getName);
        teamCmb.addValueChangeListener(e -> {
            System.out.println("Team selected " + e.getValue().getName());
            data.clear();

            /*List<Person> lst = personService.findAllInTeam(e.getValue().getId());
            int columns = (int) Math.min(5, Math.max(3, Math.sqrt(lst.size())));
            board.removeAll();
            HorizontalLayout row = null;
            for (int i = 0; i < lst.size(); i++) {
                if (i % columns == 0) {
                    row = new HorizontalLayout();
                    row.setSizeFull();
                    board.add(row);
                }
                Div div = new Div();
                div.getStyle().set("display", "inline");
                div.setWidthFull();
                div.add(lst.get(i).getFullName());
                row.add(div);

            }*/


            data.addAll(personPhotoService.getTeamsPhoto(e.getValue().getId().toString()));
            int columns = (int) Math.min(5, Math.max(2, Math.sqrt(data.size())));
            board.removeAll();
            HorizontalLayout row = null;
            for (int i = 0; i < data.size(); i++) {
                if (i % columns == 0) {
                    row = new HorizontalLayout();
                    row.setSizeFull();
                    board.add(row);
                }
                Div div = new Div();
                div.add(data.get(i).getPerson().getFullName());
                div.add(data.get(i).getImagedt().toString());
                row.add(div);

            }

        });

        HorizontalLayout head = new HorizontalLayout(
                new Html("<video id=\"video\">Video stream not available.</video>"),
                new VerticalLayout(
                        new H4("Team board"),
                        new HorizontalLayout(
                                new H6("Select team"), teamCmb
                        )
                )

        );

        board = new VerticalLayout();

        add(
                head,
                board,
                new Html("<hr/>"),
                new Html("<div><canvas id='canvas' style='visibility: hidden'/></div>")
        );

    }

    private Div createComponent(String text) {
        Div div = new Div();
        div.getStyle().set("display", "inline");
        div.setWidthFull();
        div.setText(text);
        return div;
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        super.onDetach(detachEvent);
        Page page = UI.getCurrent().getPage();
        page.executeJs("stopVideoRecording()");
    }


    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        Page page = UI.getCurrent().getPage();
        page.executeJs("startVideoRecording($0)", this.userContext.getUserId().toString());
    }



    @Override
    public void afterNavigation(AfterNavigationEvent event) {

        final List<Team> teams = teamService.findTeams(userContext.getOrg());

        teams.stream().findFirst().ifPresent(

                team -> {

                    teamCmb.setItems(teams);

                    teamCmb.setValue(team);

                }

        );

    }
}
