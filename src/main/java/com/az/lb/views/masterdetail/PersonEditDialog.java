package com.az.lb.views.masterdetail;

import com.az.lb.model.Person;
import com.az.lb.views.ViewConst;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.HtmlContainer;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.H5;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
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
        nameLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1, FormLayout.ResponsiveStep.LabelsPosition.TOP),
                new FormLayout.ResponsiveStep("300px", 1, FormLayout.ResponsiveStep.LabelsPosition.ASIDE));
        nameLayout.setWidth(ViewConst.DIALOG_WIDTH);

        add(
                message,
                nameLayout
        );

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
