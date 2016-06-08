package com.mirzaaqibbeg.commitloktra;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private DownandParse downandParse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Fdown.isNetwork(this)) {
            downandParse = new DownandParse();
            String URL_COMMIT = "https://api.github.com/repos/rails/rails/commits";
            downandParse.execute(URL_COMMIT);
        } else {
            Toast.makeText(this, "Please connect with the network.", Toast.LENGTH_LONG)
                    .show();
        }

    }

    class DownandParse extends AsyncTask<String, Integer, ArrayList<FileCommit>> {

        private String stringDownloaded = null;

        private ArrayList<FileCommit> commitArrayList;
        private ProgressBar progressBar;
        private ListView listView;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            commitArrayList = new ArrayList<>();
            listView = (ListView) findViewById(R.id.list_commits);
            progressBar = (ProgressBar) findViewById(R.id.progress_bar);

            if (progressBar != null) {
                progressBar.setVisibility(View.VISIBLE);
            }
            if (listView != null) {
                listView.setVisibility(View.GONE);
            }
        }

        @Override
        protected ArrayList<FileCommit> doInBackground(String... params) {
            Fdown downloader = new Fdown();
            try {
                stringDownloaded = downloader.downloadContent(params[0]);

            } catch (IOException e) {
                e.printStackTrace();
            }
            if (stringDownloaded != null) {
                try {
                    JSONArray jsonArray = new JSONArray(stringDownloaded);
                    if (jsonArray != null) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject comJsonObj = jsonArray.getJSONObject(i); // this is the Json object at index i that run for all values
                            /**
                             *This  block will set for the SHA enc
                             */
                            FileCommit commit = new FileCommit();  // create here the commit object

                            if (isOk(FileCommit.N_SHA, comJsonObj)) {
                                commit.setCommitSHA(comJsonObj.getString(FileCommit.N_SHA));
                            } else {
                                commit.setCommitSHA("There is no SHA present here");
                            }


                            if (isOk(FileCommit.N_COMMIT, comJsonObj)) {
                                JSONObject commitObject = comJsonObj.getJSONObject(FileCommit.N_COMMIT);
                                if (commitObject != null) {
                                    if (isOk(FileCommit.N_AUTHOR, commitObject)) {
                                        JSONObject authorObject = commitObject.getJSONObject(FileCommit.N_AUTHOR);
                                        if (authorObject != null) {
                                            if (isOk(FileCommit.N_AUTHOR_NAME, authorObject)) {
                                                commit.setCommitAuthorName(authorObject.getString(FileCommit
                                                        .N_AUTHOR_NAME));
                                            } else {
                                                commit.setCommitAuthorName("No such name are available here");
                                            }
                                        }

                                        if (isOk(FileCommit.N_COMMIT_MESSAGE, commitObject)) {
                                            commit.setCommitMessage(commitObject.getString(FileCommit.N_COMMIT_MESSAGE));
                                        }
                                    }
                                }
                            }
                            commitArrayList.add(commit);


                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            return commitArrayList;
        }

        @Override
        protected void onPostExecute(ArrayList<FileCommit> commitArrayList) {
            super.onPostExecute(commitArrayList);

            if (progressBar != null) {
                progressBar.setVisibility(View.GONE);
            }



            if (listView != null) {
                listView.setAdapter(new CommitListAdapter(commitArrayList));
                listView.setVisibility(View.VISIBLE);
            }

        }

        private boolean isOk(String objName, JSONObject jsonObject) {
            if (jsonObject.has(objName) && !jsonObject.isNull(objName)) {
                return true;
            } else return false;
        }
    }

    class CommitListAdapter extends BaseAdapter {

        private final ArrayList<FileCommit> arrayListCommit;

        public CommitListAdapter(ArrayList<FileCommit> arrayList) {
            this.arrayListCommit = arrayList;
        }

        @Override
        public int getCount() {
            return this.arrayListCommit.size();
        }

        @Override
        public Object getItem(int position) {
            return arrayListCommit.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                LayoutInflater vi;
                vi = LayoutInflater.from(getApplicationContext());
                convertView = vi.inflate(R.layout.lstitm_com, null);
            }

            FileCommit commit = arrayListCommit.get(position);

            TextView textAuthor = (TextView) convertView.findViewById(R.id.author);
            TextView textSHA = (TextView) convertView.findViewById(R.id.sha);
            TextView textMessage = (TextView) convertView.findViewById(R.id.message);

            textAuthor.setText(commit.getCommitAuthorName());
            textSHA.setText(commit.getCommitSHA());
            textMessage.setText(commit.getCommitMessage());

            return convertView;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (downandParse != null) {
            downandParse.cancel(true);
        }
    }
}