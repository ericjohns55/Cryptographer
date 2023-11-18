package me.ericjohns55.cryptography.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import me.ericjohns55.cryptography.R;
import me.ericjohns55.cryptography.macros.Macro;

/**
 * Provides an adapter for a ListView to present MacroItems visually
 * This class holds all visual data related to a macro and provides the necessary
 * methods for adding, deleting, updating, and rearranging the order in the ListView
 *
 * @author Eric Johns
 */

public class MacroListAdapter extends BaseAdapter {
    // Simple class to hold onto the labels in each ListView item
    static class ViewHolder {
        TextView nameLabel;
        TextView argumentsLabel;
    }

    // Required application information for accessing resources and displaying views
    private final Context context;
    private final LayoutInflater inflater;

    // Holds onto each individual item in the ListView
    private List<MacroListItem> list;

    // Holds onto the index of the last tapped ListView item
    // If none have been selected, this will be -1
    private int lastSelected = -1;

    /**
     * Default constructor for the MacroListAdapter
     * @param context Application context
     * @param list List of items to display in the ListView
     */
    public MacroListAdapter(Context context, List<MacroListItem> list) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.list = list;
    }

    /**
     * Updates the last selected item
     * @param pos Index of the selected item
     */
    public void setSelection(int pos) {
        lastSelected = pos;
        notifyDataSetChanged();
    }

    /**
     * Replaces all data in the adapter with the given argument
     * @param data New data for the ListView
     */
    public void replaceData(List<MacroListItem> data) {
        this.list = data;
        notifyDataSetChanged();
    }

    /**
     * Returns the index of the last selected item
     * Will be -1 if nothing was inputted
     */
    public int getSelection() {
        return lastSelected;
    }

    /**
     * Returns true if an item has been selected, false otherwise
     */
    public boolean hasSelection() {
        return lastSelected != -1;
    }

    /**
     * Replaces the MacroItem at the index with a new one
     * @param index Index to replace
     * @param newItem New item
     */
    public void replaceItem(int index, MacroListItem newItem) {
        if (index < 0 || index >= getCount()) return;

        list.get(index).setName(newItem.getName());
        list.get(index).setMacroItem(newItem.getMacroItem());
        notifyDataSetChanged();
    }

    /**
     * Adds a new item to the bottom of the ListView and sets the last selected item
     * index to be this new one
     * @param item The new item
     */
    public void addItem(MacroListItem item) {
        list.add(item);
        lastSelected = getCount() - 1;
        notifyDataSetChanged();
    }

    /**
     * Deletes an item from the given index from the adapter.
     * If the deleted item was the last view, the last selected position moves down 1
     * @param pos Index to delete
     */
    public void deleteItem(int pos) {
        list.remove(pos);

        if (lastSelected == getCount()) lastSelected--;

        notifyDataSetChanged();
    }

    /**
     * Moves the item at the given position up one in the ListView
     * @param pos Index of item to move
     */
    public void moveItemUp(int pos) {
        if (pos == 0) return;
        lastSelected--;

        Collections.swap(list, pos, pos - 1);
        notifyDataSetChanged();
    }

    /**
     * Moves the item at the given position down one in the ListView
     * @param pos Index of item to move
     */
    public void moveItemDown(int pos) {
        if (pos == getCount() - 1) return;

        lastSelected++;

        Collections.swap(list, pos, pos + 1);
        notifyDataSetChanged();
    }

    /**
     * Converts all the data in the ListView into a Macro object
     * @return Macro with all the data in the ListView
     */
    public Macro toMacro() {
        Macro macro = new Macro();

        for (MacroListItem listItem : list) {
            macro.addMacroItem(listItem.getMacroItem());
        }

        return macro;
    }

    /**
     * Purges all data in the ListView
     */
    public void resetData() {
        list = new ArrayList<>();
        notifyDataSetChanged();
    }

    /**
     * Returns the number of elements in the ListView
     */
    @Override
    public int getCount() {
        return list.size();
    }

    /**
     * Returns the item at the given index in the adapter
     */
    @Override
    public MacroListItem getItem(int i) {
        return list.get(i);
    }

    /**
     * Returns the item ID at the given index in the ListView
     */
    @Override
    public long getItemId(int i) {
        return i;
    }

    /**
     * Generates a ListView item at the given position
     * This will update the name and arguments label to reflect the information
     * contained in the MacroItem
     * If the view is also selected, it will lighten the background
     */
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder = new ViewHolder();

        if (view == null) {     // not a recycled view, create a new one
            view = inflater.inflate(R.layout.macro_list_item, viewGroup, false);
            holder.nameLabel = view.findViewById(R.id.name_view);
            holder.argumentsLabel = view.findViewById(R.id.arguments_view);
            view.setTag(holder);    // hold view information in the tag
        } else {
            holder = (ViewHolder) view.getTag();    // recycle view from tag
        }

        // populating the TextView fields with the information in the MacroListItem
        holder.nameLabel.setText(list.get(i).getName());
        holder.argumentsLabel.setText(list.get(i).parseExtraArgument());

        // lightens the background of the View if it is currently selected
        if (i == lastSelected) {
            view.setBackgroundColor(context.getColor(R.color.lighter_gray));
        } else {
            view.setBackgroundColor(context.getColor(R.color.transparent));
        }

        return view;
    }
}
