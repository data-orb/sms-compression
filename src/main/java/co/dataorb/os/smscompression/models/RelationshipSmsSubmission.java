package co.dataorb.os.smscompression.models;

import java.util.Objects;

import co.dataorb.os.smscompression.SmsCompressionException;
import co.dataorb.os.smscompression.SmsConsts;
import co.dataorb.os.smscompression.SmsConsts.MetadataType;
import co.dataorb.os.smscompression.SmsConsts.SubmissionType;
import co.dataorb.os.smscompression.SmsSubmissionReader;
import co.dataorb.os.smscompression.SmsSubmissionWriter;

public class RelationshipSmsSubmission
    extends
    SmsSubmission
{
    protected Uid relationshipType;

    protected Uid relationship;

    protected Uid from;

    protected Uid to;

    /* Getters and Setters */

    public Uid getRelationshipType()
    {
        return relationshipType;
    }

    public void setRelationshipType( String relationshipType )
    {
        this.relationshipType = new Uid( relationshipType, MetadataType.RELATIONSHIP_TYPE );
    }

    public Uid getRelationship()
    {
        return relationship;
    }

    public void setRelationship( String relationship )
    {
        this.relationship = new Uid( relationship, MetadataType.RELATIONSHIP );
    }

    public Uid getFrom()
    {
        return from;
    }

    public void setFrom( String from )
    {
        this.from = new Uid( from, null );
    }

    public Uid getTo()
    {
        return to;
    }

    public void setTo( String to )
    {
        this.to = new Uid( to, null );
    }

    @Override
    public boolean equals( Object o )
    {
        if ( !super.equals( o ) )
        {
            return false;
        }
        RelationshipSmsSubmission subm = (RelationshipSmsSubmission) o;

        return Objects.equals( relationshipType, subm.relationshipType )
            && Objects.equals( relationship, subm.relationship ) && Objects.equals( from, subm.from )
            && Objects.equals( to, subm.to );
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
        writer.writeId( relationshipType );
        writer.writeId( relationship );
        writer.writeNewId( from.getUid() );
        writer.writeNewId( to.getUid() );
    }

    @Override
    public void readSubm( SmsSubmissionReader reader, int version )
        throws SmsCompressionException
    {
        if ( version != 1 && version != 2 )
        {
            throw new SmsCompressionException( versionError( version ) );
        }
        this.relationshipType = reader.readId( MetadataType.RELATIONSHIP_TYPE );
        this.relationship = reader.readId( MetadataType.RELATIONSHIP );
        this.from = new Uid( reader.readNewId(), null );
        this.to = new Uid( reader.readNewId(), null );
    }

    @Override
    public int getCurrentVersion()
    {
        return 2;
    }

    @Override
    public SubmissionType getType()
    {
        return SmsConsts.SubmissionType.RELATIONSHIP;
    }
}
