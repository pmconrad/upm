/*
 * (C) 2013,2024 Peter Conrad <conrad@quisquis.de>
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

import java.io.*;

import java.nio.file.Files;

import com.github.sardine.Sardine;
import com.github.sardine.SardineFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author Peter Conrad
 */
public class WebdavTransport extends Transport {
    private static final Log log = LogFactory.getLog(WebdavTransport.class);

    private Sardine open(String user, String pass) {
        // FIXME: proxy settings?
        return user == null || pass == null ? SardineFactory.begin() : SardineFactory.begin(user, pass);
    }

    @Override
    public void put(String targetLocation, File file,
                    String username, String password) throws TransportException {
        log.info("Webdav.put('" + targetLocation + "', '" + file.getPath()
                 + "', " + (username != null ? "'" + username + "'" : "null")
                 + ", ...)");
        try {
            Sardine sardine = open(username, password);
            try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
                Files.copy(file.toPath(), bos);
                sardine.put(addTrailingSlash(adjustUrl(targetLocation)) + file.getName(), bos.toByteArray());
            }
        } catch (RuntimeException | IOException e) {
            throw new TransportException("There's been some kind of problem uploading a file to the WebDAV server.", e);
        }
    }

    @Override
    public byte[] get(String url, String username, String password)
            throws TransportException {
        log.info("Webdav.get('" + url + "', "
                 + (username != null ? "'" + username + "'" : "null")
                 + ", ...)");
        try {
            Sardine sardine = open(username, password);
            try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    InputStream is = sardine.get(adjustUrl(url))) {
                byte[] buf = new byte[1024];
                int c;
                while ((c = is.read(buf)) > 0) {
                    bos.write(buf, 0, c);
                }
                return bos.toByteArray();
            }
        } catch (RuntimeException | IOException e) {
            throw new TransportException(e);
        }
    }

    @Override
    public void delete(String targetLocation, String name,
                       String username, String password) throws TransportException {
        /* delete is only ever called right before put, so it isn't useful. Skip it.
        log.info("Webdav.delete('" + targetLocation + "', '" + name
                 + "', " + (username != null ? "'" + username + "'" : "null")
                 + ", ...)");
        try {
            open(username, password).delete(addTrailingSlash(adjustUrl(targetLocation)) + name);
        } catch (RuntimeException | IOException e) {
            throw new TransportException(e);
        }
        */
    }

    public static String adjustUrl(String url) {
        if (url.startsWith("webdav")) { return "http" + url.substring(6); }
        return url;
    }
}
