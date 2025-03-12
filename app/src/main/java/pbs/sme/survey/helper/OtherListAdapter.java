package pbs.sme.survey.helper;

import static pbs.sme.survey.activity.MyActivity.getTimeNow;
import static pbs.sme.survey.activity.MyActivity.getTimeNowwithSeconds;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.widget.ImageViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.net.UnknownHostException;
import java.util.List;

import pbs.sme.survey.DB.Database;
import pbs.sme.survey.R;
import pbs.sme.survey.activity.GeoActivity;
import pbs.sme.survey.activity.MyActivity;
import pbs.sme.survey.activity.OtherListActivity;
//import pbs.sme.survey.activity.NHouseActivity;
import pbs.sme.survey.api.ApiClient;
import pbs.sme.survey.api.ApiInterface;
import pbs.sme.survey.model.Block;
import pbs.sme.survey.model.Constants;
import pbs.sme.survey.model.Household;
import pbs.sme.survey.model.NCH;
import pbs.sme.survey.model.House;
import pbs.sme.survey.model.Section12;
import pbs.sme.survey.online.Returning;
import pbs.sme.survey.online.Sync;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OtherListAdapter extends RecyclerView.Adapter<OtherListAdapter.ViewHolder> {
    private final Activity context;
    Database dbHandler;
    private List<Section12> data;
    String env;

    public OtherListAdapter(Activity context, List<Section12> list, Database dbHandler, String env){
        //this.arrContacts=arrContacts;
        this.context=context;
        this.dbHandler=dbHandler;
        this.data=list;
        this.env=env;
    }

    // method for filtering our recyclerview items.
    public void filterList(List<Section12> filterlist) {
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
        View v = LayoutInflater.from(context).inflate(R.layout.list_entries,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setIsRecyclable(false);
        final Section12 a=data.get(position);
        holder.sno.setText(String.valueOf(a.flag));

        holder.title.setText(a.title);
        int listed=dbHandler.queryInteger("SELECT count(*) from "+Section12.class.getSimpleName()+" where ENV='"+env+"' AND  is_deleted=0 and flag="+a.flag);
        if(listed==0){
            holder.imgtick.setVisibility(View.INVISIBLE);
        }
        if(a.sync_time!=null){
            holder.imgtick.setVisibility(View.VISIBLE);
            ImageViewCompat.setImageTintList(holder.imgtick, ColorStateList.valueOf(context.getColor(R.color.success)));
        }
        else if(a.env==null || a.env.isEmpty()){
            holder.imgtick.setVisibility(View.INVISIBLE);
            holder.emp.setText("0");
        }
        else{
            holder.imgtick.setVisibility(View.VISIBLE);
            if(a.emp_count!=null){
                holder.emp.setText(String.valueOf(a.emp_count));
            }
            else{
                holder.emp.setText("0");
            }

        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cmsg=withinDate(a);
                if(cmsg!=null){
                    Toast.makeText(context,cmsg,Toast.LENGTH_LONG).show();
                    return;
                }

                Block b = (Block) context.getIntent().getSerializableExtra(Constants.EXTRA.IDX_BLOCK);
                Intent i = new Intent(context, GeoActivity.class);
                i.putExtra(Constants.EXTRA.IDX_BLOCK, b);
                if(a.env!=null && !a.env.isEmpty()){
                    i.putExtra(Constants.EXTRA.IDX_HOUSE, a);
                }
                else{
                    i.putExtra(Constants.EXTRA.IDX_ID, a.flag);
                    i.putExtra(Constants.EXTRA.IDX_TITLE,a.title);
                    i.putExtra(Constants.EXTRA.IDX_EMP,a.emp_count);
                }
                context.startActivity(i);

            }
        });

        holder.btnView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //Creating the instance of PopupMenu
                PopupMenu popup = new PopupMenu(context, holder.btnView);
                //Inflating the Popup using xml file
                popup.getMenuInflater().inflate(R.menu.entry, popup.getMenu());

                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getTitle().toString()){
                            case "Upload":
                                ((MyActivity) context).updateSyncTimeToNow();
                                //syncHouse(a,holder.btnView, holder.imgtick);
                                break;
                            case "Edit":
                                String cmsg=withinDate(a);
                                if(cmsg!=null){
                                    Toast.makeText(context,cmsg,Toast.LENGTH_LONG).show();
                                    break;
                                }
                                /*Intent i = new Intent(context, NHouseActivity.class);
                                i.putExtra(Constants.EXTRA.IDX_HOUSE, a);
                                context.startActivity(i);*/
                                break;
                            case "Delete":
                                Toast.makeText(context,"NCH Household cannot be Deleted",Toast.LENGTH_LONG).show();
                                break;
                        }
                        return true;
                    }
                });
                popup.show();//showing popup menu
            }
        });

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView sno,title, emp;
        ImageView btnView, imgtick;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            sno=itemView.findViewById(R.id.tv_sno);
            title=itemView.findViewById(R.id.tv_title);
            emp=itemView.findViewById(R.id.tv_emp);
            btnView=itemView.findViewById(R.id.menubtn);
            imgtick=itemView.findViewById(R.id.imgtick);
        }
    }

    public void syncHouse(final House h, ImageView view, ImageView tick){
        boolean status=(h.sync_time!=null?true:false);
        if(!status && h.getHHs()>0){
            Toast.makeText(context,"Uploading Please Wait",Toast.LENGTH_SHORT).show();
            List<Household> h2=dbHandler.queryRawSql(Household.class,"SELECT * FROM "+ Household.class.getSimpleName()+" WHERE ENV='"+env+"' AND  HOUSE_UID='"+h.house_uid+"' and sync_time is null;");
            if(h2.size()>0){
                Sync s=new Sync();
                s.setList_hh(h2);
                view.setEnabled(false);
                Call<Returning> call= ApiClient.getApiClient().create(ApiInterface.class).upload(s);
                call.enqueue(new Callback<Returning>() {
                    @Override
                    public void onResponse(Call<Returning> call, Response<Returning> response) {
                        view.setEnabled(true);
                        Returning u=response.body();
                        if(u!=null){
                            Toast.makeText(context,u.getMsg(),Toast.LENGTH_SHORT).show();
                            if(u.getCode()==1){
                                h.sync_time=getTimeNow();
                                dbHandler.execSql("UPDATE "+ House.class.getSimpleName()+" SET sync_time='"+ getTimeNowwithSeconds()+"' where ENV='"+env+"' AND  house_uid='"+h.house_uid+"';");
                                dbHandler.execSql("UPDATE "+ Household.class.getSimpleName()+" SET sync_time='"+ getTimeNowwithSeconds()+"' where ENV='"+env+"' AND  house_uid='"+h.house_uid+"';");
                                ImageViewCompat.setImageTintList(tick, ColorStateList.valueOf(context.getColor(R.color.success)));
                                ((OtherListActivity)context).updateUpload();
                                StatsUtil.updateSyncTimeToNow();
                            }
                        }
                        else {
                            Toast.makeText(context,"Error Response Format",Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<Returning> call, Throwable t) {
                        view.setEnabled(true);
                        if((t instanceof UnknownHostException)){
                            Toast.makeText(context,"Network Issue, Unable to connect to server",Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(context,t.toString(),Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
            else {
                Toast.makeText(context,"Household Already Synced",Toast.LENGTH_SHORT).show();
            }

        }
        else if(h.getHHs()==0){
            Toast.makeText(context,"Please First Add Household",Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(context,"House Already Synced",Toast.LENGTH_SHORT).show();
        }
    }

    public String withinDate(Section12 a){
        try{
            String sdate=dbHandler.queryString("SELECT start_date from "+ NCH.class.getSimpleName()+" where (is_deleted='false' or is_deleted=0) and id="+a.flag);
            String edate=dbHandler.queryString("SELECT end_date from "+ NCH.class.getSimpleName()+" where (is_deleted='false' or is_deleted=0) and id="+a.flag);
            sdate=DateHelper.toDate("yyyy-MM-dd'T'HH:mm:ss","dd MMM, yyyy",sdate);
            edate=DateHelper.toDate("yyyy-MM-dd'T'HH:mm:ss","dd MMM, yyyy",edate);
            String cmsg=DateHelper.DateCheck(sdate,edate,0, 0);
            if(cmsg!=null){
                return "Start Date: "+sdate+", End Date:"+edate+"\n "+DateHelper.DateCheck(sdate,edate,0, 0);
            }
            return null;
        }
        catch (Exception e){
            return null;
        }
    }


}
