package com.az.lb.views.login;

import com.az.lb.model.Registration;
import com.az.lb.repository.RegistrationRepository;
import com.az.lb.servise.PersonService;
import com.az.lb.servise.mail.MailSendJob;
import com.az.lb.views.dashboard.TeamView;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.UUID;

@Route(value = ChangePasswordView.ROUTE)
@PageTitle("Change password")
public class ChangePasswordView extends HorizontalLayout implements BeforeEnterObserver {

    public static final String ROUTE = "change-password";

    private static final Logger logger = LoggerFactory.getLogger(ChangePasswordView.class);

    @Autowired
    private PersonService personService;


    @Autowired
    private RegistrationRepository registrationRepository;

    private Label infoLabel = new Label();
    private H3 headLabel = new H3();
    private PasswordField passwordField = new PasswordField();
    private PasswordField confirmPasswordField = new PasswordField();
    private Button submitButton = new Button("Go to main page");
    private Binder<LoginPasswordBean> changePasswordBinder = new Binder<>();

    public ChangePasswordView() {

        final FlexLayout submitButtonWrapper = new FlexLayout(submitButton);
        submitButtonWrapper.setJustifyContentMode(JustifyContentMode.CENTER);

        FormLayout formLayout = new FormLayout();
        formLayout.add(headLabel);
        formLayout.addFormItem(passwordField, "Password");
        formLayout.addFormItem(confirmPasswordField, "Confirm");
        formLayout.add(submitButtonWrapper);
        formLayout.add(infoLabel);

        submitButton.setMaxWidth("180pt");

        submitButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        formLayout.setWidth("360px");

        setClassName("pwd-view");

        Element formElement = new Element("form");
        formElement.appendChild(formLayout.getElement());

        Element ironForm = new Element("iron-form");
        ironForm.appendChild(formElement);

        getElement().appendChild(ironForm);

        setSizeFull();
        this.setDefaultVerticalComponentAlignment(Alignment.CENTER);
        this.setJustifyContentMode(JustifyContentMode.CENTER);

        createValidation();


    }

    private void createValidation() {
        changePasswordBinder.forField(passwordField).withValidator(
                pwd -> pwd.length() >= 8, "Please provide password with at least 8 chars"

        ).bind(LoginPasswordBean::getLogin, LoginPasswordBean::setLogin);
        changePasswordBinder.forField(confirmPasswordField)
                .withValidator(
                        pwd -> pwd.length() >= 8, "Please provide password with at least 8 chars"
                )
                .withValidator(
                        pwd -> pwd.equals(passwordField.getValue()), "Password and confirmation must be equals"

                ).bind(LoginPasswordBean::getPassword, LoginPasswordBean::setPassword);
    }


    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        final List<String> regreq = event.getLocation().getQueryParameters().getParameters().get("regreq");
        try {
            Registration optReg = registrationRepository.findById(UUID.fromString(regreq.get(0))).get();
            passwordField.setEnabled(true);
            confirmPasswordField.setEnabled(true);
            headLabel.setText("Please provide password");
            submitButton.setText("Change password");
            submitButton.addClickListener(e -> {

                try {
                    changePasswordBinder.writeBean(new LoginPasswordBean());

                    personService.acceptInvitation(optReg.getId().toString(), confirmPasswordField.getValue());
                    headLabel.setText("Password is changed, try your login");
                    submitButton.setText("Log book");
                    submitButton.addClickListener(ep -> UI.getCurrent().navigate(LoginView.class));

                } catch (ValidationException ve) {
                    headLabel.setText("Please provide password and confirmation with at least 8 char length");
                }

            });
        } catch (Exception ex) {
            logger.error("Cannot find registration for " + regreq, ex);
            infoLabel.setText("No invitation information is provided. Ask for invitation one more time, please.");
            submitButton.setText("Home");
            submitButton.addClickListener(e -> UI.getCurrent().navigate(LoginView.class));
        }
    }

}
