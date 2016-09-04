package com.yonguk.test.studentdbmanager;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnInsert, btnUpdate, btnDelete, btnQuery, btnDeleteTable = null;
    TextView tvResult = null;
    public StudentsDBManager mDbManager = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnInsert = (Button) findViewById(R.id.btn_insert);
        btnUpdate = (Button) findViewById(R.id.btn_update);
        btnDelete = (Button) findViewById(R.id.btn_delete);
        btnQuery = (Button) findViewById(R.id.btn_qurey);
        btnDeleteTable = (Button) findViewById(R.id.btn_delete_table);
        tvResult = (TextView) findViewById(R.id.tv_result);

        btnInsert.setOnClickListener(this);
        btnUpdate.setOnClickListener(this);
        btnDelete.setOnClickListener(this);
        btnQuery.setOnClickListener(this);
        btnDeleteTable.setOnClickListener(this);

        mDbManager = StudentsDBManager.getInstance(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.btn_insert:
                //ContentValues -> DB에 데이터를 추가/갱신할때 편리하게 하도록 도와주는 클래스
                ContentValues addRowValues = new ContentValues();
                addRowValues.put("number", "201111208");
                addRowValues.put("name", "김용욱");
                addRowValues.put("department", "미소");
                addRowValues.put("grade", "4");

                long insertRecordId = mDbManager.insert(addRowValues);
                tvResult.setText("레코드 추가 : " + insertRecordId);
                break;

            case R.id.btn_update:
                ContentValues updateRowValue = new ContentValues();
                updateRowValue.put("name", "이은정");
                int updateRecordCnt = mDbManager.update(updateRowValue,
                                                        "number=201111208",
                                                        null);
                tvResult.setText("레코드 갱신 : " + updateRecordCnt);
                break;

            case R.id.btn_delete:
                int deleteRecordCnt = mDbManager.delete(null,null);
                tvResult.setText("삭제된 레코드 수 : " + deleteRecordCnt);
                break;

            case R.id.btn_qurey:
                String[] colums = new String[]{"_id", "number", "name",
                                                "department", "grade"};
                Cursor c = mDbManager.query(colums, null, null, null, null, null);
                if(c != null){
                    tvResult.setText("");
                    while(c.moveToNext()){
                        int     id          = c.getInt(0);
                        String  number      = c.getString(1);
                        String  name        = c.getString(2);
                        String  department  = c.getString(3);
                        int     grade       = c.getInt(4);

                        tvResult.append(
                                        "id : " + id + "\n" +
                                        "nunber : " + number + "\n" +
                                        "name : " + name + "\n" +
                                        "department : " + department + "\n" +
                                        "grade : " + grade + "\n" +
                                        "--------------------------------\n"
                        );
                    }
                    tvResult.append("\n Total : " + c.getCount());
                    c.close();
                }
                break;

            case R.id.btn_delete_table:
                mDbManager.deleteTable();
                tvResult.setText("테이블 삭제 완료");
                break;
        }// end of switch()

    }//end of onClick()




}
