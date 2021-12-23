/*
 * Copyright (C) 2016 Nishant Srivastava
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package github.nisrulz.projectpackagehunter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.List;

class RVDetailsAdapter extends RecyclerView.Adapter<RVDetailsAdapter.ItemViewHolder> {

    public class ItemViewHolder extends RecyclerView.ViewHolder {

        final TextView txtvw_details;

        final TextView txtvw_header;

        public ItemViewHolder(View itemView) {
            super(itemView);
            txtvw_header = (TextView) itemView.findViewById(R.id.txtvw_header);
            txtvw_details = (TextView) itemView.findViewById(R.id.txtvw_details);
        }
    }

    private List<ElementInfo> dataList;

    public RVDetailsAdapter(List<ElementInfo> dataList) {
        this.dataList = dataList;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view =
                LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_details_item, parent, false);

        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        holder.txtvw_header.setText(dataList.get(position).getHeader());
        holder.txtvw_details.setText(dataList.get(position).toString());
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }
}
