package ru.netology.graphics.image;

public class TransformerIntToChar implements TextColorSchema {
    char[] chars = {'#', '$', '@', '%', '*', '+', '-', '\''};

    @Override
    public char convert(int color) {
        char targetChar = ' ';
        char[] currentChar = {(char) color};
        for (int i = 0; i < currentChar.length; i++){
           targetChar = currentChar[i / 8] = chars[i];
        }
        return targetChar;
    }
}
