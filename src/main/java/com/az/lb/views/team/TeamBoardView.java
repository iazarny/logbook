package com.az.lb.views.team;



import com.az.lb.MainView;
import com.az.lb.UserContext;
import com.az.lb.model.PersonPhoto;
import com.az.lb.model.Team;
import com.az.lb.servise.PersonPhotoService;
import com.az.lb.servise.PersonService;
import com.az.lb.servise.TeamService;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Page;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;

import java.time.temporal.ChronoUnit;
import java.util.*;

@Route(value = TeamBoardView.ROUTE, layout = MainView.class)
@PageTitle("Team board")
@Secured({"ADM", "USER"})
@CssImport("styles/views/team/team-board.css")
public class TeamBoardView extends VerticalLayout implements AfterNavigationObserver {

    public static final String ROUTE = "TeamBoardView";

    public static int REFRESH_TIMEOUT = 30000;

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

    private volatile UUID selectedTeamId = null;

    private TeamBoardRefreshThread thread = null;

    public TeamBoardView(@Autowired UserContext userContext) {

        setId("team-board");

        this.userContext = userContext;

        UI.getCurrent().getPage().addJavaScript("./js/VideoRecorder.js");

        teamCmb = new ComboBox<>();
        teamCmb.setAllowCustomValue(false);
        teamCmb.setItemLabelGenerator(Team::getName);
        teamCmb.addValueChangeListener(e -> {
            getTeamPhotoBoard(e.getValue().getId(), true);
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

    public void getTeamPhotoBoard(UUID tid, boolean hard) {
        selectedTeamId = tid;
        data.clear();
        data.addAll(personPhotoService.getTeamsPhoto(selectedTeamId.toString()));

        if (hard) {
            int columns = (int) Math.min(5, Math.max(2, Math.sqrt(data.size())));
            board.removeAll();

            HorizontalLayout row = null;
            for (int i = 0; i < data.size(); i++) {
                if (i % columns == 0) {
                    row = new HorizontalLayout();
                    row.setSizeFull();
                    board.add(row);
                }
                row.add(createComponent(data.get(i)));
            }
        } else {
            List<Component> collector = new ArrayList<>();
            findChildren(board, "img-",  collector);
            collector.forEach(component -> {
                final Image img = (Image) component;
                final int tailIdx = img.getSrc().indexOf("&rnd");
                final String src;
                if ( tailIdx == -1) {
                    src = img.getSrc();
                } else {
                    src = img.getSrc().substring(0, tailIdx);
                }
                String newSrc = src + "&rnd=" + UUID.randomUUID().toString();
                img.setSrc(newSrc);
            });
        }
    }


    private void findChildren(Component root, String idFilder, List<Component> collector) {
        root.getChildren().forEach(
                c -> {
                    c.getId().ifPresent(
                            id -> {
                                if(id.startsWith(idFilder)) {
                                    collector.add(c);
                                }
                            }
                    );
                    findChildren(c, idFilder, collector);
                }
        );
    }

    private Div createComponent(PersonPhoto ph) {
        Div div = new Div();
        div.setId("div-" + ph.getId().toString());
        div.setClassName("photo-card");

        Html mailTo = new Html(
                String.format("<div><a href='mailto:%s'>%s</a></div>",
                        ph.getPerson().getEmail(), ph.getPerson().getFullName())
        );

        Image image = new Image();
        image.setId("img-" + ph.getId().toString());
        image.setSrc(
                String.format(
                        "download/photo?pid=%s", ph.getId().toString()
                )
        );
        image.setAlt(
                String.format(
                        "Photo at", ph.getImagedt().truncatedTo(ChronoUnit.MINUTES)
                )
        );


        Div msg = new Div();

        div.add(mailTo);
        div.add(image);
        div.add(msg);

        return div;
    }


    @Override
    protected void onDetach(DetachEvent detachEvent) {
        super.onDetach(detachEvent);
        Page page = UI.getCurrent().getPage();
        page.executeJs("stopVideoRecording()");

        thread.interrupt();
        thread = null;
    }


    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        Page page = UI.getCurrent().getPage();
        page.executeJs("startVideoRecording($0)", this.userContext.getUserId().toString());

        thread = new TeamBoardRefreshThread(attachEvent.getUI(), this);
        thread.start();
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


    private static class TeamBoardRefreshThread extends Thread {
        private final UI ui;
        private final TeamBoardView view;

        private TeamBoardRefreshThread(UI ui, TeamBoardView view) {
            this.ui = ui;
            this.view = view;
        }

        @Override
        public void run() {
            try {
                while(true) {

                    if (view.selectedTeamId != null) {
                        ui.access(() -> view.getTeamPhotoBoard(view.selectedTeamId, false));
                    }
                    Thread.sleep(TeamBoardView.REFRESH_TIMEOUT);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


}
