package com.franciscoolivero.android.theguardiannews;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by franciscoolivero on 8/19/18.
 */

public class NewsAdapter extends ArrayAdapter<News> {


    public NewsAdapter(Activity context, List<News> newsList) {
        super(context, 0, newsList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }

        News currentNews = getItem(position);
        ViewHolder holder = new ViewHolder(listItemView);

        // Format the magnitude to show 1 decimal place
        // Display the magnitude of the current news in that TextView
        holder.section.setText(currentNews.getSectionName());

        if(currentNews.hasPublicationDate()){
            //Format from UTC date to Strings with Date
            //Display the date of the current news to the TextView.
            holder.date.setText(formatDate(currentNews));
            holder.containerDate.setVisibility(View.VISIBLE);
        } else {
            holder.containerDate.setVisibility(View.GONE);
        }

        //Sets the Article Title to it's TextView
        holder.title.setText(currentNews.getArticleTitle());

        if(currentNews.hasAuthor()){
            //Sets the author/contributor text to it's TextView
            String authorName = getContext().getResources().getString((R.string.by_default_author_text))+currentNews.getAuthorName();
            holder.author.setText(authorName);
            holder.author.setVisibility(View.VISIBLE);
        } else{
            holder.author.setVisibility(View.GONE);
        }

        return listItemView;
    }
//yyyy-MM-dd-'T'HH:mm:ss'Z'

    private String formatDate(News currentNews) {
        String[] splitPublicationDate = currentNews.getPublicationDate().split("T");
        String currentNewsDateWithoutTime = splitPublicationDate[0];
        String[] dividedDate = currentNewsDateWithoutTime.split("-");
        int year = Integer.parseInt(dividedDate[0]);
        int month = Integer.parseInt(dividedDate[1])-1;
        int date = Integer.parseInt(dividedDate[2]);
        GregorianCalendar gregorianCalendarObject = new GregorianCalendar(year, month, date);
        Date dateObject = gregorianCalendarObject.getTime();
        return DateFormat.getDateInstance(DateFormat.MEDIUM).format(dateObject);
    }


    static class ViewHolder {
        @BindView(R.id.text_section)
        TextView section;
        @BindView(R.id.container_date)
        LinearLayout containerDate;
        @BindView(R.id.text_date)
        TextView date;
        @BindView(R.id.text_news_title)
        TextView title;
        @BindView(R.id.text_author)
        TextView author;

        private ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
