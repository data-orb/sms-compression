package org.dataorb.dlms.smscompression.models;

import java.util.Date;

import org.dataorb.dlms.smscompression.SmsCompressionException;
import org.dataorb.dlms.smscompression.SmsConsts.SubmissionType;
import org.dataorb.dlms.smscompression.SmsSubmissionReader;
import org.dataorb.dlms.smscompression.SmsSubmissionWriter;

public class SmsSubmissionHeader
{
    protected SubmissionType type;

    protected int version;

    protected Date lastSyncDate;

    protected int submissionId;

    public int getSubmissionId()
    {
        return submissionId;
    }

    public void setSubmissionId( int submissionId )
    {
        this.submissionId = submissionId;
    }

    public SubmissionType getType()
    {
        return type;
    }

    public void setType( SubmissionType type )
    {
        this.type = type;
    }

    public int getVersion()
    {
        return version;
    }

    public void setVersion( int version )
    {
        this.version = version;
    }

    public Date getLastSyncDate()
    {
        return lastSyncDate;
    }

    public void setLastSyncDate( Date lastSyncDate )
    {
        this.lastSyncDate = lastSyncDate;
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }
        SmsSubmissionHeader hdr = (SmsSubmissionHeader) o;
        return type.equals( hdr.type ) && version == hdr.version && lastSyncDate.equals( hdr.lastSyncDate )
            && submissionId == hdr.submissionId;
    }

    public void validateHeaer()
        throws SmsCompressionException
    {
        // TODO: More validation here
        if ( submissionId < 0 || submissionId > 255 )
        {
            throw new SmsCompressionException( "Ensure the Submission ID has been set for this submission" );
        }
    }

    public void writeHeader( SmsSubmissionWriter writer )
        throws SmsCompressionException
    {
        writer.writeType( type );
        writer.writeVersion( version );
        writer.writeNonNullableDate( lastSyncDate );
        writer.writeSubmissionId( submissionId );
    }

    public void readHeader( SmsSubmissionReader reader )
        throws SmsCompressionException
    {
        this.type = reader.readType();
        this.version = reader.readVersion();
        this.lastSyncDate = reader.readNonNullableDate();
        this.submissionId = reader.readSubmissionId();
    }
}
