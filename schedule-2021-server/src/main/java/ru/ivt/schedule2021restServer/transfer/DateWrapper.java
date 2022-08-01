package ru.ivt.schedule2021restServer.transfer;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class DateWrapper {
    private DateOptionsDto dates;
    private List<?> dataList;
}
