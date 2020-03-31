package com.az.lb.views.activity;

import com.az.lb.model.PersonActivity;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.JavaScript;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.page.Page;

public class RecordPersonSpeachNotification extends Notification {

    private final PersonActivity personActivity;


    public RecordPersonSpeachNotification(final PersonActivity personActivity) {

        super();

        this.personActivity = personActivity;

        //UI.getCurrent().getPage().addJavaScript("recorder.js");
        UI.getCurrent().getPage().addJavaScript("js/recorder.js");
        UI.getCurrent().getPage().addJavaScript("js/record-person-speach-notification.js");

        Button stopRecordingBtn = new Button("Stop recording");

        stopRecordingBtn.addClickListener(event -> {
            stopRecording();
        } );

        setPosition(Notification.Position.TOP_CENTER);

        add(
                new Label("Recording for " + personActivity.getPerson().getFullName()),
                stopRecordingBtn
        );

        Page page = UI.getCurrent().getPage();

        page.executeJs("startRecording()");




    }

    private void stopRecording() {

        Page page = UI.getCurrent().getPage();

        page.executeJs("stopRecording($0)", personActivity.getId().toString());


        this.close();
        //todo save
    }
}
