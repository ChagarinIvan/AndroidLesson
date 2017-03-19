package by.chagarin.androidlesson.categories.fragments;

import by.chagarin.androidlesson.DataLoader;

public class TransactionsCategores extends ChildFragmentOne {

    @Override
    public String getQuery() {
        return DataLoader.CATEGORIES + "/transactions";
    }
}
