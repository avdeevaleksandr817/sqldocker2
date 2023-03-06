package ru.netology.test;

import org.junit.jupiter.api.AfterAll;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import ru.netology.data.DataHelper;
import ru.netology.data.SQLHelper;

import static org.testng.AssertJUnit.assertEquals;
import static ru.netology.data.APIHelper.*;
import static ru.netology.data.SQLHelper.*;

public class TransferTest {
    DataHelper.UserData user;
    String token;
    DataHelper.CardData[] cards;
    int indexCartTo;
    int indexCartFrom;
    int balanceCartTo;
    int balanceCartFrom;
    @AfterAll
    static void teardown() {
        SQLHelper.setDown();
    }

    @BeforeMethod
    public void setUp() {
        reloadVerifyCodeTable();
        user = DataHelper.getUser();
        authentication(user);
        var verifyData = DataHelper.getValidCode(user.getLogin());
        token = verification(verifyData);
        cards = getCards(token);
        int i = 0;
        for (DataHelper.CardData card : cards) {
            card.setNumber(getNumberCardById(card.getId()));
            i++;
        }
    }
    //перезагрузка баланса
    @AfterMethod
    public void reloadBalance() {
        reloadBalanceCards(cards[indexCartTo].getId(), balanceCartTo);
        reloadBalanceCards(cards[indexCartFrom].getId(), balanceCartFrom);

    }

    //успешный перевод
    @Test
    public void shouldTransferHappyPath() {
        indexCartTo = 0;
        indexCartFrom = 1;
        balanceCartTo = Integer.parseInt(cards[indexCartTo].getBalance());
        balanceCartFrom = Integer.parseInt(cards[indexCartFrom].getBalance());
        int amount = 2500;

        var transferData = new DataHelper.TransferData(cards[indexCartFrom].getNumber(),
                cards[indexCartTo].getNumber(), String.valueOf(amount));
        transfer(transferData, token);
        assertEquals(balanceCartTo + amount, getBalanceCardById(cards[indexCartTo].getId()));
        assertEquals(balanceCartFrom - amount, getBalanceCardById(cards[indexCartFrom].getId()));
    }

