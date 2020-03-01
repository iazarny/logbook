package com.az.lb.views.activity;

import com.az.lb.UserContext;
import com.az.lb.model.Person;
import com.az.lb.model.PersonActivity;
import com.az.lb.model.PersonActivityDetail;
import com.az.lb.servise.PersonService;
import com.az.lb.servise.TeamService;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.shared.Registration;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

//@CssImport("styles/views/personactivitydetail/person-activity-detail.css")
public class PersonAdtivityDetailDialog extends Dialog {

    private final UserContext userContext;
    private final PersonService personService;


    private H2 message;
    private List<Person> availableMembers;
    private ComboBox<Person> availableMembersCmb;

    private Button closeButton;

    private Registration closeListenerRegistration = null;

    private PersonActivity personActivity;

    private Grid<PersonActivityDetail> grid;

    public PersonAdtivityDetailDialog(UserContext userContext, PersonService personService) {

        super();

        this.personService = personService;

        this.userContext = userContext;

        setId("person-activity-detail");

        availableMembersCmb = new ComboBox<Person>();
        availableMembersCmb.setItemLabelGenerator(Person::getFullName);

        closeButton = new Button("Close");

        message = new H2("Detail activity");

        grid = new Grid<PersonActivityDetail>();

        grid.addColumn(i -> i.getTask())
                .setSortable(true)
                .setHeader("Task");

        grid.addColumn(i -> i.getName())
                .setSortable(true)
                .setHeader("Name");

        grid.addColumn(i -> i.getSpend())
                .setSortable(true)
                .setHeader("Spend");

        grid.addColumn(new ComponentRenderer<>(i -> {
            return new Checkbox(i.isDone());
        }))
                .setSortable(true)
                .setHeader("Done");

        grid.addColumn(new ComponentRenderer<>(i -> {
            return new HorizontalLayout(
                    new Icon(VaadinIcon.CROSS_CUTLERY)
            );
        }));

        grid.setWidth("96%");

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
        return this;

    }


}


