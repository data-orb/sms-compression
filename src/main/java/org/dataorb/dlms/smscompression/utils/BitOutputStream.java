package org.dataorb.dlms.smscompression.utils;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;

import org.dataorb.dlms.smscompression.SmsCompressionException;

/**
 * A stream where bits can be written to. Because they are written to an
 * underlying byte stream, the end of the stream is padded with 0's up to a
 * multiple of 8 bits. The bits are written in big endian. Mutable and not
 * thread-safe.
 * 
 * @see BitInputStream
 */
public final class BitOutputStream
    implements
    AutoCloseable
{
    private OutputStream output;

    private int currentByte;

    private int numBitsFilled;

    public BitOutputStream( OutputStream out )
    {
        Objects.requireNonNull( out );
        output = out;
        currentByte = 0;
        numBitsFilled = 0;
    }

    /**
     * Writes a bit to the stream. The specified bit must be 0 or 1.
     * 
     * @param b the bit to write, which must be 0 or 1
     * @throws IOException if an I/O exception occurred
     */
    public void writeBit( int b )
        throws IOException
    {
        if ( b != 0 && b != 1 )
            throw new IllegalArgumentException( "Argument must be 0 or 1" );
        currentByte = (currentByte << 1) | b;
        numBitsFilled++;
        if ( numBitsFilled == 8 )
        {
            output.write( currentByte );
            currentByte = 0;
            numBitsFilled = 0;
        }
    }

    /**
     * Writes n bits to the stream
     * 
     * @param i the integer to write, which should be positive and representable
     *        in n or less bits
     * @param n the number of bits to write i in
     * @throws IOException if an I/O exception occurred
     */
    public void write( int i, int n )
        throws SmsCompressionException
    {
        try
        {
            if ( i < 0 )
                throw new NumberFormatException( "Cannot write negative ints" );
            int mask = 1 << (n - 1);
            while ( n > 0 )
            {
                int nextBit = (mask & i) > 0 ? 1 : 0;
                writeBit( nextBit );
                mask >>>= 1;
                n--;
            }
        }
        catch ( IOException e )
        {
            throw new SmsCompressionException( e );
        }
    }

    /**
     * Closes this stream and the underlying output stream. If called when this
     * bit stream is not at a byte boundary, then the minimum number of "0" bits
     * (between 0 and 7 of them) are written as padding to reach the next byte
     * boundary.
     * 
     * @throws IOException if an I/O exception occurred
     */
    @Override
    public void close()
        throws IOException
    {
        while ( numBitsFilled != 0 )
            writeBit( 0 );
        output.close();
    }

}
