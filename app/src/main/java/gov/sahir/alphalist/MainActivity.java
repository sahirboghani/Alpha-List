package gov.sahir.alphalist;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import gov.sahir.alphalist.ContentProvider.ListContentProvider;
import gov.sahir.alphalist.Database.ListTable;


public class MainActivity extends ListActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private ListCursorAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView listView = getListView();
        filldata();

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {

                // delete if marked off
                if (ListCursorAdapter.isItemDone(view)) {
                    Uri uri = Uri.parse(ListContentProvider.CONTENT_URI + "/" + id);
                    getContentResolver().delete(uri, null, null);
                }

                return true;
            }
        });

        Button button = (Button) findViewById(R.id.add_todo_button);
        button.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                fancyToast(getQuote());
                return true;
            }
        });
    }

    @Override
    protected void onListItemClick(ListView listView, View view, int position, long id) {
        super.onListItemClick(listView, view, position, id);

        ContentValues contentValues = new ContentValues();
        contentValues.put(ListTable.COLUMN_DONE, ListCursorAdapter.toggled(view));

        Uri uri = Uri.parse(ListContentProvider.CONTENT_URI + "/" + id);
        getContentResolver().update(uri, contentValues, null, null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
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

        return super.onOptionsItemSelected(item);
    }

    private void filldata(){
        getLoaderManager().initLoader(0, null, this);
        mAdapter = new ListCursorAdapter(this, null, 0);
        setListAdapter(mAdapter);
    }

    public void addTodoButtonHandler(View view) {
        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
        View layout = layoutInflater.inflate(R.layout.new_todo, (ViewGroup) findViewById(R.id.new_todo_root_element));

        final EditText newTodo = (EditText) layout.findViewById(R.id.new_todo);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Alpha List")

                .setView(layout)

                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int id) {
                       addTodoHelper(newTodo.getText().toString());
                    }
                })

                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
        builder.create().show();
    }

    private void addTodoHelper(String newTodo) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ListTable.COLUMN_DESCRIPTION, newTodo);
        contentValues.put(ListTable.COLUMN_DONE, 0);
        getContentResolver().insert(ListContentProvider.CONTENT_URI, contentValues);
    }

    //
    //  LoaderManager.LoaderCallbacks
    //

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
        return new CursorLoader(this, ListContentProvider.CONTENT_URI, ListCursorAdapter.COLUMNS, null, null, ListCursorAdapter.ORDER_BY);
    }
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mAdapter.swapCursor(cursor);
    }
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }


    private String getQuote() {
        return quotes[(int)(Math.random()*quotes.length)];
    }

    private void fancyToast(CharSequence text) {
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.quote_toast, (ViewGroup) findViewById(R.id.quote_toast_layout_root));
        view.setBackgroundColor(Color.parseColor("#4099FF"));
        TextView textView = (TextView) view.findViewById(R.id.quote);
        textView.setText(text);

        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.BOTTOM, 0, 50);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(view);
        toast.show();
    }

    private static String[] quotes = {"Life is about making an impact, not making an income.\n\nKevin Kruse",
            "Whatever the mind of man can conceive and believe, it can achieve. \n\nNapoleon Hill",
            "Strive not to be a success, but rather to be of value. \n\nAlbert Einstein",
            "Two roads diverged in a wood, and I - I took the one less traveled by, And that has made all the difference.  \n\nRobert Frost",
            "I attribute my success to this: I never gave or took any excuse. \n\nFlorence Nightingale",
            "You miss 100% of the shots you don\'t take. \n\nWayne Gretzky",
            "I\'ve missed more than 9000 shots in my career. I\'ve lost almost 300 games. 26 times I\'ve been trusted to take the game winning shot and missed. I\'ve failed over and over and over again in my life. And that is why I succeed. \n\nMichael Jordan",
            "The most difficult thing is the decision to act, the rest is merely tenacity. \n\nAmelia Earhart",
            "Every strike brings me closer to the next home run. \n\nBabe Ruth",
            "Definiteness of purpose is the starting point of all achievement. \n\nW. Clement Stone",
            "Life isn\'t about getting and having, it\'s about giving and being. \n\nKevin Kruse",
            "Life is what happens to you while you\'re busy making other plans. \n\nJohn Lennon",
            "We become what we think about. \n\nEarl Nightingale",
            "Twenty years from now you will be more disappointed by the things that you didn\'t do than by the ones you did do, so throw off the bowlines, sail away from safe harbor, catch the trade winds in your sails.  Explore, Dream, Discover. \n\nMark Twain",
            "Life is 10% what happens to me and 90% of how I react to it. \n\nCharles Swindoll",
            "The most common way people give up their power is by thinking they don\'t have any. \n\nAlice Walker",
            "The mind is everything. What you think you become.  \n\nBuddha",
            "The best time to plant a tree was 20 years ago. The second best time is now. \n\nChinese Proverb",
            "An unexamined life is not worth living. \n\nSocrates",
            "Eighty percent of success is showing up. \n\nWoody Allen",
            "Your time is limited, so don\'t waste it living someone else\'s life. \n\nSteve Jobs",
            "Winning isn\'t everything, but wanting to win is. \n\nVince Lombardi",
            "I am not a product of my circumstances. I am a product of my decisions. \n\nStephen Covey",
            "Every child is an artist.  The problem is how to remain an artist once he grows up. \n\nPablo Picasso",
            "You can never cross the ocean until you have the courage to lose sight of the shore. \n\nChristopher Columbus",
            "I\'ve learned that people will forget what you said, people will forget what you did, but people will never forget how you made them feel. \n\nMaya Angelou",
            "Either you run the day, or the day runs you. \n\nJim Rohn",
            "Whether you think you can or you think you can\'t, you\'re right. \n\nHenry Ford",
            "The two most important days in your life are the day you are born and the day you find out why. \n\nMark Twain",
            "Whatever you can do, or dream you can, begin it.  Boldness has genius, power and magic in it. \n\nJohann Wolfgang von Goethe",
            "The best revenge is massive success. \n\nFrank Sinatra",
            "People often say that motivation doesn\'t last. Well, neither does bathing.  That\'s why we recommend it daily. \n\nZig Ziglar",
            "Life shrinks or expands in proportion to one\'s courage. \n\nAnais Nin",
            "If you hear a voice within you say \'\'you cannot paint,\'\' then by all means paint and that voice will be silenced. \n\nVincent Van Gogh",
            "There is only one way to avoid criticism: do nothing, say nothing, and be nothing. \n\nAristotle",
            "Ask and it will be given to you; search, and you will find; knock and the door will be opened for you. \n\nJesus",
            "The only person you are destined to become is the person you decide to be. \n\nRalph Waldo Emerson",
            "Go confidently in the direction of your dreams.  Live the life you have imagined. \n\nHenry David Thoreau",
            "When I stand before God at the end of my life, I would hope that I would not have a single bit of talent left and could say, I used everything you gave me. \n\nErma Bombeck",
            "Few things can help an individual more than to place responsibility on him, and to let him know that you trust him. \n\nBooker T. Washington",
            "Certain things catch your eye, but pursue only those that capture the heart. \n\nAncient Indian Proverb",
            "Believe you can and you\'re halfway there. \n\nTheodore Roosevelt",
            "Everything you\'ve ever wanted is on the other side of fear. \n\nGeorge Addair",
            "We can easily forgive a child who is afraid of the dark; the real tragedy of life is when men are afraid of the light. \n\nPlato",
            "Teach thy tongue to say, \'\'I do not know,\'\' and thous shalt progress. \n\nMaimonides",
            "Start where you are. Use what you have.  Do what you can. \n\nArthur Ashe",
            "When I was 5 years old, my mother always told me that happiness was the key to life.  When I went to school, they asked me what I wanted to be when I grew up.  I wrote down \'happy\'.  They told me I didn\'t understand the assignment, and I told them they didn\'t understand life. \n\nJohn Lennon",
            "Fall seven times and stand up eight. \n\nJapanese Proverb",
            "When one door of happiness closes, another opens, but often we look so long at the closed door that we do not see the one that has been opened for us. \n\nHelen Keller",
            "Everything has beauty, but not everyone can see. \n\nConfucius",
            "How wonderful it is that nobody need wait a single moment before starting to improve the world. \n\nAnne Frank",
            "When I let go of what I am, I become what I might be. \n\nLao Tzu",
            "Life is not measured by the number of breaths we take, but by the moments that take our breath away. \n\nMaya Angelou",
            "Happiness is not something readymade.  It comes from your own actions. \n\nDalai Lama",
            "If you\'re offered a seat on a rocket ship, don\'t ask what seat! Just get on. \n\nSheryl Sandberg",
            "First, have a definite, clear practical ideal; a goal, an objective. Second, have the necessary means to achieve your ends; wisdom, money, materials, and methods. Third, adjust all your means to that end. \n\nAristotle",
            "If the wind will not serve, take to the oars. \n\nLatin Proverb",
            "You can\'t fall if you don\'t climb.  But there\'s no joy in living your whole life on the ground. \n\nUnknown",
            "We must believe that we are gifted for something, and that this thing, at whatever cost, must be attained. \n\nMarie Curie",
            "Too many of us are not living our dreams because we are living our fears. \n\nLes Brown",
            "Challenges are what make life interesting and overcoming them is what makes life meaningful. \n\nJoshua J. Marine",
            "If you want to lift yourself up, lift up someone else. \n\nBooker T. Washington",
            "I have been impressed with the urgency of doing. Knowing is not enough; we must apply. Being willing is not enough; we must do. \n\nLeonardo da Vinci",
            "Limitations live only in our minds.  But if we use our imaginations, our possibilities become limitless. \n\nJamie Paolinetti",
            "You take your life in your own hands, and what happens? A terrible thing, no one to blame. \n\nErica Jong",
            "What\'s money? A man is a success if he gets up in the morning and goes to bed at night and in between does what he wants to do. \n\nBob Dylan",
            "I didn\'t fail the test. I just found 100 ways to do it wrong. \n\nBenjamin Franklin",
            "In order to succeed, your desire for success should be greater than your fear of failure. \n\nBill Cosby",
            "A person who never made a mistake never tried anything new. \n\nAlbert Einstein",
            "The person who says it cannot be done should not interrupt the person who is doing it. \n\nChinese Proverb",
            "There are no traffic jams along the extra mile. \n\nRoger Staubach",
            "It is never too late to be what you might have been. \n\nGeorge Eliot",
            "You become what you believe. \n\nOprah Winfrey",
            "I would rather die of passion than of boredom. \n\nVincent van Gogh",
            "A truly rich man is one whose children run into his arms when his hands are empty. \n\nUnknown",
            "It is not what you do for your children, but what you have taught them to do for themselves, that will make them successful human beings.  \n\nAnn Landers",
            "If you want your children to turn out well, spend twice as much time with them, and half as much money. \n\nAbigail Van Buren",
            "Build your own dreams, or someone else will hire you to build theirs. \n\nFarrah Gray",
            "Education costs money.  But then so does ignorance. \n\nSir Claus Moser",
            "I have learned over the years that when one\'s mind is made up, this diminishes fear. \n\nRosa Parks",
            "Remember that not getting what you want is sometimes a wonderful stroke of luck. \n\nDalai Lama",
            "You can\'t use up creativity.  The more you use, the more you have. \n\nMaya Angelou",
            "Dream big and dare to fail. \n\nNorman Vaughan",
            "Our lives begin to end the day we become silent about things that matter. \n\nMartin Luther King Jr.",
            "Do what you can, where you are, with what you have. \n\nTeddy Roosevelt",
            "If you do what you\'ve always done, you\'ll get what you\'ve always gotten. \n\nTony Robbins",
            "Dreaming, after all, is a form of planning. \n\nGloria Steinem",
            "It\'s your place in the world; it\'s your life. Go on and do all you can with it, and make it the life you want to live. \n\nMae Jemison",
            "You may be disappointed if you fail, but you are doomed if you don\'t try. \n\nBeverly Sills",
            "Remember no one can make you feel inferior without your consent. \n\nEleanor Roosevelt",
            "Life is what we make it, always has been, always will be. \n\nGrandma Moses",
            "The question isn\'t who is going to let me; it\'s who is going to stop me. \n\nAyn Rand",
            "When everything seems to be going against you, remember that the airplane takes off against the wind, not with it. \n\nHenry Ford",
            "It\'s not the years in your life that count. It\'s the life in your years. \n\nAbraham Lincoln",
            "Change your thoughts and you change your world. \n\nNorman Vincent Peale",
            "Either write something worth reading or do something worth writing. \n\nBenjamin Franklin",
            "The only way to do great work is to love what you do. \n\nSteve Jobs",
            "If you can dream it, you can achieve it. \n\nZig Ziglar"};

}

