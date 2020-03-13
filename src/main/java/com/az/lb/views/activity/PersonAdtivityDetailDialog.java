package com.az.lb.views.activity;

import com.az.lb.UserContext;
import com.az.lb.model.Person;
import com.az.lb.model.PersonActivity;
import com.az.lb.model.PersonActivityDetail;
import com.az.lb.servise.PersonActivityDetailService;
import com.az.lb.servise.PersonService;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.HtmlContainer;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.editor.Editor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H5;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
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

    private final static String SIZE_TASK = "60px";
    private final static String SIZE_NAME = "120px";
    private final static String SIZE_DESCR = "240px";
    private final static String SIZE_SPEND = "60px";
    private final static String SIZE_DONE = "30px";
    private final static String SIZE_ACT = "72px";

    private final UserContext userContext;
    private final PersonService personService;
    private final PersonActivityDetailService personActivityDetailService;


    private HtmlContainer message;
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
    private boolean naemColumnEnabled = false;


    public PersonAdtivityDetailDialog(UserContext userContext,
                                      PersonService personService,
                                      PersonActivityDetailService personActivityDetailService) {

        super();

        setCloseOnEsc(true);
        setCloseOnOutsideClick(true);

        this.personService = personService;

        this.userContext = userContext;

        this.personActivityDetailService = personActivityDetailService;

        setId("person-activity-detail");

        availableMembersCmb = new ComboBox<Person>();
        availableMembersCmb.setItemLabelGenerator(Person::getFullName);

        this.closeButton = new Button("Close");

        this.message = new H5("Detail activity");

        this.grid = new Grid<>();
        this.grid.setWidth("96%");
        this.personActivityDetailBinder = new Binder<>(PersonActivityDetail.class);
        this.personActivityDetailEditor = grid.getEditor();

        Grid.Column<PersonActivityDetail> taskColumn = grid.addColumn(i -> i.getTask())
                .setResizable(true)
                .setSortable(true)
                .setHeader("Task")
                .setWidth(SIZE_TASK)
                .setAutoWidth(true);

        if (naemColumnEnabled) {
            Grid.Column<PersonActivityDetail> nameColumn = grid.addColumn(i -> i.getName())
                    .setResizable(true)
                    .setSortable(true)
                    .setHeader("Name")
                    .setWidth(SIZE_NAME);

            TextField nameTextField = new TextField();
            personActivityDetailBinder.forField(nameTextField)
                    .bind("name");
            nameColumn.setEditorComponent(nameTextField);
        }


        Grid.Column<PersonActivityDetail> detailColumn = grid.addColumn(i -> i.getDetail())
                .setResizable(true)
                .setSortable(true)
                .setHeader("Detail")
                .setWidth(SIZE_DESCR);

        Grid.Column<PersonActivityDetail> spendColumn = grid.addColumn(i -> i.getSpend())
                .setResizable(true)
                .setSortable(true)
                .setHeader("Spend")
                .setWidth(SIZE_SPEND);


        Grid.Column<PersonActivityDetail> doneColumn = grid.addColumn(new ComponentRenderer<>(i -> {
            Checkbox rez = new Checkbox(i.isDone());
            rez.setEnabled(false);
            return rez;
        }))
                .setResizable(true)
                .setSortable(true)
                .setAutoWidth(true)
                .setWidth(SIZE_DONE)
                .setHeader("Done");


        personActivityDetailEditor.setBinder(personActivityDetailBinder);
        //personActivityDetailEditor.setBuffered(true);
        Div validationStatus = new Div();
        validationStatus.setId("validation");


        this.teskTextField = new TextField();
        this.teskTextField.setWidth(SIZE_TASK);
        personActivityDetailBinder.forField(teskTextField)
                .withValidator(new StringLengthValidator("Task name length must be between 3 and 32.", 3, 32))
                //.withStatusLabel(validationStatus)
                .bind("task");
        taskColumn.setEditorComponent(teskTextField);




        TextArea detaildTextField = new TextArea();
        detaildTextField.setWidth(SIZE_DESCR);
        detaildTextField.setHeight("120pt");
        personActivityDetailBinder.forField(detaildTextField)
                .withValidator(new StringLengthValidator("Detail length must be between 3 and 32768.", 3, 32768))
                //.withStatusLabel(validationStatus)
                .bind("detail");
        detailColumn.setEditorComponent(detaildTextField);

        TextField spendTextField = new TextField();
        spendTextField.setWidth(SIZE_SPEND);
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
            final Icon delIcon = new Icon(VaadinIcon.MINUS_CIRCLE_O);
            editButtons.add(delIcon);


            final Icon editIcon = new Icon(VaadinIcon.PENCIL);
            editButtons.add(editIcon);
            editIcon.addClickListener(e -> {
                personActivityDetailEditor.editItem(i);
                teskTextField.focus();
            });
            editIcon.setVisible(!personActivityDetailEditor.isOpen());


            HorizontalLayout hl = new HorizontalLayout(delIcon, editIcon);


            return hl;

        })
                .setAutoWidth(true)
                .setFlexGrow(0)
                .setTextAlign(ColumnTextAlign.END);

        Icon saveIcon = new Icon(VaadinIcon.CHECK);
        saveIcon.addClassName("save");
        saveIcon.addClickListener(e -> {
                    saveNewRecord = true;
                    personActivityDetailEditor.closeEditor();
                }
        );
        Icon cancelIcon = new Icon(VaadinIcon.CLOSE);
        cancelIcon.addClassName("cancel");
        cancelIcon.addClickListener(e -> {
                    saveNewRecord = false;
                    personActivityDetailEditor.cancel();
                }
        );
        Div buttons = new Div(saveIcon, cancelIcon);
        editorColumn.setEditorComponent(buttons);

        personActivityDetailEditor.addCloseListener(event -> {
            if (saveNewRecord) {
                final PersonActivityDetail pad = event.getItem();
                if (StringUtils.isNotBlank(pad.getTask())) {
                    personActivityDetailService.save(pad);
                    Notification.show(" Save - " + event.getItem().getTask());
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
                    Notification.show(" Cancel ");
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

        final FlexLayout closeButtonWrapper = new FlexLayout(closeButton);
        closeButtonWrapper.setJustifyContentMode(FlexComponent.JustifyContentMode.END);

        HorizontalLayout hl = new HorizontalLayout(
                message,
                availableMembersCmb,
                closeButtonWrapper
        );
        hl.setWidth("96%");
        hl.expand(closeButtonWrapper);

        add(hl);
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


