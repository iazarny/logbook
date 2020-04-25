package com.az.lb.servise;

import com.az.lb.misc.DurationHumanizer;
import com.az.lb.misc.DurationValidator;
import com.az.lb.model.Org;
import com.az.lb.model.Person;
import com.az.lb.model.PersonActivityDetail;
import com.az.lb.repository.PersonActivityDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@Service
public class ReportingService {


    class PersonSpend {
        String presonName;
        String description;
        Duration duration = Duration.ZERO;
        boolean done;

        public PersonSpend(String presonName, Duration duration, boolean done, String description) {
            this.presonName = presonName;
            this.duration = duration;
            this.done = done;
            this.description = description;
        }
    }

    private final DurationValidator durationValidator = new DurationValidator("");


    @Autowired
    private PersonActivityDetailRepository repository;



    @Transactional
    public String findAllFromTillDateAsHtmlTable(Org org, Person person, LocalDate fromdt, LocalDate tilldt) {
        final List<PersonActivityDetail> lst = repository.findAllFromTillDate(org.getId(), person.getId(),
                fromdt, tilldt);
        final StringBuffer buff = new StringBuffer();

        String taskSecription = "";
        Duration taskDuration = Duration.ZERO;
        LocalDate date = LocalDate.MIN;

        for (int i = 0; i < lst.size(); i++) {

            PersonActivityDetail pad = lst.get(i);
            if (date.equals(pad.getActivity().getActivity().getDt())) {
                taskDuration = taskDuration.plus(getDuration(pad.getSpend()));
                taskSecription += "<br><span>" + pad.getTask() + "</span><span>" + pad.getDetail() + "<span>";
            } else {
                taskDuration = getDuration(pad.getSpend());
                date = pad.getActivity().getActivity().getDt();
                taskSecription = "<span>" + pad.getTask() + "</span><span>" + pad.getDetail() + "<span>";
            }

            if (i + 1 == lst.size() || !lst.get(i + 1).getActivity().getActivity().getDt().equals(date)) {
                String durationString;
                if (Duration.ZERO.equals(taskDuration)) {
                    durationString = "";
                } else {
                    durationString = "<hr>" + DurationHumanizer.humanizeTotal(taskDuration.toString());
                }

                String str = String.format(
                        "<tr>" +
                                "<td style='white-space: nowrap;' valign='top'>%s</td>" +
                                "<td valign='top'>%s</td>" +
                                "</tr>",
                        date.toString() + durationString,
                        taskSecription
                );

                buff.append(str);
            }

        }

        return "<table class='detail-table-report'>" + buff.toString() + "</table>";

    }


    @Transactional
    public String findAllFromTillDateAsHtmlTable(Org org, LocalDate fromdt, LocalDate tilldt) {
        final List<PersonActivityDetail> lst = repository.findAllFromTillDate(org.getId(), fromdt, tilldt);
        final StringBuffer buff = getTableRows(lst);
        return "<table class='detail-table-report'>" + buff.toString() + "</table>";
    }

    StringBuffer getTableRows(List<PersonActivityDetail> lst) {
        final StringBuffer buff = new StringBuffer();
        String task = "";
        String person = "";
        Duration taskDuration = Duration.ZERO;
        List<ReportingService.PersonSpend> personsSpend = new ArrayList<>();

        for (int i = 0; i < lst.size(); i++) {

            PersonActivityDetail pad = lst.get(i);
            if (pad.getTask().equalsIgnoreCase(task)) {
                // add time if person not changed, otherwise start counting from 0
                if (person.equalsIgnoreCase(pad.getActivity().getPerson().getFullName())) {
                    taskDuration = taskDuration.plus(getDuration(pad.getSpend()));
                } else {
                    taskDuration = getDuration(pad.getSpend());
                }
            } else {
                task = pad.getTask();
                taskDuration = getDuration(pad.getSpend());
                personsSpend = new ArrayList<>();
            }
            personsSpend.add(new ReportingService.PersonSpend(pad.getActivity().getPerson().getFullName(), taskDuration, pad.isDone(), pad.getDetail()));

            if (i + 1 == lst.size() || !lst.get(i + 1).getTask().equalsIgnoreCase(task)) {


                String str = String.format(
                        "<tr class='%s' >" +
                                "<td style='white-space: nowrap;' valign='top'>%s</td>" +
                                "<td valign='top'>%s</td>" +
                                "<td style='white-space: nowrap; text-align: end;' valign='top'>%s</td>" +
                                "<td style='white-space: nowrap; text-align: end;' valign='top'>%s</td>" +
                                "</tr>",
                        isTaskDone(personsSpend) ? "task-tr task-done" : "task-tr task-not-done",
                        task,
                        getDescriptions(personsSpend),
                        DurationHumanizer.humanizeTotal(getTotalSpend(personsSpend).toString()),
                        getPersonsTime(personsSpend)
                );

                buff.append(str);
            }

        }
        return buff;
    }


    private String getDescriptions(List<ReportingService.PersonSpend> personsSpend) {
        StringBuffer buff = new StringBuffer();
        personsSpend.forEach(ps -> {
            buff.append(ps.description);
            buff.append("<br/>");
        });
        return buff.toString();
    }

    private boolean isTaskDone(List<ReportingService.PersonSpend> personsSpend) {
        return !personsSpend.stream().anyMatch(ps -> !ps.done);
    }

    private Duration getTotalSpend(List<ReportingService.PersonSpend> personsSpend) {
        return personsSpend.stream().map(ps -> ps.duration).reduce(Duration.ZERO, (a, b) -> a.plus(b));
    }

    private String getPersonsTime(List<ReportingService.PersonSpend> personsSpend) {
        if (personsSpend.size() == 1) {
            return "<span>" + personsSpend.get(0).presonName + "</span>";
        }
        StringBuffer buff = new StringBuffer();
        personsSpend.forEach(ps -> {
            buff.append("<span>");
            buff.append(ps.presonName);
            buff.append("</span>");
            buff.append("<span>&nbsp;</span>");
            buff.append("<span>");
            buff.append(DurationHumanizer.humanizeTotal(durationValidator.getDuration(ps.duration.toString()).toString()));
            buff.append("</span></br>");
        });
        return buff.toString();
    }


    private Duration getDuration(String text) {
        try {
            return durationValidator.getDuration(text);
        } catch (DateTimeParseException exception) {
            return Duration.ZERO;
        }
    }





}
