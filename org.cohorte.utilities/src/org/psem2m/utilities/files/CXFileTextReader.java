package org.psem2m.utilities.files;

import static java.nio.file.StandardOpenOption.READ;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.psem2m.utilities.json.JSONArray;
import org.psem2m.utilities.json.JSONObject;

/**
 * @author ogattaz
 *
 */
public class CXFileTextReader {

	/**
	 * 
	 * <pre>
	 * {
	 *   "total":213,
	 *   "offset":20,
	 *   "size":5,
	 *   "lines":[
	 * 	        { "n":20, "l":"2021-04-28 10:13:34.997|INFO  |asksExecutorShiroAware(1)|         executeTask|task id=[Task_1849] finished status=[SUCCESS]"},
	 * 	        { "n":21, "l":"2021-04-28 10:13:34.997|INFO  |asksExecutorShiroAware(1)|             endTask|====END======> duration=[   130,657] task: Execution \"Task that rename a file\" at 2021-04-28 10:13:34.308 "},
	 * 	        { "n":22, "l":"2021-04-28 10:13:34.997|INFO  |asksExecutorShiroAware(1)|             newTask|======================================================================="},
	 * 	        { "n":23, "l":"2021-04-28 10:13:34.997|INFO  |asksExecutorShiroAware(1)|             newTask|====BEGIN ===> Execution \"Comparing the content of the current csv file against the previous one\" at 2021-04-28 10:13:34.353"},
	 * 	        { "n":24, "l":"2021-04-28 10:13:35.041|INFO  |asksExecutorShiroAware(1)|         executeTask|task id=[Task_9639] should execute a callable"},
	 * 	        { "n":25, "l":"2021-04-28 10:13:35.062|INFO  |asksExecutorShiroAware(1)|         executeTask|task id=[Task_9639] wait the end of the task"}
	 *      ]
	 *  }
	 * </pre>
	 * 
	 * @author ogattaz
	 *
	 */
	public class CTextPage {

		public static final String PROP_LINES = "lines";

		public static final String PROP_OFFSET = "offset";

		public static final String PROP_SIZE = "size";

		public static final String PROP_TOTAL = "total";

		private final List<String> pLines;

		private final long pOffset;

		private final long pTotal;

		/**
		 * @param aTotal
		 * @param aLines
		 */
		CTextPage(final long aTotal, final long aOffset, final List<String> aLines) {
			super();
			pTotal = aTotal;
			pOffset = aOffset;
			pLines = aLines;
		}

		/**
		 * @return
		 */
		public List<String> getLines() {
			return pLines;
		}

		/**
		 * @return
		 */
		public long getNbLine() {
			return pLines.size();
		}

		/**
		 * @return
		 */
		public long getOffset() {
			return pOffset;
		}

		/**
		 * @return
		 */
		public long getTotal() {
			return pTotal;
		}

		/**
		 * @return
		 */
		boolean hasTotal() {
			return getTotal() > 0;
		}

