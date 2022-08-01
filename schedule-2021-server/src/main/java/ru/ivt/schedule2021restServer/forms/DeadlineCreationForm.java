package ru.ivt.schedule2021restServer.forms;

import lombok.Data;


@Data
public class DeadlineCreationForm {
    private final String title;
    private final String description;
    private final String notificationTime;
    private final String deadlineDate;
    private final Long studentId;
    private final String subjectName;

    public String getDeadlineDate() {
        return deadlineDate;
    }

    public String getNotificationTime() {
        return notificationTime;
    }

    public Long getStudentId() {
        return studentId;
    }

    public String getDescription() {
        return description;
    }

    public String getTitle() {
        return title;
    }
}
