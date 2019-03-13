package com.storyvendingmachine.www.pp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.storyvendingmachine.www.pp.Allurl.base_url;


///**
// * A simple {@link Fragment} subclass.
// * Activities that contain this fragment must implement the
// * {@link HomeFragment.OnFragmentInteractionListener} interface
// * to handle interaction events.
// * Use the {@link HomeFragment#newInstance} factory method to
// * create an instance of this fragment.
// */
public class LawHomeFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

//    private OnFragmentInteractionListener mListener;
//
//    public HomeFragment() {
//        // Required empty public constructor
//    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LawHomeFragment newInstance(String param1, String param2) {
        LawHomeFragment fragment = new LawHomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootview =inflater.inflate(R.layout.fragment_home_law, container, false);
        getAnnouncement_Error_Suggestion(rootview);
        return rootview;
    }

    public void getAnnouncement_Error_Suggestion(final View rootview){
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        String url = base_url + "getAnnouncement_Error_Suggestion.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("announce response", response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray news_announcement_jsonArray = jsonObject.getJSONArray("response1");

                            LinearLayout news_linLayout = (LinearLayout) rootview.findViewById(R.id.announcement_LinearLayout);
                            for(int i = 0 ; i < news_announcement_jsonArray.length(); i++){
                                String news_primary_key = news_announcement_jsonArray.getJSONObject(i).getString("primary_key");
                                String news_exam_type_code = news_announcement_jsonArray.getJSONObject(i).getString("exam_type_code");
                                String news_type = news_announcement_jsonArray.getJSONObject(i).getString("type");
                                String news_title = news_announcement_jsonArray.getJSONObject(i).getString("title");
                                String news_content = news_announcement_jsonArray.getJSONObject(i).getString("content");
                                String news_upload_date = news_announcement_jsonArray.getJSONObject(i).getString("upload_date");
                                String news_upload_time = news_announcement_jsonArray.getJSONObject(i).getString("upload_time");
                                String news_isNew = news_announcement_jsonArray.getJSONObject(i).getString("isNew");

                                View container_news_inner_element = getLayoutInflater().inflate(R.layout.container_news_inner_element, null);
                                ImageView news_isNew_imageView = (ImageView) container_news_inner_element.findViewById(R.id.new_imageView);
                                TextView news_type_textView = (TextView) container_news_inner_element.findViewById(R.id.new_title_type_textView);
                                TextView news_title_textView = (TextView) container_news_inner_element.findViewById(R.id.new_title_textView);
                                TextView news_upload_date_textView = (TextView) container_news_inner_element.findViewById(R.id.upload_date_textView);

                                indentify_isNew(news_isNew, news_isNew_imageView);

                                news_type_textView.setText(type_selector(news_type));
                                news_title_textView.setText(news_title);
                                news_upload_date_textView.setText(news_upload_date);

                                news_linLayout.addView(container_news_inner_element);
                            }



                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams(){
                Map<String, String> params = new HashMap<String, String>();
                params.put("token", "passpop");
                return params;
            }
        };
        queue.add(stringRequest);
    }

//    // TODO: Rename method, update argument and hook method into UI event
//    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
//        }
//    }
//
//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
//    }
//
//    @Override
//    public void onDetach() {
//        super.onDetach();
//        mListener = null;
//    }
//
//    /**
//     * This interface must be implemented by activities that contain this
//     * fragment to allow an interaction in this fragment to be communicated
//     * to the activity and potentially other fragments contained in that
//     * activity.
//     * <p>
//     * See the Android Training lesson <a href=
//     * "http://developer.android.com/training/basics/fragments/communicating.html"
//     * >Communicating with Other Fragments</a> for more information.
//     */
//    public interface OnFragmentInteractionListener {
//        // TODO: Update argument type and name
//        void onFragmentInteraction(Uri uri);
//    }


    public void indentify_isNew(String input_str, ImageView input_imageView){
        if(input_str.equals("old")){
            input_imageView.setVisibility(View.GONE);
        }else{
            input_imageView.setVisibility(View.INVISIBLE);
        }
    }
    public String type_selector(String input_str){
        if(input_str.equals("news")){
            return "[뉴스]";
        }else if(input_str.equals("update")){
            return "[업데이트]";
        }else{
            return "[공지]";
        }
    }
}
