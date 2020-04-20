package com.az.lb.views.login;

import com.az.lb.model.Registration;
import com.az.lb.repository.RegistrationRepository;
import com.az.lb.servise.mail.MailService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Route(value = RegisterView.ROUTE)
@PageTitle("Register")
public class RegisterView extends HorizontalLayout {

    public static final String ROUTE = "register";

    @Autowired
    private RegistrationRepository registrationRepository;

    @Autowired
    private MailService mailService;

    @Value("${lb.registration.callbackurl}")
    private String lbRegistrationCallbackurl;


    private TextField orgNameTextField = new TextField("Organization name");
    private TextField firstNameTextField = new TextField("First name");
    private TextField lastNameTextField = new TextField("Last name");
    private TextField userEmailTextField = new TextField("Email");
    private PasswordField passwordField = new PasswordField("Password");
    private Button submitButton = new Button("Create");
    private Label infoLabel = new Label("");

    public RegisterView() {

        FormLayout formLayout = new FormLayout(
                new H3("Log book"),
                orgNameTextField,
                firstNameTextField,
                lastNameTextField,
                userEmailTextField,
                passwordField,
                submitButton);
        formLayout.add(infoLabel);


        submitButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);


        formLayout.setWidth("360px");
        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1, FormLayout.ResponsiveStep.LabelsPosition.TOP),
                new FormLayout.ResponsiveStep("600px", 1, FormLayout.ResponsiveStep.LabelsPosition.ASIDE));

        setClassName("register-view");

        Element formElement = new Element("form");
        formElement.appendChild(formLayout.getElement());

        Element ironForm = new Element("iron-form");
        ironForm.appendChild(formElement);

        getElement().appendChild(ironForm);

        setSizeFull();
        this.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        this.setJustifyContentMode(JustifyContentMode.CENTER);


        submitButton.addClickListener(
                e -> {

                    final Optional<Registration> optPrevReg = registrationRepository.findByEmail(userEmailTextField.getValue());
                    Registration reg = new Registration();
                    reg.setEmail(userEmailTextField.getValue());
                    reg.setName(orgNameTextField.getValue());
                    reg.setPwd(passwordField.getValue());
                    reg.setFirstname(firstNameTextField.getValue());
                    reg.setLastname(lastNameTextField.getValue());
                    if (optPrevReg.isPresent()) {
                        reg.setId(optPrevReg.get().getId());
                    }
                    reg = registrationRepository.save(reg);

                    Map<String, Object> data = new HashMap<>();
                    data.put("email", reg.getEmail());
                    data.put("orgName", reg.getName());
                    data.put("firstName", reg.getFirstname());
                    data.put("lastName", reg.getLastname());
                    data.put("callbackkey", reg.getId().toString());
                    data.put("callbackurl", lbRegistrationCallbackurl);
                    data.put("callbackhit", lbRegistrationCallbackurl+ "?regreq=" + reg.getId().toString());




                    mailService.send(
                            MailService.MAIL_REGISTER,
                            userEmailTextField.getValue(),
                            "Register a new organization in the Log Book ",
                            data,
                            null //todo
                    );

                    infoLabel.setText(
                            String.format("Please, check your email to confirm registration of \"%s\"", orgNameTextField.getValue())
                    );

                }
        );
    }
}
