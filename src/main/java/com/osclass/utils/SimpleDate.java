package com.osclass.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class SimpleDate {

    public static final String MONTHS[] = { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" };
    
    private Calendar calendar = Calendar.getInstance();
    private int month = calendar.get(Calendar.MONTH);
    private int day = calendar.get(Calendar.DATE);
    private int year = calendar.get(Calendar.YEAR);

    public SimpleDate() {
    }

    public Calendar getCalendar() {
        return calendar;
    }

    public void setCalendar(Calendar calendar) {
        this.calendar = calendar;
        month = this.calendar.get(Calendar.MONTH);
        day = this.calendar.get(Calendar.DATE);
        year = this.calendar.get(Calendar.YEAR);
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
        calendar.set(Calendar.MONTH, month);
    }
    
    public int getDay() {
        return day;
    }
    
    public void setDay(int day) {
        this.day = day;
        calendar.set(Calendar.DATE, day);
    }
    
    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
        calendar.set(Calendar.YEAR, year);
    }
    
    public SimpleDate advanceDay(int days) {
        calendar.add(Calendar.DATE, days);
        day = calendar.get(Calendar.DATE);
        month = calendar.get(Calendar.MONTH);
        year = calendar.get(Calendar.YEAR);
        return this;
    }

    public SimpleDate advanceMonth(int months) {
        calendar.add(Calendar.MONTH, months);
        month = calendar.get(Calendar.MONTH);
        year = calendar.get(Calendar.YEAR);
        return this;
    }

    public SimpleDate advanceYear(int years) {
        calendar.add(Calendar.YEAR, years);
        year = calendar.get(Calendar.YEAR);
        return this;
    }
    
    public String getMonthString() {
        return MONTHS[month];
    }
    
    public String toString(String format) {
        SimpleDateFormat simpleDate = new SimpleDateFormat(format);
        Date date = calendar.getTime();
        String dateString = simpleDate.format(date);
        return dateString;
    }
    
    public boolean isBefore(SimpleDate date) {
        return calendar.before(date);
    }
    
    public boolean isAfter(SimpleDate date) {
        return calendar.after(date);
    }
    
    public int compareTo(SimpleDate date) {
        return calendar.compareTo(date.getCalendar());
    }
    
    public int compareCalendarTo(Calendar calendar) {
        return this.calendar.compareTo(calendar);
    }
    
    public void setStringMoth(String month){
        switch(month){
            case "Jan" :
                setMonth(0);
            break;
            case "Feb" :
                setMonth(1);
            break;
            case "Mar" :
                setMonth(2);
            break;
            case "Apr" :
                setMonth(3);
            break;
            case "May" :
                setMonth(4);
            break;
            case "Jun" :
                setMonth(5);
            break;
            case "Jul" :
                setMonth(6);
            break;
            case "Aug" :
                setMonth(7);
            break;
            case "Sep" :
                setMonth(8);
            break;
            case "Oct" :
                setMonth(9);
            break;
            case "Nov" :
                setMonth(10);
            break;
            case "Dec" :
                setMonth(11);
            break;
            default : 
                System.out.println("Invalid month");
        }
    }
}