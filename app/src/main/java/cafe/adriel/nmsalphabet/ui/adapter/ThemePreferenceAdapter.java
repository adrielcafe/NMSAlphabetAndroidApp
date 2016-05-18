package cafe.adriel.nmsalphabet.ui.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.preference.ListPreference;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.WrapperListAdapter;

import cafe.adriel.nmsalphabet.util.ThemeUtil;

public class ThemePreferenceAdapter extends ListPreference {

    public ThemePreferenceAdapter(Context context) {
        super(context);
    }

    public ThemePreferenceAdapter(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void showDialog(Bundle state) {
        super.showDialog(state);
        AlertDialog dialog = (AlertDialog) getDialog();
        ListView listView = dialog.getListView();
        ListPrefWrapperAdapter fontTypeAdapter = new ListPrefWrapperAdapter(listView.getAdapter());
        listView.setAdapter(fontTypeAdapter);
        int selectedPosition = findIndexOfValue(getValue());
        if (selectedPosition != -1) {
            listView.setItemChecked(selectedPosition, true);
            listView.setSelection(selectedPosition);
        }
    }

    private class ListPrefWrapperAdapter implements WrapperListAdapter {
        private ListAdapter originalAdapter;

        public ListPrefWrapperAdapter(ListAdapter origAdapter) {
            originalAdapter = origAdapter;
        }

        @Override
        public ListAdapter getWrappedAdapter() {
            return originalAdapter;
        }

        @Override
        public boolean areAllItemsEnabled() {
            return getWrappedAdapter().areAllItemsEnabled();
        }

        @Override
        public boolean isEnabled(int position) {
            return getWrappedAdapter().isEnabled(position);
        }

        @Override
        public void registerDataSetObserver(DataSetObserver observer) {
            getWrappedAdapter().registerDataSetObserver(observer);
        }

        @Override
        public void unregisterDataSetObserver(DataSetObserver observer) {
            getWrappedAdapter().unregisterDataSetObserver(observer);
        }

        @Override
        public int getCount() {
            return getWrappedAdapter().getCount();
        }

        @Override
        public Object getItem(int position) {
            return getWrappedAdapter().getItem(position);
        }

        @Override
        public long getItemId(int position) {
            return getWrappedAdapter().getItemId(position);
        }

        @Override
        public boolean hasStableIds() {
            return getWrappedAdapter().hasStableIds();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView view = (TextView) getWrappedAdapter().getView(position, convertView, parent);
            view.setTextSize(30);
            switch (position){
                case 0:
                    view.setText(ThemeUtil.getThemePreview(getContext(), ThemeUtil.THEME_1));
                    break;
                case 1:
                    view.setText(ThemeUtil.getThemePreview(getContext(), ThemeUtil.THEME_2));
                    break;
                case 2:
                    view.setText(ThemeUtil.getThemePreview(getContext(), ThemeUtil.THEME_3));
                    break;
                case 3:
                    view.setText(ThemeUtil.getThemePreview(getContext(), ThemeUtil.THEME_4));
                    break;
                case 4:
                    view.setText(ThemeUtil.getThemePreview(getContext(), ThemeUtil.THEME_5));
                    break;
            }
            return view;
        }

        @Override
        public int getItemViewType(int position) {
            return getWrappedAdapter().getItemViewType(position);
        }

        @Override
        public int getViewTypeCount() {
            return getWrappedAdapter().getViewTypeCount();
        }

        @Override
        public boolean isEmpty() {
            return getWrappedAdapter().isEmpty();
        }
    }
}