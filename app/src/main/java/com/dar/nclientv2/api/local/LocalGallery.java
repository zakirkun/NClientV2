package com.dar.nclientv2.api.local;

import android.graphics.BitmapFactory;
import android.os.Parcel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.dar.nclientv2.api.components.Comment;
import com.dar.nclientv2.api.components.GenericGallery;
import com.dar.nclientv2.components.classes.Size;
import com.dar.nclientv2.utility.LogUtility;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

public class LocalGallery extends GenericGallery{
    private final int id,min,max;
    private final String title;
    private final File directory;
    private final boolean valid;
    private static final Pattern FILE_PATTERN=Pattern.compile("^(\\d{3,9})\\.(gif|png|jpg|jpeg)$",Pattern.CASE_INSENSITIVE);
    private Size maxSize=new Size(0,0),minSize=new Size(Integer.MAX_VALUE,Integer.MAX_VALUE);
    private File[]retriveValidFiles(){
        return directory.listFiles((dir, name) -> FILE_PATTERN.matcher(name).matches());
    }
    private static int getPageFromFile(File f){
        String n=f.getName();
        return Integer.parseInt(n.substring(0,n.indexOf('.')));
    }
    public LocalGallery(File file, int id){
        directory=file;
        title=file.getName();
        this.id=id;
        int max=0,min=Integer.MAX_VALUE;
        //Inizio ricerca pagine
        File[] files=retriveValidFiles();
        //Find page with max number
        if(files!=null&&files.length >= 1) {
            Arrays.sort(files, (o1, o2) -> getPageFromFile(o1)-getPageFromFile(o2));
            min=getPageFromFile(files[0]);
            max=getPageFromFile(files[files.length-1]);
        }
        this.max=max;
        this.min=min;
        valid=max!=0&&min!=Integer.MAX_VALUE&&id>0;
    }
    public void calculateSizes(){
        File[] files=retriveValidFiles();
        if(files!=null)
            for(File f:files)
                checkSize(f);
    }
    private void checkSize(File f){
        LogUtility.d("Decoding: "+f);
        BitmapFactory.Options options=new BitmapFactory.Options();
        options.inJustDecodeBounds=true;
        BitmapFactory.decodeFile(f.getAbsolutePath(),options);
        if(options.outWidth>maxSize.getWidth())maxSize.setWidth(options.outWidth);
        if(options.outWidth<minSize.getWidth())minSize.setWidth(options.outWidth);
        if(options.outHeight>maxSize.getHeight())maxSize.setHeight(options.outHeight);
        if(options.outHeight<minSize.getHeight())minSize.setHeight(options.outHeight);
    }
    @Override
    public List<Comment> getComments() {
        return null;
    }
    @Override
    public Size getMaxSize() {
        return maxSize;
    }

    @Override
    public Size getMinSize() {
        return minSize;
    }

    private LocalGallery(Parcel in) {
        maxSize=in.readParcelable(Size.class.getClassLoader());
        minSize=in.readParcelable(Size.class.getClassLoader());
        id = in.readInt();
        min = in.readInt();
        max = in.readInt();
        title = in.readString();
        directory=new File(in.readString());
        valid=true;
    }

    public static final Creator<LocalGallery> CREATOR = new Creator<LocalGallery>() {
        @Override
        public LocalGallery createFromParcel(Parcel in) {
            return new LocalGallery(in);
        }

        @Override
        public LocalGallery[] newArray(int size) {
            return new LocalGallery[size];
        }
    };

    @Override
    public String getThumbnail() {
        return null;
    }

    @Override
    public Type getType() {
        return Type.LOCAL;
    }

    @Override
    public boolean isValid() {
        return valid;
    }
    @Override
    public int getId() {
        return id;
    }
    @Override
    public int getPageCount() {
        return max;
    }
    @Override
    public String getTitle() {
        return title;
    }

    public int getMin() {
        return min;
    }

    public int getMax() {
        return max;
    }

    public File getDirectory() {
        return directory;
    }
    public static File getPage(File dir,int page){
        if(dir==null||!dir.exists())return null;
        String pag=String.format(Locale.US,"%03d.",page);
        File x;
        x=new File(dir,pag+"jpg");
        if(x.exists())return x;
        x=new File(dir,pag+"png");
        if(x.exists())return x;
        x=new File(dir,pag+"gif");
        if(x.exists())return x;
        x=new File(dir,pag+"jpeg");
        if(x.exists())return x;
        return null;
    }
    @Nullable
    public File getPage(int index){
        return getPage(directory,index);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(maxSize, flags);
        dest.writeParcelable(minSize, flags);
        dest.writeInt(id);
        dest.writeInt(min);
        dest.writeInt(max);
        dest.writeString(title);
        dest.writeString(directory.getAbsolutePath());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LocalGallery gallery = (LocalGallery) o;

        if (id != gallery.id) return false;
        if (!title.equals(gallery.title)) return false;
        return directory.equals(gallery.directory);
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + title.hashCode();
        result = 31 * result + directory.hashCode();
        return result;
    }

    @NonNull
    @Override
    public String toString() {
        return "LocalGallery{" +
                "id=" + id +
                ", min=" + min +
                ", max=" + max +
                ", title='" + title + '\'' +
                ", directory=" + directory +
                ", valid=" + valid +
                '}';
    }
}
