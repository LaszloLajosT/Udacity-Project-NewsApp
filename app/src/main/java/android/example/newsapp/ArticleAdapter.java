package android.example.newsapp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class ArticleAdapter extends ArrayAdapter<Article> {

    private static final String LOG_TAG = android.example.newsapp.ArticleAdapter.class.getSimpleName();

    public ArticleAdapter(Context context, ArrayList<Article> articles) {
        super(context, 0, articles);
    }

    /**
     * Provides a view for an AdapterView (ListView, GridView, etc.)
     *
     * @param position    The position in the list of data that should be displayed in the
     *                    list item view.
     * @param convertView The recycled view to populate.
     * @param parent      The parent ViewGroup that is used for inflation.
     * @return The View for the position in the AdapterView.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Check if the existing view is being reused, otherwise inflate the view
        View listItemView = convertView;

        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.article_list_item, parent, false);
        }

        Article current_article = getItem(position);

        //Save (year,month,day) from webPublicationDate
        String date = null;
        //Save (hour,minutes) from webPublicationDate
        String time = null;

        ImageView image = listItemView.findViewById(R.id.imageView);
        image.setImageBitmap(current_article.getMainPicture());

        TextView sectionNameTextView = listItemView.findViewById(R.id.section_name_text_view);
        sectionNameTextView.setText(current_article.getSectionName());

        // Find the TextView with view ID magnitude
        TextView webTitleTextView = listItemView.findViewById(R.id.headline_article_text_view);
        webTitleTextView.setText(current_article.getHeadLine());

        TextView authorTextView = listItemView.findViewById(R.id.author_text_view);
        authorTextView.setText(current_article.getAuthor());


        SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'Z'");
        String dateInString = current_article.getWebPublicationDate();
        try {
            Date publicationDate = myFormat.parse(dateInString);

            SimpleDateFormat dates = new SimpleDateFormat("dd MMMM yyyy");
            SimpleDateFormat times = new SimpleDateFormat("h:mm a");
            date = dates.format(publicationDate);
            time = times.format(publicationDate);
            Log.i(LOG_TAG, "TEST: ArticleAdapter. Check date value:  " + date);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        TextView dateTextView = listItemView.findViewById(R.id.date_text_view);
        dateTextView.setText(date);

        TextView timeTextView = listItemView.findViewById(R.id.time_text_view);
        timeTextView.setText(time);

        return listItemView;
    }
}

