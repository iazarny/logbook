package com.az.lb.views.report;


import com.az.lb.MainView;
import com.az.lb.UserContext;
import com.az.lb.model.PersonActivity;
import com.az.lb.servise.PersonActivityDetailService;
import com.az.lb.servise.PersonActivityService;
import com.az.lb.servise.TeamService;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.button.Button;
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
import java.util.List;

@Route(value = "DailyReport", layout = MainView.class)
@RouteAlias(value = "DailyReport", layout = MainView.class)
@PageTitle("Daily report")
@CssImport("styles/views/reports/reports.css")
@Secured({"ADM"})
public class ReportDailyView extends VerticalLayout implements AfterNavigationObserver {

    @Autowired
    private TeamService teamService;

    @Autowired
    private PersonActivityService personActivityService;

    @Autowired
    private PersonActivityDetailService personActivityDetailService;

    private final UserContext userContext;

    private final DatePicker labelDatePicker;

    private final Button confirmButton;

    private final VerticalLayout reportBody;


    public ReportDailyView(@Autowired UserContext userContext) {
        super();
        this.userContext = userContext;
        this.labelDatePicker = new DatePicker( LocalDate.now());
        this.confirmButton = new Button("Generate");
        this.reportBody = new VerticalLayout();
        reportBody.getStyle().set("overflow", "auto");
        reportBody.setWidth("99%");
        reportBody.setHeight("70vh");

        HorizontalLayout fl = new HorizontalLayout();
        fl.add(new H6("Date"));
        fl.add(labelDatePicker);
        fl.add(confirmButton);

        configureHandlers();

        add(
                new H4("Daily report."),
                fl,
                new Html("<hr/>"),
                reportBody
        );

    }

    String str = "";

    public void configureHandlers() {

        str = "";

        this.confirmButton.addClickListener(
                e -> {


                    teamService.findTeams(userContext.getOrg())
                            .forEach(
                                    t -> {
                                        str += "<h4>" +t.getName() + " " + labelDatePicker.getValue() + "</h4><hr/>";

                                        List<PersonActivity> pal = personActivityService.findAllByTeamDate(t, labelDatePicker.getValue());


                                        pal.forEach(
                                                pa -> {
                                                    str += " " + pa.getPerson().getFullName();

                                                    str += personActivityService.getDetailsAsHtmlTable(
                                                            personActivityDetailService.findActivityDetail(pa),
                                                            pa.getNote(), pa.getTags(), true
                                                    );
                                                }
                                        );



                                    }
                            );

                    str = str.replace("'detail-table'", "'detail-table-report'");




                    reportBody.add(
                            new Html(
                                    "<div" +
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
