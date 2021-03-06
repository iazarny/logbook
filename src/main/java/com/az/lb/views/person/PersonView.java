package com.az.lb.views.person;

import com.az.lb.UserContext;
import com.az.lb.model.Person;
import com.az.lb.repository.RegistrationRepository;
import com.az.lb.security.SecurityUtils;
import com.az.lb.servise.PersonService;
import com.az.lb.servise.mail.MailService;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.binder.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import com.az.lb.MainView;
import org.springframework.security.access.annotation.Secured;

@Route(value = "Persons", layout = MainView.class)
@PageTitle("Persons")
@CssImport("./styles/views/masterdetail/master-detail-view.css")
@Secured({"ADM", "USER"})
public class PersonView extends VerticalLayout implements AfterNavigationObserver {

    private static final Logger logger = LoggerFactory.getLogger(PersonView.class);

    private UserContext userContext;

    @Autowired
    private PersonService service;

    @Autowired
    private MailService mailService;

    @Autowired
    private RegistrationRepository registrationRepository;


    private Grid<Person> grid;

    private TextField firstname = new TextField();
    private TextField lastname = new TextField();
    private TextField email = new TextField();
    private Checkbox orgManager = new Checkbox();
    private Checkbox blocked = new Checkbox();

    private Button forgot = new Button("Change password");
    private Button cancel = new Button("Cancel");
    private Button save = new Button("Save");

    private PersonEditDialog personEditDialog;

    private Binder<Person> binder;

    public PersonView(@Autowired UserContext userContext) {

        this.userContext = userContext;

        setId("master-detail-view");
        // Configure Grid
        this.grid = new Grid<>();
        this.grid.setHeightFull();
        this.grid.addColumn(Person::getFullName)
                .setHeader("Name")
                .setWidth("40%")
                .setSortable(true)
                .setResizable(true);
        this.grid.addColumn(Person::getEmail)
                .setHeader("Email")
                .setWidth("60%")
                .setSortable(true)
                .setResizable(true);


        //when a row is selected or deselected, populate form
        this.grid.asSingleSelect().addValueChangeListener(
                event -> populateForm(event.getValue()));

        // Configure Form
        this.binder = new Binder<>(Person.class);

        // Bind fields. This where you'd define e.g. validation rules
        this.binder.bindInstanceFields(this);
        // note that password field isn't bound since that property doesn't exist in
        // Employee

        // the grid valueChangeEvent will clear the form too
        this.cancel.addClickListener(e -> grid.asSingleSelect().clear());

        this.forgot.addClickListener(

                e -> {
                    this.grid.getSelectedItems().forEach(
                            p-> {
                                service.forgotPassword(p.getEmail());
                                Notification.show(
                                        "Change password mail has been sent to " + p.getFullName(),
                                        3000,
                                        Notification.Position.TOP_CENTER
                                );
                            }
                    );
                }

        );

        this.save.addClickListener(e -> {

            this.grid.getSelectedItems().forEach(
                    p -> {
                        try {

                            Person prev = service.findById(p.getId().toString()).get();

                            this.binder.writeBean(p);
                            p.setPwd(prev.getPwd());

                            this.service.save(p);
                            this.grid.getDataProvider().refreshItem(p);
                            Notification.show("Saved " + p.getFullName());

                        } catch (ValidationException ex) {
                            logger.warn("Cannot save person " + p, ex);
                            Notification.show("Error " + p.getFullName());
                        }
                    }
            );
        });

        SplitLayout splitLayout = new SplitLayout();
        splitLayout.setSizeFull();

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);

        this.personEditDialog = new PersonEditDialog("New person");

        final Button addBtn = new Button("Add");

        final FlexLayout caddButtonWrapper = new FlexLayout(addBtn);
        caddButtonWrapper.setJustifyContentMode(FlexComponent.JustifyContentMode.END);

        HorizontalLayout hl = new HorizontalLayout(
                new H4("Organization members"),
                caddButtonWrapper
        );
        hl.setWidthFull();
        hl.expand(caddButtonWrapper);

        addBtn.addClickListener(event -> {
            newPerson();
        });

        add(
                personEditDialog,
                hl,
                splitLayout
        );

        if (!SecurityUtils.hasRole("ADM")) {
            addBtn.setEnabled(false);
            forgot.setEnabled(false);
            save.setEnabled(false);
            cancel.setEnabled(false);
        }
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
                    person.setBlocked(false);
                    person = service.save(person);
                    grid.setItems(service.findAll(userContext.getOrg()));
                    if (personEditDialog.isSendInvitation()) {
                        if (service.invitePerson(person.getEmail())) {
                            Notification.show("Invitation sent to " + person.getEmail(),
                                    3000, Notification.Position.TOP_CENTER);
                        }
                    }
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
        addFormItem(editorDiv, formLayout, orgManager, "Lead");
        addFormItem(editorDiv, formLayout, blocked, "Blocked");
        createButtonLayout(editorDiv);
        splitLayout.addToSecondary(editorDiv);
    }

    private void createButtonLayout(Div editorDiv) {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setId("button-layout");
        buttonLayout.setWidthFull();
        buttonLayout.setSpacing(true);
        forgot.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonLayout.add(forgot, cancel, save);
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
    }
}
