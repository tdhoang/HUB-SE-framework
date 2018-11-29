/**
 * 
 */
package se.de.hu_berlin.informatik.utils.compression.ziputils;

import java.nio.file.Path;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import se.de.hu_berlin.informatik.utils.miscellaneous.Log;
import se.de.hu_berlin.informatik.utils.processors.AbstractProcessor;

/**
 * Adds byte arrays to a zip file.
 * 
 * @author Simon Heiden
 */
public class ZipFileReader extends AbstractProcessor<Path,ZipFileWrapper> {
	
	public ZipFileReader() {
		//if this module needs an input item
		super();
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.utils.tm.ITransmitter#processItem(java.lang.Object)
	 */
	@Override
	public ZipFileWrapper processItem(Path zipFilePath) {
		ZipFile zipFile = null;
		try {
			zipFile = new ZipFile(zipFilePath.toString());
			if (!zipFile.isValidZipFile()) {
				Log.abort(this, "File '%s' is no valid zip file.", zipFilePath);
			}
		} catch (ZipException e) {
			Log.abort(this, "Could not initialize zip file '%s' for reading.", zipFilePath);
		}
		
		return new ZipFileWrapper(zipFilePath);
	}

}
