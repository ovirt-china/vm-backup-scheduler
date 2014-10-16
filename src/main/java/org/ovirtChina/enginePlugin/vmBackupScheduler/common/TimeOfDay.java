package org.ovirtChina.enginePlugin.vmBackupScheduler.common;

public class TimeOfDay {
    private int hour;
    private int minute;

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public TimeOfDay(int hour, int minute) {
        super();
        this.hour = hour;
        this.minute = minute;
    }

    @Override
    public String toString() {
        return hour + ":" + minute;
    }

    public static TimeOfDay parseTimeOfDay(String timeOfDay) {
        String[] split = timeOfDay.split(":");
        return new TimeOfDay(Integer.parseInt(split[0]), Integer.parseInt(split[1]));
    }
}
