package com.az.lb.views.activity;

import com.az.lb.model.PersonActivity;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.HtmlContainer;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H5;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.shared.Registration;

public class PersonActivityNoteDialog extends Dialog {



    HtmlContainer message;
    TextArea notes;
    TextField tags;

    Button confirmButton;
    Button cancelButton;

    private Registration cancelListenerRegistration = null;
    private Registration confirmListenerRegistration = null;

    public PersonActivityNoteDialog() {
        super();
        setCloseOnEsc(true);
        setCloseOnOutsideClick(true);

        message = new H5("Notes");
        notes = new TextArea("Notes");
        notes.setHeight("300px");
        notes.setWidth("400px");

        tags= new TextField("Tags");
        tags.setWidth("400px");

        confirmButton = new Button("New");
        cancelButton = new Button("Cancel");

        final FlexLayout cancelButtonWrapper = new FlexLayout(cancelButton);
        cancelButtonWrapper.setJustifyContentMode(FlexComponent.JustifyContentMode.END);

        HorizontalLayout hl = new HorizontalLayout(
                confirmButton,
                cancelButtonWrapper
        );
        hl.setWidthFull();
        hl.expand(cancelButtonWrapper);

        add(new VerticalLayout(
                message,
                notes,
                tags,
                hl
        ));
    }


    public PersonActivityNoteDialog message(String message) {
        this.message.setText(message);
        return this;
    }

    public PersonActivityNoteDialog onCancel(ComponentEventListener<ClickEvent<Button>> listener) {
        if (cancelListenerRegistration != null) {
            cancelListenerRegistration.remove();
        }
        cancelListenerRegistration = this.cancelButton.addClickListener(listener);
        return this;
    }

    public PersonActivityNoteDialog onConfirm(ComponentEventListener<ClickEvent<Button>> listener) {
        if (confirmListenerRegistration != null) {
            confirmListenerRegistration.remove();
        }
        confirmListenerRegistration = this.confirmButton.addClickListener(listener);
        return this;
    }


    public PersonActivityNoteDialog confirmText(String confirmText) {
        confirmButton.setText(confirmText);
        return this;
    }

    public PersonActivityNoteDialog notes(String value) {
        this.notes.setValue(value);
        return this;
    }

    public PersonActivityNoteDialog tags(String value) {
        this.tags.setValue(value);
        return this;
    }

    public String getNotes() {
        return notes.getValue();
    }

    public String getTags() {
        return tags.getValue();
    }
}
