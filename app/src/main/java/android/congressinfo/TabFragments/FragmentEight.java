package android.congressinfo.TabFragments;

import android.congressinfo.DetailsActivities.CommitteesDetails;
import android.congressinfo.CommitteesJSON.CommitteesMain;
import android.congressinfo.MainActivity;
import android.congressinfo.MyTools;
import android.congressinfo.R;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FragmentEight extends Fragment {
    private final String COMMITTEES_PATH = "http://sample-env-1.5ah5ppemzf.us-east-1.elasticbeanstalk.com/?dataBase=committees&flag_6=true";
    private ListView listView8 = null;
    private CommitteesMain committeesMain = null;

    public FragmentEight() {
        // empty
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_eight, container, false);
        listView8 = (ListView) view.findViewById(R.id.listView8);
        new FragmentEight.JSONTask_jointCommittees().execute(COMMITTEES_PATH);
        return view;
    }

    public class JSONTask_jointCommittees extends AsyncTask<String, Integer, String> {
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected String doInBackground(String... params) {
            String result = MyTools.stringFromURL(params[0]);
            return result;
        }

        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Gson gson = new Gson();
            committeesMain = gson.fromJson(result, CommitteesMain.class);
            final List<Map<String, Object>> jointCommitteesInfo = new ArrayList<Map<String, Object>>();
            for (int i = 0; i < committeesMain.getCount(); i++) {
                HashMap<String, Object> map = new HashMap<String, Object>();
                map.put("line1", committeesMain.getResults()[i].getCommittee_id());
                map.put("line2", committeesMain.getResults()[i].getName());
                map.put("line3", committeesMain.getResults()[i].getChamber());
                if (map.get("line3").equals("joint")) {
                    map.put("line3", "Joint");
                }
                map.put("filter", committeesMain.getResults()[i].getChamber());
                map.put("sorter", committeesMain.getResults()[i].getName());
                map.put("id", committeesMain.getResults()[i].getCommittee_id());
                if (map.get("filter").equals("joint")) {
                    jointCommitteesInfo.add(map);
                }
            }
            MyTools.sortListMapByString(jointCommitteesInfo);
            SimpleAdapter simpleAdapter = new SimpleAdapter(FragmentEight.this.getActivity(), jointCommitteesInfo, R.layout.committees_item,
                    new String[] {"line1", "line2", "line3"},
                    new int[] {R.id.textView_committees_1, R.id.textView_committees_2, R.id.textView_committees_3});
            listView8.setAdapter(simpleAdapter);

            listView8.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    MainActivity.currentPage_c = 2;
                    Intent setDetails = new Intent(view.getContext(), CommitteesDetails.class);
                    String id = (String) jointCommitteesInfo.get(i).get("id");
                    setDetails.putExtra("id", id);
                    int j;
                    for (j = 0; j < committeesMain.getPage().getCount(); j++) {
                        if (committeesMain.getResults()[j].getCommittee_id().equals(id)) {
                            break;
                        }
                    }

                    String committee_id = committeesMain.getResults()[j].getCommittee_id();
                    setDetails.putExtra("committee_id", committee_id);

                    String title = committeesMain.getResults()[j].getName();
                    setDetails.putExtra("title", title);

                    String chamber = committeesMain.getResults()[j].getChamber();
                    setDetails.putExtra("chamber", chamber);

                    String parent_committee = "N.A";
                    if (committeesMain.getResults()[j].getParent_committee_id() != null) {
                        parent_committee = committeesMain.getResults()[j].getParent_committee_id();
                    }
                    setDetails.putExtra("parent_committee", parent_committee);

                    String contact = "N.A";
                    if (committeesMain.getResults()[j].getPhone() != null) {
                        contact = committeesMain.getResults()[j].getPhone();
                    }
                    setDetails.putExtra("contact", contact);

                    String office = "N.A";
                    if (committeesMain.getResults()[j].getOffice() != null) {
                        office = committeesMain.getResults()[j].getOffice();
                    }
                    setDetails.putExtra("office", office);

                    startActivity(setDetails);
                }
            });
        }
    }
}
