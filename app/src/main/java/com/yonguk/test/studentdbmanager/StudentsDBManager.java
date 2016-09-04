package com.yonguk.test.studentdbmanager;

/**
 * Created by dosi on 2016-08-28.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 확장성, 독립성, 가독성 등을 고려하여 별도의 DB매니저 클래스 구현.
 * DB매니저 클래스는 싱글톤 패턴으로 설계하여 오직 하나의 객체만 생성되도록 한다.
 * DB매니저 내부에서 사용하게되는 SQLiteDatabase 객체를 오직 한개만 생성하기 위해서이다.
 * SQLiteDatabase 는 DB에 접근하는 클래스인데 여러 객체가 생성되어 스레드에서 DB에 동시 접근 및 갱신을
 * 할 경우 동기화에 문제가 발생한다. 하지만 오직 한개의 객체만 생성되게 하면 여러 스레드에서
 * 동시에 DB에 접근을 하더라도 내부적으로 동기를 맞춰 문제가 없다.
 *
 * SQLiteOpenHelper 클래스는 DB를 여는데 도움을 주는 클래스이다. DB를 열 때 발생할 수 있는 다양한
 * 예외사항을 개발자가 처리하는데 도움을 준다.
 * (1) DB를 열 때 DB와 테이블이 존재하지 않으면 생성을 돕는다.
 * (2) DB 여는 것을 돕는다.
 * (3) DB를 열 때 기존의 DB 테이블 구조가 변경되었다면 갱신을 돕는다.
 *
 * SQLiteOpenHelper 클래스의 3가지 재정의 함수 -> onCreate(), onOpen(), onUpgrade()
 * */
public class StudentsDBManager extends SQLiteOpenHelper{
    Context context = null;
    private static StudentsDBManager mDbManager = null;
    private SQLiteDatabase mDatabase = null;

    /** DB명, 테이블명, DB버전 정보 등을 정의 **/
    static final String DB_STUDENTS = "Students.db";
    static final String TABLE_STUDENTS = "Students";
    static final int DB_VERSION = 1;

    /**DB매니저 객체는 싱글톤으로 구현한다.**/
    public static StudentsDBManager getInstance(Context context){
        /*
        StudentDBManager 객체를 생성하는 함수.
        StudentDBManager 의 생성자를 private 으로 만들어 new 를 통한 객체 생성을 막고
        이 함수를 통해서만 객체를 생성할 수 있게 한다 .
         -> 이 앱을 사용하는 동안 한 개의 StudentDBManager 객체만 생성되도록 하기 위해
        StudentDBManager 객체가 존재하지 않으면 새로 만들고
        존재하면 이미 생성되어 있는 객체를 리턴한다.
        */
        if(mDbManager == null){
            mDbManager = new StudentsDBManager(context,
                                                DB_STUDENTS,
                                                null,
                                                DB_VERSION);
        }
        return mDbManager;
    }

    private StudentsDBManager(Context context,
                              String dbName,
                              SQLiteDatabase.CursorFactory factory,
                              int version){

        super(context,dbName,factory,version);
        this.context = context;


    }

    //DB에서 사용할 테이블 여기서 생성
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_STUDENTS +
                        "(" + "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "number     TEXT, " +
                        "name       TEXT, " +
                        "department TEXT, " +
                        "grade      INTEGER); "
        );
    }

    //DB가 정상적으로 열리면 호출되는 함수
    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
    }


    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

/*        //DB버전이 변경될 경우 사용 ( 테이블 구조 바꾸는 경우)
        if(oldVersion < newVersion){
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_STUDENTS);
            onCreate(sqLiteDatabase);
        }*/
    }

    /**레코드 삽입**/
    public long insert(ContentValues addRowValue){
        //이렇게 execSQL 함수를 이용하여 SQL문을 통해 레코드를 추가 할 수 있지만,
/*        mDatabase.execSQL("INSERT INTO " + TABLE_STUDENTS +
                        " VALUES(" +
                        "null, " +
                        "'201111208'," +
                        "'김용욱'," +
                        "'미소'," +
                        "4);"
        );*/

        //SQLiteDatabase에서 제공하는 insert 함수를 사용하는 것이 가독성이 좋고 편리하다.
        //return 값(long)의 경우 레코드가 성공적으로 추가되면 추가된 레코드의 id를 반환하고
        //실패할 경우 -1을 반환한다.
        return getWritableDatabase().insert(TABLE_STUDENTS, null, addRowValue);
    }

    /**쿼리(레코드 가져오기)**/
    public Cursor query(String[] colums,
                        String selection,
                        String[] selectionArgs,
                        String groupBy,
                        String having,
                        String orderBy){

        //colums -> 가져올 컬럼명

        //다음과 같이 rawQuery 함수를 이용해서 직접 SELECT문으로 가져올 수 도 있다.
        /*Cursor c = null;
        c = mDatabase.rawQuery("SELECT * FROM " + TABLE_STUDENTS, null);*/
        return getReadableDatabase().query(TABLE_STUDENTS,
                colums,
                selection,
                selectionArgs,
                groupBy,
                having,
                orderBy
        );
    }
    /**레코드 갱신**/
    public int update(ContentValues updateRowValue,
                      String whereClause,
                      String[] whereArgs){

        //updateRowValue -> 수정하고 싶은 컬럼과 수정할 값
        //whereClause -> 수정할 레코드를 검색하는 조건문

/*        mDatabase.execSQL("UPDATE " + TABLE_STUDENTS +
                            "SET name = '이은정'" +
                            "WHERE number = '201111208';");
        */
        //반환값(int) -> 수정된 레코드 수
        return getWritableDatabase().update(TABLE_STUDENTS,
                updateRowValue,
                whereClause,
                whereArgs);
    }

    /**레코드 삭제**/
    public int delete(String whereClause,
                      String[] whereArgs){
        //whereClause -> 삭제할 레코드를 검색하는 조건문
        //mDatabase.execSQL("DELETE FROM " + TABLE_STUDENTS);
        //반환값(int) 삭제한 레코드 수
        return getWritableDatabase().delete(TABLE_STUDENTS,
                whereClause,
                whereArgs);
    }

    /**테이블 새로 만들기**/
    public void deleteTable(){
        getWritableDatabase().execSQL("DROP TABLE IF EXISTS " + TABLE_STUDENTS);
        onCreate(getWritableDatabase());
    }

}
