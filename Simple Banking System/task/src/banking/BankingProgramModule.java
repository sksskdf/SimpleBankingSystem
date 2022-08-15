package banking;

import java.util.Scanner;

public class BankingProgramModule {
    final static Scanner sc = new Scanner(System.in);
    public static final String MAIN_MENU = "1. Create an account\n" +
            "2. Log into account\n" +
            "0. Exit\n";
    public static final String CARD_CREATED_INFO = "Your card has been created\n" +
            "Your card number:\n" +
            "%d\n" +
            "Your card PIN:\n" +
            "%d\n\n";
    public static final String ACCOUNT_ACCESS_SUCCESSFULLY_MENU = "1. Balance\n" +
            "2. Add income\n" +
            "3. Do transfer\n" +
            "4. Close account\n" +
            "5. Log out\n" +
            "0. Exit\n";
    static DBConnection dbconn;

    static void Running(String[] args) {
        dbconn = new DBConnection(args);
        CardInfo cardInfo = new CardInfo();
        Loop:
        while(true) {
            System.out.println(MAIN_MENU);

            switch (sc.nextInt()) {
                case 1 : {
                    cardInfo.generate_card();
                    dbconn.insertCardInfo(cardInfo);
                    System.out.printf(CARD_CREATED_INFO,
                            cardInfo.getCard_number(),
                            cardInfo.getPin());
                    break;
                }
                case 2 : {
                    System.out.println("Enter your card number:\n");
                    long card_number = sc.nextLong();
                    System.out.println("Enter your PIN:\n");
                    int pin_number = sc.nextInt();

                    if (dbconn.authentication(card_number, pin_number) == 1) {
                        System.out.println("You have successfully logged in!\n");
                        Loop2:
                        while(true) {
                            System.out.println(ACCOUNT_ACCESS_SUCCESSFULLY_MENU);
                            switch (sc.nextInt()) {
                                case 1 : { //Balance
                                    System.out.printf("Balance: %d\n", dbconn.getBalance(card_number));
                                    break;
                                } case 2 : { //Add income
                                    System.out.println("Enter income:\n");
                                    int income = sc.nextInt();
                                    dbconn.addIncome(income, card_number);
                                    System.out.println("Income was added!");
                                    break;
                                } case 3 : { // Do transfer
                                    System.out.println("Transfer");
                                    System.out.println("Enter card number:\n");
                                    Long target_card_number = sc.nextLong();
                                    if (CardInfo.validate_luhn(target_card_number)) {
                                        System.out.println("Probably you made a mistake in the card number.\nPlease try again!\n");
                                        break;
                                    } else {
                                        int check = dbconn.authentication(target_card_number);

                                        if (target_card_number == card_number) {
                                            System.out.println("You can't transfer money to the same account!\n");
                                            break;
                                        }

                                        if (check == 0) {
                                            System.out.println("Such a card does not exist.\n");
                                            break;
                                        }

                                        System.out.println("Enter how much money you want to transfer\n");
                                        int money_to_transfer = sc.nextInt();
                                        int check_balance = dbconn.check_balance(money_to_transfer, card_number);

                                        if (check_balance == 0) {
                                            System.out.println("Not enough money!");
                                            break;
                                        } else {
                                            dbconn.transfer_money(money_to_transfer, card_number, target_card_number);
                                            System.out.println("Success!");
                                            break;
                                        }
                                    }
                                } case 4 : { // Close account
                                    dbconn.deleteCardInfo(card_number);
                                    System.out.println("The account has been closed!");
                                    break Loop2;
                                } case 5 : { // Log out
                                    System.out.println("You have successfully logged out!");
                                    break Loop2;
                                } case 0 : { // Exit
                                    System.out.println("Bye!");

                                    break Loop;
                                }
                            }
                        }
                    } else {
                        System.out.println("Wrong card number or PIN!");
                    }
                    break;
                }
                case 0 : {
                    System.out.println("Bye!");
                    break Loop;
                }
            }
        }
    }
}
