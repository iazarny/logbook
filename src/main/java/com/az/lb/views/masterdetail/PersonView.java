package com.az.lb.views.masterdetail;

import com.az.lb.model.Person;
import com.az.lb.servise.PersonService;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import com.az.lb.MainView;
@Route(value = "Master-Detail", layout = MainView.class)
@PageTitle("Master Detail")
@CssImport("styles/views/masterdetail/master-detail-view.css")
public class PersonView extends VerticalLayout implements AfterNavigationObserver {

    @Autowired
    private PersonService service;

    private Grid<Person> employees;

    private TextField firstname = new TextField();
    private TextField lastname = new TextField();
    private TextField email = new TextField();
    private PasswordField password = new PasswordField();

    private Button cancel = new Button("Cancel");
    private Button save = new Button("Save");

    private PersonEditDialog personEditDialog;

    private Binder<Person> binder;

    public PersonView() {
        setId("master-detail-view");
        // Configure Grid
        employees = new Grid<>();
        //employees.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        employees.setHeightFull();
        employees.addColumn(Person::getFirstName).setHeader("First name");
        employees.addColumn(Person::getLastName).setHeader("Last name");
        employees.addColumn(Person::getEmail).setHeader("Email");

        //when a row is selected or deselected, populate form
        employees.asSingleSelect().addValueChangeListener(event -> populateForm(event.getValue()));

        // Configure Form
        binder = new Binder<>(Person.class);

        // Bind fields. This where you'd define e.g. validation rules
        binder.bindInstanceFields(this);
        // note that password field isn't bound since that property doesn't exist in
        // Employee

        // the grid valueChangeEvent will clear the form too
        cancel.addClickListener(e -> employees.asSingleSelect().clear());

        save.addClickListener(e -> {
            Notification.show("Not implemented");
        });

        SplitLayout splitLayout = new SplitLayout();
        splitLayout.setSizeFull();

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);

        final Button addBtn = new Button("Add");
        personEditDialog = new PersonEditDialog("New person");

        addBtn.addClickListener(event -> {
            newPerson();
        });

        add(personEditDialog);
        add(
                addBtn,
                new H2("Organization members"),
                splitLayout
        );
    }
/* private void newTeam() {
        teamDialog
                .message("New team")
                .onCancel(e -> {teamDialog.close();})
                .onConfirm(e -> {
                    Team team = service.createNewTeam(
                            userContext.getOrg().getId().toString(),
                            teamDialog.input.getValue());
                    grid.setItems(service.findAll());
                    grid.getDataProvider().refreshAll();
                    teamDialog.close();
                })
                .open(); //teamDialog.input.getElement().callJsFunction("focus");
    }*/
    private void newPerson() {
        personEditDialog
                .message("New person")
                .email("")
                .firstName("")
                .lastName("")
                .manager(false)
                .onCancel(e -> personEditDialog.close())
                .onConfirm( e-> {
                    System.out.println(e); //todo
                })
                .open();

    }

    private void createEditorLayout(SplitLayout splitLayout) {
        Div editorDiv = new Div();
        editorDiv.setId("editor-layout");
        FormLayout formLayout = new FormLayout();
        addFormItem(editorDiv, formLayout, firstname, "First name");
        addFormItem(editorDiv, formLayout, lastname, "Last name");
        addFormItem(editorDiv, formLayout, email, "Email");
        createButtonLayout(editorDiv);
        splitLayout.addToSecondary(editorDiv);
    }

    private void createButtonLayout(Div editorDiv) {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setId("button-layout");
        buttonLayout.setWidthFull();
        buttonLayout.setSpacing(true);
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonLayout.add(cancel, save);
        editorDiv.add(buttonLayout);
    }

    private void createGridLayout(SplitLayout splitLayout) {
        Div wrapper = new Div();
        wrapper.setId("wrapper");
        wrapper.setWidthFull();
        splitLayout.addToPrimary(wrapper);
        wrapper.add(employees);
    }

    private void addFormItem(Div wrapper, FormLayout formLayout,
            AbstractField field, String fieldName) {
        formLayout.addFormItem(field, fieldName);
        wrapper.add(formLayout);
        field.getElement().getClassList().add("full-width");
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {

        // Lazy init of the grid items, happens only when we are sure the view will be
        // shown to the user
        employees.setItems(service.findAll()); // todo by org
    }

    private void populateForm(Person value) {
        // Value can be null as well, that clears the form
        binder.readBean(value);

        // The password field isn't bound through the binder, so handle that
        password.setValue("");
    }
}
