package com.az.lb.views.report;


import com.az.lb.MainView;
import com.az.lb.UserContext;
import com.az.lb.model.Person;
import com.az.lb.servise.PersonActivityDetailService;
import com.az.lb.servise.PersonService;
import com.az.lb.servise.ReportingService;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.H6;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;

import java.time.LocalDate;

@Route(value = "PersonReport", layout = MainView.class)
@RouteAlias(value = "PersonReport", layout = MainView.class)
@PageTitle("Person activity report")
@CssImport("styles/views/reports/reports.css")
@Secured({"ADM"})
public class ReportPersonView extends VerticalLayout implements AfterNavigationObserver {

    @Autowired
    private PersonService personService;

    @Autowired
    private ReportingService reportingService;

    @Autowired
    private PersonActivityDetailService personActivityDetailService;

    private final UserContext userContext;

    private final ComboBox<Person> persons;

    private final DatePicker fromDatePicker;

    private final DatePicker tillDatePicker;

    private final Button confirmButton;

    public ReportPersonView(@Autowired UserContext userContext) {
        super();
        this.userContext = userContext;
        this.fromDatePicker = new DatePicker( LocalDate.now().minusDays(7));
        this.tillDatePicker = new DatePicker( LocalDate.now());
        this.confirmButton = new Button("Generate");
        this.confirmButton.setEnabled(false);
        this.persons = new ComboBox<>();
        this.persons.setItemLabelGenerator(Person::getFullName);

        configureHandlers();
        HorizontalLayout fl = new HorizontalLayout();
        fl.add(new H6( "Person"));
        fl.add(persons);
        fl.add(new H6( "From"));
        fl.add(fromDatePicker);
        fl.add(new H6( "Till"));
        fl.add(tillDatePicker);
        fl.add(confirmButton);
        //fl.setResponsiveSteps(
          //      new FormLayout.ResponsiveStep("0", 4, FormLayout.ResponsiveStep.LabelsPosition.ASIDE));
        add(
                new H4("Person activity report."),
                fl,
                new Html("<hr/>")
        );
    }



    public void configureHandlers() {

        persons.addValueChangeListener(
                e -> {
                    confirmButton.setEnabled(true);
                }
        );

        confirmButton.addClickListener(
                e -> {

                    String str =  reportingService.findAllFromTillDateAsHtmlTable(
                            userContext.getOrg(),
                            persons.getValue(),
                            fromDatePicker.getValue(),
                            tillDatePicker.getValue()
                    );

                    add(
                            new Html(
                                    "<div>" +
                                            str +
                                            "</dv>")
                    );

                }
        );
    }




    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        this.persons.setItems(
                personService.findAll(userContext.getOrg())
        );
    }
}
