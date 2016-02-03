package org.cohorte.utilities.crypto;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.File;

import javax.imageio.ImageIO;

import org.psem2m.utilities.logging.CActivityLoggerBasicConsole;
import org.psem2m.utilities.logging.CActivityLoggerNull;
import org.psem2m.utilities.logging.IActivityLogger;

/**
 * Class Steganography => HAVE TO BE OBFUSCATE
 *
 * @author William_Wilson
 * @author ogattaz
 *
 * @see http://www.dreamincode.net/forums/topic/27950-steganography/
 *
 * @see http://www.yworks.com/products/yguard
 *
 */
public class CSteganography {

	private static final String IMAGE_FORMAT_DEFAULT = "png";
	private final String pImageFormat;
	private final IActivityLogger pLogger;

	/**
	 * @param aImageFormat
	 */
	public CSteganography() {
		this(IMAGE_FORMAT_DEFAULT, CActivityLoggerNull.getInstance());
	}

	/**
	 * @param aImageFormat
	 */
	public CSteganography(final String aImageFormat) {
		this(aImageFormat, CActivityLoggerNull.getInstance());
	}

	/**
	 * @param aImageFormat
	 * @param aLogger
	 */
	public CSteganography(final String aImageFormat,
			final IActivityLogger aLogger) {
		super();
		pImageFormat = aImageFormat;
		pLogger = (aLogger != null) ? aLogger : CActivityLoggerBasicConsole
				.getInstance();
	}

	/**
	 * Handles the addition of text into an image
	 *
	 * @param aImage
	 *            The image to add hidden text to
	 * @param aText
	 *            The text to hide in the image
	 *
	 * @return Returns the image with the text embedded in it
	 */
	private BufferedImage add_text(final BufferedImage aImage,
			final String aText) {

		// convert all items to byte arrays: image, message, message length
		final byte img[] = get_byte_data(aImage);
		final byte msg[] = aText.getBytes();
		final byte len[] = bit_conversion(msg.length);
		try {
			// 0 first positiong
			encode_text(img, len, 0);
			// 4 bytes of space for length: 4bytes*8bit = 32 bits
			encode_text(img, msg, 32);

		} catch (final Exception e) {
			getLogger().logSevere(this, "add_text",
					"Target File cannot hold message! %s", e);
		}
		return aImage;
	}

	/**
	 * Gernerates proper byte format of an integer
	 *
	 * @param i
	 *            The integer to convert
	 *
	 * @return Returns a byte[4] array converting the supplied integer into
	 *         bytes
	 */
	private byte[] bit_conversion(int i) {
		// originally integers (ints) cast into bytes
		// byte byte7 = (byte)((i & 0xFF00000000000000L) >>> 56);
		// byte byte6 = (byte)((i & 0x00FF000000000000L) >>> 48);
		// byte byte5 = (byte)((i & 0x0000FF0000000000L) >>> 40);
		// byte byte4 = (byte)((i & 0x000000FF00000000L) >>> 32);

		// only using 4 bytes
		final byte byte3 = (byte) ((i & 0xFF000000) >>> 24); // 0
		final byte byte2 = (byte) ((i & 0x00FF0000) >>> 16); // 0
		final byte byte1 = (byte) ((i & 0x0000FF00) >>> 8); // 0
		final byte byte0 = (byte) ((i & 0x000000FF));
		// {0,0,0,byte0} is equivalent, since all shifts >=8 will be 0
		return (new byte[] { byte3, byte2, byte1, byte0 });
	}

	/**
	 * @param aStegFile
	 * @return
	 */
	public String decode(final File aStegFile) {

		byte[] decode;
		try {
			// user space is necessary for decrypting
			final BufferedImage image = user_space(getImage(aStegFile));
			decode = decode_text(get_byte_data(image));
			return (new String(decode));
		} catch (final Exception e) {
			getLogger().logSevere(this, "add_text",
					"There is no hidden message in this image! %s", e);
			return "";
		}
	}

