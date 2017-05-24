package net.suntrans.suntranssmarthome.homepage.myhome.edit;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import net.suntrans.suntranssmarthome.R;
import net.suntrans.suntranssmarthome.model.PatternItem;

import java.util.List;

import android.content.Context;

/**
 * Created by Looney on 2017/5/23.
 */

public class AddSenceDevGrpAdapter extends BaseExpandableListAdapter {
    private List<PatternItem> datas;
    private Context mContext;

    public AddSenceDevGrpAdapter(List<PatternItem> datas, Context mContext) {
        this.datas = datas;
        this.mContext = mContext;
        System.out.println("ssssssss:" + datas.get(0).getDevices().size());

    }

    @Override
    public int getGroupCount() {
        return datas.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return datas.get(groupPosition).getDevices().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return datas.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return datas.get(groupPosition).getDevices().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        View view = null;
        GroupHolder groupHolder = null;
        if (convertView != null) {
            view = convertView;
            groupHolder = (GroupHolder) view.getTag();
        } else {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_group, parent, false);
            groupHolder = new GroupHolder(view);
            view.setTag(groupHolder);
        }
        groupHolder.setData(groupPosition);
        return view;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        View view = null;
        ChildHolder holder = null;
        if (convertView != null) {
            view = convertView;
            holder = (ChildHolder) view.getTag();
        } else {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_children, parent, false);
            holder = new ChildHolder(view);
            view.setTag(holder);
        }
        holder.setData(groupPosition, childPosition);
        return view;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }


    public class GroupHolder {
        TextView mName;
        ImageView mImage;
        CheckBox state;
        String[] colors = new String[]{"#f99e5b", "#d3e4ad", "#94c9d6"};

        public GroupHolder(View view) {
            mName = (TextView) view.findViewById(R.id.name);
            mImage = (ImageView) view.findViewById(R.id.imageView);
            state = (CheckBox) view.findViewById(R.id.checkbox);

        }

        public void setData(final int groupPosition) {
            mName.setText(datas.get(groupPosition).getName());
            state.setChecked(datas.get(groupPosition).isCheck());
            mImage.setBackgroundColor(Color.parseColor(colors[groupPosition % 3]));
            state.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                  if (state.isChecked()){
                      for (int i=0;i<datas.get(groupPosition).getDevices().size();i++){
                          datas.get(groupPosition).getDevices().get(i).setChecked(true);
                      }
                      datas.get(groupPosition).setCheck(true);
                  }else {
                      for (int i=0;i<datas.get(groupPosition).getDevices().size();i++){
                          datas.get(groupPosition).getDevices().get(i).setChecked(false);
                      }
                      datas.get(groupPosition).setCheck(false);
                  }
                    notifyDataSetChanged();
                }
            });
        }
    }

    public class ChildHolder {
        ImageView mImage;
        TextView mText;
        CheckBox state;

        public ChildHolder(View view) {
            mText = (TextView) view.findViewById(R.id.name);
            state = (CheckBox) view.findViewById(R.id.checkbox);

        }

        public void setData(final int groupPosition, final int childPosition) {
            mText.setText(datas.get(groupPosition).getDevices().get(childPosition).getName());
            state.setChecked(datas.get(groupPosition).getDevices().get(childPosition).isChecked());
            state.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    datas.get(groupPosition).getDevices().get(childPosition).setChecked(state.isChecked());
                    int checkedCount =0;
                    for (int i=0;i<datas.get(groupPosition).getDevices().size();i++){
                        if (datas.get(groupPosition).getDevices().get(i).isChecked())
                            checkedCount++;
                    }
                    if (checkedCount==datas.get(groupPosition).getDevices().size()){
                        datas.get(groupPosition).setCheck(true);
                    }else {
                        datas.get(groupPosition).setCheck(false);

                    }
                    notifyDataSetChanged();

                }
            });

        }
    }


    public void setOnItemClickListener(AddSenceDevGrpAdapter.onItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    private onItemClickListener onItemClickListener;

    public interface onItemClickListener {
        void onParentChekBoxCheckedListener(CheckBox checkBox);

        void onChildrenChekBoxCheckedListener(CheckBox checkBox);
    }
}
