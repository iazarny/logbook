package com.az.lb.views.org;


import com.az.lb.MainView;
import com.az.lb.UserContext;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import org.springframework.beans.factory.annotation.Autowired;

@Route(value = "OrganizationSettings", layout = MainView.class)
@RouteAlias(value = "OrganizationSettings", layout = MainView.class)
@PageTitle("Organization Settings")
@CssImport("styles/views/dashboard/dashboard-view.css")
public class OrganizationSettingView extends VerticalLayout implements AfterNavigationObserver {

    private final UserContext userContext;

    public OrganizationSettingView(@Autowired UserContext userContext) {
        super();
        this.userContext = userContext;
        add(
                new Label("Organization Settings")
        );
    }


    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        add(
                new Label(userContext.toString())
        );
    }
}
