package pbs.sme.survey.helper;

import static pbs.sme.survey.R.*;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import pbs.sme.survey.DB.Database;
import pbs.sme.survey.model.Section;

public class SectionAdapter extends RecyclerView.Adapter<SectionAdapter.ViewHolder> {
    private final Activity context;
    Database dbHandler;
    private List<Section> data;
    Intent intent;

    public SectionAdapter(Activity context, List<Section> list, Database dbHandler, Intent intent){
        //this.arrContacts=arrContacts;
        this.context=context;
        this.dbHandler=dbHandler;
        this.intent=intent;
        this.data=list;
    }

    // method for filtering our recyclerview items.
    public void filterList(List<Section> filterlist) {
        // below line is to add our filtered
        // list in our course array list.
        data = filterlist;
        // below line is to notify our adapter
        // as change in recycler view data.
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(layout.list_section,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Section a=data.get(position);
        holder.code.setText(a.getCode());
        holder.name.setText(a.getName());
        holder.time.setText((a.getCreated()==null || a.getCreated().equals(""))?"": DateHelper.toDate("yyyy-MM-dd'T'HH:mm","dd MMM, HH:mm",a.getCreated()));

        if(a.getCreated()!=null){
            holder.btnView.setVisibility(View.VISIBLE);
        }else{
            holder.btnView.setVisibility(View.INVISIBLE);
        }
        if(a.getStatus()!=null){
            holder.statusbtn.setVisibility(View.VISIBLE);
            holder.statusbtn.setImageResource(a.getStatus());
        }
        else{
            holder.statusbtn.setVisibility(View.INVISIBLE);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                intent.setClass(context,a.getActivity());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView code, name, time;
        ImageView btnView,statusbtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            code=itemView.findViewById(id.txt_code);
            name=itemView.findViewById(id.txt_name);
            time=itemView.findViewById(id.txt_time);
            btnView=itemView.findViewById(id.editbtn);
            statusbtn=itemView.findViewById(id.statusbtn);
        }
    }



}
