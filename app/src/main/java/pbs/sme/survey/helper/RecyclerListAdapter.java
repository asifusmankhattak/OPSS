package pbs.sme.survey.helper;

import static pbs.sme.survey.activity.MyActivity.getTimeNow;
import static pbs.sme.survey.helper.SyncService.BROADCAST_HOUSEHOLD_CHANGE;

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
import java.util.Objects;

import pbs.sme.survey.DB.Database;
import pbs.sme.survey.R;
//import pbs.sme.survey.activity.HouseActivity;
import pbs.sme.survey.activity.GeoActivity;
//import pbs.sme.survey.activity.NHouseActivity;
import pbs.sme.survey.activity.ListActivity;
import pbs.sme.survey.api.ApiClient;
import pbs.sme.survey.api.ApiInterface;
import pbs.sme.survey.model.Block;
import pbs.sme.survey.model.Constants;
import pbs.sme.survey.model.Household;
import pbs.sme.survey.model.House;
import pbs.sme.survey.model.Section12;
import pbs.sme.survey.online.Returning;
import pbs.sme.survey.online.Sync;
import pk.gov.pbs.utils.CustomActivity;
import pk.gov.pbs.utils.StaticUtils;
import pk.gov.pbs.utils.UXEventListeners;
import pk.gov.pbs.utils.UXToolkit;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecyclerListAdapter extends RecyclerView.Adapter<RecyclerListAdapter.ViewHolder> {
    private final Activity context;
    Database dbHandler;
    private List<Section12> data;
    String env;

    public RecyclerListAdapter(Activity context, List<Section12> list, Database dbHandler, String env){
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
        final Section12 a=data.get(position);
        holder.setIsRecyclable(false);
        holder.sno.setText(String.format("%04d",a.sno));
        holder.title.setText(a.title);
        holder.emp.setText(String.valueOf(a.emp_count));
        if(a.sync_time!=null){
            ImageViewCompat.setImageTintList(holder.imgtick, ColorStateList.valueOf(context.getColor(R.color.success)));
        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Block b = (Block) context.getIntent().getSerializableExtra(Constants.EXTRA.IDX_BLOCK);
                Intent i = new Intent(context, GeoActivity.class);
                i.putExtra(Constants.EXTRA.IDX_BLOCK, b);
                i.putExtra(Constants.EXTRA.IDX_HOUSE, a);
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
                                //syncHouse(a,holder.btnView, holder.imgtick);
                                break;
                            case "Edit":
                                    Block b = (Block) context.getIntent().getSerializableExtra(Constants.EXTRA.IDX_BLOCK);
                                    Intent i = new Intent(context, GeoActivity.class);
                                    i.putExtra(Constants.EXTRA.IDX_BLOCK, b);
                                    i.putExtra(Constants.EXTRA.IDX_HOUSE, a);
                                    context.startActivity(i);
                                break;
                            case "Delete":
                                UXToolkit tk = ((CustomActivity) context).getUXToolkit();
                                Section12 fullObj = dbHandler.querySingle(Section12.class, "env=? and blk_desc=? and house_uid=?", env, a.blk_desc, a.uid.toString());
                                if (fullObj!=null) {
                                    tk.showConfirmDialogue("حذف کرنے کی تصدیق کریں", "کیا آپ مکان نمبر "+fullObj.sno+" کو حذف کرنا چاہتے ہیں؟", new UXEventListeners.ConfirmDialogueEventsListener() {
                                        @Override
                                        public void onCancel() {
                                        }
                                        @Override
                                        public void onOK() {
                                            if (dbHandler.softDeleteForm(fullObj) > 0) {
                                                Intent i = new Intent();
                                                i.setAction(BROADCAST_HOUSEHOLD_CHANGE);
                                                context.sendBroadcast(i);
                                                for (Section12 h : data) {
                                                    if (h.blk_desc.equalsIgnoreCase(a.blk_desc) && Objects.equals(h.uid, a.uid))
                                                        StaticUtils.getHandler().post(() -> {
                                                            data.remove(h);
                                                        });

                                                }
                                                ((ListActivity) context).updateStats();
                                                StaticUtils.getHandler().post(() -> {
                                                    notifyDataSetChanged();
                                                });
                                            }
                                        }
                                    });
                                }
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
        //LinearLayout colorLay;

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
        if(!status){
            Toast.makeText(context,"Uploading Please Wait",Toast.LENGTH_SHORT).show();
            List<Household> unsync=dbHandler.queryRawSql(Household.class,"SELECT * FROM "+ Household.class.getSimpleName()+" WHERE  env='"+env+"' and  HOUSE_UID='"+h.house_uid+"' and sync_time is null;");
            Long total=dbHandler.getCount(Household.class,"HOUSE_UID=? and env=?",h.house_uid,env);
            if(total==0){
                Household a=new Household();
                a.created_time=h.created_time;
                a.modified_time=h.modified_time;
                a.setHouse(h);
                unsync.add(a);
            }
            Sync s=new Sync();
            s.setList_hh(unsync);
            view.setEnabled(false);
            Call<Returning> call= ApiClient.getApiClient().create(ApiInterface.class).upload(s);
            call.enqueue(new Callback<Returning>() {
                @Override
                public void onResponse(Call<Returning> call, Response<Returning> response) {
                    view.setEnabled(true);
                    Returning u=response.body();
                    if(u!=null){
                        if(u.getCode()==1){
                            h.sync_time=getTimeNow();
                            Toast.makeText(context,u.getMsg(),Toast.LENGTH_SHORT).show();
                            dbHandler.execSql("UPDATE "+ House.class.getSimpleName()+" SET sync_time='"+ getTimeNow()+"' where  env='"+env+"' and  house_uid='"+h.house_uid+"';");
                            dbHandler.execSql("UPDATE "+ Household.class.getSimpleName()+" SET sync_time='"+ getTimeNow()+"' where  env='"+env+"' and  house_uid='"+h.house_uid+"' and sync_time is null;");
                            ImageViewCompat.setImageTintList(tick, ColorStateList.valueOf(context.getColor(R.color.success)));
                            StatsUtil.updateSyncTimeToNow();
                        }
                        else{
                            Toast.makeText(context,"Failure, Server Code: "+u.getMsg(),Toast.LENGTH_SHORT).show();
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
            Toast.makeText(context,"Already Synced",Toast.LENGTH_SHORT).show();
        }
    }
}
