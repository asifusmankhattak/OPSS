package pbs.sme.survey.helper;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import pbs.sme.survey.R;
/*import pbs.sme.survey.activity.BlockActivity;
import pbs.sme.survey.activity.NCHListActivity;*/
import pbs.sme.survey.activity.BlockActivity;
import pbs.sme.survey.activity.OtherListActivity;
import pbs.sme.survey.model.Block;
import pbs.sme.survey.model.Constants;

import java.util.List;

public class BlockAdapter extends ArrayAdapter<Block> {
    Activity context;

    public BlockAdapter(Activity context, List<Block> list) {
        super(context, R.layout.list_items, list);
        this.context = context;
    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        if (view == null)
            view = inflater.inflate(R.layout.list_items, parent, false);

        LinearLayout layout=view.findViewById(R.id.list_container);

        TextView l_code=view.findViewById(R.id.blk_head);

        TextView code=view.findViewById(R.id.txt_code);
        TextView start=view.findViewById(R.id.txt_start);
        TextView end=view.findViewById(R.id.txt_end);
        TextView status=view.findViewById(R.id.txt_status);
        TextView adderss=view.findViewById(R.id.txt_address);

        Block a= getItem(position);

        if(a.getBlockCode().length()>9){
            l_code.setText(a.getType()+": ");
            code.setText(String.valueOf(a.getCount()));
            adderss.setVisibility(View.GONE);
            layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Intent intent = new Intent(context, OtherListActivity.class);
                    intent.putExtra(Constants.EXTRA.IDX_BLOCK, a);
                    context.startActivity(intent);
                }
            });
        }
        else{
            l_code.setText("Block Code: ");
            code.setText(a.getBlockCode());
            adderss.setVisibility(View.VISIBLE);
            adderss.setText(a.getAddressWithLabel());
            layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Intent intent = new Intent(context, BlockActivity.class);
                    intent.putExtra(Constants.EXTRA.IDX_BLOCK, a);
                    context.startActivity(intent);
                }
            });
        }

        status.setText(a.getStatus());
        start.setText(String.valueOf(a.getStartDate()));
        end.setText(String.valueOf(a.getEndDate()));

        return view;
    }


}
