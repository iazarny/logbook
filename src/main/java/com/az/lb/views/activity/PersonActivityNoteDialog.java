package com.az.lb.views.activity;

import com.az.lb.UserContext;
import com.az.lb.model.PersonActivity;
import com.vaadin.flow.component.HtmlContainer;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H5;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.shared.Registration;

import java.time.LocalDate;

public class PersonActivityNoteDialog extends Dialog {


    private PersonActivity personActivity;

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
        notes = new TextArea();
        notes.setHeight("300px");
        notes.setWidth("400px");

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
                labelDatePicker,
                hl
        ));
    }


}
