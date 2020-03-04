package com.az.lb.views.activity;

import com.az.lb.UserContext;
import com.az.lb.model.Person;
import com.az.lb.model.PersonActivity;
import com.az.lb.model.PersonActivityDetail;
import com.az.lb.servise.PersonActivityDetailService;
import com.az.lb.servise.PersonService;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.editor.Editor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.validator.StringLengthValidator;
import com.vaadin.flow.shared.Registration;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.WeakHashMap;

//@CssImport("styles/views/personactivitydetail/person-activity-detail.css")

public class PersonAdtivityDetailDialog extends Dialog {

    private final UserContext userContext;
    private final PersonService personService;
    private final PersonActivityDetailService personActivityDetailService;


    private H2 message;
    private ComboBox<Person> availableMembersCmb;

    private Button closeButton;

    private Registration closeListenerRegistration = null;

    private PersonActivity personActivity;

    private List<PersonActivityDetail> persistedDetails;
    private final Grid<PersonActivityDetail> grid;
    private final TextField teskTextField;

    private final Binder<PersonActivityDetail> personActivityDetailBinder;
    private final Editor<PersonActivityDetail> personActivityDetailEditor;

    private boolean autoAddAllowed = true;
    private boolean saveNewRecord = false;


    public PersonAdtivityDetailDialog(UserContext userContext,
                                      PersonService personService,
                                      PersonActivityDetailService personActivityDetailService ) {

        super();

        setCloseOnEsc(true);
        setCloseOnOutsideClick(true);

        this.personService = personService;

        this.userContext = userContext;

        this.personActivityDetailService = personActivityDetailService;

        setId("person-activity-detail");

        availableMembersCmb = new ComboBox<Person>();
        availableMembersCmb.setItemLabelGenerator(Person::getFullName);

        closeButton = new Button("Close");

        message = new H2("Detail activity");

        grid = new Grid<PersonActivityDetail>();

        Grid.Column<PersonActivityDetail> taskColumn = grid.addColumn(i -> i.getTask())
                .setSortable(true)
                .setHeader("Task")
                .setWidth("32pt")
                .setAutoWidth(true);

        Grid.Column<PersonActivityDetail> nameColumn = grid.addColumn(i -> i.getName())
                .setSortable(true)
                .setHeader("Name");

        Grid.Column<PersonActivityDetail> detailColumn = grid.addColumn(i -> i.getDetail())
                .setSortable(true)
                .setHeader("Detail");

        Grid.Column<PersonActivityDetail> spendColumn = grid.addColumn(i -> i.getSpend())
                .setSortable(true)
                .setHeader("Spend")
                .setWidth("32pt")
                .setAutoWidth(true);


        Grid.Column<PersonActivityDetail> doneColumn = grid.addColumn(new ComponentRenderer<>(i -> {
            Checkbox rez = new Checkbox(i.isDone());
            rez.setEnabled(false);
            return rez;
        }))
                .setSortable(true)
                .setHeader("Done");

        /*grid.addColumn(new ComponentRenderer<>(i -> {
            return  new Icon(VaadinIcon.MINUS_CIRCLE_O);
        }))
                .setTextAlign(ColumnTextAlign.END)
                .setWidth("32pt")
                .setAutoWidth(true);*/


        grid.setWidth("96%");

        this.personActivityDetailBinder = new Binder<>(PersonActivityDetail.class);
        this.personActivityDetailEditor = grid.getEditor();
        personActivityDetailEditor.setBinder(personActivityDetailBinder);
        //personActivityDetailEditor.setBuffered(true);
        Div validationStatus = new Div();
        validationStatus.setId("validation");




        this.teskTextField = new TextField();
        personActivityDetailBinder.forField(teskTextField)
                .withValidator(new StringLengthValidator("Task name length must be between 3 and 32.", 3, 32))
                //.withStatusLabel(validationStatus)
                .bind("task");
        taskColumn.setEditorComponent(teskTextField);

        TextField nameTextField = new TextField();
        personActivityDetailBinder.forField(nameTextField)
                //.withValidator(new StringLengthValidator("Name length must be between 3 and 512.", 3, 512))
                //.withStatusLabel(validationStatus)
                .bind("name");
        nameColumn.setEditorComponent(nameTextField);


        TextArea detaildTextField = new TextArea();
        personActivityDetailBinder.forField(detaildTextField)
                .withValidator(new StringLengthValidator("Detail length must be between 3 and 32768.", 3, 32768))
                //.withStatusLabel(validationStatus)
                .bind("detail");
        detailColumn.setEditorComponent(detaildTextField);

        TextField spendTextField = new TextField();
        personActivityDetailBinder.forField(spendTextField)
                //.withValidator(new StringLengthValidator("Spend length must be between 2 and 8.", 2, 8))
                //.withStatusLabel(validationStatus)
                .bind("spend");
        spendColumn.setEditorComponent(spendTextField);

        Checkbox doneCheckBox = new Checkbox();
        personActivityDetailBinder.forField(doneCheckBox)
                //.withStatusLabel(validationStatus)
                .bind("done");
        doneColumn.setEditorComponent(doneCheckBox);

        Collection<Icon> editButtons = Collections.newSetFromMap(new WeakHashMap<>());

        Grid.Column<PersonActivityDetail> editorColumn = grid.addComponentColumn(i -> {

            Icon delIcon = new Icon(VaadinIcon.MINUS_CIRCLE_O);
            editButtons.add(delIcon);

            Icon editIcon = new Icon(VaadinIcon.PENCIL);
            editIcon.addClickListener(e -> {
                personActivityDetailEditor.editItem(i);
                nameTextField.focus();
            });
            editIcon.setVisible(!personActivityDetailEditor.isOpen());
            editButtons.add(editIcon);
            return  editIcon;

            /*Button edit = new Button("Edit");
            edit.addClassName("edit");
            edit.addClickListener(e -> {
                personActivityDetailEditor.editItem(i);
                nameTextField.focus();
            });
            edit.setEnabled(!personActivityDetailEditor.isOpen());
            editButtons.add(edit);
            return edit;*/
        });

        Button save = new Button("Save", e -> {
            //personActivityDetailEditor.save();
            saveNewRecord = true;
            personActivityDetailEditor.closeEditor();
        });
        save.addClassName("save");
        Button cancel = new Button("Cancel", e -> {
            saveNewRecord = false;
            personActivityDetailEditor.cancel();
        });
        cancel.addClassName("cancel");

        Div buttons = new Div(save, cancel);
        editorColumn.setEditorComponent(buttons);

        personActivityDetailEditor.addCloseListener(event -> {
            if (saveNewRecord) {
                System.out.println("$$$$$$$$$$$ addCloseListener " + event);
                final PersonActivityDetail pad = event.getItem();
                if (StringUtils.isNotBlank(pad.getTask())) {
                    personActivityDetailService.save(pad);
                    Notification.show( " Save - " + event.getItem().getTask()  );
                    if (autoAddAllowed) {
                        addNewItemToFill();
                    }
                }
            }
        });

/*
        personActivityDetailEditor.addSaveListener(
                event -> {
                    System.out.println("########## addSaveListener " + event);

                }
        );
*/

        personActivityDetailEditor.addCancelListener(
                event -> {
                    Notification.show( " Cancel " + event.getItem()  );
                    if (event.getItem().getId() == null) {
                        removeItemDrimGrid(event.getItem());
                    }
                    autoAddAllowed = false;
                }
        );

        //personActivityDetailBinder.setStatusLabel();
        /*personActivityDetailBinder.setValidationStatusHandler(

                h -> {
                    System.out.println(h);
                }

        );*/

        addDialogCloseActionListener(closeEvt -> {
            if (personActivityDetailEditor.isOpen()) {
                personActivityDetailEditor.cancel();
            }

        });

        HorizontalLayout hl = new HorizontalLayout(
                message,
                availableMembersCmb,
                closeButton
        );
        hl.setWidthFull();

        add(  hl  );
        add(grid);


    }

