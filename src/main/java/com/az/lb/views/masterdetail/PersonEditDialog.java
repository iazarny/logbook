package com.az.lb.views.masterdetail;

import com.az.lb.model.Person;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.shared.Registration;


public class PersonEditDialog extends Dialog {

    private H2 message;
    private TextField emailTextField;
    private TextField firstNameTextField;
    private TextField lastNameTextField;
    private Checkbox managerCombobox;

    private Button confirmButton;
    private Button cancelButton;

    private Registration cancelListenerRegistration = null;
    private Registration confirmListenerRegistration = null;


    public PersonEditDialog(String title) {

        super();
        setCloseOnEsc(true);
        setCloseOnOutsideClick(true);

        message = new H2(title);
        emailTextField = new TextField("Email");
        firstNameTextField = new TextField("First name");
        lastNameTextField = new TextField("Last name");
        confirmButton = new Button("New");
        cancelButton = new Button("Cancel");
        managerCombobox = new Checkbox("Manager");

        HorizontalLayout hl = new HorizontalLayout(
                confirmButton,
                cancelButton
        );

        FormLayout nameLayout = new FormLayout();

        nameLayout.add(firstNameTextField);
        nameLayout.add(lastNameTextField);
        nameLayout.add(emailTextField);
        nameLayout.add(managerCombobox);
        nameLayout.add(hl);

        add(nameLayout);

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
        if (confirmListenerRegistration != null) {
            confirmListenerRegistration.remove();
        }
        confirmListenerRegistration = this.confirmButton.addClickListener(listener);
        return this;
    }


}
