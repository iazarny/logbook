package com.az.lb.views.activity;

import com.az.lb.model.PersonActivity;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;

public class RecordPersonPlayNotification extends Notification {

    public RecordPersonPlayNotification(final PersonActivity personActivity) {
        Html html = new Html(
                String.format(
                        "<audio controls><source src=\"download/audio?pid=%s\" type=\"audio/mpeg\"></audio>",
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
        add(html);
        add(btnStop);
    }

}
