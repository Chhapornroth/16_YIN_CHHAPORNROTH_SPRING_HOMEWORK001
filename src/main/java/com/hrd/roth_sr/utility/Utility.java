package com.hrd.roth_sr.utility;

import com.hrd.roth_sr.model.enums.TicketStatus;

public class Utility {
    public static boolean isNullOrEmpty(String string){
        return string == null || string.isEmpty();
    }

    public static boolean isTicketStatusNotValid(String status){
        for(TicketStatus s : TicketStatus.values()){
            if(s.name().equalsIgnoreCase(status)){
                return false;
            }
        }
        return true;
    }
}
