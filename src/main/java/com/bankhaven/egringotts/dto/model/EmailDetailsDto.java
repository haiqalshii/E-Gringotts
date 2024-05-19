package com.bankhaven.egringotts.dto.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailDetailsDto {
    private String recipient;
    private String messageBody;
    private String subject;
    private String attachment;
}
