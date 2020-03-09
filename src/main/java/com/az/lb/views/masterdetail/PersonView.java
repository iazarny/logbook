package com.az.lb.views.masterdetail;

import com.az.lb.UserContext;
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
@PageTitle("Persons")
@CssImport("styles/views/masterdetail/master-detail-view.css")
public class PersonView extends VerticalLayout implements AfterNavigationObserver {

    private UserContext userContext;

    @Autowired
    private PersonService service;

    private Grid<Person> grid;

    private TextField firstname = new TextField();
    private TextField lastname = new TextField();
    private TextField email = new TextField();
    private PasswordField password = new PasswordField();

    private Button cancel = new Button("Cancel");
    private Button save = new Button("Save");

    private PersonEditDialog personEditDialog;

    private Binder<Person> binder;

    public PersonView(@Autowired UserContext userContext) {

        this.userContext = userContext;

        setId("master-detail-view");
        // Configure Grid
        this.grid = new Grid<>();
        //grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        this.grid.setHeightFull();
        this.grid.addColumn(Person::getFirstName).setHeader("First name");
        this.grid.addColumn(Person::getLastName).setHeader("Last name");
        this.grid.addColumn(Person::getEmail).setHeader("Email");

        //when a row is selected or deselected, populate form
        this.grid.asSingleSelect().addValueChangeListener(event -> populateForm(event.getValue()));

        // Configure Form
        this. binder = new Binder<>(Person.class);

        // Bind fields. This where you'd define e.g. validation rules
        this.binder.bindInstanceFields(this);
        // note that password field isn't bound since that property doesn't exist in
        // Employee

        // the grid valueChangeEvent will clear the form too
        this.cancel.addClickListener(e -> grid.asSingleSelect().clear());

        this.save.addClickListener(e -> {
            Notification.show("Not implemented");
        });

        SplitLayout splitLayout = new SplitLayout();
        splitLayout.setSizeFull();

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);

        this.personEditDialog = new PersonEditDialog("New person");

        final Button addBtn = new Button("Add");



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
    private void newPerson() {
        personEditDialog
                .email("")
                .firstName("")
                .lastName("")
                .manager(false)
                .message("Add new person")
                .onCancel( cancel -> {
                    personEditDialog.close();
                })
                .onConfirm(confirm -> {
                    Person person = new Person();
                    person.setOrgManager(personEditDialog.isManager());
                    person.setFirstName(personEditDialog.getFirstName());
                    person.setLastName(personEditDialog.getLastName());
                    person.setEmail(personEditDialog.getEmai());
                    person.setOrg(userContext.getOrg());
                    service.save(person);
                    grid.setItems(service.findAll(userContext.getOrg()));
                    personEditDialog.close();

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
        wrapper.add(grid);
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
        grid.setItems(service.findAll(userContext.getOrg()));
    }

    private void populateForm(Person value) {
        // Value can be null as well, that clears the form
        binder.readBean(value);

        // The password field isn't bound through the binder, so handle that
        password.setValue("");
    }
}
