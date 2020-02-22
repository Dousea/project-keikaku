package orlandohutapea.projectkeikaku;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class LessonActivity extends AppCompatActivity {
    /////////////
    /* APP BAR */
    /////////////
    private void initializeAppBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("");
    }

    ///////////
    /* CARDS */
    ///////////
    // TODO: Make this UpdateDatabaseTask work!
    private static class UpdateDatabaseTask extends AsyncTask<Void, Void, Void> {
        private WeakReference<LessonActivity> activityReference;

        UpdateDatabaseTask(LessonActivity activity) {
            this.activityReference = new WeakReference<>(activity);
        }

        @Override
        protected void onPreExecute() {
            LessonActivity activity = activityReference.get();

        }

        @Override
        protected Void doInBackground(Void... params) {
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            LessonActivity activity = activityReference.get();
            if (activity == null || activity.isFinishing()) return;


        }
    }

    private static class Action {
        String type;
        String characterLiteral;

        Action(String type, String characterLiteral) {
            this.type = type;
            this.characterLiteral = characterLiteral;
        }
    }

    List<Action> actions = new ArrayList<>();
    int currentCardPosition;
    int inputTextViewId;

    private void initializeCards() {
        currentCardPosition = 0;

        Intent intent = getIntent();
        String inputChars = intent.getStringExtra(AddCharsDialogFragment.INPUT_CHARS);
        char characters[] = inputChars.toCharArray();

        for (char character : characters)
            CharacterCard.list.add(new CharacterCard(
                    CharacterCard.LESSON,
                    Character.map.get(String.valueOf(character))));
    }

    /*
    private void updateCharacterDatabase(String typeOfUpdate) {
        CharacterDao dao = CharacterDatabase.getDatabase(this).characterDao();
        CharacterEntity entity = dao.findByLiteral(character.getLiteral());

        if (!typeOfUpdate.equals("NEW") && entity == null)
            try {
                throw new Exception("trying to update database with null entity");
            } catch (Exception e) {
                e.printStackTrace();
            }

        CharacterEntity prevEntity = entity != null ?
                entity.copy() :
                new CharacterEntity() { { setLiteral(character.getLiteral()); } };
        String typeOfUndo = "REVERT";

        switch (typeOfUpdate) {
            case "MEANING_CORRECT": {
                entity.setMeaningCorrect(true);
                break;
            }
            case "READING_CORRECT": {
                entity.setReadingCorrect(true);
                break;
            }
        }

        if (entity.isMeaningCorrect() && entity.isReadingCorrect())
            typeOfUpdate = "RAISE";

        switch (typeOfUpdate) {
            case "NEW": {
                typeOfUndo = "DELETE";

                entity = entity == null ? new CharacterEntity() : entity;
                entity.setLiteral(character.getLiteral());
                entity.setBox(0);
                entity.setInterval(0);
                entity.setMeaningCorrect(false);
                entity.setReadingCorrect(false);
                break;
            }
            case "RAISE":
            case "LOWER": {
                if (typeOfUpdate.equals("RAISE")) {
                    entity.setBox(entity.getBox() + 1);
                } else {
                    entity.setBox(entity.getBox() - 1);
                }

                entity.setMeaningCorrect(false);
                entity.setReadingCorrect(false);

                int type = ReviewTimeUtils.boxToIntervalType.get(entity.getBox());
                int value = ReviewTimeUtils.boxToIntervalValue.get(entity.getBox());
                Calendar newCalendar = (Calendar) Calendar.getInstance().clone();
                newCalendar.add(type, value);
                entity.setInterval(newCalendar.getTimeInMillis());
                break;
            }
        }

        CharacterDatabase.getDatabase(this).characterDao().update(entity);
    }

    private void undoCharacterDatabase() {
        UndoAction action = undoActions.get(undoActions.size());
        CharacterDao dao = CharacterDatabase.getDatabase(this).characterDao();

        switch (action.type) {
            case "DELETE":
                dao.delete(action.entity);
                break;
            default:
                dao.update(action.entity);
        }
    }
    */

    private void updateAction(String typeOfUpdate) {
        actions.add(new Action(typeOfUpdate,
                CharacterCard.list.get(currentCardPosition).character.getLiteral()));
    }

    private void undoAction() {
        actions.remove(actions.size() - 1);
    }

    private void updateCardView(Character character, int type) {
        LinearLayout view = findViewById(R.id.content);
        view.removeAllViews();

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;

        TextView literalText = new TextView(this);
        literalText.setLayoutParams(params);
        literalText.setText(character.getLiteral());

        switch (type) {
            case CharacterCard.LESSON: {
                String readingsString = "";
                for (Character.Reading reading : character.getReadings())
                    readingsString = readingsString.concat(reading.reading)
                            .concat(" (").concat(reading.type).concat(")");
                TextView readingsText = new TextView(this);
                readingsText.setLayoutParams(params);
                readingsText.setText(readingsString);

                String meaningsString = "";
                for (String meaning : character.getMeanings())
                    meaningsString = meaningsString.concat(meaning).concat("; ");
                TextView meaningsText = new TextView(this);
                meaningsText.setLayoutParams(params);
                meaningsText.setText(meaningsString);

                view.addView(readingsText);
                view.addView(meaningsText);
                break;
            }
            case CharacterCard.REVIEW_MEANING: {
                EditText meaningEditText = new EditText(this);
                meaningEditText.setLayoutParams(params);
                meaningEditText.setHint(getResources().getText(R.string.lesson_char_meaning_input_hint));
                meaningEditText.setInputType(InputType.TYPE_CLASS_TEXT |
                        InputType.TYPE_TEXT_VARIATION_LONG_MESSAGE);

                inputTextViewId = View.generateViewId();

                meaningEditText.setId(inputTextViewId);

                view.addView(meaningEditText);
                break;
            }
            case CharacterCard.REVIEW_MEANING_CORRECT:
            case CharacterCard.REVIEW_MEANING_INCORRECT: {
                String meaningsString = "";
                for (String meaning : character.getMeanings())
                    meaningsString = meaningsString.concat(meaning).concat("; ");
                TextView meaningsText = new TextView(this);
                meaningsText.setLayoutParams(params);
                meaningsText.setText(meaningsString);

                view.addView(meaningsText);
                break;
            }
            case CharacterCard.REVIEW_READING: {
                EditText readingEditText = new EditText(this);
                readingEditText.setLayoutParams(params);
                readingEditText.setHint(getResources().getText(R.string.lesson_char_reading_input_hint));
                readingEditText.setInputType(InputType.TYPE_CLASS_TEXT |
                        InputType.TYPE_TEXT_VARIATION_LONG_MESSAGE);

                inputTextViewId = View.generateViewId();

                readingEditText.setId(inputTextViewId);

                view.addView(readingEditText);
                break;
            }
            case CharacterCard.REVIEW_READING_CORRECT:
            case CharacterCard.REVIEW_READING_INCORRECT: {
                String readingsString = "";
                for (Character.Reading reading : character.getReadings())
                    readingsString = readingsString.concat(reading.reading)
                            .concat(" (").concat(reading.type).concat(")");
                TextView readingsText = new TextView(this);
                readingsText.setLayoutParams(params);
                readingsText.setText(readingsString);

                view.addView(readingsText);
                break;
            }
        }

        view.addView(literalText);
    }

    private void updateCardButton(final int type) {
        LinearLayout buttonBar = findViewById(R.id.button_bar);
        buttonBar.removeAllViews();

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                (int) getResources().getDimension(R.dimen.standard_0),
                ViewGroup.LayoutParams.WRAP_CONTENT);
        params.weight = 1;

        switch (type) {
            case CharacterCard.LESSON:
            case CharacterCard.REVIEW_MEANING_CORRECT:
            case CharacterCard.REVIEW_MEANING_INCORRECT:
            case CharacterCard.REVIEW_READING_CORRECT:
            case CharacterCard.REVIEW_READING_INCORRECT: {
                if (currentCardPosition > 0) {
                    Button prevButton = new Button(this, null, R.attr.buttonBarNegativeButtonStyle);
                    prevButton.setLayoutParams(params);
                    prevButton.setText(getResources().getText(R.string.lesson_action_prev));
                    prevButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            undoAction();
                            CharacterCard card = CharacterCard.list.get(--currentCardPosition);
                            updateCardView(card.character, card.type);
                            updateCardButton(card.type);
                        }
                    });

                    buttonBar.addView(prevButton);
                }

                if (currentCardPosition < CharacterCard.list.size() - 1) {
                    final String typeOfUpdate;

                    switch (type) {
                        case CharacterCard.REVIEW_MEANING_CORRECT:
                            typeOfUpdate = "MEANING_CORRECT";
                            break;
                        case CharacterCard.REVIEW_READING_CORRECT:
                            typeOfUpdate = "READING_CORRECT";
                            break;
                        case CharacterCard.REVIEW_MEANING_INCORRECT:
                        case CharacterCard.REVIEW_READING_INCORRECT:
                            typeOfUpdate = "LOWER";
                            break;
                        default:
                            typeOfUpdate = "NEW";
                    }

                    Button nextButton = new Button(this, null, R.attr.buttonBarPositiveButtonStyle);
                    nextButton.setLayoutParams(params);
                    nextButton.setText(getResources().getText(R.string.lesson_action_next));
                    nextButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            updateAction(typeOfUpdate);
                            CharacterCard card = CharacterCard.list.get(++currentCardPosition);
                            updateCardView(card.character, card.type);
                            updateCardButton(card.type);
                        }
                    });

                    buttonBar.addView(nextButton);
                }

                if (currentCardPosition == CharacterCard.list.size() - 1) {
                    Button completeButton = new Button(this, null, R.attr.buttonBarButtonStyle);
                    completeButton.setLayoutParams(params);
                    completeButton.setText(getResources().getText(R.string.lesson_action_complete));
                    completeButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // TODO: Do something about the "actions" variable
                            Intent intent = new Intent(LessonActivity.this, MainActivity.class);
                            startActivity(intent);
                        }
                    });

                    buttonBar.addView(completeButton);
                }

                break;
            }
            case CharacterCard.REVIEW_MEANING:
            case CharacterCard.REVIEW_READING: {
                Button checkButton = new Button(this, null, R.attr.buttonBarButtonStyle);
                checkButton.setLayoutParams(params);
                checkButton.setText(getResources().getText(R.string.lesson_action_check));
                checkButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CharacterCard card = CharacterCard.list.get(currentCardPosition);

                        int checkType = 0;
                        LinearLayout view = findViewById(R.id.content);
                        EditText input = view.findViewById(inputTextViewId);
                        String inputString = input.getText().toString();

                        if (inputString.length() > 0) {
                            if (type == CharacterCard.REVIEW_MEANING) {
                                for (String meaning : card.character.getMeanings())
                                    if (inputString.equals(meaning))
                                        checkType = CharacterCard.REVIEW_MEANING_CORRECT;

                                if (checkType == 0)
                                    checkType = CharacterCard.REVIEW_MEANING_INCORRECT;
                            } else {
                                for (Character.Reading reading : card.character.getReadings())
                                    if (inputString.equals(reading.reading))
                                        checkType = CharacterCard.REVIEW_READING_CORRECT;

                                if (checkType == 0)
                                    checkType = CharacterCard.REVIEW_READING_INCORRECT;
                            }

                            updateCardView(card.character, checkType);
                            updateCardButton(checkType);
                        } else
                            Toast.makeText(getApplicationContext(),
                                    type == CharacterCard.REVIEW_MEANING ?
                                    R.string.lesson_char_meaning_input_hint :
                                    R.string.lesson_char_reading_input_hint,
                                    Toast.LENGTH_LONG)
                                    .show();
                    }
                });

                buttonBar.addView(checkButton);
                break;
            }
        }
    }

    ///////////////
    /* OVERRIDES */
    ///////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_lesson);

        initializeCards();
        initializeAppBar();

        CharacterCard card = CharacterCard.list.get(currentCardPosition);
        updateCardView(card.character, card.type);
        updateCardButton(card.type);
    }

}
