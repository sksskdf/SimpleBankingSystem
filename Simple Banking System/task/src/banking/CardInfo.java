package banking;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class CardInfo {
    private long card_number;
    private int pin;
    private int balance = 0;

    public long getCard_number() {
        return card_number;
    }

    public void setCard_number(int card_number) {
        this.card_number = card_number;
    }

    public int getPin() {
        return pin;
    }

    public void setPin(int pin) {
        this.pin = pin;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public void generate_card() {
        //generate 16 digits card number
        StringBuilder fixed_number = new StringBuilder("400000");
        StringBuilder pin_number = new StringBuilder();
        Random random_number = new Random();


        IntStream.rangeClosed(1, 9).forEach(e -> fixed_number.append(random_number.nextInt(10)));
        IntStream.rangeClosed(1, 4).forEach(e -> pin_number.append(random_number.nextInt(10)));

        char[] chars = fixed_number.toString().toCharArray();
        List<Integer> list = new ArrayList<>();
        for (char c : chars) {
            list.add(Character.getNumericValue(c));
        }

        int sum = 0;

        for (int i = 0; i < list.size(); i++) {
            int num = list.get(i);
            if ((i + 1) % 2 != 0) {
                num *= 2;
            }

            if (num > 9) {
                num -= 9;
            }

            sum += num;
        }

        int checksum = 10 - (sum % 10);
        if (checksum < 10) {
            fixed_number.append(checksum);
        } else {
            fixed_number.append(0);
        }

        this.card_number = Long.parseLong(fixed_number.toString());
        int temp_pin = Integer.parseInt(pin_number.toString());

        if (String.valueOf(temp_pin).length() == 3) {
            pin_number.append(random_number.nextInt(10));
        }

        this.pin = Integer.parseInt(pin_number.toString());
    }

    public static boolean validate_luhn(Long card_number) {
        char[] chars = card_number.toString().toCharArray();
        List<Integer> list = new ArrayList<>();

        for(char c : chars) {
            list.add(Character.getNumericValue(c));
        }

        int valid_num = 0;

        for (int i = 0; i < list.size(); i++) {
            int num = list.get(i);
            if (i % 2 == 0) {
                num *= 2;
            }

            if (num > 9) {
                num -= 9;
            }

            valid_num += num;
        }

        int check_sum = list.get(list.size()-1);

        //if (list.get(0) == 2) return true;
        //2000007269641764
        return (check_sum + (valid_num % 10)) % 10 == 0;
    }
}
