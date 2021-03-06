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
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.TemplateRenderer;
import com.vaadin.flow.router.*;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.List;

@Route(value = PersonActivityView.ROUTE, layout = MainView.class)
@RouteAlias(value = PersonActivityView.ROUTE, layout = MainView.class)
@PageTitle("Person Activity")
@CssImport("./styles/views/personactivity/person-activity.css")
public class PersonActivityView extends VerticalLayout implements AfterNavigationObserver /*, HasUrlParameter<String>*/ {

    public static final  String ROUTE = "PersonActivity";

    @Autowired
    private UserContext userContext;

    private final Grid<PersonActivity> grid;
    private List<PersonActivity> data;
    private Activity act;

    private final PersonAdtivityDetailDialog personAdtivityDetailDialog;
    private final PersonActivityNoteDialog personActivityNoteDialog;
    private final SelectPersonDialog selectPersonDialog;

    private final ConfirmDialog confirmDialog;


    @Autowired
    private PersonActivityService personActivityService;


    public PersonActivityView(@Autowired UserContext userContext,
                              @Autowired PersonService personService,
                              @Autowired PersonActivityDetailService personActivityDetailService) {
        setId("person-activity-view");
        this.userContext = userContext;

        this.confirmDialog = new ConfirmDialog("Please confirm", "");


        this.personAdtivityDetailDialog = new PersonAdtivityDetailDialog(
                userContext,
                personService,
                personActivityDetailService);

        this.personActivityNoteDialog = new PersonActivityNoteDialog();

        this.selectPersonDialog = new SelectPersonDialog();


        this.grid = new Grid<PersonActivity>();
        this.grid.setId("person-activity-list");
        this.grid.setHeightFull();

        Grid.Column<PersonActivity> nameColumn = this.grid.addColumn(pa -> pa.getPerson().getFullName())
                .setHeader("Name")
                .setResizable(true)
                .setWidth("15%")
                .setSortable(true);

        Grid.Column<PersonActivity> detailColumn = this.grid.addColumn(new ComponentRenderer<>(pa -> {
            return new Html(
                    createDetailActivity(personActivityDetailService, pa)
            );
        }))
                .setResizable(true)
                .setWidth("55%")
                .setHeader("Detail")
                .setFlexGrow(1);

        Grid.Column<PersonActivity> notesColumn = this.grid.addColumn(
                TemplateRenderer.<PersonActivity>of("<div style='align-self:baseline; white-space:normal;'>[[item.note]]</div>")
                        .withProperty("note", vp -> {
                            boolean needHr = StringUtils.isNotBlank(vp.getNote()) && StringUtils.isNotBlank(vp.getTags());
                            return ObjectUtils.defaultIfNull(vp.getNote(), "") +
                                    (needHr ? "\n" : "") +
                                    ObjectUtils.defaultIfNull(vp.getTags(), "");
                        })

        )
                .setResizable(true)
                .setWidth("10%")
                .setHeader("Notes / Blockers")
                .setFlexGrow(1);


        Grid.Column gc = this.grid.addColumn(new ComponentRenderer<>(pa -> {
            Button playBtn = new Button(
                    new Icon(VaadinIcon.PLAY),
                    e -> {
                        Notification playNotification = new RecordPersonPlayNotification(pa);
                        playNotification.open();
                    }
            );
            playBtn.setEnabled(!pa.isRecordEmpty());

            Button delBtn = new Button(
                    new Icon(VaadinIcon.DEL),
                    e -> {
                        removeActivity(pa);
                    }
            );

            Button recordBtn = new Button(
                    new Icon(VaadinIcon.MICROPHONE),
                    e -> {
                        Notification notification = new RecordPersonRecordNotification(pa, id -> {
                            playBtn.setEnabled(true);
                            delBtn.setEnabled(false);

                        });
                        notification.open();
                    }
            );


            delBtn.setEnabled(
                    StringUtils.isBlank(pa.getNote())
                            && StringUtils.isBlank(pa.getTags())
                            && pa.isRecordEmpty()
                            && personActivityDetailService.countAllByActivity(pa) == 0
            );

            return new HorizontalLayout(delBtn, playBtn, recordBtn);

        }))
                .setAutoWidth(false)
                .setWidth("200px")
                .setFlexGrow(0)
                .setTextAlign(ColumnTextAlign.END);

        this.grid.addItemClickListener(
                ie -> {
                    getUI().get().getCurrent().getPage().retrieveExtendedClientDetails(
                            r -> {
                                if (nameColumn == ie.getColumn() || detailColumn == ie.getColumn()) {
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

                                } else if (notesColumn == ie.getColumn()) {

                                    personActivityNoteDialog
                                            .notes(StringUtils.stripToEmpty(ie.getItem().getNote()))
                                            .tags(StringUtils.stripToEmpty(ie.getItem().getTags()))
                                            .message("Notes / blockers")
                                            .onCancel(
                                                    e -> {
                                                        personActivityNoteDialog.close();
                                                        grid.getDataProvider().refreshAll();
                                                    }
                                            )
                                            .onConfirm(
                                                    e -> {
                                                        ie.getItem().setNote(personActivityNoteDialog.getNotes());
                                                        ie.getItem().setTags(personActivityNoteDialog.getTags());
                                                        personActivityService.save(ie.getItem());
                                                        personActivityNoteDialog.close();
                                                        grid.getDataProvider().refreshAll();
                                                    }
                                            )
                                            .open();

                                }
                            }
                    );
                }
        );


        final HtmlContainer titleContainer = new H4("Activity for " + userContext.getSelectedTeam().getName() + " on " + userContext.getSelectedDate());

        final Button addBtn = new Button("Add");
        addBtn.addClickListener(
                e -> {
                    selectPersonDialog
                            .message("Select person to add")
                            .confirmText("Add")
                            .persons(personService.findAllWithoutActivity(userContext.getOrg(), act))
                            .onCancel(
                                    ce -> {
                                        selectPersonDialog.close();
                                    }
                            )
                            .onConfirm(
                                    ce -> {
                                        PersonActivity pa = new PersonActivity();
                                        pa.setPerson(selectPersonDialog.getPerson());
                                        pa.setActivity(act);
                                        personActivityService.save(pa);
                                        data.add(pa);

                                        //!!!grid.setItems(personActivityService.findAllByActivity(act));
                                        grid.getDataProvider().refreshAll();

                                        grid.getDataProvider().refreshAll();
                                        selectPersonDialog.close();
                                    }
                            )
                            .open();

                }
        );

        final FlexLayout cancelButtonWrapper = new FlexLayout(addBtn);
        cancelButtonWrapper.setJustifyContentMode(JustifyContentMode.END);

        final HorizontalLayout topHorizontalLayout = new HorizontalLayout(
                titleContainer,
                cancelButtonWrapper
        );
        topHorizontalLayout.expand(cancelButtonWrapper);
        topHorizontalLayout.setWidthFull();

        add(topHorizontalLayout);

        add(personAdtivityDetailDialog);

        add(confirmDialog);

        add(grid);


    }

    private String createDetailActivity(PersonActivityDetailService personActivityDetailService,
                                        PersonActivity pa) {
        final List<PersonActivityDetail> details = personActivityDetailService.findActivityDetail(pa);
        final String cellBody;
        if (details.isEmpty()) {
            cellBody = "<div></div>";
        } else {
            cellBody = personActivityService.getDetailsAsHtmlTable(details, pa.getNote(),
                    pa.getTags(), false);
        }
        return cellBody;
    }


    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        act = personActivityService.createPersonsActivitySheet(
                userContext.getSelectedTeam(),
                userContext.getSelectedDate());
        data = personActivityService.findAllByActivity(act);
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
