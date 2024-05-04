package org.example;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FishingAnalyzer {

    private static final String url = "https://vir-lab.ru/letter/";

    private final RestTemplate template = new RestTemplate();


    public void run() {

        float fishingCounter = 0.0F;
        ResponseEntity<Mail> response = template.exchange(url, HttpMethod.GET, new HttpEntity<>(null), Mail.class);
        Mail mail = response.getBody();
        System.out.println(mail);

        if (checkEmail(mail.getFrom_email(), mail.getFrom_name())) {
            fishingCounter += 0.2F;
            System.out.printf("""
                    
                    Внимание! адрес отправителя и имя отправителя не совпали:\s
                    адрес отправителя : %s, имя отправителя : %s
                    """, mail.getFrom_email(), mail.getFrom_name());
        }

        if (checkName(mail.getTo_name(), mail.getText())) {
            fishingCounter += 0.123F;
            System.out.printf("""
                    
                    Внимание! в тексте указано не ваше имя:\s
                    имя : %s, текст : %s
                    """, mail.getTo_name(), mail.getText());
        }

        if (checkDate(mail.getDate(), mail.getText())) {
            fishingCounter += 0.13F;
            System.out.printf("""
                    
                    Внимание! дата в тексте позже даты получения письма:\s
                    дата: %s, текст: %s
                    """, mail.getDate(), mail.getText());
        }

        if (checkPhrases(mail.getText())) {
            fishingCounter += 0.2F;
            System.out.println("\nВнимание! В тексте присутствуют подозрительные фразы");
        }

        if (checkUrlInText(mail.getText())) {
            fishingCounter += 0.1F;
            System.out.println("""
                    
                    Внимание! URL в сообщении
                    """);
        }

        if (validateUrl(mail.getText(), mail.getFrom_email())) {
            fishingCounter += 0.3F;
            System.out.println("""
                   
                    Внимание! Url в тексте несоответствует домену почты отправителя
                    """);
        }

        if (checkAttachment(mail.getAttachment())) {
            fishingCounter += 0.15F;
            System.out.printf("""
                    
                    Внимание! Подозрительное вложение %s
                    """, mail.getAttachment());
        }

        String result = fishingCounter >= 0.5F ? "Да" : "Нет";

        System.out.printf("""
                
                Является ли фишингом: %s,\s
                Уровень фишинговости: %s
                """, result, fishingCounter);
    }

    private Boolean checkEmail(String email, String name) {
        String[] parseEmail = email.split("@");
        String parseName = name.replace(" ", "").toLowerCase();

        String domain = parseEmail[1].toLowerCase().substring(0, parseEmail[1].toLowerCase().indexOf("."));

        for (int i = 0; i < domain.length(); i++) {
            if (parseName.charAt(i) != domain.charAt(i))
                return true;
        }
        return false;
    }


    private Boolean checkName(String name, String text) {
        String[] parseName = name.split(" ");
        return !text.contains(parseName[0]);
    }

    private Boolean checkDate(String date, String text) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate dateTime = LocalDate.parse(date, dateTimeFormatter);

        Pattern pattern = Pattern.compile("[0-9]{2}[.][0-9]{2}[.][0-9]{4}");
        Matcher matcher = pattern.matcher(text);

        if (matcher.find()) {
            LocalDate textDate = LocalDate.parse(text.substring(matcher.start(), matcher.end()), DateTimeFormatter.ofPattern("dd.MM.yyyy"));

            return textDate.isAfter(dateTime);
        }

        return false;
    }

    private Boolean checkPhrases(String text) {
        List<String> targetPhrases = List.of(
                "ввод",
                "номер",
                "банк",
                "карт"
        );

        for (String s: targetPhrases) {
            if (text.contains(s))
                return true;
        }
        return false;
    }

    private Boolean checkUrlInText(String text) {
        Pattern pattern = Pattern.compile("(?:https?://|ftps?://|www\\.)(?:(?![.,?!;:()]*(?:\\s|$))\\S){2,}");
        Matcher matcher = pattern.matcher(text);

        return matcher.find();
    }

    private Boolean validateUrl(String text, String email) {
        String domain = email.split("@")[1];

        Pattern pattern = Pattern.compile("(?:https?://|ftps?://|www\\.)(?:(?![.,?!;:()]*(?:\\s|$))\\S){2,}");
        Matcher matcher = pattern.matcher(text);

        if (matcher.find()) {
            String url = text.substring(matcher.start(), matcher.end());

            return !url.contains(domain);
        }

        return false;
    }

    private Boolean checkAttachment(String attachment) {
        List<String> keys = List.of(
                "virus",
                "dmg",
                "exe",
                "trojan",
                "ran",
                "run",
                "mal",
                "ware",
                "run",
                "setup",
                "backdoor",
                "scam"
        );

        for (String key: keys) {
            if (attachment.toLowerCase().contains(key))
                return true;
        }
        return false;
    }

}
