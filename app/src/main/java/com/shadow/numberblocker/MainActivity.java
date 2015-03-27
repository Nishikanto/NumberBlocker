package com.shadow.numberblocker;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;


public class MainActivity extends ActionBarActivity {

    private ArrayList<ListModel> items = null;
    DatabaseClass DC;
    ArrayList<String> numbers;
    ListView BlockList;
    ListAdapter numberList;
    private static final int CONTACT_PICKER_RESULT = 1001;
    String phone;
    String finalNum;
    boolean cursorFlag = false;
    Cursor c;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //getSupportActionBar().setDisplayShowHomeEnabled(true);
        //getSupportActionBar().setIcon(R.drawable.ic_launcher);
        //getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.GRAY));

        BlockList = (ListView) this.findViewById(R.id.list_item_iterator);
        loadData();
    }

    private void loadData() {
        try {
            DC = new DatabaseClass(MainActivity.this);
            DC.open();
            Cursor c = DC.getData();
            int iNum = c.getColumnIndex(DC.columnName()[0]);
            int iMsg = c.getColumnIndex(DC.columnName()[1]);
            int iCall = c.getColumnIndex(DC.columnName()[2]);
            numbers = new ArrayList<>();
            items = new ArrayList<ListModel>();
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                items.add(new ListModel(c.getString(iNum), c.getString(iMsg).equals("1"), c.getString(iCall).equals("1")));
                numbers.add(c.getString(iNum));
            }
            DC.close();
        } catch (Exception ex) {
            System.out.println(ex);
        }
        numberList = new ListAdapter();
        BlockList.setAdapter(numberList);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        SubMenu submenu1 = menu.addSubMenu("");
        submenu1.setIcon(R.drawable.ic_add);

        submenu1.add(1, 3, 2, "Add from call logs");
        submenu1.add(1, 1, 3, "Add from contacts");
        submenu1.add(1, 2, 4, "Add new");
        submenu1.getItem().setShowAsAction(
                MenuItem.SHOW_AS_ACTION_ALWAYS
                        | MenuItem.SHOW_AS_ACTION_WITH_TEXT);

        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == 1) {
            Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
            intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
            startActivityForResult(intent, CONTACT_PICKER_RESULT);
        }

        if (item.getItemId() == 3) {
            String[] callLogFields = {android.provider.CallLog.Calls._ID,
                    android.provider.CallLog.Calls.NUMBER,
                    android.provider.CallLog.Calls.CACHED_NAME /* im not using the name but you can*/};
            String viaOrder = android.provider.CallLog.Calls.DATE + " DESC";


            final Cursor callLog_cursor = this.getContentResolver().query(
                    android.provider.CallLog.Calls.CONTENT_URI, callLogFields,
                    null, null, viaOrder);

            AlertDialog.Builder myversionOfCallLog = new AlertDialog.Builder(this);
            android.content.DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int item) {
                    callLog_cursor.moveToPosition(item);
                    String numFromLog = callLog_cursor.getString(callLog_cursor
                            .getColumnIndex(android.provider.CallLog.Calls.NUMBER));
                    if (numFromLog.contains("+")) {
                        String[] parts = numFromLog.split("\\+");
                        finalNum = parts[1];
                    } else
                        finalNum = numFromLog;
                    if(finalNum.charAt(0)=='0') finalNum=finalNum.substring(1);
                    DC = new DatabaseClass(MainActivity.this);
                    DC.open();
                    DC.createEntry(finalNum, new Boolean(true), new Boolean(true));
                    DC.close();
                    loadData();
                    callLog_cursor.close();
                }
            };
            myversionOfCallLog.setCursor(callLog_cursor, listener, CallLog.Calls.NUMBER);
            myversionOfCallLog.setTitle("Choose from Call Log");
            myversionOfCallLog.create().show();
        }

        if (item.getItemId() == 2) {
            Intent foo = new Intent(this, AddActivity.class);
            this.startActivityForResult(foo, 1);
        }

        return super.onOptionsItemSelected(item);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case CONTACT_PICKER_RESULT:
                    try {
                        c = managedQuery(data.getData(), null, null, null, null);
                        cursorFlag = true;
                        if (c.moveToFirst()) {
                            phone = c.getString(c.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        }
                        if (phone.contains("+")) {
                            String[] parts = phone.split("\\+");
                            phone = parts[1];
                        }
                        if(phone.charAt(0)=='0') phone = phone.substring(1);
                        DC = new DatabaseClass(MainActivity.this);
                        DC.open();
                        DC.createEntry(phone, new Boolean(true), new Boolean(true));
                        DC.close();
                        loadData();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    break;

                case 1:
                    try {
                        String value = data.getStringExtra("value");
                        if (value != null && value.length() > 0 && value.matches("[0-9]+")) {
                            if (value.charAt(0) == '0') value = value.substring(1);
                            DC = new DatabaseClass(MainActivity.this);
                            DC.open();
                            DC.createEntry(value, new Boolean(true), new Boolean(true));
                            DC.close();
                            loadData();
                        } else
                            Toast.makeText(getBaseContext(), "Number cant containt extra charachter", Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    break;
            }

        }

    }

    private class ListAdapter extends BaseAdapter {
        private LayoutInflater inflater = null;

        public ListAdapter() {
            this.inflater = MainActivity.this.getLayoutInflater();
        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public Object getItem(int position) {
            return items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            convertView = inflater.inflate(R.layout.items, null);

            /*//You can also use this to have alternate list item color
            RelativeLayout itemQuestion = null;
            if (position % 2 == 0) {
                itemQuestion = (RelativeLayout) convertView.findViewById(R.id.item);
                itemQuestion.setBackgroundColor(Color.CYAN);
            } else {
                itemQuestion = (RelativeLayout) convertView.findViewById(R.id.item);
                itemQuestion.setBackgroundColor(Color.WHITE);
            }*/

            ListModel item = items.get(position);
            TextView question = (TextView) convertView.findViewById(R.id.number);
            question.setText(item.getNumber());

            final CheckBox chkMsg = (CheckBox) convertView.findViewById(R.id.chkMsg);
            final CheckBox chkCall = (CheckBox) convertView.findViewById(R.id.chkCall);
            final ImageButton deleteBtn = (ImageButton) convertView.findViewById(R.id.btnDelete);
            chkMsg.setChecked(item.getMsg());
            chkCall.setChecked(item.getCall());
            chkMsg.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    DC = new DatabaseClass(MainActivity.this);
                    DC.open();
                    DC.EditMSG(numbers.get(position), isChecked);
                    //Toast.makeText(MainActivity.this, numbers.get(position) + "Message Block " + isChecked, Toast.LENGTH_LONG).show();
                }

            });
            chkCall.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    DC = new DatabaseClass(MainActivity.this);
                    DC.open();
                    DC.EditCALL(numbers.get(position), isChecked);
                    //Toast.makeText(MainActivity.this, numbers.get(position) + "Call Block " + isChecked, Toast.LENGTH_LONG).show();
                }

            });

            deleteBtn.setOnClickListener(new CompoundButton.OnClickListener() {

                @Override
                public void onClick(View v) {
                    DC = new DatabaseClass(MainActivity.this);
                    DC.open();
                    String num = numbers.get(position);
                    DC.DeleteData(num);
                    DC.close();
                    loadData();
                }
            });


            return convertView;
        }
    }

    public void onResume() {
        super.onResume();
        loadData();
    }

    public void onDestroy(){
        super.onDestroy();
        if(cursorFlag == true)
            c.close();
    }

}
