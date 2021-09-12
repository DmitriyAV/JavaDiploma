package ru.netology.graphics.image;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.net.URL;

public class Converter implements TextGraphicsConverter {

    private int sourceWidth;
    private int sourceHeight;
    private double sourceRatio;
    private int targetWidth;
    private int targetHeight;
    private double targetRatio;

    @Override
    public String convert(String url) throws IOException, BadImageSizeException {
        // Вот так просто мы скачаем картинку из интернета :)
        BufferedImage img = ImageIO.read(new URL(url));

        // Если конвертер попросили проверять на максимально допустимое
        // соотношение сторон изображения, то вам здесь надо сделать эту проверку,
        // и, если картинка не подходит, выбросить исключение BadImageSizeException.
        // Чтобы получить ширину картинки, вызовите img.getWidth(), высоту - img.getHeight()

        sourceWidth = img.getWidth();
        sourceHeight = img.getHeight();

        if (getTargetRatio() > 0){
        setSourceRatio();
        checkRatio();
        }

        // Если конвертеру выставили максимально допустимые ширину и/или высоту,
        // вам надо по ним и по текущим высоте и ширине вычислить новые высоту
        // и ширину.
        int newWidth;
        int newHeight;

        if (getTargetWidth() > 0) {
            newWidth = setNewWidth();
        }
        else newWidth = getSourceWidth();

        if (getTargetHeight() > 0) {
         newHeight = setNewHeight();
        }
        else newHeight = getSourceHeight();

        // Теперь нам надо попросить картинку изменить свои размеры на новые.
        // Последний параметр означает, что мы просим картинку плавно сузиться
        // на новые размеры. В результате мы получаем ссылку на новую картинку, которая
        // представляет собой суженную старую.
        Image scaledImage = img.getScaledInstance(newWidth, newHeight, BufferedImage.SCALE_SMOOTH);

        // Теперь сделаем её чёрно-белой. Для этого поступим так:
        // Создадим новую пустую картинку нужных размеров, заранее указав последним
        // параметром чёрно-белую цветовую палитру:
        BufferedImage bwImg = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_BYTE_GRAY);
        // Попросим у этой картинки инструмент для рисования на ней:
        Graphics2D graphics = bwImg.createGraphics();
        // А этому инструменту скажем, чтобы он скопировал содержимое из нашей суженной картинки:
        graphics.drawImage(scaledImage, 0, 0, null);

        // Теперь в bwImg у нас лежит чёрно-белая картинка нужных нам размеров.
        // Вы можете отслеживать каждый из этапов, просто в любом удобном для
        // вас моменте сохранив промежуточную картинку в файл через:
         //ImageIO.write(imageObject, "png", new File("out.png"));
        // После вызова этой инструкции у вас в проекте появится файл картинки out.png

        // Теперь давайте пройдёмся по пикселям нашего изображения.
        // Если для рисования мы просили у картинки .createGraphics(),
        // то для прохода по пикселям нам нужен будет этот инструмент:
        WritableRaster bwRaster = bwImg.getRaster();

        // Он хорош тем, что у него мы можем спросить пиксель на нужных
        // нам координатах, указав номер столбца (w) и строки (h)
        // int color = bwRaster.getPixel(w, h, new int[3])[0];
        // Выглядит странно? Согласен. Сам возвращаемый методом пиксель — это
        // массив из трёх интов, обычно это интенсивность красного, зелёного и синего.
        // Но у нашей чёрно-белой картинки цветов нет, и нас интересует
        // только первое значение в массиве. Вы спросите, а зачем
        // мы ещё параметром передаём интовый массив на три ячейки?
        // Дело в том, что этот метод не хочет создавать его сам и просит
        // вас сделать это, а сам метод лишь заполнит его и вернёт.
        // Потому что создавать массивы каждый раз слишком медленно. Вы можете создать
        // массив один раз, сохранить в переменную и передавать один
        // и тот же массив в метод, ускорив тем самым программу.

