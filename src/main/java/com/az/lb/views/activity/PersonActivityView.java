package com.az.lb.views.activity;


import com.az.lb.MainView;
import com.az.lb.UserContext;
import com.az.lb.model.Activity;
import com.az.lb.model.PersonActivity;
import com.az.lb.model.PersonActivityDetail;
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
import com.vaadin.flow.component.grid.ColumnTextAlign;
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
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

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
            final List<PersonActivityDetail> details = personActivityDetailService.findActivityDetail(pa);
            final String cellBody;
            if (details.isEmpty()) {
                cellBody = "<div></div>";
            } else {
                cellBody = "<table width='100%' class='detail-table'>" + details.stream()
                        .map( ad -> "<tr class='detail-table-tr'>" +
                                "<td>" + ad.getTask() + "</td>" +
                                "<td>" + StringUtils.truncate(ad.getDetail(), 20) + "</td>" +
                                "<td>" + ad.getSpend() + "</td>" +
                                "</tr>")
                        .collect(Collectors.joining())
                        + "</table>";
            }
            return new Html(cellBody);

        })).setHeader("Detail");

        grid.addColumn(new ComponentRenderer<>(pa -> {
            return new VerticalLayout(
                    new Label(pa.getNote()),
                    new Label(pa.getTags())
            );
        })).setHeader("Notes / Blockers");

        grid.addColumn(new ComponentRenderer<>(pa -> {

            if (StringUtils.isNotBlank(pa.getNote()) || StringUtils.isNotBlank(pa.getTags()) || ArrayUtils.isNotEmpty(pa.getRecord())  ) {
                return new Html("<div></div>");
            }
            Icon delIcon = new Icon(VaadinIcon.MINUS_CIRCLE_O);
            delIcon.addClickListener(
                    e -> {
                        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>");
                    }
            );
            return delIcon;
        }))
                .setWidth("32px")
                .setAutoWidth(true)
                .setTextAlign(ColumnTextAlign.END);

        grid.addItemClickListener(
                ie -> {


                   getUI().get().getCurrent().getPage().retrieveExtendedClientDetails(
                           r -> {

                               personAdtivityDetailDialog.setWidth( ((int)(r.getBodyClientWidth()*0.98)) + "px");
                               personAdtivityDetailDialog.setHeight( ((int)(r.getBodyClientHeight()*0.98)) + "px");
                               personAdtivityDetailDialog
                                       .personActivity(ie.getItem())
                                       .message("Detail activity")
                                       .autoAdd(LocalDate.now().equals(userContext.getSelectedDate()))
                                       .onClose(
                                               e-> {
                                                   personAdtivityDetailDialog.close();
                                                   grid.getDataProvider().refreshAll();
                                               }
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
