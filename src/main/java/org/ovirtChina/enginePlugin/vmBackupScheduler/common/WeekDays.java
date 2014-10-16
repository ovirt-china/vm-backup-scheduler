package org.ovirtChina.enginePlugin.vmBackupScheduler.common;

public class WeekDays {
    private boolean monday;
    private boolean tuesday;
    private boolean wednesday;
    private boolean thursday;
    private boolean friday;
    private boolean saturday;
    private boolean sunday;

    public boolean isMonday() {
        return monday;
    }
    public void setMonday(boolean monday) {
        this.monday = monday;
    }
    public boolean isTuesday() {
        return tuesday;
    }
    public void setTuesday(boolean tuesday) {
        this.tuesday = tuesday;
    }
    public boolean isWednesday() {
        return wednesday;
    }
    public void setWednesday(boolean wednesday) {
        this.wednesday = wednesday;
    }
    public boolean isThursday() {
        return thursday;
    }
    public void setThursday(boolean thursday) {
        this.thursday = thursday;
    }
    public boolean isFriday() {
        return friday;
    }
    public void setFriday(boolean friday) {
        this.friday = friday;
    }
    public boolean isSaturday() {
        return saturday;
    }
    public void setSaturday(boolean saturday) {
        this.saturday = saturday;
    }
    public boolean isSunday() {
        return sunday;
    }
    public void setSunday(boolean sunday) {
        this.sunday = sunday;
    }

    public WeekDays() {
    }

    public WeekDays(String value) {
        if (value == null) {
            value = "0000000";
        } else if (value.length() < 7) {
            value += "0000000";
        }
        char[] weekDaysSplit = value.toCharArray();
        if ('1' == weekDaysSplit[0]) {
            setMonday(true);
        }
        if ('1' == weekDaysSplit[1]) {
            setTuesday(true);
        }
        if ('1' == weekDaysSplit[2]) {
            setWednesday(true);
        }
        if ('1' == weekDaysSplit[3]) {
            setThursday(true);
        }
        if ('1' == weekDaysSplit[4]) {
            setFriday(true);
        }
        if ('1' == weekDaysSplit[5]) {
            setSaturday(true);
        }
        if ('1' == weekDaysSplit[6]) {
            setSunday(true);
        }
    }

    @Override
    public String toString() {
        return isMonday()?"1":"0" +
                (isTuesday()?"1":"0") +
                (isWednesday()?"1":"0") +
                (isThursday()?"1":"0") +
                (isFriday()?"1":"0") +
                (isSaturday()?"1":"0") +
                (isSunday()?"1":"0");
    }

    public static WeekDays parseWeeekDays(String weekDays) {
        return new WeekDays(weekDays);
    }
}
