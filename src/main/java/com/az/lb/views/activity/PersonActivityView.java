package com.az.lb.views.activity;


import com.az.lb.MainView;
import com.az.lb.UserContext;
import com.az.lb.model.PersonActivity;
import com.az.lb.model.Team;
import com.az.lb.servise.PersonActivityService;
import com.az.lb.servise.TeamService;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.function.ValueProvider;
import com.vaadin.flow.router.*;
import com.vaadin.flow.shared.Registration;
import org.springframework.beans.factory.annotation.Autowired;

@Route(value = "PersonActivity", layout = MainView.class)
//@RouteAlias(value = "PersonActivity", layout = MainView.class)
@PageTitle("PersonActivity")
//@CssImport("styles/views/dashboard/dashboard-view.css")
public class PersonActivityView extends VerticalLayout implements AfterNavigationObserver /*, HasUrlParameter<String>*/ {

    private UserContext userContext;

    private Grid<PersonActivity> grid;


    @Autowired
    private PersonActivityService personActivityService;


    /*@Override
    public void setParameter(BeforeEvent event, String parameter) {

    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {

    }*/

    public PersonActivityView(@Autowired UserContext userContext) {

        this.userContext = userContext;

        setId("person-activity-view");
        grid = new Grid<PersonActivity>();
        grid.setId("person-activity-list");
        grid.setHeightFull();
        grid.addColumn( pa -> pa.getPerson().getFirstName() )
                .setHeader("Name")
                .setSortable(true);


        final Button addBtn = new Button("Add");

        HorizontalLayout hl = new HorizontalLayout(addBtn);
        hl.setAlignItems(Alignment.CENTER);

        add(
                new H2("Activity "),
                hl,
                grid
        );
    }


    @Override
    public void afterNavigation(AfterNavigationEvent event) {

        add(new Label(userContext + "<br>afterNavigation " + event));

        grid.setItems(personActivityService.findAllByTeamDate(
                userContext.getSelectedTeam(),
                userContext.getSelectedDate()
        ));
        grid.getDataProvider().refreshAll();

    }
}
