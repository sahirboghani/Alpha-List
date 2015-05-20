package gov.sahir.alphalist;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import gov.sahir.alphalist.Database.ListTable;

/**
 * Created by Sahir on 5/13/2015.
 */
public class ListCursorAdapter extends CursorAdapter {

    private static class ViewHolder {
        ImageView icon;
        TextView item;
        boolean isDone;
    }

    private static final int ID = 0;
    private static final int DESCRIPTION = 1;
    private static final int DONE = 2;

    public static final String[] COLUMNS = new String[] { ListTable.COLUMN_ID, ListTable.COLUMN_DESCRIPTION, ListTable.COLUMN_DONE };
    public static final String ORDER_BY = ListTable.COLUMN_DONE + "," + ListTable.COLUMN_ID;

    private static final int TRUE = 1;

    private LayoutInflater mInflater;

    public ListCursorAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, flags);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        View row = mInflater.inflate(R.layout.todo_item, viewGroup, false);

        // cache views
        ViewHolder viewHolder = new ViewHolder();
        viewHolder.icon = (ImageView) row.findViewById(R.id.icon);
        viewHolder.item = (TextView) row.findViewById(R.id.item);
        row.setTag(viewHolder);

        return row;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = updateViewHolderValues(view, cursor);
        decorateView(viewHolder, cursor);
    }

    private ViewHolder updateViewHolderValues(View view, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();

        viewHolder.item.setText(cursor.getString(DESCRIPTION));
        viewHolder.isDone = (cursor.getInt(DONE) == TRUE);

        return viewHolder;
    }

    private void decorateView(ViewHolder task, Cursor cursor) {
        if(task.isDone)
            task.item.setPaintFlags(task.item.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        else
            task.item.setPaintFlags(task.item.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
    }

    public static int toggled(View view) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        viewHolder.isDone = !viewHolder.isDone;

        return viewHolder.isDone ? 1 : 0;
    }

    public static boolean isItemDone(View view) {
        return ((ViewHolder)view.getTag()).isDone;
    }

}
