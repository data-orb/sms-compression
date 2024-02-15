package co.dataorb.os.smscompression.models;

import co.dataorb.os.smscompression.SmsConsts.MetadataType;

public class SmsDataValue
{
    protected Uid categoryOptionCombo;

    protected Uid dataElement;

    protected String value;

    protected SmsValue<?> smsValue;

    public SmsDataValue( String categoryOptionCombo, String dataElement, String value )
    {
        this.categoryOptionCombo = new Uid( categoryOptionCombo, MetadataType.CATEGORY_OPTION_COMBO );
        this.dataElement = new Uid( dataElement, MetadataType.DATA_ELEMENT );
        this.value = value;
        this.smsValue = SmsValue.asSmsValue( value );
    }

    public SmsDataValue( Uid categoryOptionCombo, Uid dataElement, SmsValue<?> smsValue )
    {
        this.categoryOptionCombo = categoryOptionCombo;
        this.dataElement = dataElement;
        this.smsValue = smsValue;
        // TODO: We probably need better handling than just toString() here
        this.value = smsValue.getValue().toString();
    }

    public Uid getCategoryOptionCombo()
    {
        return categoryOptionCombo;
    }

    public Uid getDataElement()
    {
        return dataElement;
    }

    public String getValue()
    {
        return value;
    }

    public SmsValue<?> getSmsValue()
    {
        return smsValue;
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
        SmsDataValue dv = (SmsDataValue) o;

        return categoryOptionCombo.equals( dv.categoryOptionCombo ) && dataElement.equals( dv.dataElement )
            && value.equals( dv.value );
    }

    @Override
    public int hashCode()
    {
        return 0;
    }
}
