package neutrinos.addme.activity;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import neutrinos.addme.R;
import neutrinos.addme.utilities.Logger;

public class PremiumContent extends AppCompatActivity {
    Context context;
    TextView offer_code;
    private Button offer_btn;
    private ImageView premium_image;
    private Button mapButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_premium_content);
        offer_code = findViewById(R.id.offer_code);
        offer_btn = findViewById(R.id.offer_btn);
        premium_image = findViewById(R.id.premium_image);
        mapButton = findViewById(R.id.map_btn);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        final Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String premiumImage = bundle.getString("image");
            Logger.logD("imagecheck", "" + premiumImage);
            Picasso.get()
                    .load(premiumImage).fit()
                    .into(premium_image);
        }
        context = PremiumContent.this;
        offer_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                assert cm != null;
                cm.setText(offer_code.getText());
                Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show();
            }
        });
        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle1=new Bundle();
                bundle1.putString("latlong","13.057823,77.5967276");
                Intent intent=new Intent(PremiumContent.this,MapsActivity.class);
                intent.putExtras(bundle1);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
