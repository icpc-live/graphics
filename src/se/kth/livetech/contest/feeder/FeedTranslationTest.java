package se.kth.livetech.contest.feeder;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

public class FeedTranslationTest {
	public void test(FeedTranslator trans, InputSource in) throws SAXException, IOException {
		XMLReader r = XMLReaderFactory.createXMLReader();
		XMLWriterFilter out = new XMLWriterFilter(new PrintWriter(System.out));
		trans.setContentHandler(out);
		out.setParent(trans);
		r.setContentHandler(trans);
		trans.setParent(r);
		r.parse(in);
	}

	public static void main(String[] args) throws IOException, SAXException {
		String file = args.length > 0 ? args[0] : "rehersal_vm2011.txt";
		int yearIn = args.length > 1 ? Integer.parseInt(args[1]) : 2011;
		int yearOut = args.length > 2 ? Integer.parseInt(args[2]) : 2012;
		FeedTranslation.FeedFormat formatIn = FeedTranslation.format(yearIn);
		FeedTranslation.FeedFormat formatOut = FeedTranslation.format(yearOut);
		
		FeedTranslator t = new FeedTranslator(formatIn, formatOut);
		
		Reader fin = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
		InputSource in = new InputSource(fin);
		
		FeedTranslationTest test = new FeedTranslationTest();
		test.test(t, in);
	}
}
