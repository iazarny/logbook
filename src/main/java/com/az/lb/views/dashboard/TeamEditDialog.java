package com.az.lb.views.dashboard;

import com.az.lb.views.ConfirmDialog;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.shared.Registration;

public class TeamEditDialog extends Dialog {

    H2 message;
    TextField input;

    Button confirmButton;
    Button cancelButton;

    private Registration cancelListenerRegistration = null;
    private Registration confirmListenerRegistration = null;


    public TeamEditDialog(String title) {
        super();
        setCloseOnEsc(true);
        setCloseOnOutsideClick(true);

        message = new H2(title);
        input = new TextField();
        confirmButton = new Button("New");
        cancelButton = new Button("Cancel");
        HorizontalLayout hl = new HorizontalLayout(
                confirmButton,
                cancelButton
        );
        hl.setAlignItems(FlexComponent.Alignment.END);

        add(new VerticalLayout(
                message,
                input,
                hl
        ));
    }

    public TeamEditDialog message(String message) {
        this.message.setText(message);
        return this;
    }

    public TeamEditDialog onCancel(ComponentEventListener<ClickEvent<Button>> listener) {
        if (cancelListenerRegistration != null) {
            cancelListenerRegistration.remove();
        }
        cancelListenerRegistration = this.cancelButton.addClickListener(listener);
        return this;
    }

    public TeamEditDialog onConfirm(ComponentEventListener<ClickEvent<Button>> listener) {
        if (confirmListenerRegistration != null) {
            confirmListenerRegistration.remove();
        }
        confirmListenerRegistration = this.confirmButton.addClickListener(listener);
        return this;
    }


    public TeamEditDialog teamName(String name) {
        input.setValue(name);
        return this;
    }
}