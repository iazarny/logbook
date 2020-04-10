package com.az.lb.views.activity;

import com.az.lb.model.PersonActivity;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

import java.util.UUID;

public class RecordPersonPlayNotification extends Notification {

    public RecordPersonPlayNotification(final PersonActivity personActivity) {
        String id = "player" + UUID.randomUUID().toString();
        Html html = new Html(
                String.format(
                                "<audio id='%s' controls>" +
                                "<source src=\"download/audio?pid=%s\" type=\"audio/mpeg\">" +
                                "</audio>" ,
                        id,
                        personActivity.getId().toString()
                )
        );
        Button btnStop = new Button("Close");
        btnStop.addClickListener(
                e -> {
                    this.close();
                }
        );
        setPosition(Notification.Position.TOP_CENTER);
        add(
                new HorizontalLayout(
                        html,btnStop
                )
        );
    }

}
