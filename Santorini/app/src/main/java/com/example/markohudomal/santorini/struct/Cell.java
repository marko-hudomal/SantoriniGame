package com.example.markohudomal.santorini.struct;

import android.graphics.Color;

public class Cell {
    public int height=0;
    public int player=-1;

    public int color=0;
    public int x;
    public  int y;

    //Konstruktor
    public Cell(int height, int player, int color, int x, int y) {
        this.height = height;
        this.player = player;
        this.color = color;
        this.x = x;
        this.y = y;
    }

    //Geter i Seter za igraca koji je na toj celiji, -1 u suprotnom
    public int getPlayer() {
        return player;
    }
    public void setPlayer(int player) {
        this.player = player;
    }

    //Geter i Seter za visinu celije
    public int getHeight() {
        return height;
    }
    public void setHeight(int height) {
        this.height = height;
    }

    //Geter i Seter koordinata celije
    public int getX() {
        return x;
    }
    public void setX(int x) {
        this.x = x;
    }
    public int getY() {
        return y;
    }
    public void setY(int y) {
        this.y = y;
    }

    //Geter i Seter boje celije
    public int getColor() {
        return color;
    }
    public void setColor(int color) {
        this.color = color;
    }

    //Inkrementiranje nivoa celije
    public void incHeight()
    {
        this.height++;

    }
}
