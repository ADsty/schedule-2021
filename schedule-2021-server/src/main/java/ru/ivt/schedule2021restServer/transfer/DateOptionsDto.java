package ru.ivt.schedule2021restServer.transfer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DateOptionsDto {
    private LocalDate currentStartWeek;
    private LocalDate currentEndWeek;
    private LocalDate previousWeek;
    private LocalDate nextWeek;
}
