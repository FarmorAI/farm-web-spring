package com.farmorai.backend.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class OrderUtil {

    public static String generateOrderNumber() {
        String date = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String nanoTime = String.valueOf(System.nanoTime()).substring(6);
        String uuid = UUID.randomUUID().toString().replace("-", "").substring(0, 4);
        return date + nanoTime + uuid;
    }
}
