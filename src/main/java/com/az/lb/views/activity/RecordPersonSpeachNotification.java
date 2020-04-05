package com.az.lb.views.activity;

import com.az.lb.model.PersonActivity;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.JavaScript;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.page.Page;

import java.util.function.Consumer;

public class RecordPersonSpeachNotification extends Notification {

    private final PersonActivity personActivity;
    private final Consumer<String> onClose;


    public RecordPersonSpeachNotification(final PersonActivity personActivity, final Consumer<String> onClose) {

        super();

        this.personActivity = personActivity;
        this.onClose = onClose;

        UI.getCurrent().getPage().addJavaScript("js/WebAudioRecorder.min.js");
        UI.getCurrent().getPage().addJavaScript("js/app.js");

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

        onClose.accept(personActivity.getId().toString());

    }
}
