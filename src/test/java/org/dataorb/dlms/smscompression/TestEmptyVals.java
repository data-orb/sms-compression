package org.dataorb.dlms.smscompression;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.dataorb.dlms.smscompression.models.AggregateDatasetSmsSubmission;
import org.dataorb.dlms.smscompression.models.EnrollmentSmsSubmission;
import org.dataorb.dlms.smscompression.models.SmsEvent;
import org.dataorb.dlms.smscompression.models.SmsMetadata;
import org.dataorb.dlms.smscompression.models.SmsSubmission;
import org.dataorb.dlms.smscompression.models.SmsSubmissionHeader;
import org.dataorb.dlms.smscompression.models.SimpleEventSmsSubmission;
import org.dataorb.dlms.smscompression.models.TrackerEventSmsSubmission;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.gson.Gson;

public class TestEmptyVals
{
    SmsMetadata meta;

    SmsSubmissionWriter writer;

    SmsSubmissionReader reader;

    public String compressSubm( SmsSubmission subm )
        throws Exception
    {
        byte[] compressSubm = writer.compress( subm );
        String comp64 = TestUtils.encBase64( compressSubm );
        TestUtils.printBase64Subm( comp64, subm.getClass() );
        return comp64;
    }

    public SmsSubmission decompressSubm( String comp64 )
        throws Exception
    {
        byte[] decSubmBytes = TestUtils.decBase64( comp64 );
        SmsSubmissionHeader header = reader.readHeader( decSubmBytes );
        Assert.assertNotNull( header );
        return reader.readSubmission( decSubmBytes, meta );
    }

    @Before
    public void init()
        throws Exception
    {
        Gson gson = new Gson();
        String metadataJson = IOUtils.toString( new FileReader( "src/test/resources/metadata.json" ) );
        meta = gson.fromJson( metadataJson, SmsMetadata.class );
        writer = new SmsSubmissionWriter( meta );
        reader = new SmsSubmissionReader();
    }

    @After
    public void cleanup()
    {

    }

    @Test
    public void testEncDecAggregateDatasetNulls()
    {
        try
        {
            AggregateDatasetSmsSubmission origSubm = CreateSubm.createAggregateDatasetSubmission();
            origSubm.setValues( null );
            String comp64 = compressSubm( origSubm );
            AggregateDatasetSmsSubmission decSubm = (AggregateDatasetSmsSubmission) decompressSubm( comp64 );

            TestUtils.checkSubmissionsAreEqual( origSubm, decSubm );
        }
        catch ( Exception e )
        {
            e.printStackTrace();
            Assert.fail( e.getMessage() );
        }
    }

    @Test
    public void testEncDecSimpleEventNulls()
    {
        try
        {
            SimpleEventSmsSubmission origSubm = CreateSubm.createSimpleEventSubmission();
            origSubm.setEventDate( null );
            origSubm.setDueDate( null );
            origSubm.setCoordinates( null );
            origSubm.setValues( null );
            String comp64 = compressSubm( origSubm );
            SimpleEventSmsSubmission decSubm = (SimpleEventSmsSubmission) decompressSubm( comp64 );

            TestUtils.checkSubmissionsAreEqual( origSubm, decSubm );
        }
        catch ( Exception e )
        {
            e.printStackTrace();
            Assert.fail( e.getMessage() );
        }
    }

    @Test
    public void testEncDecEnrollmentNulls()
    {
        try
        {
            EnrollmentSmsSubmission origSubm = CreateSubm.createEnrollmentSubmission();
            origSubm.setEnrollmentDate( null );
            origSubm.setIncidentDate( null );
            origSubm.setCoordinates( null );
            origSubm.setValues( null );
            origSubm.setEvents( null );
            String comp64 = compressSubm( origSubm );
            EnrollmentSmsSubmission decSubm = (EnrollmentSmsSubmission) decompressSubm( comp64 );

            TestUtils.checkSubmissionsAreEqual( origSubm, decSubm );
        }
        catch ( Exception e )
        {
            e.printStackTrace();
            Assert.fail( e.getMessage() );
        }
    }

    @Test
    public void testEncDecEnrollmentNullEvents()
    {
        try
        {
            EnrollmentSmsSubmission origSubm = CreateSubm.createEnrollmentSubmission();
            List<SmsEvent> blankEvents = new ArrayList<>();
            for ( SmsEvent e : origSubm.getEvents() )
            {
                e.setEventDate( null );
                e.setDueDate( null );
                e.setCoordinates( null );
                e.setValues( null );
                blankEvents.add( e );
            }
            origSubm.setEvents( blankEvents );
            String comp64 = compressSubm( origSubm );
            EnrollmentSmsSubmission decSubm = (EnrollmentSmsSubmission) decompressSubm( comp64 );

            TestUtils.checkSubmissionsAreEqual( origSubm, decSubm );
        }
        catch ( Exception e )
        {
            e.printStackTrace();
            Assert.fail( e.getMessage() );
        }
    }

    @Test
    public void testEncDecTrackerEventNulls()
    {
        try
        {
            TrackerEventSmsSubmission origSubm = CreateSubm.createTrackerEventSubmission();
            origSubm.setEventDate( null );
            origSubm.setDueDate( null );
            origSubm.setCoordinates( null );
            origSubm.setValues( null );
            String comp64 = compressSubm( origSubm );
            TrackerEventSmsSubmission decSubm = (TrackerEventSmsSubmission) decompressSubm( comp64 );

            TestUtils.checkSubmissionsAreEqual( origSubm, decSubm );
        }
        catch ( Exception e )
        {
            e.printStackTrace();
            Assert.fail( e.getMessage() );
        }
    }

}
