package co.dataorb.os.smscompression.models;

import java.util.Date;

import co.dataorb.os.smscompression.SmsCompressionException;
import co.dataorb.os.smscompression.SmsConsts.MetadataType;
import co.dataorb.os.smscompression.SmsConsts.SubmissionType;
import co.dataorb.os.smscompression.SmsSubmissionReader;
import co.dataorb.os.smscompression.SmsSubmissionWriter;

public abstract class SmsSubmission
{
    protected SmsSubmissionHeader header;

    protected Uid userId;

    public abstract int getCurrentVersion();

    public abstract SubmissionType getType();

    // Note: When handling versioning, create a new method to handle
    // each version, rather than handling all formats in this method alone
    public abstract void writeSubm( SmsSubmissionWriter writer, int version )
        throws SmsCompressionException;

    public abstract void readSubm( SmsSubmissionReader reader, int version )
        throws SmsCompressionException;

    public SmsSubmission()
    {
        this.header = new SmsSubmissionHeader();
        header.setType( this.getType() );
        // Initialise the submission ID so we know if it's been set correctly
        header.setSubmissionId( -1 );
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
        SmsSubmission subm = (SmsSubmission) o;
        return userId.equals( subm.userId ) && header.equals( subm.header );
    }

    public void setSubmissionId( int submissionId )
    {
        header.setSubmissionId( submissionId );
    }

    public Uid getUserId()
    {
        return userId;
    }

    public void setUserId( String userId )
    {
        this.userId = new Uid( userId, MetadataType.USER );
    }

    public void validateSubmission()
        throws SmsCompressionException
    {
        header.validateHeaer();
        if ( userId.getUid().isEmpty() )
        {
            throw new SmsCompressionException( "Ensure the UserID is set in the submission" );
        }
        // TODO: We should run validations on each submission here
    }

    public void write( SmsMetadata meta, SmsSubmissionWriter writer, int version )
        throws SmsCompressionException
    {
        // Ensure we set the lastSyncDate in the subm header
        Date lastSyncDate = meta != null && meta.lastSyncDate != null ? meta.lastSyncDate : new Date( 0 );
        header.setLastSyncDate( lastSyncDate );

        validateSubmission();
        header.setVersion( version );
        header.writeHeader( writer );
        writer.writeId( userId );
        writeSubm( writer, version );
    }

    public void read( SmsSubmissionReader reader, SmsSubmissionHeader header )
        throws SmsCompressionException
    {
        this.header = header;
        this.userId = reader.readId( MetadataType.USER );
        readSubm( reader, this.header.getVersion() );
    }

    protected String versionError( int version )
    {
        return String.format( "Version %d of %s is not supported", version, this.getClass().getSimpleName() );
    }
}