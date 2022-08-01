package ru.ivt.schedule2021restServer.forms;

import lombok.Data;

@Data
public class StudentRegistrationForm implements StudentForm {
    private final String name;
    private final String password;
    private final String groupName;
    private final String role;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getPassword() {
        return password;
    }
}
