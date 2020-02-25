package com.az.lb.views.activity;


import com.az.lb.MainView;
import com.az.lb.UserContext;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import com.vaadin.flow.shared.Registration;
import org.springframework.beans.factory.annotation.Autowired;

@Route(value = "PersonActivity", layout = MainView.class)
//@RouteAlias(value = "PersonActivity", layout = MainView.class)
@PageTitle("PersonActivity")
//@CssImport("styles/views/dashboard/dashboard-view.css")
public class PersonActivity extends VerticalLayout implements AfterNavigationObserver /*, HasUrlParameter<String>*/ {

    private UserContext userContext;


    /*@Override
    public void setParameter(BeforeEvent event, String parameter) {

    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {

    }*/

    public PersonActivity(@Autowired UserContext userContext) {

        this.userContext = userContext;

        setId("activity-view");


        add(new Label(userContext + "Lorem ipsum dolor sit amet, "));
    }


    @Override
    public void afterNavigation(AfterNavigationEvent event) {

        add(new Label(userContext + "<br>afterNavigation " + event));

    }
}
