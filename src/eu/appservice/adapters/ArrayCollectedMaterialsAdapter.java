package eu.appservice.adapters;

import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import eu.appservice.CollectedMaterial;
import eu.appservice.R;
import eu.appservice.Utils;

/**
 * Created by Lukasz on 27.09.13.
 */
@TargetApi(Build.VERSION_CODES.BASE)
public class ArrayCollectedMaterialsAdapter extends ArrayAdapter<CollectedMaterial> {


    private final Activity context;
    private List<CollectedMaterial> collectedMaterials;
    private SparseBooleanArray mSelectedItemIds;

    public ArrayCollectedMaterialsAdapter(Activity context, int resource,
                                          List<CollectedMaterial> collectedMaterials) {
        super(context, resource, collectedMaterials);

        this.context = context;
        this.collectedMaterials = collectedMaterials;
        mSelectedItemIds = new SparseBooleanArray();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        View rowView = convertView;
        if (rowView == null) {
            LayoutInflater layoutInflater = context.getLayoutInflater();
            rowView = layoutInflater.inflate(R.layout.list_inflate, null, true);
            holder = new ViewHolder();
            holder.tvIndex = (TextView) rowView.findViewById(R.id.tvIndex);
            holder.tvName = (TextView) rowView.findViewById(R.id.tvIndexNazwa);
            holder.tvDate = (TextView) rowView.findViewById(R.id.tvData);
            holder.tvPickedAmount = (TextView) rowView.findViewById(R.id.tvIloscJednostkaMpk);
            holder.tvPos = (TextView) rowView.findViewById(R.id.tvPozycjaNaLiscie);
            holder.tvMpk = (TextView) rowView.findViewById(R.id.tvWydaneOpis);
            holder.imSignification = (ImageView) rowView.findViewById(R.id.imageViewListAdapter);
            rowView.setTag(holder);

        } else {
            holder = (ViewHolder) rowView.getTag();
        }

        CollectedMaterial pm = collectedMaterials.get(position);


        String isToZero;
        if (pm.isToZero()) isToZero = "na zero";
        else isToZero = "";

        int resultPosition = collectedMaterials.size() - position;


        holder.tvIndex.setText(pm.getSplittedIndexByCpglRegule());
        holder.tvName.setText(pm.getName());
        holder.tvDate.setText(Utils.reformatDate(pm.getDate(),context.getString(R.string.date_format_out)));///reformatDate() testing reformat date -reformatDate()<
        holder.tvPickedAmount.setText("ilość: " + Utils.parse(pm.getCollectedQuantity()) + " " + pm.getUnit() + "       " + isToZero);
        holder.tvMpk.setText("MPK: " + pm.getMpk() + "   Zlecenie: " + pm.getBudget());
        holder.tvPos.setText(String.valueOf(resultPosition));
        if (!pm.getSignAddress().equals("") && pm.getSignAddress() != null)
            holder.imSignification.setVisibility(View.VISIBLE);
        else
            holder.imSignification.setVisibility(View.INVISIBLE);

        rowView.setBackgroundColor(mSelectedItemIds.get(position) ? 0x9934B5E4 : Color.TRANSPARENT);
        return rowView;


    }

    @Override
    public void add(CollectedMaterial collectedMaterial) {
        collectedMaterials.add(collectedMaterial);
        notifyDataSetChanged();
    }

    @Override
    public void remove(CollectedMaterial object) {
        collectedMaterials.remove(object);
        notifyDataSetChanged();
    }

    public void selectView(int position, boolean value) {
        if (value) {
            mSelectedItemIds.put(position, value);
        } else {
            mSelectedItemIds.delete(position);
        }
        notifyDataSetChanged();

    }

    public void removeSelection() {
        mSelectedItemIds = new SparseBooleanArray();
        notifyDataSetChanged();
    }

    public void toggleSelection(int position) {
        selectView(position, !mSelectedItemIds.get(position));
        notifyDataSetChanged();
    }

    public int getSelectedCount() {
        return mSelectedItemIds.size();
    }

    public SparseBooleanArray getSelectedItemIds() {
        return mSelectedItemIds;
    }

    static class ViewHolder {
        public TextView tvPos;
        public TextView tvIndex;
        public TextView tvName;
        public TextView tvDate;
        public TextView tvMpk;
        public ImageView imSignification;
        public TextView tvPickedAmount;

    }





}

