package com.lak.tunebreaddriver.Util;

public class User {
    public String fname;
    public String lname;
    public String UserType;
    public String NIC;
    public String email;
    public String phone;
    public Boolean authorize;
    public String vehicleType;
    public String vehicleNumber;
    public String fromDate;
    public String toDate;
    public int mapType;
    public User(){

    }
    public User(String fname, String lname, String userType, String NIC, String email, String phone, String vehicleType, String vehicleNumber, String fromDate, String toDate,int mapType) {
        this.fname = fname;
        this.lname = lname;
        UserType = userType;
        this.NIC = NIC;
        this.email = email;
        this.phone = phone;
        this.vehicleType = vehicleType;
        this.vehicleNumber = vehicleNumber;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.authorize = true;
        this.mapType = mapType;
    }
}
