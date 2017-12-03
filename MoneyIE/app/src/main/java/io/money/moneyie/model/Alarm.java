package io.money.moneyie.model;

public class Alarm {
    private Integer id;
    private Integer date;
    private Integer hour;
    private Integer minutes;
    private String massage;

    public Alarm(Integer id, Integer date, Integer hour, Integer minutes, String massage) {
        this.id = id;
        this.date = date;
        this.hour = hour;
        this.minutes = minutes;
        this.massage = massage;
    }

    public Integer getId(){return id;}

    public Integer getDate() {
        return date;
    }

    public Integer getHour() {
        return hour;
    }

    public Integer getMinutes() {
        return minutes;
    }

    public String getMassage() {
        return massage;
    }
}
