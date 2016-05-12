/**
 * 
 */
package se.de.hu_berlin.informatik.utils.miscellaneous;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Provides unique, automatically generated output paths.
 * 
 * @author Simon Heiden
 */
public class OutputPathGenerator implements IOutputPathGenerator<Path> {

	/**
	 * Path to main output directory.
	 */
	private final Path outputdir;
	
	/**
	 * Internal file counter.
	 */
	private int fileCounter = 0;
	
	/**
	 * Internal sub folder count. 
	 */
	private int subFolderCount = 0;
	
	/**
	 * Holds the current subFolder name.
	 */
	private String subFolder = null;
	
	/**
	 * Should files be overwritten? 
	 */
	private boolean overwrite = false;
	
	/**
	 * Creates an {@link OutputPathGenerator} object with the given parameters. 
	 * Will fail if the output directory already exists.
	 * @param outputdir
	 * holds the path to the output directory
	 */
	public OutputPathGenerator(Path outputdir) {
		this.outputdir = outputdir;
		if (!outputdir.toFile().mkdirs()) {
			Misc.abort(this, "Could not create directory \"%s\"!", outputdir.toString());			
		}
	}
	
	/**
	 * Creates an {@link OutputPathGenerator} object with the given parameters. 
	 * Will fail if the output directory already exists and should not be overwritten.
	 * @param outputdir
	 * holds the path to the output directory
	 * @param overwrite
	 * if existing directories or files shall be overwritten
	 */
	public OutputPathGenerator(Path outputdir, boolean overwrite) {
		this.outputdir = outputdir;
		this.overwrite = overwrite;
		if (outputdir.toFile().exists()) {
			if (!overwrite) {
				Misc.abort(this, "Directory \"%s\" already exists!", outputdir.toString());
			}
		} else if (!outputdir.toFile().mkdirs()) {
			Misc.abort(this, "Could not create directory \"%s\"!", outputdir.toString());
		}
	}

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.utils.miscellaneous.IOutputPathGenerator#getNewOutputPath(java.lang.String)
	 */
	public Path getNewOutputPath(String extension) {		
		return getNewOutputPath(null, extension);
	}
	

	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.utils.miscellaneous.IOutputPathGenerator#getNewOutputPath(java.nio.file.Path, java.lang.String)
	 */
	@Override
	public Path getNewOutputPath(Path originalPath, String extension) {
		String prefix = "";
		if (originalPath != null) {
			prefix = originalPath.getFileName().toString() + "_";
		}
		if (fileCounter % 1000 == 0) {			
			++subFolderCount;
			subFolder = outputdir.toString()  + File.separator + String.valueOf(subFolderCount);
			if (Paths.get(subFolder).toFile().exists()) {
				if (!overwrite) {
					Misc.abort(this, "Directory \"%s\" already exists!", subFolder);
				}
			}else if (!Paths.get(subFolder).toFile().mkdirs()) {
				Misc.abort(this, "Could not create directory \"%s\"!", subFolder);
			}			
			fileCounter = 0;
		}
		return Paths.get(subFolder,  prefix + String.valueOf(++fileCounter) + extension);
	}

}
