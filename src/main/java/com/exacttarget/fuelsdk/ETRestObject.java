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

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.log4j.Logger;

import com.exacttarget.fuelsdk.annotations.ExternalName;
import com.exacttarget.fuelsdk.annotations.RestObject;

public abstract class ETRestObject extends ETObject {
    private static Logger logger = Logger.getLogger(ETRestObject.class);

    @Expose
    @ExternalName("id")
    private String id = null;
    @Expose
    @ExternalName("key")
    private String key = null;
    @Expose
    @ExternalName("createdDate")
    private Date createdDate = null;
    @Expose
    @ExternalName("modifiedDate")
    private Date modifiedDate = null;

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public Date getCreatedDate() {
        return createdDate;
    }

    @Override
    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    @Override
    public Date getModifiedDate() {
        return modifiedDate;
    }

    @Override
    public void setModifiedDate(Date modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    protected static <T extends ETRestObject> ETResponse<T> create(ETClient client,
                                                                   List<T> objects)
        throws ETSdkException
    {
        ETResponse<T> response = new ETResponse<T>();

        if (objects == null || objects.size() == 0) {
            return response;
        }

        //
        // Get handle to the REST connection:
        //

        ETRestConnection connection = client.getRestConnection();

        //
        // Automatically refresh the token if necessary:
        //

        client.refreshToken();

        //
        // Read call details from the RestObject annotation:
        //

        RestObject annotations = objects.get(0).getClass().getAnnotation(RestObject.class);

        assert annotations != null;

        logger.trace("path: " + annotations.path());
        logger.trace("primaryKey: " + annotations.primaryKey());
        logger.trace("collection: " + annotations.collection());
        logger.trace("totalCount: " + annotations.totalCount());

        String path = annotations.path();

        Gson gson = connection.getGson();

        JsonParser jsonParser = new JsonParser();

        //
        // There's currently no way to do this in bulk so
        // we walk through the list of objects and create
        // them one at a time:
        //

        for (T object : objects) {
            String json = gson.toJson(object);

            logger.trace("POST " + path);

            if (logger.isTraceEnabled()) {
                JsonObject jsonObject = jsonParser.parse(json).getAsJsonObject();
                String jsonPrettyPrinted = gson.toJson(jsonObject);
                for (String line : jsonPrettyPrinted.split("\\n")) {
                    logger.trace(line);
                }
            }

            json = connection.post(path, json);

            JsonObject jsonObject = jsonParser.parse(json).getAsJsonObject();

            if (logger.isTraceEnabled()) {
                String jsonPrettyPrinted = gson.toJson(jsonObject);
                for (String line : jsonPrettyPrinted.split("\\n")) {
                    logger.trace(line);
                }
            }

            ETResult<T> result = new ETResult<T>();
            result.setRequestId(connection.getLastCallRequestId());
            result.setResponseCode(connection.getLastCallResponseCode());
            result.setResponseMessage(connection.getLastCallResponseMessage());
            @SuppressWarnings("unchecked")
            T createdObject = (T) gson.fromJson(json, object.getClass());
            createdObject.setClient(client);
            result.setObject(createdObject);

            response.addResult(result);

            object.setClient(client);
        }

        return response;
    }

    protected static <T extends ETRestObject> ETResponse<T> retrieve(ETClient client,
                                                                     ETFilter filter,
                                                                     Integer page,
                                                                     Integer pageSize,
                                                                     Class<T> type,
                                                                     String... properties)
        throws ETSdkException
    {
        ETResponse<T> response = new ETResponse<T>();

        //
        // Read call details from the RestObject annotation:
        //

        RestObject annotations = type.getAnnotation(RestObject.class);

        assert annotations != null;

        logger.trace("path: " + annotations.path());
        logger.trace("primaryKey: " + annotations.primaryKey());
        logger.trace("collection: " + annotations.collection());
        logger.trace("totalCount: " + annotations.totalCount());

        String path = annotations.path();

        if (filter != null) {
            logger.trace("filter: " + filter);

            if ((filter.getOperator() == ETFilter.Operator.EQUALS) &&
                (filter.getProperty().equals(annotations.primaryKey())))
            {
                //
                // Replace the item via the primary key in the filter:
                //

                path += "/" + filter.getValue();
            } else {
                //
                // Add object specific query parameters:
                //

                Method method = null;
                try {
                    method = type.getDeclaredMethod("toQueryParams", ETFilter.class);
                } catch (NoSuchMethodException ex) {
                    // there's no toQueryParams method on TYPE
                } catch (SecurityException ex) {
                    throw new ETSdkException(ex);
                }

                if (method != null) {
                    try {
                        path += "?" + method.invoke(null, filter);
                    } catch (Exception ex) {
                        throw new ETSdkException(ex);
                    }
                }
            }
        }

        ETRestConnection connection = client.getRestConnection();

        Gson gson = connection.getGson();

        JsonObject jsonObject = retrieve(client, path, page, pageSize, properties);

        response.setRequestId(connection.getLastCallRequestId());
        response.setResponseCode(connection.getLastCallResponseCode());
        response.setResponseMessage(connection.getLastCallResponseMessage());

        if (jsonObject.get("page") != null) {
            response.setPage(jsonObject.get("page").getAsInt());
            logger.trace("page = " + response.getPage());
            response.setPageSize(jsonObject.get("pageSize").getAsInt());
            logger.trace("pageSize = " + response.getPageSize());
            response.setTotalCount(jsonObject.get(annotations.totalCount()).getAsInt());
            logger.trace("totalCount = " + response.getTotalCount());

            if (response.getPage() * response.getPageSize() < response.getTotalCount()) {
                response.setMoreResults(true);
            }

            JsonArray collection = jsonObject.get(annotations.collection()).getAsJsonArray();

            for (JsonElement element : collection) {
                T object = gson.fromJson(element, type);
                object.setClient(client);
                ETResult<T> result = new ETResult<T>();
                result.setObject(object);
                response.addResult(result);
            }
        } else {
            T object = gson.fromJson(jsonObject, type);
            object.setClient(client);
            ETResult<T> result = new ETResult<T>();
            result.setObject(object);
            response.addResult(result);
        }

        return response;
    }

    protected static JsonObject retrieve(ETClient client,
                                         String path,
                                         Integer page,
                                         Integer pageSize,
                                         String... properties)
        throws ETSdkException
    {
        //
        // Get handle to the REST connection:
        //

        ETRestConnection connection = client.getRestConnection();

        //
        // Automatically refresh the token if necessary:
        //

        client.refreshToken();

        //
        // Append the query parameter string:
        //

        StringBuilder stringBuilder = new StringBuilder(path);

        boolean firstQueryParameter;
        if (path.indexOf('?') == -1) {
            firstQueryParameter = true;
        } else {
            firstQueryParameter = false;
        }

        if (page != null && pageSize != null) {
            if (firstQueryParameter) {
                firstQueryParameter = false;
                stringBuilder.append("?");
            } else {
                stringBuilder.append("&");
            }
            stringBuilder.append("$page=");
            stringBuilder.append(page);
            stringBuilder.append("&");
            stringBuilder.append("$pagesize=");
            stringBuilder.append(pageSize);
        }

        if (properties.length > 0) {
            if (properties.length != 1
                || properties[0].length() < 8
                || !properties[0].substring(0, 8).toLowerCase().equals("order by"))
            {
                throw new ETSdkException("REST objects do not support partial retrieves");
            }
            String tokens[] = properties[0].substring(9).split(" ");
            String property = tokens[0];
            Boolean ascending = null;
            if (tokens.length == 2) {
                if (tokens[1].toLowerCase().equals("asc")) {
                    ascending = true;
                } else if (tokens[1].toLowerCase().equals("desc")) {
                    ascending = false;
                }
            }
            if (firstQueryParameter) {
                firstQueryParameter = false;
                stringBuilder.append("?");
            } else {
                stringBuilder.append("&");
            }
            stringBuilder.append("$orderby=");
            stringBuilder.append(property);
            if (ascending != null) {
                stringBuilder.append("%20");
                if (ascending) {
                    stringBuilder.append("asc");
                } else {
                    stringBuilder.append("desc");
                }
            }
        }

        path = stringBuilder.toString();

        //
        // Perform the HTTP GET:
        //

        logger.trace("GET " + path);

        String json = connection.get(path);

        Gson gson = connection.getGson();
        JsonParser jsonParser = new JsonParser();
        JsonObject jsonObject = jsonParser.parse(json).getAsJsonObject();

        if (logger.isTraceEnabled()) {
            String jsonPrettyPrinted = gson.toJson(jsonObject);
            for (String line : jsonPrettyPrinted.split("\\n")) {
                logger.trace(line);
            }
        }

        return jsonObject;
    }

    protected static <T extends ETRestObject> ETResponse<T> update(ETClient client,
                                                                   List<T> objects)
        throws ETSdkException
    {
        ETResponse<T> response = new ETResponse<T>();

        if (objects == null || objects.size() == 0) {
            return response;
        }

        //
        // Get handle to the REST connection:
        //

        ETRestConnection connection = client.getRestConnection();

        //
        // Automatically refresh the token if necessary:
        //

        client.refreshToken();

        //
        // Read call details from the RestObject annotation:
        //

        RestObject annotations = objects.get(0).getClass().getAnnotation(RestObject.class);

        assert annotations != null;

        logger.trace("path: " + annotations.path());
        logger.trace("primaryKey: " + annotations.primaryKey());
        logger.trace("collection: " + annotations.collection());
        logger.trace("totalCount: " + annotations.totalCount());

        Gson gson = connection.getGson();
        JsonParser jsonParser = new JsonParser();

        //
        // There's currently no way to do this in bulk so
        // we walk through the list of objects and update
        // them one at a time:
        //

        for (T object : objects) {
            //
            // Construct the path to the object:
            //

            // XXX should throw an exception for complex expressions

            String p = annotations.path();

            String json = gson.toJson(object);

            logger.trace("PATCH " + p);

            if (logger.isTraceEnabled()) {
                JsonObject jsonObject = jsonParser.parse(json).getAsJsonObject();
                String jsonPrettyPrinted = gson.toJson(jsonObject);
                for (String line : jsonPrettyPrinted.split("\\n")) {
                    logger.trace(line);
                }
            }

            json = connection.patch(p, json);

            JsonObject jsonObject = jsonParser.parse(json).getAsJsonObject();

            if (logger.isTraceEnabled()) {
                String jsonPrettyPrinted = gson.toJson(jsonObject);
                for (String line : jsonPrettyPrinted.split("\\n")) {
                    logger.trace(line);
                }
            }

            ETResult<T> result = new ETResult<T>();
            result.setRequestId(connection.getLastCallRequestId());
            result.setResponseCode(connection.getLastCallResponseCode());
            result.setResponseMessage(connection.getLastCallResponseMessage());
            @SuppressWarnings("unchecked")
            T updatedObject = (T) gson.fromJson(json, object.getClass());
            updatedObject.setClient(client);
            result.setObject(updatedObject);

            response.addResult(result);

            object.setClient(client);
        }

        return response;
    }

    protected static <T extends ETRestObject> ETResponse<T> delete(ETClient client,
                                                                   List<T> objects)
        throws ETSdkException
    {
        ETResponse<T> response = new ETResponse<T>();

        if (objects == null || objects.size() == 0) {
            return response;
        }

        //
        // Get handle to the REST connection:
        //

        ETRestConnection connection = client.getRestConnection();

        //
        // Automatically refresh the token if necessary:
        //

        client.refreshToken();

        //
        // Read call details from the RestObject annotation:
        //

        RestObject annotations = objects.get(0).getClass().getAnnotation(RestObject.class);

        assert annotations != null;

        logger.trace("path: " + annotations.path());
        logger.trace("primaryKey: " + annotations.primaryKey());
        logger.trace("collection: " + annotations.collection());
        logger.trace("totalCount: " + annotations.totalCount());

        Gson gson = connection.getGson();
        JsonParser jsonParser = new JsonParser();

        //
        // There's currently no way to do this in bulk so
        // we walk through the list of objects and delete
        // them one at a time:
        //

        for (T object : objects) {
            //
            // Construct the path to the object:
            //

            // XXX should throw an exception for complex expressions

            String p = annotations.path();

            String json = gson.toJson(object);

            logger.trace("DELETE " + p);

            if (logger.isTraceEnabled()) {
                JsonObject jsonObject = jsonParser.parse(json).getAsJsonObject();
                String jsonPrettyPrinted = gson.toJson(jsonObject);
                for (String line : jsonPrettyPrinted.split("\\n")) {
                    logger.trace(line);
                }
            }

            // XXX doesn't return any data.. are all like this?
            connection.delete(p);

            ETResult<T> result = new ETResult<T>();
            result.setRequestId(connection.getLastCallRequestId());
            result.setResponseCode(connection.getLastCallResponseCode());
            result.setResponseMessage(connection.getLastCallResponseMessage());

            response.addResult(result);

            object.setClient(client);
        }

        return response;
    }

    protected static String getInternalProperty(Class<? extends ETRestObject> type,
                                                String name)
        throws ETSdkException
    {
        String internalProperty = null;

        Class<? extends ETRestObject> externalType = type; // for code readability

        Field externalField = getField(externalType, name);

        SerializedName serializedNameAnnotation =
                externalField.getAnnotation(SerializedName.class);

        if (serializedNameAnnotation != null) {
            internalProperty = serializedNameAnnotation.value();
        } else {
            // internal name is the same as external name
            internalProperty = externalField.getName();
        }

        return internalProperty;
    }
}
