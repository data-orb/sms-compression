package co.dataorb.os.smscompression.utils;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

import co.dataorb.os.smscompression.SmsCompressionException;

/**
 * A stream of bits that can be read. Because they come from an underlying byte
 * stream, the total number of bits is always a multiple of 8. The bits are read
 * in big endian. Mutable and not thread-safe.
 * 
 * @see BitOutputStream
 */
public final class BitInputStream
    implements
    AutoCloseable
{
    private InputStream input;

    private int currentByte;

    private int numBitsRemaining;

    public BitInputStream( InputStream in )
    {
        Objects.requireNonNull( in );
        input = in;
        currentByte = 0;
        numBitsRemaining = 0;
    }

    /**
     * Reads a bit from this stream. Returns 0 or 1 if a bit is available, or -1
     * if the end of stream is reached. The end of stream always occurs on a
     * byte boundary.
     * 
     * @return the next bit of 0 or 1, or -1 for the end of stream
     * @throws IOException if an I/O exception occurred
     */
    public int readBit()
        throws IOException
    {
        if ( currentByte == -1 )
            return -1;
        if ( numBitsRemaining == 0 )
        {
            currentByte = input.read();
            if ( currentByte == -1 )
                return -1;
            numBitsRemaining = 8;
        }
        if ( numBitsRemaining <= 0 )
            throw new AssertionError();
        numBitsRemaining--;
        return (currentByte >>> numBitsRemaining) & 1;
    }

    /**
     * Reads n bits from this stream and returns the result as an integer.
     * Throws an EOFException if the end of the stream is reached.
     * 
     * @return the next n bits as an integer
     * @throws SmsCompressionException if an I/O exception or EOF exception
     *         occurred
     */
    public int read( int n )
        throws SmsCompressionException
    {
        try
        {
            int i = 0;
            while ( n > 0 )
            {
                i <<= 1;
                i |= readBitEof();
                n--;
            }
            return i;
        }
        catch ( IOException e )
        {
            throw new SmsCompressionException( e );
        }
    }

    /**
     * Reads a bit from this stream. Returns 0 or 1 if a bit is available, or
     * throws an {@code EOFException} if the end of stream is reached. The end
     * of stream always occurs on a byte boundary.
     * 
     * @return the next bit, 0 or 1
     * @throws IOException if an I/O exception occurred
     * @throws EOFException if the end of stream is reached
     */
    public int readBitEof()
        throws IOException
    {
        int result = readBit();
        if ( result != -1 )
            return result;
        else
            throw new EOFException();
    }

    /**
     * Closes this stream and the underlying input stream.
     * 
     * @throws IOException if an I/O exception occurred
     */
    @Override
    public void close()
        throws IOException
    {
        input.close();
        currentByte = -1;
        numBitsRemaining = 0;
    }

}
