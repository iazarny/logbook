package com.yahoo.igor.spring;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.editor.Editor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.converter.StringToIntegerConverter;
import com.vaadin.flow.data.selection.SingleSelect;
import com.vaadin.flow.data.validator.StringLengthValidator;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.WeakHashMap;

@SpringComponent
@UIScope
public class MainViewGrid extends Tab {

    String filterStr = null;

    public MainViewGrid(@Autowired MessageBean bean) {


        Button button = new Button("Click me",
                e -> Notification.show(bean.getMessage()));


        ComboBox comboBox = new ComboBox<>();
        Grid<Person> grid = new Grid<>();
        Grid.Column<Person> nameColumn =  grid.addColumn(Person::getName)
                .setHeader("Name")
                .setSortable(true)
                .setSortProperty("name");

        Grid.Column<Person> dobColumn = grid.addColumn(person -> Integer.toString(person.getYearOfBirth()))
                .setHeader("Year of birth")
                .setSortable(true)
                .setSortProperty("yearOfBirth");
                /*.setSortOrderProvider(
                      new SortOrderProvider() {

                          @Override
                          public Stream<QuerySortOrder> apply(SortDirection sortDirection) {
                              return Collections.singletonList(
                                      new QuerySortOrder("yearOfBirth", sortDirection))
                                      .stream();
                          }
                      }
                );*/
        grid.setPageSize(5);
        grid.setDataProvider(

                new PersonsDataProvider()
        );



        Binder<Person> binder = new Binder<>(Person.class);
        Editor<Person> editor = grid.getEditor();
        editor.setBinder(binder);
        editor.setBuffered(true);

        Div validationStatus = new Div();
        validationStatus.setId("validation");

        TextField firstNameField = new TextField();
        binder.forField(firstNameField)
                .withValidator(new StringLengthValidator("First name length must be between 3 and 50.", 3, 50))
                .withStatusLabel(validationStatus).bind("name");
        nameColumn.setEditorComponent(firstNameField);

        TextField ageField = new TextField();
        binder.forField(ageField)
                .withConverter(
                        new StringToIntegerConverter("Age must be a number."))
                .withStatusLabel(validationStatus).bind("yearOfBirth");
        dobColumn.setEditorComponent(ageField);


        Collection<Button> editButtons = Collections
                .newSetFromMap(new WeakHashMap<>());
        Grid.Column<Person> editorColumn = grid.addComponentColumn(person -> {
            Button edit = new Button("Edit");
            edit.addClassName("edit");
            edit.addClickListener(e -> {
                editor.editItem(person);
                firstNameField.focus();
            });
            edit.setEnabled(!editor.isOpen());
            editButtons.add(edit);
            return edit;
        });

        editor.addOpenListener(e -> editButtons.stream()
                .forEach(b -> button.setEnabled(!editor.isOpen())));
        editor.addCloseListener(e -> editButtons.stream()
                .forEach(b -> button.setEnabled(!editor.isOpen())));

        Button save = new Button("Save", e -> editor.save());
        save.addClassName("save");

        Button cancel = new Button("Cancel", e -> editor.cancel());
        cancel.addClassName("cancel");

        // Add a keypress listener that listens for an escape key up event.
// Note! some browsers return key as Escape and some as Esc
        grid.getElement().addEventListener("keyup", event -> editor.cancel())
                .setFilter("event.key === 'Escape' || event.key === 'Esc'");

        Div buttons = new Div(save, cancel);
        editorColumn.setEditorComponent(buttons);

        editor.addSaveListener(
                event -> Notification.show( " Save - " + event.getItem().getName() + ", "
                        + event.getItem().getYearOfBirth()  )
        );





        SingleSelect<Grid<Person>, Person> personSelect = grid.asSingleSelect();
        personSelect.addValueChangeListener(e -> {
            Person p = e.getValue();
            System.out.println(">>>> person selected " + p);
        });








        TextField textField = new TextField();
        textField.setPlaceholder("Filter by name...");
        textField.setClearButtonVisible(true);
        textField.setValueChangeMode(ValueChangeMode.EAGER);
        textField.addValueChangeListener(e -> {
            System.out.println("{" + e.getValue() + "}");
            ((PersonsDataProvider)grid.getDataProvider()).setFilterStr(e.getValue());
            grid.getDataProvider().refreshAll();
        });
        
        add(button);
        add(textField);
        add(comboBox);
        add(grid);



    }

}
