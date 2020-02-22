package orlandohutapea.projectkeikaku;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CharacterXmlParser {
    public HashMap<String, Character> parse(InputStream in) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            return parseXml(parser);
        } finally {
            in.close();
        }
    }

    private HashMap<String, Character> parseXml(XmlPullParser parser) throws XmlPullParserException, IOException {
        HashMap<String, Character> chars = new HashMap<>();

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG)
                continue;

            String name = parser.getName();

            if (name.equals("character")) {
                Character character = readCharacter(parser);
                chars.put(character.getLiteral(), character);
            } else
                skip(parser);
        }

        return chars;
    }

    private Character readCharacter(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, null, "character");

        String literal = null;
        List<Character.Reading> reading = new ArrayList<>();
        List<String> meaning = new ArrayList<>();

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG)
                continue;

            switch (parser.getName()) {
                case "literal":
                    parser.require(XmlPullParser.START_TAG, null, "literal");
                    literal = readText(parser);
                    parser.require(XmlPullParser.END_TAG, null, "literal");
                    break;
                case "reading":
                    String type = parser.getAttributeValue(null, "type");
                    String isCommonAttr = parser.getAttributeValue(null, "is-common");
                    boolean isCommon = isCommonAttr != null && isCommonAttr.equals("true");

                    parser.require(XmlPullParser.START_TAG, null, "reading");
                    reading.add(new Character.Reading(type, readText(parser), isCommon));
                    parser.require(XmlPullParser.END_TAG, null, "reading");
                    break;
                case "meaning":
                    parser.require(XmlPullParser.START_TAG, null, "meaning");
                    meaning.add(readText(parser));
                    parser.require(XmlPullParser.END_TAG, null, "meaning");
                    break;
                default:
                    skip(parser);
            }
        }

        return new Character(literal,
                reading.toArray(new Character.Reading[reading.size()]),
                meaning.toArray(new String[meaning.size()]));
    }

    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";

        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }

        return result;
    }

    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG)
            throw new IllegalStateException();

        int depth = 1;

        while (depth != 0)
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
