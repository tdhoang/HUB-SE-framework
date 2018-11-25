/**
 * 
 */
package se.de.hu_berlin.informatik.utils.compression.ziputils;

import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.nio.file.Path;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.io.ZipInputStream;
import net.lingala.zip4j.model.FileHeader;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;
import se.de.hu_berlin.informatik.utils.miscellaneous.Log;
import se.de.hu_berlin.informatik.utils.miscellaneous.Pair;
import se.de.hu_berlin.informatik.utils.processors.AbstractProcessor;

/**
 * Adds byte arrays to a zip file.
 * 
 * @author Simon Heiden
 */
public class MoveNamedByteArraysBetweenZipFilesProcessor extends AbstractProcessor<Pair<String, String>, Boolean> {

	private ZipFile zipFileSource;
	private ZipFile zipFileTarget;
	private ZipParameters parameters;
	
	public MoveNamedByteArraysBetweenZipFilesProcessor(Path zipFilePathSoure, Path zipFilePathTarget) {
		//if this module needs an input item
		super();
		
		if (!zipFilePathSoure.toFile().exists()) {
			Log.abort(this, "File '%s' does not exist.", zipFilePathSoure);
		}
		if (!zipFilePathTarget.toFile().exists()) {
			Log.abort(this, "File '%s' does not exist.", zipFilePathTarget);
		}
		
		try {
			zipFileSource = new ZipFile(zipFilePathSoure.toString());
		} catch (ZipException e) {
			Log.abort(this, e, "Could not initialize zip file '%s'.", zipFileSource);
		}
		try {
			zipFileTarget = new ZipFile(zipFilePathTarget.toString());
		} catch (ZipException e) {
			Log.abort(this, e, "Could not initialize zip file '%s'.", zipFileTarget);
		}
		
		parameters = new ZipParameters();
		parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
		parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_ULTRA);
		
		// we set this flag to true. If this flag is true, Zip4j identifies that
		// the data will not be from a file but directly from a stream
		parameters.setSourceExternalStream(true);
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.utils.tm.ITransmitter#processItem(java.lang.Object)
	 */
	@Override
	public Boolean processItem(Pair<String, String> sourceAndTargetFileNames) {
		FileHeader fileHeader = null;
		try {
			fileHeader = zipFileSource.getFileHeader(sourceAndTargetFileNames.first());
		} catch (ZipException e) {
			Log.abort(this, e, "File '%s' does not exist.", sourceAndTargetFileNames.first());
		}
		if (fileHeader == null) {
			return false;
		}
		try {
			// this sets the name of the file for this entry in the zip file
			parameters.setFileNameInZip(sourceAndTargetFileNames.second());
			
			PipedOutputStream out = null;
			Thread thread = null;
			try {
				PipedInputStream in = new PipedInputStream();
				out = new PipedOutputStream(in);

				thread = startZipFileListener(sourceAndTargetFileNames.second(), in);
				thread.start();

				ZipInputStream sourceStream = zipFileSource.getInputStream(fileHeader);

				byte[] buffer = new byte[4096];
				int bytesRead;
				while ((bytesRead = sourceStream.read(buffer)) != -1) {
					out.write(buffer, 0, bytesRead);
				}
			} catch (ZipException e) {
				Log.abort(this, e, "File '%s' does not exist or failed to create stream.", sourceAndTargetFileNames.first());
			} finally {
				if (out != null) {
					out.flush();
					out.close();
				}
				if (thread != null) {
					while (thread.isAlive()) {
						try {
							thread.join();
						} catch (InterruptedException e) {
						}
					}
				}
			}
		} catch (IOException e) {
			Log.abort(this, e, "Could not copy files.");
		}
		return true;
	}

	private Thread startZipFileListener(String fileName, PipedInputStream in) {
		return new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					// this sets the name of the file for this entry in the zip file, starting from '0.bin'
					parameters.setFileNameInZip(fileName);

					InputStream is = in;

					// Creates a new entry in the zip file and adds the content to the zip file
					zipFileTarget.addStream(is, parameters);
				} catch (ZipException e) {
					Log.abort(this, e, "Zip file '%s' does not exist.", zipFileTarget.getFile());
				}
			}
		});
	}
}
