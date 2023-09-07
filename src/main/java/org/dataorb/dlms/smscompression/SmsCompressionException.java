package org.dataorb.dlms.smscompression;

public class SmsCompressionException
    extends
    Exception
{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public SmsCompressionException( String message )
    {
        super( message );
    }

    public SmsCompressionException( String message, Throwable error )
    {
        super( message, error );
    }

    public SmsCompressionException( Throwable error )
    {
        super( error );
    }
}
