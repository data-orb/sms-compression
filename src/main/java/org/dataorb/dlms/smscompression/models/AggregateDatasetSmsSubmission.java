package org.dataorb.dlms.smscompression.models;

import java.util.List;
import java.util.Objects;

import org.dataorb.dlms.smscompression.SmsCompressionException;
import org.dataorb.dlms.smscompression.SmsConsts;
import org.dataorb.dlms.smscompression.SmsConsts.MetadataType;
import org.dataorb.dlms.smscompression.SmsConsts.SubmissionType;
import org.dataorb.dlms.smscompression.SmsSubmissionReader;
import org.dataorb.dlms.smscompression.SmsSubmissionWriter;

public class AggregateDatasetSmsSubmission
    extends
    SmsSubmission
{
    protected Uid orgUnit;

    protected Uid dataSet;

    protected boolean complete;

    protected Uid attributeOptionCombo;

    protected String period;

    protected List<SmsDataValue> values;

    /* Getters and Setters */

    public Uid getOrgUnit()
    {
        return orgUnit;
    }

    public void setOrgUnit( String orgUnit )
    {
        this.orgUnit = new Uid( orgUnit, MetadataType.ORGANISATION_UNIT );
    }

    public Uid getDataSet()
    {
        return dataSet;
    }

    public void setDataSet( String dataSet )
    {
        this.dataSet = new Uid( dataSet, MetadataType.DATASET );
    }

    public boolean isComplete()
    {
        return complete;
    }

    public void setComplete( boolean complete )
    {
        this.complete = complete;
    }

    public Uid getAttributeOptionCombo()
    {
        return attributeOptionCombo;
    }

    public void setAttributeOptionCombo( String attributeOptionCombo )
    {
        this.attributeOptionCombo = new Uid( attributeOptionCombo, MetadataType.CATEGORY_OPTION_COMBO );
    }

    public String getPeriod()
    {
        return period;
    }

    public void setPeriod( String period )
    {
        this.period = period;
    }

    public List<SmsDataValue> getValues()
    {
        return values;
    }

    public void setValues( List<SmsDataValue> values )
    {
        this.values = values;
    }

    @Override
    public boolean equals( Object o )
    {
        if ( !super.equals( o ) )
        {
            return false;
        }
        AggregateDatasetSmsSubmission subm = (AggregateDatasetSmsSubmission) o;

        return Objects.equals( orgUnit, subm.orgUnit ) && Objects.equals( dataSet, subm.dataSet )
            && Objects.equals( complete, subm.complete )
            && Objects.equals( attributeOptionCombo, subm.attributeOptionCombo )
            && Objects.equals( period, subm.period ) && Objects.equals( values, subm.values );
    }

    /* Implementation of abstract methods */

    @Override
    public void writeSubm( SmsSubmissionWriter writer, int version )
        throws SmsCompressionException
    {
        switch ( version )
        {
        case 1:
            writeSubmV1( writer );
            break;
        case 2:
            writeSubmV2( writer );
            break;
        default:
            throw new SmsCompressionException( versionError( version ) );
        }
    }

    private void writeSubmV1( SmsSubmissionWriter writer )
        throws SmsCompressionException
    {
        writer.writeId( orgUnit );
        writer.writeId( dataSet );
        writer.writeBool( complete );
        writer.writeId( attributeOptionCombo );
        writer.writePeriod( period );
        writer.writeDataValues( values );
    }

    private void writeSubmV2( SmsSubmissionWriter writer )
        throws SmsCompressionException
    {
        writer.writeId( orgUnit );
        writer.writeId( dataSet );
        writer.writeBool( complete );
        writer.writeId( attributeOptionCombo );
        writer.writePeriod( period );
        boolean hasValues = (values != null && !values.isEmpty());
        writer.writeBool( hasValues );
        if ( hasValues )
        {
            writer.writeDataValues( values );
        }
    }

    @Override
    public void readSubm( SmsSubmissionReader reader, int version )
        throws SmsCompressionException
    {
        switch ( version )
        {
        case 1:
            readSubmV1( reader );
            break;
        case 2:
            readSubmV2( reader );
            break;
        default:
            throw new SmsCompressionException( versionError( version ) );
        }
    }

    private void readSubmV1( SmsSubmissionReader reader )
        throws SmsCompressionException
    {
        this.orgUnit = reader.readId( MetadataType.ORGANISATION_UNIT );
        this.dataSet = reader.readId( MetadataType.DATASET );
        this.complete = reader.readBool();
        this.attributeOptionCombo = reader.readId( MetadataType.CATEGORY_OPTION_COMBO );
        this.period = reader.readPeriod();
        this.values = reader.readDataValues();
    }

    private void readSubmV2( SmsSubmissionReader reader )
        throws SmsCompressionException
    {
        this.orgUnit = reader.readId( MetadataType.ORGANISATION_UNIT );
        this.dataSet = reader.readId( MetadataType.DATASET );
        this.complete = reader.readBool();
        this.attributeOptionCombo = reader.readId( MetadataType.CATEGORY_OPTION_COMBO );
        this.period = reader.readPeriod();
        boolean hasValues = reader.readBool();
        this.values = hasValues ? reader.readDataValues() : null;
    }

    @Override
    public int getCurrentVersion()
    {
        return 2;
    }

    @Override
    public SubmissionType getType()
    {
        return SmsConsts.SubmissionType.AGGREGATE_DATASET;
    }
}
