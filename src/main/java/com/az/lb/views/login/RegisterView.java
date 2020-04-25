package com.az.lb.views.login;

import com.az.lb.model.Registration;
import com.az.lb.repository.RegistrationRepository;
import com.az.lb.servise.mail.MailService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.validator.EmailValidator;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
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
    private Binder<Registration> binderRegistration = new Binder<>();

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


        createValidation();


        submitButton.addClickListener(
                e -> {
                    try {
                        Registration reg = new Registration();
                        binderRegistration.writeBean(reg);
                        createNewRegistration(reg);
                        infoLabel.setText(
                                String.format("Please, check your email to confirm registration of \"%s\"", orgNameTextField.getValue())
                        );
                        submitButton.addClickListener(
                                e2 -> {
                                     UI.getCurrent().navigate(LoginView.class);
                                }
                        );
                        submitButton.setText("Login");
                    } catch (ValidationException ve) {
                        infoLabel.setText(
                                "Validation has failed for some fields"
                        );
                    }

                }
        );
    }

    private String NAME_ERR_MSG = "Name must contain at least three characters. And less than 64";

    private void createValidation() {
        binderRegistration.forField(orgNameTextField).withValidator(
                name -> name.length() >= 3 && name.length() <= 64, NAME_ERR_MSG

        ).bind(Registration::getName, Registration::setName);

        binderRegistration.forField(firstNameTextField).withValidator(
                name -> name.length() >= 3 && name.length() <= 64, NAME_ERR_MSG
        ).bind(Registration::getFirstname, Registration::setFirstname);

        binderRegistration.forField(lastNameTextField).withValidator(
                name -> name.length() >= 3 && name.length() <= 63, NAME_ERR_MSG
        ).bind(Registration::getLastname, Registration::setLastname);

        binderRegistration.forField(userEmailTextField).withValidator(
                new EmailValidator("Please provide correct email")
        ).bind(Registration::getEmail, Registration::setEmail);

        binderRegistration.forField(passwordField).withValidator(
                pwd -> pwd.length() >= 8 ,
                "Name must contain at least 8 characters"
        ).bind(Registration::getPwd, Registration::setPwd);
    }

    private void createNewRegistration(Registration reg) {
        final Optional<Registration> optPrevReg = registrationRepository.findByEmail(userEmailTextField.getValue());
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
    }
}
