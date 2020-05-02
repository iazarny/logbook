package com.az.lb.views.login;

import com.az.lb.model.Org;
import com.az.lb.model.Registration;
import com.az.lb.repository.RegistrationRepository;
import com.az.lb.servise.OrgService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.router.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Route(value = RegisterConfirmView.ROUTE)
@PageTitle("Register confirmation")
public class RegisterConfirmView extends HorizontalLayout implements BeforeEnterObserver {

    public static final String ROUTE = "register-confirm";

    @Autowired
    private RegistrationRepository registrationRepository;

    @Autowired
    private OrgService orgService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Label infoLabel = new Label();
    private Button submitButton = new Button("Go to main page");

    public RegisterConfirmView() {
        FormLayout formLayout = new FormLayout(
                new H3("Log book"),
                infoLabel,
                submitButton);

        submitButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        formLayout.setWidth("360px");

        setClassName("register-view");

        Element formElement = new Element("form");
        formElement.appendChild(formLayout.getElement());

        Element ironForm = new Element("iron-form");
        ironForm.appendChild(formElement);

        getElement().appendChild(ironForm);

        setSizeFull();
        this.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        this.setJustifyContentMode(JustifyContentMode.CENTER);


    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        final List<String> regreq = event.getLocation().getQueryParameters().getParameters().get("regreq");
        if (CollectionUtils.isEmpty(regreq)) {
            infoLabel.setText("No registration information is provided. Register one more time, please.");
            submitButton.setText("Go to registration");
            submitButton.addClickListener(e -> UI.getCurrent().navigate(RegisterView.class) );
        } else {

            String id = regreq.get(0);

            Optional<Registration> optReg = registrationRepository.findById(UUID.fromString(id));

            if(optReg.isPresent()) {
                Registration registration = optReg.get();
                String encodedPwd = passwordEncoder.encode(registration.getPwd());
                Org org = orgService.createNewOrganiation(
                        registration.getName(),
                        registration.getEmail(),
                        registration.getFirstname(),
                        registration.getLastname(),
                        encodedPwd);
                registrationRepository.delete(registration);
                infoLabel.setText("Registration is confirmed. After login you can add people, teams ans start track activity");
                submitButton.setText("Open log book.");
                submitButton.addClickListener(e -> UI.getCurrent().navigate(LoginView.class) );
            } else {
                infoLabel.setText("Registration link is expired, sorry. Register one more time, please.");
                submitButton.setText("Go to registration");
                submitButton.addClickListener(e -> UI.getCurrent().navigate(LoginView.class) );
            }
        }

    }

}
