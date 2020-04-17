package com.az.lb.views.login;

import com.az.lb.servise.PersonService;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;


@Route(value = LoginView.ROUTE)
@PageTitle("Login")
@NpmPackage(value = "@polymer/iron-form", version = "3.0.1")
@JsModule("@polymer/iron-form/iron-form.js")

public class LoginView extends HorizontalLayout {

    public static final String ROUTE = "login";

    @Autowired
    private PersonService personService;

    private TextField userNameTextField = new TextField("Name");
    private PasswordField passwordField = new PasswordField("Password");
    private Button submitButton = new Button("Login");
    private Button forgotPasswordButton = new Button("Forgot password");
    private Button registerOrganizationButton = new Button("Register organization");

    public LoginView() {

        userNameTextField.getElement().setAttribute("name", "username");
        userNameTextField.addInputListener(event -> {
            forgotPasswordButton.setEnabled(true);
        });

        passwordField.getElement().setAttribute("name", "password");

        submitButton.setId("submitbutton");
        submitButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        forgotPasswordButton.setId("forgotbutton");
        forgotPasswordButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        forgotPasswordButton.setEnabled(false);

        registerOrganizationButton.setId("registedbutton");
        registerOrganizationButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        final FlexLayout linksWrapper = new FlexLayout(forgotPasswordButton, registerOrganizationButton);
        linksWrapper.setJustifyContentMode(JustifyContentMode.CENTER);


        UI.getCurrent().getPage().executeJs(
                "document.getElementById('submitbutton').addEventListener('click', () => document.getElementById('ironform').submit());");

        FormLayout formLayout = new FormLayout(
                new H3("Log book"),
                userNameTextField,
                passwordField,
                submitButton,
                linksWrapper);

        this.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        this.setJustifyContentMode(JustifyContentMode.CENTER);

        Element formElement = new Element("form");
        formElement.setAttribute("method", "post");
        formElement.setAttribute("action", "login");
        formElement.appendChild(formLayout.getElement());

        Element ironForm = new Element("iron-form");
        ironForm.setAttribute("id", "ironform");
        ironForm.setAttribute("allow-redirect", true);
        ironForm.appendChild(formElement);

        getElement().appendChild(ironForm);

        ironForm.appendChild(formElement);

        formLayout.setWidth("360px");
        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1, FormLayout.ResponsiveStep.LabelsPosition.TOP),
                new FormLayout.ResponsiveStep("600px", 1, FormLayout.ResponsiveStep.LabelsPosition.ASIDE));

        setClassName("login-view");

        //this.getStyle ().set ( "border" , "6px dotted green" );
        setSizeFull();


        forgotPasswordButton.addClickListener(
                e -> {
                    if (personService.forgotPassword(userNameTextField.getValue())) {
                        Notification.show(
                                "Reset password mail has been sent",
                                4000,
                                Notification.Position.TOP_CENTER
                        );
                    }
                }
        );

        registerOrganizationButton.addClickListener(
                e -> {
                    UI.getCurrent().navigate(RegisterView.ROUTE);
                }
        );

        passwordField.addKeyPressListener(Key.ENTER,  e -> {
            UI.getCurrent().getPage().executeJs(
                    "document.getElementById('ironform').submit();");

        });


    }

}
