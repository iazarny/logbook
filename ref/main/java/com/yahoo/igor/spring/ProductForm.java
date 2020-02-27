package com.yahoo.igor.spring;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;

@Route(value = "/pf")
public class ProductForm extends VerticalLayout {

    public ProductForm() {
        add(new HorizontalLayout(new TextField("Name"), new TextArea()));
        add(new HorizontalLayout(new TextField("Description"), new TextArea()));
        add(new HorizontalLayout( new TextArea("Description")));

        NumberField nf = new NumberField("цена");
        nf.setSuffixComponent(new Span("$"));
        nf.setStep(0.01);

        add(new HorizontalLayout(
                new TextField("Price"),
                nf
                //new HorizontalLayout(new Label("$"), new NumberField())
        ));
        add(new HorizontalLayout(new TextField("Available since"), new DatePicker()));
        add(new HorizontalLayout(new TextField("Category"),
                new ComboBox<String>("Cat 1", "Cat 2", "Cat 3", "Cat 4", "Cat 5")));
        add(new HorizontalLayout(new Button("Save"), new Button("Cancel")));
    }
}