	/**
	 * Decrypt assumes the image being used is of type .png, extracts the hidden
	 * text from an image
	 *
	 * @param path
	 *            The path (folder) containing the image to extract the message
	 *            from
	 *
	 * @param name
	 *            The name of the image to extract the message from
	 *
	 * @param type
	 *            integer representing either basic or advanced encoding
	 */
	public String decode(String path, String name) {
		return decode(path, name, getImageFormat());
	}

	/**
	 * @param path
	 * @param name
	 * @param aExtension
	 * @return
	 */
	public String decode(String path, String name, String aExtension) {
		return decode(new File(image_path(path, name, aExtension)));
	}

	/**
	 * Retrieves hidden text from an image
	 *
	 * @param image
	 *            Array of data, representing an image
	 *
	 * @return Array of data which contains the hidden text
	 */
	private byte[] decode_text(byte[] image) {

		int length = 0;
		int offset = 32;

		// loop through 32 bytes of data to determine text length
		// i=24 will also work, as only the 4th byte contains real data
		for (int i = 0; i < 32; ++i) {
			length = (length << 1) | (image[i] & 1);
		}

		final byte[] wBuffer = new byte[length];

		// loop through each byte of text
		for (int b = 0; b < wBuffer.length; ++b) {

			// loop through each bit within a byte of text
			for (int i = 0; i < 8; ++i, ++offset) {

				// assign bit: [(new byte value) << 1] OR [(text byte) AND 1]
				wBuffer[b] = (byte) ((wBuffer[b] << 1) | (image[offset] & 1));

			}
		}
		return wBuffer;
	}

	/**
	 * @param aImageFile
	 * @param aSteganoFile
	 * @param aMessage
	 *            The text to hide in the image
	 * @return a boolean true is all OK
	 */

	public boolean encode(final File aImageFile, final File aSteganoFile,
			final String aMessage) {

		final BufferedImage image_orig = getImage(aImageFile);

		// user space is not necessary for Encrypting
		BufferedImage image = user_space(image_orig);
		image = add_text(image, aMessage);

		return (setImage(image, aSteganoFile, getImageFormat()));
	}

	/**
	 * Encrypt an image with text, the output file and the source file
	 *
	 * @param aImageParentPath
	 *            The path (folder) containing the image source
	 * @param aImageName
	 *            The name of the image source
	 * @param aSteganoParentPath
	 *            The path (folder) containing the stegano to write
	 * @param aSteganoName
	 *            The output name of the file
	 * @param aMessage
	 *            The text to hide in the image
	 * @param type
	 *            integer representing either basic or advanced encoding
	 * @return a boolean true is all OK
	 */
	public boolean encode(String aImageParentPath, String aImageName,
			String aSteganoParentPath, String aSteganoName, String aMessage) {

		return encode(aImageParentPath, aImageName, getDefaultImageExtension(),
				aSteganoParentPath, aSteganoName, getDefaultImageExtension(),
				aMessage);
	}

	/**
	 * Encrypt an image with text, the output file will be of type .png
	 *
	 * @param aImageParentPath
	 *            The path (folder) containing the image source
	 *
	 * @param aImageName
	 *            The name of the image source
	 * @param aImageExtension
	 *            The extension type of the image source (jpg, png)
	 * @param aSteganoParentPath
	 *            The path (folder) containing the stegano to write
	 * @param aSteganoName
	 *            The output name of the file
	 * @param aSteganoExtension
	 *            The extension type of the stegano (jpg, png)
	 * @param aMessage
	 *            The text to hide in the image
	 * @param type
	 *            integer representing either basic or advanced encoding
	 * @return a boolean true is all OK
	 */
	public boolean encode(String aImageParentPath, String aImageName,
			String aImageExtension, String aSteganoParentPath,
			String aSteganoName, String aSteganoExtension, String aMessage) {

		return encode(
				new File(image_path(aImageParentPath, aImageName,
						aImageExtension)),
				new File(image_path(aSteganoParentPath, aSteganoName,
						aSteganoExtension)), aMessage);
	}

