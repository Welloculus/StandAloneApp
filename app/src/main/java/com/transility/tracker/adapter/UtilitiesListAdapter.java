package com.transility.tracker.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.transility.tracker.R;
import com.transility.tracker.app.receiver.UtilitySelectListener;
import com.transility.tracker.beans.UtilitiesBean;

import java.util.List;

/**
 * Adapter to show the list of devices associated with the user.
 */
public class UtilitiesListAdapter extends RecyclerView.Adapter<UtilitiesListAdapter.ListItemViewHolder> {

    private List<UtilitiesBean> utilitiesList;
    UtilitySelectListener listener;
    /**
     * The type My view holder.
     */
    public class ListItemViewHolder extends RecyclerView.ViewHolder {
        private TextView utilityName;
        private TextView utilityDesc;

        /**
         * Instantiates a new My view holder.
         *
         * @param view the view
         */
        public ListItemViewHolder(View view) {
            super(view);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onUtilitySelected(utilitiesList.get(getPosition()));
                }
            });
            utilityName = (TextView) view.findViewById(R.id.txt_utility_name);
            utilityDesc = (TextView) view.findViewById(R.id.txt_utility_desc);
        }
    }

    /**
     * Instantiates a new Device list adapter.
     *
     * @param utilitiesList the device info list
     */
    public UtilitiesListAdapter(List<UtilitiesBean> utilitiesList, UtilitySelectListener listener) {
        this.utilitiesList = utilitiesList;
        this.listener = listener;
    }

    @Override
    public ListItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.utilities_list_row, parent, false);

        return new ListItemViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(ListItemViewHolder holder, final int position) {
        final UtilitiesBean utilitiesBean = utilitiesList.get(position);
        holder.utilityName.setText(utilitiesBean.getUtilitiesName());
        holder.utilityDesc.setText(utilitiesBean.getUtilitiesDesc());

    }

    @Override
    public int getItemCount() {
        return utilitiesList != null ? utilitiesList.size() : 0;
    }

}