        // Вам осталось пробежаться двойным циклом по всем столбцам (ширина)
        // и строкам (высота) изображения, на каждой внутренней итерации
        // получить степень белого пикселя (int color выше) и по ней
        // получить соответствующий символ c. Логикой превращения цвета
        // в символ будет заниматься другой объект, который мы рассмотрим ниже

        char[][] charsArray = new char[newWidth][newHeight];
        TransformerIntToChar schema = new TransformerIntToChar();
        for (int w = 0; w < newWidth; w++) {

            for (int h = 0; h < newHeight; h++) {

                int color = bwRaster.getPixel(w, h, new int[3])[0];
                char c = schema.convert(color);
                //запоминаем символ c, например, в двумерном массиве или как-то ещё на ваше усмотрение
                saveCharInArray(c, charsArray);
            }
        }

        // Осталось собрать все символы в один большой текст
        // Для того, чтобы изображение не было слишком узким, рекомендую
        // каждый пиксель превращать в два повторяющихся символа, полученных
        // от схемы.

        return printText(charsArray); // Возвращаем собранный текст.
    }

    private String printText(char[][] colorChar) {
        StringBuilder sb = new StringBuilder();

        for (char[] chars : colorChar) {
            for (char aChar : chars) {
                sb
                        .append(aChar)
                        .append('\n');

            }
        }
        return sb.toString();
    }

    private void saveCharInArray(char c, char[][] chars) {
        for (int i = 0; i < chars.length; i++) {
            for (int j = 0; j < chars.length; j++) {
                chars[i][j] = c;
            }
        }
    }

    private void  setSourceRatio() {
        // TODO: 12.09.2021  если sourceRatio больше, то exception
        this.sourceRatio = (double) getSourceWidth() / getSourceHeight();
    }

    public double getSourceRatio() {
        return sourceRatio;
    }

    public double getTargetRatio() {
        return targetRatio;
    }

    private void checkRatio() throws BadImageSizeException {
        if (getSourceRatio() > getTargetRatio()) throw new BadImageSizeException(getSourceRatio(), getTargetRatio());
    }

    private int setNewWidth() {
        // TODO: 12.09.2021 установить новый размер
        int newWidth = 0;
        if (sourceWidth > targetWidth) {
            double ratio = (double) targetWidth / sourceWidth;

            this.sourceHeight = (int) (sourceHeight * ratio);
            newWidth = (int) (targetWidth * ratio);

        } else {
            System.out.println("Width " + sourceWidth + " is ok");
            return sourceWidth;
            }
         return newWidth;
    }

    private int setNewHeight() {
        // TODO: 12.09.2021 установить новый размер
        int newHeight = 0;
        if (sourceHeight > targetHeight) {
            double ratio = (double) targetHeight / sourceHeight;

            this.sourceWidth = (int) (sourceWidth * ratio);
            newHeight = (int) (targetHeight * ratio);

        } else {
            System.out.println("Height " + sourceHeight + " is ok");
            return sourceHeight;
        }
        return newHeight;
    }

    private int getTargetHeight() {
        return targetHeight;
    }

    private int getTargetWidth() {
        return targetWidth;
    }

    public int getSourceWidth() {
        return sourceWidth;
    }

    public int getSourceHeight() {
        return sourceHeight;
    }

    @Override
    public void setMaxWidth(int width) {
        // TODO: 12.09.2021 устанавливает макс. значение
        this.targetWidth = width;
    }

    @Override
    public void setMaxHeight(int height) {
        // TODO: 12.09.2021 устанавливает макс. значение
        this.targetHeight = height;
    }

    @Override
    public void setMaxRatio(double maxRatio) {
        // TODO: 12.09.2021 устанавливает макс. значение
        this.targetRatio = maxRatio;
    }

    @Override
    public void setTextColorSchema(TextColorSchema schema) {

    }

}
