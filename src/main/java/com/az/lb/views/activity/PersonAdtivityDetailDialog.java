package com.az.lb.views.activity;

import com.az.lb.UserContext;
import com.az.lb.model.Person;
import com.az.lb.model.PersonActivity;
import com.az.lb.model.PersonActivityDetail;
import com.az.lb.servise.PersonActivityDetailService;
import com.az.lb.servise.PersonService;
import com.az.lb.servise.TeamService;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
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
import com.vaadin.flow.router.Route;
import com.vaadin.flow.shared.Registration;
import org.springframework.beans.factory.annotation.Autowired;

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
    private List<Person> availableMembers;
    private ComboBox<Person> availableMembersCmb;

    private Button closeButton;

    private Registration closeListenerRegistration = null;

    private PersonActivity personActivity;

    private Grid<PersonActivityDetail> grid;

    public PersonAdtivityDetailDialog(UserContext userContext,
                                      PersonService personService,
                                      PersonActivityDetailService personActivityDetailService ) {

        super();

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
                .setHeader("Task");

        Grid.Column<PersonActivityDetail> nameColumn = grid.addColumn(i -> i.getName())
                .setSortable(true)
                .setHeader("Name");

        Grid.Column<PersonActivityDetail> detailColumn = grid.addColumn(i -> i.getDetail())
                .setSortable(true)
                .setHeader("Detail");

        Grid.Column<PersonActivityDetail> spendColumn = grid.addColumn(i -> i.getSpend())
                .setSortable(true)
                .setHeader("Spend");

        Grid.Column<PersonActivityDetail> doneColumn = grid.addColumn(new ComponentRenderer<>(i -> {
            Checkbox rez = new Checkbox(i.isDone());
            rez.setEnabled(false);
            return rez;
        }))
                .setSortable(true)
                .setHeader("Done");

        grid.addColumn(new ComponentRenderer<>(i -> {
            return new HorizontalLayout(
                    new Icon(VaadinIcon.CROSS_CUTLERY)
            );
        }));

        grid.setWidth("96%");

        Binder<PersonActivityDetail> personActivityDetailBinder = new Binder<>(PersonActivityDetail.class);
        Editor<PersonActivityDetail> personActivityDetailEditor = grid.getEditor();
        personActivityDetailEditor.setBinder(personActivityDetailBinder);
        personActivityDetailEditor.setBuffered(true);
        Div validationStatus = new Div();
        validationStatus.setId("validation");




        TextField teskTextField = new TextField();
        personActivityDetailBinder.forField(teskTextField)
                .withValidator(new StringLengthValidator("Task name length must be between 3 and 32.", 3, 32))
                .withStatusLabel(validationStatus).bind("task");
        taskColumn.setEditorComponent(teskTextField);

        TextField nameTextField = new TextField();
        personActivityDetailBinder.forField(nameTextField)
                .withValidator(new StringLengthValidator("Name length must be between 3 and 512.", 3, 512))
                .withStatusLabel(validationStatus).bind("name");
        nameColumn.setEditorComponent(nameTextField);


        TextArea detaildTextField = new TextArea();
        personActivityDetailBinder.forField(detaildTextField)
                .withValidator(new StringLengthValidator("Detail length must be between 3 and 32768.", 3, 32768))
                .withStatusLabel(validationStatus).bind("detail");
        detailColumn.setEditorComponent(detaildTextField);

        TextField spendTextField = new TextField();
        personActivityDetailBinder.forField(spendTextField)
                .withValidator(new StringLengthValidator("Spend length must be between 2 and 8.", 2, 8))
                .withStatusLabel(validationStatus).bind("spend");
        spendColumn.setEditorComponent(spendTextField);

        Checkbox doneCheckBox = new Checkbox();
        personActivityDetailBinder.forField(doneCheckBox)
                .withStatusLabel(validationStatus).bind("done");
        doneColumn.setEditorComponent(doneCheckBox);

        Collection<Button> editButtons = Collections
                .newSetFromMap(new WeakHashMap<>());

        Grid.Column<PersonActivityDetail> editorColumn = grid.addComponentColumn(i -> {
            Button edit = new Button("Edit");
            edit.addClassName("edit");
            edit.addClickListener(e -> {
                personActivityDetailEditor.editItem(i);
                nameTextField.focus();
            });
            edit.setEnabled(!personActivityDetailEditor.isOpen());
            editButtons.add(edit);
            return edit;
        });

        Button save = new Button("Save", e -> personActivityDetailEditor.save());
        save.addClassName("save");
        Button cancel = new Button("Cancel", e -> personActivityDetailEditor.cancel());
        cancel.addClassName("cancel");

        Div buttons = new Div(save, cancel);
        editorColumn.setEditorComponent(buttons);

        personActivityDetailEditor.addSaveListener(
                event -> {
                    Notification.show( " Save - " + event.getItem()  );
                }
        );

        personActivityDetailEditor.addCancelListener(
                event -> {
                    Notification.show( " Cancel - " + event.getItem()  );
                }
        );

        personActivityDetailBinder.setValidationStatusHandler(

        );


        add(message);
        add(availableMembersCmb);
        add(grid);
        add(closeButton);

    }

    public PersonAdtivityDetailDialog onClose(ComponentEventListener<ClickEvent<Button>> listener) {
        if (closeListenerRegistration != null) {
            closeListenerRegistration.remove();
        }
        closeListenerRegistration = this.closeButton.addClickListener(listener);
        return this;
    }

    public PersonAdtivityDetailDialog message(String message) {
        this.message.setText(message);
        return this;
    }

    public PersonAdtivityDetailDialog personActivity(final PersonActivity personActivity) {
        this.personActivity = personActivity;
        this.availableMembers = personService.findAll(userContext.getOrg());
        this.availableMembersCmb.setItems(availableMembers);
        this.availableMembersCmb.setValue(personActivity.getPerson());
        addTestDetails(personActivity);
        return this;

    }

    public void addTestDetails(final PersonActivity personActivity) {

        PersonActivityDetail detail = new PersonActivityDetail();
        detail.setActivity(personActivity);
        detail.setDetail("Detail some lorepm ipsum");
        detail.setTask("ABC-1234");
        detail.setName("Add to add some issue and refine");
        personActivityDetailService.save(detail);

        //grid.setItems(personActivityDetailService.findActivityDetail(personActivity));
        grid.setItems(Collections.singletonList(detail));
        grid.getDataProvider().refreshAll();

    }


}


