package com.idea.plugin.api;

import java.time.LocalDateTime;

public class WanNianLiVO {
    private Integer code;
    private String msg;
    private Data data;

    public WanNianLiVO() {
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }


    public static class Data {
        private String date;
        private LocalDateTime weekDay;
        private String yearTips;
        private Integer type;
        private String typeDes;
        private String chineseZodiac;
        private String solarTerms;
        private String avoid;
        private String lunarCalendar;
        private String suit;
        private Integer dayOfYear;
        private Integer weekOfYear;
        private String constellation;
        private Integer indexWorkDayOfMonth;


        public Data() {
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public LocalDateTime getWeekDay() {
            return weekDay;
        }

        public void setWeekDay(LocalDateTime weekDay) {
            this.weekDay = weekDay;
        }

        public String getYearTips() {
            return yearTips;
        }

        public void setYearTips(String yearTips) {
            this.yearTips = yearTips;
        }

        public Integer getType() {
            return type;
        }

        public void setType(Integer type) {
            this.type = type;
        }

        public String getTypeDes() {
            return typeDes;
        }

        public void setTypeDes(String typeDes) {
            this.typeDes = typeDes;
        }

        public String getChineseZodiac() {
            return chineseZodiac;
        }

        public void setChineseZodiac(String chineseZodiac) {
            this.chineseZodiac = chineseZodiac;
        }

        public String getSolarTerms() {
            return solarTerms;
        }

        public void setSolarTerms(String solarTerms) {
            this.solarTerms = solarTerms;
        }

        public String getAvoid() {
            return avoid;
        }

        public void setAvoid(String avoid) {
            this.avoid = avoid;
        }

        public String getLunarCalendar() {
            return lunarCalendar;
        }

        public void setLunarCalendar(String lunarCalendar) {
            this.lunarCalendar = lunarCalendar;
        }

        public String getSuit() {
            return suit;
        }

        public void setSuit(String suit) {
            this.suit = suit;
        }

        public Integer getDayOfYear() {
            return dayOfYear;
        }

        public void setDayOfYear(Integer dayOfYear) {
            this.dayOfYear = dayOfYear;
        }

        public Integer getWeekOfYear() {
            return weekOfYear;
        }

        public void setWeekOfYear(Integer weekOfYear) {
            this.weekOfYear = weekOfYear;
        }

        public String getConstellation() {
            return constellation;
        }

        public void setConstellation(String constellation) {
            this.constellation = constellation;
        }

        public Integer getIndexWorkDayOfMonth() {
            return indexWorkDayOfMonth;
        }

        public void setIndexWorkDayOfMonth(Integer indexWorkDayOfMonth) {
            this.indexWorkDayOfMonth = indexWorkDayOfMonth;
        }
    }
}
