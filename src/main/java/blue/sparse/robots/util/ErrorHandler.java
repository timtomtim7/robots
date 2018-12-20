package blue.sparse.robots.util;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.Logger;

public final class ErrorHandler {

	private static final SimpleDateFormat DATE_FORMAT
			= new SimpleDateFormat("yyyy-mm-dd hh-mm");

	private final File errorFolder;
	private final Logger logger;

	public ErrorHandler(File errorFolder, Logger logger) {
		this.errorFolder = errorFolder;
		this.logger = logger;
	}

	private File getNewErrorFile() {
		String date = DATE_FORMAT.format(Calendar.getInstance().getTime());
		errorFolder.mkdirs();
		return new File(errorFolder, date + ".log");
	}

	public void logError(Throwable throwable) {
		final File file = getNewErrorFile();
		try {
			final PrintWriter writer = new PrintWriter(
					new BufferedWriter(
							new FileWriter(file, true)
					)
			);
			throwable.printStackTrace(writer);
			writer.close();
		} catch (IOException ignored) { }
		logger.warning("Error logged to " + file.getName());
	}

}
