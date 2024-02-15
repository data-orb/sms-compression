package co.dataorb.os.smscompression.models;

import co.dataorb.os.smscompression.SmsConsts.MetadataType;
import co.dataorb.os.smscompression.SmsConsts.ValueType;

public class SmsAttributeValue
{
    protected Uid attribute;

    protected String value;

    protected SmsValue<?> smsValue;

    protected ValueType type;

    public SmsAttributeValue( String attribute, String value )
    {
        this.attribute = new Uid( attribute, MetadataType.TRACKED_ENTITY_ATTRIBUTE );
        this.value = value;
        this.smsValue = SmsValue.asSmsValue( value );
    }

    public SmsAttributeValue( Uid attribute, SmsValue<?> smsValue )
    {
        this.attribute = attribute;
        this.smsValue = smsValue;
        // TODO: We probably need better handling than just toString() here
        this.value = smsValue.getValue().toString();
    }

    public Uid getAttribute()
    {
        return this.attribute;
    }

    public String getValue()
    {
        return this.value;
    }

    public SmsValue<?> getSmsValue()
    {
        return this.smsValue;
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
        SmsAttributeValue dv = (SmsAttributeValue) o;

        return attribute.equals( dv.attribute ) && value.equals( dv.value );
    }

    @Override
    public int hashCode()
    {
        return 0;
    }
}
