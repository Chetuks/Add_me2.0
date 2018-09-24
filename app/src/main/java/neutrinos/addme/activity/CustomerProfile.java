package neutrinos.addme.activity;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import neutrinos.addme.adapter.ItemAdapter;
import neutrinos.addme.beenclass.ItemBeen;
import neutrinos.addme.utilities.Logger;
import neutrinos.addme.R;

public class CustomerProfile extends AppCompatActivity {
    RecyclerView listView;
    RecyclerView listView1;
    List<ItemBeen> itemBeens = new ArrayList<>();
    List<ItemBeen> itemBeens1 = new ArrayList<>();
    private int getPoints;
    private String customer_Name;
    private int mobile_Number;
    private String customerImage;


    private String transaction_ID;
    private String transaction_Status;
    private String product_Name;
    private String purchased_Date;
    private int total_Price;
    private int customerID;
    private int gained_Points;
    private String card_Number;
    private String emaid_Customer;
    private String created_Date;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_profile);
        listView = (RecyclerView) findViewById(R.id.historyListView);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Json Method
        parceMyResponse();
        //filling the list

        //Declaring the ID of layout
        TextView customerName = (TextView) findViewById(R.id.custNameid);
        TextView mobileNumber = (TextView) findViewById(R.id.custMobileid);
        TextView cardPoints = (TextView) findViewById(R.id.custCardPointsid);

        TextView gainedPoints= (TextView) findViewById(R.id.SuccessTopMiddle_id);



        //Displaying the values in Layout
        customerName.setText(String.valueOf(customer_Name));
        mobileNumber.setText(String.valueOf(mobile_Number));
        cardPoints.setText(String.valueOf(getPoints));


    }


    private void parceMyResponse() {
            try {
                String jsonStructure="{\n" +
                        "  \"customer_id\": 123,\n" +
                        "  \"customer_name\": \"Android\",\n" +
                        "  \"mobile_number\": 1234566,\n" +
                        "  \"card_number\": \"1234rr\",\n" +
                        "  \"email\": \"1abcd@gmail\",\n" +
                        "  \"created_date\": \"12/12/12 00:12:12\",\n" +
                        "  \"customer_image\": \"abcd.jpg\",\n" +
                        "  \"transaction_history\": [\n" +
                        "    {\n" +
                        "      \"transaction_id\": \"99c6d2a3-54d5-4b5f-8259-fc0da63ed4c4\",\n" +
                        "      \"transaction_status\": \"Success\",\n" +
                        "      \"product_name\": \"orange\",\n" +
                        "      \"purchased_date\": \"10-Mar-2018 2:36:52 pm\",\n" +
                        "      \"total_price\": 1000,\n" +
                        "      \"points\": 250\n" +
                        "    },\n" +
                        "    {\n" +
                        "      \"transaction_id\": \"99c6d2a3-54d5-4b5f-8259-fc0da63ed4c4\",\n" +
                        "      \"transaction_status\": \"Success\",\n" +
                        "      \"product_name\": \"orange\",\n" +
                        "      \"purchased_date\": \"10-Mar-2018 2:36:52 pm\",\n" +
                        "      \"total_price\": 1000,\n" +
                        "      \"points\": 250\n" +
                        "    },\n" +
                        "    {\n" +
                        "      \"transaction_id\": \"99c6d2a3-54d5-4b5f-8259-fc0da63ed4c4\",\n" +
                        "      \"transaction_status\": \"Failure\",\n" +
                        "      \"product_name\": \"Apple\",\n" +
                        "      \"purchased_date\": \"10-Mar-2018 2:36:52 pm\",\n" +
                        "      \"total_price\": 3000,\n" +
                        "      \"points\": 750\n" +
                        "    },\n" +
                        "    {\n" +
                        "      \"transaction_id\": \"99c6d2a3-54d5-4b5f-8259-fc0da63ed4c4\",\n" +
                        "      \"transaction_status\": \"Success\",\n" +
                        "      \"product_name\": \"Banana\",\n" +
                        "      \"purchased_date\": \"10-Feb-2018 2:36:52 pm\",\n" +
                        "      \"total_price\": 4000,\n" +
                        "      \"points\": 1000\n" +
                        "    },\n" +
                        "    {\n" +
                        "      \"transaction_id\": \"99c6d2a3-54d5-4b5f-8259-fc0da63ed4c4\",\n" +
                        "      \"transaction_status\": \"Success\",\n" +
                        "      \"product_name\": \"Grapes\",\n" +
                        "      \"purchased_date\": \"20-Feb-2018 2:36:52 pm\",\n" +
                        "      \"total_price\": 500,\n" +
                        "      \"points\": 125\n" +
                        "    }\n" +
                        "  ]\n" +
                        "}";


                JSONObject jsonObject = new JSONObject(jsonStructure);

                Logger.logD("customer_id",jsonObject.getInt("customer_id")+"");



                customerID=jsonObject.getInt("customer_id");
                customer_Name=jsonObject.getString("customer_name");
                mobile_Number=jsonObject.getInt("mobile_number");
                card_Number=jsonObject.getString("card_number");
                emaid_Customer=jsonObject.getString("email");
                created_Date=jsonObject.getString("created_date");
                customerImage=jsonObject.getString("customer_image");


                JSONArray jsonArray = jsonObject.getJSONArray("transaction_history");

                for(int i=0; i<jsonArray.length(); i++)
                {
                    JSONObject innerJsonObject=jsonArray.getJSONObject(i);

                    Logger.logD("transaction_id",innerJsonObject.getString("transaction_id"));

                    transaction_ID=innerJsonObject.getString("transaction_id");
                    transaction_Status=innerJsonObject.getString("transaction_status");
                    product_Name=innerJsonObject.getString("product_name");
                    purchased_Date=innerJsonObject.getString("purchased_date");
                    total_Price=innerJsonObject.getInt("total_price");
                    getPoints=  innerJsonObject.getInt("points");

                    filltheList();

                }

            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
    }




    private void filltheList() {
        //Call Api and fetch the item list after filling items
        itemBeens.add(new ItemBeen(transaction_Status, String.valueOf(total_Price),purchased_Date));
        Logger.logD("transaction_Status",transaction_Status);


        setAdapter();
    }

    private void setAdapter() {
        ItemAdapter itemAdapter = new ItemAdapter(CustomerProfile.this, itemBeens);
        listView.setAdapter(itemAdapter);
        listView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 1));
        listView.setHasFixedSize(true);
    }



    //new

    private void filltheList1() {
        //Call Api and fetch the item list after filling items
        itemBeens.add(new ItemBeen(transaction_Status, String.valueOf(total_Price),purchased_Date));
        Logger.logD("transaction_Status",transaction_Status);


        setAdapter1();
    }

    private void setAdapter1() {
        ItemAdapter itemAdapter = new ItemAdapter(CustomerProfile.this, itemBeens);
        listView.setAdapter(itemAdapter);
        listView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 1));
        listView.setHasFixedSize(true);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        else if(id == android.R.id.home){
           onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}




