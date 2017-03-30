package com.example.samsung.p0641_alertdialogiyemsmult;

import android.app.Dialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    final String LOG_TAG = "myLogs";

    final int DIALOG_ITEMS = 1,
            DIALOG_CURSOR = 2;
    boolean[] chkd;
    DB db;
    Cursor cursor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        int[] chkdInts = getResources().getIntArray(R.array.chkd);
        chkd = new boolean[chkdInts.length];
        int index = 0;

        for (int chkdInt :
            chkdInts){
            chkd[index] = (chkdInt == 1);
            index++;
        }

        db = new DB(this);
        db.open();
        cursor = db.getAllData();
        startManagingCursor(cursor);

    }

    public void onClickButton(View view) {
        switch (view.getId()) {
            case R.id.btnItems :
                showDialog(DIALOG_ITEMS);
                break;
            case R.id.btnCursor :
                showDialog(DIALOG_CURSOR);
                break;
            default :
                break;
        }
    }

    protected Dialog onCreateDialog(int id) {
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        switch (id) {
            //Диалог из Массива
            case DIALOG_ITEMS :
                adb.setTitle(R.string.items);
                adb.setMultiChoiceItems(R.array.data, chkd, myItemMultiClicListener);
                break;
            //Диалог из Курсора
            case DIALOG_CURSOR :
                adb.setTitle(R.string.cursor);
                adb.setMultiChoiceItems(cursor, DB.COLUMN_CHK, DB.COLUMN_TXT, myCursorMultiClicListener);
                break;
        }
        adb.setPositiveButton(R.string.ok, myBtnClicListener);
        return adb.create();
    }

    // For Android below 3.2
    protected void onPrepareDialog(int id, Dialog dialog) {
        //Доступ к адаптеру списка диалога
        AlertDialog aDialog = (AlertDialog) dialog;
        ListAdapter lAdapter = aDialog.getListView().getAdapter();

        switch (id) {
            case DIALOG_ITEMS :
                //Проверка возможности преобразования
                if (lAdapter instanceof BaseAdapter) {
                    //Преобразование и вызов мета-уведомления о новых данных
                    BaseAdapter bAdapter = (BaseAdapter) lAdapter;
                    bAdapter.notifyDataSetChanged();
                }
                break;
            case DIALOG_CURSOR :
                break;
            default :
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.close();
    }

    //Обработчик нажатия на пункт списка массива в диалоге
    DialogInterface.OnMultiChoiceClickListener myItemMultiClicListener = new DialogInterface.OnMultiChoiceClickListener() {

        @Override
        public void onClick(DialogInterface dialog, int which, boolean isChecked) {
            //Формирование строки вывода в лог и всплывающее сообщение позиции выбранного элемента
            String message = getString(R.string.position_number) + which
                    + (isChecked ? getString(R.string.is_checked) : getString(R.string.is_not_checked));
            Toast.makeText(getBaseContext(), message, Toast.LENGTH_SHORT).show();
            Log.d(LOG_TAG, message);
        }
    };

    //Обработчик нажатия на пункт списка сурсора в диалоге
    DialogInterface.OnMultiChoiceClickListener myCursorMultiClicListener = new DialogInterface.OnMultiChoiceClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which, boolean isChecked) {
            db.chengeRec(which, isChecked);
            cursor.requery();
            //Формирование строки вывода в лог и всплывающее сообщение позиции выбранного элемента
            String message = getString(R.string.position_number) + which
                    + (isChecked ? getString(R.string.is_checked) : getString(R.string.is_not_checked));
            Toast.makeText(getBaseContext(), message, Toast.LENGTH_SHORT).show();
            Log.d(LOG_TAG, message);
        }
    };

    //Обработчик нажатия на кнопку "ОК" в диалоге
    DialogInterface.OnClickListener myBtnClicListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            SparseBooleanArray sparseBooleanArray = ((AlertDialog) dialog).getListView().getCheckedItemPositions();

            for (int index = 0; index < sparseBooleanArray.size(); index++) {
                int key = sparseBooleanArray.keyAt(index);

                if (sparseBooleanArray.get(key)) {
                    //Формирование строки вывода в лог и всплывающее сообщение позиции выбранного элемента
                    String message = getString(R.string.position_number) + which
                            + (sparseBooleanArray.get(key) ? getString(R.string.is_checked) : getString(R.string.is_not_checked));
                    Toast.makeText(getBaseContext(), message, Toast.LENGTH_SHORT).show();
                    Log.d(LOG_TAG, message);
                }
            }
        }
    };
}