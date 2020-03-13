package com.az.lb.views.report;


import com.az.lb.MainView;
import com.az.lb.UserContext;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import org.springframework.beans.factory.annotation.Autowired;

@Route(value = "TaskReport", layout = MainView.class)
@RouteAlias(value = "TaskReport", layout = MainView.class)
@PageTitle("Task report")
@CssImport("styles/views/dashboard/dashboard-view.css")
public class ReportTaskView extends VerticalLayout implements AfterNavigationObserver {

    private final UserContext userContext;

    public ReportTaskView(@Autowired UserContext userContext) {
        super();
        this.userContext = userContext;
        add(
                new Label("Task report")
        );
    }


    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        add(
                new Label(userContext.toString())
        );
    }
}
