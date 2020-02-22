package orlandohutapea.projectkeikaku;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AddCharsDialogFragment extends DialogFragment {
    static String INPUT_CHARS = "INPUT_CHARS";

    private static class AddCharsCharactersAdapter extends BaseAdapter {
        private Context mContext;
        private List<String> mSelectedChars = new ArrayList<>();
        private HashMap<String, View> mCharsPosition = new HashMap<>();

        private class ViewHolder {
            private TextView literalText;

            ViewHolder(TextView literalText) {
                this.literalText = literalText;
            }
        }

        AddCharsCharactersAdapter(Context context) {
            this.mContext = context;
        }

        public void setCharacterSelected(String string, boolean selected) {
            if (selected && !mSelectedChars.contains(string))
                mSelectedChars.add(string);
            else if (!selected && mSelectedChars.contains(string))
                mSelectedChars.remove(string);
        }

        public boolean isCharacterSelected(String string) {
            return mSelectedChars.contains(string);
        }

        public View getCharacterPosition(String string) {
            return mCharsPosition.get(string);
        }

        @Override
        public int getCount() {
            return Character.array.length;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public Character getItem(int position) {
            return Character.array[position];
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Character character = Character.array[position];

            if (mCharsPosition.containsKey(character.getLiteral()))
                mCharsPosition.remove(character.getLiteral());

            mCharsPosition.put(character.getLiteral(), convertView);

            if (convertView == null) {
                LayoutInflater layoutInflater = LayoutInflater.from(mContext);
                convertView = layoutInflater.inflate(R.layout.cell_addchars_char, null);

                TextView literalText = convertView.findViewById(R.id.character_literal);

                ViewHolder viewHolder = new ViewHolder(literalText);
                convertView.setTag(viewHolder);
            }

            // FIXME: Check if it's already in the cards..
            if (CharacterCard.map.containsKey(character.getLiteral())) {
                System.out.println("CARD: " + character.getLiteral());
                convertView.setClickable(false);
            }

            // Set the cell's background color
            if (isCharacterSelected(character.getLiteral()))
                convertView.setBackgroundColor(convertView.getContext().getColor(R.color.colorAccent));
            else
                convertView.setBackgroundColor(0xFFFFFFFF);

            ViewHolder viewHolder = (ViewHolder) convertView.getTag();
            viewHolder.literalText.setText(character.getLiteral());

            return convertView;
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_addchars, null);

        final EditText inputChars = (EditText) view.findViewById(R.id.input_chars);
        final GridView gridView = (GridView) view.findViewById(R.id.grid_chars);
        final AddCharsCharactersAdapter adapter = new AddCharsCharactersAdapter(view.getContext());

        inputChars.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                for (int i = start; i < start + count; i++) {
                    String literal = String.valueOf(s.charAt(i));
                    adapter.setCharacterSelected(literal, false);

                    if (adapter.getCharacterPosition(literal) != null)
                        adapter.getCharacterPosition(literal).setBackgroundColor(0xFFFFFFFF);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                for (int i = start; i < start + count; i++) {
                    String literal = String.valueOf(s.charAt(i));
                    adapter.setCharacterSelected(literal, true);

                    if (adapter.getCharacterPosition(literal) != null)
                        adapter.getCharacterPosition(literal).setBackgroundColor(
                                ContextCompat.getColor(
                                        adapter.getCharacterPosition(literal).getContext(),
                                        R.color.colorAccent));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String literal = adapter.getItem(position).getLiteral();
                adapter.setCharacterSelected(literal, !adapter.isCharacterSelected(literal));

                if (adapter.isCharacterSelected(literal)) {
                    inputChars.append(literal);
                    inputChars.setSelection(inputChars.getText().length());
                    view.setBackgroundColor(ContextCompat.getColor(view.getContext(), R.color.colorAccent));
                } else {
                    int index = inputChars.getText().toString().indexOf(literal);
                    inputChars.getText().delete(index, index + literal.length());
                    inputChars.setSelection(index);
                    view.setBackgroundColor(0xFFFFFFFF);
                }
            }
        });

        builder.setView(view)
                .setPositiveButton(R.string.dialog_add_button, null)
                .setNegativeButton(R.string.dialog_cancel_button, null);

        Dialog dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                final AlertDialog alertDialog = (AlertDialog) dialog;
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
                        .setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (inputChars.getText().length() > 0) {
                                    Intent intent = new Intent(getActivity(), LessonActivity.class);
                                    intent.putExtra(INPUT_CHARS, inputChars.getText().toString());
                                    startActivity(intent);
                                    alertDialog.dismiss();
                                } else
                                    Toast.makeText(getActivity().getApplicationContext(),
                                            R.string.dialog_toast_add_chars, Toast.LENGTH_SHORT)
                                            .show();
                            }
                        });
                alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                        .setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                alertDialog.cancel();
                            }
                        });
            }
        });

        return dialog;
    }
}
