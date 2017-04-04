package faruqisan.assesmentone_quiz;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class WelcomeActivity extends AppCompatActivity {

    EditText etPlayerName;
    Button btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        this.etPlayerName = (EditText) findViewById(R.id.welcomeName);
        this.btnSubmit = (Button) findViewById(R.id.welcomeSubmitButton);
        this.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startGame();
            }
        });
    }

    public void startGame(){
        String playerName = this.etPlayerName.getText().toString();
        SharedPreferences sharedPreferences = getSharedPreferences("quizGame", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (saveNameToSharedPreferences(playerName, editor)) {
            Intent intent = new Intent(WelcomeActivity.this,QuizActivity.class);
            startActivity(intent);
        }

    }

    private boolean saveNameToSharedPreferences(String name, SharedPreferences.Editor editor){
        editor.putString("playerName",name);
        if(editor.commit()){
            return true;
        }
        return false;
    }
}
