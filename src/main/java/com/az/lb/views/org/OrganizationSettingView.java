package com.az.lb.views.org;


import com.az.lb.MainView;
import com.az.lb.UserContext;
import com.az.lb.servise.OrgService;
import com.az.lb.views.ViewConst;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.*;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;


@Route(value = "OrganizationSettings", layout = MainView.class)
@RouteAlias(value = "OrganizationSettings", layout = MainView.class)
@PageTitle("Organization Settings")
@CssImport("./styles/views/dashboard/dashboard-view.css")
@Secured("ADM")
public class OrganizationSettingView extends VerticalLayout implements AfterNavigationObserver {

    private final UserContext userContext;

    @Autowired
    private OrgService orgService;

    private TextField name = new TextField();
    private Checkbox fillteam = new Checkbox();
    private Button saveButton = new Button("Save") ;

    public OrganizationSettingView(@Autowired UserContext userContext) {
        super();
        this.userContext = userContext;

        FormLayout formLayout = new FormLayout();
        formLayout.addFormItem(name, "Name");
        //formLayout.addFormItem(fillteam, "Allow impersonalized log");

        final FlexLayout caddButtonWrapper = new FlexLayout(saveButton);
        caddButtonWrapper.setJustifyContentMode(FlexComponent.JustifyContentMode.END);

        formLayout.add(
                caddButtonWrapper
        );

        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1, FormLayout.ResponsiveStep.LabelsPosition.TOP),
                new FormLayout.ResponsiveStep("300px", 1, FormLayout.ResponsiveStep.LabelsPosition.ASIDE));
        formLayout.setWidth(ViewConst.DIALOG_WIDTH);

        //saveButton.setEnabled(false);


        add(new HorizontalLayout(new H4("Organization settings")));
        add(formLayout);


    }


    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        name.setValue(userContext.getOrg().getName());
        fillteam.setValue(BooleanUtils.toBoolean(userContext.getOrg().getFillteam()));
        name.addValueChangeListener(
                e -> saveButton.setEnabled(true)
        );
    }
}
