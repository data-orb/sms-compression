package org.dataorb.dlms.smscompression.models;

import java.util.Objects;

import org.dataorb.dlms.smscompression.SmsCompressionException;
import org.dataorb.dlms.smscompression.SmsConsts;
import org.dataorb.dlms.smscompression.SmsConsts.MetadataType;
import org.dataorb.dlms.smscompression.SmsConsts.SubmissionType;
import org.dataorb.dlms.smscompression.SmsSubmissionReader;
import org.dataorb.dlms.smscompression.SmsSubmissionWriter;

public class DeleteSmsSubmission
    extends
    SmsSubmission
{
    protected Uid event;

    /* Getters and Setters */

    public Uid getEvent()
    {
        return event;
    }

    public void setEvent( String event )
    {
        this.event = new Uid( event, MetadataType.EVENT );
    }

    @Override
    public boolean equals( Object o )
    {
        if ( !super.equals( o ) )
        {
            return false;
        }
        DeleteSmsSubmission subm = (DeleteSmsSubmission) o;
        return Objects.equals( event, subm.event );
    }

    /* Implementation of abstract methods */

    @Override
    public void writeSubm( SmsSubmissionWriter writer, int version )
        throws SmsCompressionException
    {
        if ( version != 1 && version != 2 )
        {
            throw new SmsCompressionException( versionError( version ) );
        }
        writer.writeId( event );
    }

    @Override
    public void readSubm( SmsSubmissionReader reader, int version )
        throws SmsCompressionException
    {
        if ( version != 1 && version != 2 )
        {
            throw new SmsCompressionException( versionError( version ) );
        }
        this.event = reader.readId( MetadataType.EVENT );
    }

    @Override
    public int getCurrentVersion()
    {
        return 2;
    }

    @Override
    public SubmissionType getType()
    {
        return SmsConsts.SubmissionType.DELETE;
    }
}