		/**
		 * <pre>
		 * 	    [ 
		 * 	        { "n":20, "l":"2021-04-28 10:13:34.997|INFO  |asksExecutorShiroAware(1)|         executeTask|task id=[Task_1849] finished status=[SUCCESS]"},
		 * 	        { "n":21, "l":"2021-04-28 10:13:34.997|INFO  |asksExecutorShiroAware(1)|             endTask|====END======> duration=[   130,657] task: Execution \"Task that rename a file\" at 2021-04-28 10:13:34.308 "},
		 * 	        { "n":22, "l":"2021-04-28 10:13:34.997|INFO  |asksExecutorShiroAware(1)|             newTask|======================================================================="},
		 * 	        { "n":23, "l":"2021-04-28 10:13:34.997|INFO  |asksExecutorShiroAware(1)|             newTask|====BEGIN ===> Execution \"Comparing the content of the current csv file against the previous one\" at 2021-04-28 10:13:34.353"},
		 * 	        { "n":24, "l":"2021-04-28 10:13:35.041|INFO  |asksExecutorShiroAware(1)|         executeTask|task id=[Task_9639] should execute a callable"},
		 * 	        { "n":25, "l":"2021-04-28 10:13:35.062|INFO  |asksExecutorShiroAware(1)|         executeTask|task id=[Task_9639] wait the end of the task"}
		 * 	    ]
		 * </pre>
		 * 
		 * @param aLines the list of line
		 * @param aOffset the start offset
		 * @return
		 */
		public JSONArray linesToJson() {
			JSONArray wJsonLines = new JSONArray();
			long wNumLine = getOffset();
			for (String wLine : pLines) {
				JSONObject wJsonLine = new JSONObject();
				wNumLine++;
				wJsonLine.put("n", wNumLine);
				wJsonLine.put("l", wLine);
				wJsonLines.put(wJsonLine);
			}
			return wJsonLines;
		}

		/**
		 * @return
		 */
		public JSONObject toJson() {
			JSONObject wPage = new JSONObject();
			wPage.put(PROP_TOTAL, getTotal());
			wPage.put(PROP_OFFSET, getOffset());
			wPage.put(PROP_SIZE, getNbLine());
			wPage.put(PROP_LINES, linesToJson());
			return wPage;
		}
	}

	/**
	 * @author ogattaz
	 *
	 */
	class CTextPager implements Predicate<String> {

		final long pEnd;

		final String pGrep;

		final boolean pHasGrep;

		final boolean pHasGrepNot;

		long pNbMatchedLine = 0;

		long pNbScannedLine = 0;

		final long pStart;

		/**
		 * @param aOffset
		 * @param aPageSize
		 */
		CTextPager(final int aOffset, final int aPageSize, final String aGrep) {
			super();

			pHasGrep = (aGrep != null && !aGrep.isEmpty());
			pHasGrepNot = pHasGrep && aGrep.charAt(0) == '!';
			pGrep = (pHasGrepNot) ? aGrep.substring(1) : aGrep;

			pStart = aOffset;
			pEnd = aOffset + aPageSize + 1;
		}

		/**
		 * @return
		 */
		long getNbMatchedLine() {
			return (pHasGrep) ? pNbMatchedLine : getNbScannedLine();
		}

		/**
		 * @return
		 */
		long getNbScannedLine() {
			return pNbScannedLine;
		}

		/**
		 * @param aLine
		 * @return
		 */
		private boolean grep(final String aLine) {
			return aLine.indexOf(pGrep) > -1;
		}

		/**
		 * @param aLine
		 * @return
		 */
		private boolean grepMatch(final String aLine) {
			return aLine != null && (pHasGrepNot) ? !grep(aLine) : grep(aLine);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.function.Predicate#test(java.lang.Object)
		 */
		@Override
		public boolean test(String aLine) {
			pNbScannedLine++;
			if (!pHasGrep) {
				return pNbScannedLine > pStart && pNbScannedLine < pEnd;
			}
			//
			else {
				boolean wMatch = grepMatch(aLine);
				if (wMatch) {
					pNbMatchedLine++;
					return pNbMatchedLine > pStart && pNbMatchedLine < pEnd;
				} else {
					return false;
				}
			}
		}
	}

	private static final int DEFAULT_BUFFER_SIZE = 512;

	private static final String DEFAULT_CHARSET = "UTF-8";

	private static final char NEW_LINE = 10;

	public static final String NO_GREP = null;

	private final File pFile;

	private final Charset pFileCharset;

	/**
	 * @param aFile
	 */
	public CXFileTextReader(final File aFile) {
		this(aFile, Charset.forName(DEFAULT_CHARSET));
	}

