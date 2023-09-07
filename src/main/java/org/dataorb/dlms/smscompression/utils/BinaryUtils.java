package org.dataorb.dlms.smscompression.utils;

public class BinaryUtils
{

    /**
     * @return an array of bytes as a binary string
     */
    public static String print( byte[] byteArray )
    {
        String output = "";
        for ( byte b : byteArray )
        {
            output += print( b );
        }

        return output;
    }

    /**
     * @return a single byte as a binary string
     */
    public static String print( byte b )
    {
        return Integer.toBinaryString( (b & 0xFF) + 0x100 ).substring( 1 );
    }

    /**
     * @return the log2 of n
     */
    public static int log2( int n )
    {
        if ( n <= 0 )
            throw new IllegalArgumentException();
        return 31 - Integer.numberOfLeadingZeros( n );
    }

    /**
     * @return the minimum bit length needed to represent this int (unsigned)
     */
    public static int bitlenNeeded( int n )
    {
        return n == 0 ? 1 : log2( n ) + 1;
    }

    /**
     * @return a unique hash for a string for a given bit length
     */
    public static int hash( String s, int bitlen )
    {
        return Math.abs( s.hashCode() ) % (int) Math.pow( 2, bitlen );
    }
}
