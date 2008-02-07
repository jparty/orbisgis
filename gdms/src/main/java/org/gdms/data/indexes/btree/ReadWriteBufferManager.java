package org.gdms.data.indexes.btree;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;

public class ReadWriteBufferManager {

	private int bufferSize;

	private ByteBuffer buffer;

	private FileChannel channel;

	private int windowStart;

	private int positionInFile;

	private boolean bufferModified;

	private int highestModification;

	/**
	 * Instantiates a ReadBufferManager to read the specified channel
	 *
	 * @param channel
	 * @throws IOException
	 */
	public ReadWriteBufferManager(FileChannel channel) throws IOException {
		this(channel, 1024 * 32);
	}

	/**
	 * Instantiates a ReadBufferManager to read the specified channel. The
	 * specified bufferSize is the size of the channel content cached in memory
	 *
	 * @param channel
	 * @param bufferSize
	 * @throws IOException
	 */
	public ReadWriteBufferManager(FileChannel channel, int bufferSize)
			throws IOException {
		this.channel = channel;
		buffer = ByteBuffer.allocate(bufferSize);
		this.bufferSize = bufferSize;
		readIntoBuffer(0);
		windowStart = 0;
	}

	private void readIntoBuffer(long position) throws IOException {
		channel.position(position);
		int numBytes = this.channel.read(buffer);
		if (numBytes == -1) {
			numBytes = 0;
		}
		if (numBytes < bufferSize) {
			byte[] fillBytes = new byte[bufferSize - numBytes];
			buffer.put(fillBytes);
		}
		buffer.flip();
	}

	/**
	 * Moves the window if necessary to contain the desired byte and returns the
	 * position of the byte in the window
	 *
	 * @param bytePos
	 * @throws IOException
	 */
	private int getWindowOffset(int bytePos, int length) throws IOException {
		int desiredMin = bytePos;
		int desiredMax = desiredMin + length - 1;
		if ((desiredMin >= windowStart)
				&& (desiredMax < windowStart + buffer.capacity())) {
			return desiredMin - windowStart;
		} else {
			// Write back the buffer
			if (bufferModified) {
				flush();
			}

			// Calculate buffer capacity
			int bufferCapacity = Math.max(bufferSize, length);
			// bufferCapacity = Math.min(bufferCapacity, (int) channel.size());

			windowStart = bytePos;

			// Get a buffer of the suitable size
			if (buffer.capacity() != bufferCapacity) {
				ByteOrder order = buffer.order();
				buffer = ByteBuffer.allocate(bufferCapacity);
				buffer.order(order);
			} else {
				buffer.clear();
			}

			// Read the buffer
			readIntoBuffer(windowStart);

			// We won't write back the buffer so far
			bufferModified = false;
			highestModification = 0;

			return desiredMin - windowStart;
		}
	}

	/**
	 * Gets the byte value at the specified position
	 *
	 * @param bytePos
	 * @return
	 * @throws IOException
	 */
	public byte getByte(int bytePos) throws IOException {
		return buffer.get(getWindowOffset(bytePos, 1));
	}

	/**
	 * Gets the size of the channel
	 *
	 * @return
	 * @throws IOException
	 */
	public long getLength() throws IOException {
		return channel.size();
	}

	/**
	 * Specifies the byte order. One of the constants in {@link ByteBuffer}
	 *
	 * @param order
	 */
	public void order(ByteOrder order) {
		buffer.order(order);
	}

	/**
	 * Gets the int value at the specified position
	 *
	 * @param bytePos
	 * @return
	 * @throws IOException
	 */
	public int getInt(int bytePos) throws IOException {
		return buffer.getInt(getWindowOffset(bytePos, 4));
	}

	/**
	 * Gets the byte value at the current position
	 *
	 * @return
	 * @throws IOException
	 */
	public byte get() throws IOException {
		byte ret = getByte(positionInFile);
		positionInFile += 1;
		return ret;
	}

	/**
	 * Gets the int value at the current position
	 *
	 * @return
	 * @throws IOException
	 */
	public int getInt() throws IOException {
		int ret = getInt(positionInFile);
		positionInFile += 4;
		return ret;
	}

	/**
	 * skips the specified number of bytes from the current position in the
	 * channel
	 *
	 * @param numBytes
	 * @throws IOException
	 */
	public void skip(int numBytes) throws IOException {
		positionInFile += numBytes;
	}

	/**
	 * Gets the byte[] value at the current position
	 *
	 * @param buffer
	 * @return
	 * @throws IOException
	 */
	public ByteBuffer get(byte[] buffer) throws IOException {
		int windowOffset = getWindowOffset(positionInFile, buffer.length);
		this.buffer.position(windowOffset);
		positionInFile += buffer.length;
		return this.buffer.get(buffer);
	}

	/**
	 * Gets the byte[] value at the specified position
	 *
	 * @param pos
	 * @param buffer
	 * @return
	 * @throws IOException
	 */
	public ByteBuffer get(int pos, byte[] buffer) throws IOException {
		int windowOffset = getWindowOffset(pos, buffer.length);
		this.buffer.position(windowOffset);
		return this.buffer.get(buffer);
	}

	/**
	 * Moves the current position to the specified one
	 *
	 * @param position
	 */
	public void position(int position) {
		this.positionInFile = position;
	}

	/**
	 * Gets the double value at the specified position
	 *
	 * @return
	 * @throws IOException
	 */
	public double getDouble() throws IOException {
		double ret = getDouble(positionInFile);
		positionInFile += 8;
		return ret;
	}

	/**
	 * Gets the double value at the specified position
	 *
	 * @param bytePos
	 * @return
	 * @throws IOException
	 */
	public double getDouble(int bytePos) throws IOException {
		return buffer.getDouble(getWindowOffset(bytePos, 8));
	}

	/**
	 * If the current position is at the end of the channel
	 *
	 * @return
	 * @throws IOException
	 */
	public boolean isEOF() throws IOException {
		return (buffer.remaining() == 0)
				&& (windowStart + buffer.capacity() == channel.size());
	}

	public int getPosition() {
		return positionInFile;
	}

	public void putInt(int value) throws IOException {
		buffer.putInt(getWindowOffset(getPosition(), 4), value);
		positionInFile += 4;
		modificationDone();
	}

	public void put(byte value) throws IOException {
		buffer.put(getWindowOffset(getPosition(), 1), value);
		positionInFile += 1;
		modificationDone();
	}

	public void put(byte[] bytes) throws IOException {
		int windowOffset = getWindowOffset(getPosition(), bytes.length);
		buffer.position(windowOffset);
		buffer.put(bytes);
		positionInFile += bytes.length;
		modificationDone();
	}

	private void modificationDone() {
		bufferModified = true;
		int bufferModification = positionInFile - windowStart;
		if (bufferModification > highestModification) {
			highestModification = bufferModification;
		}
	}

	public void flush() throws IOException {
		buffer.position(0);
		buffer.limit(highestModification);
		channel.position(windowStart);
		channel.write(buffer);
		buffer.clear();
	}

	public int getEOFDirection() throws IOException {
		int fileSize = (int) channel.size();
		int highestModificationInFile = windowStart + highestModification;

		return Math.max(fileSize, highestModificationInFile);
	}

}
