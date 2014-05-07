package eu.appservice.adapters;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import eu.appservice.Material;
import eu.appservice.R;
import eu.appservice.Utils;

/**
 * Created by Lukasz on 30.09.13.
 *﹕ ${PROJECT_NAME}
 */
@TargetApi(Build.VERSION_CODES.BASE)
public class ArrayMaterialAdapter extends ArrayAdapter<Material> {

    private final Activity context;
    private List<Material> materialList;

    public ArrayMaterialAdapter(Activity context, int resource, List<Material> lm) {
        super(context, resource, lm);
        this.context = context;
        this.materialList = lm;
    }

    static class ViewHolder {
        public TextView tvPos;
        public TextView tvIndex;
        public TextView tvName;
        public TextView tvAmountUnit;
        public TextView tvStock;


    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        View rowView = convertView;
        if (rowView == null) {
            LayoutInflater li = context.getLayoutInflater();
            rowView = li.inflate(R.layout.material_list_inflate, null, true);
            holder = new ViewHolder();
            holder.tvPos = (TextView) rowView.findViewById(R.id.tvPosMaterialListInflate);
            holder.tvIndex = (TextView) rowView.findViewById(R.id.tvIndexMaterialListInflate);
            holder.tvName = (TextView) rowView.findViewById(R.id.tvNameMaterialLIstInflate);
            holder.tvStock = (TextView) rowView.findViewById(R.id.tvStockMaterialListInflate);
            holder.tvAmountUnit = (TextView) rowView.findViewById(R.id.tvAmountUnitMaterialListInflate);

            rowView.setTag(holder);
        } else {
            holder = (ViewHolder) rowView.getTag();

        }
        holder.tvPos.setText(String.valueOf(position + 1));
        holder.tvIndex.setText((materialList.get(position)).getSplittedIndexByCpglRegule());
        holder.tvName.setText(materialList.get(position).getName());
        holder.tvStock.setText("skład: " + materialList.get(position).getStore());
        holder.tvAmountUnit.setText("ilość: " + Utils.parse(materialList.get(position).getAmount()) + " " + materialList.get(position).getUnit());

        return rowView;
    }
}
