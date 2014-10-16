package org.ovirtChina.enginePlugin.vmBackupScheduler.common;

public class Test {

    public static void main(String[] args) {
        WeekDays te = new WeekDays("sdfds1fd");
        System.out.println("WeekDays: " + te);
        System.out.println("Monday: " + te.isMonday());
        System.out.println("Tuesday: " + te.isTuesday());
        System.out.println("Wednesday: " + te.isWednesday());
        System.out.println("Thursday: " + te.isThursday());
        System.out.println("Friday: " + te.isFriday());
        System.out.println("Saturday: " + te.isSaturday());
        System.out.println("Sunday: " + te.isSunday());
    }

}
