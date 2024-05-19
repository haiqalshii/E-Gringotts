package com.bankhaven.egringotts.service.interfaces;

import com.bankhaven.egringotts.dto.model.EmailDetailsDto;

public interface EmailService {
    void sendEmailAlert(EmailDetailsDto emailDetailsDto);
}