	/**
	 * @param aFile
	 */
	/**
	 * @param aFile
	 * @param aFileCharset
	 */
	public CXFileTextReader(final File aFile, Charset aFileCharset) {
		super();

		if (!aFile.exists()) {
			throw new InvalidPathException(aFile.getAbsolutePath(), "File does not exist");
		}

		pFile = aFile;
		pFileCharset = aFileCharset;
	}

	/**
	 * @param aFile
	 * @param aCharsetName
	 */
	public CXFileTextReader(final File aFile, final String aCharsetName) {
		this(aFile, Charset.forName(aCharsetName));

	}

	/**
	 * @param aFilePath
	 */
	public CXFileTextReader(final String aFilePath) {
		this(new File(aFilePath));
	}

	/**
	 * @param aFilePath
	 * @param aFileCharset
	 */
	public CXFileTextReader(final String aFilePath, final Charset aFileCharset) {
		this(new File(aFilePath), aFileCharset);
	}

	/**
	 * @param aFilePath
	 * @param aFileCharset
	 */
	public CXFileTextReader(final String aFilePath, final String aCharsetName) {
		this(new File(aFilePath), Charset.forName(aCharsetName));
	}

	/**
	 * @return
	 * @throws IOException
	 */
	public long countLines() throws IOException {

		return Files.lines(pFile.toPath(), getFileCharset()).count();
	}

	/**
	 * @param aGrep
	 * @return
	 * @throws IOException
	 */
	public long countLines(final String aGrep) throws IOException {

		return readPage(0, 0, aGrep).getTotal();
	}

	/**
	 * @param aLines
	 * @param aOutput
	 */
	private void flushLine(LinkedList<String> aLines, ByteArrayOutputStream aOutput) {

		try {
			aLines.addFirst(aOutput.toString(getFileCharsetName()));
		} catch (UnsupportedEncodingException e) {
			aLines.addFirst(aOutput.toString());
		}
		aOutput.reset();
	}

	/**
	 * @return
	 */
	public File getFile() {
		return pFile;
	}

	/**
	 * @return
	 */
	Charset getFileCharset() {
		return pFileCharset;
	}

	/**
	 * @return
	 */
	String getFileCharsetName() {
		return getFileCharset().name();
	}

	/**
	 * @return
	 */
	public String getFileName() {
		return pFile.getName();
	}

	/**
	 * @return
	 */
	public Path getPath() {
		return getFile().toPath();
	}

	/**
	 * @param out
	 * @param bytes
	 * @param offset
	 * @param limit
	 * @throws IOException
	 */
	private void prependBuffer(ByteArrayOutputStream out, byte[] bytes, int offset, int limit) throws IOException {
		ByteArrayOutputStream tmp = new ByteArrayOutputStream();
		tmp.write(bytes, offset, limit);
		out.writeTo(tmp);
		out.reset();
		tmp.writeTo(out);
	}

	/**
	 * @param aBytes
	 * @param aOutput
	 * @param aLines
	 * @param aNumberOfLines
	 * @throws IOException
	 */
	private void readChunk(byte[] aBytes, ByteArrayOutputStream aOutput, LinkedList<String> aLines, int aNumberOfLines)
			throws IOException {

		int wLimit = 0;
		int wOffset = aBytes.length - 1;
		boolean wHasToFlush = false;
		for (int i = wOffset; i >= 0 && aLines.size() < aNumberOfLines; i--) {
			byte b = aBytes[i];
			if (b == NEW_LINE) {
				wHasToFlush = true;
			} else {
				wOffset = i;
				wLimit++;
			}
			if (wHasToFlush) {
				prependBuffer(aOutput, aBytes, wOffset, wLimit);
				flushLine(aLines, aOutput);
				wLimit = 0;
				wHasToFlush = false;
			}
		}

		if (wLimit > 0) {
			// in case we have some characters left without in one line
			// we add them to buffer in front of the others
			prependBuffer(aOutput, aBytes, wOffset, wLimit);
		}
	}

