package com.project.onlinevotingsystem.dto;

import com.project.onlinevotingsystem.entity.Gender;

public class UserUpdateDTO {
    private String email;
    private String password;
    private String phoneNumber;
    private Gender gender;
    private String address;
    private String city;
    private String state;
    private String pincode;
    private String profileImageUrl;
    private String panCardNumber;
    private String passportNumber;

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public Gender getGender() { return gender; }
    public void setGender(Gender gender) { this.gender = gender; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public String getPincode() { return pincode; }
    public void setPincode(String pincode) { this.pincode = pincode; }

    public String getProfileImageUrl() { return profileImageUrl; }
    public void setProfileImageUrl(String profileImageUrl) { this.profileImageUrl = profileImageUrl; }

    public String getPanCardNumber() { return panCardNumber; }
    public void setPanCardNumber(String panCardNumber) { this.panCardNumber = panCardNumber; }

    public String getPassportNumber() { return passportNumber; }
    public void setPassportNumber(String passportNumber) { this.passportNumber = passportNumber; }
}
