package com.az.lb.views.activity;

import com.az.lb.UserContext;
import com.az.lb.misc.DurationValidator;
import com.az.lb.model.Person;
import com.az.lb.model.PersonActivity;
import com.az.lb.model.PersonActivityDetail;
import com.az.lb.servise.PersonActivityDetailService;
import com.az.lb.servise.PersonService;
import com.az.lb.views.ConfirmDialog;
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
import com.vaadin.flow.component.html.H4;
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
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.validator.StringLengthValidator;
import com.vaadin.flow.shared.Registration;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.WeakHashMap;

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


    private final ConfirmDialog confirmDialog;

    private final HtmlContainer message;
    private final HtmlContainer personName;
    private final Button closeButton;
    private final Button addeButton;
    private Registration closeListenerRegistration = null;
    private final Grid<PersonActivityDetail> grid;
    private final TextField teskTextField;
    private final Binder<PersonActivityDetail> personActivityDetailBinder;
    private final Editor<PersonActivityDetail> personActivityDetailEditor;

    private  PersonActivity personActivity;
    private  List<PersonActivityDetail> persistedDetails;

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

        this.confirmDialog = new ConfirmDialog("Please confirm", "");

        this.personName = new H5();

        this.addeButton = new Button("Add");
        this.closeButton = new Button("Close");

        this.message = new H5("Detail activity");

        this.grid = new Grid<>();
        this.grid.setWidth("96%");
        this.grid.setHeightFull();

        this.personActivityDetailBinder = new Binder<>(PersonActivityDetail.class);
        this.personActivityDetailEditor = grid.getEditor();



        Grid.Column<PersonActivityDetail> taskColumn = grid.addColumn(i -> i.getTask())
                .setResizable(true)
                .setSortable(true)
                .setHeader("Task")
                .setAutoWidth(true)
                .setFlexGrow(1);

        Grid.Column<PersonActivityDetail> detailColumn = grid.addColumn(i -> i.getDetail())
                .setResizable(true)
                .setSortable(true)
                .setHeader("Detail")
                .setAutoWidth(true)
                .setFlexGrow(1);

        Grid.Column<PersonActivityDetail> spendColumn = grid.addColumn(i -> i.getSpend())
                .setResizable(true)
                .setSortable(true)
                .setHeader("Spend")
                .setAutoWidth(true)
                .setFlexGrow(1);

        Grid.Column<PersonActivityDetail> doneColumn = grid.addColumn(new ComponentRenderer<>(i -> {
            Checkbox rez = new Checkbox(i.isDone());
            rez.setEnabled(false);
            return rez;
        }))
                .setResizable(true)
                .setSortable(true)
                .setHeader("Done")
                .setAutoWidth(true)
                .setFlexGrow(1);


        personActivityDetailEditor.setBinder(personActivityDetailBinder);
        Div validationStatus = new Div();
        validationStatus.setId("validation");


        this.teskTextField = new TextField();
        this.teskTextField.setWidth("80px");
        personActivityDetailBinder.forField(teskTextField)
                .withValidator(new StringLengthValidator("Task name length must be \nbetween 3 and 32.", 3, 32))
                //.withStatusLabel(validationStatus)
                .bind("task");
        taskColumn.setEditorComponent(teskTextField);




        TextArea detaildTextField = new TextArea();
        detaildTextField.setMinWidth("120pt");
        detaildTextField.setMaxWidth("440pt");
        detaildTextField.setWidth("340pt");
        detaildTextField.setHeight("220px");
        personActivityDetailBinder.forField(detaildTextField)
                .withValidator(new StringLengthValidator("Detail length must be between 3 and 32768.", 3, 32768))
                //.withStatusLabel(validationStatus)
                .bind("detail");
        detailColumn.setEditorComponent(detaildTextField);

        TextField spendTextField = new TextField();
        spendTextField.setWidth(SIZE_SPEND);
        personActivityDetailBinder.forField(spendTextField)
                .withValidator(
                        new DurationValidator("")
                )
                //.withStatusLabel(validationStatus)
                .bind("spend");
        spendColumn.setEditorComponent(spendTextField);

        Checkbox doneCheckBox = new Checkbox();
        personActivityDetailBinder.forField(doneCheckBox)
                //.withStatusLabel(validationStatus)
                .bind("done");
        doneColumn.setEditorComponent(doneCheckBox);

        Collection<Button> editButtons = Collections.newSetFromMap(new WeakHashMap<>());

        Grid.Column<PersonActivityDetail> editorColumn = grid.addComponentColumn(i -> {
            final Button delBtn = new Button(
                    new Icon(VaadinIcon.DEL),
                    e-> {
                        confirmDialog
                                .message("Do you want to remove task " + i.getTask() + " ?")
                                .onCancel(ce -> {
                                    grid.getDataProvider().refreshAll();
                                    confirmDialog.close();
                                })
                                .onConfirm(ce -> {
                                    personActivityDetailService.delete(i);
                                    persistedDetails = personActivityDetailService.findActivityDetail(this.personActivity);
                                    grid.setItems(this.persistedDetails);
                                    grid.getDataProvider().refreshAll();
                                    confirmDialog.close();
                                })
                                .open();

                    }

            );
            editButtons.add(delBtn);
            final Button editBtn = new Button(
                    new Icon(VaadinIcon.PENCIL),
                    e -> {
                        personActivityDetailEditor.editItem(i);
                        teskTextField.focus();
                    }
            );
            editButtons.add(editBtn);
            editBtn.setEnabled(!personActivityDetailEditor.isOpen());

            return new HorizontalLayout(delBtn, editBtn);

        })
                .setAutoWidth(true)
                .setFlexGrow(1);

        Button saveBtn = new Button(new Icon(VaadinIcon.CLOUD_UPLOAD_O), e -> {
            saveNewRecord = true;
            personActivityDetailEditor.closeEditor();
        });
        Button cancelBtn =  new Button(new Icon(VaadinIcon.CLOSE), e -> {
            saveNewRecord = false;
            personActivityDetailEditor.cancel();
        });
        editorColumn.setEditorComponent( new HorizontalLayout(saveBtn, cancelBtn));

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
                addeButton.setEnabled(true);
            }
        });

        personActivityDetailEditor.addCancelListener(
                event -> {
                    Notification.show(" Cancel ");
                    if (event.getItem().getId() == null) {
                        removeItemDrimGrid(event.getItem());
                    }
                    autoAddAllowed = false;
                    addeButton.setEnabled(true);
                }
        );

        personActivityDetailEditor.addOpenListener( event -> {
                    addeButton.setEnabled(false);
                }
        );

        addDialogCloseActionListener(closeEvt -> {
            if (personActivityDetailEditor.isOpen()) {
                personActivityDetailEditor.cancel();
            }
        });

        addeButton.addClickListener(e -> {
            addNewItemToFill();
        });

        final FlexLayout closeButtonWrapper = new FlexLayout(addeButton, closeButton);
        closeButtonWrapper.setJustifyContentMode(FlexComponent.JustifyContentMode.END);


        HorizontalLayout hl = new HorizontalLayout(
                message,
                personName,
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
        addeButton.setEnabled(!autoAddAllowed);
        return this;

    }

    public PersonAdtivityDetailDialog message(String message) {
        this.message.setText(message);
        return this;
    }

    public PersonAdtivityDetailDialog personActivity(final PersonActivity personActivity) {
        this.personActivity = personActivity;
        this.personName.setText(personActivity.getPerson().getFullName());
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


