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

package com.exacttarget.fuelsdk.model.converter;

import com.exacttarget.fuelsdk.ETSdkException;
import com.exacttarget.fuelsdk.ETSoapObject;
import com.exacttarget.fuelsdk.internal.APIObject;

@Deprecated
public class ObjectConverter {
    @Deprecated
    @SuppressWarnings("unchecked")
    public static <T extends ETSoapObject> T convertToEtObject(APIObject internalObject,
                                                               Class<T> externalClass,
                                                               boolean isPatch)
        throws ETSdkException
    {
        T externalObject = null;
        try {
            externalObject = externalClass.newInstance();
        } catch (Exception ex) {
            throw new ETSdkException("could not instantiate "
                    + externalClass.getName(), ex);
        }
        return (T) externalObject.fromInternal(internalObject);
    }

    @Deprecated
    @SuppressWarnings("unchecked")
    public static <T extends APIObject> T convertFromEtObject(ETSoapObject externalObject,
                                                              Class<T> internalClass,
                                                              boolean isPatch)
        throws ETSdkException
    {
        return (T) externalObject.toInternal();
    }
}
