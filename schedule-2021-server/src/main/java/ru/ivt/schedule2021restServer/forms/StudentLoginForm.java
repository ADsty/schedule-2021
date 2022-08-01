package ru.ivt.schedule2021restServer.forms;

import lombok.Data;

@Data
public class StudentLoginForm implements StudentForm {
    private final String name;
    private final String password;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getPassword() {
        return password;
    }
}
