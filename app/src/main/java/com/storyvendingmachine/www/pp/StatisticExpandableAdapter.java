package com.storyvendingmachine.www.pp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

public class StatisticExpandableAdapter extends BaseExpandableListAdapter {
    private Context context;
    private List<StatisticExpandable_List_Group> expandableListTitle;
    private HashMap<StatisticExpandable_List_Group, List<StatisticExpandable_List_Item>> expandableListDetail;

    public StatisticExpandableAdapter(Context context, List<StatisticExpandable_List_Group> expandableListTitle,
                                             HashMap<StatisticExpandable_List_Group, List<StatisticExpandable_List_Item>> expandableListDetail) {
        this.context = context;
        this.expandableListTitle = expandableListTitle;
        this.expandableListDetail = expandableListDetail;

        Log.e("size ::", String.valueOf(expandableListTitle.size()));
    }

    // -------------여기서부터는 item adaper--------------------
    @Override
    public Object getChild(int listPosition, int expandedListPosition) {
        return this.expandableListDetail.get(this.expandableListTitle.get(listPosition))
                .get(expandedListPosition);
    }

    @Override
    public long getChildId(int listPosition, int expandedListPosition) {
        return expandedListPosition;
    }

    @Override
    public View getChildView(int listPosition, final int expandedListPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
//        final String expandedListText = (String) getChild(listPosition, expandedListPosition);

        StatisticExpandable_List_Item item = (StatisticExpandable_List_Item) getChild(listPosition, expandedListPosition);
        String pass = item.getPass();
//        String pass = expandableListDetail.get(expandedListPosition).get(listPosition).pass;

        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.container_statistic_exam_result, null);
        }
        TextView expandedListTextView = (TextView) convertView
                .findViewById(R.id.title_textView);
        expandedListTextView.setText(pass);
        return convertView;
    }

    @Override
    public int getChildrenCount(int listPosition) {
        return this.expandableListDetail.get(this.expandableListTitle.get(listPosition)).size();
    }


    // -------------여기서부터는 group adaper--------------------
    @Override
    public Object getGroup(int listPosition) {
        return this.expandableListTitle.get(listPosition);
    }

    @Override
    public int getGroupCount() {
        return this.expandableListTitle.size();
    }

    @Override
    public long getGroupId(int listPosition) {
        return listPosition;
    }

    @Override
    public View getGroupView(int listPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
//        String listTitle = (String) getGroup(listPosition);

        StatisticExpandable_List_Group group = (StatisticExpandable_List_Group) getGroup(listPosition);
        String exam_name =group.getExam_name();
        String exam_placed_year = group.getExam_placed_year();
        String exam_placed_round = group.getExam_placed_round();




        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.select_exam_elements_container, null);
        }
        TextView ExamEachNameTextView = (TextView) convertView.findViewById(R.id.ExamEachNameTextView);

        ExamEachNameTextView.setText(exam_name+" "+exam_placed_year+" "+ exam_placed_round);
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int listPosition, int expandedListPosition) {
        return true;
    }
}
