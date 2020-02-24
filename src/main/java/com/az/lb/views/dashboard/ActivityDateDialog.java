package com.az.lb.views.dashboard;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.shared.Registration;

import java.time.LocalDate;

public class ActivityDateDialog extends Dialog {

    H2 message;
    DatePicker labelDatePicker;

    Button confirmButton;
    Button cancelButton;

    private Registration cancelListenerRegistration = null;
    private Registration confirmListenerRegistration = null;


    public ActivityDateDialog(final String title) {
        super();
        setCloseOnEsc(true);
        setCloseOnOutsideClick(true);

        message = new H2(title);
        labelDatePicker = new DatePicker("Date");
        labelDatePicker.setValue(LocalDate.now());

        confirmButton = new Button("New");
        cancelButton = new Button("Cancel");
        HorizontalLayout hl = new HorizontalLayout(
                confirmButton,
                cancelButton
        );
        hl.setAlignItems(FlexComponent.Alignment.END);

        add(new VerticalLayout(
                message,
                labelDatePicker,
                hl
        ));
    }

    public ActivityDateDialog message(String message) {
        this.message.setText(message);
        return this;
    }

    public ActivityDateDialog onCancel(ComponentEventListener<ClickEvent<Button>> listener) {
        if (cancelListenerRegistration != null) {
            cancelListenerRegistration.remove();
        }
        cancelListenerRegistration = this.cancelButton.addClickListener(listener);
        return this;
    }

    public ActivityDateDialog onConfirm(ComponentEventListener<ClickEvent<Button>> listener) {
        if (confirmListenerRegistration != null) {
            confirmListenerRegistration.remove();
        }
        confirmListenerRegistration = this.confirmButton.addClickListener(listener);
        return this;
    }


    public ActivityDateDialog date(LocalDate localDate) {
        labelDatePicker.setValue(localDate);
        return this;
    }

    public LocalDate getLocalDate() {
        return labelDatePicker.getValue();
    }
}