	/**
	 * Encode an array of bytes into another array of bytes at a supplied offset
	 *
	 * @param aImage
	 *            Array of data representing an image
	 * @param aBuffer
	 *            Array of data to add to the supplied image data array
	 * @param offset
	 *            The offset into the image array to add the addition data
	 *
	 * @return Returns data Array of merged image and addition data
	 */
	private byte[] encode_text(byte[] aImage, byte[] aBuffer, int offset) {

		// check that the data + offset will fit in the image
		if (aBuffer.length + offset > aImage.length) {
			throw new IllegalArgumentException("File not long enough!");
		}

		// loop through each addition byte
		for (int i = 0; i < aBuffer.length; ++i) {

			// loop through the 8 bits of each byte
			final int add = aBuffer[i];

			// ensure the new offset value carries on through both loops
			for (int bit = 7; bit >= 0; --bit, ++offset) {
				// assign an integer to b, shifted by bit spaces AND 1
				// a single bit of the current byte
				final int b = (add >>> bit) & 1;
				// assign the bit by taking: [(previous byte value) AND 0xfe] OR
				// bit to add
				// changes the last bit of the byte in the image to be the bit
				// of addition
				aImage[offset] = (byte) ((aImage[offset] & 0xFE) | b);
			}
		}
		return aImage;
	}

	/**
	 * Gets the byte array of an image
	 *
	 * @param image
	 *            The image to get byte data from
	 *
	 * @return Returns the byte array of the image supplied
	 *
	 * @see Raster
	 *
	 * @see WritableRaster
	 *
	 * @see DataBufferByte
	 */
	private byte[] get_byte_data(BufferedImage image) {

		final WritableRaster raster = image.getRaster();
		final DataBufferByte buffer = (DataBufferByte) raster.getDataBuffer();
		return buffer.getData();
	}

	/**
	 * @return
	 */
	protected String getDefaultImageExtension() {
		return getImageFormat();
	}

	/**
	 * @param aImageFile
	 *            The file of the image
	 * @return A BufferedImage of the supplied file path
	 */
	private BufferedImage getImage(File aImageFile) {

		BufferedImage image = null;

		try {
			image = ImageIO.read(aImageFile);
		} catch (final Exception e) {
			getLogger().logSevere(this, "getImage",
					"Image could not be read! %s", e);
		}
		return image;
	}

	/**
	 * @return
	 */
	protected String getImageFormat() {
		return pImageFormat;
	}

	/**
	 * @return the current IActivityLogger
	 */
	protected IActivityLogger getLogger() {
		return pLogger;
	}

	/**
	 * Returns the complete path of a file, in the form: path\name.ext
	 *
	 * @param aParentPath
	 *            The path (folder) of the file
	 * @param aImageName
	 *            The name of the file
	 * @param aImageExtension
	 *            The extension of the file
	 *
	 * @return A String representing the complete path of a file
	 */
	private String image_path(String aParentPath, String aImageName,
			String aImageExtension) {
		return new File(aParentPath, aImageName + "." + aImageExtension)
				.getAbsolutePath();
	}

	/**
	 * Set method to save an image file
	 *
	 * @param aBufferedImage
	 *            The image file to save
	 * @param aFileOut
	 *            File to save the image to
	 * @param aFormatName
	 *            The extension and thus format of the file to be saved (eg.
	 *            png, jpg)
	 *
	 * @return Returns true if the save is succesful
	 */
	private boolean setImage(final BufferedImage aBufferedImage,
			final File aFileOut, final String aFormatName) {

		try {
			// delete resources used by the File
			aFileOut.delete();
			ImageIO.write(aBufferedImage, aFormatName, aFileOut);
			return true;
		} catch (final Exception e) {
			getLogger().logSevere(this, "setImage",
					"File could not be saved! %s", e);
			return false;
		}
	}

	/**
	 * Creates a user space version of a Buffered Image, for editing and saving
	 * bytes
	 *
	 * @param image
	 *            The image to put into user space, removes compression
	 *            interferences
	 *
	 * @return The user space version of the supplied image
	 */
	private BufferedImage user_space(BufferedImage image) {

		// create new_img with the attributes of image
		final BufferedImage new_img = new BufferedImage(image.getWidth(),
				image.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
		final Graphics2D graphics = new_img.createGraphics();
		graphics.drawRenderedImage(image, null);
		graphics.dispose(); // release all allocated memory for this image
		return new_img;
	}
}