package by.chagarin.androidlesson.categories.fragments;

import by.chagarin.androidlesson.DataLoader;

public class PlacesCategores extends ChildFragmentOne {
    @Override
    public String getQuery() {
        return DataLoader.CATEGORIES + "/places";
    }
}
