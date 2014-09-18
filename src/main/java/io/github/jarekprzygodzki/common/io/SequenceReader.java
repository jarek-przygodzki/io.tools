package io.github.jarekprzygodzki.common.io;

import java.io.IOException;
import java.io.Reader;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedList;

/**
 * A {@code SequenceReader} represents
 * the logical concatenation of other readers
 */
public class SequenceReader extends Reader {

	private final Deque<Reader> readers;

	/** Current reader */
	private Reader r;

	private SequenceReader(Reader... readers) throws IOException {
		this.readers = new LinkedList<Reader>();
		Collections.addAll(this.readers, readers);
		nextReader();
	}

	@Override
	public int read(char[] cbuf, int off, int len) throws IOException {
		if (r == null) {
			return -1;
		} else if (cbuf == null) {
			throw new NullPointerException();
		} else if (off < 0 || len < 0 || len > cbuf.length - off) {
			throw new IndexOutOfBoundsException();
		} else if (len == 0) {
			return 0;
		}

		int n = r.read(cbuf, off, len);
		if (n <= 0) {
			nextReader();
			return read(cbuf, off, len);
		}
		return n;

	}

	@Override
	public void close() throws IOException {
		do {
			nextReader();
		} while (hasNextReader());
	}

	private boolean hasNextReader() {
		return !readers.isEmpty();
	}

	private void nextReader() throws IOException {
		if (r != null) {
			r.close();
		}
		r = readers.poll();
	}

	public static Reader from(Reader... readers) throws IOException {
		return new SequenceReader(readers);
	}
}
