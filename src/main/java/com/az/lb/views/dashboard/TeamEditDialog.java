package com.az.lb.views.dashboard;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;

public class TeamEditDialog extends Dialog {

    TextField input;
    Button confirmButton;
    Button cancelButton;

    public TeamEditDialog(String title) {
        super();
        setCloseOnEsc(true);
        setCloseOnOutsideClick(true);

        input = new TextField();
        confirmButton = new Button("New");
        cancelButton = new Button("Cancel", event -> close());
        HorizontalLayout hl = new HorizontalLayout(
                confirmButton,
                cancelButton
        );
        hl.setAlignItems(FlexComponent.Alignment.END);
        VerticalLayout vl = new VerticalLayout(
                new H2(title),
                input,
                hl
        );
        add(vl);
    }

}
