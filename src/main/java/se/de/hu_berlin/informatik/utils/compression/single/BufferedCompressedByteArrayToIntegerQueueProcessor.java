/**
 * 
 */
package se.de.hu_berlin.informatik.utils.compression.single;

import java.io.IOException;
import java.io.InputStream;
import java.util.function.Consumer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import se.de.hu_berlin.informatik.utils.compression.ziputils.ZipFileWrapper;
import se.de.hu_berlin.informatik.utils.miscellaneous.Log;
import se.de.hu_berlin.informatik.utils.processors.AbstractConsumingProcessor;

/**
 * Decodes...
 * 
 * @author Simon Heiden
 */
public class BufferedCompressedByteArrayToIntegerQueueProcessor extends AbstractConsumingProcessor<String> {
	
	// same buffer that is used in zip utils
	private static final int BUFER_SIZE = 4096;
	private byte[] buffer = new byte[BUFER_SIZE];
		
	public static final int DELIMITER = 0;
	
	private byte usedBits;
	private int arrayPos;

	private boolean containsZero;
	private ZipFileWrapper zipFileWrapper;
	private Consumer<Integer> consumer;
	
	public BufferedCompressedByteArrayToIntegerQueueProcessor(ZipFileWrapper zipFileWrapper, 
			boolean containsZero, Consumer<Integer> consumer) {
		super();
		this.containsZero = containsZero;
		this.zipFileWrapper = zipFileWrapper;
		this.consumer = consumer;
	}
	
	/* (non-Javadoc)
	 * @see se.de.hu_berlin.informatik.utils.tm.ITransmitter#processItem(java.lang.Object)
	 */
	@Override
	public void consumeItem(String fileName) {

		InputStream inputStream = null;
		try (ZipFile zipFile = new ZipFile(zipFileWrapper.getzipFilePath().toFile())) {
			ZipEntry entry = zipFile.getEntry(fileName);
			if (entry == null) {
				Log.abort(this, "No entry '%s' in zip file!", fileName);
			}
			inputStream = zipFile.getInputStream(entry);

			int len = getNextBytesFromInputStream(inputStream);
			readHeader(len);

			boolean atTotalEnd = false;

			byte currentByte = 0;
			int currentInt = 0;
			byte remainingBits = 0;
			byte bitsLeft = 0;

			//get all the encoded integers
			while (arrayPos < len) {
				//for each number, the number of bits to get is equal
				bitsLeft = usedBits;
				//if no bits remain to get from the current byte, then get the next one from the array
				if (remainingBits == 0) {
					currentByte = buffer[arrayPos];
					remainingBits = 8;
				}

				//as long as bits are still needed, get them from the array
				while (bitsLeft > 0) {
					if (bitsLeft > remainingBits) {
						currentInt = (currentInt << remainingBits) | (currentByte & 0xFF ) >>> (8 - remainingBits);
						bitsLeft -= remainingBits;
						//					remainingBits = 0;
						++arrayPos;
						if (arrayPos >= len) {
							len = getNextBytesFromInputStream(inputStream);
							arrayPos = 0;
						}
						currentByte = buffer[arrayPos];
						remainingBits = 8;
					} else { //bitsLeft <= remainingBits
						currentInt = (currentInt << bitsLeft) | (currentByte & 0xFF ) >>> (8 - bitsLeft);
						currentByte = (byte) (currentByte << bitsLeft);
						remainingBits -= bitsLeft;
						bitsLeft = 0;
					}
				}

				if (currentInt == DELIMITER) {
					atTotalEnd = true;
					//				System.out.println();
					break;
				} else {
					//				System.out.print((currentInt-1) + ",");
					//add the next integer to the current sequence
					consumer.accept(containsZero ? currentInt-1 : currentInt);
				}
				//reset the current integer to all zeroes
				currentInt = 0;

				//if no bits remain in the current byte, then update the array position for the next step
				if (remainingBits == 0) {
					++arrayPos;
				}
				if (arrayPos >= len) {
					len = getNextBytesFromInputStream(inputStream);
					arrayPos = 0;
				}
			}

			if (!atTotalEnd) {
				Log.abort(this, "No total end marker was read!");
			}

		} catch (IOException e) {
			Log.abort(this, e, "Could not get input stream from file %s.", fileName);
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					// ignore
				}
			}
		}
		
	}

	private void readHeader(int len) {
		// header should be 1 byte:
		// | number of bits used for one element (1 byte) |
		if (len < 1) {
			Log.abort(this, "Could not read header from input stream.");
		}
		usedBits = buffer[0];
		
		arrayPos = 1;
	}
	
	// returns the length of available bytes
	private int getNextBytesFromInputStream(InputStream is) {
		try {
			return is.read(buffer);
		} catch (IOException e) {
			Log.abort(this, e, "Could not read bytes from stream.");
			return -1;
		}
	}
	
}
