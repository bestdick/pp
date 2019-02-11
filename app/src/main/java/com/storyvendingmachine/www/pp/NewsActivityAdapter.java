package com.storyvendingmachine.www.pp;

import android.content.Context;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

import static android.text.Html.fromHtml;

public class NewsActivityAdapter extends BaseExpandableListAdapter {
    private Context context;
    private List<NewsActivityGroupList> expandableListTitle;
    private HashMap<NewsActivityGroupList, NewsActivityItemList> expandableListDetail;

    public NewsActivityAdapter(Context context, List<NewsActivityGroupList> expandableListTitle,
                                      HashMap<NewsActivityGroupList,
                                              NewsActivityItemList> expandableListDetail) {
        this.context = context;
        this.expandableListTitle = expandableListTitle;
        this.expandableListDetail = expandableListDetail;

    }

    final String STRING_NEWS = "news";
    final String STRING_ANNOUNCEMENT = "announcement";
    final String STRING_UPDATE = "update";

    final String STRING_SUGGESTION = "suggestion";
    final String STRING_FEDDBACK = "feedback";
    final String STRING_ERROR = "error";
    // -------------여기서부터는 item adaper--------------------
    @Override
    public Object getChild(int listPosition, int expandedListPosition) {
//        return this.expandableListDetail.get(expandedListPosition);
        return this.expandableListDetail.get(this.expandableListTitle.get(listPosition));
//                .get(expandedListPosition);
    }

    @Override
    public long getChildId(int listPosition, int expandedListPosition) {
        return expandedListPosition;
    }

    @Override
    public View getChildView(int listPosition, final int expandedListPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
//        final String expandedListText = (String) getChild(listPosition, expandedListPosition);

        NewsActivityItemList item = (NewsActivityItemList) getChild(listPosition, expandedListPosition);
        String content = item.getContent();
        String upload_date = item.getUpload_date();
        String upload_time = item.getUpload_time();
        String title = item.getTitle();
        Spanned html_content = Html.fromHtml(content);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.container_for_news_item_element, null);
        }
        TextView title_textView = (TextView) convertView.findViewById(R.id.textView27);
        TextView upload_date_time_textView = (TextView) convertView.findViewById(R.id.upload_date_time_textView);
        TextView content_textView = (TextView) convertView.findViewById(R.id.content_textView);

        title_textView.setText(title);
        upload_date_time_textView.setText(upload_date+" "+upload_time);
        content_textView.setText(html_content);
        return convertView;
    }

    @Override
    public int getChildrenCount(int listPosition) {
        return 1;
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

        NewsActivityGroupList group = (NewsActivityGroupList) getGroup(listPosition);
        String news_title = group.getNews_title();
        String type = group.getType();
        String isNew = group.getIsNew();
        String upload_date = group.getUpload_date();
        String upload_time = group.getUpload_time();




        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.container_news_inner_element, null);
        }
        ImageView isNew_imageView = (ImageView) convertView.findViewById(R.id.new_imageView);
        TextView new_title_type_textView = (TextView) convertView.findViewById(R.id.new_title_type_textView);
        TextView new_title_textView = (TextView) convertView.findViewById(R.id.new_title_textView);
        TextView upload_date_textView = (TextView) convertView.findViewById(R.id.upload_date_textView);

        if(type.equals(STRING_NEWS)){
            new_title_type_textView.setText("[뉴스]");
        }else if (type.equals(STRING_ANNOUNCEMENT)){
            new_title_type_textView.setText("[공지]");
        }else if (type.equals(STRING_UPDATE)){
            new_title_type_textView.setText("[업데이트]");
        }else if(type.equals(STRING_FEDDBACK)){
            new_title_type_textView.setText("[피드백]");
        }else if(type.equals(STRING_SUGGESTION)){
            new_title_type_textView.setText("[건의및개선]");
        }else{
//            type.equals(STRING_ERROR)
            new_title_type_textView.setText("[오류]");
        }
        if(isNew.equals("old")) {
            isNew_imageView.setVisibility(View.GONE);
        }
        new_title_textView.setText(news_title);
        upload_date_textView.setText(upload_date);

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