    //Не должен переводить отрицательное значение, граничное значение -1
    @Test
    public void shouldNoTransferLimitValuesMinusOne() {
        indexCartTo = 0;
        indexCartFrom = 1;
        balanceCartTo = Integer.parseInt(cards[indexCartTo].getBalance());
        balanceCartFrom = Integer.parseInt(cards[indexCartFrom].getBalance());
        int amount = -1;

        var transferData = new DataHelper.TransferData(cards[indexCartFrom].getNumber(),
                cards[indexCartTo].getNumber(), String.valueOf(amount));
        transfer(transferData, token);
        assertEquals(balanceCartTo, getBalanceCardById(cards[indexCartTo].getId()));
        assertEquals(balanceCartFrom, getBalanceCardById(cards[indexCartFrom].getId()));
    }
    //Не должен переводить значение 0
    @Test
    public void shouldNoTransferLimitValuesZero() {
        indexCartTo = 0;
        indexCartFrom = 1;
        balanceCartTo = Integer.parseInt(cards[indexCartTo].getBalance());
        balanceCartFrom = Integer.parseInt(cards[indexCartFrom].getBalance());
        int amount = 0;

        var transferData = new DataHelper.TransferData(cards[indexCartFrom].getNumber(),
                cards[indexCartTo].getNumber(), String.valueOf(amount));
        transfer(transferData, token);
        assertEquals(balanceCartTo, getBalanceCardById(cards[indexCartTo].getId()));
        assertEquals(balanceCartFrom, getBalanceCardById(cards[indexCartFrom].getId()));
    }
    //должен переводить положительное значение, граничное значение +1
    @Test
    public void shouldTransferLimitValuesPlusOne() {
        indexCartTo = 0;
        indexCartFrom = 1;
        balanceCartTo = Integer.parseInt(cards[indexCartTo].getBalance());
        balanceCartFrom = Integer.parseInt(cards[indexCartFrom].getBalance());
        int amount = 1;

        var transferData = new DataHelper.TransferData(cards[indexCartFrom].getNumber(),
                cards[indexCartTo].getNumber(), String.valueOf(amount));
        transfer(transferData, token);
        assertEquals(balanceCartTo + amount, getBalanceCardById(cards[indexCartTo].getId()));
        assertEquals(balanceCartFrom - amount, getBalanceCardById(cards[indexCartFrom].getId()));
    }
    //должен перевести значение баланс минус один
    @Test
    public void shouldTransferBoundaryBalanceCartMinusOne() {
        indexCartTo = 0;
        indexCartFrom = 1;
        balanceCartTo = Integer.parseInt(cards[indexCartTo].getBalance());
        balanceCartFrom = Integer.parseInt(cards[indexCartFrom].getBalance());
        int amount = balanceCartFrom - 1;

        var transferData = new DataHelper.TransferData(cards[indexCartFrom].getNumber(),
                cards[indexCartTo].getNumber(), String.valueOf(amount));
        transfer(transferData, token);
        assertEquals(balanceCartTo + amount, getBalanceCardById(cards[indexCartTo].getId()));
        assertEquals(balanceCartFrom - amount, getBalanceCardById(cards[indexCartFrom].getId()));
    }
    //перевод баланса всей карты
    @Test
    public void shouldTransferBoundaryBalanceCartAll() {
        indexCartTo = 0;
        indexCartFrom = 1;
        balanceCartTo = Integer.parseInt(cards[indexCartTo].getBalance());
        balanceCartFrom = Integer.parseInt(cards[indexCartFrom].getBalance());
        int amount = balanceCartFrom;

        var transferData = new DataHelper.TransferData(cards[indexCartFrom].getNumber(),
                cards[indexCartTo].getNumber(), String.valueOf(amount));
        transfer(transferData, token);
        assertEquals(balanceCartTo + amount, getBalanceCardById(cards[indexCartTo].getId()));
        assertEquals(balanceCartFrom - amount, getBalanceCardById(cards[indexCartFrom].getId()));
    }

    //не должен перевести баланс карты +1(перевод больше баланса)
    @Test
    public void shouldNoTransferBoundaryBalanceCartPlusOne() {
        indexCartTo = 0;
        indexCartFrom = 1;
        balanceCartTo = Integer.parseInt(cards[indexCartTo].getBalance());
        balanceCartFrom = Integer.parseInt(cards[indexCartFrom].getBalance());
        int amount = balanceCartFrom + 1;

        var transferData = new DataHelper.TransferData(cards[indexCartFrom].getNumber(),
                cards[indexCartTo].getNumber(), String.valueOf(amount));
        transfer(transferData, token);
        assertEquals(balanceCartTo, getBalanceCardById(cards[indexCartTo].getId()));
        assertEquals(balanceCartFrom, getBalanceCardById(cards[indexCartFrom].getId()));
    }
    //не должен переводить с той же карты
    @Test
    public void shouldNoTransferFromTheSameCard1() {
        indexCartTo = 0;
        indexCartFrom = 0;
        balanceCartTo = Integer.parseInt(cards[indexCartTo].getBalance());
        balanceCartFrom = Integer.parseInt(cards[indexCartFrom].getBalance());
        int amount = 10002;

        var transferData = new DataHelper.TransferData(cards[indexCartFrom].getNumber(),
                cards[indexCartTo].getNumber(), String.valueOf(amount));
        transfer(transferData, token);
        assertEquals(balanceCartTo, getBalanceCardById(cards[indexCartTo].getId()));
        assertEquals(balanceCartFrom, getBalanceCardById(cards[indexCartFrom].getId()));
    }

}
