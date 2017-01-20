package by.chagarin.androidlesson;

import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;

import java.util.ArrayList;
import java.util.List;

/**
 * класс для обработки логики по изменению тулбара при выборе элемента и т.д.
 */

abstract class SelectableAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {
    //массив булен/число
    private SparseBooleanArray selectedItems;

    public SelectableAdapter() {
        this.selectedItems = new SparseBooleanArray();
    }

    public boolean isSelected(int position) {
        return getSelectedItem().contains(position);
    }

    public void togglePosition(int position) {
        if (selectedItems.get(position, false)) {
            selectedItems.delete(position);
        } else {
            selectedItems.put(position, true);
        }
        //указание изменить элемент
        notifyItemChanged(position);
    }

    public void clearSelection() {
        List<Integer> selection = getSelectedItem();
        selectedItems.clear();

        for (Integer i : selection) {
            notifyItemChanged(i);
        }
    }

    public int getSelectedItemsCount() {
        return selectedItems.size();
    }

    public List<Integer> getSelectedItem() {
        List<Integer> list = new ArrayList<>(selectedItems.size());

        for (int n = 0; n < selectedItems.size(); n++) {
            list.add(selectedItems.keyAt(n));
        }
        return list;
    }
}
