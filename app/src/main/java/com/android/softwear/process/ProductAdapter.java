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
public class ProductAdapter extends ArrayAdapter<Product> {

    private Context activity;
    private ArrayList<Product> products;
    private static LayoutInflater inflater = null;
    public static View currentView;
    public static ViewHolder viewHolder;
    String money = "$";
    String dept = "Dept: ";
    String qty ="Qty: ";
    Bitmap bit;
    static int pos;
    static String URI;


    public ProductAdapter(Context activity, int textViewResourceId, ArrayList<Product> product) {
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
                vi = inflater.inflate(R.layout.activity_product_view, parent, false);
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
            /*
            try {
                InputStream is = new URL(URI).openStream();
                Bitmap myBit = BitmapFactory.decodeStream(is);
                holder.product_img.setImageBitmap(getResizedBitmap(myBit, 250, 250));
            } catch(Exception e) {
                e.printStackTrace();
            }
            /*
            try {
                pos = position;
                setPos(pos);
                setURI(URI);
                setCurrentView(convertView);
                setCurrentHolder(holder);
                new getImageView().execute();
            } catch (Exception e) {
                e.printStackTrace();
            }
            */
            //URLConnection con = new URL(URI).openConnection();
            //con.connect();
            //holder.product_img.setImageURI(uri);
            //holder.product_img.setImageBitmap(BitmapFactory.decodeStream(con.getInputStream()));
            //Picasso.with(getContext()).load(URI + products.get(position).getProduct_img()).into(holder.product_img);
            //holder.product_img.setTag(URI)
        }
        catch(Exception e) {

        }

        return vi;
    }
    /*
    private class getImageView extends AsyncTask<Void, Void, Void> {
        String tempURI = getURI();
        int tempPos = getPos();
        View vi = getCurrentView();
        ViewHolder tempHolder = getCurrentHolder();

        protected void onPreExecute() {
            tempHolder.product_img = (ImageView) vi.findViewById(R.id.icon);
            vi.setTag(tempHolder);
        }

        protected Void doInBackground(Void... arg0)  {
            bit = getBitmap(tempURI);
            return null;
        }

        protected void onPostExecute(Void result) {
            tempHolder.product_img.setImageBitmap(bit);
        }

    }

    private class getBitmapView extends AsyncTask<Void, Void, Bitmap> {
        String tempURI = getURI();
        int tempPos = getPos();
        View vi = getCurrentView();
        ViewHolder tempHolder = getCurrentHolder();

        protected void onPreExecute() {
            tempHolder.product_img = (ImageView) vi.findViewById(R.id.icon);
            vi.setTag(tempHolder);
        }

        protected Bitmap doInBackground(Void... arg0)  {
            bit = getBitmap(tempURI);
            return null;
        }

        protected void onPostExecute(Void result) {
            setBitmap(bit);
        }

    }

    public Bitmap getBitmap(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setDoInput(true);
            conn.connect();
            InputStream input = conn.getInputStream();
            Bitmap bm = BitmapFactory.decodeStream(input);
            return bm;
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void setBitmap(Bitmap bit) {
        this.bit = bit;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    public int getPos() {
        return pos;
    }

    public void setURI(String URI) {
        this.URI = URI;
    }

    public String getURI() {
        return URI;
    }

    public void setCurrentView(View currentView) {
        this.currentView = currentView;
    }

    public View getCurrentView() {
        return currentView;
    }

    public void setCurrentHolder(ViewHolder viewHolder) {
        this.viewHolder = viewHolder;
    }

    public ViewHolder getCurrentHolder() {
        return viewHolder;
    }

    private void scaleImage(ImageView views)
    {
        // Get the ImageView and its bitmap
        ImageView view = views;
        Drawable drawing = view.getDrawable();
        if (drawing == null) {
            return; // Checking for null & return, as suggested in comments
        }
        Bitmap bitmap = ((BitmapDrawable)drawing).getBitmap();

        // Get current dimensions AND the desired bounding box
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int bounding = dpToPx(250);
        Log.i("Test", "original width = " + Integer.toString(width));
        Log.i("Test", "original height = " + Integer.toString(height));
        Log.i("Test", "bounding = " + Integer.toString(bounding));

        // Determine how much to scale: the dimension requiring less scaling is
        // closer to the its side. This way the image always stays inside your
        // bounding box AND either x/y axis touches it.
        float xScale = ((float) bounding) / width;
        float yScale = ((float) bounding) / height;
        float scale = (xScale <= yScale) ? xScale : yScale;
        Log.i("Test", "xScale = " + Float.toString(xScale));
        Log.i("Test", "yScale = " + Float.toString(yScale));
        Log.i("Test", "scale = " + Float.toString(scale));

        // Create a matrix for the scaling and add the scaling data
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);

        // Create a new bitmap and convert it to a format understood by the ImageView
        Bitmap scaledBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
        width = scaledBitmap.getWidth(); // re-use
        height = scaledBitmap.getHeight(); // re-use
        BitmapDrawable result = new BitmapDrawable(scaledBitmap);
        Log.i("Test", "scaled width = " + Integer.toString(width));
        Log.i("Test", "scaled height = " + Integer.toString(height));

        // Apply the scaled bitmap
        view.setImageDrawable(result);

        // Now change ImageView's dimensions to match the scaled image
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) view.getLayoutParams();
        params.width = width;
        params.height = height;
        view.setLayoutParams(params);

        Log.i("Test", "done");
    }

    private int dpToPx(int dp)
    {
        //float density = getApplicationContext().getResources().getDisplayMetrics().density;
        //return Math.round((float)dp * density);
        return -1;
    }

    public Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
        return resizedBitmap;
    }
    */
}