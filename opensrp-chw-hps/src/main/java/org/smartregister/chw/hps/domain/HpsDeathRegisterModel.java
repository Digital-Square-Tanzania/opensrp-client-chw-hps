package org.smartregister.chw.hps.domain;

public class HpsDeathRegisterModel {
    private String deathId;
    private String firstName;
    private String middleName;
    private String lastName;
    private String dob;
    private String dod;
    private String sex;
    private String causeOfDeath;

    public String getDeathId() {
        return deathId;
    }

    public void setDeathId(String deathId) {
        this.deathId = deathId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getDod() {
        return dod;
    }

    public void setDod(String dod) {
        this.dod = dod;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getCauseOfDeath() {
        return causeOfDeath;
    }

    public void setCauseOfDeath(String causeOfDeath) {
        this.causeOfDeath = causeOfDeath;
    }


    public String getFullName(){
        return firstName+" "+middleName+" "+lastName;
    }
}
