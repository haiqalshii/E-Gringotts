package com.bankhaven.egringotts.service.interfaces;

import com.bankhaven.egringotts.dto.model.AccountDto;
import com.bankhaven.egringotts.dto.request.auth.NewAccountRequestDto;

public interface AccountServiceInterface {
    String createNewAccountForUser(NewAccountRequestDto newAccountRequest);
}
