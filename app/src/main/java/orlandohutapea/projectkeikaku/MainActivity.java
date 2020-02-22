package orlandohutapea.projectkeikaku;

import android.animation.Animator;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.w3c.dom.CharacterData;

import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static class LoadTask extends AsyncTask<Void, Void, Void> {
        private WeakReference<MainActivity> activityReference;

        LoadTask(MainActivity activity) {
            this.activityReference = new WeakReference<>(activity);
        }

        @Override
        protected void onPreExecute() {
            MainActivity activity = activityReference.get();
            activity.setFABMenuVisibility(View.GONE);
            activity.insertProgressBar();
        }

        @Override
        protected Void doInBackground(Void... params) {
            MainActivity activity = activityReference.get();

            try {
                // Load XML file
                if (Character.map == null) {
                    CharacterXmlParser parser = new CharacterXmlParser();
                    HashMap<String, Character> list = parser.parse(
                            activity.getAssets().open("characters.xml", MODE_PRIVATE));

                    Character.map = list;
                    Character.array = list.values().toArray(new Character[list.size()]);
                }

                // Load database and set review cards
                if (CharacterCard.map == null) {
                    List<CharacterEntity> entities = CharacterDatabase.getDatabase(activity).characterDao()
                            .getNeedReviews();

                    CharacterCard.initialize();

                    for (CharacterEntity entity : entities) {
                        int type = entity.isMeaningCorrect() ?
                                CharacterCard.REVIEW_READING :
                                CharacterCard.REVIEW_MEANING;
                        CharacterCard newCard = new CharacterCard(type,
                                Character.map.get(entity.getLiteral()));
                        CharacterCard.insert(newCard, entity.getLiteral());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            MainActivity activity = activityReference.get();
            if (activity == null || activity.isFinishing()) return;

            activity.removeProgressBar();
            activity.updateCharacterGrid();
            activity.setFABMenuVisibility(View.VISIBLE);
        }
    }

    ////////////////////
    /* CHARACTER GRID */
    ////////////////////
    GridView characterGrid;
    LinearLayout primaryLayout;
    LinearLayout progressLayout;

    private static class CharactersAdapter extends BaseAdapter {
        private Context context;

        private class ViewHolder {
            private TextView literalText;

            ViewHolder(TextView literalText) {
                this.literalText = literalText;
            }
        }

        CharactersAdapter(Context context) {
            this.context = context;
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

            if (convertView == null) {
                LayoutInflater layoutInflater = LayoutInflater.from(context);
                convertView = layoutInflater.inflate(R.layout.cell_char, null);

                TextView literalText = convertView.findViewById(R.id.character_literal);

                ViewHolder viewHolder = new ViewHolder(literalText);
                convertView.setTag(viewHolder);
            }

            ViewHolder viewHolder = (ViewHolder) convertView.getTag();
            viewHolder.literalText.setText(character.getLiteral());

            return convertView;
        }
    }

    private void insertProgressBar() {
        progressLayout = new LinearLayout(this);
        progressLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));
        progressLayout.setGravity(Gravity.CENTER);

        ProgressBar progressBar = new ProgressBar(this);
        progressBar.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        progressBar.setIndeterminate(true);

        progressLayout.addView(progressBar);

        primaryLayout = findViewById(R.id.main_layout);
        primaryLayout.addView(progressLayout);
    }

    private void removeProgressBar() {
        primaryLayout.removeView(progressLayout);
    }

    private void updateCharacterGrid() {
        characterGrid = findViewById(R.id.character_grid);
        characterGrid.setAdapter(new CharactersAdapter(this));
    }

    /////////////////////////////////
    /* FLOATING ACTION BUTTON MENU */
    /////////////////////////////////
    FloatingActionButton fab, fabAddChar, fabReviewChar;
    LinearLayout fabAddCharLayout, fabReviewCharLayout;
    View fabBackground;
    boolean isFABOpen = false;

    private void initializeFABMenu() {
        fabAddCharLayout = findViewById(R.id.fab_add_layout);
        fabReviewCharLayout = findViewById(R.id.fab_review_layout);

        fab = findViewById(R.id.fab);
        fabAddChar = findViewById(R.id.fab_add);
        fabReviewChar = findViewById(R.id.fab_review);

        fabBackground = findViewById(R.id.fab_background);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isFABOpen) showFABMenu();
                else closeFABMenu();
            }
        });

        fabAddChar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment fragment = new AddCharsDialogFragment();
                fragment.show(getSupportFragmentManager(), "add_chars");
            }
        });

        fabBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeFABMenu();
            }
        });
    }

    private void setFABMenuVisibility(int visibility) {
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setVisibility(visibility);
    }

    private void showFABMenu() {
        isFABOpen = true;

        fab.animate().rotationBy(180);

        fabBackground.setVisibility(View.VISIBLE);
        fabBackground.animate().alpha(1f);

        fabAddCharLayout.setVisibility(View.VISIBLE);
        fabAddCharLayout.animate().alpha(1f);
        fabAddCharLayout.animate().translationY(-getResources().getDimension(R.dimen.standard_55));

        fabReviewCharLayout.setVisibility(View.VISIBLE);
        fabReviewCharLayout.animate().alpha(1f);
        fabReviewCharLayout.animate().translationY(-getResources().getDimension(R.dimen.standard_100));
    }

    private void closeFABMenu() {
        isFABOpen = false;

        fab.animate().rotationBy(-180);

        fabBackground.animate().alpha(0f);

        fabAddCharLayout.animate().alpha(0f);
        fabAddCharLayout.animate().translationY(0);

        fabReviewCharLayout.animate().alpha(0f);
        fabReviewCharLayout.animate().translationY(0).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if (!isFABOpen) {
                    fabAddCharLayout.setVisibility(View.GONE);
                    fabReviewCharLayout.setVisibility(View.GONE);
                    fabBackground.setVisibility(View.GONE);
                }
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
    }

    ///////////////////////
    /* NAVIGATION DRAWER */
    ///////////////////////
    DrawerLayout drawerLayout;

    private void initializeNavigationDrawer() {
        drawerLayout = findViewById(R.id.drawer_layout);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setCheckedItem(R.id.nav_char);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                // Set item as selected to persist highlight
                menuItem.setChecked(true);
                // Close drawer when item is tapped
                drawerLayout.closeDrawers();

                // TODO: Swap UI fragments and shits here

                return true;
            }
        });
    }

    /////////////
    /* APP BAR */
    /////////////
    private void initializeAppBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);
    }

    ///////////////
    /* OVERRIDES */
    ///////////////
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        initializeAppBar();
        initializeNavigationDrawer();
        initializeFABMenu();
    }

    @Override
    public void onResume() {
        if (Character.map == null || CharacterCard.map == null)
            try {
                new LoadTask(this).execute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        else
            updateCharacterGrid();

        super.onResume();
    }

    @Override
    public void onBackPressed() {
        if (isFABOpen) closeFABMenu();
        else super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
