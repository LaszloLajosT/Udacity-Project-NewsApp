package android.example.newsapp;

import android.graphics.Bitmap;

public class Article {

    String sectionName;
    String webPublicationDate;
    String trailText;
    String webUrl;
    String headLine;
    String author;
    Bitmap mainPicture;

    public Article(final String sectionName, final String webPublicationDate, final String headLine, final String trailText, final String webUrl, final String author, final Bitmap mainPicture) {
        this.sectionName = sectionName;
        this.webPublicationDate = webPublicationDate;
        this.headLine = headLine;
        this.trailText = trailText;
        this.webUrl = webUrl;
        this.author = author;
        this.mainPicture = mainPicture;
    }

    public Article(final String sectionName, final String webPublicationDate, final String headLine, final String webUrl, final String author, final Bitmap mainPicture) {
        this.sectionName = sectionName;
        this.webPublicationDate = webPublicationDate;
        this.headLine = headLine;
        this.webUrl = webUrl;
        this.author = author;
        this.mainPicture = mainPicture;
    }

    public Article() {
    }

    public String getSectionName() {
        return sectionName;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }

    public String getWebPublicationDate() {
        return webPublicationDate;
    }

    public void setWebPublicationDate(String webPublicationDate) {
        this.webPublicationDate = webPublicationDate;
    }

    public String getTrailText() {
        return trailText;
    }

    public void setTrailText(String trailText) {
        this.trailText = trailText;
    }

    public String getWebUrl() {
        return webUrl;
    }

    public void setWebUrl(String webUrl) {
        this.webUrl = webUrl;
    }

    public String getHeadLine() {
        return headLine;
    }

    public void setHeadLine(String headLine) {
        this.headLine = headLine;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Bitmap getMainPicture() {
        return mainPicture;
    }

    public void setMainPicture(Bitmap mainPicture) {
        this.mainPicture = mainPicture;
    }

    @Override
    public String toString() {
        return "Article{" +
                "sectionName='" + sectionName + '\'' +
                ", webPublicationDate='" + webPublicationDate + '\'' +
                ", trailText='" + trailText + '\'' +
                ", webUrl='" + webUrl + '\'' +
                ", headLine='" + headLine + '\'' +
                ", author='" + author + '\'' +
                ", mainPicture='" + mainPicture + '\'' +
                '}';
    }
}