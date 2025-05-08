package org.smartregister.chw.hps.domain;

import org.joda.time.DateTime;
import org.joda.time.Period;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import timber.log.Timber;

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

    public int getAge() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        try {
            Date birthDate = sdf.parse(dob);
            Calendar birthCal = Calendar.getInstance();
            birthCal.setTime(birthDate);

            Calendar today = Calendar.getInstance();
            int age = today.get(Calendar.YEAR) - birthCal.get(Calendar.YEAR);

            // Adjust age if today's date is before the birth date this year
            int currentMonth = today.get(Calendar.MONTH);
            int birthMonth = birthCal.get(Calendar.MONTH);
            if (currentMonth < birthMonth ||
                    (currentMonth == birthMonth && today.get(Calendar.DAY_OF_MONTH) < birthCal.get(Calendar.DAY_OF_MONTH))) {
                age--;
            }
            return age;
        } catch (Exception e) {
            // Handle exception if date format is invalid
            Timber.e(e);
            return -1;
        }
    }
}
