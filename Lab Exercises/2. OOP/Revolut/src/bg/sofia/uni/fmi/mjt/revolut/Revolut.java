package bg.sofia.uni.fmi.mjt.revolut;


import bg.sofia.uni.fmi.mjt.revolut.account.Account;
import bg.sofia.uni.fmi.mjt.revolut.card.Card;

import java.time.LocalDate;

public class Revolut implements RevolutAPI {

    private Card[] cards;
    private Account[] accounts;

    public Revolut(Account[] accounts, Card[] cards) {
        this.cards = cards;
        this.accounts = accounts;
    }

    @Override
    public boolean pay(Card card, int pin, double amount, String currency) {
        Card cardInRevolut = getCardFromRevolut(card);
        if (isCardValid(cardInRevolut, pin) && cardInRevolut.getType().equals("PHYSICAL")) {
            Account accountInRevolut = findAccount(amount, currency);
            if (isAccountSufficient(accountInRevolut, amount)) {
                accountInRevolut.withdraw(amount);
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean payOnline(Card card, int pin, double amount, String currency, String shopURL) {
        if (shopURL.contains(".biz")) {
            return false;
        }

        Card cardInRevolut = getCardFromRevolut(card);
        if (isCardValid(cardInRevolut, pin)) {
            Account accountInRevolut = findAccount(amount, currency);
            if (isAccountSufficient(accountInRevolut, amount)) {
                accountInRevolut.withdraw(amount);
                if (cardInRevolut.getType().equals("VIRTUALONETIME")) {
                    cardInRevolut.block();
                }
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean addMoney(Account account, double amount) {
        Account accountInRevolut = getAccountFromRevolut(account);
        if (accountInRevolut != null) {
            accountInRevolut.deposit(amount);
            return true;
        }

        return false;
    }

    @Override
    public boolean transferMoney(Account from, Account to, double amount) {
        if (!from.equals(to)) {
            Account fromInRevolut = getAccountFromRevolut(from);
            Account toInRevolut = getAccountFromRevolut(to);
            if (fromInRevolut != null && toInRevolut != null) {
                if (fromInRevolut.getAmount() < amount) {
                    return false;
                }

                fromInRevolut.withdraw(amount);
                String fromCurrency = from.getCurrency();
                String toCurrency = to.getCurrency();
                double convertedAmount = amount;
                if (fromCurrency.equals("BGN") && toCurrency.equals("EUR")) {
                    convertedAmount = getEURfromBGN(amount);
                } else if (fromCurrency.equals("EUR") && toCurrency.equals("BGN")) {
                    convertedAmount = getBGNfromEUR(amount);
                }

                to.deposit(convertedAmount);
                return true;
            }
        }

        return false;
    }

    @Override
    public double getTotalAmount() {
        double amount = 0;
        for (Account account : accounts) {
            amount += getAmountInBGN(account);
        }

        return amount;
    }

    private boolean isCardValid(Card card, int pin) {
        return card != null
                && !card.isBlocked()
                && card.checkPin(pin)
                && card.getExpirationDate().isAfter(LocalDate.now());
    }

    private boolean isAccountSufficient(Account account, double amount) {
        return account != null && account.getAmount() >= amount;
    }

    private Account findAccount(double amount, String currency) {
        for (Account acc : accounts) {
            if (acc.getCurrency().equals(currency) && acc.getAmount() >= amount) {
                return acc;
            }
        }

        return null;
    }

    private double getAmountInBGN(Account account) {
        double amount = account.getAmount();

        return account.getCurrency().equals("BGN") ? amount : getBGNfromEUR(amount);
    }

    private double getBGNfromEUR(double euro) {
        return euro * 1.95583;
    }

    private double getEURfromBGN(double bgn) {
        return bgn / 1.95583;
    }

    private Card getCardFromRevolut(Card card) {
        for (Card c : cards) {
            if (c.equals(card)) {
                return c;
            }
        }

        return null;
    }

    private Account getAccountFromRevolut(Account account) {
        for (Account a : accounts) {
            if (a.equals(account)) {
                return a;
            }
        }

        return null;
    }
}


