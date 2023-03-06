package ru.netology.data;

import com.google.gson.Gson;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

public class APIHelper {
    //Спецификация запроса
    private static RequestSpecification requestSpec = new RequestSpecBuilder()
            .setBaseUri("http://localhost")         //базовый урл
            .setPort(9999)                          //установить порт
            .setAccept(ContentType.JSON)            //Принять тип контента JSON
            .setContentType(ContentType.JSON)       //установить тип контента
            .log(LogDetail.ALL)                     //логировать все
            .build();                               //собрать

    //аутентификация пользователя
    public static void authentication(DataHelper.UserData userData) {
        Gson gson = new Gson();                     //установка типа данных
        String user = gson.toJson(userData);
        given()                                     //"дано"
                .spec(requestSpec)                  //Указывается, какая спецификация используется
                .body(user)                         //Передача в теле запроса, который будет преобразован в JSON
                .when()                             //"когда"
                .post("/api/auth")             //На какой путь, относительно BaseUri отправляется запрос
                .then()                             //"тогда ожидаем"
                .statusCode(200);   //Код 200, все хорошо
    }

    //токен верификации пользователя
    public static String verification(DataHelper.VerifyCode verifyData) {
        Gson gson = new Gson();
        String verify = gson.toJson(verifyData);
        return given()                                    //"дано"
                .spec(requestSpec)                        //Указывается, какая спецификация используется
                .body(verify)                             //Передача в теле запроса, который будет преобразован в JSON
                .when()                                   //"когда"
                .post("/api/auth/verification")      //На какой путь, относительно BaseUri отправляется запрос
                .then()                                   //"тогда ожидаем"
                .statusCode(200)          //Код 200, все хорошо
                .extract()                                //извлечь
                .path("token");                      //токен
    }

    //карты
    public static DataHelper.CardData[] getCards(String token) {
        requestSpec.header("Authorization", "Bearer " + token);     //хидер(авторизация, предъявитель + токен)
        return given()                                                                   //"дано"
                .spec(requestSpec)                                                       //Указывается, какая спецификация используется
                .when()                                                                  //"когда"
                .get("/api/cards")                                                  // получить
                .then()                                                                  //"тогда ожидаем"
                .statusCode(200)                                         //Код 200, все хорошо
                .extract()                                                               //извлечь
                .response().getBody().as(DataHelper.CardData[].class);                   //как массив Card
    }

    //перевод
    public static void transfer(DataHelper.TransferData transferData, String token) {
        Gson gson = new Gson();
        String transfer = gson.toJson(transferData);
        requestSpec.header("Authorization", "Bearer " + token);     //хидер(авторизация, предъявитель + токен)
        given()                                                                          //"дано"
                .spec(requestSpec)                                                       //Указывается, какая спецификация используется
                .body(transfer)                                                          //Передача в теле запроса, который будет преобразован в JSON,
                .when()                                                                  //"когда"
                .post("/api/transfer")                                              //На какой путь, относительно BaseUri отправляется запрос
                .then()                                                                  //"тогда ожидаем"
                .statusCode(200);                                         //Код 200, все хорошо
    }
}
