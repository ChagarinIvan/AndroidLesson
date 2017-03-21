package by.chagarin.androidlesson;


import android.graphics.Color;

import org.androidannotations.annotations.EBean;

import java.util.Random;

@EBean(scope = EBean.Scope.Singleton)
public class ColorRandom {
    private Random random = new Random();

    public int getRandomColor() {
        final int baseColor = Color.WHITE;

        final int baseRed = Color.red(baseColor);
        final int baseGreen = Color.green(baseColor);
        final int baseBlue = Color.blue(baseColor);

        final int red = (baseRed + random.nextInt(256)) / 2;
        final int green = (baseGreen + random.nextInt(256)) / 2;
        final int blue = (baseBlue + random.nextInt(256)) / 2;

        int[] colorParam = new int[]{red, green, blue};
        int flag = random.nextInt(1);
        if (flag == 0) {
            int number = random.nextInt(2);
            colorParam[number] = colorParam[number] / 10;
        } else {
            int number = random.nextInt(2);
            for (int n = 0; n < 3; n++) {
                if (n != number) {
                    colorParam[number] = colorParam[number] / 10;
                }
            }
        }
        return Color.rgb(colorParam[0], colorParam[1], colorParam[2]);
    }
}
