package by.chagarin.androidlesson.objects;

public interface BaseListeners {
    //когда появляется новая транзакция
    void doEvent();

    //когда удаляется трата или поступление
    void removeElement();
}
