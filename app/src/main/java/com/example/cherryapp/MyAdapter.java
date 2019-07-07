package com.example.cherryapp;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

import static androidx.core.app.ActivityCompat.startActivityForResult;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private List<String> devices_name;
    private List<String> devices_address;
    private static String deviceAddress;
    private String deviceName;

    private TextView tvResult;

    // Provide a reference to the views for each data item
    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView txtName;
        public TextView txtAddress;
        public View layout;

        public ViewHolder(View v) {
            super(v);
            layout = v;
            txtName = (TextView) v.findViewById(R.id.firstLine);
            txtAddress = (TextView) v.findViewById(R.id.secondLine);

        }
    }

    public void add(int position, String device_name, String device_address) {
        devices_name.add(position, device_name);
        devices_address.add(position,device_address);
        notifyItemInserted(position);
    }

    public void remove(int position) {
        devices_name.remove(position);
        devices_address.remove(position);
        notifyItemRemoved(position);
    }

    public MyAdapter(List<String> myDatasetNames, List<String> myDatasetAddresses) {
        devices_name = myDatasetNames;
        devices_address = myDatasetAddresses;
    }

    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.row_layout, parent, false);
        final ViewHolder vh = new ViewHolder(v);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = vh.getAdapterPosition();
            }
        });
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final String name = devices_name.get(position);
        final String address = devices_address.get(position);
        holder.txtName.setText(name);

        holder.txtName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //remove(position);
                //DEVICE_ADDRESS = name;
                Toast.makeText(v.getContext(), "You have clicked: "+name, Toast.LENGTH_SHORT).show();
                //tv.setText(name);
                deviceName = name;
                deviceAddress = address;

                // get position

            }
        });
        holder.txtAddress.setText("Address: " + address);
    }


    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return devices_name.size();
    }

    static public String getDeviceAddress(){
        return deviceAddress;
    }
}

