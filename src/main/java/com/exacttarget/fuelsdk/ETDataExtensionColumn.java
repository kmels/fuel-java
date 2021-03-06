//
// This file is part of the Fuel Java SDK.
//
// Copyright (C) 2013, 2014 ExactTarget, Inc.
// All rights reserved.
//
// Permission is hereby granted, free of charge, to any person
// obtaining a copy of this software and associated documentation
// files (the "Software"), to deal in the Software without restriction,
// including without limitation the rights to use, copy, modify,
// merge, publish, distribute, sublicense, and/or sell copies
// of the Software, and to permit persons to whom the Software
// is furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be
// included in all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY
// KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
// WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
// PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS
// OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
// OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT
// OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH
// THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
//

package com.exacttarget.fuelsdk;

import com.exacttarget.fuelsdk.annotations.ExternalName;
import com.exacttarget.fuelsdk.annotations.InternalName;
import com.exacttarget.fuelsdk.annotations.SoapObject;
import com.exacttarget.fuelsdk.internal.DataExtensionField;

@SoapObject(internalType = DataExtensionField.class, unretrievable = {
    "ID", "Description", "DataExtension", "Precision"
})
public class ETDataExtensionColumn extends ETSoapObject {
    @ExternalName("name")
    private String name = null;
    @ExternalName("description")
    private String description = null;
    @ExternalName("dataExtension")
    private ETDataExtension dataExtension = null;
    @ExternalName("type")
    @InternalName("fieldType")
    private Type type = null;
    @ExternalName("defaultValue")
    private String defaultValue = null;
    @ExternalName("isPrimaryKey")
    private Boolean isPrimaryKey = null;
    @ExternalName("isRequired")
    private Boolean isRequired = null;
    @ExternalName("length")
    @InternalName("maxLength")
    private Integer length = null;
    @ExternalName("precision")
    private Integer precision = null;
    @ExternalName("scale")
    private Integer scale = null;

    public ETDataExtensionColumn() {}

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name.toLowerCase();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ETDataExtension getDataExtension() {
        return dataExtension;
    }

    public void setDataExtension(ETDataExtension dataExtension) {
        this.dataExtension = dataExtension;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public Boolean getIsPrimaryKey() {
        return isPrimaryKey;
    }

    public void setIsPrimaryKey(Boolean isPrimaryKey) {
        this.isPrimaryKey = isPrimaryKey;
    }

    public Boolean getIsRequired() {
        return isRequired;
    }

    public void setIsRequired(Boolean isRequired) {
        this.isRequired = isRequired;
    }

    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

    public Integer getPrecision() {
        return precision;
    }

    public void setPrecision(Integer precision) {
        this.precision = precision;
    }

    public Integer getScale() {
        return scale;
    }

    public void setScale(Integer scale) {
        this.scale = scale;
    }

    /**
     * @deprecated
     * Use <code>getType()</code>.
     */
    @Deprecated
    public Type getColumnType() {
        return getType();
    }

    /**
     * @deprecated
     * Use <code>setType()</code>.
     */
    @Deprecated
    public void setColumnType(Type columnType) {
        setType(columnType);
    }

    /**
     * @deprecated
     * Use <code>getMaxLength()</code>.
     */
    @Deprecated
    public Integer getMaxLength() {
        return getLength();
    }

    /**
     * @deprecated
     * Use <code>setMaxLength()</code>.
     */
    @Deprecated
    public void setMaxLength(Integer maxLength) {
        setLength(maxLength);
    }

    public enum Type {
        BOOLEAN("Boolean"),
        DATE("Date"),
        DECIMAL("Decimal"),
        EMAIL_ADDRESS("EmailAddress"),
        LOCALE("Locale"),
        NUMBER("Number"),
        PHONE("Phone"),
        TEXT("Text");
        private final String value;

        Type(String value) {
            this.value = value;
        }

        public String value() {
            return value;
        }

        public static Type fromValue(String value) {
            for (Type v : Type.values()) {
                if (v.value.equals(value)) {
                    return v;
                }
            }
            throw new IllegalArgumentException(value);
        }
    }
}
