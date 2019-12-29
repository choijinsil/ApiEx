package com.siri.apiex;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class JoinActivity extends AppCompatActivity {
    private Button btnJoin, btnCancel;
    private EditText etUserAge, etUserNm, etUserId;

    //내부 클래스로 비동기 동작할 클래스 생성
    private class HTTPAsyncTask extends AsyncTask<String, Void, String> {

        //백그라운드 작업을 실행하는 메서드
        @Override
        protected String doInBackground(String... strings) {
            try {
                return httpPost(strings[0]);
            } catch (Exception e) {
                e.printStackTrace();
                return "Error!";
            }
        }

        //doInBackground가 실행된 이후에 콜백되는 메서드
        @Override
        protected void onPostExecute(String s) {
            Toast.makeText(JoinActivity.this, "회원가입 성공!", Toast.LENGTH_SHORT).show();
            Log.d("s >> ",s);
        }
    }

    //post요청 메서드 만들기
    private String httpPost(String myUrl) throws IOException, JSONException {
        URL url = new URL(myUrl);
        // 1. HTTP연결 객체 만들기
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

        //2. 전송할jsonObject를 넣기
        JSONObject jsonObject = buildJsonObject();

        // 3. 전송할 스트림 만들기
        setPostRequestContent(conn, jsonObject);

        // 4. 실행
        conn.connect();

        // 5. response객체 리턴하기
        return conn.getResponseMessage() + "";
    }

    // 전송할 stream, 외부자원 이용할땐 IOException무조건 이용
    private void setPostRequestContent(HttpURLConnection conn, JSONObject jsonObject) throws IOException {
        OutputStream os = conn.getOutputStream();
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
        writer.write(String.valueOf(jsonObject));
        writer.flush(); // 메모리에서 내보내기
        writer.close();
        os.close();
    }

    // 안드로이드의 화면(activity_join.xml)의 뷰에서 받은 데이터를이용하여 json 형태로 만드는 메서드
    private JSONObject buildJsonObject() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.accumulate("userId", String.valueOf(etUserId.getText()));
        jsonObject.accumulate("userNm", String.valueOf(etUserNm.getText()));
        jsonObject.accumulate("userAge", String.valueOf(etUserAge.getText()));

        return jsonObject;
    }

    //서버작업 오청순서
    // 1) 서버 url 공통으로 빼기
    // 2) json 데이터 형태 만들기
    // 3) 전송할 stream만들기
    // 4) http method 요청하기
    // 5) 4번을 담당할 asynctask만들기
    // 6) 버튼에 이벤트 부여
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        btnJoin = findViewById(R.id.btn_join);
        btnCancel = findViewById(R.id.btn_cancel);
        etUserAge = findViewById(R.id.et_user_age);
        etUserNm = findViewById(R.id.et_user_nm);
        etUserId = findViewById(R.id.et_user_id);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = getString(R.string.server_api);
                new HTTPAsyncTask().execute(url);
            }
        });
    }
}
