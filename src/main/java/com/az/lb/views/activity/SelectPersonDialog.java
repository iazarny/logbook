package com.az.lb.views.activity;

import com.az.lb.model.Person;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.HtmlContainer;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H5;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.shared.Registration;

import java.time.LocalDate;
import java.util.List;

public class SelectPersonDialog extends Dialog {

    HtmlContainer message;
    ComboBox<Person> personsToAdd;

    Button confirmButton;
    Button cancelButton;

    private Registration cancelListenerRegistration = null;
    private Registration confirmListenerRegistration = null;


    public SelectPersonDialog() {
        super();
        setCloseOnEsc(true);
        setCloseOnOutsideClick(true);

        message = new H5();


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

        personsToAdd = new ComboBox("Persons");
        personsToAdd.setItemLabelGenerator(Person::getFullName);
        personsToAdd.addValueChangeListener(
                e -> {
                    confirmButton.setEnabled(true);
                }
        );

        add(new VerticalLayout(
                message,
                personsToAdd,
                hl
        ));
    }

    public SelectPersonDialog message(String message) {
        this.message.setText(message);
        return this;
    }

    public SelectPersonDialog onCancel(ComponentEventListener<ClickEvent<Button>> listener) {
        if (cancelListenerRegistration != null) {
            cancelListenerRegistration.remove();
        }
        cancelListenerRegistration = this.cancelButton.addClickListener(listener);
        return this;
    }

    public SelectPersonDialog onConfirm(ComponentEventListener<ClickEvent<Button>> listener) {
        if (confirmListenerRegistration != null) {
            confirmListenerRegistration.remove();
        }
        confirmListenerRegistration = this.confirmButton.addClickListener(listener);
        return this;
    }


    public SelectPersonDialog confirmText(String confirmText) {
        confirmButton.setText(confirmText);
        return this;
    }

    public SelectPersonDialog persons(List<Person> persons) {
        personsToAdd.setItems(persons);
        return this;
    }

    public Person getPerson() {
        return personsToAdd.getValue();
    }

    @Override
    public void open() {
        confirmButton.setEnabled(false);
        super.open();
    }
}
