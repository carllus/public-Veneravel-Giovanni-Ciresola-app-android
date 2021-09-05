package com.padregiovanniciresola.padregiovanniciresola;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.venerabileciresola.appandroid.R;
import java.util.ArrayList;

public class AdapterListView extends BaseAdapter {
    private ArrayList<ItemListView> itens;
    private LayoutInflater mInflater;

    public long getItemId(int i) {
        return (long) i;
    }

    public AdapterListView(Context context, ArrayList<ItemListView> arrayList) {
        this.itens = arrayList;
        this.mInflater = LayoutInflater.from(context);
    }

    public int getCount() {
        return this.itens.size();
    }

    public ItemListView getItem(int i) {
        return this.itens.get(i);
    }

    public View getView(int i, View view, ViewGroup viewGroup) {
        View inflate = this.mInflater.inflate(R.layout.item_listview, (ViewGroup) null);
        ((TextView) inflate.findViewById(R.id.text)).setText(this.itens.get(i).getTexto());
        return inflate;
    }
}
