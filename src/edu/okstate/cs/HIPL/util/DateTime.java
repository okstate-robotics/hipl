/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.okstate.cs.hipl.util;

import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author Sridhar
 */
public class DateTime {
    
    private static String time=null;
    private static String date=null;
    
    public static String getTime(){
        init();
        return time;
    }
    
    public static String getDate(){
        init();
        return date;
    }
    
    private static void init(){
        Calendar cal=Calendar.getInstance();
        cal.setTime(new Date());
        
        date=""+String.format("%02d", cal.get(Calendar.MONTH)+1)+"-"+String.format("%02d",cal.get(Calendar.DATE))+"-"+String.format("%04d", cal.get(Calendar.YEAR));
        
        time=""+String.format("%02d", cal.get(Calendar.HOUR))+":"+String.format("%02d",cal.get(Calendar.MINUTE));
        String val="";
        if(cal.get(Calendar.AM_PM)==1){
            val="PM";
        }else{
            val="AM";
        }
        time=time+val;
    }
}

