package com.az.lb.views.person;

import com.az.lb.model.Person;
import com.az.lb.views.ViewConst;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.HtmlContainer;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.validator.EmailValidator;
import com.vaadin.flow.shared.Registration;


public class PersonEditDialog extends Dialog {

    private HtmlContainer message;
    private TextField emailTextField;
    private TextField firstNameTextField;
    private TextField lastNameTextField;
    private Checkbox managerCombobox;
    private Checkbox sendInvitation;

    private Button confirmButton;
    private Button cancelButton;

    private Registration cancelListenerRegistration = null;
    private Registration confirmListenerRegistration = null;

    private Label infoLabel = new Label("");

    private Binder<Person> binderPerson = new Binder<>();

    private String NAME_ERR_MSG = "Name must be at least 3 and less than 128 characters";

    private void createValidation() {

        binderPerson.forField(firstNameTextField).withValidator(
                name -> name.length() >= 3 && name.length() <= 128, NAME_ERR_MSG

        ).bind(Person::getFirstName, Person::setFirstName);

        binderPerson.forField(lastNameTextField).withValidator(
                name -> name.length() >= 3 && name.length() <= 128, NAME_ERR_MSG

        ).bind(Person::getLastName, Person::setLastName);

        binderPerson.forField(emailTextField).withValidator(
                new EmailValidator("Please provide correct email")
        ).bind(Person::getEmail, Person::setEmail);

    }


    public PersonEditDialog(String title) {

        super();
        setCloseOnEsc(true);
        setCloseOnOutsideClick(true);

        message = new H4(title);
        emailTextField = new TextField();
        firstNameTextField = new TextField();
        lastNameTextField = new TextField();
        confirmButton = new Button("New");
        cancelButton = new Button("Cancel");
        managerCombobox = new Checkbox();
        sendInvitation = new Checkbox();

        final FlexLayout cancelButtonWrapper = new FlexLayout(cancelButton);
        cancelButtonWrapper.setJustifyContentMode(FlexComponent.JustifyContentMode.END);

        final FlexLayout confirmButtonWrapper = new FlexLayout(confirmButton);
        confirmButtonWrapper.setJustifyContentMode(FlexComponent.JustifyContentMode.END);

        HorizontalLayout hl = new HorizontalLayout(
                confirmButtonWrapper,
                cancelButtonWrapper
        );
        hl.setWidthFull();
        hl.expand(confirmButtonWrapper, cancelButtonWrapper);

        FormLayout nameLayout = new FormLayout();
        nameLayout.addFormItem( firstNameTextField, "First name");
        nameLayout.addFormItem(lastNameTextField,"Last name");
        nameLayout.addFormItem(emailTextField,"Email");
        nameLayout.addFormItem(managerCombobox, "Manager");
        nameLayout.addFormItem(sendInvitation, "Send invitation");
        nameLayout.add(hl);
        nameLayout.add(infoLabel);
        nameLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1, FormLayout.ResponsiveStep.LabelsPosition.TOP),
                new FormLayout.ResponsiveStep("300px", 1, FormLayout.ResponsiveStep.LabelsPosition.ASIDE));
        nameLayout.setWidth(ViewConst.DIALOG_WIDTH);

        add(
                message,
                nameLayout
        );

        createValidation();

    }

    public PersonEditDialog message(String message) {
        this.message.setText(message);
        return this;
    }

    public PersonEditDialog email(String email) {
        this.emailTextField.setValue(email);
        return this;
    }

    public PersonEditDialog firstName(String firstName) {
        this.firstNameTextField.setValue(firstName);
        return this;
    }

    public PersonEditDialog lastName(String lastName) {
        this.lastNameTextField.setValue(lastName);
        return this;
    }

    public PersonEditDialog manager(Boolean manager) {
        this.managerCombobox.setValue(manager);
        return this;
    }

    public PersonEditDialog person(Person person) {
        emailTextField.setValue(person.getEmail());
        firstNameTextField.setValue(person.getFirstName());
        lastNameTextField.setValue(person.getLastName());
        managerCombobox.setValue(person.getOrgManager());
        return this;
    }

    public PersonEditDialog onCancel(ComponentEventListener<ClickEvent<Button>> listener) {
        if (cancelListenerRegistration != null) {
            cancelListenerRegistration.remove();
        }
        cancelListenerRegistration = this.cancelButton.addClickListener(listener);
        return this;
    }

    public PersonEditDialog onConfirm(ComponentEventListener<ClickEvent<Button>> listener) {

        ComponentEventListener<ClickEvent<Button>> wrapper = new ComponentEventListener<ClickEvent<Button>>() {

            ComponentEventListener<ClickEvent<Button>> wraped = listener;

            @Override
            public void onComponentEvent(ClickEvent<Button> event) {
                Person person = new Person();
                try {
                    binderPerson.writeBean(person);
                    wraped.onComponentEvent(event);
                } catch (ValidationException e) {
                    infoLabel.setText(
                            "Validation has failed"
                    );
                }


            }
        };


        if (confirmListenerRegistration != null) {
            confirmListenerRegistration.remove();
        }
        confirmListenerRegistration = this.confirmButton.addClickListener(wrapper);
        return this;
    }

    public boolean isSendInvitation() {
        return sendInvitation.getValue();
    }

    public String getEmai() {
        return emailTextField.getValue();
    }

    public String getFirstName() {
        return firstNameTextField.getValue();
    }

    public String getLastName() {
        return lastNameTextField.getValue();
    }

    public boolean isManager() {
        return managerCombobox.getValue();
    }
}
