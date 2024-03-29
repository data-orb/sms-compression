package co.dataorb.os.smscompression;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import co.dataorb.os.smscompression.SmsConsts.MetadataType;
import co.dataorb.os.smscompression.SmsConsts.SmsEnrollmentStatus;
import co.dataorb.os.smscompression.SmsConsts.SmsEventStatus;
import co.dataorb.os.smscompression.SmsConsts.SubmissionType;
import co.dataorb.os.smscompression.models.AggregateDatasetSmsSubmission;
import co.dataorb.os.smscompression.models.DeleteSmsSubmission;
import co.dataorb.os.smscompression.models.EnrollmentSmsSubmission;
import co.dataorb.os.smscompression.models.GeoPoint;
import co.dataorb.os.smscompression.models.RelationshipSmsSubmission;
import co.dataorb.os.smscompression.models.SmsAttributeValue;
import co.dataorb.os.smscompression.models.SmsDataValue;
import co.dataorb.os.smscompression.models.SmsEvent;
import co.dataorb.os.smscompression.models.SmsMetadata;
import co.dataorb.os.smscompression.models.SmsSubmission;
import co.dataorb.os.smscompression.models.SmsSubmissionHeader;
import co.dataorb.os.smscompression.models.SimpleEventSmsSubmission;
import co.dataorb.os.smscompression.models.TrackerEventSmsSubmission;
import co.dataorb.os.smscompression.models.Uid;
import co.dataorb.os.smscompression.utils.BitInputStream;
import co.dataorb.os.smscompression.utils.IdUtil;
import co.dataorb.os.smscompression.utils.ValueUtil;

public class SmsSubmissionReader
{
    BitInputStream inStream;

    SmsMetadata meta;

    ValueReader valueReader;

    public SmsSubmissionHeader readHeader( byte[] smsBytes )
        throws SmsCompressionException
    {
        if ( !checkCrc( smsBytes ) )
            throw new SmsCompressionException( "Invalid CRC - CRC in header does not match submission" );

        ByteArrayInputStream byteStream = new ByteArrayInputStream( smsBytes );
        this.inStream = new BitInputStream( byteStream );
        inStream.read( SmsConsts.CRC_BITLEN ); // skip CRC

        SmsSubmissionHeader header = new SmsSubmissionHeader();
        header.readHeader( this );

        return header;
    }

    public SmsSubmission readSubmission( byte[] smsBytes, SmsMetadata meta )
        throws SmsCompressionException
    {
        if ( meta != null )
            meta.validate();
        this.meta = meta;
        SmsSubmissionHeader header = readHeader( smsBytes );
        this.valueReader = new ValueReader( inStream, meta );
        SmsSubmission subm = null;

        switch ( header.getType() )
        {
        case AGGREGATE_DATASET:
            subm = new AggregateDatasetSmsSubmission();
            break;
        case DELETE:
            subm = new DeleteSmsSubmission();
            break;
        case ENROLLMENT:
            subm = new EnrollmentSmsSubmission();
            break;
        case RELATIONSHIP:
            subm = new RelationshipSmsSubmission();
            break;
        case SIMPLE_EVENT:
            subm = new SimpleEventSmsSubmission();
            break;
        case TRACKER_EVENT:
            subm = new TrackerEventSmsSubmission();
            break;
        default:
            throw new SmsCompressionException( "Unknown SMS Submission Type: " + header.getType() );
        }

        subm.read( this, header );
        try
        {
            inStream.close();
        }
        catch ( IOException e )
        {
            throw new SmsCompressionException( e );
        }
        return subm;
    }

    private boolean checkCrc( byte[] smsBytes )
    {
        byte crc = smsBytes[0];
        byte[] submBytes = Arrays.copyOfRange( smsBytes, 1, smsBytes.length );

        try
        {
            MessageDigest digest = MessageDigest.getInstance( "SHA-256" );
            byte[] calcCrc = digest.digest( submBytes );
            return (calcCrc[0] == crc);
        }
        catch ( NoSuchAlgorithmException e )
        {
            e.printStackTrace();
            return false;
        }
    }

    public SubmissionType readType()
        throws SmsCompressionException
    {
        int submType = inStream.read( SmsConsts.SUBM_TYPE_BITLEN );
        return SmsConsts.SubmissionType.values()[submType];
    }

    public int readVersion()
        throws SmsCompressionException
    {
        return inStream.read( SmsConsts.VERSION_BITLEN );
    }

    public Date readNonNullableDate()
        throws SmsCompressionException
    {
        return ValueUtil.readDate( inStream );
    }

    public Date readDate()
        throws SmsCompressionException
    {
        boolean hasDate = readBool();
        if ( !hasDate )
        {
            return null;

        }
        return ValueUtil.readDate( inStream );
    }

    public Uid readId( MetadataType type )
        throws SmsCompressionException
    {
        return IdUtil.readId( type, meta, inStream );
    }

    public String readNewId()
        throws SmsCompressionException
    {
        return IdUtil.readNewId( inStream );
    }

    public List<SmsAttributeValue> readAttributeValues()
        throws SmsCompressionException
    {
        return valueReader.readAttributeValues();
    }

    public List<SmsDataValue> readDataValues()
        throws SmsCompressionException
    {
        return valueReader.readDataValues();
    }

    public boolean readBool()
        throws SmsCompressionException
    {
        return ValueUtil.readBool( inStream );
    }

    // TODO: Update this once we have a better impl of period
    public String readPeriod()
        throws SmsCompressionException
    {
        return ValueUtil.readString( inStream );
    }

    public int readSubmissionId()
        throws SmsCompressionException
    {
        return inStream.read( SmsConsts.SUBM_ID_BITLEN );
    }

    public SmsEventStatus readEventStatus()
        throws SmsCompressionException
    {
        int eventStatusNum = inStream.read( SmsConsts.EVENT_STATUS_BITLEN );
        return SmsEventStatus.values()[eventStatusNum];
    }

    public List<SmsEvent> readEvents( int version )
        throws SmsCompressionException
    {
        boolean hasEvents = readBool();
        ArrayList<SmsEvent> events = null;
        if ( hasEvents )
        {
            events = new ArrayList<>();
            for ( boolean hasNext = true; hasNext; hasNext = readBool() )
            {
                SmsEvent event = new SmsEvent();
                event.readEvent( this, version );
                events.add( event );
            }
        }

        return events;
    }

    public GeoPoint readGeoPoint()
        throws SmsCompressionException
    {
        GeoPoint gp = null;
        boolean hasGeoPoint = readBool();
        if ( hasGeoPoint )
        {
            float lat = ValueUtil.readFloat( inStream );
            float lon = ValueUtil.readFloat( inStream );
            gp = new GeoPoint( lat, lon );
        }

        return gp;
    }

    public SmsEnrollmentStatus readEnrollmentStatus()
        throws SmsCompressionException
    {
        int enrollStatusNum = inStream.read( SmsConsts.ENROL_STATUS_BITLEN );
        return SmsEnrollmentStatus.values()[enrollStatusNum];
    }
}
