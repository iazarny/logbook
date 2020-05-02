package com.az.lb;

import com.az.lb.security.SecurityUtils;
import com.az.lb.views.activity.TeamActivityView;
import com.az.lb.views.team.TeamView;
import com.az.lb.views.login.LoginView;
import com.az.lb.views.masterdetail.PersonView;
import com.az.lb.views.org.OrganizationSettingView;
import com.az.lb.views.report.ReportDailyView;
import com.az.lb.views.report.ReportPersonView;
import com.az.lb.views.report.ReportTaskView;
import com.github.appreciated.app.layout.component.applayout.LeftLayouts;
import com.github.appreciated.app.layout.component.builder.AppLayoutBuilder;
import com.github.appreciated.app.layout.component.menu.left.LeftSubmenu;
import com.github.appreciated.app.layout.component.menu.left.builder.LeftAppMenuBuilder;
import com.github.appreciated.app.layout.component.menu.left.builder.LeftSubMenuBuilder;
import com.github.appreciated.app.layout.component.menu.left.items.LeftClickableItem;
import com.github.appreciated.app.layout.component.menu.left.items.LeftNavigationItem;
import com.github.appreciated.app.layout.component.router.AppLayoutRouterLayout;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;
import org.springframework.security.access.annotation.Secured;

import static com.github.appreciated.app.layout.entity.Section.FOOTER;

/**
 * The main view is a top-level placeholder for other views.
 */
@JsModule("./styles/shared-styles.js")
@PWA(name = "Log Book", shortName = "Log Book")
@Theme(value = Lumo.class, variant = Lumo.DARK)
@Secured({"ADM", "USER"})
@Push
public class MainView extends AppLayoutRouterLayout<LeftLayouts.LeftResponsiveHybrid> { /*extends AppLayout {*/

    public MainView() {

        LeftSubMenuBuilder settingsSubMenuBuilder = LeftSubMenuBuilder.get("Settings", VaadinIcon.COG.create());
        settingsSubMenuBuilder.add(
                new LeftNavigationItem("Persons", VaadinIcon.MALE.create(), PersonView.class),
                new LeftNavigationItem("Teams", VaadinIcon.GROUP.create(), TeamView.class));
        if (SecurityUtils.hasRole("ADM")) {
            settingsSubMenuBuilder.add(new LeftNavigationItem("Organization", VaadinIcon.BUILDING.create(), OrganizationSettingView.class));
        }

        LeftSubmenu settingsSubMenu = settingsSubMenuBuilder.build();


        LeftAppMenuBuilder leftAppMenuBuilder = LeftAppMenuBuilder.get();
        leftAppMenuBuilder.add(new LeftNavigationItem("Activity", VaadinIcon.HOME.create(), TeamActivityView.class));
        if (SecurityUtils.hasRole("ADM")) {
            leftAppMenuBuilder.add(LeftSubMenuBuilder.get("Reports", VaadinIcon.PRINT.create())
                    .add(new LeftNavigationItem("Daily", VaadinIcon.CALENDAR.create(), ReportDailyView.class),
                            new LeftNavigationItem("Tasks", VaadinIcon.TASKS.create(), ReportTaskView.class),
                            new LeftNavigationItem("Person", VaadinIcon.MALE.create(), ReportPersonView.class))
                    .build());
        }
        leftAppMenuBuilder.add(settingsSubMenu);
        leftAppMenuBuilder.addToSection(FOOTER, new LeftClickableItem("Exit", VaadinIcon.EXIT.create(),
                clickEvent -> {
                    VaadinSession.getCurrent().getSession().invalidate();
                    //SecurityContextHolder.clearContext();
                    //VaadinService.getCurrentRequest().getWrappedSession().invalidate();
                    UI.getCurrent().navigate(LoginView.class);
                }
        ));


        init(AppLayoutBuilder.get(LeftLayouts.LeftResponsiveHybrid.class)
                .withTitle("Log book")
                .withAppMenu(leftAppMenuBuilder.build())
                .build());
    }


}
