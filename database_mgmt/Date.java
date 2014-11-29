/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database_mgmt;

/**
 *
 * @author Carl
 */
public class Date{
    private int day;
    private int month;
    private int year;
    
    public Date(int day, int month, int year){
        set(day, month, year);
    }
    
    public Date(Date date){
        set(date);
    }
    
    public Date(String date){
        String[] nums = date.split("[-]");
        boolean isDate = true;
        if(this.checkIfDate(date)){
            for(int i = 0; i < nums.length; i++){
                if(isInt(nums[i])){
                    int temp = Integer.parseInt(nums[i]);
                    if(i == 0 && (temp > 0 && temp < 32))
                        this.day = temp;
                    if(i == 1 && (temp > 0 && temp < 13))
                        this.month = temp;
                    if(i == 2 && (temp >= 0 && temp <= 9999))
                        this.year = temp;
                }
            }
        }else{
            throw new IllegalArgumentException("ERROR: attempted conversion of invalid string to date");
        }
    }
    
    public String toString(){
        if(day < 10 && month < 10){
            return "0" + day + "-0" + month + "-" + year;
        }
        else if(day < 10 && month >= 10){
            return "0" + day + "-" + month + "-" + year;
        }
        else if(day > 10 && month < 10){
            return day + "-0" + month + "-" + year;
        }
        else{
            return day + "-" + month + "-" + year;
        }
    }
    
    public void set(int day, int month, int year){
        if(acceptDate(day, month, year)){
            this.day = day;
            this.month = month;
            this.year = year;
        }
        else{
            throw new IllegalArgumentException(day+"-"+month+"-"+year);
        }
    }
    
    public void set(Date date){
        if(acceptDate(date.day, date.month, date.year)){
            this.day = date.getDay();
            this.month = date.getMonth();
            this.year = date.getYear();
        }
        else{
            throw new IllegalArgumentException(date.day+"-"+date.month+"-"+date.year);
        }
    }
    
    public int getDay(){
        return day;
    }
    
    public int getMonth(){
        return month;
    }
    
    public int getYear(){
        return year;
    }
    
    private boolean acceptDate(int day, int month, int year){
        if(day < 1 || day > 31)
            return false;
        if(month < 1 || month > 12)
            return false;
        if(year < 0 || year > 9999)
            return false;
        return true;
    }
    
    private static boolean isInt(String s){
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException e){
            return false;
        }
    }
    
    public static boolean checkIfDate(String s){
        String[] nums = s.split("[-]");
        boolean isDate = true;
        if(nums.length == 3){
            for(int i = 0; i < nums.length; i++){
                if(isInt(nums[i])){
                    int temp = Integer.parseInt(nums[i]);
                    if(i == 0 && (temp < 1 || temp > 31))
                        return false;
                    if(i == 1 && (temp < 1 || temp > 12))
                        return false;
                    if(i == 2 && (temp < 0 || temp > 9999))
                        return false;
                }
            }
        }else{
            isDate = false;
        }
        return isDate;
    }
    
    public boolean compareDates(Date d, String operator){
        switch (operator) {
            case "=":   return (this.year == d.getYear() &&
                                this.month == d.getMonth() &&
                                this.day == d.getDay());
            case "!=":  return !(this.year == d.getYear() &&
                                this.month == d.getMonth() &&
                                this.day == d.getDay());
            case "<":   return ((this.year == d.getYear() &&
                                this.month == d.getMonth() &&
                                this.day < d.getDay()) ||
                                (this.year == d.getYear() &&
                                this.month < d.getMonth()) ||
                                this.year < d.getYear());
            case ">":   return ((this.year == d.getYear() &&
                                this.month == d.getMonth() &&
                                this.day > d.getDay()) ||
                                (this.year == d.getYear() &&
                                this.month > d.getMonth()) ||
                                this.year > d.getYear());
            case "<=":  return ((this.year == d.getYear() &&
                                this.month == d.getMonth() &&
                                this.day <= d.getDay()) ||
                                (this.year == d.getYear() &&
                                this.month <= d.getMonth()) ||
                                this.year <= d.getYear());
            case ">=":  return ((this.year == d.getYear() &&
                                this.month == d.getMonth() &&
                                this.day >= d.getDay()) ||
                                (this.year == d.getYear() &&
                                this.month >= d.getMonth()) ||
                                this.year >= d.getYear());
            default: return false;
        }
    }
}
