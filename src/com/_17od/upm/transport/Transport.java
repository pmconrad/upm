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
package com._17od.upm.transport;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Locale;


/**
 * This interface allows the PasswordDatabase to communicate with a remote location
 * without having to know what the underlying transport or remote filesystem is
 */
public abstract class Transport {

    public abstract void put(String targetLocation, File file, String username, String password) throws TransportException;

    public void put(String targetLocation, File file) throws TransportException {
        put(targetLocation, file, null, null);
    }

    public byte[] get(String url, String fileName) throws TransportException {
        return get(url, fileName, null, null);
    }

    public byte[] get(String url, String fileName, String username, String password) throws TransportException {
        url = addTrailingSlash(url);
        return get(url + fileName, username, password);
    }

    public abstract byte[] get(String url, String username, String password) throws TransportException;

    public abstract void delete(String targetLocation, String name, String username, String password) throws TransportException;

    public void delete(String targetLocation, String name) throws TransportException {
        delete(targetLocation, name, null, null);
    }

    public File getRemoteFile(String remoteLocation, String fileName) throws TransportException {
        return getRemoteFile(remoteLocation, fileName, null, null);
    }


    public File getRemoteFile(String remoteLocation) throws TransportException {
        return getRemoteFile(remoteLocation, null, null);
    }


    public File getRemoteFile(String remoteLocation, String fileName, String httpUsername, String httpPassword) throws TransportException {
        remoteLocation = addTrailingSlash(remoteLocation);
        return getRemoteFile(remoteLocation + fileName, httpUsername, httpPassword);
    }

    public File getRemoteFile(String remoteLocation, String httpUsername, String httpPassword) throws TransportException {
        try {
            byte[] remoteFile = get(remoteLocation, httpUsername, httpPassword);
            File downloadedFile = File.createTempFile("upm", null);
            try (FileOutputStream fos = new FileOutputStream(downloadedFile)) {
                fos.write(remoteFile);
            }
            return downloadedFile;
        } catch (IOException e) {
            throw new TransportException(e);
        }
    }

    public static Transport getTransportForURL(String url) {
        String lowerUrl = url.toLowerCase(Locale.getDefault());
        if (lowerUrl.startsWith("http:") || lowerUrl.startsWith("https:")) {
            return new HTTPTransport();
        }
        if (lowerUrl.startsWith("webdav:") || lowerUrl.startsWith("webdavs:")) {
            return new WebdavTransport();
        }
        return null;
    }
    
    public static boolean isASupportedProtocol(String protocol) {
        boolean supported = false;
        if (protocol.equals("http")) {
            supported = true;
        } else if (protocol.equals("https")) {
            supported = true;
        } else if (protocol.equals("file")) {
            supported = true;
        } else if (protocol.equals("webdav") || protocol.equals("webdavs")) {
            supported = true;
        }
        return supported;
    }

    protected static String addTrailingSlash(String url) {
        if (url.charAt(url.length() - 1) != '/') {
            url = url + '/';
        }
        return url;
    }

    protected static boolean isNotEmpty(String stringToCheck) {
        return stringToCheck != null && !stringToCheck.trim().isEmpty();
    }
}
