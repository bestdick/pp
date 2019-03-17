package com.storyvendingmachine.www.pp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.storyvendingmachine.www.pp.Allurl.base_url;


//
///**
// * A simple {@link Fragment} subclass.
// * Activities that contain this fragment must implement the
// * {@link ExamSubClassFragment.OnFragmentInteractionListener} interface
// * to handle interaction events.
// * Use the {@link ExamSubClassFragment#newInstance} factory method to
// * create an instance of this fragment.
// */
public class LawExamSubClassFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

//    private OnFragmentInteractionListener mListener;
//
//    public ExamSubClassFragment() {
//        // Required empty public constructor
//    }
//
//    /**
//     * Use this factory method to create a new instance of
//     * this fragment using the provided parameters.
//     *
//     * @param param1 Parameter 1.
//     * @param param2 Parameter 2.
//     * @return A new instance of fragment ExamSubClassFragment.
//     */
//    // TODO: Rename and change types and number of parameters

    ListView listView;
    LawexamListviewAdapter examlistviewAdapter;
    List<LawExamListTypeA> examListTypeAList;

    public static LawExamSubClassFragment newInstance(String param1, String param2) {
        LawExamSubClassFragment fragment = new LawExamSubClassFragment();
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
        View rootview = inflater.inflate(R.layout.fragment_exam_sub_class_law, container, false);
        initializer(rootview);
        type_initializer(rootview);
        return rootview;
    }
    public void initializer(View rootview){
        listView = rootview.findViewById(R.id.listView);
        examListTypeAList = new ArrayList<LawExamListTypeA>();
        examlistviewAdapter= new LawexamListviewAdapter(getActivity(), examListTypeAList);
        listView.setAdapter(examlistviewAdapter);

        swiper(rootview);
    }
    public void type_initializer(View rootview){
        if(mParam1.equals("major_1003")){
            getExamList(mParam1);
        }else if(mParam1.equals("major_1002")){
            getExamList(mParam1);
        }else{
            //mParam1.equals("major_1001");
            getExamList(mParam1);
        }
    }

    public void swiper(View rootview){
        final SwipeRefreshLayout mSwipeRefreshLayout = (SwipeRefreshLayout) rootview.findViewById(R.id.swiper);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                examListTypeAList.clear();
                getExamList(mParam1);
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
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

    private void getExamList(final String major_type){
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        String url = base_url+"getExamList.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.e("exam list response", response);
                        try {
                            if(mParam1.equals("major_1003")) {
                                JSONObject jsonObject = new JSONObject(response);
                                JSONArray jsonArray = jsonObject.getJSONArray("response");
                                if(jsonArray.length()<=0){
                                    LawExamListTypeA item = new LawExamListTypeA("empty", null,
                                            null, null, null, null, 0);
                                    examListTypeAList.add(item);
                                    examlistviewAdapter.notifyDataSetChanged();
                                }else{
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        String exam_placed_year = jsonArray.getJSONObject(i).getString("year_round");
                                        LawExamListTypeA item = new LawExamListTypeA("year", exam_placed_year,
                                                null, null, null, null, 0);
                                        examListTypeAList.add(item);
                                        JSONArray inner_data_array = jsonArray.getJSONObject(i).getJSONArray("inner_data");
                                        for (int j = 0; j < inner_data_array.length(); j++) {
                                            String major_type = inner_data_array.getJSONObject(j).getString("major_type");
                                            String major_type_kor = inner_data_array.getJSONObject(j).getString("major_type_kor");
                                            String minor_type = inner_data_array.getJSONObject(j).getString("minor_type");
                                            String minor_type_kor = inner_data_array.getJSONObject(j).getString("minor_type_kor");
                                            JSONArray second_inner_data_array = inner_data_array.getJSONObject(j).getJSONArray("second_inner_data");
                                            int count_questions = second_inner_data_array.length();
                                            LawExamListTypeA inner_item = new LawExamListTypeA("all", exam_placed_year,
                                                    major_type, major_type_kor, minor_type, minor_type_kor, count_questions);
                                            examListTypeAList.add(inner_item);
                                        }
                                    }
                                    examlistviewAdapter.notifyDataSetChanged();
                                }
                            }else if(mParam1.equals("major_1002")){
                                JSONObject jsonObject = new JSONObject(response);
                                JSONArray jsonArray = jsonObject.getJSONArray("response");
                                if(jsonArray.length()<=0) {
                                    LawExamListTypeA item = new LawExamListTypeA("empty", null,
                                            null, null, null, null, 0);
                                    examListTypeAList.add(item);
                                    examlistviewAdapter.notifyDataSetChanged();
                                }else{

                                }
                            }else{
//                                mParam1.equals("major_1001")
                                JSONObject jsonObject = new JSONObject(response);
                                JSONArray jsonArray = jsonObject.getJSONArray("response");
                                if(jsonArray.length()<=0) {
                                    LawExamListTypeA item = new LawExamListTypeA("empty", null,
                                            null, null, null, null, 0);
                                    examListTypeAList.add(item);
                                    examlistviewAdapter.notifyDataSetChanged();
                                }else{

                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
//
//                        examlistviewAdapter.notifyDataSetChanged();
                    }},
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("are we here 6", error.toString());
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams(){
                Map<String, String> params = new HashMap<String, String>();
                params.put("token", "passpop");
                params.put("type", "list");
                params.put("major_type", major_type);
                return params;
            }
        };
        queue.add(stringRequest);
    }

}
