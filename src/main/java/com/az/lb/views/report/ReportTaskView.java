package com.az.lb.views.report;


import com.az.lb.MainView;
import com.az.lb.UserContext;
import com.az.lb.model.PersonActivityDetail;
import com.az.lb.servise.PersonActivityDetailService;
import com.az.lb.servise.PersonActivityService;
import com.az.lb.servise.TeamService;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.H5;
import com.vaadin.flow.component.html.H6;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;

import java.time.LocalDate;
import java.util.List;

@Route(value = "TaskReport", layout = MainView.class)
@RouteAlias(value = "TaskReport", layout = MainView.class)
@PageTitle("Task report")
@CssImport("styles/views/dashboard/dashboard-view.css")
@Secured({"ADM"})
public class ReportTaskView extends VerticalLayout implements AfterNavigationObserver {

    @Autowired
    private TeamService teamService;

    @Autowired
    private PersonActivityService personActivityService;

    @Autowired
    private PersonActivityDetailService personActivityDetailService;

    private final UserContext userContext;

    private final DatePicker fromDatePicker;

    private final DatePicker tillDatePicker;

    private final Button confirmButton;

    public ReportTaskView(@Autowired UserContext userContext) {
        super();
        this.userContext = userContext;
        this.fromDatePicker = new DatePicker(LocalDate.now().minusDays(7));
        this.tillDatePicker = new DatePicker(LocalDate.now());
        this.confirmButton = new Button("Generate");
        configureHandlers();

        HorizontalLayout fl = new HorizontalLayout();
        fl.add(new H6( "From"));
        fl.add(fromDatePicker);
        fl.add(new H6( "Till"));
        fl.add(tillDatePicker);
        fl.add(confirmButton);

        //fl.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 4, FormLayout.ResponsiveStep.LabelsPosition.ASIDE));
        add(
                new H4("Tasks report."),
                fl,
                new Html("<hr/>")
        );
    }



    public void configureHandlers() {
        confirmButton.addClickListener(
                e -> {

                    String str = personActivityDetailService.findAllFromTillDateAsHtmlTable(
                            userContext.getOrg(), fromDatePicker.getValue(), tillDatePicker.getValue());

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
        //
    }
}
