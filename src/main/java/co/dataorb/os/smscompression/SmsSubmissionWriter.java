package co.dataorb.os.smscompression;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import co.dataorb.os.smscompression.SmsConsts.SmsEnrollmentStatus;
import co.dataorb.os.smscompression.SmsConsts.SmsEventStatus;
import co.dataorb.os.smscompression.SmsConsts.SubmissionType;
import co.dataorb.os.smscompression.models.GeoPoint;
import co.dataorb.os.smscompression.models.SmsAttributeValue;
import co.dataorb.os.smscompression.models.SmsDataValue;
import co.dataorb.os.smscompression.models.SmsEvent;
import co.dataorb.os.smscompression.models.SmsMetadata;
import co.dataorb.os.smscompression.models.SmsSubmission;
import co.dataorb.os.smscompression.models.Uid;
import co.dataorb.os.smscompression.utils.BitOutputStream;
import co.dataorb.os.smscompression.utils.IdUtil;
import co.dataorb.os.smscompression.utils.ValueUtil;

public class SmsSubmissionWriter
{
    ByteArrayOutputStream byteStream;

    BitOutputStream outStream;

    SmsMetadata meta;

    ValueWriter valueWriter;

    // By default we enable hashing but it can be disabled
    boolean hashingEnabled = true;

    public SmsSubmissionWriter( SmsMetadata meta )
        throws SmsCompressionException
    {
        if ( meta != null )
            meta.validate();
        this.meta = meta;
    }

    public boolean isHashingEnabled()
    {
        return hashingEnabled;
    }

    public void setHashingEnabled( boolean useHashing )
    {
        this.hashingEnabled = useHashing;
    }

    public byte[] compress( SmsSubmission subm )
        throws SmsCompressionException
    {
        return compress( subm, subm.getCurrentVersion() );
    }

    public byte[] compress( SmsSubmission subm, int version )
        throws SmsCompressionException
    {
        this.byteStream = new ByteArrayOutputStream();
        this.outStream = new BitOutputStream( byteStream );
        this.valueWriter = new ValueWriter( outStream, meta, hashingEnabled );

        subm.write( meta, this, version );

        return toByteArray();
    }

    public byte[] toByteArray()
        throws SmsCompressionException
    {
        try
        {
            outStream.close();
            byte[] subm = byteStream.toByteArray();
            byte[] crcSubm = writeCrc( subm );
            return crcSubm;
        }
        catch ( IOException e )
        {
            throw new SmsCompressionException( e );
        }
    }

    public byte[] writeCrc( byte[] subm )
    {
        byte crc;
        try
        {
            MessageDigest digest = MessageDigest.getInstance( "SHA-256" );
            byte[] calcCrc = digest.digest( subm );
            crc = calcCrc[0];
        }
        catch ( NoSuchAlgorithmException e )
        {
            e.printStackTrace();
            return null;
        }

        byte[] crcSubm = new byte[subm.length + 1];
        System.arraycopy( subm, 0, crcSubm, 1, subm.length );
        crcSubm[0] = crc;

        return crcSubm;
    }

    public void writeType( SubmissionType type )
        throws SmsCompressionException
    {
        outStream.write( type.ordinal(), SmsConsts.SUBM_TYPE_BITLEN );
    }

    public void writeVersion( int version )
        throws SmsCompressionException
    {
        outStream.write( version, SmsConsts.VERSION_BITLEN );
    }

    public void writeNonNullableDate( Date date )
        throws SmsCompressionException
    {
        ValueUtil.writeDate( date, outStream );
    }

    public void writeDate( Date date )
        throws SmsCompressionException
    {
        writeBool( date != null );
        if ( date != null )
        {
            ValueUtil.writeDate( date, outStream );
        }
    }

    public void writeId( Uid uid )
        throws SmsCompressionException
    {
        IdUtil.writeId( uid, hashingEnabled, meta, outStream );
    }

    public void writeNewId( String id )
        throws SmsCompressionException
    {
        IdUtil.writeNewId( id, outStream );
    }

    public void writeAttributeValues( List<SmsAttributeValue> values )
        throws SmsCompressionException
    {
        valueWriter.writeAttributeValues( values );
    }

    public void writeDataValues( List<SmsDataValue> values )
        throws SmsCompressionException
    {
        valueWriter.writeDataValues( values );
    }

    public void writeBool( boolean val )
        throws SmsCompressionException
    {
        ValueUtil.writeBool( val, outStream );
    }

    // TODO: We should consider a better implementation for period than just
    // String
    public void writePeriod( String period )
        throws SmsCompressionException
    {
        ValueUtil.writeString( period, outStream );
    }

    public void writeSubmissionId( int submissionId )
        throws SmsCompressionException
    {
        outStream.write( submissionId, SmsConsts.SUBM_ID_BITLEN );
    }

    public void writeEventStatus( SmsEventStatus eventStatus )
        throws SmsCompressionException
    {
        outStream.write( eventStatus.ordinal(), SmsConsts.EVENT_STATUS_BITLEN );
    }

    public void writeEvents( List<SmsEvent> events, int version )
        throws SmsCompressionException
    {
        boolean hasEvents = (events != null && !events.isEmpty());
        writeBool( hasEvents );
        if ( hasEvents )
        {
            for ( Iterator<SmsEvent> eventIter = events.iterator(); eventIter.hasNext(); )
            {
                SmsEvent event = eventIter.next();
                event.writeEvent( this, version );
                writeBool( eventIter.hasNext() );
            }
        }
    }

    public void writeGeoPoint( GeoPoint coordinates )
        throws SmsCompressionException
    {
        writeBool( coordinates != null );
        if ( coordinates != null )
        {
            ValueUtil.writeFloat( coordinates.getLatitude(), outStream );
            ValueUtil.writeFloat( coordinates.getLongitude(), outStream );
        }
    }

    public void writeEnrollmentStatus( SmsEnrollmentStatus enrollmentStatus )
        throws SmsCompressionException
    {
        outStream.write( enrollmentStatus.ordinal(), SmsConsts.ENROL_STATUS_BITLEN );
    }
}
