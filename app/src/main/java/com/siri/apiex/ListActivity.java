package com.siri.apiex;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ListActivity extends AppCompatActivity {
    private ListView list_member;
    private ArrayAdapter<String> adapter;
    List<String> list = new ArrayList<>();


    //string에서 json을 꺼내오기
    // - string을 json 객체로 만들기
    // - json객체에서 key로 원하는 데이터 꺼내기
    // - 꺼낸 데이터의 자료형을 부여하기
    private void convertJsonToString(String result) {
        try {
            //string을 json 변경
            JSONObject jsonObject = new JSONObject(result);
            Log.d("api>>>", jsonObject + "");

            JSONObject _embedded = jsonObject.getJSONObject("_embedded");

            JSONArray items = _embedded.getJSONArray("persons");

            for (int i = 0; i < items.length(); i++) {
                JSONObject item = items.getJSONObject(i);

                String userId = item.getString("userId");
                String userNm = item.getString("userNm");
                int userAge = item.getInt("userAge");

                list.add(String.format("%s / %s / %d", userId, userNm, userAge));
            }


            //어댑터에 데이터가 변경되었다고 알람을 보내기
            adapter.notifyDataSetChanged();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //http 요청을 담당할 asynctask class
    private class HTTPAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            try {
                return httpGet(strings[0]);
            } catch (Exception e) {
                e.printStackTrace();
                return "데이터 수신에 실패하였습니다.";
            }

        }

        @Override
        protected void onPostExecute(String s) {
            Log.d("api>>>", s);
            convertJsonToString(s);
        }
    }


    //get 요청으로 데이터를 수신받는 메서드
    private String httpGet(String myUrl) throws IOException {
        URL url = new URL(myUrl);
        InputStream inputStream = null;

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.connect();

        inputStream = conn.getInputStream();

        //요청 상태에 따른 조건
        if (inputStream != null) {
            return convertInputStreamToString(inputStream);
        } else {
            return "연결 실패";
        }
    }


    //stream 객체을 수신하여 string으로 변환하는 메서드
    private String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = bufferedReader.readLine();
        String result = "";
        while (line != null) {       //수신받은 데이터가 마지막 라인이 아니라면
            result += line;
            line = bufferedReader.readLine();
        }


        inputStream.close();
        return result;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        //객체 얻기
        list_member = findViewById(R.id.list_member);

        //어댑터 객체 생성
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, list);
        list_member.setAdapter(adapter);
        list_member.setDivider(new ColorDrawable(Color.RED));
        list_member.setDividerHeight(1);

        //데이터 요청하기
        String url = getString(R.string.server_api);
        new HTTPAsyncTask().execute(url);
    }
}
