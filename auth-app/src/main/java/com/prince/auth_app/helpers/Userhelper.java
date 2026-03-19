package com.prince.auth_app.helpers;

import java.util.UUID;

public class Userhelper {

    public static UUID parseUuid(String Uuid){
        return UUID.fromString(Uuid);
    }
}
