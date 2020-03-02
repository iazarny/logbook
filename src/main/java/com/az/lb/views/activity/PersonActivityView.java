package com.az.lb.views.activity;


import com.az.lb.MainView;
import com.az.lb.UserContext;
import com.az.lb.model.Activity;
import com.az.lb.model.PersonActivity;
import com.az.lb.model.Team;
import com.az.lb.servise.PersonActivityDetailService;
import com.az.lb.servise.PersonActivityService;
import com.az.lb.servise.PersonService;
import com.az.lb.servise.TeamService;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.function.ValueProvider;
import com.vaadin.flow.router.*;
import com.vaadin.flow.shared.Registration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

@Route(value = "PersonActivity", layout = MainView.class)
//@RouteAlias(value = "PersonActivity", layout = MainView.class)
@PageTitle("PersonActivity")
@CssImport("styles/views/personactivity/person-activity.css")
public class PersonActivityView extends VerticalLayout implements AfterNavigationObserver /*, HasUrlParameter<String>*/ {

    @Autowired
    private UserContext userContext;

    @Autowired
    private PersonService personService;

    @Autowired
    private PersonActivityDetailService personActivityDetailService;

    private Grid<PersonActivity> grid;

    private PersonAdtivityDetailDialog personAdtivityDetailDialog;

    @Autowired
    private PersonActivityService personActivityService;



    public PersonActivityView(@Autowired UserContext userContext,
                              @Autowired PersonService personService,
                              @Autowired PersonActivityDetailService personActivityDetailService) {

        this.userContext = userContext;
        this.personService = personService;
        this.personActivityDetailService = personActivityDetailService;

        setId("person-activity-view");



        personAdtivityDetailDialog = new PersonAdtivityDetailDialog(
                userContext,
                personService,
                personActivityDetailService);
        add(personAdtivityDetailDialog);

        grid = new Grid<PersonActivity>();
        grid.setId("person-activity-list");
        grid.setHeightFull();
        grid.addColumn( pa -> pa.getPerson().getFullName() )
                .setHeader("Name")
                .setSortable(true);


        grid.addColumn(new ComponentRenderer<>(pa -> {
            return new Html("<table border='1' width='100%'><tr><td>bla-bla</td><td>bla-bla</td></tr><tr><td>bla-bla</td><td>bla-bla</td></tr></table>");
        })).setHeader("Detail");

        grid.addColumn(new ComponentRenderer<>(pa -> {
            return new VerticalLayout(
                    new Label(pa.getNote()),
                    new Label(pa.getTags())
            );
        })).setHeader("Notes / Blockers");

        grid.addColumn(new ComponentRenderer<>(pa -> {
            Icon delIcon = new Icon(VaadinIcon.DEL_A);
            delIcon.addClickListener(
                    e -> {
                        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>");

                    }
            );




            return delIcon;
        }));

        grid.addItemClickListener(
                ie -> {


                   getUI().get().getCurrent().getPage().retrieveExtendedClientDetails(
                           r -> {

                               personAdtivityDetailDialog.setWidth( ((int)(r.getBodyClientWidth()*0.98)) + "px");
                               personAdtivityDetailDialog.setHeight( ((int)(r.getBodyClientHeight()*0.98)) + "px");
                               personAdtivityDetailDialog
                                       .personActivity(ie.getItem())
                                       .message("Detail activity")
                                       .onClose(
                                               e-> {personAdtivityDetailDialog.close();}
                                       )
                                       .open();
                           }
                   );


                }
        );

        final Button addBtn = new Button("Add");

        HorizontalLayout hl = new HorizontalLayout(addBtn);
        hl.setAlignItems(Alignment.CENTER);

        add(
                new H2("Activity "),
                hl
        );

        add(grid);


    }


    @Override
    public void afterNavigation(AfterNavigationEvent event) {

        add(new Label(userContext + "<br>afterNavigation " + event));

        Activity act = personActivityService.createPersonsActivitySheet(userContext.getSelectedTeam(),
                userContext.getSelectedDate());

        List<PersonActivity>data =  personActivityService.findAllByActivity(act);

        grid.setItems(data);
        grid.getDataProvider().refreshAll();



    }
}
