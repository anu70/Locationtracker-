package in.org.verkstad.locationtracker;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by anu on 3/19/2016.
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    LayoutInflater inflater;
    ArrayList<Double> latitude,longitude,speed,direction;
    ArrayList<String> time;
    public RecyclerViewAdapter(Context context,ArrayList<Double> latitude,ArrayList<Double> longitude,ArrayList<Double> speed,ArrayList<Double> direction,ArrayList<String> time){
        inflater=LayoutInflater.from(context);
        this.latitude=latitude;
        this.longitude=longitude;
        this.speed=speed;
        this.direction=direction;
        this.time=time;

    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.list_row,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        double latitude1 = latitude.get(position);
        double longitude1 = longitude.get(position);
        double speed1 = speed.get(position);
        double direction1 = direction.get(position);
        String time1 = time.get(position);
        holder.location.setText(latitude1+","+longitude1);
        holder.direction.setText(""+direction1);
        holder.speed.setText(""+speed1);
        holder.time.setText(time1);

    }

    @Override
    public int getItemCount() {
        return latitude.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        TextView location,speed,direction,time;
        public ViewHolder(View itemView) {
            super(itemView);
            location= (TextView) itemView.findViewById(R.id.location);
            speed = (TextView) itemView.findViewById(R.id.speed);
            direction= (TextView) itemView.findViewById(R.id.direction);
            time= (TextView) itemView.findViewById(R.id.time);
        }
    }
}
