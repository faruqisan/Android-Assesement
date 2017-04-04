package faruqisan.assesmentone_quiz;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import android.support.v7.widget.ShareActionProvider;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.LinkedList;
import Models.Quiz;

public class QuizActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
//    private Query query;

    TextView tvQuestionText;
    TextView tvHintText;
    EditText etAnswerField;

    Button btnAnswerButton;
    Button btnHintButton;

    int hintUsed;
    int maxHintUsed;
    int currentQuiz;
    int currentScore;
    int currentQuizHintUsed;

    SharedPreferences sharedPreferences;
    String playerName;
//    private ShareActionProvider mShareActionProvider;
    private ShareActionProvider mShareActionProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        this.tvQuestionText = (TextView) findViewById(R.id.questionText);
        this.tvHintText = (TextView) findViewById(R.id.hintText);
        this.etAnswerField = (EditText) findViewById(R.id.answerField);

        this.btnAnswerButton = (Button) findViewById(R.id.answerButton);
        this.btnHintButton = (Button) findViewById(R.id.hintButton);

        this.sharedPreferences = getSharedPreferences("quizGame", Context.MODE_PRIVATE);
        this.playerName= sharedPreferences.getString("playerName","");
        this.maxHintUsed=10;
        this.currentQuiz=0;
        this.currentScore=0;
        this.hintUsed=0;
        this.currentQuizHintUsed=0;

        if (playerName!= "" || playerName!= null) {
            this.getSupportActionBar().setTitle(playerName+" |");

        }

        this.btnAnswerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkAnswer(currentQuiz);
            }
        });

        this.btnHintButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showHintActivity();
            }
        });

        if (savedInstanceState != null) {
            this.currentQuiz = savedInstanceState.getInt("currentQuiz",0);
            this.hintUsed = savedInstanceState.getInt("hintUsed", 0);
            this.currentQuizHintUsed = savedInstanceState.getInt("currenQuizHintUsed", 0);
        }

        setQuiz(this.currentQuiz);
    }

    //untuk memasukkan actionbar ke activity
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_quiz_actions,menu);
        MenuItem item = menu.findItem(R.id.share);
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
        return true;
    }

    //untuk handle ketika actionbar di klik
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.share:
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
//                Intent sharingIntent = new Intent(Intent.ACTION_CHOOSER);
                sharingIntent.setType("text/plain");
                String answer = etAnswerField.getText().toString();
                sharingIntent.putExtra(Intent.EXTRA_SUBJECT,"share");
                sharingIntent.putExtra(Intent.EXTRA_TEXT, answer);
                startActivity(Intent.createChooser(sharingIntent,"sharing option"));
                return true;
            case R.id.exit:
                this.finish();
                return true;
            case R.id.restart :
                this.currentQuiz = 0;
                this.setQuiz(this.currentQuiz);

            default:
                return super.onOptionsItemSelected(item);

        }
    }

    public void setQuiz(int index){
        this.mDatabase = FirebaseDatabase.getInstance().getReference().child("questions").child(String.valueOf(index));
        Log.d("index from set",String.valueOf(index));
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try{
                    currentQuizHintUsed=0;
                    Quiz quiz = dataSnapshot.getValue(Quiz.class);

                    int min = 0;
                    int completeHintLength = quiz.getAnswer().length();
                    int randomWordAppear = min + (int) (Math.random() * (completeHintLength-1 - min) + 1);
                    char hintedCharacter = quiz.getAnswer().charAt(randomWordAppear);

                    String hint="";
                    for(int i=0;i<completeHintLength;i++) {
                        if (i == randomWordAppear) {
                            hint += String.valueOf(hintedCharacter);
                        }else{
                            hint += " _";
                        }
                    }

                    tvQuestionText.setText(quiz.getQuestion());
                    tvHintText.setText(hint);
                    etAnswerField.setText("");
                }catch (NullPointerException e){
                    Log.d("exc", "over");
                    // Game Over
                    Context context = getApplicationContext();
                    CharSequence text = "Game Over!";
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    public void showHintActivity(){
        Intent intent = new Intent(QuizActivity.this, HintActivity.class);
        startActivityForResult(intent,1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == 2) {
                this.mDatabase = FirebaseDatabase.getInstance().getReference().child("questions").child(String.valueOf(this.currentQuiz));
                mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Quiz quiz = dataSnapshot.getValue(Quiz.class);
                        String answer = quiz.getAnswer();
                        if (hintUsed < maxHintUsed) {
                            hintUsed++;
                            ++currentQuizHintUsed;
                            Log.d("crQHU", String.valueOf(currentQuizHintUsed));
                            String newHint="";
                            int tmpRandom = 0 + (int) (Math.random() * (answer.length()-1 - 0) + 1);
                            LinkedList<Integer> usedChar = new LinkedList<>();
                            LinkedList<String> tmpChar = new LinkedList<>();
                            for (int i=0;i<=currentQuizHintUsed;i++) {
                                Log.d("iter", String.valueOf(i));
                                int randomWordAppear = 0 + (int) (Math.random() * (answer.length()-1 - 0) + 1);
                                Log.d("randApp", String.valueOf(randomWordAppear));
                                for(int x=0;x<usedChar.size();x++) {
                                    if (randomWordAppear == usedChar.get(x)) {
                                        randomWordAppear = 0 + (int) (Math.random() * (answer.length()-1 - 0) + 1);
                                    }
                                }
//                                while (tmpRandom == randomWordAppear) {
//                                    randomWordAppear = 0 + (int) (Math.random() * (answer.length()-1 - 0) + 1);
//                                    Log.d("newRandApp", String.valueOf(randomWordAppear));
//                                }
                                char hintedCharacter = answer.charAt(randomWordAppear);
                                Log.d("hinted", String.valueOf(hintedCharacter));
                                tmpRandom = randomWordAppear;
                                usedChar.add(tmpRandom);
                                tmpChar.add(String.valueOf(hintedCharacter));
                            }
                            for(int j=0;j<answer.length();j++) {
                                boolean isHinted = false;
                                int charAt=0;
                                for (int k=0;k<usedChar.size();k++) {
                                    if(usedChar.get(k)==j){
                                        isHinted = true;
                                        charAt=k;
                                    }
                                }
                                if (isHinted) {
                                    newHint += tmpChar.get(charAt);
                                }else{
                                    newHint += " _ ";
                                }
                            }
                            Log.d("compHint", newHint);
                            tvHintText.setText(newHint);
                        }else{
                            Context context = getApplicationContext();
                            CharSequence text = "Hint Habis!";
                            int duration = Toast.LENGTH_SHORT;

                            Toast toast = Toast.makeText(context, text, duration);
                            toast.show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        }
    }

    public void addHint() {

        hintUsed++;
        if (hintUsed == maxHintUsed) {
            hintUsed = 0;
        }
//        new QuizRepository().getResponse(this, new VolleyCallback() {
//            @Override
//            public void onSuccesResponse(JSONArray result) {
//                try {
//                    JSONObject jsonObject = result.getJSONObject(currentQuiz);
//                    String completeHint = jsonObject.getString("answer").toString();
//                    int min = 0;
//                    int completeHintLength = completeHint.length();
//                    Log.d("user", String.valueOf(hintUsed));
//                    if (hintUsed == 0) {
//                        int randomWordAppear = min + (int) (Math.random() * (completeHintLength-1 - min) + 1);
//                        int randomWordAppear2;
//                        randomWordAppear2= min + (int) (Math.random() * (completeHintLength-1 - min) + 1);
//                        while (randomWordAppear == randomWordAppear2) {
//                            randomWordAppear2= min + (int) (Math.random() * (completeHintLength-1 - min) + 1);
//                        }
//
//                        char hintedCharacter = completeHint.charAt(randomWordAppear);
//                        char hintedCharacter2 = completeHint.charAt(randomWordAppear2);
//
//                        String hint="";
//                        for(int i=0;i<completeHintLength;i++) {
//                            if (i == randomWordAppear) {
//                                hint += String.valueOf(hintedCharacter);
//                            } else if (i == randomWordAppear2) {
//                                hint += String.valueOf(hintedCharacter2);
//                            } else {
//                                hint += " _";
//                            }
//                        }
//
//                        tvHintText.setText(hint);
//
//                    } else if (hintUsed == 1) {
//                        int randomWordAppear = min + (int) (Math.random() * (completeHintLength-1 - min) + 1);
//                        int randomWordAppear2;
//                        int randomWordAppear3;
//                        randomWordAppear2= min + (int) (Math.random() * (completeHintLength-1 - min) + 1);
//                        randomWordAppear3= min + (int) (Math.random() * (completeHintLength-1 - min) + 1);
//                        while (randomWordAppear == randomWordAppear2 && randomWordAppear == randomWordAppear3 && randomWordAppear2 == randomWordAppear3) {
//                            randomWordAppear2= min + (int) (Math.random() * (completeHintLength-1 - min) + 1);
//                            randomWordAppear3= min + (int) (Math.random() * (completeHintLength-1 - min) + 1);
//                        }
//
//                        char hintedCharacter = completeHint.charAt(randomWordAppear);
//                        char hintedCharacter2 = completeHint.charAt(randomWordAppear2);
//                        char hintedCharacter3 = completeHint.charAt(randomWordAppear3);
//
//                        String hint="";
//                        for(int i=0;i<completeHintLength;i++) {
//                            if (i == randomWordAppear) {
//                                hint += String.valueOf(hintedCharacter);
//                            } else if (i == randomWordAppear2) {
//                                hint += String.valueOf(hintedCharacter2);
//                            } else if (i ==  randomWordAppear3) {
//                                hint += String.valueOf(hintedCharacter3);
//                            } else {
//                                hint += " _";
//                            }
//                        }
//
//                        tvHintText.setText(hint);
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
    }

    public void checkAnswer(int index) {
        this.mDatabase = FirebaseDatabase.getInstance().getReference().child("questions").child(String.valueOf(index));
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Quiz quiz = dataSnapshot.getValue(Quiz.class);
                String inputtedAnswer = etAnswerField.getText().toString();
                if(quiz.getAnswer().equalsIgnoreCase(inputtedAnswer)){
                    Context context = getApplicationContext();
                    CharSequence text = "Benar!";
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }else{
                    Context context = getApplicationContext();
                    CharSequence text = "Salah!";
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }
                setQuiz(++currentQuiz);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }


        });
    }

    private void resetQuiz(){
        this.currentQuiz=0;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("currentQuiz",this.currentQuiz);
        outState.putInt("hintUsed", this.hintUsed);
        outState.putInt("currenQuizHintUsed",this.currentQuizHintUsed);
    }
}
