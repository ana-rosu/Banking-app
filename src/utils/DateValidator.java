package utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class DateValidator {
    public static Date parseDate(Scanner scanner, String prompt, Date minDate, Date maxDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(false);
        Date date = null;
        boolean validInput = false;

        while (!validInput) {
            System.out.println(prompt);
            String userInput = scanner.nextLine();

            try {
                date = dateFormat.parse(userInput);
                if (minDate != null && date.before(minDate)) {
                    System.out.println("The date cannot be before " + dateFormat.format(minDate) + ". Please try again.");
                } else if (maxDate != null && date.after(maxDate)) {
                    System.out.println("The date cannot be after " + dateFormat.format(maxDate) + ". Please try again.");
                } else {
                    validInput = true;
                }
            } catch (ParseException e) {
                System.out.println("Invalid date format. Please enter the date in yyyy-mm-dd format.");
            }
        }

        return date;
    }
}
