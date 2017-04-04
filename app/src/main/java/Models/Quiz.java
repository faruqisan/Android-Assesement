package Models;

/**
 * Created by faruqisan on 18-Feb-17.
 */

public class Quiz {
    private int id;
    private String question;
    private String answer;
    private boolean answered;

    public Quiz() {

    }

    public Quiz(int id, String answer, String question) {
        this.id = id;
        this.answer = answer;
        this.question = question;
        this.answered=false;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public boolean isAnswered() {
        return answered;
    }

    public void setAnswered(boolean answered) {
        this.answered = answered;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }
}
