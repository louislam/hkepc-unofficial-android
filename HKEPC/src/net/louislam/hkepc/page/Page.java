package net.louislam.hkepc.page;

import android.content.Context;
import net.louislam.hkepc.MainActivity;

import net.louislam.hkepc.db.DatabaseHelper;
import org.jsoup.nodes.Document;

public abstract class Page {
    DatabaseHelper dbHelper;
	protected MainActivity mainActivity;
    final String TAG = getId();
	
	public abstract String getId();
	public abstract String getContent(Document doc);
	
	public void setMainActivity(MainActivity a) {
		this.mainActivity = a;
	}



    protected DatabaseHelper initDbHelper(){
        return getDbInstance();
    }

    public DatabaseHelper getDbInstance(){
        if(dbHelper == null){
            dbHelper = new DatabaseHelper(mainActivity);
        }

        return dbHelper;
    }
}
