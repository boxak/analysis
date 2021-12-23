package com.javas.analysis.utils;

public class ElasticUtil {
    public static String convertPubDate(String pubDate) {
        String[] splitArr = pubDate.split(" ");
        String[] dateArr = splitArr[0].split("\\.");
        String year = dateArr[0];
        String month = dateArr[1];
        String day = dateArr[2];

        boolean isPm = "오후".equals(splitArr[1]);
        String time = splitArr[2];
        String timeArr[] = time.split(":");
        int hour = Integer.parseInt(timeArr[0]);
        int minute = Integer.parseInt(timeArr[1]);

        if (isPm) {
            time = Integer.toString(hour+12) + ":" + Integer.toString(minute);
        }

        String resultDate = String.format("%s-%s-%sT%s", year, month, day, time);

        return resultDate;
    }

    public static String convertRegDate(String regDate) {
        return regDate.replace(" ","T");
    }
}
