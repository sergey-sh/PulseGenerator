package com.shkul.pulsegenerator.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.shkul.pulsegenerator.R;
import com.shkul.pulsegenerator.model.TypeWaveModel;

public class TypeWaveListAdapter extends ArrayAdapter<TypeWaveModel> {
	private final Context context;
	private final TypeWaveModel[] values;

	public TypeWaveListAdapter(Context context, TypeWaveModel[] values) {
		super(context, -1, values);
		this.context = context;
		this.values = values;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.single_list_item, parent, false);
		TextView textView = rowView.findViewById(R.id.aNametxt);
		ImageView imageView = rowView.findViewById(R.id.appIconIV);
		textView.setText(values[position].getName());
		imageView.setImageResource(values[position].getImageId());
		return rowView;
	}
}

