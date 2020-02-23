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

@Route(value = "Activity", layout = MainView.class)
//@RouteAlias(value = "", layout = MainView.class)
@PageTitle("Activity")
//@CssImport("styles/views/dashboard/dashboard-view.css")
public class ActivityView extends VerticalLayout implements AfterNavigationObserver, HasUrlParameter<String> {

    private UserContext userContext;


    @Override
    public void setParameter(BeforeEvent event, String parameter) {

    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {

    }

    public ActivityView(@Autowired UserContext userContext) {

        this.userContext = userContext;

        setId("activity-view");


        add(new Label("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed " +
                "do eiusmod tempor incididunt ut labore et dolore magna aliqua. Mauris pharetra et ultrices neque " +
                "ornare aenean euismod. Purus in mollis nunc sed id. Vitae elementum curabitur vitae nunc sed. Morbi non arcu risus" +
                " quis varius quam quisque id. Blandit libero volutpat sed cras ornare. Nec feugiat in fermentum posuere urna nec. Elementum" +
                " eu facilisis sed odio morbi quis commodo odio aenean. Feugiat in ante metus dictum at tempor. Lectus quam id leo in vitae " +
                "turpis. Tempor id eu nisl nunc mi ipsum faucibus vitae. Viverra orci sagittis eu volutpat odio facilisis mauris " +
                "sit. Arcu bibendum at varius vel pharetra vel turpis nunc eget. Vel pretium lectus quam id leo in vitae turpis" +
                " massa. Libero volutpat sed cras ornare arcu dui vivamus arcu." +
                " Nibh tellus molestie nunc non blandit massa enim nec. Egestas egestas fringilla phasellus faucibus scelerisque " +
                "eleifend donec pretium vulputate."));
    }


}
