package org.dataorb.dlms.smscompression;

import java.text.SimpleDateFormat;

public class SmsConsts
{

    public enum SubmissionType
    {
        AGGREGATE_DATASET, DELETE, ENROLLMENT, RELATIONSHIP, SIMPLE_EVENT, TRACKER_EVENT,

        ;

    }

    public enum MetadataType
    {
        USER,
        TRACKED_ENTITY_TYPE,
        TRACKED_ENTITY_ATTRIBUTE,
        PROGRAM,
        ORGANISATION_UNIT,
        DATA_ELEMENT,
        CATEGORY_OPTION_COMBO,
        DATASET,
        PROGRAM_STAGE,
        EVENT,
        ENROLLMENT,
        TRACKED_ENTITY_INSTANCE,
        RELATIONSHIP,
        RELATIONSHIP_TYPE,

        ;
    }

    public enum ValueType
    {
        INT, FLOAT, DATE, BOOL, STRING,

        ;
    }

    public enum SmsEventStatus
    {
        ACTIVE, COMPLETED, VISITED, SCHEDULE, OVERDUE, SKIPPED,

        ;

    }

    public enum SmsEnrollmentStatus
    {
        ACTIVE, COMPLETED, CANCELLED

        ;

    }

    public static final int VARLEN_BITLEN = 6;

    public static final int CHAR_BITLEN = 8;

    public static final int EPOCH_DATE_BITLEN = 32;

    public static final int SUBM_TYPE_BITLEN = 4;

    public static final int VERSION_BITLEN = 4;

    public static final int CRC_BITLEN = 8;

    public static final int SUBM_ID_BITLEN = 8;

    public static final int ID_LEN = 11;

    public static final int VALTYPE_BITLEN = 3;

    public static final int FIXED_INT_BITLEN = 4;

    public static final int EVENT_STATUS_BITLEN = 3;

    public static final int ENROL_STATUS_BITLEN = 2;

    public static final int METADATA_TYPE_BITLEN = 5; // Keeping this large (32)
                                                      // to support more types
                                                      // in future

    public static final int MAX_FIXED_NUM = (int) Math.pow( 2, Math.pow( 2, FIXED_INT_BITLEN ) ) - 1;

    // TODO: We should change this to be adjustable depending on largest int
    public static final int INT_BITLEN = 32;

    // TODO: Consider if we can come up with a better format for floats
    public static final int FLOAT_BITLEN = 32;

    // As we only store timestamps to the second, this is the format we use
    public static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";

    public static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat( DATE_FORMAT );

}
