package com.az.lb.views;


import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.shared.Registration;

public class ConfirmDialog extends Dialog {

    public Button confirmButton;
    public Button cancelButton;

    private H2 title;
    private Label message;

    private Registration cancelListenerRegistration = null;
    private Registration confirmListenerRegistration = null;

    public ConfirmDialog(String title, String message) {

        super();

        setCloseOnEsc(true);
        setCloseOnOutsideClick(true);

        this.confirmButton = new Button("Confirm");
        this.cancelButton = new Button("Cancel");
        this.title = new H2(title);
        this.message = new Label(message);

        this.cancelButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        HorizontalLayout hl = new HorizontalLayout(
                this.confirmButton,
                this.cancelButton);
        hl.setAlignItems(FlexComponent.Alignment.END);

        add( new VerticalLayout(
                this.title,
                this.message,
                hl
        ));
    }

    public ConfirmDialog title(String text) {
        this.title.setText(text);
        return this;
    }

    public ConfirmDialog message(String text) {
        this.message.setText(text);
        return this;
    }

    public ConfirmDialog onCancel(ComponentEventListener<ClickEvent<Button>> listener) {
        if (cancelListenerRegistration != null) {
            cancelListenerRegistration.remove();
        }
        cancelListenerRegistration = this.cancelButton.addClickListener(listener);
        return this;
    }

    public ConfirmDialog onConfirm(ComponentEventListener<ClickEvent<Button>> listener) {
        if (confirmListenerRegistration != null) {
            confirmListenerRegistration.remove();
        }
        confirmListenerRegistration = this.confirmButton.addClickListener(listener);
        return this;
    }
}
