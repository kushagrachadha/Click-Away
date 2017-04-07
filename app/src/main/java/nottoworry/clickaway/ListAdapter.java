package nottoworry.clickaway;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import nottoworry.myapp.R;

/**
 * Created by sahil on 11/2/17.
 */

public class ListAdapter extends ArrayAdapter {

    Activity activity;
    int layoutResource;
    ArrayList<ModelData> data = new ArrayList<>();

    public ListAdapter(Activity act, int resource, ArrayList<ModelData> data) {
        super(act, resource, data);
        activity = act;
        layoutResource = resource;
        this.data = data;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if(data!=null)
            return data.size();
        return 0;
    }

    @Override
    public ModelData getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        ModelData t = data.get(position);
        if(t!=null)
            return (t.getId());
        else
            return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        ViewHolder holder = null;

        if(v == null || ((v.getTag())== null)){

            LayoutInflater li = LayoutInflater.from(activity);
            v = li.inflate(R.layout.list_item, null);


            holder = new ViewHolder();

            holder.mTitle = (TextView) v.findViewById(R.id.listName);
            holder.mDes = (TextView) v.findViewById(R.id.listAddress);
            //holder.mThumbnail = (ImageView) v.findViewById(R.id.le_ma_thumbnail);
            v.setTag(holder);

        }else {
            holder = (ViewHolder) v.getTag();
        }

        holder.mymessage = data.get(position);
        holder.mTitle.setText(holder.mymessage.getName());
        holder.mDes.setText(holder.mymessage.getAddress());
        //holder.mThumbnail.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_person_white_48dp));

        return v;
    }

    class ViewHolder{

        ModelData mymessage;
        TextView mTitle;
        TextView mDes;
        ImageView mThumbnail;
    }
}

