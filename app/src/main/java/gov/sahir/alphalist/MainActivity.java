package gov.sahir.alphalist;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

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

}
