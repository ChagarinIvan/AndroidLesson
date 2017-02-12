package by.chagarin.androidlesson.objects;

import org.androidannotations.annotations.EBean;

import java.util.ArrayList;
import java.util.List;

@EBean(scope = EBean.Scope.Singleton)
public class Base {
    List<BaseListeners> listeners = new ArrayList<>();

    public void addListener(BaseListeners listener) {
        listeners.add(listener);
    }

    public void doSomething() {
        //Делаем что-то о чем требуется оповестить всех слушателей
        for (BaseListeners listener : listeners) {
            listener.doEvent();
        }
    }

    //запускается когда удаляется трата или поступление
    public void removeElementToListeners() {
        for (BaseListeners listener : listeners) {
            listener.removeElement();
        }
    }
}
