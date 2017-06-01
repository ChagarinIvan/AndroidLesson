package by.chagarin.androidlesson;


import org.androidannotations.annotations.EBean;

import java.util.ArrayList;
import java.util.Random;

@EBean(scope = EBean.Scope.Singleton)
public class ColorRandom {
    private Random random = new Random();
    private ArrayList<Integer> colorList = new ArrayList<Integer>() {
        {
            add(R.color.color1);
            add(R.color.color2);
            add(R.color.color3);
            add(R.color.color4);
            add(R.color.color5);
            add(R.color.color6);
            add(R.color.color7);
            add(R.color.color8);
            add(R.color.color9);
            add(R.color.color10);
            add(R.color.color11);
            add(R.color.color12);
            add(R.color.color13);
            add(R.color.color14);
            add(R.color.color15);
        }
    };

    public int getRandomColor() {
        return colorList.get(random.nextInt(14));
    }
}
