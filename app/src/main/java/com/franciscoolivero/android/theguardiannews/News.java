package com.franciscoolivero.android.theguardiannews;

/**
 * Created by franciscoolivero on 12/30/18.
 * This is my code for Udacity.
 */


/**
 * An {@link News} object contains all data related to a news article.
 */

public class News {
    private String sectionName;
    private String articleTitle;
    private String articleWebUrl;

    //May not exist in JSON response. Handle with hasAuthor / hasPublicationDate methods.
    private String publicationDate;
    private String authorName;

    public News(String sectionName, String articleTitle, String articleWebUrl, String publicationDate, String authorName) {
        this.sectionName = sectionName;
        this.articleTitle = articleTitle;
        this.articleWebUrl = articleWebUrl;
        this.publicationDate = publicationDate;
        this.authorName = authorName;
    }

    public String getSectionName() {
        return sectionName;
    }

    public String getArticleTitle() {
        return articleTitle;
    }

    public String getArticleWebUrl() {
        return articleWebUrl;
    }

    public String getPublicationDate() {
        return publicationDate;
    }

    public String getAuthorName() {
        return authorName;
    }

    public boolean hasAuthor(){
        if(authorName==null){
            return false;
        }
        return true;
    }

    public boolean hasPublicationDate(){
        if(publicationDate==null){
            return false;
        }
        return true;
    }
}
