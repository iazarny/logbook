package com.az.lb.views.activity;

import com.az.lb.model.PersonActivity;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.page.Page;

import java.util.function.Consumer;

@CssImport("styles/views/recording/recording.css")
public class RecordPersonRecordNotification extends Notification {

    private final PersonActivity personActivity;
    private final Consumer<String> onClose;


    public RecordPersonRecordNotification(final PersonActivity personActivity, final Consumer<String> onClose) {

        super();

        this.personActivity = personActivity;
        this.onClose = onClose;

        UI.getCurrent().getPage().addJavaScript("./js/WebAudioRecorder.min.js");
        UI.getCurrent().getPage().addJavaScript("./js/WebAudioRecorderAz.js");

        Button stopRecordingBtn = new Button("Stop recording");

        stopRecordingBtn.addClickListener(event -> {
            stopRecording();
        } );

        setPosition(Notification.Position.TOP_CENTER);

        Label info = new Label("Recording for " + personActivity.getPerson().getFullName()) ;
        info.setClassName("blink_me");

        add(
                info,
                new Label(" "),
                stopRecordingBtn
        );

        Page page = UI.getCurrent().getPage();

        page.executeJs("startRecording()");

    }

    private void stopRecording() {

        Page page = UI.getCurrent().getPage();

        System.out.println("CALL stopRecording("+personActivity.getId().toString()+")");


        page.executeJs("stopRecording($0)", personActivity.getId().toString());

        System.out.println("CALL  ok ");

        this.close();

        onClose.accept(personActivity.getId().toString());

    }
}
