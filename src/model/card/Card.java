package model.card;

import java.util.*;

public class Card {
    private final String cardNumber;
    private final String cardHolderName;
    private final Date expiryDate;
    private final int CVV;
    static private Set<String> usedNumbers = new HashSet<>();
    public Card(String cardHolderName) {
        this.cardHolderName = cardHolderName;
        this.CVV = this.generateCVV() ;
        this.expiryDate = this.generateExpiryDate();
        String generatedNumber = this.generateCardNumber();
        while(usedNumbers.contains(generatedNumber))
            generatedNumber = this.generateCardNumber();
        this.cardNumber = generatedNumber;
        usedNumbers.add(this.cardNumber);
    }
    private Date generateExpiryDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.YEAR, 3);
        return calendar.getTime();
    }

    private String generateCardNumber(){
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 16; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }

    private int generateCVV(){
        var rand = new Random();
        return 100 + rand.nextInt(899);
    }

    @Override
    public String toString() {
        return "Card{" +
                "cardNumber='" + cardNumber + '\'' +
                ", cardHolderName='" + cardHolderName + '\'' +
                ", expiryDate=" + expiryDate +
                ", CVV=" + CVV +
                '}';
    }
}
