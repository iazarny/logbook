package com.az.lb.views.activity;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.HtmlContainer;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H5;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.shared.Registration;

import java.time.LocalDate;

public class ActivityDateDialog extends Dialog {

    HtmlContainer message;
    DatePicker labelDatePicker;

    Button confirmButton;
    Button cancelButton;

    private Registration cancelListenerRegistration = null;
    private Registration confirmListenerRegistration = null;


    public ActivityDateDialog(final String title) {
        super();
        setCloseOnEsc(true);
        setCloseOnOutsideClick(true);

        message = new H5(title);
        labelDatePicker = new DatePicker("Date");
        labelDatePicker.setValue(LocalDate.now());

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


    public ActivityDateDialog confirmText(String confirmText) {
        confirmButton.setText(confirmText);
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
