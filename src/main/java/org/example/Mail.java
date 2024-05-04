package org.example;

import lombok.Data;

@Data
public class Mail {


    private String id;
    private String from_email;
    private String from_name;
    private String to_name;
    private String to_email;
    private String date;
    private String subject;
    private String text;
    private String attachment;


    @Override
    public String toString() {
        return "Письмо:\n" +
                "id= " + id + '\n' +
                "from_email= " + from_email + '\n' +
                "from_name= " + from_name + '\n' +
                "to_name= " + to_name + '\n' +
                "to_email= " + to_email + '\n' +
                "date= " + date + '\n' +
                "subject= " + subject + '\n' +
                "text= " + text + '\n' +
                "attachment= " + attachment;
    }
}
