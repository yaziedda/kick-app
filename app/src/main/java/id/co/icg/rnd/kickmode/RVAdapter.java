package id.co.icg.rnd.kickmode;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Dizzay on 11/30/2017.
 */

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.ViewHolder> {

    List<LoginModel> list;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onClick(LoginModel model);
    }

    public RVAdapter(List<LoginModel> list, OnItemClickListener listener) {
        this.list = list;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final LoginModel loginModel = list.get(position);
        holder.tvAndroidVersionModel.setText("Device : "+loginModel.getAndroidOs()+", "+loginModel.getAndroidType());
        holder.tvVersion.setText("iReload - V"+loginModel.getAppVersion());
        holder.tvIp.setText("IP : "+loginModel.getLatestIP());
        holder.tvTime.setText("Latest Login : "+loginModel.getTime());
        holder.tvLocation.setText("Lokasi : "+loginModel.getSimpleLocation());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onClick(loginModel);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_version)
        TextView tvVersion;
        @BindView(R.id.tv_android_version_model)
        TextView tvAndroidVersionModel;
        @BindView(R.id.tv_ip)
        TextView tvIp;
        @BindView(R.id.tv_time)
        TextView tvTime;
        @BindView(R.id.tv_location)
        TextView tvLocation;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
