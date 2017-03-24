package com.fuglkrig.server;

/**
 * Created by Magnus on 03.03.2017.
 */
public class Object {
    String name;
    int id;
    int code;

    int speedX;
    int speedY;

    //hvorfor trenger vi denne? Vi vet jo retningen basert på om speedX er negativ eller positiv.
    int directionX;
}