	/**
	 * @param aOffset
	 * @param aPageSize
	 * @return
	 * @throws IOException
	 */
	public CTextPage readPage(final int aOffset, final int aPageSize) throws IOException {

		return readPage(aOffset, aPageSize, NO_GREP);
	}

	/**
	 * @param aOffset
	 * @param aPageSize
	 * @param aGrep
	 * @return
	 * @throws IOException
	 */
	public CTextPage readPage(final int aOffset, final int aPageSize, final String aGrep) throws IOException {

		CTextPager wCPredicatePager = new CTextPager(aOffset, aPageSize, aGrep);

		List<String> wLines = Files.lines(pFile.toPath(), getFileCharset()).filter(wCPredicatePager)
				.collect(Collectors.toList());

		return new CTextPage(wCPredicatePager.getNbMatchedLine(), aOffset, wLines);
	}

	/**
	 * 
	 * @param aOffset
	 * @param aPageSize
	 * @return
	 * @throws IOException
	 */
	public JSONObject readPageAsJson(final int aOffset, final int aPageSize) throws IOException {

		return readPageAsJson(aOffset, aPageSize, NO_GREP);
	}

	/**
	 * @param aOffset
	 * @param aPageSize
	 * @param aGrep
	 * @return
	 * @throws IOException
	 */
	public JSONObject readPageAsJson(final int aOffset, final int aPageSize, final String aGrep) throws IOException {

		return readPage(aOffset, aPageSize, aGrep).toJson();
	}

	/**
	 * Read a number of lines from bottom of file
	 * 
	 * @param path a string represents for path to file
	 * @param numberOfLines an integer number indicates number of lines to be
	 * retrieved
	 * @return a list of string represents for lines
	 *
	 * @throws InvalidPathException if file does not exist
	 * @throws IOException If an I/O error occurs
	 *
	 * @param aNumberOfLines
	 * @return
	 * @throws IOException
	 */
	public CTextPage tail(final int aNumberOfLines) throws IOException {

		LinkedList<String> wLines = new LinkedList<>();
		FileChannel wFileChannel = null;
		ByteArrayOutputStream wOut = null;
		try {
			wFileChannel = FileChannel.open(getPath(), READ);

			long wFileSize = wFileChannel.size();
			if (wFileSize == 0) {
				return new CTextPage(0, 0, wLines);
			}

			int wBufferSize = DEFAULT_BUFFER_SIZE;
			long wReadBytes = 0;
			long wPosition = 0;
			ByteBuffer wByteBuffer;
			wOut = new ByteArrayOutputStream();
			do {
				if (wBufferSize > wFileSize) {
					wBufferSize = (int) wFileSize;
					wPosition = 0;
				} else {
					wPosition = wFileSize - (wReadBytes + wBufferSize);
					if (wPosition < 0) {
						wPosition = 0;
						wBufferSize = (int) (wFileSize - wReadBytes);
					}
				}

				wByteBuffer = ByteBuffer.allocate(wBufferSize);
				wReadBytes += wFileChannel.read(wByteBuffer, wPosition);

				readChunk(wByteBuffer.array(), wOut, wLines, aNumberOfLines);
			} while (wLines.size() < aNumberOfLines && wPosition > 0);

			if (wOut.size() > 0) {
				flushLine(wLines, wOut);
			}

			return new CTextPage(0, 0, wLines);

		} catch (Exception e) {
			throw new IOException(String.format("Unable to tail [%s] lines from file [%s]", aNumberOfLines,
					getFileName()), e);
		} finally {

			if (wOut != null) {
				wOut.close();
			}
			if (wFileChannel != null) {
				wFileChannel.close();
			}
		}
	}

	/**
	 * @param aPageSize
	 * @return
	 * @throws IOException
	 */
	public JSONObject tailAsJson(final int aNumberOfLines) throws IOException {

		return tail(aNumberOfLines).toJson();
	}
}
