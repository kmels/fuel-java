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

import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.exacttarget.fuelsdk.deprecated.ETResponse;
import com.exacttarget.fuelsdk.deprecated.ETResult;
import com.exacttarget.fuelsdk.filter.ETFilter;
import com.exacttarget.fuelsdk.filter.ETFilterOperators;
import com.exacttarget.fuelsdk.filter.ETSimpleFilter;
import com.exacttarget.fuelsdk.soap.ETFolderServiceImpl;

import static org.junit.Assert.*;

@SuppressWarnings("deprecation")
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ETFolderTestDeprecatedMethods {
    private static ETClient client = null;
    private static ETFolderService service = null;

    @BeforeClass
    public static void setUpBeforeClass()
        throws ETSdkException
    {
        client = new ETClient();
        service = new ETFolderServiceImpl();
    }

    @Test
    public void _01_TestRetrieveAll()
        throws ETSdkException
    {
        ETResponse<ETFolder> response = service.get(client);
        for (ETFolder folder : response.getResults()) {
            // ensure all properties were retrieved
            assertNotNull(folder.getId());
            assertNotNull(folder.getCustomerKey());
            assertNotNull(folder.getName());
            assertNotNull(folder.getDescription());
            assertNotNull(folder.getCreatedDate());
            assertNotNull(folder.getModifiedDate());
            assertNotNull(folder.getContentType());
            // parentFolder can be null if there's no parent
            assertNotNull(folder.getIsActive());
            assertNotNull(folder.getIsEditable());
            assertNotNull(folder.getAllowChildren());
        }
    }

    @Test
    public void _02_TestRetrieveAllPropertiesSubset()
        throws ETSdkException
    {
        ETResponse<ETFolder> response = service.get(client,
                                                    "customerKey",
                                                    "name",
                                                    "description");
        for (ETFolder folder : response.getResults()) {
            // ensure only the specified properties were retrieved
            assertNull(folder.getId());
            assertNotNull(folder.getCustomerKey());
            assertNotNull(folder.getName());
            assertNotNull(folder.getDescription());
            assertNull(folder.getCreatedDate());
            assertNull(folder.getModifiedDate());
            assertNull(folder.getContentType());
            assertNull(folder.getParentFolderKey());
            assertNull(folder.getIsActive());
            assertNull(folder.getIsEditable());
            assertNull(folder.getAllowChildren());
        }
    }

    @Test
    public void _03_TestRetrieveFiltered()
        throws ETSdkException
    {
        // retrieve the Data Extension folder, which
        // has customer key of dataextension_default
        ETFilter filter = new ETSimpleFilter("CustomerKey",
                                             ETFilterOperators.EQUALS,
                                             "dataextension_default");
        ETResponse<ETFolder> response = service.get(client, filter);
        // ensure we only received 1
        assertEquals(1, response.getResults().size());
        ETFolder folder = response.getResults().get(0);
        // ensure it's the Data Extensions folder
        // and that all properties were retrieved
        assertNotNull(folder.getId());
        assertEquals("dataextension_default", folder.getCustomerKey());
        assertEquals("Data Extensions", folder.getName());
        assertEquals("", folder.getDescription());
        assertNotNull(folder.getCreatedDate());
        assertNotNull(folder.getModifiedDate());
        assertEquals("dataextension", folder.getContentType());
        assertNull(folder.getParentFolderKey());
        assertTrue(folder.getIsActive());
        assertFalse(folder.getIsEditable());
        assertTrue(folder.getAllowChildren());
    }

    @Test
    public void _04_TestRetrieveFilteredPropertiesSubset()
        throws ETSdkException
    {
        ETFilter filter = new ETSimpleFilter("CustomerKey",
                                             ETFilterOperators.EQUALS,
                                             "dataextension_default");
        ETResponse<ETFolder> response = service.get(client,
                                                    filter,
                                                    "customerKey",
                                                    "name",
                                                    "description");
        // ensure we only received 1
        assertEquals(1, response.getResults().size());
        ETFolder folder = response.getResults().get(0);
        // ensure it's the Data Extensions folder
        // and that only the specified properties
        // were retrieved
        assertNull(folder.getId());
        assertEquals("dataextension_default", folder.getCustomerKey());
        assertEquals("Data Extensions", folder.getName());
        assertEquals("", folder.getDescription());
        assertNull(folder.getCreatedDate());
        assertNull(folder.getModifiedDate());
        assertNull(folder.getContentType());
        assertNull(folder.getParentFolderKey());
        assertNull(folder.getIsActive());
        assertNull(folder.getIsEditable());
        assertNull(folder.getAllowChildren());
    }

    private static String id = null;

    @Test
    public void _05_TestCreateSingle()
        throws ETSdkException
    {
        ETFolder folder = new ETFolder();
        folder.setCustomerKey("test1");
        folder.setName("test1");
        folder.setDescription("test1");
        folder.setContentType("dataextension");
        folder.setParentFolderKey("dataextension_default");
        ETResponse<ETResult> response = service.post(client, folder);
        assertNotNull(response.getRequestId());
        assertEquals("OK", response.getStatusCode());
        assertEquals("OK", response.getStatusMessage());
        assertNull(response.getErrorCode());
        assertNull(response.getPage());
        assertNull(response.getPageSize());
        assertNull(response.getTotalCount());
        assertFalse(response.hasMoreResults());
        assertEquals(1, response.getResults().size());
        ETResult result = response.getResults().get(0);
        assertEquals("OK", result.getStatusCode());
        assertEquals("Folder created successfully.", result.getStatusMessage());
        assertNull(result.getErrorCode());
        assertNotNull(result.getId());
        // save the ID for use in the next test
        id = result.getId();
    }

    private static ETFolder createdFolder = null;

    @Test
    public void _06_TestRetrieveCreatedSingle()
        throws ETSdkException
    {
        ETFilter filter = new ETSimpleFilter("ID",
                                             ETFilterOperators.EQUALS,
                                             id.toString());
        ETResponse<ETFolder> response = service.get(client, filter);
        // ensure we only received 1
        assertEquals(1, response.getResults().size());
        createdFolder = response.getResults().get(0);
        // ensure it's the folder we just created
        assertEquals(id, createdFolder.getId());
        assertEquals("test1", createdFolder.getCustomerKey());
        assertEquals("test1", createdFolder.getName());
        assertEquals("test1", createdFolder.getDescription());
        assertNotNull(createdFolder.getCreatedDate());
        assertNotNull(createdFolder.getModifiedDate());
        assertEquals("dataextension", createdFolder.getContentType());
        assertEquals("dataextension_default", createdFolder.getParentFolderKey());
        assertTrue(createdFolder.getIsActive());
        assertFalse(createdFolder.getIsEditable());
        assertFalse(createdFolder.getAllowChildren());
    }

    @Test
    public void _07_TestUpdateSingle()
        throws ETSdkException
    {
        createdFolder.setName("TEST1");
        ETResponse<ETResult> response = service.patch(client, createdFolder);
        assertNotNull(response.getRequestId());
        assertEquals("OK", response.getStatusCode());
        assertEquals("OK", response.getStatusMessage());
        assertNull(response.getErrorCode());
        assertNull(response.getPage());
        assertNull(response.getPageSize());
        assertNull(response.getTotalCount());
        assertFalse(response.hasMoreResults());
        assertEquals(1, response.getResults().size());
        ETResult result = response.getResults().get(0);
        assertEquals("OK", result.getStatusCode());
        assertEquals("Folder updated successfully.", result.getStatusMessage());
        assertNull(result.getErrorCode());
        assertNull(result.getId());
    }

    @Test
    public void _08_TestRetrieveUpdatedSingle()
        throws ETSdkException
    {
        ETFilter filter = new ETSimpleFilter("ID",
                                             ETFilterOperators.EQUALS,
                                             id.toString());
        ETResponse<ETFolder> response = service.get(client, filter);
        // ensure we only received 1
        assertEquals(1, response.getResults().size());
        ETFolder folder = response.getResults().get(0);
        assertEquals(id, folder.getId());
        assertEquals("test1", folder.getCustomerKey());
        assertEquals("TEST1", folder.getName());
        assertEquals("test1", folder.getDescription());
        assertNotNull(folder.getCreatedDate());
        assertNotNull(folder.getModifiedDate());
        assertEquals("dataextension", folder.getContentType());
        assertEquals("dataextension_default", folder.getParentFolderKey());
        assertTrue(folder.getIsActive());
        assertFalse(folder.getIsEditable());
        assertFalse(folder.getAllowChildren());
    }

    @Test
    public void _09_TestDeleteSingle()
        throws ETSdkException
    {
        ETFolder folder = new ETFolder();
        folder.setCustomerKey("test1");
        ETResponse<ETResult> response = service.delete(client, folder);
        assertNotNull(response.getRequestId());
        assertEquals("OK", response.getStatusCode());
        assertEquals("OK", response.getStatusMessage());
        assertNull(response.getErrorCode());
        assertNull(response.getPage());
        assertNull(response.getPageSize());
        assertNull(response.getTotalCount());
        assertFalse(response.hasMoreResults());
        assertEquals(1, response.getResults().size());
        ETResult result = response.getResults().get(0);
        assertEquals("OK", result.getStatusCode());
        assertEquals("Folder deleted successfully.", result.getStatusMessage());
        assertNull(result.getErrorCode());
        assertNull(result.getId());
    }

    private static String id1 = null;
    private static String id2 = null;

    @Test
    public void _10_TestCreateMultiple()
        throws ETSdkException
    {
        ETFolder folder1 = new ETFolder();
        folder1.setCustomerKey("test1");
        folder1.setName("test1");
        folder1.setDescription("test1");
        folder1.setContentType("dataextension");
        folder1.setParentFolderKey("dataextension_default");
        ETFolder folder2 = new ETFolder();
        folder2.setCustomerKey("test2");
        folder2.setName("test2");
        folder2.setDescription("test2");
        folder2.setContentType("dataextension");
        folder2.setParentFolderKey("dataextension_default");
        List<ETFolder> folders = new ArrayList<ETFolder>();
        folders.add(folder1);
        folders.add(folder2);
        ETResponse<ETResult> response = service.post(client, folders);
        assertNotNull(response.getRequestId());
        assertEquals("OK", response.getStatusCode());
        assertEquals("OK", response.getStatusMessage());
        assertNull(response.getErrorCode());
        assertNull(response.getPage());
        assertNull(response.getPageSize());
        assertNull(response.getTotalCount());
        assertFalse(response.hasMoreResults());
        assertEquals(2, response.getResults().size());
        ETResult result1 = response.getResults().get(0);
        assertEquals("OK", result1.getStatusCode());
        assertEquals("Folder created successfully.", result1.getStatusMessage());
        assertNull(result1.getErrorCode());
        assertNotNull(result1.getId());
        // save the ID for use in the next test
        id1 = result1.getId();
        ETResult result2 = response.getResults().get(0);
        assertEquals("OK", result2.getStatusCode());
        assertEquals("Folder created successfully.", result2.getStatusMessage());
        assertNull(result2.getErrorCode());
        assertNotNull(result2.getId());
        // save the ID for use in the next test
        id2 = result2.getId();
    }

    @Test
    public void _11_TestRetrieveCreatedMultiple()
        throws ETSdkException
    {
        // XXX implement when we can pass the filter id = <id1> and id = <id2>
    }

    @Test
    public void _12_TestUpdateMultiple()
        throws ETSdkException
    {
        // XXX implement when we implement _11_TestRetrieveCreatedMultiple
    }

    @Test
    public void _13_TestRetrieveUpdatedMultiple()
        throws ETSdkException
    {
        // XXX implement when we implement _12_TestUpdateMultiple
    }

    @Test
    public void _14_TestDeleteMultiple()
        throws ETSdkException
    {
        ETFolder folder1 = new ETFolder();
        folder1.setCustomerKey("test1");
        ETFolder folder2 = new ETFolder();
        folder2.setCustomerKey("test2");
        List<ETFolder> folders = new ArrayList<ETFolder>();
        folders.add(folder1);
        folders.add(folder2);
        ETResponse<ETResult> response = service.delete(client, folders);
        assertNotNull(response.getRequestId());
        assertEquals("OK", response.getStatusCode());
        assertEquals("OK", response.getStatusMessage());
        assertNull(response.getErrorCode());
        assertNull(response.getPage());
        assertNull(response.getPageSize());
        assertNull(response.getTotalCount());
        assertFalse(response.hasMoreResults());
        assertEquals(2, response.getResults().size());
        ETResult result1 = response.getResults().get(0);
        assertEquals("OK", result1.getStatusCode());
        assertEquals("Folder deleted successfully.", result1.getStatusMessage());
        assertNull(result1.getErrorCode());
        assertNull(result1.getId());
        ETResult result2 = response.getResults().get(0);
        assertEquals("OK", result2.getStatusCode());
        assertEquals("Folder deleted successfully.", result2.getStatusMessage());
        assertNull(result2.getErrorCode());
        assertNull(result2.getId());
    }
}
