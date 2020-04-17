package com.az.lb.security;

import com.az.lb.views.login.InvitationConfirmView;
import com.az.lb.views.login.LoginView;
import com.az.lb.views.login.RegisterConfirmView;
import com.az.lb.views.login.RegisterView;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.NotFoundException;
import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.VaadinServiceInitListener;
import org.springframework.stereotype.Component;

@Component
public class ConfigureUIServiceInitListener implements VaadinServiceInitListener {

    @Override
    public void serviceInit(ServiceInitEvent event) {
        event.getSource().addUIInitListener(uiEvent -> {
            final UI ui = uiEvent.getUI();
            ui.addBeforeEnterListener(this::beforeEnter);
        });
    }

    /**
     * Reroutes the user if (s)he is not authorized to access the view.
     *
     * @param event
     *            before navigation event with event details
     */
    private void beforeEnter(BeforeEnterEvent event) {


        if (InvitationConfirmView.class.equals(event.getNavigationTarget())) {
            return;
        }

        if (RegisterConfirmView.class.equals(event.getNavigationTarget())) {
            return;
        }

        if (RegisterView.class.equals(event.getNavigationTarget())) {
            return;
        }

        if (LoginView.class.equals(event.getNavigationTarget())) {
            return;
        }

        if (!SecurityUtils.isUserLoggedIn()) {
            event.rerouteTo(LoginView.class);
        }

        if(!SecurityUtils.isAccessGranted(event.getNavigationTarget())) {
            event.rerouteToError(NotFoundException.class);
        }

    }
}