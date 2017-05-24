package com.example.hussainshabbir.romanquiz;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.CountDownTimer;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Random;

public class QuizActivity extends AppCompatActivity {
    String randomQuestion = "";
    private int randomQuizNumber = 0;
    private int count = 0;
    private int questionIndex = 1;
    private CountDownTimer timer;
    private int target = 0;
    private int totalScore = 0;
    private ArrayList selectedCorrectAnswers;
    Button first, second, third, fourth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);
        //getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00BFFF")));
        getSupportActionBar().hide();
        final Button start = (Button)findViewById(R.id.Start);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start.setVisibility(View.GONE);
                startPuzzle();
            }
        });
    }

    private int generateRandomQuizNumber() {
        Random random = new Random();
        int number = 0;
        Intent intent = getIntent();
        String levelType = intent.getStringExtra("level");
        TextView levelTypeTxtVw = (TextView)findViewById(R.id.Level);
        levelTypeTxtVw.setText(String.format("Level: %s",levelType.toUpperCase()));
        switch (levelType) {
            case "easy":
                number = random.nextInt(100);
                while (number == 0) {
                    number=random.nextInt(100);
                }
                break;
            case "medium":
                number = random.nextInt(1000);
                while (number == 0) {
                    number=random.nextInt(1000);
                }
                break;
            case "hard":
                number = random.nextInt(5000);
                while (number == 0) {
                    number=random.nextInt(5000);
                }
                break;
        }
        return number;
    }

    private void updateButtonOptions() {
        first = (Button)findViewById(R.id.firstOption);
        second = (Button)findViewById(R.id.secondOption);
        third = (Button)findViewById(R.id.thirdOption);
        fourth = (Button)findViewById(R.id.fourthOption);
        final Button buttons[] = {first,second,third,fourth};
        configureButton(buttons);
        String optionList[] = randomizeOptionsValue();
        if (optionList != null && optionList.length > 0) {
            int j = 0;
            for (String value : optionList) {
                for (int i=j; i<4; i++) {
                    buttons[i].setText(value);
                    j++;
                    break;
                }
            }
        } else {
            first.setText(String.valueOf(generateRandomQuizNumber()));
            second.setText(String.valueOf(generateRandomQuizNumber()));
            third.setText(String.valueOf(generateRandomQuizNumber()));
            fourth.setText(String.valueOf(generateRandomQuizNumber()));
        }
        Random ansOption = new Random();
        int whichOptionHasAns = ansOption.nextInt(4);
        while (whichOptionHasAns == 0) {
            whichOptionHasAns = ansOption.nextInt(4);
        }
        switch (whichOptionHasAns) {
            case 1:
                first.setText(String.valueOf(randomQuizNumber));
                break;
            case 2:
                second.setText(String.valueOf(randomQuizNumber));
                break;
            case 3:
                third.setText(String.valueOf(randomQuizNumber));
                break;
            case 4:
                fourth.setText(String.valueOf(randomQuizNumber));
                break;
        }
        questionIndex++;
    }

    private void generateRandomQuestion() {
        randomQuestion = "";
        int number = generateRandomQuizNumber();
        while (number == 0) {
            number = generateRandomQuizNumber();
        }
        randomQuizNumber = number;
        String romanLiterals[] = {"M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I"};
        int romanNumbers[] = {1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1};
        for (int i=0; i< romanNumbers.length; i++) {
            while (number >= romanNumbers[i]) {
                number = number -  romanNumbers[i];
                randomQuestion = randomQuestion.concat(romanLiterals[i]);
            }
        }
        TextView puzzleQuestion = (TextView)findViewById(R.id.PuzzleQuestion);
        String questionFormat = String.format("%d) %s",questionIndex,randomQuestion);
        puzzleQuestion.setText(questionFormat);
        TextView questions = (TextView)findViewById(R.id.Question);
        questionFormat = String.format("Questions: %d/20",questionIndex);
        questions.setText(questionFormat);
    }

    private void configurePuzzle() {
        generateRandomQuestion();
        updateButtonOptions();
    }

    private void configureTarget() {
        target = generateRandomQuizNumber();
        TextView targetVw = (TextView)findViewById(R.id.Target);
        targetVw.setText(String.format("Target: %s",String.valueOf(target)));
    }

    private void configureButton(Button[] buttons) {
        for (final Button button : buttons) {
            final Button btn = button;
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    count++;
                    if (count >= 20) {
                        btn.setEnabled(false);
                        selectedCorrectAnswers = null;
                        if (totalScore >= target) {
                            disableButtons();
                            resetTimer();
                            displayAlert("Congratulations! You won:)");
                        } else {
                            disableButtons();
                            resetTimer();
                            displayAlert("Sorry! You lost:(");
                        }
                        return;
                    } else if (totalScore >= target) {
                        disableButtons();
                        resetTimer();
                        displayAlert("Congratulations! You won:)");
                        return;
                    } else {
                        validateAnswer(button);
                        configurePuzzle();
                    }
                }
            });
        }
    }

    private void validateAnswer(Button button) {
        if (selectedCorrectAnswers == null) {
            selectedCorrectAnswers = new ArrayList<String>();
        }
        String buttonTxt = button.getText().toString();
        if (Integer.parseInt(buttonTxt) == randomQuizNumber) {
            selectedCorrectAnswers.add(button.getText());
        }
        int targetScore = target/10;
        int rightAnsCount = selectedCorrectAnswers.size();
        totalScore = (int)Math.round(rightAnsCount * targetScore);
        TextView score = (TextView)findViewById(R.id.Score);
        score.setText(String.format("Score: %d",totalScore));
    }

    private void executeTimer() {
        final TextView timerTxtVw = (TextView)findViewById(R.id.Time);
        timer = new CountDownTimer(45000, 1000) {
            public void onTick(long millisUntilFinished) {
                int sec= (int)millisUntilFinished / 1000;
                timerTxtVw.setText(String.format("Time: %d",sec));
                //here you can have your logic to set text to edittext
            }

            public void onFinish() {
                System.out.print("done");
                timerTxtVw.setText(String.format("Time: %d",0));
                disableButtons();
                if (totalScore >= target) {
                    displayAlert("Congratulations! You won:)");
                } else {
                    disableButtons();
                    displayAlert("Sorry! You lost:(");
                }
            }
        }.start();
    }

    private void resetTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    private void disableButtons() {
        Button buttons[] = {first,second,third,fourth};
        for (Button button: buttons) {
            button.setEnabled(false);
        }
    }

    private void displayAlert(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(QuizActivity.this);
        builder.setTitle("Result")
                .setMessage(msg)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private String[] randomizeOptionsValue() {
        String option = String.valueOf(randomQuizNumber);
        String optionList[] = null;
        int lastCharLength = 0;
        if (option.length() > 1) {
            lastCharLength = option.length() - 1;
        } else if (option.length() > 2) {
            lastCharLength = option.length() - 2;
        } else if (option.length() > 3) {
            lastCharLength = option.length() - 3;
        } else if (option.length() > 4) {
            lastCharLength = option.length() - 4;
        }
        if (lastCharLength > 0) {
            String firstFewChar = option.substring(0, lastCharLength);
            String lastFewChar = option.substring(lastCharLength);
            System.out.print(firstFewChar);
            System.out.print(lastFewChar);
            int firstValue = Integer.parseInt(firstFewChar);
            int secondValue = Integer.parseInt(lastFewChar);
            optionList = new String[4];
            for (int i = 0; i< 4; i++) {
                firstValue = firstValue + 1;
                System.out.print(String.format("%d Value = %d", i, firstValue));
                optionList[i] = String.format("%d%d",firstValue, secondValue);
            }
        }
        return optionList;
    }

    private void startPuzzle() {
        Button start = (Button)findViewById(R.id.Start);
        configurePuzzle();
        configureTarget();
        executeTimer();
    }
}