    public Editor<PersonActivityDetail> getPersonActivityDetailEditor() {
        return personActivityDetailEditor;
    }

    public PersonAdtivityDetailDialog onClose(ComponentEventListener<ClickEvent<Button>> listener) {
        if (closeListenerRegistration != null) {
            closeListenerRegistration.remove();
        }
        closeListenerRegistration = this.closeButton.addClickListener(listener);
        return this;
    }

    public PersonAdtivityDetailDialog autoAdd(boolean autoAddAllowed) {
        this.autoAddAllowed = autoAddAllowed;
        return this;

    }

    public PersonAdtivityDetailDialog message(String message) {
        this.message.setText(message);
        return this;
    }

    public PersonAdtivityDetailDialog personActivity(final PersonActivity personActivity) {
        final List<Person> availableMembers = personService.findAll(userContext.getOrg());
        this.personActivity = personActivity;
        this.availableMembersCmb.setItems(availableMembers);
        this.availableMembersCmb.setValue(personActivity.getPerson());



        addNewItemToFill();


        return this;
    }


    private void addNewItemToFill() {

        this.persistedDetails = personActivityDetailService.findActivityDetail(this.personActivity);
        this.grid.setItems(this.persistedDetails);


        //todo is date not now do not add record
        //this.grid.getDataProvider().refreshAll();
        PersonActivityDetail detail = new PersonActivityDetail();
        detail.setActivity(personActivity);

        this.persistedDetails.add(detail);
        //grid.getDataProvider().refreshAll();
        grid.getDataProvider().refreshItem(detail, false);

        personActivityDetailEditor.editItem(detail);
        teskTextField.focus();
    }




    private void removeItemDrimGrid(PersonActivityDetail pad) {
        this.persistedDetails.remove(pad);
        this.grid.getDataProvider().refreshAll();

    }


}


