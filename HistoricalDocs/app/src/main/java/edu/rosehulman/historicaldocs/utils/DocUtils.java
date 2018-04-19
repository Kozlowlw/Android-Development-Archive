package edu.rosehulman.historicaldocs.utils;

import android.content.Context;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import edu.rosehulman.historicaldocs.Doc;
import edu.rosehulman.historicaldocs.R;

/**
 * Created by boutell on 12/1/2015.
 */
public class DocUtils {

    public static ArrayList<Doc> loadDocs(Context context) {
        Map<String, Integer> docResources = new TreeMap<>();
        String[] titles = new String[]{
                "Ten Commandments",
                "The Suffering Servant",
                "Sermon on the Mount",
                "Magna Carta",
                "Mayflower Compact",
                "Give Me Liberty",
                "Declaration of Independence",
                "US Constitution",
                "I Have a Dream"};
        Integer[] resources = new Integer[]{
                R.raw.ten_commandments,
                R.raw.the_suffering_servant,
                R.raw.sermon_on_the_mount,
                R.raw.magna_carta,
                R.raw.mayflower_compact,
                R.raw.give_me_liberty,
                R.raw.declaration_of_independence,
                R.raw.constitution,
                R.raw.i_have_a_dream
        };
        ArrayList<Doc> docs = new ArrayList<>();
        for (int i = 0; i < titles.length; i++) {
            InputStream is = context.getResources().openRawResource(resources[i]);
            String s = null;
            try {
                s = IOUtils.toString(is);
            } catch (IOException e) {
                e.printStackTrace();
            }
            IOUtils.closeQuietly(is); // don't forget to close your streams
            Doc doc = new Doc(titles[i], s);
            docs.add(doc);
        }
        return docs;
    }


}
