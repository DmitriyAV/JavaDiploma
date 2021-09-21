package ru.netology.graphics.image;


public class TransformerIntToChar implements TextColorSchema {
    char[] chars = {'#', '$', '@', '%', '*', '+', '-', '\''};
    int period = 255 / chars.length;

    @Override
    public char convert(int color) {
        int i =  Math.abs(color / period);
        return chars[i];
    }
}