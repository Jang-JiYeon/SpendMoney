package com.kakaopay.sm.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CommonUtil {
	private static Logger logger = LogManager.getLogger(CommonUtil.class);

    //분 차이 구하기
    public static long getCurrentMinuteDifference(String sendTime) throws ParseException {
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date now = new Date();
        String currentTime = sdfDate.format(now);
        
        Date date1 = sdfDate.parse(currentTime);
		Date date2 = sdfDate.parse(sendTime);
		long diff = date1.getTime() - date2.getTime();
		long minute = diff / (1000 * 60);	//분 차이 구하기
		
		logger.info("분 차이 >>> " + minute);
        return minute;
    }
    
    //일 차이 구하기
    public static long getCurrentDayDifference(String sendTime) throws ParseException {
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date now = new Date();
        String currentTime = sdfDate.format(now);
        
        Date date1 = sdfDate.parse(currentTime);
		Date date2 = sdfDate.parse(sendTime);
		long diff = date1.getTime() - date2.getTime();
		long day = diff / (1000 * 60 * 60 * 24);	//일 차이 구하기
		
		logger.info("일 차이 >>> " + day);
        return day;
    }
}
