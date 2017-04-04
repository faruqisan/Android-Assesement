package faruqisan.assesmentone_quiz;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class HintActivity extends AppCompatActivity {

    Button btnYakin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hint);

        this.btnYakin = (Button) findViewById(R.id.buttonYakin);
        this.btnYakin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("useHint", "Use");
                setResult(2,intent);
                finish();
            }
        });
    }


}
