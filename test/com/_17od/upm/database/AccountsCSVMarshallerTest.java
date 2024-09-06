/*
 * Universal Password Manager
 * Copyright (C) 2005-2013 Adrian Smith
 *
 * This file is part of Universal Password Manager.
 *   
 * Universal Password Manager is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * Universal Password Manager is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Universal Password Manager; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */
package com._17od.upm.database;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import junit.framework.TestCase;

import com.csvreader.CsvReader;

public class AccountsCSVMarshallerTest extends TestCase {

    public void testWithExistingFile() throws IOException {
        File exportFile = File.createTempFile("testdb_", ".upm");

        AccountsCSVMarshaller marshaller = new AccountsCSVMarshaller();
        try {
            marshaller.marshal(null, exportFile);
            fail("Expected to get a FileAlreadyExistsException");
        } catch (ExportException e) {
            // expected to get here
        }
    }

    public void testWithOneSimpleAccount() throws ExportException, IOException {
        AccountInformation account = new AccountInformation(
                "Test Account1", 
                "testuser1",
                "test password1", 
                "http://www.test.com1",
                "this is a test note1");

        ArrayList<AccountInformation> accounts = new ArrayList<AccountInformation>();
        accounts.add(account);

        // Marshall the database out to a CSV file
        File csvFile = File.createTempFile("testWithOneSimpleAccount_", ".csv");
        csvFile.delete();
        AccountsCSVMarshaller marshaller = new AccountsCSVMarshaller();
        marshaller.marshal(accounts, csvFile);

        // check that the file contents are what we expect
        CsvReader reader = new CsvReader(new FileReader(csvFile));
        reader.readRecord();
        assertEquals(account, reader.getValues());
    }

    public void testWithMultipleSimpleAccounts() throws IOException, ExportException {
        AccountInformation account1 = new AccountInformation(
                "Test Account1", 
                "testuser1",
                "test password1", 
                "http://www.test.com1",
                "this is a test note1");
        AccountInformation account2 = new AccountInformation(
                "Test Account2", 
                "testuser2",
                "test password2", 
                "http://www.test.com2",
                "this is a test note2");
        AccountInformation account3 = new AccountInformation(
                "Test Account3", 
                "testuser3",
                "test password3", 
                "http://www.test.com3",
                "this is a test note3");

        ArrayList<AccountInformation> accounts = new ArrayList<AccountInformation>();
        accounts.add(account1);
        accounts.add(account2);
        accounts.add(account3);

        // Marshall the database out to a CSV file
        File csvFile = File.createTempFile("testWithMultipleSimpleAccounts_", ".csv");
        csvFile.delete();
        AccountsCSVMarshaller marshaller = new AccountsCSVMarshaller();
        marshaller.marshal(accounts, csvFile);

        // check that the file contents are what we expect
        CsvReader reader = new CsvReader(new FileReader(csvFile));
        reader.readRecord();
        assertEquals(account1, reader.getValues());
        reader.readRecord();
        assertEquals(account2, reader.getValues());
        reader.readRecord();
        assertEquals(account3, reader.getValues());
    }

    public void testWhenNotesHaveCRLF() throws IOException, ExportException {
        AccountInformation account = new AccountInformation(
                "Test Account1", 
                "testuser1",
                "test password1", 
                "http://www.test.com1",
                "this is a \ntest note1");

        ArrayList<AccountInformation> accounts = new ArrayList<>();
        accounts.add(account);

        // Marshall the database out to a CSV file
        File csvFile = File.createTempFile("testWhenNotesHaveCRLF_", ".csv");
        csvFile.delete();
        AccountsCSVMarshaller marshaller = new AccountsCSVMarshaller();
        marshaller.marshal(accounts, csvFile);

        // check that the file contents are what we expect
        CsvReader reader = new CsvReader(new FileReader(csvFile));
        reader.readRecord();
        assertEquals(account, reader.getValues());
    }

    public void testWhenNotesHasComma() throws IOException, ExportException {
        AccountInformation account = new AccountInformation(
                "Test Account1", 
                "testuser1",
                "test password1", 
                "http://www.test.com1",
                "this is a ,test note1");

        ArrayList<AccountInformation> accounts = new ArrayList<AccountInformation>();
        accounts.add(account);

        // Marshall the database out to a CSV file
        File csvFile = File.createTempFile("testWhenNotesHasComma", ".csv");
        csvFile.delete();
        AccountsCSVMarshaller marshaller = new AccountsCSVMarshaller();
        marshaller.marshal(accounts, csvFile);

        // check that the file contents are what we expect
        CsvReader reader = new CsvReader(new FileReader(csvFile));
        reader.readRecord();
        assertEquals(account, reader.getValues());
    }

    public void testImport() throws IOException, ExportException, ImportException {
        AccountInformation account = new AccountInformation(
                "Test Account1", 
                "testuser1",
                "test password1", 
                "http://www.test.com1",
                "this is a test note1");

        ArrayList<AccountInformation> accounts = new ArrayList<AccountInformation>();
        accounts.add(account);

        // Marshall the database out to a CSV file
        File csvFile = File.createTempFile("testImport", ".csv");
        csvFile.delete();
        AccountsCSVMarshaller marshaller = new AccountsCSVMarshaller();
        marshaller.marshal(accounts, csvFile);

        // do the import and ensure the AccountInformation object we get back 
        // is correct
        ArrayList<AccountInformation> importedAccounts = marshaller.unmarshal(csvFile);
        assertNotNull(importedAccounts);
        assertEquals(1, importedAccounts.size());
        assertEquals(account, (AccountInformation) importedAccounts.get(0));
    }

    private void assertEquals(AccountInformation expected, AccountInformation actual) {
        assertEquals(expected.getAccountName(), actual.getAccountName());
        assertEquals(expected.getUserId(), actual.getUserId());
        assertEquals(expected.getPassword(), actual.getPassword());
        assertEquals(expected.getUrl(), actual.getUrl());
        assertEquals(expected.getNotes(), actual.getNotes());
    }

    private void assertEquals(AccountInformation expected, String[] actual) {
        assertEquals(expected.getAccountName(), actual[0]);
        assertEquals(expected.getUserId(), actual[1]);
        assertEquals(expected.getPassword(), actual[2]);
        assertEquals(expected.getUrl(), actual[3]);
        assertEquals(expected.getNotes(), actual[4]);
    }

}
