package se.kth.livetech.communication;

import java.util.List;

import uk.co.flamingpenguin.jewel.cli.ArgumentValidationException;
import uk.co.flamingpenguin.jewel.cli.CliFactory;
import uk.co.flamingpenguin.jewel.cli.Option;
import uk.co.flamingpenguin.jewel.cli.Unparsed;

public class LiveClient {
	// Any class constructable from String may be used
	// pattern="regexp"
	// defaultValue="text"
	// description="text"
	// @see http://jewelcli.sourceforge.net/apidocs/uk/co/flamingpenguin/jewel/cli/Option.html
	public interface Options {
		@Option(shortName="s",
				longName="spider")
		String getSpider();
		boolean isSpider();
		
		@Option(helpRequest=true)
		boolean getHelp();
		
		@Unparsed
		List<String> getArgs();
		boolean isArgs();
	}
	
	public static void main(String[] args) {
		try {
			Options opts = CliFactory.parseArguments(Options.class, args);
		} catch (ArgumentValidationException e) {
			System.err.println(e.getMessage());
			System.exit(1);
		}
	}
}
