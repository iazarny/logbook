package com.az.lb.views.activity;


import com.az.lb.MainView;
import com.az.lb.UserContext;
import com.az.lb.model.Activity;
import com.az.lb.model.PersonActivity;
import com.az.lb.model.PersonActivityDetail;
import com.az.lb.servise.PersonActivityDetailService;
import com.az.lb.servise.PersonActivityService;
import com.az.lb.servise.PersonService;
import com.az.lb.views.ConfirmDialog;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.HtmlContainer;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.*;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Route(value = "PersonActivity", layout = MainView.class)
@RouteAlias(value = "PersonActivity", layout = MainView.class)
@PageTitle("PersonActivity")
@CssImport("styles/views/personactivity/person-activity.css")
public class PersonActivityView extends VerticalLayout implements AfterNavigationObserver /*, HasUrlParameter<String>*/ {

    @Autowired
    private UserContext userContext;

    @Autowired
    private PersonService personService;

    @Autowired
    private PersonActivityDetailService personActivityDetailService;

    private final Grid<PersonActivity> grid;

    private final PersonAdtivityDetailDialog personAdtivityDetailDialog;

    private final ConfirmDialog confirmDialog;

    @Autowired
    private PersonActivityService personActivityService;


    public PersonActivityView(@Autowired UserContext userContext,
                              @Autowired PersonService personService,
                              @Autowired PersonActivityDetailService personActivityDetailService) {
        setId("person-activity-view");
        this.userContext = userContext;
        this.personService = personService;
        this.personActivityDetailService = personActivityDetailService;

        this.confirmDialog = new ConfirmDialog("Please confirm", "");


        this.personAdtivityDetailDialog = new PersonAdtivityDetailDialog(
                userContext,
                personService,
                personActivityDetailService);


        this.grid = new Grid<PersonActivity>();
        this.grid.setId("person-activity-list");
        this.grid.setHeightFull();
        this.grid.addColumn(pa -> pa.getPerson().getFullName())
                .setHeader("Name")
                .setSortable(true);
        this.grid.addColumn(new ComponentRenderer<>(pa -> {
            return new Html(
                    createDetailActivity(personActivityDetailService, pa)
            );
        }))
                .setHeader("Detail");

        this.grid.addColumn(new ComponentRenderer<>(pa -> {
            return new VerticalLayout(
                    new Label(pa.getNote()),
                    new Label(pa.getTags())
            );
        }))
                .setHeader("Notes / Blockers");

        this.grid.addColumn(new ComponentRenderer<>(pa -> {
            if (StringUtils.isBlank(pa.getNote())
                    && StringUtils.isBlank(pa.getTags())
                    && ArrayUtils.isEmpty(pa.getRecord())
                    && personActivityDetailService.countAllByActivity(pa) == 0
            ) {
                Icon delIcon = new Icon(VaadinIcon.MINUS_CIRCLE_O);
                delIcon.addClickListener(e -> removeActivity(pa));
                return delIcon;

            }
            return new Html("<div></div>");
        }))
                .setAutoWidth(true)
                .setTextAlign(ColumnTextAlign.END);

        this.grid.addItemClickListener(
                ie -> {
                    getUI().get().getCurrent().getPage().retrieveExtendedClientDetails(
                            r -> {
                                personAdtivityDetailDialog.setWidth(((int) (r.getBodyClientWidth() * 0.98)) + "px");
                                personAdtivityDetailDialog.setHeight(((int) (r.getBodyClientHeight() * 0.98)) + "px");
                                personAdtivityDetailDialog
                                        .personActivity(ie.getItem())
                                        .message("Detail activity")
                                        .autoAdd(LocalDate.now().equals(userContext.getSelectedDate()))
                                        .onClose(
                                                e -> {
                                                    personAdtivityDetailDialog.close();
                                                    grid.getDataProvider().refreshAll();
                                                }
                                        )
                                        .open();
                            }
                    );
                }
        );

        final HtmlContainer titleH3 = new H5("Activity " + userContext.getSelectedTeam().getName() + " " + userContext.getSelectedDate());

        final Button addBtn = new Button("Add");

        final FlexLayout cancelButtonWrapper = new FlexLayout(addBtn);
        cancelButtonWrapper.setJustifyContentMode(JustifyContentMode.END);

        final HorizontalLayout topHorizontalLayout = new HorizontalLayout(
                titleH3,
                cancelButtonWrapper
        );
        topHorizontalLayout.expand(cancelButtonWrapper);
        topHorizontalLayout.setWidthFull();

        add(topHorizontalLayout);

        add(personAdtivityDetailDialog);

        add(confirmDialog);

        add(grid);


    }

    private String createDetailActivity(@Autowired PersonActivityDetailService personActivityDetailService, PersonActivity pa) {
        final List<PersonActivityDetail> details = personActivityDetailService.findActivityDetail(pa);
        final String cellBody;
        if (details.isEmpty()) {
            cellBody = "<div></div>";
        } else {
            cellBody = "<table width='100%' class='detail-table'>" + details.stream()
                    .map(ad -> "<tr class='detail-table-tr'>" +
                            "<td>" + ad.getTask() + "</td>" +
                            "<td>" + StringUtils.truncate(ad.getDetail(), 20) + "</td>" +
                            "<td>" + ad.getSpend() + "</td>" +
                            "</tr>")
                    .collect(Collectors.joining())
                    + "</table>";
        }
        return cellBody;
    }


    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        Activity act = personActivityService.createPersonsActivitySheet(userContext.getSelectedTeam(),
                userContext.getSelectedDate());
        List<PersonActivity> data = personActivityService.findAllByActivity(act);
        grid.setItems(data);
        grid.getDataProvider().refreshAll();
    }


    private void removeActivity(final PersonActivity personActivity) {
        confirmDialog
                .message("Do you want to remove activity for  " + personActivity.getPerson().getFullName() + " ?")
                .onCancel(e -> {
                    grid.getDataProvider().refreshAll();
                    confirmDialog.close();
                })
                .onConfirm(e -> {
                    personActivityService.delete(personActivity);
                    ((ListDataProvider) grid.getDataProvider()).getItems().remove(personActivity);
                    grid.getDataProvider().refreshAll();
                    confirmDialog.close();
                })
                .open();
    }
}
