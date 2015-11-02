package com.android.softwear.process;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.softwear.R;
import com.android.softwear.models.Product;
import com.android.volley.toolbox.ImageLoader;
import com.squareup.picasso.Picasso;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

/**
 * Created by Michael on 10/5/2015.
 */
public class ProductsAdapter extends ArrayAdapter<Product> {

    private Activity activity;
    private ArrayList<Product> products;
    private static LayoutInflater inflater = null;
    String money = "$";
    String dept = "Dept: ";
    String qty ="Qty: ";
    static String URI;


    public ProductsAdapter(Activity activity, int textViewResourceId, ArrayList<Product> product) {
        super(activity, textViewResourceId, product);
        try {
            this.activity = activity;
            this.products = product;
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            //imageLoader = new ImageLoader(activity.getApplicationActivity());
        } catch (Exception e) {
            return;
        }
    }

    @Override
    public int getCount() {
        return products.size();
    }

    public Product getItem(Product position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public static class ViewHolder {
        public TextView product_name;
        public TextView product_desc;
        public TextView product_dept;
        public TextView product_price;
        public TextView product_qty;
        public ImageView product_img;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        final ViewHolder holder;
        try {
            if(convertView == null) {
                vi = inflater.inflate(R.layout.activity_search, parent, false);
                holder = new ViewHolder();
                holder.product_name = (TextView) vi.findViewById(R.id.product_name);
                holder.product_dept = (TextView) vi.findViewById(R.id.product_dept);
                holder.product_desc = (TextView) vi.findViewById(R.id.product_desc);
                holder.product_price = (TextView) vi.findViewById(R.id.product_price);
                holder.product_qty = (TextView) vi.findViewById(R.id.product_qty);
                holder.product_img = (ImageView) vi.findViewById(R.id.icon);
                vi.setTag(holder);
            }
            else {
                holder = (ViewHolder) vi.getTag();
            }
            URI = "http://www.michaelscray.com/Softwear/graphics/";
            URI += products.get(position).getProduct_img();
            Uri uri = Uri.parse(URI + products.get(position).getProduct_img());
            holder.product_name.setText(products.get(position).getProduct_name());
            holder.product_desc.setText(products.get(position).getProduct_desc());
            holder.product_dept.setText(dept + products.get(position).getProduct_dept());
            holder.product_price.setText(money + String.valueOf(products.get(position).getPrice()));
            holder.product_qty.setText(qty + String.valueOf(products.get(position).getProduct_qty()));
            Picasso.with(getContext()).load(URI).error(R.mipmap.ic_launcher).into(holder.product_img);
        }
        catch(Exception e) {

        }

        return vi;
    }

}