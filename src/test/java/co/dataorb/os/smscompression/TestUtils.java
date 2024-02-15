package co.dataorb.os.smscompression;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import co.dataorb.os.smscompression.SmsConsts.SmsEventStatus;
import co.dataorb.os.smscompression.models.GeoPoint;
import co.dataorb.os.smscompression.models.SmsDataValue;
import co.dataorb.os.smscompression.models.SmsEvent;
import co.dataorb.os.smscompression.models.SmsSubmission;
import org.junit.Assert;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class TestUtils
{

    public static Date getNowWithoutMillis()
    {
        Calendar cal = Calendar.getInstance();
        cal.set( Calendar.MILLISECOND, 0 );
        return cal.getTime();
    }

    public static List<SmsEvent> createEventList()
    {
        List<SmsEvent> events = new ArrayList<>();
        for ( int i = 1; i <= 3; i++ )
        {
            SmsEvent event = new SmsEvent();
            event.setOrgUnit( "DiszpKrYNg8" ); // Ngelehun CHC
            event.setProgramStage( "A03MvHHogjR" ); // Birth
            event.setEventStatus( SmsEventStatus.COMPLETED );
            event.setAttributeOptionCombo( "HllvX50cXC0" ); // Default
                                                            // catOptionCombo
            event.setEvent( "r7M1gUFK3X" + i ); // New UID
            event.setEventDate( getNowWithoutMillis() );
            event.setDueDate( getNowWithoutMillis() );
            event.setCoordinates( new GeoPoint( 8.4844694f, -13.2364332f ) );
            ArrayList<SmsDataValue> values = new ArrayList<>();
            values.add( new SmsDataValue( "HllvX50cXC0", "UXz7xuGCEhU", String.valueOf( i + 1 ) ) ); // Weight
            event.setValues( values );
            events.add( event );
        }

        return events;
    }

    public static void printSubm( SmsSubmission subm )
    {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        System.out.println( gson.toJson( subm ) );
    }

    public static String encBase64( byte[] subm )
    {
        return Base64.getEncoder().encodeToString( subm );
    }

    public static byte[] decBase64( String subm )
    {
        return Base64.getDecoder().decode( subm );
    }

    public static String stripTillValid( String subm )
    {
        while ( !subm.isEmpty() )
        {
            try
            {
                Base64.getDecoder().decode( subm );
                return subm;
            }
            catch ( Exception e )
            {
                subm = subm.subSequence( 0, subm.length() - 1 ).toString();
            }
        }
        return subm;
    }

    public static void printBase64Subm( String subm, Class<?> submType )
    {
        System.out.println( submType );
        System.out.println( "Base64 encoding is: " + subm );
        System.out.println( "Char length: " + subm.length() );
        System.out.println( "Num SMS: " + ((subm.length() / 160) + 1) );
        System.out.println( "************************" );
    }

    public static void checkSubmissionsAreEqual( SmsSubmission origSubm, SmsSubmission decSubm )
    {
        if ( !origSubm.equals( decSubm ) )
        {
            System.out.println( "Submissions are not equal!" );
            System.out.println( "Original submission: " );
            printSubm( origSubm );
            System.out.println( "Decoded submission: " );
            printSubm( decSubm );
            Assert.fail();
        }
    }
}
