package edu.rosehulman.vacationtracker;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.graphics.ColorUtils;
import android.util.Log;
import android.util.Xml;

import com.google.android.gms.maps.model.LatLng;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Matt Boutell on 2/3/2016.
 * Rose-Hulman Institute of Technology.
 * Covered by MIT license.
 */
public class Utils {


    public static List<StateBoundary> getStateBoundaries(Context context) {
        List<StateBoundary> stateBoundaries = new ArrayList<>();

        StatesXmlParser statesXmlParser = new StatesXmlParser();

        InputStream is = context.getResources().openRawResource(R.raw.states);
        try {
            stateBoundaries = statesXmlParser.parse(is);
        } catch (XmlPullParserException e) {
            Log.e(Constants.TAG, e.toString());
        } catch (IOException e) {
            Log.e(Constants.TAG, e.toString());
            e.printStackTrace();
        }
        return stateBoundaries;
    }

    public static class StatesXmlParser {
        private static final String ns = null;

        public List<StateBoundary> parse(InputStream in) throws XmlPullParserException, IOException {
            try {
                XmlPullParser parser = Xml.newPullParser();
                parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
                parser.setInput(in, null);
                parser.nextTag();
                return readXml(parser);
            } finally {
                in.close();
            }
        }

        private List<StateBoundary> readXml(XmlPullParser parser) throws XmlPullParserException, IOException {
            List<StateBoundary> entries = new ArrayList<>();

            Log.d(Constants.TAG, "Start of readXML");

            while (parser.next() != XmlPullParser.END_TAG) {
                if (parser.getEventType() != XmlPullParser.START_TAG) {
                    continue;
                }
                String name = parser.getName();
                //Log.d(Constants.TAG, "Name: " + name);

                // Starts by looking for the entry tag
                if (name.equals("state")) {
                    entries.add(readState(parser));
                } else {
                    skip(parser);
                }
            }
            return entries;
        }

        // Parses the contents of an entry. If it encounters a title, summary, or link tag, hands them off
// to their respective "read" methods for processing. Otherwise, skips the tag.
        private StateBoundary readState(XmlPullParser parser) throws XmlPullParserException, IOException {
            parser.require(XmlPullParser.START_TAG, ns, "state");
            String stateName = parser.getAttributeValue(null, "name");
            String colorString = parser.getAttributeValue(null, "colour");
            Log.d(Constants.TAG, "State: " + stateName + " color: " + colorString);
            int color = Color.parseColor(colorString);
            //Color.colorToHSV();
            // Set 25% opaque
            color = ColorUtils.setAlphaComponent(color, 63);
            StateBoundary stateBoundary = new StateBoundary(stateName, color);

            while (parser.next() != XmlPullParser.END_TAG) {
                if (parser.getEventType() != XmlPullParser.START_TAG) {
                    continue;
                }
                String name = parser.getName();
                if (name.equals("point")) {
                    String latString = parser.getAttributeValue(null, "lat");
                    parser.nextTag(); // Needed?
                    String lngString = parser.getAttributeValue(null, "lng");
                    //Log.d(Constants.TAG, "Lat/lng: " + latString + " " + lngString);
                    double lat;
                    try {
                        lat = Double.parseDouble(latString);
                    } catch (Exception e) {
                        lat = 0.0;
                    }
                    double lng;
                    try {
                        lng = Double.parseDouble(lngString);
                    } catch (Exception e) {
                        lng = 0.0;
                    }
                    stateBoundary.addLatLng(new LatLng(lat, lng));
                } else {
                    skip(parser);
                }
            }
            return stateBoundary;
        }

        // For the tags title and summary, extracts their text values.
        private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
            String result = "";
            if (parser.next() == XmlPullParser.TEXT) {
                result = parser.getText();
                parser.nextTag();
            }
            return result;
        }

        private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                throw new IllegalStateException();
            }
            int depth = 1;
            while (depth != 0) {
                switch (parser.next()) {
                    case XmlPullParser.END_TAG:
                        depth--;
                        break;
                    case XmlPullParser.START_TAG:
                        depth++;
                        break;
                }
            }
        }
    }
}
