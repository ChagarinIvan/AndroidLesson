package by.chagarin.androidlesson.fragments.categorys_fragments;

import java.util.List;

import by.chagarin.androidlesson.DataLoader;
import by.chagarin.androidlesson.objects.Category;

public class PlacesCategores extends ChildFragmentOne {
    @Override
    public String getQuery() {
        return DataLoader.CATEGORIES + "/places";
    }

    @Override
    public List<Category> getCategoryList() {
        return DataLoader.placesCategoryList;
    }
}
