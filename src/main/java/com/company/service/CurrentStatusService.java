package com.company.service;


import com.company.enums.CurrentStatus;

import java.util.HashMap;

public class CurrentStatusService {

    public static HashMap<Long, CurrentStatus> currentStatus = new HashMap<>();


    public static CurrentStatus getCurrenStatus(Long userId){

        CurrentStatus status = currentStatus.get(userId);

        if(status == null) return CurrentStatus.DEFAULT;

        return status;
    }
}
